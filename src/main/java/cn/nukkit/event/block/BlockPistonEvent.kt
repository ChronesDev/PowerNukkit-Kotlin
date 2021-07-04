package cn.nukkit.event.block

import cn.nukkit.block.Block

class BlockPistonEvent(piston: BlockPistonBase, direction: BlockFace, blocks: List<Block>, destroyedBlocks: List<Block>, extending: Boolean) : BlockEvent(piston), Cancellable {
    private val direction: BlockFace
    private val blocks: List<Block>
    private val destroyedBlocks: List<Block>
    val isExtending: Boolean
    fun getDirection(): BlockFace {
        return direction
    }

    fun getBlocks(): List<Block> {
        return ArrayList(blocks)
    }

    fun getDestroyedBlocks(): List<Block> {
        return destroyedBlocks
    }

    @Override
    override fun getBlock(): BlockPistonBase {
        return super.getBlock() as BlockPistonBase
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.direction = direction
        this.blocks = blocks
        this.destroyedBlocks = destroyedBlocks
        isExtending = extending
    }
}