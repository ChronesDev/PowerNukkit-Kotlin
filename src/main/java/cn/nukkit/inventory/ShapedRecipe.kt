package cn.nukkit.inventory

import cn.nukkit.item.Item

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ShapedRecipe(@get:Override override var recipeId: String?, @get:Override override val priority: Int, primaryResult: Item, shape: Array<String>, ingredients: Map<Character?, Item?>, extraResults: List<Item?>?) : CraftingRecipe {
    private val primaryResult: Item
    private override val extraResults: List<Item> = ArrayList()
    private override val ingredientsAggregate: List<Item>
    private var least: Long = 0
    private var most: Long = 0
    val shape: Array<String>

    private val ingredients: CharObjectHashMap<Item> = CharObjectHashMap()

    constructor(primaryResult: Item, shape: Array<String>, ingredients: Map<Character?, Item?>, extraResults: List<Item?>?) : this(null, 1, primaryResult, shape, ingredients, extraResults) {}

    val width: Int
        get() = shape[0].length()

    fun getHeight(): Int {
        return shape.size
    }

    @Override
    fun getResult(): Item {
        return primaryResult
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

    fun setIngredient(key: String, item: Item?): ShapedRecipe {
        return this.setIngredient(key.charAt(0), item)
    }

    fun setIngredient(key: Char, item: Item?): ShapedRecipe {
        if (String.join("", shape).indexOf(key) < 0) {
            throw RuntimeException("Symbol does not appear in the shape: $key")
        }
        ingredients.put(key, item)
        return this
    }

    fun getIngredientList(): List<Item> {
        val items: List<Item> = ArrayList()
        var y = 0
        val y2 = getHeight()
        while (y < y2) {
            var x = 0
            val x2 = width
            while (x < x2) {
                items.add(getIngredient(x, y))
                ++x
            }
            ++y
        }
        return items
    }

    fun getIngredientMap(): Map<Integer, Map<Integer, Item>> {
        val ingredients: Map<Integer, Map<Integer, Item>> = LinkedHashMap()
        var y = 0
        val y2 = getHeight()
        while (y < y2) {
            val m: Map<Integer, Item> = LinkedHashMap()
            var x = 0
            val x2 = width
            while (x < x2) {
                m.put(x, getIngredient(x, y))
                ++x
            }
            ingredients.put(y, m)
            ++y
        }
        return ingredients
    }

    fun getIngredient(x: Int, y: Int): Item {
        val item: Item = ingredients.get(shape[y].charAt(x))
        return if (item != null) item.clone()!! else Item.get(Item.AIR)!!
    }

    @Override
    fun registerToCraftingManager(manager: CraftingManager) {
        manager.registerShapedRecipe(this)
    }

    @Override
    fun getType(): RecipeType {
        return RecipeType.SHAPED
    }

    @Override
    fun getExtraResults(): List<Item> {
        return extraResults
    }

    @Override
    fun getAllResults(): List<Item> {
        val list: List<Item> = ArrayList()
        list.add(primaryResult)
        list.addAll(extraResults)
        return list
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
    override fun toString(): String {
        val joiner = StringJoiner(", ")
        ingredients.forEach { character, item -> joiner.add(item.getName().toString() + ":" + item.getDamage()) }
        return joiner.toString()
    }

    @Override
    override fun requiresCraftingTable(): Boolean {
        return getHeight() > 2 || width > 2
    }

    @Override
    fun getIngredientsAggregate(): List<Item> {
        return ingredientsAggregate
    }

    class Entry(val x: Int, val y: Int)

    /**
     * Constructs a ShapedRecipe instance.
     *
     * @param primaryResult    Primary result of the recipe
     * @param shape<br></br>        Array of 1, 2, or 3 strings representing the rows of the recipe.
     * This accepts an array of 1, 2 or 3 strings. Each string should be of the same length and must be at most 3
     * characters long. Each character represents a unique type of ingredient. Spaces are interpreted as air.
     * @param ingredients<br></br>  Char =&gt; Item map of items to be set into the shape.
     * This accepts an array of Items, indexed by character. Every unique character (except space) in the shape
     * array MUST have a corresponding item in this list. Space character is automatically treated as air.
     * @param extraResults<br></br> List of additional result items to leave in the crafting grid afterwards. Used for things like cake recipe
     * empty buckets.
     *
     *
     * Note: Recipes **do not** need to be square. Do NOT add padding for empty rows/columns.
     */
    init {
        val rowCount = shape.size
        if (rowCount > 3 || rowCount <= 0) {
            throw RuntimeException("Shaped recipes may only have 1, 2 or 3 rows, not $rowCount")
        }
        val columnCount: Int = shape[0].length()
        if (columnCount > 3 || columnCount <= 0) {
            throw RuntimeException("Shaped recipes may only have 1, 2 or 3 columns, not $columnCount")
        }
        var i = 0
        val shapeLength = shape.size
        while (i < shapeLength) {
            val row = shape[i]
            if (row.length() !== columnCount) {
                throw RuntimeException("Shaped recipe rows must all have the same length (expected " + columnCount + ", got " + row.length() + ")")
            }
            for (x in 0 until columnCount) {
                val c: Char = row.charAt(x)
                if (c != ' ' && !ingredients.containsKey(c)) {
                    throw RuntimeException("No item specified for symbol '$c'")
                }
            }
            shape[i] = row.intern()
            i++
        }
        this.primaryResult = primaryResult.clone()!!
        this.extraResults.addAll(extraResults)
        this.shape = shape
        for (entry in ingredients.entrySet()) {
            this.setIngredient(entry.getKey(), entry.getValue())
        }
        ingredientsAggregate = ArrayList()
        for (c in String.join("", this.shape).toCharArray()) {
            if (c == ' ') continue
            var ingredient: Item? = this.ingredients.get(c).clone()
            for (existingIngredient in ingredientsAggregate) {
                if (existingIngredient.equals(ingredient!!, ingredient!!.hasMeta(), ingredient!!.hasCompoundTag())) {
                    existingIngredient.setCount(existingIngredient.getCount() + ingredient!!.getCount())
                    ingredient = null
                    break
                }
            }
            if (ingredient != null) ingredientsAggregate.add(ingredient)
        }
        ingredientsAggregate.sort(CraftingManager.recipeComparator)
    }
}