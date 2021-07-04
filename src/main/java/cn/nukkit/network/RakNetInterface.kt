package cn.nukkit.network

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
class RakNetInterface(server: Server) : RakNetServerListener, AdvancedSourceInterface {
    private val server: Server
    private var network: Network? = null
    private val raknet: RakNetServer
    private val sessions: Map<InetSocketAddress, NukkitRakNetSession> = HashMap()
    private val sessionCreationQueue: Queue<NukkitRakNetSession> = PlatformDependent.newMpscQueue()
    private val tickFutures: Set<ScheduledFuture<*>> = HashSet()
    private val sessionsToTick: FastThreadLocal<Set<NukkitRakNetSession>> = object : FastThreadLocal<Set<NukkitRakNetSession?>?>() {
        @Override
        protected fun initialValue(): Set<NukkitRakNetSession> {
            return Collections.newSetFromMap(IdentityHashMap())
        }
    }
    private var advertisement: ByteArray

    @Override
    override fun setNetwork(network: Network?) {
        this.network = network
    }

    @Override
    override fun process(): Boolean {
        var session: NukkitRakNetSession
        while (sessionCreationQueue.poll().also { session = it } != null) {
            val address: InetSocketAddress = session.raknet.getAddress()
            val ev = PlayerCreationEvent(this, Player::class.java, Player::class.java, null, address)
            server.getPluginManager().callEvent(ev)
            val clazz: Class<out Player?> = ev.getPlayerClass()
            try {
                val constructor: Constructor<out Player?> = clazz.getConstructor(SourceInterface::class.java, Long::class.java, InetSocketAddress::class.java)
                val player: Player = constructor.newInstance(this, ev.getClientId(), ev.getSocketAddress())
                server.addPlayer(address, player)
                session.player = player
                sessions.put(address, session)
            } catch (e: NoSuchMethodException) {
                log.error("Error while creating the player class {}", clazz, e)
            } catch (e: InvocationTargetException) {
                log.error("Error while creating the player class {}", clazz, e)
            } catch (e: InstantiationException) {
                log.error("Error while creating the player class {}", clazz, e)
            } catch (e: IllegalAccessException) {
                log.error("Error while creating the player class {}", clazz, e)
            }
        }
        val iterator: Iterator<NukkitRakNetSession> = sessions.values().iterator()
        while (iterator.hasNext()) {
            val nukkitSession = iterator.next()
            val player: Player? = nukkitSession.player
            if (nukkitSession.disconnectReason != null) {
                player.close(player.getLeaveMessage(), nukkitSession.disconnectReason, false)
                iterator.remove()
                continue
            }
            var packet: DataPacket
            while (nukkitSession.inbound.poll().also { packet = it } != null) {
                try {
                    nukkitSession.player.handleDataPacket(packet)
                } catch (e: Exception) {
                    log.error(FormattedMessage("An error occurred whilst handling {} for {}", arrayOf<Object>(packet.getClass().getSimpleName(), nukkitSession.player.getName()), e))
                }
            }
        }
        return true
    }

    @Override
    override fun getNetworkLatency(player: Player): Int {
        val session: RakNetServerSession = raknet.getSession(player.getSocketAddress())
        return if (session == null) -1 else session.getPing()
    }

    @Override
    override fun close(player: Player) {
        this.close(player, "unknown reason")
    }

    @Override
    override fun close(player: Player, reason: String?) {
        val session: RakNetServerSession = raknet.getSession(player.getSocketAddress())
        if (session != null) {
            session.close()
        }
    }

    @Override
    override fun shutdown() {
        tickFutures.forEach { future -> future.cancel(false) }
        raknet.close()
    }

    @Override
    override fun emergencyShutdown() {
        tickFutures.forEach { future -> future.cancel(true) }
        raknet.close()
    }

    @Override
    override fun blockAddress(address: InetAddress?) {
        raknet.block(address)
    }

    @Override
    override fun blockAddress(address: InetAddress?, timeout: Int) {
        raknet.block(address, timeout, TimeUnit.SECONDS)
    }

    @Override
    override fun unblockAddress(address: InetAddress?) {
        raknet.unblock(address)
    }

    @Override
    override fun sendRawPacket(socketAddress: InetSocketAddress?, payload: ByteBuf?) {
        raknet.send(socketAddress, payload)
    }

    @Override
    fun setName(name: String) {
        val info: QueryRegenerateEvent = server.getQueryInformation()
        val names: Array<String> = name.split("!@#") //Split double names within the program
        val motd: String = Utils.rtrim(names[0].replace(";", "\\;"), '\\')
        val subMotd = if (names.size > 1) Utils.rtrim(names[1].replace(";", "\\;"), '\\') else ""
        val joiner: StringJoiner = StringJoiner(";")
                .add("MCPE")
                .add(motd)
                .add(Integer.toString(ProtocolInfo.CURRENT_PROTOCOL))
                .add(ProtocolInfo.MINECRAFT_VERSION_NETWORK)
                .add(Integer.toString(info.getPlayerCount()))
                .add(Integer.toString(info.getMaxPlayerCount()))
                .add(toString(raknet.getGuid()))
                .add(subMotd)
                .add(Server.getGamemodeString(server.getDefaultGamemode(), true))
                .add("1")
        advertisement = joiner.toString().getBytes(StandardCharsets.UTF_8)
    }

    @Override
    override fun putPacket(player: Player, packet: DataPacket): Integer? {
        return this.putPacket(player, packet, false)
    }

    @Override
    override fun putPacket(player: Player, packet: DataPacket, needACK: Boolean): Integer? {
        return this.putPacket(player, packet, needACK, false)
    }

    @Override
    override fun putPacket(player: Player, packet: DataPacket, needACK: Boolean, immediate: Boolean): Integer? {
        val session = sessions[player.getSocketAddress()]
        if (session != null) {
            packet.tryEncode()
            if (!immediate) {
                session.outbound.offer(packet.clone())
            } else {
                session.sendPacketImmediately(packet.clone())
            }
        }
        return null
    }

    @Override
    fun onConnectionRequest(inetSocketAddress: InetSocketAddress?): Boolean {
        return true
    }

    @Override
    fun onQuery(inetSocketAddress: InetSocketAddress?): ByteArray {
        return advertisement
    }

    @Override
    fun onSessionCreation(session: RakNetServerSession) {
        val nukkitSession: NukkitRakNetSession = NukkitRakNetSession(session)
        session.setListener(nukkitSession)
        sessionCreationQueue.offer(nukkitSession)

        // We need to make sure this gets put into the correct thread local hashmap
        // for ticking or race conditions will occur.
        session.getEventLoop().execute { sessionsToTick.get().add(nukkitSession) }
    }

    @Override
    fun onUnhandledDatagram(ctx: ChannelHandlerContext?, datagramPacket: DatagramPacket) {
        server.handlePacket(datagramPacket.sender(), datagramPacket.content())
    }

    @RequiredArgsConstructor
    private inner class NukkitRakNetSession : RakNetSessionListener {
        val raknet: RakNetServerSession? = null
        val inbound: Queue<DataPacket> = PlatformDependent.newSpscQueue()
        val outbound: Queue<DataPacket> = PlatformDependent.newMpscQueue()
        var disconnectReason: String? = null
        val player: Player? = null
        @Override
        fun onSessionChangeState(rakNetState: RakNetState?) {
        }

        @Override
        fun onDisconnect(disconnectReason: DisconnectReason) {
            if (disconnectReason === DisconnectReason.TIMED_OUT) {
                disconnect("Timed out")
            } else {
                disconnect("Disconnected from Server")
            }
        }

        @Override
        fun onEncapsulated(packet: EncapsulatedPacket) {
            val buffer: ByteBuf = packet.getBuffer()
            val packetId: Short = buffer.readUnsignedByte()
            if (packetId.toInt() == 0xfe) {
                val packetBuffer = ByteArray(buffer.readableBytes())
                buffer.readBytes(packetBuffer)
                try {
                    network.processBatch(packetBuffer, inbound)
                } catch (e: ProtocolException) {
                    disconnect("Sent malformed packet")
                    log.error("Unable to process batch packet", e)
                }
            }
        }

        @Override
        fun onDirect(byteBuf: ByteBuf?) {
            // We don't allow any direct packets so ignore.
        }

        private fun disconnect(message: String) {
            disconnectReason = message
            sessionsToTick.get().remove(this)
        }

        fun sendOutbound() {
            val toBatch: List<DataPacket> = ObjectArrayList()
            var packet: DataPacket
            while (outbound.poll().also { packet = it } != null) {
                if (packet.pid() === ProtocolInfo.BATCH_PACKET) {
                    if (!toBatch.isEmpty()) {
                        sendPackets(toBatch)
                        toBatch.clear()
                    }
                    sendPacket((packet as BatchPacket).payload)
                } else {
                    toBatch.add(packet)
                }
            }
            if (!toBatch.isEmpty()) {
                sendPackets(toBatch)
            }
        }

        private fun sendPackets(packets: Collection<DataPacket>) {
            val batched = BinaryStream()
            for (packet in packets) {
                Preconditions.checkArgument(packet !is BatchPacket, "Cannot batch BatchPacket")
                Preconditions.checkState(packet.isEncoded, "Packet should have already been encoded")
                val buf: ByteArray = packet.getBuffer()
                batched.putUnsignedVarInt(buf.size)
                batched.put(buf)
            }
            try {
                sendPacket(Network.deflateRaw(batched.getBuffer(), network!!.getServer().networkCompressionLevel))
            } catch (e: IOException) {
                log.error("Unable to compress batched packets", e)
            }
        }

        private fun sendPacket(payload: ByteArray) {
            val byteBuf: ByteBuf = ByteBufAllocator.DEFAULT.ioBuffer(1 + payload.size)
            byteBuf.writeByte(0xfe)
            byteBuf.writeBytes(payload)
            this.raknet.send(byteBuf)
        }

        fun sendPacketImmediately(packet: DataPacket) {
            val batched = BinaryStream()
            Preconditions.checkArgument(packet !is BatchPacket, "Cannot batch BatchPacket")
            Preconditions.checkState(packet.isEncoded, "Packet should have already been encoded")
            val buf: ByteArray = packet.getBuffer()
            batched.putUnsignedVarInt(buf.size)
            batched.put(buf)
            try {
                val payload: ByteArray = Network.deflateRaw(batched.getBuffer(), network!!.getServer().networkCompressionLevel)
                val byteBuf: ByteBuf = ByteBufAllocator.DEFAULT.ioBuffer(1 + payload.size)
                byteBuf.writeByte(0xfe)
                byteBuf.writeBytes(payload)
                this.raknet.send(byteBuf, RakNetPriority.IMMEDIATE)
            } catch (e: Exception) {
                log.error("Error occured while sending a packet immediately", e)
            }
        }
    }

    init {
        this.server = server
        val bindAddress = InetSocketAddress(if (Strings.isNullOrEmpty(this.server.getIp())) "0.0.0.0" else this.server.getIp(), this.server.getPort())
        raknet = RakNetServer(bindAddress, Runtime.getRuntime().availableProcessors())
        raknet.bind().join()
        raknet.setListener(this)
        for (executor in raknet.getBootstrap().config().group()) {
            tickFutures.add(executor.scheduleAtFixedRate({
                for (session in sessionsToTick.get()) {
                    session.sendOutbound()
                }
            }, 0, 50, TimeUnit.MILLISECONDS))
        }
    }
}