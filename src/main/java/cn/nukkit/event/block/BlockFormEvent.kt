package cn.nukkit.event.block

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockFormEvent(block: Block, newState: Block?) : BlockGrowEvent(block, newState), Cancellable {
    companion object {
        val handlers: HandlerList = HandlerList()
    }
}