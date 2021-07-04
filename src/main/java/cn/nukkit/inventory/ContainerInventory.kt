package cn.nukkit.inventory

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class ContainerInventory : BaseInventory {
    constructor(holder: InventoryHolder?, type: InventoryType) : super(holder, type) {}
    constructor(holder: InventoryHolder?, type: InventoryType, items: Map<Integer?, Item?>) : super(holder, type, items) {}
    constructor(holder: InventoryHolder?, type: InventoryType, items: Map<Integer?, Item?>, overrideSize: Integer?) : super(holder, type, items, overrideSize) {}
    constructor(holder: InventoryHolder?, type: InventoryType, items: Map<Integer?, Item?>, overrideSize: Integer?, overrideTitle: String?) : super(holder, type, items, overrideSize, overrideTitle) {}

    @Override
    override fun onOpen(who: Player) {
        super.onOpen(who)
        val pk = ContainerOpenPacket()
        pk.windowId = who.getWindowId(this)
        pk.type = this.getType().getNetworkType()
        val holder: InventoryHolder = this.getHolder()!!
        if (holder is Vector3) {
            pk.x = (holder as Vector3).getX()
            pk.y = (holder as Vector3).getY()
            pk.z = (holder as Vector3).getZ()
        } else {
            pk.z = 0
            pk.y = pk.z
            pk.x = pk.y
        }
        if (holder is Entity) {
            pk.entityId = (holder as Entity).getId()
        }
        who.dataPacket(pk)
        this.sendContents(who)
    }

    @Override
    override fun onClose(who: Player) {
        val pk = ContainerClosePacket()
        pk.windowId = who.getWindowId(this)
        pk.wasServerInitiated = who.getClosingWindowId() !== pk.windowId
        who.dataPacket(pk)
        super.onClose(who)
    }

    companion object {
        fun calculateRedstone(inv: Inventory?): Int {
            return if (inv == null) {
                0
            } else {
                var itemCount = 0
                var averageCount = 0f
                for (slot in 0 until inv.getSize()) {
                    val item: Item = inv.getItem(slot)
                    if (item.getId() !== 0) {
                        averageCount += item.getCount() as Float / Math.min(inv.getMaxStackSize(), item.getMaxStackSize()) as Float
                        ++itemCount
                    }
                }
                averageCount = averageCount / inv.getSize() as Float
                NukkitMath.floorFloat(averageCount * 14) + if (itemCount > 0) 1 else 0
            }
        }
    }
}