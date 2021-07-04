package cn.nukkit.network.rcon

import lombok.extern.log4j.Log4j2

/**
 * Thread that performs all RCON network work. A server.
 *
 * @author Tee7even
 */
@Log4j2
class RCONServer(address: String?, port: Int, password: String) : Thread() {
    @Volatile
    private var running: Boolean
    private val serverChannel: ServerSocketChannel
    private val selector: Selector
    private val password: String
    private val rconSessions: Set<SocketChannel> = HashSet()
    private val receiveQueue: List<RCONCommand> = ArrayList()
    private val sendQueues: Map<SocketChannel, List<RCONPacket>> = HashMap()
    fun receive(): RCONCommand? {
        synchronized(receiveQueue) {
            if (!receiveQueue.isEmpty()) {
                val command: RCONCommand = receiveQueue[0]
                receiveQueue.remove(0)
                return command
            }
            return null
        }
    }

    fun respond(channel: SocketChannel, id: Int, response: String) {
        send(channel, RCONPacket(id, SERVERDATA_RESPONSE_VALUE, response.getBytes()))
    }

    fun close() {
        running = false
        selector.wakeup()
    }

    fun run() {
        while (running) {
            try {
                synchronized(sendQueues) {
                    for (channel in sendQueues.keySet()) {
                        channel.keyFor(selector).interestOps(SelectionKey.OP_WRITE)
                    }
                }
                selector.select()
                val selectedKeys: Iterator<SelectionKey> = selector.selectedKeys().iterator()
                while (selectedKeys.hasNext()) {
                    val key: SelectionKey = selectedKeys.next()
                    selectedKeys.remove()
                    if (key.isAcceptable()) {
                        val serverSocketChannel: ServerSocketChannel = key.channel() as ServerSocketChannel
                        val socketChannel: SocketChannel = serverSocketChannel.accept()
                        socketChannel.socket()
                        socketChannel.configureBlocking(false)
                        socketChannel.register(selector, SelectionKey.OP_READ)
                    } else if (key.isReadable()) {
                        read(key)
                    } else if (key.isWritable()) {
                        write(key)
                    }
                }
            } catch (exception: BufferUnderflowException) {
                log.trace("Got a possible corrupt packet", exception)
                //Corrupted packet, ignore
            } catch (exception: Exception) {
                log.error("An exception happened processing the RCON server", exception)
            }
        }
        try {
            serverChannel.keyFor(selector).cancel()
            serverChannel.close()
            selector.close()
        } catch (exception: IOException) {
            log.error("An exception happened while closing the RCON server", exception)
        }
        synchronized(this) { this.notify() }
    }

    @Throws(IOException::class)
    private fun read(key: SelectionKey) {
        val channel: SocketChannel = key.channel() as SocketChannel
        val buffer: ByteBuffer = ByteBuffer.allocate(4096)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        val bytesRead: Int
        bytesRead = try {
            channel.read(buffer)
        } catch (exception: IOException) {
            key.cancel()
            channel.close()
            rconSessions.remove(channel)
            sendQueues.remove(channel)
            return
        }
        if (bytesRead == -1) {
            key.cancel()
            channel.close()
            rconSessions.remove(channel)
            sendQueues.remove(channel)
            return
        }
        buffer.flip()
        handle(channel, RCONPacket(buffer))
    }

    private fun handle(channel: SocketChannel, packet: RCONPacket) {
        when (packet.getType()) {
            SERVERDATA_AUTH -> {
                val payload = ByteArray(1)
                if (String(packet.getPayload(), Charset.forName("UTF-8")).equals(password)) {
                    rconSessions.add(channel)
                    send(channel, RCONPacket(packet.getId(), SERVERDATA_AUTH_RESPONSE, payload))
                    return
                }
                send(channel, RCONPacket(-1, SERVERDATA_AUTH_RESPONSE, payload))
            }
            SERVERDATA_EXECCOMMAND -> {
                if (!rconSessions.contains(channel)) {
                    return
                }
                val command: String = String(packet.getPayload(), Charset.forName("UTF-8")).trim()
                synchronized(receiveQueue) { receiveQueue.add(RCONCommand(channel, packet.getId(), command)) }
            }
        }
    }

    @Throws(IOException::class)
    private fun write(key: SelectionKey) {
        val channel: SocketChannel = key.channel() as SocketChannel
        synchronized(sendQueues) {
            val queue: List<RCONPacket> = sendQueues[channel]!!
            val buffer: ByteBuffer = queue[0].toBuffer()
            try {
                channel.write(buffer)
                queue.remove(0)
            } catch (exception: IOException) {
                key.cancel()
                channel.close()
                rconSessions.remove(channel)
                sendQueues.remove(channel)
                return
            }
            if (queue.isEmpty()) {
                sendQueues.remove(channel)
            }
            key.interestOps(SelectionKey.OP_READ)
        }
    }

    private fun send(channel: SocketChannel, packet: RCONPacket) {
        if (!channel.keyFor(selector).isValid()) {
            return
        }
        synchronized(sendQueues) {
            val queue: List<RCONPacket> = sendQueues.computeIfAbsent(channel) { k -> ArrayList() }
            queue.add(packet)
        }
        selector.wakeup()
    }

    companion object {
        private const val SERVERDATA_AUTH = 3
        private const val SERVERDATA_AUTH_RESPONSE = 2
        private const val SERVERDATA_EXECCOMMAND = 2
        private const val SERVERDATA_RESPONSE_VALUE = 0
    }

    init {
        this.setName("RCON")
        running = true
        serverChannel = ServerSocketChannel.open()
        serverChannel.configureBlocking(false)
        serverChannel.socket().bind(InetSocketAddress(address, port))
        selector = SelectorProvider.provider().openSelector()
        serverChannel.register(selector, SelectionKey.OP_ACCEPT)
        this.password = password
    }
}