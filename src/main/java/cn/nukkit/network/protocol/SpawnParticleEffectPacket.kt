package cn.nukkit.network.protocol

import cn.nukkit.math.Vector3f

@ToString
class SpawnParticleEffectPacket : DataPacket() {
    var dimensionId = 0
    var uniqueEntityId: Long = -1
    var position: Vector3f? = null
    var identifier: String? = null

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putByte(dimensionId.toByte())
        this.putEntityUniqueId(uniqueEntityId)
        this.putVector3f(position)
        this.putString(identifier)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.SPAWN_PARTICLE_EFFECT_PACKET
    }
}