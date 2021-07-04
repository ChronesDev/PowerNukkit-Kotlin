package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class SetLocalPlayerAsInitializedPacket : DataPacket() {
    var eid: Long = 0

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        eid = this.getUnsignedVarLong()
    }

    @Override
    override fun encode() {
        this.putUnsignedVarLong(eid)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET
    }
}