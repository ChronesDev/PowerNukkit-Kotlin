/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2021  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cn.nukkit.inventory.transaction

import cn.nukkit.Player

/**
 * @author joserobjr
 * @since 2021-05-16
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class SmithingTransaction @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(source: Player?, actions: List<InventoryAction>) : InventoryTransaction(source, actions) {
    private var equipmentItem: Item? = null
    private var ingredientItem: Item? = null
    private var outputItem: Item? = null

    @Override
    override fun addAction(action: InventoryAction) {
        super.addAction(action)
        if (action is SmithingItemAction) {
            when ((action as SmithingItemAction).getType()) {
                0 -> equipmentItem = action.getTargetItem()
                2 -> outputItem = action.getSourceItem()
                1 -> ingredientItem = action.getTargetItem()
            }
        } else if (action is CreativeInventoryAction) {
            val creativeAction: CreativeInventoryAction = action as CreativeInventoryAction
            if (creativeAction.getActionType() === 0 && creativeAction.getSourceItem().isNull()
                    && !creativeAction.getTargetItem().isNull() && creativeAction.getTargetItem().getId() === ItemID.NETHERITE_INGOT) {
                ingredientItem = action.getTargetItem()
            }
        }
    }

    @Override
    override fun canExecute(): Boolean {
        val inventory: Inventory = getSource().getWindowById(Player.SMITHING_WINDOW_ID) ?: return false
        val grindstoneInventory: SmithingInventory = inventory as SmithingInventory
        if (outputItem == null || outputItem.isNull() ||
                (equipmentItem == null || equipmentItem.isNull()) && (ingredientItem == null || ingredientItem.isNull())) {
            return false
        }
        val air: Item = Item.get(0)
        val equipment: Item = if (equipmentItem != null) equipmentItem else air
        val ingredient: Item = if (ingredientItem != null) ingredientItem else air
        return (equipment.equals(grindstoneInventory.getEquipment(), true, true)
                && ingredient.equals(grindstoneInventory.getIngredient(), true, true)
                && outputItem.equals(grindstoneInventory.getResult(), true, true))
    }

    @Override
    override fun execute(): Boolean {
        if (this.hasExecuted() || !canExecute()) {
            this.source.removeAllWindows(false)
            this.sendInventories()
            return false
        }
        val inventory: SmithingInventory = getSource().getWindowById(Player.SMITHING_WINDOW_ID) as SmithingInventory
        val air: Item = Item.get(0)
        val equipment: Item = if (equipmentItem != null) equipmentItem else air
        val ingredient: Item = if (ingredientItem != null) ingredientItem else air
        val event = SmithingTableEvent(inventory, equipment, outputItem, ingredient, source)
        this.source.getServer().getPluginManager().callEvent(event)
        if (event.isCancelled()) {
            this.source.removeAllWindows(false)
            this.sendInventories()
            return true
        }
        for (action in this.actions) {
            if (action.execute(this.source)) {
                action.onExecuteSuccess(this.source)
            } else {
                action.onExecuteFail(this.source)
            }
        }
        if (!source.isCreative()) {
            source.sendAllInventories()
            source.getCursorInventory().sendContents(source)
        }
        return true
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getEquipmentItem(): Item? {
        return if (equipmentItem == null) null else equipmentItem.clone()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getIngredientItem(): Item? {
        return if (ingredientItem == null) null else ingredientItem.clone()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getOutputItem(): Item? {
        return if (outputItem == null) null else outputItem.clone()
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun checkForItemPart(actions: List<InventoryAction?>): Boolean {
            return actions.stream().anyMatch { it -> it is SmithingItemAction }
        }
    }
}