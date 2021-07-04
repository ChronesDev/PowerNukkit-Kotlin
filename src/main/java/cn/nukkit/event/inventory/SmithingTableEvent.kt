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
package cn.nukkit.event.inventory

import cn.nukkit.Player

/**
 * @author joserobjr
 * @since 2021-05-16
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class SmithingTableEvent @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(inventory: SmithingInventory, @Nonnull equipmentItem: Item, @Nonnull resultItem: Item, @Nonnull ingredientItem: Item, @Nonnull player: Player) : InventoryEvent(inventory), Cancellable {
    @Nonnull
    private val equipmentItem: Item

    @Nonnull
    private val resultItem: Item

    @Nonnull
    private val ingredientItem: Item

    @Nonnull
    private val player: Player
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getEquipmentItem(): Item {
        return equipmentItem
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getResultItem(): Item {
        return resultItem
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getIngredientItem(): Item {
        return ingredientItem
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getPlayer(): Player {
        return player
    }

    companion object {
        val handlers: HandlerList = HandlerList()
            @PowerNukkitOnly @Since("1.4.0.0-PN") get
    }

    init {
        this.equipmentItem = equipmentItem
        this.resultItem = resultItem
        this.ingredientItem = ingredientItem
        this.player = player
    }
}