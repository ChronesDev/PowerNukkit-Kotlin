package cn.nukkit.blockentity

import cn.nukkit.Player

class BlockEntityBrewingStand(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt), InventoryHolder, BlockEntityContainer, BlockEntityNameable {
    protected var inventory: BrewingInventory? = null
    var brewTime = 0
    var fuelTotal = 0
    var fuel = 0
    @Override
    protected override fun initBlockEntity() {
        inventory = BrewingInventory(this)
        if (!namedTag.contains("Items") || namedTag.get("Items") !is ListTag) {
            namedTag.putList(ListTag("Items"))
        }
        for (i in 0 until size) {
            inventory.setItem(i, getItem(i))
        }
        if (!namedTag.contains("CookTime") || namedTag.getShort("CookTime") > MAX_BREW_TIME) {
            brewTime = MAX_BREW_TIME
        } else {
            brewTime = namedTag.getShort("CookTime")
        }
        fuel = namedTag.getShort("FuelAmount")
        fuelTotal = namedTag.getShort("FuelTotal")
        if (brewTime < MAX_BREW_TIME) {
            this.scheduleUpdate()
        }
        super.initBlockEntity()
    }

    @get:Override
    @set:Override
    override var name: String
        get() = if (hasName()) this.namedTag.getString("CustomName") else "Brewing Stand"
        set(name) {
            if (name == null || name.equals("")) {
                namedTag.remove("CustomName")
                return
            }
            namedTag.putString("CustomName", name)
        }

    @Override
    override fun hasName(): Boolean {
        return namedTag.contains("CustomName")
    }

    @Override
    override fun close() {
        if (!closed) {
            for (player in HashSet(getInventory().getViewers())) {
                player.removeWindow(getInventory())
            }
            super.close()
        }
    }

    @Override
    override fun onBreak() {
        for (content in inventory.getContents().values()) {
            level.dropItem(this, content)
        }
        inventory.clearAll()
    }

    @Override
    override fun saveNBT() {
        namedTag.putList(ListTag("Items"))
        for (index in 0 until size) {
            setItem(index, inventory.getItem(index))
        }
        namedTag.putShort("CookTime", brewTime)
        namedTag.putShort("FuelAmount", fuel)
        namedTag.putShort("FuelTotal", fuelTotal)
    }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() = getBlock().getId() === BlockID.BREWING_STAND_BLOCK

    @get:Override
    override val size: Int
        get() = 5

    protected fun getSlotIndex(index: Int): Int {
        val list: ListTag<CompoundTag> = this.namedTag.getList("Items", CompoundTag::class.java)
        for (i in 0 until list.size()) {
            if (list.get(i).getByte("Slot") === index) {
                return i
            }
        }
        return -1
    }

    @Override
    override fun getItem(index: Int): Item {
        val i = getSlotIndex(index)
        return if (i < 0) {
            ItemBlock(Block.get(BlockID.AIR), 0, 0)
        } else {
            val data: CompoundTag = this.namedTag.getList("Items").get(i) as CompoundTag
            NBTIO.getItemHelper(data)
        }
    }

    @Override
    override fun setItem(index: Int, item: Item) {
        val i = getSlotIndex(index)
        val d: CompoundTag = NBTIO.putItemHelper(item, index)
        if (item.getId() === BlockID.AIR || item.getCount() <= 0) {
            if (i >= 0) {
                this.namedTag.getList("Items").getAll().remove(i)
            }
        } else if (i < 0) {
            this.namedTag.getList("Items", CompoundTag::class.java).add(d)
        } else {
            this.namedTag.getList("Items", CompoundTag::class.java).add(i, d)
        }
    }

    @Override
    fun getInventory(): BrewingInventory? {
        return inventory
    }

    @Deprecated
    @DeprecationDetails(since = "1.3.1.2-PN", reason = "Checks the wrong location")
    protected fun checkIngredient(ingredient: Item): Boolean {
        return ingredients.contains(ingredient.getId())
    }

    @PowerNukkitDifference(info = "Fixed a lot of issues involving the brewing processes", since = "1.3.1.2-PN")
    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    override fun onUpdate(): Boolean {
        if (closed) {
            return false
        }
        restockFuel()
        if (fuel <= 0 || matchRecipes(true)[0] == null) {
            stopBrewing()
            return false
        }
        if (brewTime == MAX_BREW_TIME) {
            val e = StartBrewEvent(this)
            this.server.getPluginManager().callEvent(e)
            if (e.isCancelled()) {
                return false
            }
            sendBrewTime()
        }
        if (--brewTime > 0) {
            if (brewTime % 40 == 0) {
                sendBrewTime()
            }
            return true
        }

        //20 seconds
        val e = BrewEvent(this)
        this.server.getPluginManager().callEvent(e)
        if (e.isCancelled()) {
            stopBrewing()
            return true
        }
        var mixed = false
        val recipes: Array<MixRecipe?> = matchRecipes(false)
        for (i in 0..2) {
            val recipe: MixRecipe = recipes[i] ?: continue
            val previous: Item = inventory.getItem(i + 1)
            if (!previous.isNull()) {
                val result: Item = recipe.getResult()
                result.setCount(previous.getCount())
                if (recipe is ContainerRecipe) {
                    result.setDamage(previous.getDamage())
                }
                inventory.setItem(i + 1, result)
                mixed = true
            }
        }
        if (mixed) {
            val ingredient: Item = inventory.getIngredient()
            ingredient.count--
            inventory.setIngredient(ingredient)
            fuel--
            sendFuel()
            this.getLevel().addSound(this, Sound.RANDOM_POTION_BREWED)
        }
        stopBrewing()
        return true
    }

    private fun restockFuel() {
        val fuel: Item = getInventory().getFuel()
        if (this.fuel > 0 || fuel.getId() !== ItemID.BLAZE_POWDER || fuel.getCount() <= 0) {
            return
        }
        fuel.count--
        this.fuel = 20
        fuelTotal = 20
        inventory.setFuel(fuel)
        sendFuel()
    }

    private fun stopBrewing() {
        brewTime = 0
        sendBrewTime()
        brewTime = MAX_BREW_TIME
    }

    private fun matchRecipes(quickTest: Boolean): Array<MixRecipe?> {
        val recipes: Array<MixRecipe?> = arrayOfNulls<MixRecipe>(if (quickTest) 1 else 3)
        val ingredient: Item = inventory.getIngredient()
        val craftingManager: CraftingManager = getLevel().getServer().getCraftingManager()
        for (i in 0..2) {
            val potion: Item = inventory.getItem(i + 1)
            if (potion.isNull()) {
                continue
            }
            var recipe: MixRecipe = craftingManager.matchBrewingRecipe(ingredient, potion)
            if (recipe == null) {
                recipe = craftingManager.matchContainerRecipe(ingredient, potion)
            }
            if (recipe == null) {
                continue
            }
            if (quickTest) {
                recipes[0] = recipe
                return recipes
            }
            recipes[i] = recipe
        }
        return recipes
    }

    protected fun sendFuel() {
        val pk = ContainerSetDataPacket()
        for (p in inventory.getViewers()) {
            val windowId: Int = p.getWindowId(inventory)
            if (windowId > 0) {
                pk.windowId = windowId
                pk.property = ContainerSetDataPacket.PROPERTY_BREWING_STAND_FUEL_AMOUNT
                pk.value = fuel
                p.dataPacket(pk)
                pk.property = ContainerSetDataPacket.PROPERTY_BREWING_STAND_FUEL_TOTAL
                pk.value = fuelTotal
                p.dataPacket(pk)
            }
        }
    }

    protected fun sendBrewTime() {
        val pk = ContainerSetDataPacket()
        pk.property = ContainerSetDataPacket.PROPERTY_BREWING_STAND_BREW_TIME
        pk.value = brewTime
        for (p in inventory.getViewers()) {
            val windowId: Int = p.getWindowId(inventory)
            if (windowId > 0) {
                pk.windowId = windowId
                p.dataPacket(pk)
            }
        }
    }

    @PowerNukkitDifference(info = "Will stop the processing if there are no other matching recipe", since = "1.3.1.2-PN")
    fun updateBlock() {
        val block: Block = this.getLevelBlock() as? BlockBrewingStand ?: return
        var meta = 0
        for (i in 1..3) {
            val potion: Item = inventory.getItem(i)
            val id: Int = potion.getId()
            if ((id == ItemID.POTION || id == ItemID.SPLASH_POTION || id == ItemID.LINGERING_POTION) && potion.getCount() > 0) {
                meta = meta or (1 shl i - 1)
            }
        }
        block.setDamage(meta)
        this.level.setBlock(block, block, false, false)
        if (brewTime != MAX_BREW_TIME && matchRecipes(true)[0] == null) {
            stopBrewing()
        }
    }

    @get:Override
    override val spawnCompound: CompoundTag
        get() {
            val nbt: CompoundTag = CompoundTag()
                    .putString("id", BlockEntity.BREWING_STAND)
                    .putInt("x", this.x as Int)
                    .putInt("y", this.y as Int)
                    .putInt("z", this.z as Int)
                    .putShort("FuelTotal", fuelTotal)
                    .putShort("FuelAmount", fuel)
            if (brewTime < MAX_BREW_TIME) {
                nbt.putShort("CookTime", brewTime)
            }
            if (hasName()) {
                nbt.put("CustomName", namedTag.get("CustomName"))
            }
            return nbt
        }

    companion object {
        const val MAX_BREW_TIME = 400

        @Deprecated
        @DeprecationDetails(since = "1.3.1.2-PN", reason = "Makes no sense and is unused", replaceWith = "Use CraftingManager")
        val ingredients: List<Integer> = ArrayList(Arrays.asList(
                ItemID.NETHER_WART, ItemID.GHAST_TEAR, ItemID.GLOWSTONE_DUST, ItemID.REDSTONE_DUST, ItemID.GUNPOWDER,
                ItemID.MAGMA_CREAM, ItemID.BLAZE_POWDER, ItemID.GOLDEN_CARROT, ItemID.SPIDER_EYE, ItemID.FERMENTED_SPIDER_EYE,
                ItemID.GLISTERING_MELON, ItemID.SUGAR, ItemID.RABBIT_FOOT, ItemID.PUFFERFISH, ItemID.TURTLE_SHELL,
                ItemID.PHANTOM_MEMBRANE, 437))
    }
}