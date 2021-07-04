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
package cn.nukkit.nbt

import cn.nukkit.item.Item

/**
 * @author joserobjr
 * @since 2021-02-14
 */
@ExtendWith(PowerNukkitExtension::class)
internal class NBTIOTest {
    @Test
    fun gitHubIssue960() {
        val badItem = Item(879, 3, 12, "Cocoa Bean from a bad Alpha version")
        val compoundTag: CompoundTag = NBTIO.putItemHelper(badItem)
        val recoveredItem: Item = NBTIO.getItemHelper(compoundTag)
        val correctItem: Item = MinecraftItemID.COCOA_BEANS.get(12)
        assertEquals(correctItem, recoveredItem)
    }
}