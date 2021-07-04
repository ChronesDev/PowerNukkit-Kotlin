package cn.nukkit.inventory

import cn.nukkit.item.Item

class ContainerRecipe(input: Item, ingredient: Item, output: Item) : MixRecipe(input, ingredient, output) {
    @Override
    fun registerToCraftingManager(manager: CraftingManager) {
        manager.registerContainerRecipe(this)
    }

    @Override
    fun getType(): RecipeType {
        throw UnsupportedOperationException()
    }
}