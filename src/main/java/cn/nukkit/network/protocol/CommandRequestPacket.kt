package cn.nukkit.network.protocol

import cn.nukkit.network.protocol.types.CommandOriginData

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class CommandRequestPacket : DataPacket() {
    var command: String? = null
    var data: CommandOriginData? = null

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        command = this.getString()
        val type: Origin = CommandOriginData.Origin.values().get(this.getVarInt())
        val uuid: UUID = this.getUUID()
        val requestId: String = this.getString()
        var varLong: Long? = null
        if (type === CommandOriginData.Origin.DEV_CONSOLE || type === CommandOriginData.Origin.TEST) {
            varLong = this.getVarLong()
        }
        data = CommandOriginData(type, uuid, requestId, varLong)
    }

    @Override
    override fun encode() {
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.COMMAND_REQUEST_PACKET
        const val TYPE_PLAYER = 0
        const val TYPE_COMMAND_BLOCK = 1
        const val TYPE_MINECART_COMMAND_BLOCK = 2
        const val TYPE_DEV_CONSOLE = 3
        const val TYPE_AUTOMATION_PLAYER = 4
        const val TYPE_CLIENT_AUTOMATION = 5
        const val TYPE_DEDICATED_SERVER = 6
        const val TYPE_ENTITY = 7
        const val TYPE_VIRTUAL = 8
        const val TYPE_GAME_ARGUMENT = 9
        const val TYPE_INTERNAL = 10
    }
}