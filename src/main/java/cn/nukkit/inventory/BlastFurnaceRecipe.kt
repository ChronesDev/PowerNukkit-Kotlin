package cn.nukkit.inventory

import cn.nukkit.item.Item

class BlastFurnaceRecipe(result: Item, ingredient: Item) : SmeltingRecipe {
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
        manager.registerBlastFurnaceRecipe(this)
    }

    @Override
    fun getType(): RecipeType {
        return if (ingredient.hasMeta()) RecipeType.BLAST_FURNACE_DATA else RecipeType.BLAST_FURNACE
    }

    init {
        output = result.clone()!!
        this.ingredient = ingredient.clone()!!
    }
}