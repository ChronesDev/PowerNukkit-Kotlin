package cn.nukkit.network.protocol

import cn.nukkit.item.Item

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class MobEquipmentPacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    var eid: Long = 0
    var item: Item? = null
    var inventorySlot = 0
    var hotbarSlot = 0
    var windowId = 0

    @Override
    override fun decode() {
        eid = this.getEntityRuntimeId() //EntityRuntimeID
        item = this.getSlot()
        inventorySlot = this.getByte()
        hotbarSlot = this.getByte()
        windowId = this.getByte()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putEntityRuntimeId(eid) //EntityRuntimeID
        this.putSlot(item)
        this.putByte(inventorySlot.toByte())
        this.putByte(hotbarSlot.toByte())
        this.putByte(windowId.toByte())
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.MOB_EQUIPMENT_PACKET
    }
}