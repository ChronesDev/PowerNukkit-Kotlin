package cn.nukkit.network.protocol

import cn.nukkit.api.DeprecationDetails

/**
 * @author Nukkit Project Team
 */
@ToString
class HurtArmorPacket : DataPacket() {
    @Since("1.3.0.0-PN")
    var cause = 0

    @Since("1.3.0.0-PN")
    var damage = 0

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putVarInt(cause)
        this.putVarInt(damage)
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.HURT_ARMOR_PACKET
    }
}