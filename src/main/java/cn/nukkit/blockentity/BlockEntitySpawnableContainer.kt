package cn.nukkit.blockentity

import cn.nukkit.Player

abstract class BlockEntitySpawnableContainer(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt), InventoryHolder, BlockEntityContainer {
    protected var inventory: ContainerInventory? = null
    @Override
    protected override fun initBlockEntity() {
        if (!this.namedTag.contains("Items") || this.namedTag.get("Items") !is ListTag) {
            this.namedTag.putList(ListTag<CompoundTag>("Items"))
        }
        val list: ListTag<CompoundTag> = this.namedTag.getList("Items") as ListTag<CompoundTag>
        for (compound in list.getAll()) {
            val item: Item = NBTIO.getItemHelper(compound)
            inventory.slots.put(compound.getByte("Slot"), item)
        }
        super.initBlockEntity()
    }

    @Override
    override fun close() {
        if (!closed) {
            for (player in HashSet(this.getInventory().getViewers())) {
                player.removeWindow(this.getInventory())
            }
            super.close()
        }
    }

    @Override
    override fun onBreak() {
        for (content in inventory.getContents().values()) {
            level.dropItem(this, content)
        }
        inventory.clearAll() // Stop items from being moved around by another player in the inventory
    }

    @Override
    override fun saveNBT() {
        this.namedTag.putList(ListTag<CompoundTag>("Items"))
        for (index in 0 until this.getSize()) {
            setItem(index, inventory.getItem(index))
        }
    }

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
            ItemBlock(BlockAir(), 0, 0)
        } else {
            val data: CompoundTag = this.namedTag.getList("Items").get(i) as CompoundTag
            NBTIO.getItemHelper(data)
        }
    }

    @Override
    override fun setItem(index: Int, item: Item) {
        val i = getSlotIndex(index)
        val d: CompoundTag = NBTIO.putItemHelper(item, index)

        // If item is air or count less than 0, remove the item from the "Items" list
        if (item.getId() === Item.AIR || item.getCount() <= 0) {
            if (i >= 0) {
                this.namedTag.getList("Items").remove(i)
            }
        } else if (i < 0) {
            // If it is less than i, then it is a new item, so we are going to add it at the end of the list
            this.namedTag.getList("Items", CompoundTag::class.java).add(d)
        } else {
            // If it is more than i, then it is an update on a inventorySlot, so we are going to overwrite the item in the list
            this.namedTag.getList("Items", CompoundTag::class.java).add(i, d)
        }
    }
}