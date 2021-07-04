package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/11/23
 */
class BlockFlower @JvmOverloads constructor(meta: Int = 0) : BlockFlowable(meta) {
    @get:Override
    override val id: Int
        get() = FLOWER

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = flowerType.getEnglishName()

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var flowerType: SmallFlowerType
        get() = getPropertyValue(RED_FLOWER_TYPE)
        set(flowerType) {
            setPropertyValue(RED_FLOWER_TYPE, flowerType)
        }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    protected fun setOnSingleFlowerType(acceptsOnly: SmallFlowerType, attemptedToSet: SmallFlowerType?) {
        if (attemptedToSet == null || attemptedToSet === acceptsOnly) {
            return
        }
        val persistenceName: String = getPersistenceName()
        throw InvalidBlockPropertyValueException(
                ArrayBlockProperty(persistenceName + "_type", false, arrayOf<SmallFlowerType>(acceptsOnly)),
                acceptsOnly,
                attemptedToSet,
                persistenceName + " only accepts " + acceptsOnly.name().toLowerCase()
        )
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    fun canPlantOn(block: Block): Boolean {
        return isSupportValid(block)
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        val down: Block = this.down()
        if (canPlantOn(down)) {
            this.getLevel().setBlock(block, this, true)
            return true
        }
        return false
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will break on normal update if the supporting block is invalid")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!canPlantOn(down())) {
                this.getLevel().useBreakOn(this)
                return Level.BLOCK_UPDATE_NORMAL
            }
        }
        return 0
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.FOLIAGE_BLOCK_COLOR

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (item.isFertilizer()) { //Bone meal
            if (player != null && player.gamemode and 0x01 === 0) {
                item.count--
            }
            this.level.addParticle(BoneMealParticle(this))
            for (i in 0..7) {
                val vec: Vector3 = this.add(
                        ThreadLocalRandom.current().nextInt(-3, 4),
                        ThreadLocalRandom.current().nextInt(-1, 2),
                        ThreadLocalRandom.current().nextInt(-3, 4))
                if (level.getBlock(vec).getId() === AIR && level.getBlock(vec.down()).getId() === GRASS && vec.getY() >= 0 && vec.getY() < 256) {
                    if (ThreadLocalRandom.current().nextInt(10) === 0) {
                        this.level.setBlock(vec, uncommonFlower, true)
                    } else {
                        this.level.setBlock(vec, get(id), true)
                    }
                }
            }
            return true
        }
        return false
    }

    protected val uncommonFlower: cn.nukkit.block.Block
        protected get() = get(DANDELION)

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val RED_FLOWER_TYPE: BlockProperty<SmallFlowerType> = ArrayBlockProperty("flower_type", true, arrayOf<SmallFlowerType>(
                SmallFlowerType.POPPY,
                SmallFlowerType.ORCHID,
                SmallFlowerType.ALLIUM,
                SmallFlowerType.HOUSTONIA,
                SmallFlowerType.TULIP_RED,
                SmallFlowerType.TULIP_ORANGE,
                SmallFlowerType.TULIP_WHITE,
                SmallFlowerType.TULIP_PINK,
                SmallFlowerType.OXEYE,
                SmallFlowerType.CORNFLOWER,
                SmallFlowerType.LILY_OF_THE_VALLEY
        ))

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(RED_FLOWER_TYPE)

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic value. Use FlowerType instead")
        val TYPE_POPPY = 0

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic value. Use FlowerType instead")
        val TYPE_BLUE_ORCHID = 1

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic value. Use FlowerType instead")
        val TYPE_ALLIUM = 2

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic value. Use FlowerType instead")
        val TYPE_AZURE_BLUET = 3

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic value. Use FlowerType instead")
        val TYPE_RED_TULIP = 4

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic value. Use FlowerType instead")
        val TYPE_ORANGE_TULIP = 5

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic value. Use FlowerType instead")
        val TYPE_WHITE_TULIP = 6

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic value. Use FlowerType instead")
        val TYPE_PINK_TULIP = 7

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic value. Use FlowerType instead")
        val TYPE_OXEYE_DAISY = 8
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun isSupportValid(block: Block): Boolean {
            return when (block.getId()) {
                GRASS, DIRT, FARMLAND, PODZOL -> true
                else -> false
            }
        }
    }
}