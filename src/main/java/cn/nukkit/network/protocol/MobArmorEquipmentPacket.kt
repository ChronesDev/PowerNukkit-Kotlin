package cn.nukkit.network.protocol

import cn.nukkit.item.Item

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class MobArmorEquipmentPacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    var eid: Long = 0
    var slots: Array<Item?> = arrayOfNulls<Item>(4)

    @Override
    override fun decode() {
        eid = this.getEntityRuntimeId()
        slots = arrayOfNulls<Item>(4)
        slots[0] = this.getSlot()
        slots[1] = this.getSlot()
        slots[2] = this.getSlot()
        slots[3] = this.getSlot()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putEntityRuntimeId(eid)
        this.putSlot(slots[0])
        this.putSlot(slots[1])
        this.putSlot(slots[2])
        this.putSlot(slots[3])
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET
    }
}