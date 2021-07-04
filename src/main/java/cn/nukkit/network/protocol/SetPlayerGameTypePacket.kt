package cn.nukkit.network.protocol

import lombok.ToString

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class SetPlayerGameTypePacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    var gamemode = 0

    @Override
    override fun decode() {
        gamemode = this.getVarInt()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putVarInt(gamemode)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET
    }
}