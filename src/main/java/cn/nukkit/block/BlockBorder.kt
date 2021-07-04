package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
class BlockBorder @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockWallBase(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = BORDER_BLOCK

    @get:Override
    override val hardness: Double
        get() = (-1).toDouble()

    @get:Override
    override val resistance: Double
        get() = 18000000

    @get:Override
    override val name: String
        get() = "Border Block"

    @Override
    override fun isBreakable(item: Item?): Boolean {
        return false
    }

    @Override
    override fun canBePushed(): Boolean {
        return false
    }

    @Override
    override fun canBePulled(): Boolean {
        return false
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        return if (player != null && (!player.isCreative() || !player.isOp())) {
            false
        } else super.place(item, block, target, face, fx, fy, fz, player)
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isBreakable(vector: Vector3?, layer: Int, face: BlockFace?, item: Item?, @Nullable player: Player?, setBlockDestroy: Boolean): Boolean {
        return if (player != null && (!player.isCreative() || !player.isOp())) {
            false
        } else super.isBreakable(vector, layer, face, item, player, setBlockDestroy)
    }

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return Item.EMPTY_ARRAY
    }

    @Override
    protected override fun recalculateBoundingBox(): AxisAlignedBB {
        val aabb: AxisAlignedBB = super.recalculateBoundingBox()
        aabb.setMinY(Double.MIN_VALUE)
        aabb.setMaxY(Double.MAX_VALUE)
        return aabb
    }
}