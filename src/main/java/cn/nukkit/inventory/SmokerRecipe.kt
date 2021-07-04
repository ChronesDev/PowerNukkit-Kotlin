package cn.nukkit.inventory

import cn.nukkit.item.Item

class SmokerRecipe(result: Item, ingredient: Item) : SmeltingRecipe {
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
        manager.registerSmokerRecipe(this)
    }

    @Override
    fun getType(): RecipeType {
        return if (ingredient.hasMeta()) RecipeType.SMOKER_DATA else RecipeType.SMOKER
    }

    init {
        output = result.clone()!!
        this.ingredient = ingredient.clone()!!
    }
}