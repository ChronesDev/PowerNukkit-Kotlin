package cn.nukkit.event.block

import cn.nukkit.Player

/**
 * @author Snake1999
 * @since 2016/1/22
 */
class DoorToggleEvent(block: Block, player: Player) : BlockUpdateEvent(block), Cancellable {
    private var player: Player
    fun setPlayer(player: Player) {
        this.player = player
    }

    fun getPlayer(): Player {
        return player
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.player = player
    }
}