package cn.nukkit.inventory

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
class PlayerCursorInventory internal constructor(playerUI: PlayerUIInventory) : PlayerUIComponent(playerUI, 0, 1) {
    private override val playerUI: PlayerUIInventory

    /**
     * This override is here for documentation and code completion purposes only.
     *
     * @return Player
     */
    @Override
    override fun getHolder(): Player {
        return playerUI.getHolder()
    }

    @Override
    override fun getType(): InventoryType {
        return InventoryType.CURSOR
    }

    @Override
    override fun sendContents(vararg players: Player?) {
        playerUI.sendSlot(0, players)
    }

    init {
        this.playerUI = playerUI
    }
}