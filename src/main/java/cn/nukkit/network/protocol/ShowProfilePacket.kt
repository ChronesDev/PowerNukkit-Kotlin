package cn.nukkit.network.protocol

import lombok.ToString

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class ShowProfilePacket : DataPacket() {
    var xuid: String? = null

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        xuid = this.getString()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putString(xuid)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.SHOW_PROFILE_PACKET
    }
}