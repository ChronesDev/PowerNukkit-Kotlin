package cn.nukkit.network.protocol

import cn.nukkit.Player

/**
 * @author Nukkit Project Team
 */
@ToString
class AdventureSettingsPacket : DataPacket() {
    var flags: Long = 0
    var commandPermission = PERMISSION_NORMAL.toLong()
    var flags2: Long = 0
    var playerPermission: Long = Player.PERMISSION_MEMBER
    var customFlags //...
            : Long = 0
    var entityUniqueId //This is a little-endian long, NOT a var-long. (WTF Mojang)
            : Long = 0

    override fun decode() {
        flags = getUnsignedVarInt()
        commandPermission = getUnsignedVarInt()
        flags2 = getUnsignedVarInt()
        playerPermission = getUnsignedVarInt()
        customFlags = getUnsignedVarInt()
        entityUniqueId = getLLong()
    }

    override fun encode() {
        this.reset()
        this.putUnsignedVarInt(flags)
        this.putUnsignedVarInt(commandPermission)
        this.putUnsignedVarInt(flags2)
        this.putUnsignedVarInt(playerPermission)
        this.putUnsignedVarInt(customFlags)
        this.putLLong(entityUniqueId)
    }

    fun getFlag(flag: Int): Boolean {
        return if (flag and BITFLAG_SECOND_SET != 0) {
            flags2 and flag.toLong() != 0L
        } else flags and flag.toLong() != 0L
    }

    fun setFlag(flag: Int, value: Boolean) {
        val flags = flag and BITFLAG_SECOND_SET != 0
        if (value) {
            if (flags) {
                flags2 = flags2 or flag.toLong()
            } else {
                this.flags = this.flags or flag.toLong()
            }
        } else {
            if (flags) {
                flags2 = flags2 and flag.inv().toLong()
            } else {
                this.flags = this.flags and flag.inv().toLong()
            }
        }
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.ADVENTURE_SETTINGS_PACKET
        const val PERMISSION_NORMAL = 0
        const val PERMISSION_OPERATOR = 1
        const val PERMISSION_HOST = 2
        const val PERMISSION_AUTOMATION = 3
        const val PERMISSION_ADMIN = 4
        //TODO: check level 3
        /**
         * This constant is used to identify flags that should be set on the second field. In a sensible world, these
         * flags would all be set on the same packet field, but as of MCPE 1.2, the new abilities flags have for some
         * reason been assigned a separate field.
         */
        const val BITFLAG_SECOND_SET = 1 shl 16
        const val WORLD_IMMUTABLE = 0x01
        const val NO_PVP = 0x02
        const val AUTO_JUMP = 0x20
        const val ALLOW_FLIGHT = 0x40
        const val NO_CLIP = 0x80
        const val WORLD_BUILDER = 0x100
        const val FLYING = 0x200
        const val MUTED = 0x400
        const val BUILD_AND_MINE = 0x01 or BITFLAG_SECOND_SET
        const val DOORS_AND_SWITCHES = 0x02 or BITFLAG_SECOND_SET
        const val OPEN_CONTAINERS = 0x04 or BITFLAG_SECOND_SET
        const val ATTACK_PLAYERS = 0x08 or BITFLAG_SECOND_SET
        const val ATTACK_MOBS = 0x10 or BITFLAG_SECOND_SET
        const val OPERATOR = 0x20 or BITFLAG_SECOND_SET
        const val TELEPORT = 0x80 or BITFLAG_SECOND_SET
    }
}