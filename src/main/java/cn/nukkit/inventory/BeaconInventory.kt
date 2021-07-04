package cn.nukkit.inventory

import cn.nukkit.Player

/**
 * @author Rover656
 */
class BeaconInventory(playerUI: PlayerUIInventory, position: Position) : FakeBlockUIComponent(playerUI, InventoryType.BEACON, 27, position) {
    @Override
    override fun onClose(who: Player) {
        super.onClose(who)
        val drops: Array<Item> = who.getInventory().addItem(this.getItem(0))
        for (drop in drops) {
            if (!who.dropItem(drop)) {
                this.getHolder().getLevel().dropItem(this.getHolder().add(0.5, 0.5, 0.5), drop)
            }
        }
        this.clear(0)
    }
}