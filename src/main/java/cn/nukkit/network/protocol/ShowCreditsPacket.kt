package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class ShowCreditsPacket : DataPacket() {
    var eid: Long = 0
    var status = 0

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        eid = this.getEntityRuntimeId()
        status = this.getVarInt()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putEntityRuntimeId(eid)
        this.putVarInt(status)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.SHOW_CREDITS_PACKET
        const val STATUS_START_CREDITS = 0
        const val STATUS_END_CREDITS = 1
    }
}