package cn.nukkit.entity.item

import cn.nukkit.Player

class EntityMinecartHopper(chunk: FullChunk?, nbt: CompoundTag?) : EntityMinecartAbstract(chunk, nbt), InventoryHolder {
    protected var inventory: MinecartHopperInventory? = null

    @get:Override
    val name: String
        get() = type.getName()

    @get:Override
    override val type: MinecartType
        get() = MinecartType.valueOf(5)

    @get:Override
    override val isRideable: Boolean
        get() = false

    @Override
    override fun dropItem() {
        super.dropItem()
        this.level.dropItem(this, Item.get(Item.HOPPER))
        for (item in inventory.getContents().values()) {
            this.level.dropItem(this, item)
        }
        inventory.clearAll()
    }

    @Override
    fun mountEntity(entity: Entity?, mode: Byte): Boolean {
        return false
    }

    @Override
    override fun onInteract(player: Player, item: Item?, clickedPos: Vector3?): Boolean {
        player.addWindow(inventory)
        return false // If true, the count of items player has in hand decreases
    }

    @Override
    fun getInventory(): MinecartHopperInventory? {
        return inventory
    }

    @Override
    override fun initEntity() {
        super.initEntity()
        inventory = MinecartHopperInventory(this)
        if (this.namedTag.contains("Items") && this.namedTag.get("Items") is ListTag) {
            val inventoryList: ListTag<CompoundTag> = this.namedTag.getList("Items", CompoundTag::class.java)
            for (item in inventoryList.getAll()) {
                inventory.setItem(item.getByte("Slot"), NBTIO.getItemHelper(item))
            }
        }
        this.dataProperties
                .putByte(DATA_CONTAINER_TYPE, 11)
                .putInt(DATA_CONTAINER_BASE_SIZE, inventory.getSize())
                .putInt(DATA_CONTAINER_EXTRA_SLOTS_PER_STRENGTH, 0)
    }

    @Override
    override fun saveNBT() {
        super.saveNBT()
        this.namedTag.putList(ListTag<CompoundTag>("Items"))
        if (inventory != null) {
            for (slot in 0..4) {
                val item: Item = inventory.getItem(slot)
                if (item != null && item.getId() !== Item.AIR) {
                    this.namedTag.getList("Items", CompoundTag::class.java)
                            .add(NBTIO.putItemHelper(item, slot))
                }
            }
        }
    }

    companion object {
        @get:Override
        val networkId = 96
            get() = Companion.field
    }

    init {
        setDisplayBlock(Block.get(Block.HOPPER_BLOCK), false)
    }
}