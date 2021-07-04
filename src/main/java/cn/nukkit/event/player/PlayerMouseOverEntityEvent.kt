package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerMouseOverEntityEvent(player: Player?, entity: Entity) : PlayerEvent() {
    private val entity: Entity
    fun getEntity(): Entity {
        return entity
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.entity = entity
    }
}