package cn.nukkit.event.entity

import cn.nukkit.entity.EntityLiving

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EntityDeathEvent(entity: EntityLiving?, drops: Array<Item>) : EntityEvent() {
    private var drops: Array<Item>

    constructor(entity: EntityLiving?) : this(entity, Item.EMPTY_ARRAY) {}

    fun getDrops(): Array<Item> {
        return drops
    }

    fun setDrops(drops: Array<Item>?) {
        var drops: Array<Item>? = drops
        if (drops == null) {
            drops = Item.EMPTY_ARRAY
        }
        this.drops = drops
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        entity = entity
        this.drops = drops
    }
}