package cn.nukkit.network.protocol

import lombok.ToString

@ToString(exclude = "sha256")
class ResourcePackDataInfoPacket : DataPacket() {
    var packId: UUID? = null
    var maxChunkSize = 0
    var chunkCount = 0
    var compressedPackSize: Long = 0
    var sha256: ByteArray
    var premium = false
    var type = TYPE_RESOURCE

    @Override
    override fun decode() {
        packId = UUID.fromString(this.getString())
        maxChunkSize = this.getLInt()
        chunkCount = this.getLInt()
        compressedPackSize = this.getLLong()
        sha256 = this.getByteArray()
        premium = this.getBoolean()
        type = this.getByte()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putString(packId.toString())
        this.putLInt(maxChunkSize)
        this.putLInt(chunkCount)
        this.putLLong(compressedPackSize)
        this.putByteArray(sha256)
        this.putBoolean(premium)
        this.putByte(type.toByte())
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.RESOURCE_PACK_DATA_INFO_PACKET
        const val TYPE_INVALID = 0
        const val TYPE_ADDON = 1
        const val TYPE_CACHED = 2
        const val TYPE_COPY_PROTECTED = 3
        const val TYPE_BEHAVIOR = 4
        const val TYPE_PERSONA_PIECE = 5
        const val TYPE_RESOURCE = 6
        const val TYPE_SKINS = 7
        const val TYPE_WORLD_TEMPLATE = 8
        const val TYPE_COUNT = 9
    }
}