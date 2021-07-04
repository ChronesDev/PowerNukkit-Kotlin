package cn.nukkit.network.protocol

import cn.nukkit.api.Since

@Since("1.3.0.0-PN")
@ToString
class UpdatePlayerGameTypePacket : DataPacket() {
    @Since("1.3.0.0-PN")
    var gameType: GameType? = null

    @Since("1.3.0.0-PN")
    var entityId: Long = 0

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        gameType = GameType.from(this.getVarInt())
        entityId = this.getVarLong()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putVarInt(gameType!!.ordinal())
        this.putVarLong(entityId)
    }

    @Since("1.3.0.0-PN")
    enum class GameType {
        SURVIVAL, CREATIVE, ADVENTURE, SURVIVAL_VIEWER, CREATIVE_VIEWER, DEFAULT, WORLD_DEFAULT;

        companion object {
            private val VALUES = values()
            fun from(id: Int): GameType {
                return VALUES[id]
            }
        }
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.UPDATE_PLAYER_GAME_TYPE_PACKET
    }
}