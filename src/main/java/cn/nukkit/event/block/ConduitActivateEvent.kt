package cn.nukkit.event.block

import cn.nukkit.block.Block

class ConduitActivateEvent(block: Block) : BlockEvent(block) {
    companion object {
        val handlers: HandlerList = HandlerList()
    }
}