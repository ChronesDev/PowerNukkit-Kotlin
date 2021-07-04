package cn.nukkit.blockproperty

import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException

internal class BlockPropertyTest {
    var direction: BlockProperty<BlockFace> = CommonBlockProperties.FACING_DIRECTION
    @Test
    fun validateMeta() {
        assertThrows(InvalidBlockPropertyMetaException::class.java) { direction.validateMeta(7, 0) }
        assertThrows(InvalidBlockPropertyMetaException::class.java) { direction.validateMeta(7L, 0) }
        assertThrows(InvalidBlockPropertyMetaException::class.java) { direction.validateMeta(BigInteger.valueOf(7), 0) }
    }

    @get:Test
    val value: Unit
        get() {
            assertEquals(BlockFace.EAST, direction.getValue(13, 0))
            assertEquals(BlockFace.EAST, direction.getValue(13L, 0))
            assertEquals(BlockFace.EAST, direction.getValue(BigInteger.valueOf(13), 0))
        }

    @Test
    fun setValue() {
        assertEquals(12, direction.setValue(13, 0, BlockFace.WEST))
        assertEquals(12L, direction.setValue(13L, 0, BlockFace.WEST))
        assertEquals(BigInteger.valueOf(12), direction.setValue(BigInteger.valueOf(13), 0, BlockFace.WEST))
    }

    @Test
    fun setIntValue() {
        assertEquals(2, direction.getIntValue(-21, 2))
        assertEquals(2, direction.getIntValue(-21L, 2))
        assertEquals(2, direction.getIntValue(BigInteger.valueOf(-21), 2))
    }
}