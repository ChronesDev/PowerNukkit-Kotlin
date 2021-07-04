package cn.nukkit.event.redstone

import cn.nukkit.block.Block

/**
 * @author Angelic47 (Nukkit Project)
 */
class RedstoneUpdateEvent(source: Block?) : BlockUpdateEvent(source) {
    companion object {
        val handlers: HandlerList = HandlerList()
    }
}