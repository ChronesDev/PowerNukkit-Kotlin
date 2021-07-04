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
package cn.nukkit.level.format.anvil.util

import cn.nukkit.block.BlockID

/**
 * @author joserobjr
 * @since 2020-10-02
 */
@ExtendWith(PowerNukkitExtension::class)
internal class ImmutableBlockStorageTest {
    var storage: ImmutableBlockStorage? = null
    private val stone: BlockState = BlockState.of(BlockID.STONE)
    @BeforeEach
    fun setUp() {
        storage = BlockStorage().immutableCopy()
    }

    @Test
    fun setBlockId() {
        assertThrows(UnsupportedOperationException::class.java) { storage.setBlockId(0, 0, 0, 1) }
    }

    @Test
    fun setBlockData() {
        assertThrows(UnsupportedOperationException::class.java) { storage.setBlockData(0, 0, 0, 1) }
    }

    @Test
    fun setBlock() {
        assertThrows(UnsupportedOperationException::class.java) { storage.setBlock(0, 0, 0, 1, 2) }
    }

    @get:Test
    val andSetBlock: Unit
        get() {
            assertThrows(UnsupportedOperationException::class.java) { storage.getAndSetBlock(0, 0, 0, 1, 2) }
        }

    @get:Test
    val andSetBlockState: Unit
        get() {
            assertThrows(UnsupportedOperationException::class.java) { storage.getAndSetBlockState(0, 0, 0, stone) }
        }

    @Test
    fun setBlockState() {
        assertThrows(UnsupportedOperationException::class.java) { storage.setBlockState(0, 0, 0, stone) }
    }

    @get:Test
    val andSetFullBlock: Unit
        get() {
            assertThrows(UnsupportedOperationException::class.java) { storage.getAndSetFullBlock(0, 0, 0, stone.getFullId()) }
        }

    @Test
    fun testSetBlockState() {
        assertThrows(UnsupportedOperationException::class.java) { storage.setBlockState(0, stone) }
    }

    @Test
    fun delayPaletteUpdates() {
        storage.delayPaletteUpdates()
        assertFalse(storage.isPaletteUpdateDelayed())
    }

    @Test
    fun recheckBlocks() {
        storage.recheckBlocks()
        assertFalse(storage.hasBlockIds())
    }

    @Test
    fun copy() {
        assertFalse(storage.copy() is ImmutableBlockStorage)
    }

    @Test
    fun immutableCopy() {
        assertTrue(storage === storage.immutableCopy())
    }
}