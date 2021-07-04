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
 * @since 2020-10-06
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockExplodeEvent @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(block: Block, position: Position, blocks: Set<Block>, ignitions: Set<Block>, yield: Double, fireChance: Double) : BlockEvent(block), Cancellable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected val position: Position

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected var blocks: Set<Block>

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected var ignitions: Set<Block>

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    var yield: Double

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    val fireChance: Double
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getPosition(): Position {
        return position
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var affectedBlocks: Set<Any>
        get() = blocks
        set(blocks) {
            this.blocks = blocks
        }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getIgnitions(): Set<Block> {
        return ignitions
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setIgnitions(ignitions: Set<Block>) {
        this.ignitions = ignitions
    }

    companion object {
        @get:Since("1.4.0.0-PN")
        @get:PowerNukkitOnly
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.position = position
        this.blocks = blocks
        this.yield = yield
        this.ignitions = ignitions
        this.fireChance = fireChance
    }
}