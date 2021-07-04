package cn.nukkit.inventory

import cn.nukkit.item.Item

class RepairRecipe(inventoryType: InventoryType, result: Item, ingredients: Collection<Item>) : Recipe {
    private override val result: Item
    private val ingredients: List<Item>
    private val inventoryType: InventoryType
    @Override
    fun getResult(): Item {
        return result.clone()!!
    }

    val ingredientList: List<cn.nukkit.item.Item>
        get() {
            val ingredients: List<Item> = ArrayList()
            for (ingredient in this.ingredients) {
                ingredients.add(ingredient.clone())
            }
            return ingredients
        }

    @Override
    override fun registerToCraftingManager(manager: CraftingManager?) {
    }

    @get:Override
    override val type: cn.nukkit.inventory.RecipeType?
        get() = RecipeType.REPAIR

    fun getInventoryType(): InventoryType {
        return inventoryType
    }

    init {
        this.inventoryType = inventoryType
        this.result = result.clone()!!
        this.ingredients = ArrayList()
        for (item in ingredients) {
            if (item.getCount() < 1) {
                throw IllegalArgumentException("Recipe Ingredient amount was not 1 (value: " + item.getCount().toString() + ")")
            }
            this.ingredients.add(item.clone())
        }
    }
}