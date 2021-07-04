package cn.nukkit.event.block

import cn.nukkit.block.Block

class WaterFrostEvent(block: Block, entity: Entity) : BlockEvent(block), Cancellable {
    protected val entity: Entity
    fun getEntity(): Entity {
        return entity
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.entity = entity
    }
}