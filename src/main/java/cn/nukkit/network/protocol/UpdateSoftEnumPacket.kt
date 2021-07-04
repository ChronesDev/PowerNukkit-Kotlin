package cn.nukkit.network.protocol

import io.netty.util.internal.EmptyArrays

@ToString
class UpdateSoftEnumPacket : DataPacket() {
    val values: Array<String> = EmptyArrays.EMPTY_STRINGS
    var name = ""
    var type = Type.SET

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.UPDATE_SOFT_ENUM_PACKET
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putString(name)
        this.putUnsignedVarInt(values.size)
        for (value in values) {
            this.putString(value)
        }
        this.putByte(type.ordinal() as Byte)
    }

    enum class Type {
        ADD, REMOVE, SET
    }
}