package cn.nukkit.inventory.transaction.action

import cn.nukkit.Player

@Since("1.4.0.0-PN")
class RepairItemAction @Since("1.4.0.0-PN") constructor(sourceItem: Item, targetItem: Item, @get:Since("1.4.0.0-PN") val type: Int) : InventoryAction(sourceItem, targetItem) {
    @Override
    override fun isValid(source: Player): Boolean {
        return source.getWindowById(Player.ANVIL_WINDOW_ID) != null
    }

    @Override
    override fun execute(source: Player?): Boolean {
        return true
    }

    @Override
    override fun onExecuteSuccess(source: Player?) {
    }

    @Override
    override fun onExecuteFail(source: Player?) {
    }
}