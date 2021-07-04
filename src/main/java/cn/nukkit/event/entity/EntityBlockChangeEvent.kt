package cn.nukkit.event.entity

import cn.nukkit.block.Block

/**
 * @since 15-10-26
 */
class EntityBlockChangeEvent(entity: Entity?, from: Block, to: Block) : EntityEvent(), Cancellable {
    private val from: Block
    private val to: Block
    fun getFrom(): Block {
        return from
    }

    fun getTo(): Block {
        return to
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        entity = entity
        this.from = from
        this.to = to
    }
}