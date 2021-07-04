package cn.nukkit.network.protocol

import cn.nukkit.api.Since

@Since("1.3.0.0-PN")
@ToString
class PlayerArmorDamagePacket : DataPacket() {
    @Since("1.3.0.0-PN")
    val flags: Set<PlayerArmorDamageFlag> = EnumSet.noneOf(PlayerArmorDamageFlag::class.java)

    @Since("1.3.0.0-PN")
    val damage = IntArray(4)

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        val flagsval: Int = this.getByte()
        for (i in 0..3) {
            if (flagsval and (1 shl i) != 0) {
                flags.add(PlayerArmorDamageFlag.values()[i])
                damage[i] = this.getVarInt()
            }
        }
    }

    @Override
    override fun encode() {
        this.reset()
        var outflags = 0
        for (flag in flags) {
            outflags = outflags or (1 shl flag.ordinal())
        }
        this.putByte(outflags.toByte())
        for (flag in flags) {
            this.putVarInt(damage[flag.ordinal()])
        }
    }

    @Since("1.3.0.0-PN")
    enum class PlayerArmorDamageFlag {
        HELMET, CHESTPLATE, LEGGINGS, BOOTS
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.PLAYER_ARMOR_DAMAGE_PACKET
    }
}