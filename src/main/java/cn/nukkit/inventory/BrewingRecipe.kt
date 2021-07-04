package cn.nukkit.inventory

import cn.nukkit.item.Item

class BrewingRecipe(input: Item, ingredient: Item, output: Item) : MixRecipe(input, ingredient, output) {
    @Override
    fun registerToCraftingManager(manager: CraftingManager) {
        manager.registerBrewingRecipe(this)
    }

    @Override
    fun getType(): RecipeType {
        throw UnsupportedOperationException()
    }
}