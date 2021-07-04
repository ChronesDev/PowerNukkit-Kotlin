package cn.nukkit.inventory

import cn.nukkit.item.Item

/**
 * @author MagicDroidX (Nukkit Project)
 */
class FurnaceRecipe(result: Item, ingredient: Item) : SmeltingRecipe {
    private val output: Item
    private var ingredient: Item
    fun setInput(item: Item) {
        ingredient = item.clone()!!
    }

    @Override
    override fun getInput(): Item {
        return ingredient.clone()!!
    }

    @Override
    fun getResult(): Item {
        return output.clone()!!
    }

    @Override
    fun registerToCraftingManager(manager: CraftingManager) {
        manager.registerFurnaceRecipe(this)
    }

    @Override
    fun getType(): RecipeType {
        return if (ingredient.hasMeta()) RecipeType.FURNACE_DATA else RecipeType.FURNACE
    }

    init {
        output = result.clone()!!
        this.ingredient = ingredient.clone()!!
    }
}