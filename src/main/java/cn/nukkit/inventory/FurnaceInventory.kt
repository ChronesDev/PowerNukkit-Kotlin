package cn.nukkit.inventory

import cn.nukkit.blockentity.BlockEntityFurnace

/**
 * @author MagicDroidX (Nukkit Project)
 */
class FurnaceInventory : ContainerInventory {
    constructor(furnace: BlockEntityFurnace?) : super(furnace, InventoryType.FURNACE) {}
    constructor(furnace: BlockEntityFurnace?, inventoryType: InventoryType) : super(furnace, inventoryType) {}

    @Override
    override fun getHolder(): BlockEntityFurnace {
        return this.holder as BlockEntityFurnace
    }

    fun getResult(): Item {
        return this.getItem(2)
    }

    fun getFuel(): Item {
        return this.getItem(1)
    }

    fun getSmelting(): Item {
        return this.getItem(0)
    }

    fun setResult(item: Item?): Boolean {
        return this.setItem(2, item)
    }

    fun setFuel(item: Item?): Boolean {
        return this.setItem(1, item)
    }

    fun setSmelting(item: Item?): Boolean {
        return this.setItem(0, item)
    }

    @Override
    override fun onSlotChange(index: Int, before: Item?, send: Boolean) {
        super.onSlotChange(index, before, send)
        getHolder().scheduleUpdate()
    }
}