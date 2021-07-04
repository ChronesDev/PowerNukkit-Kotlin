package cn.nukkit.inventory

import cn.nukkit.Server

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
class CraftingManager {
    val recipes: Collection<Recipe> = ArrayDeque()
    protected val shapedRecipes: Map<Integer, Map<UUID, ShapedRecipe>> = Int2ObjectOpenHashMap()
    val furnaceRecipes: Map<Integer, FurnaceRecipe> = Int2ObjectOpenHashMap()
    val blastFurnaceRecipes: Map<Integer, BlastFurnaceRecipe> = Int2ObjectOpenHashMap()
    val smokerRecipes: Map<Integer, SmokerRecipe> = Int2ObjectOpenHashMap()
    val campfireRecipes: Map<Integer, CampfireRecipe> = Int2ObjectOpenHashMap()

    @Since("1.4.0.0-PN")
    val multiRecipes: Map<UUID, MultiRecipe> = HashMap()
    val brewingRecipes: Map<Integer, BrewingRecipe> = Int2ObjectOpenHashMap()
    val containerRecipes: Map<Integer, ContainerRecipe> = Int2ObjectOpenHashMap()
    val stonecutterRecipes: Map<Integer, StonecutterRecipe> = Int2ObjectOpenHashMap()
    protected val shapelessRecipes: Map<Integer, Map<UUID, ShapelessRecipe>> = Int2ObjectOpenHashMap()
    protected val cartographyRecipes: Map<Integer, Map<UUID, CartographyRecipe>> = Int2ObjectOpenHashMap()
    private val smithingRecipes: Int2ObjectOpenHashMap<Map<UUID, SmithingRecipe>> = Int2ObjectOpenHashMap()
    private fun registerSmithingRecipes() {
        val ingot: Item = Item.get(ItemID.NETHERITE_INGOT)
        val ids: Int2IntMap = Int2IntOpenHashMap()
        ids.put(ItemID.DIAMOND_HELMET, ItemID.NETHERITE_HELMET)
        ids.put(ItemID.DIAMOND_CHESTPLATE, ItemID.NETHERITE_CHESTPLATE)
        ids.put(ItemID.DIAMOND_LEGGINGS, ItemID.NETHERITE_LEGGINGS)
        ids.put(ItemID.DIAMOND_BOOTS, ItemID.NETHERITE_BOOTS)
        ids.put(ItemID.DIAMOND_SWORD, ItemID.NETHERITE_SWORD)
        ids.put(ItemID.DIAMOND_PICKAXE, ItemID.NETHERITE_PICKAXE)
        ids.put(ItemID.DIAMOND_HOE, ItemID.NETHERITE_HOE)
        ids.put(ItemID.DIAMOND_SHOVEL, ItemID.NETHERITE_SHOVEL)
        ids.put(ItemID.DIAMOND_AXE, ItemID.NETHERITE_AXE)
        ids.int2IntEntrySet().forEach { e ->
            SmithingRecipe(
                    Item.get(e.getIntKey()).createFuzzyCraftingRecipe(),
                    ingot,
                    Item.get(e.getIntValue())
            ).registerToCraftingManager(this)
        }
    }

    @SuppressWarnings("unchecked")
    private fun loadRecipes(config: Config) {
        val recipes: List<Map> = config.getMapList("recipes")
        log.info("Loading recipes...")
        toNextRecipe@ for (recipe in recipes) {
            try {
                when (Utils.toInt(recipe["type"])) {
                    0 -> {
                        val craftingBlock = recipe["block"] as String
                        if (!"crafting_table".equals(craftingBlock) && !"stonecutter".equals(craftingBlock) && !"cartography_table".equalsIgnoreCase(craftingBlock)) {
                            // Ignore other recipes than crafting table, stonecutter and cartography table
                            continue
                        }
                        // TODO: handle multiple result items
                        val outputs: List<Map> = recipe["output"]
                        if (outputs.size() > 1) {
                            continue
                        }
                        val first: Map<String, Object> = outputs[0]
                        val sorted: List<Item> = ArrayList()
                        for (ingredient in (recipe["input"] as List<Map>)) {
                            val recipeItem: Item = parseRecipeItem(ingredient)
                            if (recipeItem.isNull()) {
                                continue@toNextRecipe
                            }
                            sorted.add(recipeItem)
                        }
                        // Bake sorted list
                        sorted.sort(recipeComparator)
                        val recipeId = recipe["id"] as String
                        val priority: Int = Utils.toInt(recipe["priority"])
                        val result: Item = parseRecipeItem(first)
                        if (result.isNull()) {
                            continue@toNextRecipe
                        }
                        when (craftingBlock) {
                            "crafting_table" -> registerRecipe(ShapelessRecipe(recipeId, priority, result, sorted))
                            "stonecutter" -> registerRecipe(StonecutterRecipe(recipeId, priority, result, sorted[0]))
                            "cartography_table" -> registerRecipe(CartographyRecipe(recipeId, priority, result, sorted))
                        }
                    }
                    1 -> {
                        craftingBlock = recipe["block"] as String
                        if (!"crafting_table".equals(craftingBlock)) {
                            // Ignore other recipes than crafting table ones
                            continue
                        }
                        outputs = recipe["output"] as List<Map?>
                        first = outputs.remove(0)
                        val shape: Array<String> = (recipe["shape"] as List<String?>).toArray(EmptyArrays.EMPTY_STRINGS)
                        val ingredients: Map<Character?, Item> = CharObjectHashMap()
                        val extraResults: List<Item> = ArrayList()
                        for (ingredientEntry in recipe["input"].entrySet()) {
                            val ingredientChar: Char = ingredientEntry.getKey().charAt(0)
                            val ingredient: Item = parseRecipeItem(ingredientEntry.getValue())
                            if (ingredient.isNull()) {
                                continue@toNextRecipe
                            }
                            ingredients.put(ingredientChar, ingredient)
                        }
                        for (data in outputs) {
                            val output: Item = parseRecipeItem(data)
                            if (output.isNull()) {
                                continue@toNextRecipe
                            }
                            extraResults.add(output)
                        }
                        recipeId = recipe["id"] as String
                        priority = Utils.toInt(recipe["priority"])
                        val primaryResult: Item = parseRecipeItem(first)
                        if (primaryResult.isNull()) {
                            continue@toNextRecipe
                        }
                        registerRecipe(ShapedRecipe(recipeId, priority, primaryResult, shape, ingredients, extraResults))
                    }
                    2, 3 -> {
                        craftingBlock = recipe["block"] as String
                        if (!"furnace".equals(craftingBlock) && !"blast_furnace".equals(craftingBlock)
                                && !"smoker".equals(craftingBlock) && !"campfire".equals(craftingBlock)) {
                            // Ignore other recipes than furnaces, blast furnaces, smokers and campfire
                            continue
                        }
                        val resultItem: Item = parseRecipeItem(recipe["output"])
                        if (resultItem.isNull()) {
                            continue@toNextRecipe
                        }
                        var inputItem: Item
                        inputItem = try {
                            parseRecipeItem(recipe["input"])
                        } catch (old: Exception) {
                            Item.get(Utils.toInt(recipe["inputId"]), if (recipe.containsKey("inputDamage")) Utils.toInt(recipe["inputDamage"]) else -1, 1)
                        }
                        if (inputItem.isNull()) {
                            continue@toNextRecipe
                        }
                        when (craftingBlock) {
                            "furnace" -> registerRecipe(FurnaceRecipe(resultItem, inputItem))
                            "blast_furnace" -> registerRecipe(BlastFurnaceRecipe(resultItem, inputItem))
                            "smoker" -> registerRecipe(SmokerRecipe(resultItem, inputItem))
                            "campfire" -> registerRecipe(CampfireRecipe(resultItem, inputItem))
                        }
                    }
                    4 -> registerRecipe(MultiRecipe(UUID.fromString(recipe["uuid"] as String)))
                    else -> {
                    }
                }
            } catch (e: Exception) {
                log.error("Exception during registering recipe", e)
            }
        }

        // Load brewing recipes
        val potionMixes: List<Map> = config.getMapList("potionMixes")
        val runtimeMapping: RuntimeItemMapping = RuntimeItems.getRuntimeMapping()
        for (potionMix in potionMixes) {
            val fromPotionId: String = potionMix.get("inputId").toString()
            val fromPotionMeta: Int = (potionMix.get("inputMeta") as Number).intValue()
            val ingredient: String = potionMix.get("reagentId").toString()
            val ingredientMeta: Int = (potionMix.get("reagentMeta") as Number).intValue()
            val toPotionId: String = potionMix.get("outputId").toString()
            val toPotionMeta: Int = (potionMix.get("outputMeta") as Number).intValue()
            registerBrewingRecipe(BrewingRecipe(
                    Item.fromString("$fromPotionId:$fromPotionMeta"),
                    Item.fromString("$ingredient:$ingredientMeta"),
                    Item.fromString("$toPotionId:$toPotionMeta")
            ))
        }
        val containerMixes: List<Map> = config.getMapList("containerMixes")
        for (containerMix in containerMixes) {
            val fromItemId: String = containerMix.get("inputId").toString()
            val ingredient: String = containerMix.get("reagentId").toString()
            val toItemId: String = containerMix.get("outputId").toString()
            registerContainerRecipe(ContainerRecipe(Item.fromString(fromItemId), Item.fromString(ingredient), Item.fromString(toItemId)))
        }

        // Allow to rename without crafting 
        registerCartographyRecipe(CartographyRecipe(Item.get(ItemID.EMPTY_MAP), Collections.singletonList(Item.get(ItemID.EMPTY_MAP))))
        registerCartographyRecipe(CartographyRecipe(Item.get(ItemID.EMPTY_MAP, 2), Collections.singletonList(Item.get(ItemID.EMPTY_MAP, 2))))
        registerCartographyRecipe(CartographyRecipe(Item.get(ItemID.MAP), Collections.singletonList(Item.get(ItemID.MAP))))
        registerCartographyRecipe(CartographyRecipe(Item.get(ItemID.MAP, 3), Collections.singletonList(Item.get(ItemID.MAP, 3))))
        registerCartographyRecipe(CartographyRecipe(Item.get(ItemID.MAP, 4), Collections.singletonList(Item.get(ItemID.MAP, 4))))
        registerCartographyRecipe(CartographyRecipe(Item.get(ItemID.MAP, 5), Collections.singletonList(Item.get(ItemID.MAP, 5))))
    }

    private fun parseRecipeItem(data: Map<String, Object>?): Item {
        val nbt = data!!["nbt_b64"] as String?
        val nbtBytes: ByteArray = if (nbt != null) Base64.getDecoder().decode(nbt) else EmptyArrays.EMPTY_BYTES
        val count = if (data.containsKey("count")) (data["count"] as Number?).intValue() else 1
        var legacyId: Integer? = null
        if (data.containsKey("legacyId")) {
            legacyId = Utils.toInt(data["legacyId"])
        }
        var item: Item
        if (data.containsKey("blockRuntimeId")) {
            val blockRuntimeId: Int = Utils.toInt(data["blockRuntimeId"])
            try {
                val state: BlockState = BlockStateRegistry.getBlockStateByRuntimeId(blockRuntimeId)
                if (state == null || state.equals(BlockState.AIR)) {
                    return Item.getBlock(BlockID.AIR)
                }
                if (state.getProperties().equals(BlockUnknown.PROPERTIES)) {
                    return Item.getBlock(BlockID.AIR)
                }
                item = state.asItemBlock(count)
                item.setCompoundTag(nbtBytes)
                return item
            } catch (e: BlockPropertyNotFoundException) {
                log.debug("Failed to load the block runtime id {}", blockRuntimeId, e)
            }
        }
        if (legacyId != null && legacyId > 255) {
            try {
                val fullId: Int = RuntimeItems.getRuntimeMapping().getLegacyFullId(legacyId)
                val itemId: Int = RuntimeItems.getId(fullId)
                var meta: Integer? = null
                if (RuntimeItems.hasData(fullId)) {
                    meta = RuntimeItems.getData(fullId)
                }
                var fuzzy = false
                if (data.containsKey("damage")) {
                    val damage: Int = Utils.toInt(data["damage"])
                    if (damage == Short.MAX_VALUE) {
                        fuzzy = true
                    } else if (meta == null) {
                        meta = damage
                    }
                }
                item = Item.get(itemId, if (meta == null) 0 else meta, count)
                if (fuzzy) {
                    item = item.createFuzzyCraftingRecipe()
                }
                item.setCompoundTag(nbtBytes)
                return item
            } catch (e: IllegalArgumentException) {
                log.debug("Failed to load a crafting recipe item, attempting to load by string id", e)
            }
        }
        val id: String = data["id"].toString()
        item = if (data.containsKey("damage")) {
            val meta: Int = Utils.toInt(data["damage"])
            if (meta == Short.MAX_VALUE) {
                Item.fromString(id).createFuzzyCraftingRecipe()
            } else {
                Item.fromString("$id:$meta")
            }
        } else {
            Item.fromString(id)
        }
        item.setCount(count)
        item.setCompoundTag(nbtBytes)
        return item
    }

    fun rebuildPacket() {
        val pk = CraftingDataPacket()
        pk.cleanRecipes = true
        for (recipe in getRecipes()) {
            if (recipe is ShapedRecipe) {
                pk.addShapedRecipe(recipe)
            } else if (recipe is ShapelessRecipe) {
                pk.addShapelessRecipe(recipe)
            }
        }
        for (map in cartographyRecipes.values()) {
            for (recipe in map.values()) {
                pk.addCartographyRecipe(recipe)
            }
        }
        for (recipe in getFurnaceRecipes().values()) {
            pk.addFurnaceRecipe(recipe)
        }
        for (recipe in multiRecipes.values()) {
            pk.addMultiRecipe(recipe)
        }
        for (recipe in smokerRecipes.values()) {
            pk.addSmokerRecipe(recipe)
        }
        for (recipe in blastFurnaceRecipes.values()) {
            pk.addBlastFurnaceRecipe(recipe)
        }
        for (recipe in campfireRecipes.values()) {
            pk.addCampfireRecipeRecipe(recipe)
        }
        for (recipe in brewingRecipes.values()) {
            pk.addBrewingRecipe(recipe)
        }
        for (recipe in containerRecipes.values()) {
            pk.addContainerRecipe(recipe)
        }
        for (recipe in stonecutterRecipes.values()) {
            pk.addStonecutterRecipe(recipe)
        }
        pk.tryEncode()
        // TODO: find out whats wrong with compression
        packet = pk.compress(Deflater.BEST_COMPRESSION)
    }

    fun getRecipes(): Collection<Recipe> {
        return recipes
    }

    fun getFurnaceRecipes(): Map<Integer, FurnaceRecipe> {
        return furnaceRecipes
    }

    fun matchFurnaceRecipe(input: Item): FurnaceRecipe? {
        var recipe: FurnaceRecipe? = furnaceRecipes[getItemHash(input)]
        if (recipe == null) recipe = furnaceRecipes[getItemHash(input.getId(), 0)]
        return recipe
    }

    fun matchCampfireRecipe(input: Item): CampfireRecipe? {
        var recipe: CampfireRecipe? = campfireRecipes[getItemHash(input)]
        if (recipe == null) recipe = campfireRecipes[getItemHash(input.getId(), 0)]
        return recipe
    }

    fun matchBlastFurnaceRecipe(input: Item): BlastFurnaceRecipe? {
        var recipe: BlastFurnaceRecipe? = blastFurnaceRecipes[getItemHash(input)]
        if (recipe == null) recipe = blastFurnaceRecipes[getItemHash(input.getId(), 0)]
        return recipe
    }

    fun matchSmokerRecipe(input: Item): SmokerRecipe? {
        var recipe: SmokerRecipe? = smokerRecipes[getItemHash(input)]
        if (recipe == null) recipe = smokerRecipes[getItemHash(input.getId(), 0)]
        return recipe
    }

    fun registerStonecutterRecipe(recipe: StonecutterRecipe) {
        stonecutterRecipes.put(getItemHash(recipe.getResult()), recipe)
    }

    fun registerFurnaceRecipe(recipe: FurnaceRecipe) {
        val input: Item = recipe.getInput()
        furnaceRecipes.put(getItemHash(input), recipe)
    }

    fun registerBlastFurnaceRecipe(recipe: BlastFurnaceRecipe) {
        val input: Item = recipe.getInput()
        blastFurnaceRecipes.put(getItemHash(input), recipe)
    }

    fun registerSmokerRecipe(recipe: SmokerRecipe) {
        val input: Item = recipe.getInput()
        smokerRecipes.put(getItemHash(input), recipe)
    }

    fun registerCampfireRecipe(recipe: CampfireRecipe) {
        val input: Item = recipe.getInput()
        campfireRecipes.put(getItemHash(input), recipe)
    }

    fun registerShapedRecipe(recipe: ShapedRecipe) {
        val resultHash = getItemHash(recipe.getResult())
        val map: Map<UUID, ShapedRecipe> = shapedRecipes.computeIfAbsent(resultHash) { k -> HashMap() }
        val inputList: List<Item> = LinkedList(recipe.getIngredientsAggregate())
        map.put(getMultiItemHash(inputList), recipe)
    }

    fun registerRecipe(recipe: Recipe) {
        var id: UUID? = null
        if (recipe is CraftingRecipe || recipe is StonecutterRecipe) {
            id = Utils.dataToUUID(String.valueOf(++RECIPE_COUNT), String.valueOf(recipe.getResult().getId()), String.valueOf(recipe.getResult().getDamage()), String.valueOf(recipe.getResult().getCount()), Arrays.toString(recipe.getResult().getCompoundTag()))
        }
        if (recipe is CraftingRecipe) {
            recipe.setId(id)
            recipes.add(recipe)
        } else if (recipe is StonecutterRecipe) {
            recipe.setId(id)
        }
        recipe.registerToCraftingManager(this)
    }

    fun registerCartographyRecipe(recipe: CartographyRecipe) {
        val list: List<Item> = recipe.getIngredientList()
        list.sort(recipeComparator)
        val hash: UUID = getMultiItemHash(list)
        val resultHash = getItemHash(recipe.getResult())
        val map: Map<UUID, CartographyRecipe> = cartographyRecipes.computeIfAbsent(resultHash) { k -> HashMap() }
        map.put(hash, recipe)
    }

    fun registerShapelessRecipe(recipe: ShapelessRecipe) {
        val list: List<Item> = recipe.getIngredientsAggregate()
        val hash: UUID = getMultiItemHash(list)
        val resultHash = getItemHash(recipe.getResult())
        val map: Map<UUID, ShapelessRecipe> = shapelessRecipes.computeIfAbsent(resultHash) { k -> HashMap() }
        map.put(hash, recipe)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun registerSmithingRecipe(@Nonnull recipe: SmithingRecipe) {
        val list: List<Item> = recipe.getIngredientsAggregate()
        val hash: UUID = getMultiItemHash(list)
        val resultHash = getItemHash(recipe.getResult())
        val map: Map<UUID, SmithingRecipe> = smithingRecipes.computeIfAbsent(resultHash) { k -> HashMap() }
        map.put(hash, recipe)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    fun matchSmithingRecipe(equipment: Item, ingredient: Item): SmithingRecipe {
        val inputList: List<Item> = ArrayList(2)
        inputList.add(equipment.decrement(equipment.count - 1))
        inputList.add(ingredient.decrement(ingredient.count - 1))
        return matchSmithingRecipe(inputList)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    fun matchSmithingRecipe(@Nonnull inputList: List<Item>): SmithingRecipe {
        inputList.sort(recipeComparator)
        val inputHash: UUID = getMultiItemHash(inputList)
        return smithingRecipes.values().stream().flatMap { map -> map.entrySet().stream() }
                .filter { entry -> entry.getKey().equals(inputHash) }
                .map(Map.Entry::getValue)
                .findFirst().orElseGet {
                    smithingRecipes.values().stream().flatMap { map -> map.values().stream() }
                            .filter { recipe -> recipe.matchItems(inputList) }
                            .findFirst().orElse(null)
                }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    fun matchSmithingRecipe(@Nonnull equipment: Item?, @Nonnull ingredient: Item?, @Nonnull primaryOutput: Item?): SmithingRecipe {
        val inputList: List<Item> = ArrayList(2)
        inputList.add(equipment)
        inputList.add(ingredient)
        return matchSmithingRecipe(inputList, primaryOutput)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun matchSmithingRecipe(@Nonnull inputList: List<Item>, @Nonnull primaryOutput: Item): SmithingRecipe? {
        val outputHash = getItemHash(primaryOutput)
        if (!smithingRecipes.containsKey(outputHash)) {
            return null
        }
        inputList.sort(recipeComparator)
        val inputHash: UUID = getMultiItemHash(inputList)
        val recipeMap: Map<UUID, SmithingRecipe> = smithingRecipes.get(outputHash)
        if (recipeMap != null) {
            val recipe: SmithingRecipe? = recipeMap[inputHash]
            if (recipe != null && (recipe.matchItems(inputList) || matchItemsAccumulation(recipe, inputList, primaryOutput))) {
                return recipe
            }
            for (smithingRecipe in recipeMap.values()) {
                if (smithingRecipe.matchItems(inputList) || matchItemsAccumulation(smithingRecipe, inputList, primaryOutput)) {
                    return smithingRecipe
                }
            }
        }
        return null
    }

    fun registerBrewingRecipe(recipe: BrewingRecipe) {
        val input: Item = recipe.getIngredient()
        val potion: Item = recipe.getInput()
        val potionHash = getPotionHash(input, potion)
        if (brewingRecipes.containsKey(potionHash)) {
            log.warn("The brewing recipe {} is being replaced by {}", brewingRecipes[potionHash], recipe)
        }
        brewingRecipes.put(potionHash, recipe)
    }

    fun registerContainerRecipe(recipe: ContainerRecipe) {
        val input: Item = recipe.getIngredient()
        val potion: Item = recipe.getInput()
        containerRecipes.put(getContainerHash(input.getId(), potion.getId()), recipe)
    }

    fun matchBrewingRecipe(input: Item, potion: Item): BrewingRecipe? {
        return brewingRecipes[getPotionHash(input, potion)]
    }

    fun matchContainerRecipe(input: Item, potion: Item): ContainerRecipe? {
        return containerRecipes[getContainerHash(input.getId(), potion.getId())]
    }

    fun matchStonecutterRecipe(output: Item): StonecutterRecipe? {
        return stonecutterRecipes[getItemHash(output)]
    }

    fun matchCartographyRecipe(inputList: List<Item>, primaryOutput: Item, extraOutputList: List<Item>): CartographyRecipe? {
        val outputHash = getItemHash(primaryOutput)
        if (cartographyRecipes.containsKey(outputHash)) {
            inputList.sort(recipeComparator)
            val inputHash: UUID = getMultiItemHash(inputList)
            val recipes: Map<Any, Any> = cartographyRecipes[outputHash] ?: return null
            val recipe: CartographyRecipe? = recipes[inputHash]
            if (recipe != null && recipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(recipe, inputList, primaryOutput, extraOutputList)) {
                return recipe
            }
            for (cartographyRecipe in recipes.values()) {
                if (cartographyRecipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(cartographyRecipe, inputList, primaryOutput, extraOutputList)) {
                    return cartographyRecipe
                }
            }
        }
        return null
    }

    fun matchRecipe(inputList: List<Item>, primaryOutput: Item, extraOutputList: List<Item>): CraftingRecipe? {
        //TODO: try to match special recipes before anything else (first they need to be implemented!)
        val outputHash = getItemHash(primaryOutput)
        if (shapedRecipes.containsKey(outputHash)) {
            inputList.sort(recipeComparator)
            val inputHash: UUID = getMultiItemHash(inputList)
            val recipeMap: Map<UUID, ShapedRecipe>? = shapedRecipes[outputHash]
            if (recipeMap != null) {
                val recipe: ShapedRecipe? = recipeMap[inputHash]
                if (recipe != null && (recipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(recipe, inputList, primaryOutput, extraOutputList))) {
                    return recipe
                }
                for (shapedRecipe in recipeMap.values()) {
                    if (shapedRecipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(shapedRecipe, inputList, primaryOutput, extraOutputList)) {
                        return shapedRecipe
                    }
                }
            }
        }
        if (shapelessRecipes.containsKey(outputHash)) {
            inputList.sort(recipeComparator)
            val inputHash: UUID = getMultiItemHash(inputList)
            val recipes: Map<Any, Any> = shapelessRecipes[outputHash] ?: return null
            val recipe: ShapelessRecipe? = recipes[inputHash]
            if (recipe != null && (recipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(recipe, inputList, primaryOutput, extraOutputList))) {
                return recipe
            }
            for (shapelessRecipe in recipes.values()) {
                if (shapelessRecipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(shapelessRecipe, inputList, primaryOutput, extraOutputList)) {
                    return shapelessRecipe
                }
            }
        }
        return null
    }

    private fun matchItemsAccumulation(recipe: SmithingRecipe, inputList: List<Item>, primaryOutput: Item): Boolean {
        val recipeResult: Item = recipe.getResult()
        if (primaryOutput.equals(recipeResult, recipeResult.hasMeta(), recipeResult.hasCompoundTag()) && primaryOutput.getCount() % recipeResult.getCount() === 0) {
            val multiplier: Int = primaryOutput.getCount() / recipeResult.getCount()
            return recipe.matchItems(inputList, multiplier)
        }
        return false
    }

    private fun matchItemsAccumulation(recipe: CraftingRecipe?, inputList: List<Item>, primaryOutput: Item, extraOutputList: List<Item>): Boolean {
        val recipeResult: Item = recipe.getResult()
        if (primaryOutput.equals(recipeResult, recipeResult.hasMeta(), recipeResult.hasCompoundTag()) && primaryOutput.getCount() % recipeResult.getCount() === 0) {
            val multiplier: Int = primaryOutput.getCount() / recipeResult.getCount()
            return recipe!!.matchItems(inputList, extraOutputList, multiplier)
        }
        return false
    }

    @Since("1.4.0.0-PN")
    fun registerMultiRecipe(recipe: MultiRecipe) {
        multiRecipes.put(recipe.getId(), recipe)
    }

    class Entry(val resultItemId: Int, val resultMeta: Int, val ingredientItemId: Int, val ingredientMeta: Int, val recipeShape: String, val resultAmount: Int)
    companion object {
        @Since("1.5.0.0-PN")
        var packet: DataPacket? = null
        private var RECIPE_COUNT = 0
        val recipeComparator: Comparator<Item> = label@ Comparator<Item> { i1, i2 ->
            if (i1.getId() > i2.getId()) {
                return@label 1
            } else if (i1.getId() < i2.getId()) {
                return@label -1
            } else if (i1.getDamage() > i2.getDamage()) {
                return@label 1
            } else if (i1.getDamage() < i2.getDamage()) {
                return@label -1
            } else return@label Integer.compare(i1.getCount(), i2.getCount())
        }

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun getCraftingPacket(): DataPacket? {
            return packet
        }

        private fun getMultiItemHash(items: Collection<Item>): UUID {
            val stream = BinaryStream()
            for (item in items) {
                stream.putVarInt(getFullItemHash(item))
            }
            return UUID.nameUUIDFromBytes(stream.getBuffer())
        }

        private fun getFullItemHash(item: Item): Int {
            return 31 * getItemHash(item) + item.getCount()
        }

        private fun getItemHash(item: Item): Int {
            return getItemHash(item.getId(), item.getDamage())
        }

        private fun getItemHash(id: Int, meta: Int): Int {
            return id shl 8 or meta and 0xFF
        }

        private fun getPotionHash(ingredient: Item, potion: Item): Int {
            val ingredientHash: Int = ingredient.getId() and 0x3FF shl 6 or (ingredient.getDamage() and 0x3F)
            val potionHash: Int = potion.getId() and 0x3FF shl 6 or (potion.getDamage() and 0x3F)
            return ingredientHash shl 16 or potionHash
        }

        private fun getContainerHash(ingredientId: Int, containerId: Int): Int {
            return ingredientId shl 9 or containerId
        }
    }

    init {
        val recipesStream: InputStream = Server::class.java.getClassLoader().getResourceAsStream("recipes.json")
                ?: throw AssertionError("Unable to find recipes.json")
        registerSmithingRecipes()
        val recipesConfig = Config(Config.JSON)
        recipesConfig.load(recipesStream)
        loadRecipes(recipesConfig)
        val path: String = Server.getInstance().getDataPath().toString() + "custom_recipes.json"
        val filePath = File(path)
        if (filePath.exists()) {
            val customRecipes = Config(filePath, Config.JSON)
            loadRecipes(customRecipes)
        }
        rebuildPacket()
        log.info("Loaded {} recipes.", recipes.size())
    }
}