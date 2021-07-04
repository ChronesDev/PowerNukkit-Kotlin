package cn.nukkit.network.protocol

import cn.nukkit.api.DeprecationDetails

/**
 * @author Nukkit Project Team
 */
@ToString
class CraftingEventPacket : DataPacket() {
    var windowId = 0
    var type = 0
    var id: UUID? = null
    var input: Array<Item>
    var output: Array<Item>

    @Override
    override fun decode() {
        windowId = this.getByte()
        type = this.getUnsignedVarInt() as Int
        id = this.getUUID()
        input = this.getArray(Item::class.java, BinaryStream::getSlot)
        output = this.getArray(Item::class.java, BinaryStream::getSlot)
    }

    @Override
    override fun encode() {
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.CRAFTING_EVENT_PACKET

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "The name don't match the packet content")
        val TYPE_SHAPELESS = 0

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "The name don't match the packet content")
        val TYPE_SHAPED = 1

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "The name don't match the packet content")
        val TYPE_FURNACE = 2

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "The name don't match the packet content")
        val TYPE_FURNACE_DATA = 3

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "The name don't match the packet content")
        val TYPE_MULTI = 4

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "The name don't match the packet content")
        val TYPE_SHULKER_BOX = 5

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val TYPE_INVENTORY = 0

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val TYPE_CRAFTING = 1

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val TYPE_WORKBENCH = 2
    }
}