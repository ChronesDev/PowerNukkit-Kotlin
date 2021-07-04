package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author xtypr
 * @since 2015/11/25
 */
class BlockStairsStoneBrick @JvmOverloads constructor(meta: Int = 0) : BlockStairs(meta) {
    @get:Override
    override val id: Int
        get() = STONE_BRICK_STAIRS

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val hardness: Double
        get() = 1.5

    @get:Override
    override val resistance: Double
        get() = 30

    @get:Override
    override val name: String
        get() = "Stone Brick Stairs"

    @get:Override
    @get:PowerNukkitDifference(info = "Fixed the color", since = "1.3.0.0-PN")
    override val color: BlockColor
        get() = BlockColor.STONE_BLOCK_COLOR
}