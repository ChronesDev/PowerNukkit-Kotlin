package cn.nukkit.event.entity

import cn.nukkit.block.Block

/**
 * @author CreeperFace
 */
class EntityInteractEvent(entity: Entity?, block: Block) : EntityEvent(), Cancellable {
    private val block: Block
    fun getBlock(): Block {
        return block
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        entity = entity
        this.block = block
    }
}