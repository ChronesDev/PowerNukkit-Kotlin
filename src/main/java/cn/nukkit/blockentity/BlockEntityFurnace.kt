package cn.nukkit.blockentity

import cn.nukkit.Player

/**
 * @author MagicDroidX
 */
class BlockEntityFurnace(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt), InventoryHolder, BlockEntityContainer, BlockEntityNameable {
    protected var inventory: FurnaceInventory? = null
    var burnTime = 0
    var burnDuration = 0
    var cookTime = 0
    var maxTime = 0
    protected val inventoryType: InventoryType
        protected get() = InventoryType.FURNACE

    @Override
    protected override fun initBlockEntity() {
        inventory = FurnaceInventory(this, inventoryType)
        if (!this.namedTag.contains("Items") || this.namedTag.get("Items") !is ListTag) {
            this.namedTag.putList(ListTag<CompoundTag>("Items"))
        }
        for (i in 0 until size) {
            inventory.setItem(i, getItem(i))
        }
        burnTime = if (!this.namedTag.contains("BurnTime") || this.namedTag.getShort("BurnTime") < 0) {
            0
        } else {
            this.namedTag.getShort("BurnTime")
        }
        cookTime = if (!this.namedTag.contains("CookTime") || this.namedTag.getShort("CookTime") < 0 || this.namedTag.getShort("BurnTime") === 0 && this.namedTag.getShort("CookTime") > 0) {
            0
        } else {
            this.namedTag.getShort("CookTime")
        }
        burnDuration = if (!this.namedTag.contains("BurnDuration") || this.namedTag.getShort("BurnDuration") < 0) {
            0
        } else {
            this.namedTag.getShort("BurnDuration")
        }
        if (!this.namedTag.contains("MaxTime")) {
            maxTime = burnTime
            burnDuration = 0
        } else {
            maxTime = this.namedTag.getShort("MaxTime")
        }
        if (this.namedTag.contains("BurnTicks")) {
            burnDuration = this.namedTag.getShort("BurnTicks")
            this.namedTag.remove("BurnTicks")
        }
        if (burnTime > 0) {
            this.scheduleUpdate()
        }
        super.initBlockEntity()
    }

    protected val furnaceName: String
        protected get() = "Furnace"
    protected val clientName: String
        protected get() = FURNACE

    @get:Override
    @set:Override
    override var name: String
        get() = if (hasName()) this.namedTag.getString("CustomName") else furnaceName
        set(name) {
            if (name == null || name.equals("")) {
                this.namedTag.remove("CustomName")
                return
            }
            this.namedTag.putString("CustomName", name)
        }

    @Override
    override fun hasName(): Boolean {
        return this.namedTag.contains("CustomName")
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
        this.namedTag.putList(ListTag<CompoundTag>("Items"))
        for (index in 0 until size) {
            setItem(index, inventory.getItem(index))
        }
        this.namedTag.putShort("CookTime", cookTime)
        this.namedTag.putShort("BurnTime", burnTime)
        this.namedTag.putShort("BurnDuration", burnDuration)
        this.namedTag.putShort("MaxTime", maxTime)
    }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() {
            val blockID: Int = getBlock().getId()
            return blockID == idleBlockId || blockID == burningBlockId
        }

    @get:Override
    override val size: Int
        get() = 3

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
        if (item.getId() === Item.AIR || item.getCount() <= 0) {
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
    fun getInventory(): FurnaceInventory? {
        return inventory
    }

    protected val idleBlockId: Int
        protected get() = Block.FURNACE
    protected val burningBlockId: Int
        protected get() = Block.LIT_FURNACE

    protected fun setBurning(burning: Boolean) {
        if (burning) {
            if (this.getBlock().getId() === idleBlockId) {
                this.getLevel().setBlock(this, Block.get(burningBlockId, this.getBlock().getDamage()), true)
            }
        } else if (this.getBlock().getId() === burningBlockId) {
            this.getLevel().setBlock(this, Block.get(idleBlockId, this.getBlock().getDamage()), true)
        }
    }

    protected fun checkFuel(fuel: Item) {
        var fuel: Item = fuel
        val ev = FurnaceBurnEvent(this, fuel, if (fuel.getFuelTime() == null) 0 else fuel.getFuelTime())
        this.server.getPluginManager().callEvent(ev)
        if (ev.isCancelled()) {
            return
        }
        maxTime = Math.ceil(ev.getBurnTime() / speedMultiplier.toFloat())
        burnTime = Math.ceil(ev.getBurnTime() / speedMultiplier.toFloat())
        burnDuration = 0
        setBurning(true)
        if (burnTime > 0 && ev.isBurning()) {
            fuel.setCount(fuel.getCount() - 1)
            if (fuel.getCount() === 0) {
                if (fuel.getId() === Item.BUCKET && (fuel as ItemBucket).isLava()) {
                    fuel.setDamage(0)
                    fuel.setCount(1)
                } else {
                    fuel = ItemBlock(Block.get(BlockID.AIR), 0, 0)
                }
            }
            inventory.setFuel(fuel)
        }
    }

    protected fun matchRecipe(raw: Item?): SmeltingRecipe {
        return this.server.getCraftingManager().matchFurnaceRecipe(raw)
    }

    protected val speedMultiplier: Int
        protected get() = 1

    @Override
    override fun onUpdate(): Boolean {
        if (this.closed) {
            return false
        }
        this.timing.startTiming()
        var ret = false
        val fuel: Item = inventory.getFuel()
        var raw: Item = inventory.getSmelting()
        var product: Item = inventory.getResult()
        val smelt: SmeltingRecipe = matchRecipe(raw)
        val canSmelt = smelt != null && raw.getCount() > 0 && (smelt.getResult().equals(product, true) && product.getCount() < product.getMaxStackSize() || product.getId() === Item.AIR)
        if (burnTime <= 0 && canSmelt && fuel.getFuelTime() != null && fuel.getCount() > 0) {
            checkFuel(fuel)
        }
        if (burnTime > 0) {
            burnTime--
            val readyAt = 200 / speedMultiplier
            burnDuration = Math.ceil(burnTime.toFloat() / maxTime * readyAt)
            if (smelt != null && canSmelt) {
                cookTime++
                if (cookTime >= readyAt) {
                    product = Item.get(smelt.getResult().getId(), smelt.getResult().getDamage(), product.getCount() + 1)
                    val ev = FurnaceSmeltEvent(this, raw, product)
                    this.server.getPluginManager().callEvent(ev)
                    if (!ev.isCancelled()) {
                        inventory.setResult(ev.getResult())
                        raw.setCount(raw.getCount() - 1)
                        if (raw.getCount() === 0) {
                            raw = ItemBlock(Block.get(BlockID.AIR), 0, 0)
                        }
                        inventory.setSmelting(raw)
                    }
                    cookTime -= readyAt
                }
            } else if (burnTime <= 0) {
                burnTime = 0
                cookTime = 0
                burnDuration = 0
            } else {
                cookTime = 0
            }
            ret = true
        } else {
            setBurning(false)
            burnTime = 0
            cookTime = 0
            burnDuration = 0
        }
        for (player in getInventory().getViewers()) {
            val windowId: Int = player.getWindowId(getInventory())
            if (windowId > 0) {
                var pk = ContainerSetDataPacket()
                pk.windowId = windowId
                pk.property = ContainerSetDataPacket.PROPERTY_FURNACE_TICK_COUNT
                pk.value = cookTime
                player.batchDataPacket(pk)
                pk = ContainerSetDataPacket()
                pk.windowId = windowId
                pk.property = ContainerSetDataPacket.PROPERTY_FURNACE_LIT_TIME
                pk.value = burnDuration
                player.batchDataPacket(pk)
            }
        }
        this.timing.stopTiming()
        return ret
    }

    @get:Override
    override val spawnCompound: CompoundTag
        get() {
            val c: CompoundTag = CompoundTag()
                    .putString("id", clientName)
                    .putInt("x", this.x as Int)
                    .putInt("y", this.y as Int)
                    .putInt("z", this.z as Int)
                    .putShort("BurnDuration", burnDuration)
                    .putShort("BurnTime", burnTime)
                    .putShort("CookTime", cookTime)
            if (hasName()) {
                c.put("CustomName", this.namedTag.get("CustomName"))
            }
            return c
        }
}