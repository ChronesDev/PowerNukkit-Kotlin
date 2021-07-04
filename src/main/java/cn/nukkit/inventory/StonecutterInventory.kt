package cn.nukkit.inventory

import cn.nukkit.Player

class StonecutterInventory(playerUI: PlayerUIInventory, position: Position) : FakeBlockUIComponent(playerUI, InventoryType.STONECUTTER, 3, position) {
    @Override
    override fun onClose(who: Player) {
        super.onClose(who)
        who.craftingType = Player.CRAFTING_SMALL
        val drops: Array<Item> = who.getInventory().addItem(this.getItem(0))
        for (drop in drops) {
            if (!who.dropItem(drop)) {
                this.getHolder().getLevel().dropItem(this.getHolder().add(0.5, 0.5, 0.5), drop)
            }
        }
        this.clear(0)
        who.resetCraftingGridType()
    }
}