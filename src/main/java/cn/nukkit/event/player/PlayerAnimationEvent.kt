package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerAnimationEvent : PlayerEvent, Cancellable {
    private val animationType: AnimatePacket.Action
    val rowingTime: Float
        @PowerNukkitOnly @Since("1.4.0.0-PN") get

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(player: Player?, animatePacket: AnimatePacket) {
        player = player
        animationType = animatePacket.action
        rowingTime = animatePacket.rowingTime
    }

    constructor(player: Player?) : this(player, AnimatePacket.Action.SWING_ARM) {}
    constructor(player: Player?, animation: AnimatePacket.Action) {
        player = player
        animationType = animation
        rowingTime = 0f
    }

    fun getAnimationType(): AnimatePacket.Action {
        return animationType
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }
}