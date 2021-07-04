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
package cn.nukkit.level.format.updater

import cn.nukkit.block.Block

/**
 * @author joserobjr
 * @since 2020-09-20
 */
@ExtendWith(PowerNukkitExtension::class)
internal class WallUpdaterTest : BlockID {
    var x = 3
    var y = 4
    var z = 5

    @MockLevel
    var level: Level? = null
    var section: ChunkSection? = null
    var updater: WallUpdater? = null
    @Test
    fun update() {
        val state: BlockState = BlockState.of(COBBLE_WALL, 63)
        section.setBlockState(x, y, z, state)
        assertTrue(updater.update(0, 0, 0, x, y, z, state))
        val wall = BlockWall()
        wall.setWallPost(true)
        val actual: Block = section.getBlockState(x, y, z).getBlock()
        assertEquals(wall.getCurrentState(), section.getBlockState(x, y, z))
        assertEquals(wall.getCurrentState(), actual.getCurrentState())
    }

    @BeforeEach
    fun setUp() {
        section = ChunkSection(0)
        section.setContentVersion(0)
        section.delayPaletteUpdates()
        updater = WallUpdater(level, section)
    }
}