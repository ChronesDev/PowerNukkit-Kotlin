package cn.nukkit.inventory

import cn.nukkit.blockentity.BlockEntityCampfire

class CampfireInventory : ContainerInventory {
    constructor(campfire: BlockEntityCampfire?) : super(campfire, InventoryType.CAMPFIRE) {}
    constructor(furnace: BlockEntityCampfire?, inventoryType: InventoryType) : super(furnace, inventoryType) {}

    @Override
    override fun getHolder(): BlockEntityCampfire {
        return this.holder as BlockEntityCampfire
    }

    @Override
    override fun onSlotChange(index: Int, before: Item?, send: Boolean) {
        super.onSlotChange(index, before, send)
        getHolder().scheduleUpdate()
        getHolder().spawnToAll()
    }

    @Override
    fun getMaxStackSize(): Int {
        return 1
    }

    @Override
    override fun canAddItem(item: Item?): Boolean {
        return super.canAddItem(item)
    }
}