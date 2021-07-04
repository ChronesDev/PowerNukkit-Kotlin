package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
@Log4j2
class BlockCampfire @PowerNukkitOnly constructor(meta: Int) : BlockTransparentMeta(meta), Faceable, BlockEntityHolder<BlockEntityCampfire?> {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = CAMPFIRE_BLOCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.CAMPFIRE

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityCampfire?>
        get() = BlockEntityCampfire::class.java

    @get:Override
    override val lightLevel: Int
        get() = if (isExtinguished) 0 else 15

    @get:Override
    override val resistance: Double
        get() = 2

    @get:Override
    override val hardness: Double
        get() = 5

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @Override
    override fun canHarvestWithHand(): Boolean {
        return true
    }

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return arrayOf<Item>(ItemCoal(0, 1 + ThreadLocalRandom.current().nextInt(1)))
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @Override
    override fun place(@Nonnull item: Item, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        if (down().getId() === CAMPFIRE_BLOCK) {
            return false
        }
        val layer0: Block = level.getBlock(this, 0)
        val layer1: Block = level.getBlock(this, 1)
        blockFace = if (player != null) player.getDirection().getOpposite() else null
        val defaultLayerCheck = block is BlockWater && block.isSourceOrFlowingDown() || block is BlockIceFrosted
        val layer1Check = layer1 is BlockWater && layer1.isSourceOrFlowingDown() || layer1 is BlockIceFrosted
        if (defaultLayerCheck || layer1Check) {
            isExtinguished = true
            this.level.addSound(this, Sound.RANDOM_FIZZ, 0.5f, 2.2f)
            this.level.setBlock(this, 1, if (defaultLayerCheck) block else layer1, false, false)
        } else {
            this.level.setBlock(this, 1, Block.get(BlockID.AIR), false, false)
        }
        this.level.setBlock(block, this, true, true)
        try {
            val nbt = CompoundTag()
            if (item.hasCustomBlockData()) {
                val customData: Map<String, Tag> = item.getCustomBlockData().getTags()
                for (tag in customData.entrySet()) {
                    nbt.put(tag.getKey(), tag.getValue())
                }
            }
            createBlockEntity(nbt)
        } catch (e: Exception) {
            log.warn("Failed to create the block entity {} at {}", blockEntityType, getLocation(), e)
            level.setBlock(layer0, 0, layer0, true)
            level.setBlock(layer1, 0, layer1, true)
            return false
        }
        this.level.updateAround(this)
        return true
    }

    @Override
    override fun hasEntityCollision(): Boolean {
        return true
    }

    @Override
    override fun onEntityCollide(entity: Entity) {
        if (!isExtinguished && !entity.isSneaking()) {
            entity.attack(EntityDamageByBlockEvent(this, entity, EntityDamageEvent.DamageCause.FIRE, 1))
        }
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isExtinguished) {
                val layer1: Block = getLevelBlockAtLayer(1)
                if (layer1 is BlockWater || layer1 is BlockIceFrosted) {
                    isExtinguished = true
                    this.level.setBlock(this, this, true, true)
                    this.level.addSound(this, Sound.RANDOM_FIZZ, 0.5f, 2.2f)
                }
            }
            return type
        }
        return 0
    }

    @Override
    override fun onActivate(@Nonnull item: Item, @Nullable player: Player?): Boolean {
        if (item.getId() === BlockID.AIR || item.getCount() <= 0) {
            return false
        }
        val campfire: BlockEntityCampfire = getOrCreateBlockEntity()
        var itemUsed = false
        if (item.isShovel() && !isExtinguished) {
            isExtinguished = true
            this.level.setBlock(this, this, true, true)
            this.level.addSound(this, Sound.RANDOM_FIZZ, 0.5f, 2.2f)
            itemUsed = true
        } else if (item.getId() === ItemID.FLINT_AND_STEEL) {
            item.useOn(this)
            isExtinguished = false
            this.level.setBlock(this, this, true, true)
            campfire.scheduleUpdate()
            this.level.addSound(this, Sound.FIRE_IGNITE)
            itemUsed = true
        }
        val cloned: Item = item.clone()
        cloned.setCount(1)
        val inventory: CampfireInventory = campfire.getInventory()
        if (inventory.canAddItem(cloned)) {
            val recipe: CampfireRecipe = this.level.getServer().getCraftingManager().matchCampfireRecipe(cloned)
            if (recipe != null) {
                inventory.addItem(cloned)
                item.setCount(item.getCount() - 1)
                return true
            }
        }
        return itemUsed
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun onProjectileHit(@Nonnull projectile: Entity, @Nonnull position: Position?, @Nonnull motion: Vector3?): Boolean {
        if (projectile.isOnFire() && projectile is EntityArrow && isExtinguished) {
            isExtinguished = false
            level.setBlock(this, this, true)
            return true
        }
        return false
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Override
    override val maxY: Double
        get() = y + 0.4371948

    @Override
    protected override fun recalculateCollisionBoundingBox(): AxisAlignedBB {
        return SimpleAxisAlignedBB(x, y, z, x + 1, y + 1, z + 1)
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.SPRUCE_BLOCK_COLOR
    var isExtinguished: Boolean
        get() = getBooleanValue(EXTINGUISHED)
        set(extinguished) {
            setBooleanValue(EXTINGUISHED, extinguished)
        }

    @get:Override
    @set:Since("1.3.0.0-PN")
    @set:PowerNukkitOnly
    @set:Override
    var blockFace: BlockFace
        get() = getPropertyValue(DIRECTION)
        set(face) {
            setPropertyValue(DIRECTION, face)
        }

    @get:Override
    override val name: String
        get() = "Campfire"

    @Override
    override fun toItem(): Item {
        return ItemCampfire()
    }

    @Override
    override fun hasComparatorInputOverride(): Boolean {
        return true
    }

    @get:Override
    override val comparatorInputOverride: Int
        get() {
            val blockEntity: BlockEntityCampfire = getBlockEntity()
            return if (blockEntity != null) {
                ContainerInventory.calculateRedstone(blockEntity.getInventory())
            } else super.getComparatorInputOverride()
        }

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @Override
    override fun canBePulled(): Boolean {
        return false
    }

    @Override
    override fun canBePushed(): Boolean {
        return false
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EXTINGUISHED: BooleanBlockProperty = BooleanBlockProperty("extinguished", false)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(DIRECTION, EXTINGUISHED)
    }
}