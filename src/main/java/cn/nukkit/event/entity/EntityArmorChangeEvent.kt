package cn.nukkit.event.entity

import cn.nukkit.entity.Entity

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EntityArmorChangeEvent(entity: Entity?, oldItem: Item, newItem: Item, slot: Int) : EntityEvent(), Cancellable {
    private val oldItem: Item
    private var newItem: Item
    val slot: Int
    fun getNewItem(): Item {
        return newItem
    }

    fun setNewItem(newItem: Item) {
        this.newItem = newItem
    }

    fun getOldItem(): Item {
        return oldItem
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        entity = entity
        this.oldItem = oldItem
        this.newItem = newItem
        this.slot = slot
    }
}