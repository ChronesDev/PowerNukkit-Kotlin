package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
class BlockDeny @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockSolid() {
    @get:Override
    override val id: Int
        get() = DENY

    @get:Override
    override val hardness: Double
        get() = (-1).toDouble()

    @get:Override
    override val resistance: Double
        get() = 18000000

    @get:Override
    override val name: String
        get() = "Deny"

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
}