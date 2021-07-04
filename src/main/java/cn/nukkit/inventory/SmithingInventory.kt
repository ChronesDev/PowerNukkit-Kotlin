/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
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
package cn.nukkit.inventory

import cn.nukkit.Player

/**
 * @author joserobjr
 * @since 2020-09-28
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class SmithingInventory @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(playerUI: PlayerUIInventory, position: Position) : FakeBlockUIComponent(playerUI, InventoryType.SMITHING_TABLE, 51, position) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    fun matchRecipe(): SmithingRecipe {
        return Server.getInstance().getCraftingManager().matchSmithingRecipe(getEquipment(), getIngredient())
    }

    @Override
    override fun onSlotChange(index: Int, before: Item?, send: Boolean) {
        if (index == EQUIPMENT || index == INGREDIENT) {
            updateResult()
        }
        super.onSlotChange(index, before, send)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun updateResult() {
        val result: Item
        val recipe: SmithingRecipe = matchRecipe()
        result = if (recipe == null) {
            Item.get(0)
        } else {
            recipe.getFinalResult(getEquipment())
        }
        setResult(result)
    }

    private fun setResult(result: Item) {
        //playerUI.setItem(50, result);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getResult(): Item {
        val recipe: SmithingRecipe = matchRecipe() ?: return Item.get(0)
        return recipe.getFinalResult(getEquipment())
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getEquipment(): Item {
        return getItem(EQUIPMENT)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setEquipment(equipment: Item?) {
        setItem(EQUIPMENT, equipment)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getIngredient(): Item {
        return getItem(INGREDIENT)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setIngredient(ingredient: Item?) {
        setItem(INGREDIENT, ingredient)
    }

    @Override
    override fun onOpen(who: Player) {
        super.onOpen(who)
        who.craftingType = Player.CRAFTING_SMITHING
    }

    @Override
    override fun onClose(who: Player) {
        super.onClose(who)
        who.craftingType = Player.CRAFTING_SMALL
        who.giveItem(getItem(EQUIPMENT), getItem(INGREDIENT))
        this.clear(EQUIPMENT)
        this.clear(INGREDIENT)
        playerUI.clear(50)
    }

    companion object {
        private const val EQUIPMENT = 0
        private const val INGREDIENT = 1

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val SMITHING_EQUIPMENT_UI_SLOT = 51

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val SMITHING_INGREDIENT_UI_SLOT = 52
    }
}