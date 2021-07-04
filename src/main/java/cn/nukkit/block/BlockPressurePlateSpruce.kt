package cn.nukkit.block

import kotlin.jvm.JvmOverloads
import kotlin.Throws
import cn.nukkit.block.BlockRedstoneComparator.Mode

class BlockPressurePlateSpruce @JvmOverloads constructor(meta: Int = 0) : BlockPressurePlateWood(meta) {
    @get:Override
    override val id: Int
        get() = SPRUCE_PRESSURE_PLATE

    @get:Override
    override val name: String
        get() = "Spruce Pressure Plate"
}