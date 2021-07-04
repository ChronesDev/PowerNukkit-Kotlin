package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author xtypr
 * @since 2015/12/7
 */
@PowerNukkitDifference(info = "Extends BlockFenceBase instead of BlockFence only in PowerNukkit", since = "1.4.0.0-PN")
class BlockFenceNetherBrick @JvmOverloads constructor(meta: Int = 0) : BlockFenceBase(meta) {
    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val name: String
        get() = "Nether Brick Fence"

    @get:Override
    override val id: Int
        get() = NETHER_BRICK_FENCE

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val resistance: Double
        get() = 6

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val color: BlockColor
        get() = BlockColor.NETHERRACK_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    @get:Since("1.2.1.0-PN")
    override val burnChance: Int
        get() = 0

    @get:Override
    @get:Since("1.2.1.0-PN")
    override val burnAbility: Int
        get() = 0
}