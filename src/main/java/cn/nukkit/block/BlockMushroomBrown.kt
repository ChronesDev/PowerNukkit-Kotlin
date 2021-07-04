package cn.nukkit.block

import kotlin.jvm.JvmOverloads
import kotlin.Throws
import cn.nukkit.block.BlockRedstoneComparator.Mode

/**
 * @author Nukkit Project Team
 */
class BlockMushroomBrown : BlockMushroom {
    constructor() : super() {}
    constructor(meta: Int) : super(0) {}

    @get:Override
    override val name: String
        get() = "Brown Mushroom"

    @get:Override
    override val id: Int
        get() = BROWN_MUSHROOM

    @get:Override
    override val lightLevel: Int
        get() = 1

    @get:Override
    protected override val type: Int
        protected get() = 0
}