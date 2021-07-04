package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class BlockStairs protected constructor(meta: Int) : BlockTransparentMeta(meta), Faceable {
    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val minY: Double
        get() = this.y + if (isUpsideDown) 0.5 else 0

    @get:Override
    override val maxY: Double
        get() = this.y + if (isUpsideDown) 1 else 0.5

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace): Boolean {
        return side === BlockFace.UP && isUpsideDown || side === BlockFace.DOWN && !isUpsideDown
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        if (player != null) {
            blockFace = player.getDirection()
        }
        if (fy > 0.5 && face !== BlockFace.UP || face === BlockFace.DOWN) {
            isUpsideDown = true
        }
        this.getLevel().setBlock(block, this, true, true)
        return true
    }

    @Override
    override fun collidesWithBB(bb: AxisAlignedBB): Boolean {
        val face: BlockFace = blockFace
        var minSlabY = 0.0
        var maxSlabY = 0.5
        var minHalfSlabY = 0.5
        var maxHalfSlabY = 1.0
        if (isUpsideDown) {
            minSlabY = 0.5
            maxSlabY = 1.0
            minHalfSlabY = 0.0
            maxHalfSlabY = 0.5
        }
        return if (bb.intersectsWith(SimpleAxisAlignedBB(
                        this.x,
                        this.y + minSlabY,
                        this.z,
                        this.x + 1,
                        this.y + maxSlabY,
                        this.z + 1
                ))) {
            true
        } else when (face) {
            EAST -> bb.intersectsWith(SimpleAxisAlignedBB(
                    this.x + 0.5,
                    this.y + minHalfSlabY,
                    this.z,
                    this.x + 1,
                    this.y + maxHalfSlabY,
                    this.z + 1
            ))
            WEST -> bb.intersectsWith(SimpleAxisAlignedBB(
                    this.x,
                    this.y + minHalfSlabY,
                    this.z,
                    this.x + 0.5,
                    this.y + maxHalfSlabY,
                    this.z + 1
            ))
            SOUTH -> bb.intersectsWith(SimpleAxisAlignedBB(
                    this.x,
                    this.y + minHalfSlabY,
                    this.z + 0.5,
                    this.x + 1,
                    this.y + maxHalfSlabY,
                    this.z + 1
            ))
            NORTH -> bb.intersectsWith(SimpleAxisAlignedBB(
                    this.x,
                    this.y + minHalfSlabY,
                    this.z,
                    this.x + 1,
                    this.y + maxHalfSlabY,
                    this.z + 0.5
            ))
            else -> false
        }
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isUpsideDown: Boolean
        get() = getBooleanValue(UPSIDE_DOWN)
        set(upsideDown) {
            setBooleanValue(UPSIDE_DOWN, upsideDown)
        }

    @get:Override
    @get:PowerNukkitDifference(info = "Was returning the wrong face", since = "1.3.0.0-PN")
    @set:Override
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var blockFace: BlockFace
        get() = getPropertyValue(STAIRS_DIRECTION)
        set(face) {
            setPropertyValue(STAIRS_DIRECTION, face)
        }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val UPSIDE_DOWN: BooleanBlockProperty = BooleanBlockProperty("upside_down_bit", false)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val STAIRS_DIRECTION: BlockProperty<BlockFace> = ArrayBlockProperty("weirdo_direction", false, arrayOf<BlockFace>(
                BlockFace.EAST, BlockFace.WEST,
                BlockFace.SOUTH, BlockFace.NORTH
        )).ordinal(true)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(STAIRS_DIRECTION, UPSIDE_DOWN)
    }
}