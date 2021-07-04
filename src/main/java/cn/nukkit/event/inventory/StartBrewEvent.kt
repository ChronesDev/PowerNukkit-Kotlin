package cn.nukkit.event.inventory

import cn.nukkit.blockentity.BlockEntityBrewingStand

/**
 * @author CreeperFace
 */
class StartBrewEvent(blockEntity: BlockEntityBrewingStand) : InventoryEvent(blockEntity.getInventory()), Cancellable {
    private val brewingStand: BlockEntityBrewingStand
    private val ingredient: Item
    private val potions: Array<Item?>
    fun getBrewingStand(): BlockEntityBrewingStand {
        return brewingStand
    }

    fun getIngredient(): Item {
        return ingredient
    }

    fun getPotions(): Array<Item?> {
        return potions
    }

    /**
     * @param index Potion index in range 0 - 2
     * @return potion
     */
    fun getPotion(index: Int): Item? {
        return potions[index]
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        brewingStand = blockEntity
        ingredient = blockEntity.getInventory().getIngredient()
        potions = arrayOfNulls<Item>(3)
        for (i in 0..2) {
            potions[i] = blockEntity.getInventory().getItem(i)
        }
    }
}