package cn.nukkit.event.entity

import cn.nukkit.entity.item.EntityItem

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemDespawnEvent(item: EntityItem?) : EntityEvent(), Cancellable {
    @Override
    override fun getEntity(): EntityItem {
        return this.entity as EntityItem
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.entity = item
    }
}