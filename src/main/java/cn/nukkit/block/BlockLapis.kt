package cn.nukkit.block

import cn.nukkit.item.ItemTool

/**
 * @author Angelic47 (Nukkit Project)
 */
class BlockLapis : BlockSolid() {
    @get:Override
    override val id: Int
        get() = LAPIS_BLOCK

    @get:Override
    override val name: String
        get() = "Lapis Lazuli Block"

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val hardness: Double
        get() = 3

    @get:Override
    override val resistance: Double
        get() = 5

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_STONE

    @get:Override
    override val color: BlockColor
        get() = BlockColor.LAPIS_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}