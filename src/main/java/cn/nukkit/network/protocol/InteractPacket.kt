package cn.nukkit.network.protocol

import lombok.ToString

/**
 * @since 15-10-15
 */
@ToString
class InteractPacket : DataPacket() {
    var action = 0
    var target: Long = 0

    @Override
    override fun decode() {
        action = this.getByte()
        target = this.getEntityRuntimeId()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putByte(action.toByte())
        this.putEntityRuntimeId(target)
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.INTERACT_PACKET
        const val ACTION_VEHICLE_EXIT = 3
        const val ACTION_MOUSEOVER = 4
        const val ACTION_OPEN_INVENTORY = 6
    }
}