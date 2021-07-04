package cn.nukkit.inventory

import cn.nukkit.item.Item

class CampfireRecipe(result: Item, ingredient: Item) : SmeltingRecipe {
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
        manager.registerCampfireRecipe(this)
    }

    @Override
    fun getType(): RecipeType {
        return if (ingredient.hasMeta()) RecipeType.CAMPFIRE_DATA else RecipeType.CAMPFIRE
    }

    init {
        output = result.clone()!!
        this.ingredient = ingredient.clone()!!
    }
}