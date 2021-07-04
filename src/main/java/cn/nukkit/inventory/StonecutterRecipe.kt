package cn.nukkit.inventory

import cn.nukkit.item.Item

@ToString
class StonecutterRecipe(private var recipeId: String?, private val priority: Int, result: Item, ingredient: Item) : Recipe {
    private val output: Item
    private var least: Long = 0
    private var most: Long = 0
    private val ingredient: Item

    constructor(result: Item, ingredient: Item) : this(null, 10, result, ingredient) {}

    @Override
    fun getResult(): Item {
        return output.clone()!!
    }

    fun getRecipeId(): String? {
        return recipeId
    }

    fun getId(): UUID {
        return UUID(least, most)
    }

    fun setId(uuid: UUID?) {
        least = uuid.getLeastSignificantBits()
        most = uuid.getMostSignificantBits()
        if (recipeId == null) {
            recipeId = getId().toString()
        }
    }

    fun getIngredient(): Item {
        return ingredient.clone()!!
    }

    @Override
    fun registerToCraftingManager(manager: CraftingManager) {
        manager.registerStonecutterRecipe(this)
    }

    @Override
    fun getType(): RecipeType {
        return RecipeType.STONECUTTER
    }

    fun getPriority(): Int {
        return priority
    }

    init {
        output = result.clone()!!
        if (ingredient.getCount() < 1) {
            throw IllegalArgumentException("Recipe '" + recipeId + "' Ingredient amount was not 1 (value: " + ingredient.getCount() + ")")
        }
        this.ingredient = ingredient.clone()!!
    }
}