package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/12/1
 */
class BlockWaterLily @JvmOverloads constructor(meta: Int = 0) : BlockFlowable(0) {
    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = CommonBlockProperties.EMPTY_PROPERTIES

    @get:Override
    override val name: String
        get() = "Lily Pad"

    @get:Override
    override val id: Int
        get() = WATER_LILY

    @get:Override
    override val minX: Double
        get() = this.x + 0.0625

    @get:Override
    override val minZ: Double
        get() = this.z + 0.0625

    @get:Override
    override val maxX: Double
        get() = this.x + 0.9375

    @get:Override
    override val maxY: Double
        get() = this.y + 0.015625

    @get:Override
    override val maxZ: Double
        get() = this.z + 0.9375

    @Override
    protected override fun recalculateBoundingBox(): AxisAlignedBB {
        return this
    }

    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (target is BlockWater || target.getLevelBlockAtLayer(1) is BlockWater) {
            val up: Block = target.up()
            if (up.getId() === Block.AIR) {
                this.getLevel().setBlock(up, this, true, true)
                return true
            }
        }
        return false
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val down: Block = this.down()
            if (down !is BlockWater && down.getLevelBlockAtLayer(1) !is BlockWater
                    && down !is BlockIceFrosted && down.getLevelBlockAtLayer(1) !is BlockIceFrosted) {
                this.getLevel().useBreakOn(this)
                return Level.BLOCK_UPDATE_NORMAL
            }
        }
        return 0
    }

    @Override
    override fun toItem(): Item {
        return ItemBlock(this, 0)
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.FOLIAGE_BLOCK_COLOR

    @Override
    override fun canPassThrough(): Boolean {
        return false
    }

    @Override
    override fun canBeFlowedInto(): Boolean {
        return false
    }
}