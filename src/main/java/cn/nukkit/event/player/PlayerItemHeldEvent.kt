package cn.nukkit.event.player

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class PlayerItemHeldEvent(player: Player?, item: Item, hotbarSlot: Int) : PlayerEvent(), Cancellable {
    private val item: Item

    @get:Deprecated
    val inventorySlot: Int
        @Deprecated get() = field

    fun getItem(): Item {
        return item
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.item = item
        inventorySlot = hotbarSlot
    }
}