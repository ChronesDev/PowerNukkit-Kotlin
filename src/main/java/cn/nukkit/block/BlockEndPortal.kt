package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

class BlockEndPortal @JvmOverloads constructor(meta: Int = 0) : BlockFlowable(0) {
    @get:Override
    override val name: String
        get() = "End Portal Block"

    @get:Override
    override val id: Int
        get() = END_PORTAL

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = CommonBlockProperties.EMPTY_PROPERTIES

    @Override
    override fun canPassThrough(): Boolean {
        return true
    }

    @Override
    override fun isBreakable(item: Item?): Boolean {
        return false
    }

    @get:Override
    override val hardness: Double
        get() = (-1).toDouble()

    @get:Override
    override val resistance: Double
        get() = 18000000

    @get:Override
    override val lightLevel: Int
        get() = 15

    @Override
    override fun hasEntityCollision(): Boolean {
        return true
    }

    @get:Override
    @get:Since("1.2.1.0-PN")
    @get:PowerNukkitOnly("NukkitX returns null")
    override val collisionBoundingBox: AxisAlignedBB
        get() = this

    @get:Override
    override val color: BlockColor
        get() = BlockColor.BLACK_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @Override
    override fun canBeFlowedInto(): Boolean {
        return false
    }

    @Override
    override fun toItem(): Item {
        return ItemBlock(Block.get(BlockID.AIR))
    }
}