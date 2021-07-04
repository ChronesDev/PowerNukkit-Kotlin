package cn.nukkit.block

import kotlin.jvm.JvmOverloads
import kotlin.Throws
import cn.nukkit.block.BlockRedstoneComparator.Mode

class BlockButtonJungle @JvmOverloads constructor(meta: Int = 0) : BlockButtonWooden(meta) {
    @get:Override
    override val id: Int
        get() = JUNGLE_BUTTON

    @get:Override
    override val name: String
        get() = "Jungle Button"
}