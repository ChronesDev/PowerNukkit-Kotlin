package cn.nukkit.inventory

import cn.nukkit.Player

class PlayerOffhandInventory(holder: EntityHumanType?) : BaseInventory(holder, InventoryType.OFFHAND) {
    @Override
    fun setSize(size: Int) {
        throw UnsupportedOperationException("Offhand can only carry one item at a time")
    }

    @Override
    override fun onSlotChange(index: Int, before: Item?, send: Boolean) {
        val holder: EntityHuman? = getHolder()
        if (holder is Player && !(holder as Player?).spawned) {
            return
        }
        sendContents(this.getViewers())
        sendContents(holder.getViewers().values())
    }

    @Override
    override fun sendContents(vararg players: Player) {
        val item: Item = this.getItem(0)
        val pk: MobEquipmentPacket = createMobEquipmentPacket(item)
        for (player in players) {
            if (player === getHolder()) {
                val pk2 = InventoryContentPacket()
                pk2.inventoryId = ContainerIds.OFFHAND
                pk2.slots = arrayOf<Item>(item)
                player.dataPacket(pk2)
            } else {
                player.dataPacket(pk)
            }
        }
    }

    @Override
    override fun sendSlot(index: Int, vararg players: Player) {
        val item: Item = this.getItem(0)
        val pk: MobEquipmentPacket = createMobEquipmentPacket(item)
        for (player in players) {
            if (player === getHolder()) {
                val pk2 = InventorySlotPacket()
                pk2.inventoryId = ContainerIds.OFFHAND
                pk2.item = item
                player.dataPacket(pk2)
            } else {
                player.dataPacket(pk)
            }
        }
    }

    private fun createMobEquipmentPacket(item: Item): MobEquipmentPacket {
        val pk = MobEquipmentPacket()
        pk.eid = getHolder().getId()
        pk.item = item
        pk.inventorySlot = 1
        pk.windowId = ContainerIds.OFFHAND
        pk.tryEncode()
        return pk
    }

    @Override
    override fun getHolder(): EntityHuman? {
        return super.getHolder() as EntityHuman?
    }
}