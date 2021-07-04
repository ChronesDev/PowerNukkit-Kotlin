package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author PikyCZ
 */
class BlockEndGateway : BlockSolid() {
    @get:Override
    override val name: String
        get() = "End Gateway"

    @get:Override
    override val id: Int
        get() = END_GATEWAY

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
    override val color: BlockColor
        get() = BlockColor.BLACK_BLOCK_COLOR

    @Override
    override fun toItem(): Item {
        return ItemBlock(Block.get(BlockID.AIR))
    }
}