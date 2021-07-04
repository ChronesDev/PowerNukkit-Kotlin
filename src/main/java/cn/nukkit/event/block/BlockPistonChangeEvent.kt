package cn.nukkit.event.block

import cn.nukkit.block.Block

/**
 * @author CreeperFace
 * @since 2.8.2017
 *
 */
@Deprecated
@Deprecated("Use BlockPistonEvent")
class BlockPistonChangeEvent(block: Block, val oldPower: Int, val newPower: Int) : BlockEvent(block) {

    companion object {
        val handlers: HandlerList = HandlerList()
    }
}