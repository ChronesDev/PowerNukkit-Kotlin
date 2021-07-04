package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockDoorIron @JvmOverloads constructor(meta: Int = 0) : BlockDoor(meta) {
    @get:Override
    override val name: String
        get() = "Iron Door Block"

    @get:Override
    override val id: Int
        get() = IRON_DOOR_BLOCK

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    override val hardness: Double
        get() = 5

    @get:Override
    override val resistance: Double
        get() = 25

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun toItem(): Item {
        return ItemDoorIron()
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.IRON_BLOCK_COLOR

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        return false
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}