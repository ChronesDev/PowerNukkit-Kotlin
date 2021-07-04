package cn.nukkit.block

import cn.nukkit.utils.DyeColor

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
class BlockTerracottaGlazedOrange @JvmOverloads constructor(meta: Int = 0) : BlockTerracottaGlazed(meta) {
    @get:Override
    override val id: Int
        get() = ORANGE_GLAZED_TERRACOTTA

    @get:Override
    override val name: String
        get() = "Orange Glazed Terracotta"
    val dyeColor: DyeColor
        get() = DyeColor.ORANGE
}