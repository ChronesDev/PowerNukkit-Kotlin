package cn.nukkit.event.entity

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author Angelic47 (Nukkit Project)
 */
class EntityExplodeEvent(entity: Entity?, position: Position, blocks: List<Block>, yield: Double) : EntityEvent(), Cancellable {
    protected val position: Position
    protected var blocks: List<Block>
    protected var ignitions: Set<Block>
    var yield: Double
    fun getPosition(): Position {
        return position
    }

    var blockList: List<Any>
        get() = blocks
        set(blocks) {
            this.blocks = blocks
        }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getIgnitions(): Set<Block> {
        return ignitions
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setIgnitions(ignitions: Set<Block>) {
        this.ignitions = ignitions
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        entity = entity
        this.position = position
        this.blocks = blocks
        this.yield = yield
        ignitions = HashSet(0)
    }
}