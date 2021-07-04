package cn.nukkit.network.protocol

import lombok.ToString

/**
 * @author Tee7even
 */
@ToString
class SetTitlePacket : DataPacket() {
    var type = 0
    var text = ""
    var fadeInTime = 0
    var stayTime = 0
    var fadeOutTime = 0

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        type = this.getVarInt()
        text = this.getString()
        fadeInTime = this.getVarInt()
        stayTime = this.getVarInt()
        fadeOutTime = this.getVarInt()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putVarInt(type)
        this.putString(text)
        this.putVarInt(fadeInTime)
        this.putVarInt(stayTime)
        this.putVarInt(fadeOutTime)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.SET_TITLE_PACKET
        const val TYPE_CLEAR = 0
        const val TYPE_RESET = 1
        const val TYPE_TITLE = 2
        const val TYPE_SUBTITLE = 3
        const val TYPE_ACTION_BAR = 4
        const val TYPE_ANIMATION_TIMES = 5
    }
}