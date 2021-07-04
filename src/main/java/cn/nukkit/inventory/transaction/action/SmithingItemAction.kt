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
package cn.nukkit.inventory.transaction.action

import cn.nukkit.Player

/**
 * @author joserobjr
 * @since 2021-05-16
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class SmithingItemAction @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(sourceItem: Item, targetItem: Item, @get:PowerNukkitOnly
@get:Since("1.4.0.0-PN") val type: Int) : InventoryAction(sourceItem, targetItem) {
    @Override
    override fun isValid(source: Player): Boolean {
        return source.getWindowById(Player.SMITHING_WINDOW_ID) != null
    }

    @Override
    override fun execute(source: Player?): Boolean {
        return true
    }

    @Override
    override fun onExecuteSuccess(source: Player?) {
        // Does nothing
    }

    @Override
    override fun onExecuteFail(source: Player?) {
        // Does nothing
    }
}