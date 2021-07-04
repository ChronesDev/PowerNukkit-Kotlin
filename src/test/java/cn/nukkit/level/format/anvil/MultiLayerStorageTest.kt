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
package cn.nukkit.level.format.anvil

import cn.nukkit.block.BlockID

/**
 * @author joserobjr
 * @since 2020-10-06
 */
@ExtendWith(PowerNukkitExtension::class)
internal class MultiLayerStorageTest {
    var storage: MultiLayerStorage? = null
    @BeforeEach
    fun setUp() {
        storage = MultiLayerStorage()
    }

    @Test
    fun hasBlocks() {
        assertFalse(storage.hasBlocks())
        val blockStorage: BlockStorage = getBlockStorage(1)
        assertFalse(storage.hasBlocks())
        blockStorage.setBlockId(0, 1, 2, BlockID.FIRE)
        assertTrue(storage.hasBlocks())
        blockStorage.setBlockId(0, 1, 2, BlockID.AIR)
        assertTrue(storage.hasBlocks())
        blockStorage.recheckBlocks()
        assertFalse(storage.hasBlocks())
        blockStorage.setBlockId(0, 1, 2, BlockID.ACACIA_WALL_SIGN)
        assertTrue(storage.hasBlocks())
    }

    private fun unexpectedSetStorage(unexpected: LayerStorage) {
        fail("Unexpected call setting to $unexpected")
    }

    private val contentVersion: Int
        private get() = ChunkUpdater.getCurrentContentVersion()

    private fun getBlockStorage(layer: Int): BlockStorage {
        return getBlockStorage(storage, layer)
    }

    private fun getBlockStorage(layerStorage: LayerStorage?, layer: Int): BlockStorage {
        return layerStorage.getOrSetStorage({ unexpected: LayerStorage -> unexpectedSetStorage(unexpected) }, { contentVersion }, layer)
    }

    @Test
    fun testClone() {
        val blockStorage: BlockStorage = getBlockStorage(1)
        blockStorage.setBlockId(0, 1, 2, BlockID.FIRE)
        val clone: MultiLayerStorage = storage.clone()
        assertTrue(clone.hasBlocks())
        val blockStorageCloned: BlockStorage = getBlockStorage(clone, 1)
        assertNotEquals(blockStorage, blockStorageCloned)
        assertNotEquals(storage, clone)
        blockStorageCloned.setBlockId(2, 2, 2, BlockID.COBBLESTONE)
        assertEquals(BlockState.AIR, blockStorage.getBlockState(2, 2, 2))
    }

    @get:Test
    val storageOrEmpty: Unit
        get() {
            assertEquals(ImmutableBlockStorage.EMPTY, storage.getStorageOrEmpty(1))
            val blockStorage: BlockStorage = getBlockStorage(1)
            blockStorage.setBlockId(1, 2, 3, BlockID.STRUCTURE_VOID)
            assertThat(storage.getStorageOrEmpty(1)).isNotNull().isNotInstanceOf(ImmutableBlockStorage::class.java)
        }

    @get:Test
    val orSetStorage: Unit
        get() {
            val blockStorage: BlockStorage = getBlockStorage(1)
            assertEquals(blockStorage, getBlockStorage(1))
            blockStorage.setBlockId(1, 2, 3, BlockID.ACACIA_TRAPDOOR)
            assertEquals(blockStorage, getBlockStorage(1))
            assertThrows(IndexOutOfBoundsException::class.java) { storage.getOrSetStorage({ unexpected: LayerStorage -> unexpectedSetStorage(unexpected) }, { contentVersion }, 2) }
        }

    @get:Test
    val storageOrNull: Unit
        get() {
            assertNull(storage.getStorageOrNull(1))
            val blockStorage: BlockStorage = getBlockStorage(1)
            blockStorage.setBlockId(1, 2, 3, BlockID.STRUCTURE_VOID)
            assertThat(storage.getStorageOrNull(1)).isNotNull().isNotInstanceOf(ImmutableBlockStorage::class.java)
        }

    @Test
    fun delayPaletteUpdates() {
        val blockStorage: BlockStorage = getBlockStorage(1)
        assertFalse(blockStorage.isPaletteUpdateDelayed())
        storage.delayPaletteUpdates()
        assertTrue(blockStorage.isPaletteUpdateDelayed())
    }

    @Test
    fun size() {
        assertEquals(0, storage.size())
        getBlockStorage(0)
        assertEquals(1, storage.size())
        getBlockStorage(1)
        assertEquals(2, storage.size())
    }

    @Test
    fun compress() {
        val attemptedToSet = AtomicBoolean()
        val toEmpty: Consumer<LayerStorage> = Consumer<LayerStorage> { reduced ->
            assertThat(reduced).isEqualTo(LayerStorage.EMPTY)
            attemptedToSet.set(true)
        }
        storage.compress(toEmpty)
        assertTrue(attemptedToSet.get())
        attemptedToSet.set(false)
        getBlockStorage(0)
        storage.compress(toEmpty)
        assertTrue(attemptedToSet.get())
        attemptedToSet.set(false)
        getBlockStorage(1)
        storage.compress(toEmpty)
        assertTrue(attemptedToSet.get())
        getBlockStorage(0).setBlockId(1, 2, 3, BlockID.ACACIA_TRAPDOOR)
        val toSingle: Consumer<LayerStorage> = Consumer<LayerStorage> { reduced ->
            assertThat(reduced).isInstanceOf(SingleLayerStorage::class.java)
            attemptedToSet.set(true)
        }
        attemptedToSet.set(false)
        storage.compress(toSingle)
        assertTrue(attemptedToSet.get())
        getBlockStorage(1)
        attemptedToSet.set(false)
        storage.compress(toSingle)
        assertTrue(attemptedToSet.get())
        getBlockStorage(1).setBlockId(1, 2, 3, BlockID.STILL_WATER)
        storage.compress { unexpected: LayerStorage -> unexpectedSetStorage(unexpected) }
    }
}