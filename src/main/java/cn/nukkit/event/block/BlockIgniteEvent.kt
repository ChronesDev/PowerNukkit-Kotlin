package cn.nukkit.event.block

import cn.nukkit.block.Block

class BlockIgniteEvent(block: Block, source: Block, entity: Entity, cause: BlockIgniteCause) : BlockEvent(block), Cancellable {
    private val source: Block
    private val entity: Entity
    val cause: BlockIgniteCause
    fun getSource(): Block {
        return source
    }

    fun getEntity(): Entity {
        return entity
    }

    enum class BlockIgniteCause {
        EXPLOSION, FIREBALL, FLINT_AND_STEEL, LAVA, LIGHTNING, SPREAD
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.source = source
        this.entity = entity
        this.cause = cause
    }
}