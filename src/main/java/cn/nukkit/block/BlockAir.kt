package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockAir : BlockTransparent() {
    @get:Override
    override val id: Int
        get() = AIR

    @get:Override
    override val name: String
        get() = "Air"

    @Override
    override fun canPassThrough(): Boolean {
        return true
    }

    @Override
    override fun isBreakable(item: Item?): Boolean {
        return false
    }

    @Override
    override fun canBeFlowedInto(): Boolean {
        return true
    }

    @Override
    override fun canBePlaced(): Boolean {
        return false
    }

    @Override
    override fun canBeReplaced(): Boolean {
        return true
    }

    @get:Override
    override val isSolid: Boolean
        get() = false

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace?): Boolean {
        return false
    }

    @get:Override
    override val boundingBox: AxisAlignedBB?
        get() = null

    @get:Override
    override val hardness: Double
        get() = 0

    @get:Override
    override val resistance: Double
        get() = 0

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.AIR_BLOCK_COLOR
}