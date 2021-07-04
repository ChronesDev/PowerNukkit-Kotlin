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
internal class SingleLayerStorageTest {
    var storage: SingleLayerStorage? = null
    @BeforeEach
    fun setUp() {
        storage = SingleLayerStorage()
    }

    @Test
    fun hasBlocks() {
        assertFalse(storage.hasBlocks())
        val blockStorage: BlockStorage = blockStorage
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
    private val blockStorage: BlockStorage
        private get() = getBlockStorage(storage)

    private fun getBlockStorage(layerStorage: LayerStorage?): BlockStorage {
        return layerStorage.getOrSetStorage({ unexpected: LayerStorage -> unexpectedSetStorage(unexpected) }, { contentVersion }, 0)
    }

    @Test
    fun testClone() {
        val blockStorage: BlockStorage = blockStorage
        blockStorage.setBlockId(0, 1, 2, BlockID.FIRE)
        val clone: SingleLayerStorage = storage.clone()
        assertTrue(clone.hasBlocks())
        val blockStorageCloned: BlockStorage = getBlockStorage(clone)
        assertNotEquals(blockStorage, blockStorageCloned)
        assertNotEquals(storage, clone)
        blockStorageCloned.setBlockId(2, 2, 2, BlockID.COBBLESTONE)
        assertEquals(BlockState.AIR, blockStorage.getBlockState(2, 2, 2))
    }

    @get:Test
    val storageOrEmpty: Unit
        get() {
            assertEquals(ImmutableBlockStorage.EMPTY, storage.getStorageOrEmpty(0))
            val blockStorage: BlockStorage = blockStorage
            blockStorage.setBlockId(1, 2, 3, BlockID.STRUCTURE_VOID)
            assertThat(storage.getStorageOrEmpty(0)).isNotNull().isNotInstanceOf(ImmutableBlockStorage::class.java)
        }

    @get:Test
    val orSetStorage: Unit
        get() {
            val blockStorage: BlockStorage = blockStorage
            assertEquals(blockStorage, blockStorage)
            blockStorage.setBlockId(1, 2, 3, BlockID.ACACIA_TRAPDOOR)
            assertEquals(blockStorage, blockStorage)
            val attemptedToSet = AtomicBoolean()
            storage.getOrSetStorage({ expanded ->
                assertThat(expanded).isInstanceOf(MultiLayerStorage::class.java)
                assertThat(expanded.getStorageOrNull(1)).isNotNull().isNotInstanceOf(ImmutableBlockStorage::class.java)
                attemptedToSet.set(true)
            }, { contentVersion }, 1)
            assertTrue(attemptedToSet.get())
        }

    @get:Test
    val storageOrNull: Unit
        get() {
            assertNull(storage.getStorageOrNull(0))
            val blockStorage: BlockStorage = blockStorage
            blockStorage.setBlockId(1, 2, 3, BlockID.STRUCTURE_VOID)
            assertThat(storage.getStorageOrNull(0)).isNotNull().isNotInstanceOf(ImmutableBlockStorage::class.java)
        }

    @Test
    fun delayPaletteUpdates() {
        val blockStorage: BlockStorage = blockStorage
        assertFalse(blockStorage.isPaletteUpdateDelayed())
        storage.delayPaletteUpdates()
        assertTrue(blockStorage.isPaletteUpdateDelayed())
    }

    @Test
    fun size() {
        assertEquals(0, storage.size())
        blockStorage
        assertEquals(1, storage.size())
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
        blockStorage
        storage.compress(toEmpty)
        assertTrue(attemptedToSet.get())
        blockStorage.setBlockId(1, 2, 3, BlockID.ACACIA_TRAPDOOR)
        storage.compress { unexpected: LayerStorage -> unexpectedSetStorage(unexpected) }
    }
}