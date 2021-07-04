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
package cn.nukkit.block

import cn.nukkit.blockstate.BlockState

/**
 * @author joserobjr
 * @since 2021-03-24
 */
@ExtendWith(PowerNukkitExtension::class)
internal class BlockWallTest {
    @MockLevel
    var level: Level? = null
    var wall: BlockWall? = null
    var diamondPickaxe: Item? = null
    @BeforeEach
    fun setUp() {
        diamondPickaxe = Item.get(ItemID.DIAMOND_PICKAXE)
        wall = BlockState.of(BlockID.STONE_WALL).getBlock(level, X, Y, Z) as BlockWall
        level.setBlock(wall, wall, false, false)
    }

    @ParameterizedTest
    @EnumSource(BlockWall.WallType::class)
    fun getDrops(wallType: BlockWall.WallType?) {
        wall.setWallType(wallType)
        val drops: Array<Item> = wall.getDrops(diamondPickaxe)
        assertEquals(1, drops.size)
        assertEquals(Item.getBlock(BlockID.STONE_WALL,
                BlockState.of(BlockID.STONE_WALL)
                        .withProperty(BlockWall.WALL_BLOCK_TYPE, wallType).getExactIntStorage()),
                drops[0])
    }

    companion object {
        private const val X = 1
        private const val Y = 2
        private const val Z = 3
    }
}