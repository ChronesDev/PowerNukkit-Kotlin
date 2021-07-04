package cn.nukkit.block

import cn.nukkit.utils.BlockColor

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockStairsAcacia @JvmOverloads constructor(meta: Int = 0) : BlockStairsWood(meta) {
    @get:Override
    override val id: Int
        get() = ACACIA_WOOD_STAIRS

    @get:Override
    override val name: String
        get() = "Acacia Wood Stairs"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.ORANGE_BLOCK_COLOR
}