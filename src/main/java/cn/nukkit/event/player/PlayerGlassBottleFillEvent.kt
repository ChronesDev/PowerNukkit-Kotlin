package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerGlassBottleFillEvent(player: Player?, target: Block, item: Item) : PlayerEvent(), Cancellable {
    protected val item: Item
    protected val target: Block
    fun getItem(): Item {
        return item
    }

    val block: Block
        get() = target

    init {
        player = player
        this.target = target
        this.item = item.clone()
    }
}