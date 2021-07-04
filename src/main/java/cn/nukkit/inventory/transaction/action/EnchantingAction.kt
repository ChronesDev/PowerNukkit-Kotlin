package cn.nukkit.inventory.transaction.action

import cn.nukkit.Player

@Since("1.3.1.0-PN")
@ToString(callSuper = true)
class EnchantingAction @Since("1.3.1.0-PN") constructor(source: Item, target: Item, @field:Since("1.3.1.0-PN") @field:Getter private val type: Int) : InventoryAction(source, target) {
    @Override
    override fun isValid(source: Player): Boolean {
        return source.getWindowById(Player.ENCHANT_WINDOW_ID) != null
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