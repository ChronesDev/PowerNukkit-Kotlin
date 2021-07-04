package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Angelic47 (Nukkit Project)
 */
class BlockLeaves @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta) {
    @get:Override
    override val id: Int
        get() = LEAVES

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = OLD_LEAF_PROPERTIES

    @get:Override
    override val hardness: Double
        get() = 0.2

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_HOE

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var type: WoodType
        get() = getPropertyValue(OLD_LEAF_TYPE)
        set(type) {
            setPropertyValue(OLD_LEAF_TYPE, type)
        }

    @get:Override
    override val name: String
        get() = type.getEnglishName().toString() + " Leaves"

    @get:Override
    override val burnChance: Int
        get() = 30

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Override
    override val burnAbility: Int
        get() = 60

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        isPersistent = true
        this.getLevel().setBlock(this, this, true)
        return true
    }

    @Override
    override fun getDrops(item: Item): Array<Item> {
        if (item.isShears()) {
            return arrayOf<Item>(
                    toItem()
            )
        }
        val drops: List<Item> = ArrayList(1)
        val fortuneEnchantment: Enchantment = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING)
        val fortune = if (fortuneEnchantment != null) fortuneEnchantment.getLevel() else 0
        val appleOdds: Int
        val stickOdds: Int
        val saplingOdds: Int
        when (fortune) {
            0 -> {
                appleOdds = 200
                stickOdds = 50
                saplingOdds = if (type === WoodType.JUNGLE) 40 else 20
            }
            1 -> {
                appleOdds = 180
                stickOdds = 45
                saplingOdds = if (type === WoodType.JUNGLE) 36 else 16
            }
            2 -> {
                appleOdds = 160
                stickOdds = 40
                saplingOdds = if (type === WoodType.JUNGLE) 32 else 12
            }
            else -> {
                appleOdds = 120
                stickOdds = 30
                saplingOdds = if (type === WoodType.JUNGLE) 24 else 10
            }
        }
        val random: ThreadLocalRandom = ThreadLocalRandom.current()
        if (canDropApple() && random.nextInt(appleOdds) === 0) {
            drops.add(Item.get(ItemID.APPLE))
        }
        if (random.nextInt(stickOdds) === 0) {
            drops.add(Item.get(ItemID.STICK))
        }
        if (random.nextInt(saplingOdds) === 0) {
            drops.add(sapling)
        }
        return drops.toArray(Item.EMPTY_ARRAY)
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (isCheckDecay) {
                if (isPersistent || findLog(this, 7, null)) {
                    isCheckDecay = false
                    getLevel().setBlock(this, this, false, false)
                } else {
                    val ev = LeavesDecayEvent(this)
                    Server.getInstance().getPluginManager().callEvent(ev)
                    if (!ev.isCancelled()) {
                        getLevel().useBreakOn(this)
                    }
                }
                return type
            }
        } else if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!isCheckDecay) {
                isCheckDecay = true
                getLevel().setBlock(this, this, false, false)
            }

            // Slowly propagates the need to update instead of peaking down the TPS for huge trees
            for (side in BlockFace.values()) {
                val other: Block = getSide(side)
                if (other is BlockLeaves) {
                    val otherLeave = other
                    if (!otherLeave.isCheckDecay) {
                        getLevel().scheduleUpdate(otherLeave, 2)
                    }
                }
            }
            return type
        }
        return type
    }

    private fun findLog(current: Block, distance: Int, visited: Long2LongMap?): Boolean {
        var visited: Long2LongMap? = visited
        if (visited == null) {
            visited = Long2LongOpenHashMap()
            visited.defaultReturnValue(-1)
        }
        if (current is BlockWood) {
            return true
        }
        if (distance == 0 || current !is BlockLeaves) {
            return false
        }
        val hash: Long = Hash.hashBlock(current)
        if (visited.get(hash) >= distance) {
            return false
        }
        visited.put(hash, distance)
        for (face in VISIT_ORDER) {
            if (findLog(current.getSide(face), distance - 1, visited)) {
                return true
            }
        }
        return false
    }

    var isCheckDecay: Boolean
        get() = getBooleanValue(UPDATE)
        set(checkDecay) {
            setBooleanValue(UPDATE, checkDecay)
        }
    var isPersistent: Boolean
        get() = getBooleanValue(PERSISTENT)
        set(persistent) {
            setBooleanValue(PERSISTENT, persistent)
        }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.FOLIAGE_BLOCK_COLOR

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    protected fun canDropApple(): Boolean {
        return type === WoodType.OAK
    }

    protected val sapling: Item
        protected get() = Item.get(BlockID.SAPLING, getIntValue(OLD_LEAF_TYPE))

    @Override
    override fun diffusesSkyLight(): Boolean {
        return true
    }

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @Override
    override fun sticksToPiston(): Boolean {
        return false
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val OLD_LEAF_TYPE: ArrayBlockProperty<WoodType> = ArrayBlockProperty("old_leaf_type", true, arrayOf<WoodType>(
                WoodType.OAK, WoodType.SPRUCE, WoodType.BIRCH, WoodType.JUNGLE
        ))

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PERSISTENT: BooleanBlockProperty = BooleanBlockProperty("persistent_bit", false)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val UPDATE: BooleanBlockProperty = BooleanBlockProperty("update_bit", false)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val OLD_LEAF_PROPERTIES: BlockProperties = BlockProperties(OLD_LEAF_TYPE, PERSISTENT, UPDATE)
        private val VISIT_ORDER: Array<BlockFace> = arrayOf<BlockFace>(
                BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.DOWN, BlockFace.UP
        )

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "Magic value. Use the accessors instead")
        val OAK = 0

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "Magic value. Use the accessors instead")
        val SPRUCE = 1

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "Magic value. Use the accessors instead")
        val BIRCH = 2

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "Magic value. Use the accessors instead")
        val JUNGLE = 3
    }
}