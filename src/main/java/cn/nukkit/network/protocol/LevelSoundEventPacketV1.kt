package cn.nukkit.network.protocol

import cn.nukkit.math.Vector3f

@ToString
class LevelSoundEventPacketV1 : LevelSoundEventPacket() {
    override var sound = 0
    override var x = 0f
    override var y = 0f
    override var z = 0f
    override var extraData = -1 //TODO: Check name
    var pitch = 1 //TODO: Check name
    override var isBabyMob = false
    override var isGlobal = false

    @Override
    override fun decode() {
        sound = this.getByte()
        val v: Vector3f = this.getVector3f()
        x = v.x
        y = v.y
        z = v.z
        extraData = this.getVarInt()
        pitch = this.getVarInt()
        isBabyMob = this.getBoolean()
        isGlobal = this.getBoolean()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putByte(sound.toByte())
        this.putVector3f(x, y, z)
        this.putVarInt(extraData)
        this.putVarInt(pitch)
        this.putBoolean(isBabyMob)
        this.putBoolean(isGlobal)
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V1
    }
}