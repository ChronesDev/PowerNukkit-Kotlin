package cn.nukkit.event.block

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockGrowEvent(block: Block, newState: Block?) : BlockEvent(block), Cancellable {
    private val newState: Block?
    fun getNewState(): Block? {
        return newState
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.newState = newState
    }
}