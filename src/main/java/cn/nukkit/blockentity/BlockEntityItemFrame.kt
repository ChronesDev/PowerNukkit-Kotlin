package cn.nukkit.blockentity

import cn.nukkit.Player

/**
 * @author Pub4Game
 * @since 03.07.2016
 */
class BlockEntityItemFrame(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt) {
    @Override
    protected override fun initBlockEntity() {
        if (!namedTag.contains("Item")) {
            namedTag.putCompound("Item", NBTIO.putItemHelper(ItemBlock(Block.get(BlockID.AIR))))
        }
        if (!namedTag.contains("ItemRotation")) {
            namedTag.putByte("ItemRotation", 0)
        }
        if (!namedTag.contains("ItemDropChance")) {
            namedTag.putFloat("ItemDropChance", 1.0f)
        }
        this.level.updateComparatorOutputLevel(this)
        super.initBlockEntity()
    }

    @get:Override
    override var name: String
        get() = "Item Frame"
        set(name) {
            super.name = name
        }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() = this.getBlock().getId() === Block.ITEM_FRAME_BLOCK
    var itemRotation: Int
        get() = this.namedTag.getByte("ItemRotation")
        set(itemRotation) {
            this.namedTag.putByte("ItemRotation", itemRotation)
            this.level.updateComparatorOutputLevel(this)
            setDirty()
        }
    var item: Item?
        get() {
            val NBTTag: CompoundTag = this.namedTag.getCompound("Item")
            return NBTIO.getItemHelper(NBTTag)
        }
        set(item) {
            setItem(item, true)
        }

    fun setItem(item: Item?, setChanged: Boolean) {
        this.namedTag.putCompound("Item", NBTIO.putItemHelper(item))
        if (setChanged) {
            setDirty()
        }
        this.level.updateComparatorOutputLevel(this)
    }

    var itemDropChance: Float
        get() = this.namedTag.getFloat("ItemDropChance")
        set(chance) {
            this.namedTag.putFloat("ItemDropChance", chance)
        }

    override fun setDirty() {
        this.spawnToAll()
        super.setDirty()
    }

    @get:Override
    override val spawnCompound: CompoundTag
        get() {
            if (!this.namedTag.contains("Item")) {
                setItem(ItemBlock(Block.get(BlockID.AIR)), false)
            }
            val item: Item? = item
            val tag: CompoundTag = CompoundTag()
                    .putString("id", BlockEntity.ITEM_FRAME)
                    .putInt("x", this.x as Int)
                    .putInt("y", this.y as Int)
                    .putInt("z", this.z as Int)
            if (!item.isNull()) {
                val itemTag: CompoundTag = NBTIO.putItemHelper(item)
                val networkFullId: Int = item.getNetworkFullId()
                val networkDamage = if (networkFullId and 0x1 == 0x1) 0 else item.getDamage()
                val namespacedId: String = RuntimeItems.getRuntimeMapping().getNamespacedIdByNetworkId(
                        RuntimeItems.getNetworkId(networkFullId)
                )
                if (namespacedId != null) {
                    itemTag.remove("id")
                    itemTag.putShort("Damage", networkDamage)
                    itemTag.putString("Name", namespacedId)
                }
                tag.putCompound("Item", itemTag)
                        .putByte("ItemRotation", itemRotation)
            }
            return tag
        }
    val analogOutput: Int
        get() = if (item == null || item.getId() === 0) 0 else itemRotation % 8 + 1

    @Since("1.4.0.0-PN")
    fun dropItem(player: Player?): Boolean {
        val before: Item? = item
        if (before == null || before.isNull()) {
            return false
        }
        val drop: EntityItem? = dropItemAndGetEntity(player)
        if (drop != null) {
            return true
        }
        val after: Item? = item
        return after == null || after.isNull()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    fun dropItemAndGetEntity(@Nullable player: Player?): EntityItem? {
        val level: Level = getValidLevel()
        val drop: Item? = item
        if (drop.isNull()) {
            if (player != null) {
                spawnTo(player)
            }
            return null
        }
        val event = ItemFrameDropItemEvent(player, getLevelBlock(), this, drop)
        level.getServer().getPluginManager().callEvent(event)
        if (event.isCancelled()) {
            if (player != null) {
                spawnTo(player)
            }
            return null
        }
        var itemEntity: EntityItem? = null
        if (itemDropChance > ThreadLocalRandom.current().nextFloat()) {
            itemEntity = level.dropAndGetItem(add(0.5, 0.25, 0.5), drop)
            if (itemEntity == null) {
                if (player != null) {
                    spawnTo(player)
                }
                return null
            }
        }
        setItem(MinecraftItemID.AIR.get(0), true)
        itemRotation = 0
        spawnToAll()
        level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_REMOVED)
        return itemEntity
    }
}