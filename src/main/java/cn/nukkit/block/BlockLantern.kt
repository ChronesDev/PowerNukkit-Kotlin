package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockLantern @PowerNukkitOnly constructor(meta: Int) : BlockFlowable(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = LANTERN

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Lantern"
    private val isBlockAboveValid: Boolean
        private get() {
            val support: Block = up()
            return when (support.getId()) {
                CHAIN_BLOCK, IRON_BARS, HOPPER_BLOCK -> true
                else -> {
                    if (support is BlockWallBase || support is BlockFence) {
                        return true
                    }
                    if (support is BlockSlab && !support.isOnTop()) {
                        return true
                    }
                    if (support is BlockStairs && !support.isUpsideDown()) {
                        true
                    } else BlockLever.isSupportValid(support, BlockFace.DOWN)
                }
            }
        }
    private val isBlockUnderValid: Boolean
        private get() {
            val support: Block = down()
            if (support.getId() === HOPPER_BLOCK) {
                return true
            }
            return if (support is BlockWallBase || support is BlockFence) {
                true
            } else BlockLever.isSupportValid(support, BlockFace.UP)
        }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (this.getLevelBlock() is BlockLiquid || this.getLevelBlockAtLayer(1) is BlockLiquid) {
            return false
        }
        val hanging = face !== BlockFace.UP && isBlockAboveValid && (!isBlockUnderValid || face === BlockFace.DOWN)
        if (!isBlockUnderValid && !hanging) {
            return false
        }
        isHanging = hanging
        this.getLevel().setBlock(this, this, true, true)
        return true
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isHanging) {
                if (!isBlockUnderValid) {
                    level.useBreakOn(this, ItemTool.getBestTool(toolType))
                }
            } else if (!isBlockAboveValid) {
                level.useBreakOn(this, ItemTool.getBestTool(toolType))
            }
            return type
        }
        return 0
    }

    @get:Override
    override val lightLevel: Int
        get() = 15

    @get:Override
    override val resistance: Double
        get() = 3.5

    @get:Override
    override val hardness: Double
        get() = 3.5

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val minX: Double
        get() = x + 5.0 / 16

    @get:Override
    override val minY: Double
        get() = y + if (!isHanging) 0 else 1.0 / 16

    @get:Override
    override val minZ: Double
        get() = z + 5.0 / 16

    @get:Override
    override val maxX: Double
        get() = x + 11.0 / 16

    @get:Override
    override val maxY: Double
        get() = y + if (!isHanging) 7.0 / 16 else 8.0 / 16

    @get:Override
    override val maxZ: Double
        get() = z + 11.0 / 16

    @Override
    override fun canPassThrough(): Boolean {
        return false
    }

    @Override
    protected override fun recalculateBoundingBox(): AxisAlignedBB {
        return this
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.IRON_BLOCK_COLOR

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isHanging: Boolean
        get() = getBooleanValue(HANGING)
        set(hanging) {
            setBooleanValue(HANGING, hanging)
        }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val HANGING: BooleanBlockProperty = BooleanBlockProperty("hanging", false)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(HANGING)
    }
}