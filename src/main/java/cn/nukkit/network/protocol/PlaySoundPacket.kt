package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class PlaySoundPacket : DataPacket() {
    var name: String? = null
    var x = 0
    var y = 0
    var z = 0
    var volume = 0f
    var pitch = 0f

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
        this.putString(name)
        this.putBlockVector3(x * 8, y * 8, z * 8)
        this.putLFloat(volume)
        this.putLFloat(pitch)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.PLAY_SOUND_PACKET
    }
}