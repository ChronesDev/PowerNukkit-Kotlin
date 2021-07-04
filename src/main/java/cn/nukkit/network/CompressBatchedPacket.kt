package cn.nukkit.network

import cn.nukkit.Server

/**
 * @author MagicDroidX (Nukkit Project)
 */
class CompressBatchedPacket(var data: ByteArray?, targets: List<InetSocketAddress?>?, level: Int, channel: Int) : AsyncTask() {
    var level = 7
    var finalData: ByteArray
    var channel = 0
    var targets: List<InetSocketAddress?>?

    constructor(data: ByteArray?, targets: List<InetSocketAddress?>?) : this(data, targets, 7) {}
    constructor(data: ByteArray?, targets: List<InetSocketAddress?>?, level: Int) : this(data, targets, level, 0) {}

    @Override
    fun onRun() {
        try {
            finalData = Network.deflateRaw(data, level)
            data = null
        } catch (e: Exception) {
            //ignore
        }
    }

    @Override
    fun onCompletion(server: Server) {
        server.broadcastPacketsCallback(finalData, targets)
    }

    init {
        this.targets = targets
        this.level = level
        this.channel = channel
    }
}