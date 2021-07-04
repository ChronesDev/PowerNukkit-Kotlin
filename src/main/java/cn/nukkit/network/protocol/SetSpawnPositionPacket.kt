package cn.nukkit.network.protocol

import cn.nukkit.api.DeprecationDetails

/**
 * @author Nukkit Project Team
 */
@ToString
class SetSpawnPositionPacket : DataPacket() {
    var spawnType = 0
    var y = 0
    var z = 0
    var x = 0

    @Since("1.3.0.0-PN")
    var dimension = 0

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putVarInt(spawnType)
        this.putBlockVector3(x, y, z)
        this.putVarInt(dimension)
        this.putBlockVector3(x, y, z)
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.SET_SPAWN_POSITION_PACKET
        const val TYPE_PLAYER_SPAWN = 0
        const val TYPE_WORLD_SPAWN = 1
    }
}