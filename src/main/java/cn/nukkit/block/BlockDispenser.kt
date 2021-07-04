package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author CreeperFace
 * @since 15.4.2017
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
class BlockDispenser @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta), RedstoneComponent, Faceable, BlockEntityHolder<BlockEntityEjectable?> {
    @Override
    override fun hasComparatorInputOverride(): Boolean {
        return true
    }

    @get:Override
    override val name: String
        get() = "Dispenser"

    @get:Override
    override val id: Int
        get() = DISPENSER

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
        get() = BlockEntity.DISPENSER

    @get:Override
    override val hardness: Double
        get() = 3.5

    @get:Override
    override val resistance: Double
        get() = 3.5

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityEjectable?>
        get() = BlockEntityDispenser::class.java

    @Override
    override fun toItem(): Item {
        return ItemBlock(this, 0)
    }

    @get:Override
    override val comparatorInputOverride: Int
        get() {
            val blockEntity: InventoryHolder = getBlockEntity()
            return if (blockEntity != null) {
                ContainerInventory.calculateRedstone(blockEntity.getInventory())
            } else 0
        }
    var isTriggered: Boolean
        get() = this.getDamage() and 8 > 0
        set(value) {
            var i = 0
            i = i or blockFace.getIndex()
            if (value) {
                i = i or 8
            }
            this.setDamage(i)
        }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        if (player == null) {
            return false
        }
        val blockEntity: InventoryHolder = getBlockEntity() ?: return false
        player.addWindow(blockEntity.getInventory())
        return true
    }

    @PowerNukkitDifference(info = "BlockData is implemented.", since = "1.4.0.0-PN")
    @Override
    override fun place(@Nonnull item: Item, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (player != null) {
            if (Math.abs(player.x - this.x) < 2 && Math.abs(player.z - this.z) < 2) {
                val y: Double = player.y + player.getEyeHeight()
                if (y - y > 2) {
                    this.setDamage(BlockFace.UP.getIndex())
                } else if (y - y > 0) {
                    this.setDamage(BlockFace.DOWN.getIndex())
                } else {
                    this.setDamage(player.getHorizontalFacing().getOpposite().getIndex())
                }
            } else {
                this.setDamage(player.getHorizontalFacing().getOpposite().getIndex())
            }
        }
        val nbt: CompoundTag = CompoundTag().putList(ListTag("Items"))
        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName())
        }
        if (item.hasCustomBlockData()) {
            val customData: Map<String, Tag> = item.getCustomBlockData().getTags()
            for (tag in customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue())
            }
        }
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null
    }

    @PowerNukkitDifference(info = "Disables the triggered state, when the block is no longer powered + use #isGettingPower() method.", since = "1.4.0.0-PN")
    @Override
    override fun onUpdate(type: Int): Int {
        if (!this.level.getServer().isRedstoneEnabled()) {
            return 0
        }
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            dispense()
            return type
        } else if (type == Level.BLOCK_UPDATE_REDSTONE || type == Level.BLOCK_UPDATE_NORMAL) {
            val triggered = isTriggered
            if (this.isGettingPower() && !triggered) {
                isTriggered = true
                this.level.setBlock(this, this, false, false)
                level.scheduleUpdate(this, this, 4)
            } else if (!this.isGettingPower() && triggered) {
                isTriggered = false
                this.level.setBlock(this, this, false, false)
            }
            return type
        }
        return 0
    }

    @PowerNukkitDifference(info = "Trigger observer on dispense fail (with #setDirty()).", since = "1.4.0.0-PN")
    fun dispense() {
        val blockEntity: InventoryHolder = getBlockEntity() ?: return
        val rand: Random = ThreadLocalRandom.current()
        var r = 1
        var slot = -1
        var target: Item? = null
        val inv: Inventory = blockEntity.getInventory()
        for (entry in inv.getContents().entrySet()) {
            val item: Item = entry.getValue()
            if (!item.isNull() && rand.nextInt(r++) === 0) {
                target = item
                slot = entry.getKey()
            }
        }
        val pk = LevelEventPacket()
        val facing: BlockFace = blockFace
        pk.x = 0.5f + facing.getXOffset() * 0.7f
        pk.y = 0.5f + facing.getYOffset() * 0.7f
        pk.z = 0.5f + facing.getZOffset() * 0.7f
        if (target == null) {
            this.level.addSound(this, Sound.RANDOM_CLICK, 1.0f, 1.2f)
            getBlockEntity().setDirty()
            return
        } else {
            if (getDispenseBehavior(target) !is DropperDispenseBehavior
                    && getDispenseBehavior(target) !is FlintAndSteelDispenseBehavior) this.level.addSound(this, Sound.RANDOM_CLICK, 1.0f, 1.0f)
        }
        pk.evid = LevelEventPacket.EVENT_PARTICLE_SHOOT
        pk.data = 7
        this.level.addChunkPacket(getChunkX(), getChunkZ(), pk)
        val origin: Item = target
        target = target.clone()
        val behavior: DispenseBehavior = getDispenseBehavior(target)
        val result: Item = behavior.dispense(this, facing, target)
        target.count--
        inv.setItem(slot, target)
        if (result != null) {
            if (result.getId() !== origin.getId() || result.getDamage() !== origin.getDamage()) {
                val fit: Array<Item> = inv.addItem(result)
                if (fit.size > 0) {
                    for (drop in fit) {
                        this.level.dropItem(this, drop)
                    }
                }
            } else {
                inv.setItem(slot, result)
            }
        }
    }

    protected fun getDispenseBehavior(item: Item?): DispenseBehavior {
        return DispenseBehaviorRegister.getBehavior(item.getId())
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN
    val dispensePosition: Vector3
        get() {
            val facing: BlockFace = blockFace
            return this.add(
                    0.5 + 0.7 * facing.getXOffset(),
                    0.5 + 0.7 * facing.getYOffset(),
                    0.5 + 0.7 * facing.getZOffset()
            )
        }

    @get:Override
    val blockFace: BlockFace
        get() = BlockFace.fromIndex(this.getDamage() and 0x07)

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val TRIGGERED: BooleanBlockProperty = BooleanBlockProperty("triggered_bit", false)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(FACING_DIRECTION, TRIGGERED)
    }
}