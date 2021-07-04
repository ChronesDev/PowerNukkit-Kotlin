package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockMoving @JvmOverloads constructor(meta: Int = 0) : BlockTransparent(), BlockEntityHolder<BlockEntityMovingBlock?> {
    @get:Override
    override val name: String
        get() = "MovingBlock"

    @get:Override
    override val id: Int
        get() = BlockID.MOVING_BLOCK

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.MOVING_BLOCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityMovingBlock?>
        get() = BlockEntityMovingBlock::class.java

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
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
    override fun isBreakable(item: Item?): Boolean {
        return false
    }

    @Override
    override fun canPassThrough(): Boolean {
        return true
    }

    @get:Override
    override val isSolid: Boolean
        get() = false
}