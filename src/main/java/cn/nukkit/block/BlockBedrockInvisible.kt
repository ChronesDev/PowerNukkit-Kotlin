package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author Pub4Game
 * @since 03.01.2016
 */
class BlockBedrockInvisible : BlockSolid() {
    @get:Override
    override val id: Int
        get() = INVISIBLE_BEDROCK

    @get:Override
    override val name: String
        get() = "Invisible Bedrock"

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 2

    @Override
    override fun canBeFlowedInto(): Boolean {
        return false
    }

    @get:Override
    override val hardness: Double
        get() = (-1).toDouble()

    @get:Override
    override val resistance: Double
        get() = 18000000

    @Override
    override fun isBreakable(item: Item?): Boolean {
        return false
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.TRANSPARENT_BLOCK_COLOR

    @Override
    override fun canBePushed(): Boolean {
        return false
    }

    @Override
    override fun canBePulled(): Boolean {
        return false
    }

    @Override
    override fun toItem(): Item {
        return ItemBlock(Block.get(BlockID.AIR))
    }
}