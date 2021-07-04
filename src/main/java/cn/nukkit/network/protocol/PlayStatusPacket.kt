package cn.nukkit.network.protocol

import lombok.ToString

/**
 * @since 15-10-13
 */
@ToString
class PlayStatusPacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    var status = 0

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putInt(status)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.PLAY_STATUS_PACKET
        const val LOGIN_SUCCESS = 0
        const val LOGIN_FAILED_CLIENT = 1
        const val LOGIN_FAILED_SERVER = 2
        const val PLAYER_SPAWN = 3
        const val LOGIN_FAILED_INVALID_TENANT = 4
        const val LOGIN_FAILED_VANILLA_EDU = 5
        const val LOGIN_FAILED_EDU_VANILLA = 6
        const val LOGIN_FAILED_SERVER_FULL = 7
    }
}