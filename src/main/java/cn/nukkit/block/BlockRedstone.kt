package cn.nukkit.block

import cn.nukkit.Player

/*
 * @author Pub4Game
 * @since 2015/12/11
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
class BlockRedstone @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(0), RedstoneComponent {
    @get:Override
    override val id: Int
        get() = REDSTONE_BLOCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = CommonBlockProperties.EMPTY_PROPERTIES

    @get:Override
    override val resistance: Double
        get() = 10

    @get:Override
    override val hardness: Double
        get() = 5

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val name: String
        get() = "Redstone Block"

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val color: BlockColor
        get() = BlockColor.REDSTONE_BLOCK_COLOR

    @Override
    @PowerNukkitOnly
    @PowerNukkitDifference(info = "Update around redstone", since = "1.4.0.0-PN")
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        if (super.place(item, block, target, face, fx, fy, fz, player)) {
            updateAroundRedstone()
            return true
        }
        return false
    }

    @Override
    @PowerNukkitOnly
    @PowerNukkitDifference(info = "Update around redstone", since = "1.4.0.0-PN")
    override fun onBreak(item: Item?): Boolean {
        if (!super.onBreak(item)) {
            return false
        }
        updateAroundRedstone()
        return true
    }

    @get:Override
    override val isPowerSource: Boolean
        get() = true

    @Override
    override fun getWeakPower(face: BlockFace?): Int {
        return 15
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}