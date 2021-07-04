package cn.nukkit.event.block

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockSpreadEvent(block: Block, source: Block, newState: Block?) : BlockFormEvent(block, newState), Cancellable {
    private val source: Block
    fun getSource(): Block {
        return source
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.source = source
    }
}