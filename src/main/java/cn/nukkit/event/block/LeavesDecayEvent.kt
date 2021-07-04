package cn.nukkit.event.block

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class LeavesDecayEvent(block: Block) : BlockEvent(block), Cancellable {
    companion object {
        val handlers: HandlerList = HandlerList()
    }
}