package cn.nukkit.inventory

import cn.nukkit.blockentity.BlockEntityBrewingStand

class BrewingInventory(brewingStand: BlockEntityBrewingStand?) : ContainerInventory(brewingStand, InventoryType.BREWING_STAND) {
    @Override
    override fun getHolder(): BlockEntityBrewingStand {
        return this.holder as BlockEntityBrewingStand
    }

    fun getIngredient(): Item {
        return getItem(0)
    }

    fun setIngredient(item: Item?) {
        setItem(0, item)
    }

    fun setFuel(fuel: Item?) {
        setItem(4, fuel)
    }

    fun getFuel(): Item {
        return getItem(4)
    }

    @Override
    override fun onSlotChange(index: Int, before: Item?, send: Boolean) {
        super.onSlotChange(index, before, send)
        if (index >= 1 && index <= 3) {
            getHolder().updateBlock()
        }
        getHolder().scheduleUpdate()
    }
}