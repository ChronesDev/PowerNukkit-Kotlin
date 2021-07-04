package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class SpawnExperienceOrbPacket : DataPacket() {
    var x = 0f
    var y = 0f
    var z = 0f
    var amount = 0

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putVector3f(x, y, z)
        this.putUnsignedVarInt(amount)
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.SPAWN_EXPERIENCE_ORB_PACKET
    }
}