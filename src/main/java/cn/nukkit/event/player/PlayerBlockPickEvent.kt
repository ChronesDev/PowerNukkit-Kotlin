package cn.nukkit.event.player

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
class PlayerBlockPickEvent(player: Player?, blockClicked: Block, item: Item) : PlayerEvent(), Cancellable {
    private val blockClicked: Block
    private var item: Item
    fun getItem(): Item {
        return item
    }

    fun setItem(item: Item) {
        this.item = item
    }

    fun getBlockClicked(): Block {
        return blockClicked
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.blockClicked = blockClicked
        this.item = item
        player = player
    }
}