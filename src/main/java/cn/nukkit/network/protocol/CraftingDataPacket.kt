package cn.nukkit.network.protocol

import cn.nukkit.api.Since

/**
 * @author Nukkit Project Team
 */
@ToString
class CraftingDataPacket : DataPacket() {
    private var entries: List<Recipe> = ArrayList()
    private val brewingEntries: List<BrewingRecipe> = ArrayList()
    private val containerEntries: List<ContainerRecipe> = ArrayList()
    var cleanRecipes = false
    fun addShapelessRecipe(vararg recipe: ShapelessRecipe?) {
        Collections.addAll(entries, recipe)
    }

    @PowerNukkitOnly
    fun addStonecutterRecipe(vararg recipes: StonecutterRecipe?) {
        Collections.addAll(entries, recipes)
    }

    fun addShapedRecipe(vararg recipe: ShapedRecipe?) {
        Collections.addAll(entries, recipe)
    }

    @PowerNukkitOnly
    fun addCartographyRecipe(vararg recipe: CartographyRecipe?) {
        Stream.of(recipe).filter { r -> r.getRecipeId() != null }.forEachOrdered { r -> entries.add(r) }
    }

    fun addFurnaceRecipe(vararg recipe: FurnaceRecipe?) {
        Collections.addAll(entries, recipe)
    }

    @PowerNukkitOnly
    fun addSmokerRecipe(vararg recipe: SmokerRecipe?) {
        Collections.addAll(entries, recipe)
    }

    @PowerNukkitOnly
    fun addBlastFurnaceRecipe(vararg recipe: BlastFurnaceRecipe?) {
        Collections.addAll(entries, recipe)
    }

    @PowerNukkitOnly
    fun addCampfireRecipeRecipe(vararg recipe: CampfireRecipe?) {
        Collections.addAll(entries, recipe)
    }

    @Since("1.4.0.0-PN")
    fun addMultiRecipe(vararg recipe: MultiRecipe?) {
        Collections.addAll(entries, recipe)
    }

    fun addBrewingRecipe(vararg recipe: BrewingRecipe?) {
        Collections.addAll(brewingEntries, recipe)
    }

    fun addContainerRecipe(vararg recipe: ContainerRecipe?) {
        Collections.addAll(containerEntries, recipe)
    }

    @Override
    override fun clean(): DataPacket {
        entries = ArrayList()
        return super.clean()
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putUnsignedVarInt(entries.size())
        var recipeNetworkId = 1
        for (recipe in entries) {
            this.putVarInt(recipe.getType().networkType)
            when (recipe.getType()) {
                STONECUTTER -> {
                    val stonecutter: StonecutterRecipe = recipe as StonecutterRecipe
                    this.putString(stonecutter.getRecipeId())
                    this.putUnsignedVarInt(1)
                    this.putRecipeIngredient(stonecutter.getIngredient())
                    this.putUnsignedVarInt(1)
                    this.putSlot(stonecutter.getResult(), true)
                    this.putUUID(stonecutter.getId())
                    this.putString(CRAFTING_TAG_STONECUTTER)
                    this.putVarInt(stonecutter.getPriority())
                    this.putUnsignedVarInt(recipeNetworkId++)
                }
                SHAPELESS, CARTOGRAPHY -> {
                    val shapeless: ShapelessRecipe = recipe as ShapelessRecipe
                    this.putString(shapeless.getRecipeId())
                    val ingredients: List<Item> = shapeless.getIngredientList()
                    this.putUnsignedVarInt(ingredients.size())
                    for (ingredient in ingredients) {
                        this.putRecipeIngredient(ingredient)
                    }
                    this.putUnsignedVarInt(1)
                    this.putSlot(shapeless.getResult(), true)
                    this.putUUID(shapeless.getId())
                    this.putString(if (recipe.getType() === RecipeType.CARTOGRAPHY) CRAFTING_TAG_CARTOGRAPHY_TABLE else CRAFTING_TAG_CRAFTING_TABLE)
                    this.putVarInt(shapeless.getPriority())
                    this.putUnsignedVarInt(recipeNetworkId++)
                }
                SHAPED -> {
                    val shaped: ShapedRecipe = recipe as ShapedRecipe
                    this.putString(shaped.getRecipeId())
                    this.putVarInt(shaped.getWidth())
                    this.putVarInt(shaped.getHeight())
                    var z = 0
                    while (z < shaped.getHeight()) {
                        var x = 0
                        while (x < shaped.getWidth()) {
                            this.putRecipeIngredient(shaped.getIngredient(x, z))
                            ++x
                        }
                        ++z
                    }
                    val outputs: List<Item> = ArrayList()
                    outputs.add(shaped.getResult())
                    outputs.addAll(shaped.getExtraResults())
                    this.putUnsignedVarInt(outputs.size())
                    for (output in outputs) {
                        this.putSlot(output, true)
                    }
                    this.putUUID(shaped.getId())
                    this.putString(CRAFTING_TAG_CRAFTING_TABLE)
                    this.putVarInt(shaped.getPriority())
                    this.putUnsignedVarInt(recipeNetworkId++)
                }
                FURNACE, FURNACE_DATA, SMOKER, SMOKER_DATA, BLAST_FURNACE, BLAST_FURNACE_DATA, CAMPFIRE, CAMPFIRE_DATA -> {
                    val smelting: SmeltingRecipe = recipe as SmeltingRecipe
                    val input: Item = smelting.getInput()
                    this.putVarInt(input.getId())
                    if (recipe.getType().name().endsWith("_DATA")) {
                        this.putVarInt(input.getDamage())
                    }
                    this.putSlot(smelting.getResult(), true)
                    when (recipe.getType()) {
                        FURNACE, FURNACE_DATA -> this.putString(CRAFTING_TAG_FURNACE)
                        SMOKER, SMOKER_DATA -> this.putString(CRAFTING_TAG_SMOKER)
                        BLAST_FURNACE, BLAST_FURNACE_DATA -> this.putString(CRAFTING_TAG_BLAST_FURNACE)
                        CAMPFIRE, CAMPFIRE_DATA -> this.putString(CRAFTING_TAG_CAMPFIRE)
                    }
                }
                MULTI -> {
                    this.putUUID((recipe as MultiRecipe).getId())
                    this.putUnsignedVarInt(recipeNetworkId++)
                }
            }
        }
        this.putUnsignedVarInt(brewingEntries.size())
        for (recipe in brewingEntries) {
            this.putVarInt(recipe.getInput().getNetworkId())
            this.putVarInt(recipe.getInput().getDamage())
            this.putVarInt(recipe.getIngredient().getNetworkId())
            this.putVarInt(recipe.getIngredient().getDamage())
            this.putVarInt(recipe.getResult().getNetworkId())
            this.putVarInt(recipe.getResult().getDamage())
        }
        this.putUnsignedVarInt(containerEntries.size())
        for (recipe in containerEntries) {
            this.putVarInt(recipe.getInput().getNetworkId())
            this.putVarInt(recipe.getIngredient().getNetworkId())
            this.putVarInt(recipe.getResult().getNetworkId())
        }
        this.putBoolean(cleanRecipes)
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.CRAFTING_DATA_PACKET
        const val CRAFTING_TAG_CRAFTING_TABLE = "crafting_table"
        const val CRAFTING_TAG_CARTOGRAPHY_TABLE = "cartography_table"
        const val CRAFTING_TAG_STONECUTTER = "stonecutter"
        const val CRAFTING_TAG_FURNACE = "furnace"
        const val CRAFTING_TAG_CAMPFIRE = "campfire"
        const val CRAFTING_TAG_BLAST_FURNACE = "blast_furnace"
        const val CRAFTING_TAG_SMOKER = "smoker"
    }
}