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
package cn.nukkit.event.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 * @since 2020-010-06
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockExplosionPrimeEvent @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(block: Block, @set:PowerNukkitOnly
@set:Since("1.4.0.0-PN")
@get:PowerNukkitOnly
@get:Since("1.4.0.0-PN") var force: Double, @set:PowerNukkitOnly
                                                                                 @set:Since("1.4.0.0-PN")
                                                                                 @get:PowerNukkitOnly
                                                                                 @get:Since("1.4.0.0-PN") var fireChance: Double) : BlockEvent(block), Cancellable {

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isBlockBreaking = true

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(block: Block, force: Double) : this(block, force, 0.0) {
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isIncendiary: Boolean
        get() = fireChance > 0
        set(incendiary) {
            if (!incendiary) {
                fireChance = 0.0
            } else if (fireChance <= 0) {
                fireChance = 1.0 / 3.0
            }
        }

    companion object {
        @get:Since("1.4.0.0-PN")
        @get:PowerNukkitOnly
        val handlers: HandlerList = HandlerList()
    }
}