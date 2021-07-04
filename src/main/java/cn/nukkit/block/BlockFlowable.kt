package cn.nukkit.block

import cn.nukkit.math.AxisAlignedBB

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class BlockFlowable protected constructor(meta: Int) : BlockTransparentMeta(meta) {
    @Override
    override fun canBeFlowedInto(): Boolean {
        return true
    }

    @Override
    override fun canPassThrough(): Boolean {
        return true
    }

    @get:Override
    override val hardness: Double
        get() = 0

    @get:Override
    override val resistance: Double
        get() = 0

    @get:Override
    override val isSolid: Boolean
        get() = false

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @Override
    override fun sticksToPiston(): Boolean {
        return false
    }

    @Override
    protected override fun recalculateBoundingBox(): AxisAlignedBB? {
        return null
    }
}