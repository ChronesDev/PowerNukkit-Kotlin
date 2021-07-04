package cn.nukkit.block

import kotlin.jvm.JvmOverloads
import kotlin.Throws
import cn.nukkit.block.BlockRedstoneComparator.Mode

/**
 * @author Pub4Game
 * @since 03.01.2015
 */
class BlockMushroomRed : BlockMushroom {
    constructor() : super() {}
    constructor(meta: Int) : super(0) {}

    @get:Override
    override val name: String
        get() = "Red Mushroom"

    @get:Override
    override val id: Int
        get() = RED_MUSHROOM

    @get:Override
    protected override val type: Int
        protected get() = 1
}