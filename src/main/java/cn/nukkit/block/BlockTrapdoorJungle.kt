package cn.nukkit.block

import cn.nukkit.utils.BlockColor

class BlockTrapdoorJungle @JvmOverloads constructor(meta: Int = 0) : BlockTrapdoor(meta) {
    @get:Override
    override val id: Int
        get() = JUNGLE_TRAPDOOR

    @get:Override
    override val name: String
        get() = "Jungle Trapdoor"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.DIRT_BLOCK_COLOR
}