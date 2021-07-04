package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerMoveEvent(player: Player?, from: Location?, to: Location?, resetBlocks: Boolean) : PlayerEvent(), Cancellable {
    private var from: Location?
    private var to: Location?
    var isResetBlocksAround: Boolean

    constructor(player: Player?, from: Location?, to: Location?) : this(player, from, to, true) {}

    fun getFrom(): Location? {
        return from
    }

    fun setFrom(from: Location?) {
        this.from = from
    }

    fun getTo(): Location? {
        return to
    }

    fun setTo(to: Location?) {
        this.to = to
    }

    @Override
    fun setCancelled() {
        super.setCancelled()
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.from = from
        this.to = to
        isResetBlocksAround = resetBlocks
    }
}