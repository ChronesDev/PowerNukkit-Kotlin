package cn.nukkit.event.player

import cn.nukkit.Player

/**
 * @author GoodLucky777
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class PlayerToggleSpinAttackEvent @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(player: Player?, isSpinAttacking: Boolean) : PlayerEvent(), Cancellable {
    val isSpinAttacking: Boolean
        @PowerNukkitOnly @Since("1.4.0.0-PN") get

    companion object {
        val handlers: HandlerList = HandlerList()
            @PowerNukkitOnly @Since("1.4.0.0-PN") get
    }

    init {
        player = player
        this.isSpinAttacking = isSpinAttacking
    }
}