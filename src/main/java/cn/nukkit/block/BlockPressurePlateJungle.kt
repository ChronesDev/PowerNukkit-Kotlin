package cn.nukkit.block

import kotlin.jvm.JvmOverloads
import kotlin.Throws
import cn.nukkit.block.BlockRedstoneComparator.Mode

class BlockPressurePlateJungle @JvmOverloads constructor(meta: Int = 0) : BlockPressurePlateWood(meta) {
    @get:Override
    override val id: Int
        get() = JUNGLE_PRESSURE_PLATE

    @get:Override
    override val name: String
        get() = "Jungle Pressure Plate"
}