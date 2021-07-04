package cn.nukkit.event.player

import cn.nukkit.Player

/**
 * @author CreeperFace
 * @since 1. 1. 2017
 */
class PlayerInteractEntityEvent(player: Player?, entity: Entity, item: Item, clickedPos: Vector3) : PlayerEvent(), Cancellable {
    protected val entity: Entity
    protected val item: Item
    protected val clickedPos: Vector3
    fun getEntity(): Entity {
        return entity
    }

    fun getItem(): Item {
        return item
    }

    fun getClickedPos(): Vector3 {
        return clickedPos
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.entity = entity
        this.item = item
        this.clickedPos = clickedPos
    }
}