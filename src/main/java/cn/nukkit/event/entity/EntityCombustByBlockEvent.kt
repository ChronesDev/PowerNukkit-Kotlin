package cn.nukkit.event.entity

import cn.nukkit.block.Block

/**
 * @author Box (Nukkit Project)
 */
class EntityCombustByBlockEvent(combuster: Block, combustee: Entity?, duration: Int) : EntityCombustEvent(combustee, duration) {
    protected val combuster: Block
    fun getCombuster(): Block {
        return combuster
    }

    init {
        this.combuster = combuster
    }
}