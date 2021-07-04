package cn.nukkit.network.protocol

import lombok.ToString

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class MobEffectPacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    var eid: Long = 0
    var eventId = 0
    var effectId = 0
    var amplifier = 0
    var particles = true
    var duration = 0

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putEntityRuntimeId(eid)
        this.putByte(eventId.toByte())
        this.putVarInt(effectId)
        this.putVarInt(amplifier)
        this.putBoolean(particles)
        this.putVarInt(duration)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.MOB_EFFECT_PACKET
        const val EVENT_ADD: Byte = 1
        const val EVENT_MODIFY: Byte = 2
        const val EVENT_REMOVE: Byte = 3
    }
}