package cn.nukkit.block

import kotlin.jvm.JvmOverloads
import kotlin.Throws
import cn.nukkit.block.BlockRedstoneComparator.Mode

class BlockPistonHeadSticky @JvmOverloads constructor(meta: Int = 0) : BlockPistonHead(meta) {
    @get:Override
    override val id: Int
        get() = PISTON_HEAD_STICKY

    @get:Override
    override val name: String
        get() = "Sticky Piston Head"
}