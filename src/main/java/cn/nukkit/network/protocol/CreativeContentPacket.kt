package cn.nukkit.network.protocol

import cn.nukkit.api.Since

@Since("1.3.0.0-PN")
@ToString
class CreativeContentPacket : DataPacket() {
    @Since("1.3.0.0-PN")
    var entries: Array<Item> = Item.EMPTY_ARRAY

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putUnsignedVarInt(entries.size)
        for (i in entries.indices) {
            this.putUnsignedVarInt(i + 1)
            this.putSlot(entries[i], true)
        }
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.CREATIVE_CONTENT_PACKET
    }
}