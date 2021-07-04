package cn.nukkit.inventory

import cn.nukkit.api.Since

/**
 * @author CreeperFace
 */
interface CraftingRecipe : Recipe {
    val recipeId: String?
    var id: UUID?
    fun requiresCraftingTable(): Boolean
    val extraResults: List<Any?>?
    val allResults: List<Any?>?
    val priority: Int

    /**
     * Returns whether the specified list of crafting grid inputs and outputs matches this recipe. Outputs DO NOT
     * include the primary result item.
     *
     * @param inputList  list of items taken from the crafting grid
     * @param extraOutputList list of items put back into the crafting grid (secondary results)
     * @return bool
     */
    @Since("1.3.0.0-PN")
    fun matchItems(inputList: List<Item?>?, extraOutputList: List<Item?>?): Boolean

    @Since("1.3.0.0-PN")
    fun matchItems(inputList: List<Item?>?, extraOutputList: List<Item?>?, multiplier: Int): Boolean

    @get:Since("1.3.0.0-PN")
    val ingredientsAggregate: List<Any?>?
}