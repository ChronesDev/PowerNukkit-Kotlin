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
package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 * @since 2020-10-04
 */
@ExtendWith(PowerNukkitExtension::class)
internal class BlockTest {
    companion object {
        @get:Override
        @SuppressWarnings("deprecation")
        val id: Int = Block.list.length - 2
            get() = Companion.field
        private val MUTABLE_STATE: Field? = null
        private val HUGE: UnsignedIntBlockProperty = UnsignedIntBlockProperty("huge", false, -0x1)

        init {
            try {
                MUTABLE_STATE = Block::class.java.getDeclaredField("mutableState")
                MUTABLE_STATE.setAccessible(true)
            } catch (e: NoSuchFieldException) {
                throw ExceptionInInitializerError(e)
            }
        }
    }

    var block: BlockTestBlock? = null
    @BeforeEach
    fun setUp() {
        Block.registerBlockImplementation(id, BlockTestBlock::class.java, "test:testblock", false)
        block = Block.get(id)
        assertNull(directMutableState)
    }

    @Test
    fun setGetDamage() {
        assertNull(directMutableState)
        assertEquals(0, block.getDamage())
        block.setDamage(0)
        assertNull(directMutableState)
        assertEquals(0, block.getDamage())
        block.setDamage(1)
        assertNotNull(directMutableState)
        assertEquals(1, block.getDamage())
    }

    @get:Test
    val bigDamage: Unit
        get() {
            assertNull(directMutableState)
            assertEquals(0, block.getBigDamage())
            block.setDamage(0)
            assertNull(directMutableState)
            assertEquals(0, block.getBigDamage())
            block.setDamage(1)
            assertNotNull(directMutableState)
            assertEquals(1, block.getBigDamage())
        }

    @get:Test
    val hugeDamage: Unit
        get() {
            assertNull(directMutableState)
            assertEquals(BigInteger.ZERO, block.getHugeDamage())
            block.setDamage(0)
            assertNull(directMutableState)
            assertEquals(BigInteger.ZERO, block.getHugeDamage())
            block.setDamage(1)
            assertNotNull(directMutableState)
            assertEquals(BigInteger.ONE, block.getHugeDamage())
        }

    @Test
    fun equalsCheckingDamage() {
        var b2: Block = Block.get(id)
        assertNull(directMutableState)
        assertNull(getDirectMutableState(b2))
        assertTrue(Block.equals(block, b2, true))
        assertNull(directMutableState)
        assertNull(getDirectMutableState(b2))
        block.setDamage(1)
        assertFalse(Block.equals(block, b2, true))
        assertNotNull(directMutableState)
        assertNull(getDirectMutableState(b2))
        block = Block.get(id)
        b2.setDamage(1)
        assertFalse(Block.equals(block, b2, true))
        assertNull(directMutableState)
        assertNotNull(getDirectMutableState(b2))
        block.setDamage(1)
        assertTrue(Block.equals(block, b2, true))
        assertNotNull(directMutableState)
        assertNotNull(getDirectMutableState(b2))
        block = Block.get(id)
        assertFalse(Block.equals(block, b2, true))
        b2 = Block.get(BlockID.STONE)
        assertNull(directMutableState)
        assertNull(getDirectMutableState(b2))
    }

    @Test
    fun setState() {
        assertNull(directMutableState)
        block.setState(BlockState.of(id, 0))
        assertNull(directMutableState)
        block.setState(BlockState.of(id, 1))
        assertNotNull(directMutableState)
        assertEquals(1, block.getDamage())
    }

    @Test
    fun setDataStorageNoRepair() {
        assertNull(directMutableState)
        block.setDataStorage(0)
        assertNull(directMutableState)
        block.setDataStorage(1)
        assertNotNull(directMutableState)
        assertEquals(1, block.getDamage())
    }

    @Test
    fun setDataStorageRepair() {
        assertNull(directMutableState)
        block.setDataStorage(0, true)
        assertNull(directMutableState)
        block.setDataStorage(1, true)
        assertNotNull(directMutableState)
        assertEquals(1, block.getDamage())
    }

    @Test
    fun setDataStorageRepairConsumer() {
        assertNull(directMutableState)
        val blockStateRepairConsumer: Consumer<BlockStateRepair> = Consumer<BlockStateRepair> { repair -> throw AssertionError("Shouldn't be called") }
        block.setDataStorage(0, true, blockStateRepairConsumer)
        assertNull(directMutableState)
        block.setDataStorage(1, true, blockStateRepairConsumer)
        assertNotNull(directMutableState)
        assertEquals(1, block.getDamage())
    }

    @Test
    fun setDataStorageFromIntNoRepair() {
        assertNull(directMutableState)
        block.setDataStorageFromInt(0)
        assertNull(directMutableState)
        block.setDataStorageFromInt(1)
        assertNotNull(directMutableState)
        assertEquals(1, block.getDamage())
    }

    @Test
    fun setDataStorageFromIntRepair() {
        assertNull(directMutableState)
        block.setDataStorageFromInt(0, true)
        assertNull(directMutableState)
        block.setDataStorageFromInt(1, true)
        assertNotNull(directMutableState)
        assertEquals(1, block.getDamage())
    }

    @Test
    fun setDataStorageFromIntRepairConsumer() {
        assertNull(directMutableState)
        val blockStateRepairConsumer: Consumer<BlockStateRepair> = Consumer<BlockStateRepair> { repair -> throw AssertionError("Shouldn't be called") }
        block.setDataStorageFromInt(0, true, blockStateRepairConsumer)
        assertNull(directMutableState)
        block.setDataStorageFromInt(1, true, blockStateRepairConsumer)
        assertNotNull(directMutableState)
        assertEquals(1, block.getDamage())
    }

    @Test
    fun setGetPropertyValueTypeBool() {
        assertNull(directMutableState)
        block.setPropertyValue(TOGGLE, false)
        assertNull(directMutableState)
        block.setPropertyValue(TOGGLE.getName(), false)
        assertNull(directMutableState)
        assertFalse(block.getPropertyValue(TOGGLE))
        assertNull(directMutableState)
        assertEquals(Boolean.FALSE, block.getPropertyValue(TOGGLE.getName()))
        assertNull(directMutableState)
        block.setPropertyValue(TOGGLE, true)
        assertNotNull(directMutableState)
        assertTrue(block.getPropertyValue(TOGGLE))
        val b2: Block = Block.get(id)
        b2.setPropertyValue(TOGGLE.getName(), true)
        assertNotNull(getDirectMutableState(b2))
        assertEquals(Boolean.TRUE, b2.getPropertyValue(TOGGLE.getName()))
    }

    @Test
    fun setGetPropertyValueTypeInt() {
        assertNull(directMutableState)
        block.setPropertyValue(REDSTONE_SIGNAL, 0)
        assertNull(directMutableState)
        block.setPropertyValue(REDSTONE_SIGNAL.getName(), 0)
        assertNull(directMutableState)
        assertEquals(0, block.getPropertyValue(REDSTONE_SIGNAL))
        assertNull(directMutableState)
        assertEquals(0, block.getPropertyValue(REDSTONE_SIGNAL.getName()))
        assertNull(directMutableState)
        block.setPropertyValue(REDSTONE_SIGNAL, 1)
        assertNotNull(directMutableState)
        assertEquals(1, block.getPropertyValue(REDSTONE_SIGNAL))
        val b2: Block = Block.get(id)
        b2.setPropertyValue(REDSTONE_SIGNAL.getName(), 1)
        assertNotNull(getDirectMutableState(b2))
        assertEquals(1, b2.getPropertyValue(REDSTONE_SIGNAL.getName()))
    }

    @Test
    fun setGetPropertyValueTypeArray() {
        assertNull(directMutableState)
        block.setPropertyValue(FACING_DIRECTION, BlockFace.DOWN)
        assertNull(directMutableState)
        block.setPropertyValue(FACING_DIRECTION.getName(), BlockFace.DOWN)
        assertNull(directMutableState)
        assertEquals(BlockFace.DOWN, block.getPropertyValue(FACING_DIRECTION))
        assertNull(directMutableState)
        assertEquals(BlockFace.DOWN, block.getPropertyValue(FACING_DIRECTION.getName()))
        assertNull(directMutableState)
        block.setPropertyValue(FACING_DIRECTION, BlockFace.UP)
        assertNotNull(directMutableState)
        assertEquals(BlockFace.UP, block.getPropertyValue(FACING_DIRECTION))
        val b2: Block = Block.get(id)
        b2.setPropertyValue(FACING_DIRECTION.getName(), BlockFace.UP)
        assertNotNull(getDirectMutableState(b2))
        assertEquals(BlockFace.UP, b2.getPropertyValue(FACING_DIRECTION.getName()))
    }

    @Test
    fun setGetPropertyValueTypeUnsignedInt() {
        assertNull(directMutableState)
        block.setPropertyValue(HUGE, 0)
        assertNull(directMutableState)
        block.setPropertyValue(HUGE.getName(), 0)
        assertNull(directMutableState)
        assertEquals(0, block.getPropertyValue(HUGE))
        assertNull(directMutableState)
        assertEquals(0, block.getPropertyValue(HUGE.getName()))
        assertNull(directMutableState)
        block.setPropertyValue(HUGE, 1)
        assertNotNull(directMutableState)
        assertEquals(1, block.getPropertyValue(HUGE))
        val b2: Block = Block.get(id)
        b2.setPropertyValue(HUGE.getName(), 1)
        assertNotNull(getDirectMutableState(b2))
        assertEquals(1, b2.getPropertyValue(HUGE.getName()))
    }

    @Test
    fun setGetBooleanValue() {
        assertNull(directMutableState)
        block.setBooleanValue(TOGGLE, false)
        assertNull(directMutableState)
        block.setBooleanValue(TOGGLE.getName(), false)
        assertNull(directMutableState)
        assertFalse(block.getBooleanValue(TOGGLE))
        assertNull(directMutableState)
        assertFalse(block.getBooleanValue(TOGGLE.getName()))
        assertNull(directMutableState)
        block.setBooleanValue(TOGGLE, true)
        assertNotNull(directMutableState)
        assertTrue(block.getBooleanValue(TOGGLE))
        val b2: Block = Block.get(id)
        b2.setBooleanValue(TOGGLE.getName(), true)
        assertNotNull(getDirectMutableState(b2))
        assertTrue(b2.getBooleanValue(TOGGLE.getName()))
    }

    @Test
    fun setGetIntValueInt() {
        assertNull(directMutableState)
        block.setIntValue(REDSTONE_SIGNAL, 0)
        assertNull(directMutableState)
        block.setIntValue(REDSTONE_SIGNAL.getName(), 0)
        assertNull(directMutableState)
        assertEquals(0, block.getIntValue(REDSTONE_SIGNAL))
        assertNull(directMutableState)
        assertEquals(0, block.getIntValue(REDSTONE_SIGNAL.getName()))
        assertNull(directMutableState)
        block.setIntValue(REDSTONE_SIGNAL, 1)
        assertNotNull(directMutableState)
        assertEquals(1, block.getIntValue(REDSTONE_SIGNAL))
        val b2: Block = Block.get(id)
        b2.setIntValue(REDSTONE_SIGNAL.getName(), 1)
        assertNotNull(getDirectMutableState(b2))
        assertEquals(1, b2.getIntValue(REDSTONE_SIGNAL.getName()))
    }

    @Test
    fun setGetIntValueUnsigned() {
        assertNull(directMutableState)
        block.setIntValue(HUGE, 0)
        assertNull(directMutableState)
        block.setIntValue(HUGE.getName(), 0)
        assertNull(directMutableState)
        assertEquals(0, block.getIntValue(HUGE))
        assertNull(directMutableState)
        assertEquals(0, block.getIntValue(HUGE.getName()))
        assertNull(directMutableState)
        block.setIntValue(HUGE, 1)
        assertNotNull(directMutableState)
        assertEquals(1, block.getIntValue(HUGE))
        val b2: Block = Block.get(id)
        b2.setIntValue(HUGE.getName(), 1)
        assertNotNull(getDirectMutableState(b2))
        assertEquals(1, b2.getIntValue(HUGE.getName()))
    }

    @get:Test
    val persistenceValue: Unit
        get() {
            assertNull(directMutableState)
            assertEquals("0", block.getPersistenceValue(FACING_DIRECTION))
            assertNull(directMutableState)
            assertEquals("0", block.getPersistenceValue(FACING_DIRECTION.getName()))
            assertNull(directMutableState)
            block.setPropertyValue(FACING_DIRECTION, BlockFace.UP)
            assertNotNull(directMutableState)
            assertEquals("1", block.getPersistenceValue(FACING_DIRECTION))
            assertEquals("1", block.getPersistenceValue(FACING_DIRECTION.getName()))
        }
    private val directMutableState: MutableBlockState
        private get() = getDirectMutableState(block)

    @SneakyThrows
    private fun getDirectMutableState(b: Block?): MutableBlockState {
        return MUTABLE_STATE.get(b) as MutableBlockState
    }

    class BlockTestBlock @JvmOverloads constructor(meta: Int = 0) : BlockMeta(meta) {
        @get:Override
        val name: String
            get() = "Test Block"

        @get:Override
        @get:Nonnull
        @get:PowerNukkitOnly
        @get:Since("1.4.0.0-PN")
        val properties: BlockProperties
            get() = PROPERTIES

        companion object {
            var PROPERTIES: BlockProperties = BlockProperties(FACING_DIRECTION, TOGGLE, REDSTONE_SIGNAL, HUGE)
        }
    }
}