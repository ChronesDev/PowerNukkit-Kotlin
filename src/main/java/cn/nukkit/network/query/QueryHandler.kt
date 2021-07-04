package cn.nukkit.network.query

import cn.nukkit.Server

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
class QueryHandler {
    private val server: Server
    private var lastToken: ByteArray
    private var token: ByteArray
    private var longData: ByteArray
    private var shortData: ByteArray
    private var timeout: Long = 0
    fun regenerateInfo() {
        val ev: QueryRegenerateEvent = server.getQueryInformation()
        longData = ev.getLongQuery(longData)
        shortData = ev.getShortQuery(shortData)
        timeout = System.currentTimeMillis() + ev.getTimeout()
    }

    fun regenerateToken() {
        lastToken = token
        val token = ByteArray(16)
        for (i in 0..15) {
            token[i] = Random().nextInt(255) as Byte
        }
        this.token = token
    }

    fun handle(address: InetSocketAddress, packet: ByteBuf) {
        val packetId: Short = packet.readUnsignedByte()
        val sessionId: Int = packet.readInt()
        when (packetId) {
            HANDSHAKE -> {
                val reply: ByteBuf = ByteBufAllocator.DEFAULT.ioBuffer(10) // 1 + 4 + 4 + 1
                reply.writeByte(HANDSHAKE)
                reply.writeInt(sessionId)
                reply.writeBytes(getTokenString(token, address.getAddress()))
                reply.writeByte(0)
                server.getNetwork().sendPacket(address, reply)
            }
            STATISTICS -> {
                val token = ByteArray(4)
                packet.readBytes(token)
                if (!Arrays.equals(token, getTokenString(this.token, address.getAddress())) &&
                        !Arrays.equals(token, getTokenString(lastToken, address.getAddress()))) {
                    break
                }
                if (timeout < System.currentTimeMillis()) {
                    regenerateInfo()
                }
                reply = ByteBufAllocator.DEFAULT.ioBuffer(64)
                reply.writeByte(STATISTICS)
                reply.writeInt(sessionId)
                if (packet.readableBytes() === 8) {
                    reply.writeBytes(longData)
                } else {
                    reply.writeBytes(shortData)
                }
                server.getNetwork().sendPacket(address, reply)
            }
        }
    }

    companion object {
        const val HANDSHAKE: Byte = 0x09
        const val STATISTICS: Byte = 0x00
        fun getTokenString(token: String, address: InetAddress?): ByteArray {
            return getTokenString(token.getBytes(StandardCharsets.UTF_8), address)
        }

        fun getTokenString(token: ByteArray?, address: InetAddress): ByteArray {
            return try {
                val digest: MessageDigest = MessageDigest.getInstance("MD5")
                digest.update(address.toString().getBytes(StandardCharsets.UTF_8))
                digest.update(token)
                Arrays.copyOf(digest.digest(), 4)
            } catch (e: NoSuchAlgorithmException) {
                ByteBuffer.allocate(4).putInt(ThreadLocalRandom.current().nextInt()).array()
            }
        }
    }

    init {
        server = Server.getInstance()
        log.info(server.getLanguage().translateString("nukkit.server.query.start"))
        val ip: String = server.getIp()
        val addr = if (!ip.isEmpty()) ip else "0.0.0.0"
        val port: Int = server.getPort()
        log.info(server.getLanguage().translateString("nukkit.server.query.info", String.valueOf(port)))
        regenerateToken()
        lastToken = token
        regenerateInfo()
        log.info(server.getLanguage().translateString("nukkit.server.query.running", arrayOf(addr, String.valueOf(port))))
    }
}