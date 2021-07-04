package cn.nukkit.network.protocol

import lombok.ToString

/**
 * @author CreeperFace
 * @since 30. 10. 2016
 */
@ToString
class BossEventPacket : DataPacket() {
    var bossEid: Long = 0
    var type = 0
    var playerEid: Long = 0
    var healthPercent = 0f
    var title = ""
    var unknown: Short = 0
    var color = 0
    var overlay = 0

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        bossEid = this.getEntityUniqueId()
        type = this.getUnsignedVarInt() as Int
        when (type) {
            TYPE_REGISTER_PLAYER, TYPE_UNREGISTER_PLAYER -> playerEid = this.getEntityUniqueId()
            TYPE_SHOW -> {
                title = this.getString()
                healthPercent = this.getLFloat()
                unknown = this.getShort() as Short
                color = this.getUnsignedVarInt() as Int
                overlay = this.getUnsignedVarInt() as Int
            }
            TYPE_UNKNOWN_6 -> {
                unknown = this.getShort() as Short
                color = this.getUnsignedVarInt() as Int
                overlay = this.getUnsignedVarInt() as Int
            }
            TYPE_TEXTURE -> {
                color = this.getUnsignedVarInt() as Int
                overlay = this.getUnsignedVarInt() as Int
            }
            TYPE_HEALTH_PERCENT -> healthPercent = this.getLFloat()
            TYPE_TITLE -> title = this.getString()
        }
    }

    @Override
    override fun encode() {
        this.reset()
        this.putEntityUniqueId(bossEid)
        this.putUnsignedVarInt(type)
        when (type) {
            TYPE_REGISTER_PLAYER, TYPE_UNREGISTER_PLAYER -> this.putEntityUniqueId(playerEid)
            TYPE_SHOW -> {
                this.putString(title)
                this.putLFloat(healthPercent)
                this.putShort(unknown)
                this.putUnsignedVarInt(color)
                this.putUnsignedVarInt(overlay)
            }
            TYPE_UNKNOWN_6 -> {
                this.putShort(unknown)
                this.putUnsignedVarInt(color)
                this.putUnsignedVarInt(overlay)
            }
            TYPE_TEXTURE -> {
                this.putUnsignedVarInt(color)
                this.putUnsignedVarInt(overlay)
            }
            TYPE_HEALTH_PERCENT -> this.putLFloat(healthPercent)
            TYPE_TITLE -> this.putString(title)
        }
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.BOSS_EVENT_PACKET

        /* S2C: Shows the bossbar to the player. */
        const val TYPE_SHOW = 0

        /* C2S: Registers a player to a boss fight. */
        const val TYPE_REGISTER_PLAYER = 1
        const val TYPE_UPDATE = 1

        /* S2C: Removes the bossbar from the client. */
        const val TYPE_HIDE = 2

        /* C2S: Unregisters a player from a boss fight. */
        const val TYPE_UNREGISTER_PLAYER = 3

        /* S2C: Sets the bar percentage. */
        const val TYPE_HEALTH_PERCENT = 4

        /* S2C: Sets title of the bar. */
        const val TYPE_TITLE = 5

        /* S2C: Not sure on this. Includes color and overlay fields, plus an unknown short. TODO: check this */
        const val TYPE_UNKNOWN_6 = 6

        /* S2C: Not implemented :( Intended to alter bar appearance, but these currently produce no effect on clientside whatsoever. */
        const val TYPE_TEXTURE = 7
    }
}