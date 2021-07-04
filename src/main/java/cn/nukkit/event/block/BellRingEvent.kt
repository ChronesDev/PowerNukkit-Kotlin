package cn.nukkit.event.block

import cn.nukkit.block.BlockBell

class BellRingEvent(bell: BlockBell, val cause: RingCause, entity: Entity) : BlockEvent(bell), Cancellable {
    private val entity: Entity

    @Override
    override fun getBlock(): BlockBell {
        return super.getBlock() as BlockBell
    }

    fun getEntity(): Entity {
        return entity
    }

    enum class RingCause {
        HUMAN_INTERACTION, REDSTONE, PROJECTILE, DROPPED_ITEM, UNKNOWN
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.entity = entity
    }
}