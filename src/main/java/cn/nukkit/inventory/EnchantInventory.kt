package cn.nukkit.inventory

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantInventory(playerUI: PlayerUIInventory, position: Position) : FakeBlockUIComponent(playerUI, InventoryType.ENCHANT_TABLE, 14, position) {
    @Override
    override fun onOpen(who: Player) {
        super.onOpen(who)
        who.craftingType = Player.CRAFTING_ENCHANT
    }

    @Override
    override fun onClose(who: Player) {
        super.onClose(who)
        who.craftingType = Player.CRAFTING_SMALL
        var drops: Array<Item> = arrayOf<Item>(getItem(0), getItem(1))
        drops = who.getInventory().addItem(drops)
        for (drop in drops) {
            if (!who.dropItem(drop)) {
                this.getHolder().getLevel().dropItem(this.getHolder().add(0.5, 0.5, 0.5), drop)
            }
        }
        clear(0)
        clear(1)
        who.resetCraftingGridType()
    }

    @Since("1.3.1.0-PN")
    fun getInputSlot(): Item {
        return this.getItem(0)
    }

    @Since("1.3.1.0-PN")
    fun getOutputSlot(): Item {
        return this.getItem(0)
    }

    @Since("1.3.1.0-PN")
    fun getReagentSlot(): Item {
        return this.getItem(1)
    }

    companion object {
        @Since("1.3.1.0-PN")
        val ENCHANT_INPUT_ITEM_UI_SLOT = 14

        @Since("1.3.1.0-PN")
        val ENCHANT_REAGENT_UI_SLOT = 15
    }
}