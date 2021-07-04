package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/11/24
 */
class BlockCarpet @JvmOverloads constructor(meta: Int = 0) : BlockFlowable(meta) {
    constructor(dyeColor: DyeColor) : this(dyeColor.getWoolData()) {}

    @get:Override
    override val id: Int
        get() = CARPET

    @get:Override
    @get:Nonnull
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val hardness: Double
        get() = 0.1

    @get:Override
    override val resistance: Double
        get() = 0.5

    @get:Override
    override val isSolid: Boolean
        get() = true

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace?): Boolean {
        return false
    }

    @get:Override
    override val name: String
        get() = DyeColor.getByWoolData(getDamage()).toString() + " Carpet"

    @Override
    override fun canPassThrough(): Boolean {
        return false
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @Override
    protected override fun recalculateBoundingBox(): AxisAlignedBB {
        return this
    }

    @get:Override
    override val maxY: Double
        get() = this.y + 0.0625

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        val down: Block = this.down()
        if (down.getId() !== Item.AIR) {
            this.getLevel().setBlock(block, this, true, true)
            return true
        }
        return false
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() === Item.AIR) {
                this.getLevel().useBreakOn(this)
                return Level.BLOCK_UPDATE_NORMAL
            }
        }
        return 0
    }

    @get:Override
    override val color: BlockColor
        get() = dyeColor.getColor()

    @set:Since("1.5.0.0-PN")
    @set:PowerNukkitOnly
    var dyeColor: DyeColor
        get() = getPropertyValue(CommonBlockProperties.COLOR)
        set(color) {
            setPropertyValue(CommonBlockProperties.COLOR, color)
        }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = CommonBlockProperties.COLOR_BLOCK_PROPERTIES
    }
}