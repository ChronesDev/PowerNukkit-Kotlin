package cn.nukkit.block

import cn.nukkit.level.Level

/**
 * @author Angelic47 (Nukkit Project)
 */
class BlockLavaStill : BlockLava {
    constructor() : super(0) {}
    constructor(meta: Int) : super(meta) {}

    @get:Override
    override val id: Int
        get() = STILL_LAVA

    @get:Override
    override val name: String
        get() = "Still Lava"

    @Override
    override fun getBlock(meta: Int): BlockLiquid {
        return Block.get(BlockID.STILL_LAVA, meta) as BlockLiquid
    }

    @Override
    override fun onUpdate(type: Int): Int {
        return if (type != Level.BLOCK_UPDATE_SCHEDULED) {
            super.onUpdate(type)
        } else 0
    }
}