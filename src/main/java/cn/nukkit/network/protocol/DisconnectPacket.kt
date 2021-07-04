package cn.nukkit.network.protocol

import lombok.ToString

/**
 * @since 15-10-12
 */
@ToString
class DisconnectPacket : DataPacket() {
    var hideDisconnectionScreen = false
    var message: String? = null

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        hideDisconnectionScreen = this.getBoolean()
        message = this.getString()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putBoolean(hideDisconnectionScreen)
        if (!hideDisconnectionScreen) {
            this.putString(message)
        }
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.DISCONNECT_PACKET
    }
}