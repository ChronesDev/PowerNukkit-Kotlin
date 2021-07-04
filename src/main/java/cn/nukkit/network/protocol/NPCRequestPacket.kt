package cn.nukkit.network.protocol

import cn.nukkit.api.Since

@ToString
class NPCRequestPacket : DataPacket() {
    @Since("1.4.0.0-PN")
    var entityRuntimeId: Long = 0

    @Since("1.4.0.0-PN")
    var requestType: RequestType? = null

    @Since("1.4.0.0-PN")
    var commandString: String? = null

    @Since("1.4.0.0-PN")
    var actionType = 0

    @Since("1.4.0.0-PN")
    enum class RequestType {
        SET_ACTIONS, EXECUTE_ACTION, EXECUTE_CLOSING_COMMANDS, SET_NAME, SET_SKIN, SET_INTERACTION_TEXT
    }

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.NPC_REQUEST_PACKET
    }

    @Override
    override fun decode() {
        entityRuntimeId = this.getEntityRuntimeId()
        requestType = RequestType.values()[this.getByte()]
        commandString = this.getString()
        actionType = this.getByte()
    }

    @Override
    override fun encode() {
        this.putEntityRuntimeId(entityRuntimeId)
        this.putByte(requestType!!.ordinal() as Byte)
        this.putString(commandString)
        this.putByte(actionType.toByte())
    }
}