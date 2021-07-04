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
package cn.nukkit.item

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 * @since 2021-02-16
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class ItemWarpedFungusOnAStick @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Integer?, count: Int) : ItemTool(WARPED_FUNGUS_ON_A_STICK, meta, count, "Warped Fungus on a Stick") {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0, 1) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(meta: Integer?) : this(meta, 1) {
    }

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }

    @Override
    override fun getMaxDurability(): Int {
        return 100
    }

    @Override
    override fun damageWhenBreaking(): Boolean {
        return false
    }
}