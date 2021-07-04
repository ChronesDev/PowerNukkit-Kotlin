package cn.nukkit.event.player

import cn.nukkit.Player

/**
 * @author CreeperFace
 * @since 18.3.2017
 */
class PlayerMapInfoRequestEvent(p: Player?, item: Item) : PlayerEvent(), Cancellable {
    private val item: Item
    val map: Item
        get() = item

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.player = p
        this.item = item
    }
}