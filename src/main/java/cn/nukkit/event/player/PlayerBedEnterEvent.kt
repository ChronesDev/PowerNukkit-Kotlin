package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerBedEnterEvent(player: Player?, bed: Block) : PlayerEvent(), Cancellable {
    private val bed: Block
    fun getBed(): Block {
        return bed
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.bed = bed
    }
}