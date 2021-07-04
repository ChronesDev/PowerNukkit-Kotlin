package cn.nukkit.event.block

import cn.nukkit.block.Block

class BlockFallEvent(block: Block) : BlockEvent(block), Cancellable {
    companion object {
        val handlers: HandlerList = HandlerList()
    }
}