package cn.nukkit.event.player

import cn.nukkit.Player

/**
 * call when a player moves wrongly
 *
 * @author WilliamGao
 */
class PlayerInvalidMoveEvent(player: Player?, revert: Boolean) : PlayerEvent(), Cancellable {
    /**
     * @param revert revert movement
     */
    var isRevert: Boolean
        @Deprecated @Deprecated("""If you just simply want to disable the movement check, please use {@link Player#setCheckMovement(boolean)} instead.
      """) set

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        isRevert = revert
    }
}