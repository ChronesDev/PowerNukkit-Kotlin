package cn.nukkit.blockentity

import cn.nukkit.Player

/**
 * @author PetteriM1
 */
class BlockEntityShulkerBox(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt), InventoryHolder, BlockEntityContainer, BlockEntityNameable {
    protected var inventory: ShulkerBoxInventory? = null
    @Override
    protected override fun initBlockEntity() {
        inventory = ShulkerBoxInventory(this)
        if (!this.namedTag.contains("Items") || this.namedTag.get("Items") !is ListTag) {
            this.namedTag.putList(ListTag<CompoundTag>("Items"))
        }
        val list: ListTag<CompoundTag> = this.namedTag.getList("Items") as ListTag<CompoundTag>
        for (compound in list.getAll()) {
            val item: Item = NBTIO.getItemHelper(compound)
            inventory.slots.put(compound.getByte("Slot"), item)
        }
        if (!this.namedTag.contains("facing")) {
            this.namedTag.putByte("facing", 0)
        }
        super.initBlockEntity()
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
    override fun saveNBT() {
        this.namedTag.putList(ListTag<CompoundTag>("Items"))
        for (index in 0 until size) {
            setItem(index, inventory.getItem(index))
        }
    }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() {
            val blockID: Int = this.getBlock().getId()
            return blockID == Block.SHULKER_BOX || blockID == Block.UNDYED_SHULKER_BOX
        }

    @get:Override
    override val size: Int
        get() = 27

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
                this.namedTag.getList("Items").remove(i)
            }
        } else if (i < 0) {
            this.namedTag.getList("Items", CompoundTag::class.java).add(d)
        } else {
            this.namedTag.getList("Items", CompoundTag::class.java).add(i, d)
        }
    }

    @Override
    fun getInventory(): BaseInventory? {
        return inventory
    }

    val realInventory: ShulkerBoxInventory?
        get() = inventory

    @get:Override
    @set:Override
    override var name: String
        get() = if (hasName()) this.namedTag.getString("CustomName") else "Shulker Box"
        set(name) {
            if (name == null || name.isEmpty()) {
                this.namedTag.remove("CustomName")
                return
            }
            this.namedTag.putString("CustomName", name)
        }

    @Override
    override fun hasName(): Boolean {
        return this.namedTag.contains("CustomName")
    }

    @get:Override
    override val spawnCompound: CompoundTag
        get() {
            val c: CompoundTag = getDefaultCompound(this, SHULKER_BOX)
                    .putByte("facing", this.namedTag.getByte("facing"))
            if (hasName()) {
                c.put("CustomName", this.namedTag.get("CustomName"))
            }
            return c
        }
}