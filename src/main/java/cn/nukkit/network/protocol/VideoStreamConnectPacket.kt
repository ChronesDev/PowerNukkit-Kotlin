package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class VideoStreamConnectPacket : DataPacket() {
    var address: String? = null
    var screenshotFrequency = 0f
    var action: Byte = 0

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.putString(address)
        this.putLFloat(screenshotFrequency)
        this.putByte(action)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.VIDEO_STREAM_CONNECT_PACKET
        const val ACTION_OPEN: Byte = 0
        const val ACTION_CLOSE: Byte = 1
    }
}