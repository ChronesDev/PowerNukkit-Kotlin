package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/11/23
 */
class BlockDoublePlant @JvmOverloads constructor(meta: Int = 0) : BlockFlowable(meta) {
    @get:Override
    override val id: Int
        get() = DOUBLE_PLANT

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var doublePlantType: DoublePlantType
        get() = getPropertyValue(DOUBLE_PLANT_TYPE)
        set(type) {
            setPropertyValue(DOUBLE_PLANT_TYPE, type)
        }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isTopHalf: Boolean
        get() = getBooleanValue(UPPER_BLOCK)
        set(topHalf) {
            setBooleanValue(UPPER_BLOCK, topHalf)
        }

    @Override
    override fun canBeReplaced(): Boolean {
        return doublePlantType.isReplaceable()
    }

    @get:Override
    override val name: String
        get() = doublePlantType.getEnglishName()

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Bottom part will break if the supporting block is invalid on normal update")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (isTopHalf) {
                // Top
                if (this.down().getId() !== DOUBLE_PLANT) {
                    this.getLevel().setBlock(this, Block.get(BlockID.AIR), false, true)
                    return Level.BLOCK_UPDATE_NORMAL
                }
            } else {
                // Bottom
                if (this.up().getId() !== DOUBLE_PLANT || !isSupportValid(down())) {
                    this.getLevel().useBreakOn(this)
                    return Level.BLOCK_UPDATE_NORMAL
                }
            }
        }
        return 0
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        val up: Block = up()
        if (up.getId() === AIR && isSupportValid(down())) {
            isTopHalf = false
            this.getLevel().setBlock(block, this, true, false) // If we update the bottom half, it will drop the item because there isn't a flower block above
            isTopHalf = true
            this.getLevel().setBlock(up, this, true, true)
            this.getLevel().updateAround(this)
            return true
        }
        return false
    }

    private fun isSupportValid(support: Block): Boolean {
        return when (support.getId()) {
            GRASS, DIRT, PODZOL, FARMLAND -> true
            else -> false
        }
    }

    @Override
    override fun onBreak(item: Item?): Boolean {
        val down: Block = down()
        if (isTopHalf) { // Top half
            this.getLevel().useBreakOn(down)
        } else {
            this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true)
        }
        return true
    }

    @Override
    override fun getDrops(item: Item): Array<Item> {
        if (isTopHalf) {
            return Item.EMPTY_ARRAY
        }
        when (doublePlantType) {
            GRASS, FERN -> {
                val dropSeeds = ThreadLocalRandom.current().nextInt(10) === 0
                if (item.isShears()) {
                    //todo enchantment
                    return if (dropSeeds) {
                        arrayOf<Item>(
                                Item.get(ItemID.WHEAT_SEEDS),
                                toItem()
                        )
                    } else {
                        arrayOf<Item>(
                                toItem()
                        )
                    }
                }
                return if (dropSeeds) {
                    arrayOf<Item>(
                            Item.get(ItemID.WHEAT_SEEDS)
                    )
                } else {
                    Item.EMPTY_ARRAY
                }
            }
        }
        return arrayOf<Item>(toItem())
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
            when (doublePlantType) {
                SUNFLOWER, SYRINGA, ROSE, PAEONIA -> {
                    if (player != null && player.gamemode and 0x01 === 0) {
                        item.count--
                    }
                    this.level.addParticle(BoneMealParticle(this))
                    this.level.dropItem(this, this.toItem())
                }
            }
            return true
        }
        return false
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val DOUBLE_PLANT_TYPE: ArrayBlockProperty<DoublePlantType> = ArrayBlockProperty(
                "double_plant_type", true, DoublePlantType::class.java
        )

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(DOUBLE_PLANT_TYPE, UPPER_BLOCK)

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", replaceWith = "DoublePlantType.SUNFLOWER", reason = "Magic values may change in future without backward compatibility.")
        val SUNFLOWER = 0

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", replaceWith = "DoublePlantType.LILAC", reason = "Magic values may change in future without backward compatibility.")
        val LILAC = 1

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", replaceWith = "DoublePlantType.TALL_GRASS", reason = "Magic values may change in future without backward compatibility.")
        val TALL_GRASS = 2

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", replaceWith = "DoublePlantType.LARGE_FERN", reason = "Magic values may change in future without backward compatibility.")
        val LARGE_FERN = 3

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", replaceWith = "DoublePlantType.ROSE_BUSH", reason = "Magic values may change in future without backward compatibility.")
        val ROSE_BUSH = 4

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", replaceWith = "DoublePlantType.PEONY", reason = "Magic values may change in future without backward compatibility.")
        val PEONY = 5

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", replaceWith = "CommonBlockProperties.UPPER_BLOCK", reason = "Magic values may change in future without backward compatibility.")
        val TOP_HALF_BITMASK = 0x8
    }
}