package cn.nukkit.inventory

import cn.nukkit.item.Item

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ShapelessRecipe(private override var recipeId: String?, private override val priority: Int, result: Item, ingredients: Collection<Item>) : CraftingRecipe {
    private val output: Item
    private var least: Long = 0
    private var most: Long = 0
    private val ingredients: List<Item>
    private override val ingredientsAggregate: List<Item>

    constructor(result: Item, ingredients: Collection<Item>) : this(null, 10, result, ingredients) {}

    @Override
    fun getResult(): Item {
        return output.clone()!!
    }

    @Override
    fun getRecipeId(): String? {
        return recipeId
    }

    @Override
    fun getId(): UUID {
        return UUID(least, most)
    }

    @Override
    fun setId(uuid: UUID) {
        least = uuid.getLeastSignificantBits()
        most = uuid.getMostSignificantBits()
        if (recipeId == null) {
            recipeId = getId().toString()
        }
    }

    fun getIngredientList(): List<Item> {
        val ingredients: List<Item> = ArrayList()
        for (ingredient in this.ingredients) {
            ingredients.add(ingredient.clone())
        }
        return ingredients
    }

    fun getIngredientCount(): Int {
        return ingredients.size()
    }

    @Override
    fun registerToCraftingManager(manager: CraftingManager) {
        manager.registerShapelessRecipe(this)
    }

    @Override
    fun getType(): RecipeType {
        return RecipeType.SHAPELESS
    }

    @Override
    override fun requiresCraftingTable(): Boolean {
        return ingredients.size() > 4
    }

    @Override
    fun getExtraResults(): List<Item> {
        return ArrayList()
    }

    @Override
    fun getAllResults(): List<Item>? {
        return null
    }

    @Override
    fun getPriority(): Int {
        return priority
    }

    fun matchItems(inputList: List<Item>, extraOutputList: List<Item>, multiplier: Int): Boolean {
        val haveInputs: List<Item> = ArrayList()
        for (item in inputList) {
            if (item.isNull()) continue
            haveInputs.add(item.clone())
        }
        val needInputs: List<Item> = ArrayList()
        if (multiplier != 1) {
            for (item in ingredientsAggregate) {
                if (item.isNull()) continue
                val itemClone: Item = item.clone()!!
                itemClone.setCount(itemClone.getCount() * multiplier)
                needInputs.add(itemClone)
            }
        } else {
            for (item in ingredientsAggregate) {
                if (item.isNull()) continue
                needInputs.add(item.clone())
            }
        }
        if (!matchItemList(haveInputs, needInputs)) {
            return false
        }
        val haveOutputs: List<Item> = ArrayList()
        for (item in extraOutputList) {
            if (item.isNull()) continue
            haveOutputs.add(item.clone())
        }
        haveOutputs.sort(CraftingManager.recipeComparator)
        val needOutputs: List<Item> = ArrayList()
        if (multiplier != 1) {
            for (item in getExtraResults()) {
                if (item.isNull()) continue
                val itemClone: Item = item.clone()!!
                itemClone.setCount(itemClone.getCount() * multiplier)
                needOutputs.add(itemClone)
            }
        } else {
            for (item in getExtraResults()) {
                if (item.isNull()) continue
                needOutputs.add(item.clone())
            }
        }
        needOutputs.sort(CraftingManager.recipeComparator)
        return matchItemList(haveOutputs, needOutputs)
    }

    /**
     * Returns whether the specified list of crafting grid inputs and outputs matches this recipe. Outputs DO NOT
     * include the primary result item.
     *
     * @param inputList  list of items taken from the crafting grid
     * @param extraOutputList list of items put back into the crafting grid (secondary results)
     * @return bool
     */
    @Override
    fun matchItems(inputList: List<Item>, extraOutputList: List<Item>): Boolean {
        return matchItems(inputList, extraOutputList, 1)
    }

    @Override
    fun getIngredientsAggregate(): List<Item> {
        return ingredientsAggregate
    }

    init {
        output = result.clone()!!
        if (ingredients.size() > 9) {
            throw IllegalArgumentException("Shapeless recipes cannot have more than 9 ingredients")
        }
        this.ingredients = ArrayList()
        ingredientsAggregate = ArrayList()
        for (item in ingredients) {
            if (item.getCount() < 1) {
                throw IllegalArgumentException("Recipe '" + recipeId + "' Ingredient amount was not 1 (value: " + item.getCount() + ")")
            }
            var found = false
            for (existingIngredient in ingredientsAggregate) {
                if (existingIngredient.equals(item, item.hasMeta(), item.hasCompoundTag())) {
                    existingIngredient.setCount(existingIngredient.getCount() + item.getCount())
                    found = true
                    break
                }
            }
            if (!found) ingredientsAggregate.add(item.clone())
            this.ingredients.add(item.clone())
        }
        ingredientsAggregate.sort(CraftingManager.recipeComparator)
    }
}