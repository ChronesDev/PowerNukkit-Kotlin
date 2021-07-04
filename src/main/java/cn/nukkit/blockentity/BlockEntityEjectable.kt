package cn.nukkit.blockentity

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
abstract class BlockEntityEjectable @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt), BlockEntityContainer, BlockEntityNameable, InventoryHolder {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected var inventory: EjectableInventory? = null
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected abstract fun createInventory(): EjectableInventory?

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    protected abstract val blockEntityName: String?
    @Override
    protected override fun initBlockEntity() {
        inventory = createInventory()
        if (!this.namedTag.contains("Items") || this.namedTag.get("Items") !is ListTag) {
            this.namedTag.putList(ListTag<CompoundTag>("Items"))
        }
        for (i in 0 until size) {
            inventory.setItem(i, getItem(i))
        }
        super.initBlockEntity()
    }

    @get:Override
    override val size: Int
        get() = 9

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
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
    fun getInventory(): EjectableInventory? {
        return inventory
    }

    @get:Override
    override val spawnCompound: CompoundTag
        get() {
            val c: CompoundTag = CompoundTag()
                    .putString("id", blockEntityName)
                    .putInt("x", this.x as Int)
                    .putInt("y", this.y as Int)
                    .putInt("z", this.z as Int)
            if (hasName()) {
                c.put("CustomName", this.namedTag.get("CustomName"))
            }
            return c
        }

    @Override
    override fun saveNBT() {
        this.namedTag.putList(ListTag<CompoundTag>("Items"))
        for (index in 0 until size) {
            setItem(index, inventory.getItem(index))
        }
        super.saveNBT()
    }

    @get:Override
    @set:Override
    override var name: String
        get() = if (hasName()) this.namedTag.getString("CustomName") else blockEntityName!!
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
    override fun onBreak() {
        for (content in inventory.getContents().values()) {
            level.dropItem(this, content)
        }
    }
}