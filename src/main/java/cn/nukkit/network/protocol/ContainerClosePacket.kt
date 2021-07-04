package cn.nukkit.network.protocol

import cn.nukkit.api.Since

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class ContainerClosePacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    var windowId = 0

    @Since("1.4.0.0-PN")
    var wasServerInitiated = true

    @Override
    override fun decode() {
        windowId = this.getByte() as Byte.toInt()
        wasServerInitiated = this.getBoolean()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putByte(windowId.toByte())
        this.putBoolean(wasServerInitiated)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.CONTAINER_CLOSE_PACKET
    }
}