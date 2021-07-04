package cn.nukkit.network.protocol

import cn.nukkit.api.DeprecationDetails

/**
 * @since 15-10-13
 */
@ToString
class TextPacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    var type: Byte = 0
    var source = ""
    var message = ""
    var parameters: Array<String> = EmptyArrays.EMPTY_STRINGS
    var isLocalized = false
    var xboxUserId = ""
    var platformChatId = ""

    @Override
    override fun decode() {
        type = getByte() as Byte
        isLocalized = this.getBoolean() || type == TYPE_TRANSLATION
        when (type) {
            TYPE_CHAT, TYPE_WHISPER, TYPE_ANNOUNCEMENT -> {
                source = this.getString()
                message = this.getString()
            }
            TYPE_RAW, TYPE_TIP, TYPE_SYSTEM, TYPE_OBJECT, TYPE_OBJECT_WHISPER -> message = this.getString()
            TYPE_TRANSLATION, TYPE_POPUP, TYPE_JUKEBOX_POPUP -> {
                message = this.getString()
                parameters = this.getArray(String::class.java, BinaryStream::getString)
            }
        }
        xboxUserId = this.getString()
        platformChatId = this.getString()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putByte(type)
        this.putBoolean(isLocalized || type == TYPE_TRANSLATION)
        when (type) {
            TYPE_CHAT, TYPE_WHISPER, TYPE_ANNOUNCEMENT -> {
                this.putString(source)
                this.putString(message)
            }
            TYPE_RAW, TYPE_TIP, TYPE_SYSTEM, TYPE_OBJECT, TYPE_OBJECT_WHISPER -> this.putString(message)
            TYPE_TRANSLATION, TYPE_POPUP, TYPE_JUKEBOX_POPUP -> {
                this.putString(message)
                this.putUnsignedVarInt(parameters.size)
                for (parameter in parameters) {
                    this.putString(parameter)
                }
            }
        }
        this.putString(xboxUserId)
        this.putString(platformChatId)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.TEXT_PACKET
        const val TYPE_RAW: Byte = 0
        const val TYPE_CHAT: Byte = 1
        const val TYPE_TRANSLATION: Byte = 2
        const val TYPE_POPUP: Byte = 3
        const val TYPE_JUKEBOX_POPUP: Byte = 4
        const val TYPE_TIP: Byte = 5
        const val TYPE_SYSTEM: Byte = 6
        const val TYPE_WHISPER: Byte = 7
        const val TYPE_ANNOUNCEMENT: Byte = 8

        @Since("1.3.0.0-PN")
        val TYPE_OBJECT: Byte = 9

        @Since("1.3.0.0-PN")
        val TYPE_OBJECT_WHISPER: Byte = 10
    }
}