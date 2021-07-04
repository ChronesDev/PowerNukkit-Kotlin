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
package cn.nukkit.event.inventory

import cn.nukkit.Player

/**
 * Fired when a player change anything in the item name in an open Anvil inventory window.
 *
 * @author joserobjr
 * @since 2021-02-14
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ToString
class PlayerTypingAnvilInventoryEvent @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(@Nonnull player: Player, @Nonnull inventory: AnvilInventory, @Nullable previousName: String, @Nonnull typedName: String) : InventoryEvent(inventory) {
    private val player: Player
    val previousName: String
        @PowerNukkitOnly @Since("1.4.0.0-PN") @Nullable get
    val typedName: String
        @PowerNukkitOnly @Since("1.4.0.0-PN") @Nonnull get

    @Override
    @Nonnull
    override fun getInventory(): AnvilInventory {
        return super.getInventory() as AnvilInventory
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
        this.player = player
        this.previousName = previousName
        this.typedName = typedName
    }
}