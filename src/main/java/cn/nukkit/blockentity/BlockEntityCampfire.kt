package cn.nukkit.blockentity

import cn.nukkit.Player

class BlockEntityCampfire(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt), InventoryHolder, BlockEntityContainer {
    private var inventory: CampfireInventory? = null
    private var burnTime: IntArray
    private var recipes: Array<CampfireRecipe?>
    private var keepItem: BooleanArray
    @Override
    protected override fun initBlockEntity() {
        inventory = CampfireInventory(this, InventoryType.CAMPFIRE)
        burnTime = IntArray(4)
        recipes = arrayOfNulls<CampfireRecipe>(4)
        keepItem = BooleanArray(4)
        for (i in 1..burnTime.size) {
            burnTime[i - 1] = namedTag.getInt("ItemTime$i")
            keepItem[i - 1] = namedTag.getBoolean("KeepItem" + 1)
            if (this.namedTag.contains("Item$i") && this.namedTag.get("Item$i") is CompoundTag) {
                inventory.setItem(i - 1, NBTIO.getItemHelper(this.namedTag.getCompound("Item$i")))
            }
        }
        super.initBlockEntity()
        scheduleUpdate()
    }

    @Override
    override fun onUpdate(): Boolean {
        var needsUpdate = false
        val block: Block = getBlock()
        val isLit = block is BlockCampfire && !(block as BlockCampfire).isExtinguished()
        for (slot in 0 until inventory.getSize()) {
            val item: Item = inventory.getItem(slot)
            if (item == null || item.getId() === BlockID.AIR || item.getCount() <= 0) {
                burnTime[slot] = 0
                recipes[slot] = null
            } else if (!keepItem[slot]) {
                var recipe: CampfireRecipe? = recipes[slot]
                if (recipe == null) {
                    recipe = this.server.getCraftingManager().matchCampfireRecipe(item)
                    if (recipe == null) {
                        inventory.setItem(slot, Item.get(0))
                        val random: ThreadLocalRandom = ThreadLocalRandom.current()
                        this.level.dropItem(add(random.nextFloat(), 0.5, random.nextFloat()), item)
                        burnTime[slot] = 0
                        recipes[slot] = null
                        continue
                    } else {
                        burnTime[slot] = 600
                        recipes[slot] = recipe
                    }
                }
                val burnTimeLeft = burnTime[slot]
                if (burnTimeLeft <= 0) {
                    val product: Item = Item.get(recipe.getResult().getId(), recipe.getResult().getDamage(), item.getCount())
                    val event = CampfireSmeltEvent(this, item, product)
                    if (!event.isCancelled()) {
                        inventory.setItem(slot, Item.get(0))
                        val random: ThreadLocalRandom = ThreadLocalRandom.current()
                        this.level.dropItem(add(random.nextFloat(), 0.5, random.nextFloat()), event.getResult())
                        burnTime[slot] = 0
                        recipes[slot] = null
                    } else if (event.getKeepItem()) {
                        keepItem[slot] = true
                        burnTime[slot] = 0
                        recipes[slot] = null
                    }
                } else if (isLit) {
                    burnTime[slot]--
                    needsUpdate = true
                } else {
                    burnTime[slot] = 600
                }
            }
        }
        return needsUpdate
    }

    fun getKeepItem(slot: Int): Boolean {
        return if (slot < 0 || slot >= keepItem.size) {
            false
        } else keepItem[slot]
    }

    fun setKeepItem(slot: Int, keep: Boolean) {
        if (slot < 0 || slot >= keepItem.size) {
            return
        }
        keepItem[slot] = keep
    }

    @Override
    override fun saveNBT() {
        for (i in 1..burnTime.size) {
            val item: Item = inventory.getItem(i - 1)
            if (item == null || item.getId() === BlockID.AIR || item.getCount() <= 0) {
                namedTag.remove("Item$i")
                namedTag.putInt("ItemTime$i", 0)
                namedTag.remove("KeepItem$i")
            } else {
                namedTag.putCompound("Item$i", NBTIO.putItemHelper(item))
                namedTag.putInt("ItemTime$i", burnTime[i - 1])
                namedTag.putBoolean("KeepItem$i", keepItem[i - 1])
            }
        }
        super.saveNBT()
    }

    fun setRecipe(index: Int, recipe: CampfireRecipe?) {
        recipes[index] = recipe
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
    }

    @get:Override
    override var name: String
        get() = "Campfire"
        set(name) {
            super.name = name
        }

    @get:Override
    override val spawnCompound: CompoundTag
        get() {
            val c: CompoundTag = CompoundTag()
                    .putString("id", BlockEntity.CAMPFIRE)
                    .putInt("x", this.x as Int)
                    .putInt("y", this.y as Int)
                    .putInt("z", this.z as Int)
            for (i in 1..burnTime.size) {
                val item: Item = inventory.getItem(i - 1)
                if (item == null || item.getId() === BlockID.AIR || item.getCount() <= 0) {
                    c.remove("Item$i")
                } else {
                    c.putCompound("Item$i", NBTIO.putItemHelper(item))
                }
            }
            return c
        }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() = getBlock().getId() === BlockID.CAMPFIRE_BLOCK

    @get:Override
    override val size: Int
        get() = 4

    @Override
    override fun getItem(index: Int): Item {
        return if (index < 0 || index >= size) {
            ItemBlock(BlockAir(), 0, 0)
        } else {
            val data: CompoundTag = this.namedTag.getCompound("Item" + (index + 1))
            NBTIO.getItemHelper(data)
        }
    }

    @Override
    override fun setItem(index: Int, item: Item?) {
        if (index < 0 || index >= size) {
            return
        }
        val nbt: CompoundTag = NBTIO.putItemHelper(item)
        this.namedTag.putCompound("Item" + (index + 1), nbt)
    }

    @Override
    fun getInventory(): CampfireInventory? {
        return inventory
    }
}