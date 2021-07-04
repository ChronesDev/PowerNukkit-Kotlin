package cn.nukkit.network.protocol

import cn.nukkit.entity.data.Skin

/**
 * @author Nukkit Project Team
 */
@ToString
class PlayerListPacket : DataPacket() {
    var type: Byte = 0
    var entries = Entry.EMPTY_ARRAY

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putByte(type)
        this.putUnsignedVarInt(entries.size)
        for (entry in entries) {
            this.putUUID(entry!!.uuid)
            if (type == TYPE_ADD) {
                this.putVarLong(entry!!.entityId)
                this.putString(entry!!.name)
                this.putString(entry!!.xboxUserId)
                this.putString(entry!!.platformChatId)
                this.putLInt(entry!!.buildPlatform)
                this.putSkin(entry!!.skin)
                this.putBoolean(entry!!.isTeacher)
                this.putBoolean(entry!!.isHost)
            }
        }
        if (type == TYPE_ADD) {
            for (entry in entries) {
                this.putBoolean(true)
            }
        }
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @ToString
    class Entry {
        val uuid: UUID
        var entityId: Long = 0
        var name = ""
        var xboxUserId = "" //TODO
        var platformChatId = "" //TODO
        var buildPlatform = -1
        var skin: Skin? = null
        var isTeacher = false
        var isHost = false

        constructor(uuid: UUID) {
            this.uuid = uuid
        }

        constructor(uuid: UUID, entityId: Long, name: String, skin: Skin?) : this(uuid, entityId, name, skin, "") {}
        constructor(uuid: UUID, entityId: Long, name: String, skin: Skin?, xboxUserId: String?) {
            this.uuid = uuid
            this.entityId = entityId
            this.name = name
            this.skin = skin
            this.xboxUserId = xboxUserId ?: ""
        }

        companion object {
            val EMPTY_ARRAY = arrayOfNulls<Entry>(0)
        }
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.PLAYER_LIST_PACKET
        const val TYPE_ADD: Byte = 0
        const val TYPE_REMOVE: Byte = 1
    }
}