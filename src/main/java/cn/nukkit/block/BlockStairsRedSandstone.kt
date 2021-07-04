package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author CreeperFace
 * @since 26. 11. 2016
 */
class BlockStairsRedSandstone @JvmOverloads constructor(meta: Int = 0) : BlockStairs(meta) {
    @get:Override
    override val id: Int
        get() = RED_SANDSTONE_STAIRS

    @get:Override
    override val hardness: Double
        get() = 0.8

    @get:Override
    override val resistance: Double
        get() = 4

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val name: String
        get() = "Red Sandstone Stairs"

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.ORANGE_BLOCK_COLOR
}