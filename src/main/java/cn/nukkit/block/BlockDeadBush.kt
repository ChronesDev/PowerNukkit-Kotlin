package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/12/2
 */
class BlockDeadBush @JvmOverloads constructor(meta: Int = 0) : BlockFlowable(0) {
    @get:Override
    override val name: String
        get() = "Dead Bush"

    @get:Override
    override val id: Int
        get() = DEAD_BUSH

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = CommonBlockProperties.EMPTY_PROPERTIES

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @Override
    override fun canBeReplaced(): Boolean {
        return true
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (isSupportValid) {
            this.getLevel().setBlock(block, this, true, true)
            return true
        }
        return false
    }

    private val isSupportValid: Boolean
        private get() = when (down().getId()) {
            SAND, TERRACOTTA, STAINED_TERRACOTTA, DIRT, PODZOL -> true
            else -> false
        }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isSupportValid) {
                this.getLevel().useBreakOn(this)
                return Level.BLOCK_UPDATE_NORMAL
            }
        }
        return 0
    }

    @Override
    override fun getDrops(item: Item): Array<Item> {
        return if (item.isShears()) {
            arrayOf<Item>(
                    toItem()
            )
        } else {
            arrayOf<Item>(
                    ItemStick(0, Random().nextInt(3))
            )
        }
    }

    override val color: BlockColor
        get() = BlockColor.FOLIAGE_BLOCK_COLOR
}