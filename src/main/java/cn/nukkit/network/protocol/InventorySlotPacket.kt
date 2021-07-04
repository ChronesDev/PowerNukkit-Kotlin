package cn.nukkit.network.protocol

import cn.nukkit.item.Item

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class InventorySlotPacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    var inventoryId = 0
    var slot = 0
    var item: Item? = null

    @Override
    override fun decode() {
        inventoryId = this.getUnsignedVarInt() as Int
        slot = this.getUnsignedVarInt() as Int
        item = this.getSlot()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putUnsignedVarInt(inventoryId)
        this.putUnsignedVarInt(slot)
        this.putSlot(item)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.INVENTORY_SLOT_PACKET
    }
}