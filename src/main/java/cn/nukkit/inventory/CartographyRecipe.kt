package cn.nukkit.inventory

import cn.nukkit.item.Item

class CartographyRecipe : ShapelessRecipe {
    constructor(result: Item, ingredients: Collection<Item>) : super(result, ingredients) {}
    constructor(recipeId: String?, priority: Int, result: Item, ingredients: Collection<Item>) : super(recipeId, priority, result, ingredients) {}

    @Override
    override fun registerToCraftingManager(manager: CraftingManager) {
        manager.registerCartographyRecipe(this)
    }

    @Override
    override fun getType(): RecipeType {
        return RecipeType.CARTOGRAPHY
    }
}