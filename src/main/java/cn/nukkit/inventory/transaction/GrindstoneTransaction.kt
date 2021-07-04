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
 * @since 2021-03-21
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class GrindstoneTransaction @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(source: Player?, actions: List<InventoryAction>) : InventoryTransaction(source, actions) {
    private var firstItem: Item? = null
    private var secondItem: Item? = null
    private var outputItem: Item? = null

    @Override
    override fun addAction(action: InventoryAction) {
        super.addAction(action)
        if (action is GrindstoneItemAction) {
            when ((action as GrindstoneItemAction).getType()) {
                NetworkInventoryAction.SOURCE_TYPE_ANVIL_INPUT -> firstItem = action.getTargetItem()
                NetworkInventoryAction.SOURCE_TYPE_ANVIL_RESULT -> outputItem = action.getSourceItem()
                NetworkInventoryAction.SOURCE_TYPE_ANVIL_MATERIAL -> secondItem = action.getTargetItem()
            }
        }
    }

    @Override
    override fun canExecute(): Boolean {
        val inventory: Inventory = getSource().getWindowById(Player.GRINDSTONE_WINDOW_ID) ?: return false
        val grindstoneInventory: GrindstoneInventory = inventory as GrindstoneInventory
        if (outputItem == null || outputItem.isNull() ||
                (firstItem == null || firstItem.isNull()) && (secondItem == null || secondItem.isNull())) {
            return false
        }
        val air: Item = Item.get(0)
        val first: Item = if (firstItem != null) firstItem else air
        val second: Item = if (secondItem != null) secondItem else air
        return (first.equals(grindstoneInventory.getFirstItem(), true, true)
                && second.equals(grindstoneInventory.getSecondItem(), true, true)
                && outputItem.equals(grindstoneInventory.getResult(), true, true))
    }

    @Override
    override fun execute(): Boolean {
        if (this.hasExecuted() || !canExecute()) {
            this.source.removeAllWindows(false)
            this.sendInventories()
            return false
        }
        val inventory: GrindstoneInventory = getSource().getWindowById(Player.GRINDSTONE_WINDOW_ID) as GrindstoneInventory
        val exp: Int = inventory.getResultExperience()
        val air: Item = Item.get(0)
        val first: Item = if (firstItem != null) firstItem else air
        val second: Item = if (secondItem != null) secondItem else air
        val event = GrindstoneEvent(inventory, first, outputItem, second, exp, source)
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
        return true
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getFirstItem(): Item? {
        return if (firstItem == null) null else firstItem.clone()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getSecondItem(): Item? {
        return if (secondItem == null) null else secondItem.clone()
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
            return actions.stream().anyMatch { it -> it is GrindstoneItemAction }
        }
    }
}