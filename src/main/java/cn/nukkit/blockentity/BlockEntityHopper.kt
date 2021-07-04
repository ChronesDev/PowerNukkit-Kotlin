package cn.nukkit.blockentity

import cn.nukkit.Player

/**
 * @author CreeperFace
 * @since 8.5.2017
 */
class BlockEntityHopper(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt), InventoryHolder, BlockEntityContainer, BlockEntityNameable {
    protected var inventory: HopperInventory? = null
    var transferCooldown = 0
    private var pickupArea: AxisAlignedBB? = null

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isDisabled = false
    private val temporalVector: BlockVector3 = BlockVector3()
    @Override
    protected override fun initBlockEntity() {
        if (this.namedTag.contains("TransferCooldown")) {
            transferCooldown = this.namedTag.getInt("TransferCooldown")
        } else {
            transferCooldown = 8
        }
        inventory = HopperInventory(this)
        if (!this.namedTag.contains("Items") || this.namedTag.get("Items") !is ListTag) {
            this.namedTag.putList(ListTag<CompoundTag>("Items"))
        }
        for (i in 0 until size) {
            inventory.setItem(i, getItem(i))
        }
        pickupArea = SimpleAxisAlignedBB(this.x, this.y, this.z, this.x + 1, this.y + 2, this.z + 1)
        this.scheduleUpdate()
        super.initBlockEntity()
        val block: Block = getBlock()
        if (block is BlockHopper) {
            isDisabled = !(block as BlockHopper).isEnabled()
        }
    }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() = this.level.getBlockIdAt(this.getFloorX(), this.getFloorY(), this.getFloorZ()) === Block.HOPPER_BLOCK

    @get:Override
    @set:Override
    override var name: String
        get() = if (hasName()) this.namedTag.getString("CustomName") else "Hopper"
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

    val isOnTransferCooldown: Boolean
        get() = transferCooldown > 0

    fun setTransferCooldown(transferCooldown: Int) {
        this.transferCooldown = transferCooldown
    }

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
    override fun saveNBT() {
        this.namedTag.putList(ListTag<CompoundTag>("Items"))
        for (index in 0 until size) {
            setItem(index, inventory.getItem(index))
        }
        this.namedTag.putInt("TransferCooldown", transferCooldown)
    }

    @Override
    fun getInventory(): HopperInventory? {
        return inventory
    }

    @Override
    override fun onUpdate(): Boolean {
        if (this.closed) {
            return false
        }
        if (isOnTransferCooldown) {
            transferCooldown--
            return true
        }
        if (isDisabled) {
            return false
        }
        val blockSide: Block = this.getBlock().getSide(BlockFace.UP)
        val blockEntity: BlockEntity = this.level.getBlockEntity(temporalVector.setComponentsAdding(this, BlockFace.UP))
        var changed = pushItems()
        changed = if (blockEntity is InventoryHolder || blockSide is BlockComposter) {
            pullItems() || changed
        } else {
            pickupItems() || changed
        }
        if (changed) {
            setTransferCooldown(8)
            setDirty()
        }
        return true
    }

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val isObservable: Boolean
        get() = false

    @PowerNukkitDifference(info = "Check if the hopper above is locked, then don't pull items.", since = "1.4.0.0-PN")
    fun pullItems(): Boolean {
        if (inventory.isFull()) {
            return false
        }
        val blockSide: Block = this.getBlock().getSide(BlockFace.UP)
        val blockEntity: BlockEntity = this.level.getBlockEntity(temporalVector.setComponentsAdding(this, BlockFace.UP))
        if (blockEntity is BlockEntityHopper) {
            if (blockEntity.isDisabled) return false
        }

        //Fix for furnace outputs
        if (blockEntity is BlockEntityFurnace) {
            val inv: FurnaceInventory = blockEntity.getInventory()
            val item: Item = inv.getResult()
            if (!item.isNull()) {
                val itemToAdd: Item = item.clone()
                itemToAdd.count = 1
                if (!inventory.canAddItem(itemToAdd)) {
                    return false
                }
                val ev = InventoryMoveItemEvent(inv, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE)
                this.server.getPluginManager().callEvent(ev)
                if (ev.isCancelled()) {
                    return false
                }
                val items: Array<Item> = inventory.addItem(itemToAdd)
                if (items.size <= 0) {
                    item.count--
                    inv.setResult(item)
                    return true
                }
            }
        } else if (blockEntity is InventoryHolder) {
            val inv: Inventory = (blockEntity as InventoryHolder).getInventory()
            for (i in 0 until inv.getSize()) {
                val item: Item = inv.getItem(i)
                if (!item.isNull()) {
                    val itemToAdd: Item = item.clone()
                    itemToAdd.count = 1
                    if (!inventory.canAddItem(itemToAdd)) {
                        continue
                    }
                    val ev = InventoryMoveItemEvent(inv, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE)
                    this.server.getPluginManager().callEvent(ev)
                    if (ev.isCancelled()) {
                        continue
                    }
                    val items: Array<Item> = inventory.addItem(itemToAdd)
                    if (items.size >= 1) {
                        continue
                    }
                    item.count--
                    inv.setItem(i, item)
                    return true
                }
            }
        } else if (blockSide is BlockComposter) {
            val blockComposter: BlockComposter = blockSide as BlockComposter
            if (blockComposter.isFull()) {
                val item: Item = blockComposter.empty()
                if (item == null || item.isNull()) {
                    return false
                }
                val itemToAdd: Item = item.clone()
                itemToAdd.count = 1
                if (!inventory.canAddItem(itemToAdd)) {
                    return false
                }
                val items: Array<Item> = inventory.addItem(itemToAdd)
                return items.size < 1
            }
        }
        return false
    }

    fun pickupItems(): Boolean {
        if (inventory.isFull()) {
            return false
        }
        var pickedUpItem = false
        for (entity in this.level.getCollidingEntities(pickupArea)) {
            if (entity.isClosed() || entity !is EntityItem) {
                continue
            }
            val itemEntity: EntityItem = entity as EntityItem
            val item: Item = itemEntity.getItem()
            if (item.isNull()) {
                continue
            }
            val originalCount: Int = item.getCount()
            if (!inventory.canAddItem(item)) {
                continue
            }
            val ev = InventoryMoveItemEvent(null, inventory, this, item, InventoryMoveItemEvent.Action.PICKUP)
            this.server.getPluginManager().callEvent(ev)
            if (ev.isCancelled()) {
                continue
            }
            val items: Array<Item> = inventory.addItem(item)
            if (items.size == 0) {
                entity.close()
                pickedUpItem = true
                continue
            }
            if (items[0].getCount() !== originalCount) {
                pickedUpItem = true
                item.setCount(items[0].getCount())
            }
        }

        //TODO: check for minecart
        return pickedUpItem
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

    fun pushItems(): Boolean {
        if (inventory.isEmpty()) {
            return false
        }
        val levelBlockState: BlockState = getLevelBlockState()
        if (levelBlockState.getBlockId() !== BlockID.HOPPER_BLOCK) {
            return false
        }
        val side: BlockFace = levelBlockState.getPropertyValue(CommonBlockProperties.FACING_DIRECTION)
        val blockSide: Block = this.getBlock().getSide(side)
        val be: BlockEntity = this.level.getBlockEntity(temporalVector.setComponentsAdding(this, side))
        if (be is BlockEntityHopper && levelBlockState.isDefaultState() || be !is InventoryHolder && blockSide !is BlockComposter) {
            return false
        }
        var event: InventoryMoveItemEvent

        //Fix for furnace inputs
        if (be is BlockEntityFurnace) {
            val inventory: FurnaceInventory = be.getInventory()
            if (inventory.isFull()) {
                return false
            }
            var pushedItem = false
            for (i in 0 until this.inventory.getSize()) {
                val item: Item = this.inventory.getItem(i)
                if (!item.isNull()) {
                    val itemToAdd: Item = item.clone()
                    itemToAdd.setCount(1)

                    //Check direction of hopper
                    if (this.getBlock().getDamage() === 0) {
                        val smelting: Item = inventory.getSmelting()
                        if (smelting.isNull()) {
                            event = InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE)
                            this.server.getPluginManager().callEvent(event)
                            if (!event.isCancelled()) {
                                inventory.setSmelting(itemToAdd)
                                item.count--
                                pushedItem = true
                            }
                        } else if (inventory.getSmelting().getId() === itemToAdd.getId() && inventory.getSmelting().getDamage() === itemToAdd.getDamage() && smelting.count < smelting.getMaxStackSize()) {
                            event = InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE)
                            this.server.getPluginManager().callEvent(event)
                            if (!event.isCancelled()) {
                                smelting.count++
                                inventory.setSmelting(smelting)
                                item.count--
                                pushedItem = true
                            }
                        }
                    } else if (Fuel.duration.containsKey(itemToAdd.getId())) {
                        val fuel: Item = inventory.getFuel()
                        if (fuel.isNull()) {
                            event = InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE)
                            this.server.getPluginManager().callEvent(event)
                            if (!event.isCancelled()) {
                                inventory.setFuel(itemToAdd)
                                item.count--
                                pushedItem = true
                            }
                        } else if (fuel.getId() === itemToAdd.getId() && fuel.getDamage() === itemToAdd.getDamage() && fuel.count < fuel.getMaxStackSize()) {
                            event = InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE)
                            this.server.getPluginManager().callEvent(event)
                            if (!event.isCancelled()) {
                                fuel.count++
                                inventory.setFuel(fuel)
                                item.count--
                                pushedItem = true
                            }
                        }
                    }
                    if (pushedItem) {
                        this.inventory.setItem(i, item)
                    }
                }
            }
            return pushedItem
        } else if (blockSide is BlockComposter) {
            val composter: BlockComposter = blockSide as BlockComposter
            if (composter.isFull()) {
                return false
            }
            for (i in 0 until inventory.getSize()) {
                val item: Item = inventory.getItem(i)
                if (item.isNull()) {
                    continue
                }
                val itemToAdd: Item = item.clone()
                itemToAdd.setCount(1)
                if (!composter.onActivate(item)) {
                    return false
                }
                item.count--
                inventory.setItem(i, item)
                return true
            }
        } else {
            val inventory: Inventory = (be as InventoryHolder).getInventory()
            if (inventory.isFull()) {
                return false
            }
            for (i in 0 until this.inventory.getSize()) {
                val item: Item = this.inventory.getItem(i)
                if (!item.isNull()) {
                    val itemToAdd: Item = item.clone()
                    itemToAdd.setCount(1)
                    if (!inventory.canAddItem(itemToAdd)) {
                        continue
                    }
                    val ev = InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE)
                    this.server.getPluginManager().callEvent(ev)
                    if (ev.isCancelled()) {
                        continue
                    }
                    val items: Array<Item> = inventory.addItem(itemToAdd)
                    if (items.size > 0) {
                        continue
                    }
                    item.count--
                    this.inventory.setItem(i, item)
                    return true
                }
            }
        }

        //TODO: check for minecart
        return false
    }

    @get:Override
    override val spawnCompound: CompoundTag
        get() {
            val c: CompoundTag = CompoundTag()
                    .putString("id", BlockEntity.HOPPER)
                    .putInt("x", this.x as Int)
                    .putInt("y", this.y as Int)
                    .putInt("z", this.z as Int)
            if (hasName()) {
                c.put("CustomName", this.namedTag.get("CustomName"))
            }
            return c
        }
}