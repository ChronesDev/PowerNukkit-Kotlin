package cn.nukkit.network.protocol

import cn.nukkit.api.Since

@Since("1.3.0.0-PN")
@ToString
class EmoteListPacket : DataPacket() {
    @Since("1.3.0.0-PN")
    var runtimeId: Long = 0

    @Since("1.3.0.0-PN")
    val pieceIds: List<UUID> = ObjectArrayList()

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        runtimeId = this.getEntityRuntimeId()
        val size = this.getUnsignedVarInt() as Int
        for (i in 0 until size) {
            val id: UUID = this.getUUID()
            pieceIds.add(id)
        }
    }

    @Override
    override fun encode() {
        this.reset()
        this.putEntityRuntimeId(runtimeId)
        this.putUnsignedVarInt(pieceIds.size())
        for (id in pieceIds) {
            this.putUUID(id)
        }
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.EMOTE_LIST_PACKET
    }
}