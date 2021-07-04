package cn.nukkit.positiontracking

import it.unimi.dsi.fastutil.ints.IntArrayList

/**
 * @author joserobjr
 */
internal class PositionTrackingStorageTest {
    var temp: File? = null
    var storage: PositionTrackingStorage? = null
    @BeforeEach
    @Throws(IOException::class)
    fun setUp() {
        temp = File.createTempFile("PositionTrackingStorageTest_", ".pnt")
        temp.deleteOnExit()
    }

    @AfterEach
    @Throws(IOException::class)
    fun tearDown() {
        if (storage != null) {
            storage.close()
        }
        assertTrue(!temp.isFile() || temp.delete(), "Failed to delete the temporary file $temp")
    }

    @Test
    @Throws(IOException::class)
    fun constructor() {
        PositionTrackingStorage(1, temp, 2).use { storage ->
            assertEquals(1, storage.getStartingHandler())
            assertEquals(2, storage.getMaxHandler())
        }
        assertEquals(10 + 4 + 4 + 4 + (1 + 8 + 4 + 8 + 8 + 8) * 2 + 4 + (8 + 4) * 15, temp.length())
        PositionTrackingStorage(1, temp).use { storage ->
            assertEquals(1, storage.getStartingHandler())
            assertEquals(2, storage.getMaxHandler())
        }
        assertThrows(IllegalArgumentException::class.java) {
            val storage = PositionTrackingStorage(2, temp)
            storage.close()
        }
        assertTrue(temp.delete())
        assertThrows(IllegalArgumentException::class.java) {
            val storage = PositionTrackingStorage(0, temp)
            storage.close()
        }
        assertThrows(IllegalArgumentException::class.java) {
            val storage = PositionTrackingStorage(-1, temp)
            storage.close()
        }
    }

    @Test
    @Throws(IOException::class)
    fun close() {
        storage = PositionTrackingStorage(1, temp)
        storage.close()
        assertThrows(IOException::class.java) { storage.addNewPosition(PositionTracking("x", 1, 2, 3)) }
        assertTrue(temp.delete())
    }

    @Test
    @Throws(IOException::class)
    fun addNewPosition() {
        storage = PositionTrackingStorage(10, temp)
        val pos1 = PositionTracking("test world", 2.33, 4.55, 6.77)
        var result: OptionalInt = storage.addNewPosition(pos1)
        assertTrue(result.isPresent())
        assertEquals(10, result.getAsInt())
        result = storage.addNewPosition(pos1)
        assertTrue(result.isPresent())
        assertEquals(11, result.getAsInt())
        val pos2 = PositionTracking("test world", 2, 4, 6)
        result = storage.addNewPosition(pos2)
        assertTrue(result.isPresent())
        assertEquals(12, result.getAsInt())
        result = storage.addNewPosition(pos2)
        assertTrue(result.isPresent())
        assertEquals(13, result.getAsInt())
        result = storage.addNewPosition(pos1, false)
        assertTrue(result.isPresent())
        assertEquals(14, result.getAsInt())
        result = storage.addNewPosition(pos1, false)
        assertTrue(result.isPresent())
        assertEquals(15, result.getAsInt())
        result = storage.addNewPosition(pos2, false)
        assertTrue(result.isPresent())
        assertEquals(16, result.getAsInt())
        result = storage.addNewPosition(pos2, false)
        assertTrue(result.isPresent())
        assertEquals(17, result.getAsInt())
        assertEquals(pos1, storage.getPosition(10))
        assertEquals(pos1, storage.getPosition(11))
        assertEquals(pos1, storage.getPosition(10, false))
        assertEquals(pos1, storage.getPosition(11, false))
        assertEquals(pos2, storage.getPosition(12))
        assertEquals(pos2, storage.getPosition(13))
        assertEquals(pos2, storage.getPosition(12, false))
        assertEquals(pos2, storage.getPosition(13, false))
        assertNull(storage.getPosition(14))
        assertNull(storage.getPosition(15))
        assertNull(storage.getPosition(16))
        assertNull(storage.getPosition(17))
        assertNull(storage.getPosition(14, true))
        assertNull(storage.getPosition(15, true))
        assertNull(storage.getPosition(16, true))
        assertNull(storage.getPosition(17, true))
        assertEquals(pos1, storage.getPosition(14, false))
        assertEquals(pos1, storage.getPosition(15, false))
        assertEquals(pos2, storage.getPosition(16, false))
        assertEquals(pos2, storage.getPosition(17, false))
        assertTrue(storage.hasPosition(10))
        assertTrue(storage.hasPosition(11))
        assertTrue(storage.hasPosition(12))
        assertTrue(storage.hasPosition(13))
        assertFalse(storage.hasPosition(14))
        assertFalse(storage.hasPosition(15))
        assertFalse(storage.hasPosition(16))
        assertFalse(storage.hasPosition(17))
        assertTrue(storage.hasPosition(14, false))
        assertTrue(storage.hasPosition(15, false))
        assertTrue(storage.hasPosition(16, false))
        assertTrue(storage.hasPosition(17, false))
        assertFalse(storage.hasPosition(18, false))
        assertTrue(storage.isEnabled(10))
        assertTrue(storage.isEnabled(11))
        assertTrue(storage.isEnabled(12))
        assertTrue(storage.isEnabled(13))
        assertFalse(storage.isEnabled(14))
        assertFalse(storage.isEnabled(15))
        assertFalse(storage.isEnabled(16))
        assertFalse(storage.isEnabled(17))
        assertFalse(storage.isEnabled(18))
        storage.close()
        storage = PositionTrackingStorage(10, temp)
        assertEquals(pos1, storage.getPosition(10))
        assertEquals(pos1, storage.getPosition(11))
        assertEquals(pos1, storage.getPosition(10, false))
        assertEquals(pos1, storage.getPosition(11, false))
        assertEquals(pos2, storage.getPosition(12))
        assertEquals(pos2, storage.getPosition(13))
        assertEquals(pos2, storage.getPosition(12, false))
        assertEquals(pos2, storage.getPosition(13, false))
        assertNull(storage.getPosition(14))
        assertNull(storage.getPosition(15))
        assertNull(storage.getPosition(16))
        assertNull(storage.getPosition(17))
        assertNull(storage.getPosition(14, true))
        assertNull(storage.getPosition(15, true))
        assertNull(storage.getPosition(16, true))
        assertNull(storage.getPosition(17, true))
        assertEquals(pos1, storage.getPosition(14, false))
        assertEquals(pos1, storage.getPosition(15, false))
        assertEquals(pos2, storage.getPosition(16, false))
        assertEquals(pos2, storage.getPosition(17, false))
        assertTrue(storage.hasPosition(10))
        assertTrue(storage.hasPosition(11))
        assertTrue(storage.hasPosition(12))
        assertTrue(storage.hasPosition(13))
        assertFalse(storage.hasPosition(14))
        assertFalse(storage.hasPosition(15))
        assertFalse(storage.hasPosition(16))
        assertFalse(storage.hasPosition(17))
        assertTrue(storage.hasPosition(14, false))
        assertTrue(storage.hasPosition(15, false))
        assertTrue(storage.hasPosition(16, false))
        assertTrue(storage.hasPosition(17, false))
        assertFalse(storage.hasPosition(18, false))
        assertTrue(storage.isEnabled(10))
        assertTrue(storage.isEnabled(11))
        assertTrue(storage.isEnabled(12))
        assertTrue(storage.isEnabled(13))
        assertFalse(storage.isEnabled(14))
        assertFalse(storage.isEnabled(15))
        assertFalse(storage.isEnabled(16))
        assertFalse(storage.isEnabled(17))
        assertFalse(storage.isEnabled(18))
    }

    @Test
    @Throws(IOException::class)
    fun sizeLimit() {
        val pos1 = PositionTracking("pos1", 1, 2, 3)
        val pos2 = PositionTracking("pos2", 4, 5, 6)
        val pos3 = PositionTracking("pos3", 7, 8, 9)
        storage = PositionTrackingStorage(5, temp, 3)
        var result: OptionalInt = storage.addOrReusePosition(pos1)
        assertTrue(result.isPresent())
        assertEquals(5, result.getAsInt())
        assertEquals(pos1, storage.getPosition(5))
        result = storage.addOrReusePosition(pos1)
        assertTrue(result.isPresent())
        assertEquals(5, result.getAsInt())
        assertEquals(pos1, storage.getPosition(5))
        result = storage.addNewPosition(pos1)
        assertTrue(result.isPresent())
        assertEquals(6, result.getAsInt())
        assertEquals(pos1, storage.getPosition(6))
        assertEquals(pos1, storage.getPosition(5))
        result = storage.addOrReusePosition(pos1)
        assertTrue(result.isPresent())
        assertTrue(result.getAsInt() === 6 || result.getAsInt() === 5)
        assertEquals(pos1, storage.getPosition(6))
        assertEquals(pos1, storage.getPosition(5))

        // 2
        result = storage.addOrReusePosition(pos2)
        assertTrue(result.isPresent())
        assertEquals(7, result.getAsInt())
        assertEquals(pos2, storage.getPosition(7))
        result = storage.addOrReusePosition(pos2)
        assertTrue(result.isPresent())
        assertEquals(7, result.getAsInt())
        assertEquals(pos2, storage.getPosition(7))
        result = storage.addNewPosition(pos2)
        assertFalse(result.isPresent())
        result = storage.addNewPosition(pos1)
        assertFalse(result.isPresent())
        result = storage.addOrReusePosition(pos1)
        assertTrue(result.isPresent())
        assertTrue(result.getAsInt() === 6 || result.getAsInt() === 5)
        assertEquals(pos1, storage.getPosition(6))
        assertEquals(pos1, storage.getPosition(5))
        result = storage.addOrReusePosition(pos2)
        assertTrue(result.isPresent())
        assertEquals(7, result.getAsInt())
        assertEquals(pos2, storage.getPosition(7))

        // 3
        result = storage.addNewPosition(pos3)
        assertFalse(result.isPresent())
        result = storage.addOrReusePosition(pos3)
        assertFalse(result.isPresent())
    }

    @Test
    @Throws(IOException::class)
    fun findTrackingHandler() {
        val pos1 = PositionTracking("pos1", 1, 2, 3)
        val pos2 = PositionTracking("pos2", 4, 5, 6)
        val pos3 = PositionTracking("pos3", 7, 8, 9)
        val pos4 = PositionTracking("pos4", 10, 11, 12)
        val handlers2: IntList = IntArrayList()
        val handlers3: IntList = IntArrayList()
        storage = PositionTrackingStorage(300, temp)
        val handler1: Int = storage.addNewPosition(pos1).orElseThrow { IOException() }
        handlers3.add(storage.addNewPosition(pos3).orElseThrow { IOException() })
        val disabled: Int = storage.addNewPosition(pos2, false).orElseThrow { IOException() }
        handlers3.add(storage.addNewPosition(pos3).orElseThrow { IOException() })
        handlers2.add(storage.addNewPosition(pos2).orElseThrow { IOException() })
        handlers3.add(storage.addNewPosition(pos3).orElseThrow { IOException() })
        handlers2.add(storage.addNewPosition(pos2).orElseThrow { IOException() })
        val handler4: Int = storage.addNewPosition(pos4, false).orElseThrow { IOException() }
        assertEquals(OptionalInt.of(handler1), storage.findTrackingHandler(pos1))
        assertEquals(handlers2, storage.findTrackingHandlers(pos2))
        assertEquals(handlers3, storage.findTrackingHandlers(pos3))
        handlers2.add(0, disabled)
        assertEquals(handlers2, storage.findTrackingHandlers(pos2, false))
        assertEquals(IntArrayList(), storage.findTrackingHandlers(pos4, true))
        assertEquals(Collections.singletonList(handler1), storage.findTrackingHandlers(pos1))
        assertEquals(Collections.singletonList(handler4), storage.findTrackingHandlers(pos4, false))
        assertTrue(handlers2.contains(storage.findTrackingHandler(pos2).orElse(0)))
        assertTrue(handlers3.contains(storage.findTrackingHandler(pos3).orElse(0)))
    }

    @Test
    @Throws(IOException::class)
    fun invalidateHandler() {
        storage = PositionTrackingStorage(3, temp)
        val pos1 = PositionTracking("pos1", 1, 2, 3)
        assertTrue(storage.addNewPosition(pos1).isPresent())
        assertTrue(storage.isEnabled(3))
        assertTrue(storage.hasPosition(3))
        assertTrue(storage.hasPosition(3, true))
        assertTrue(storage.hasPosition(3, true))
        assertFalse(storage.setEnabled(3, true))
        assertTrue(storage.setEnabled(3, false))
        assertFalse(storage.isEnabled(3))
        assertFalse(storage.hasPosition(3))
        assertFalse(storage.hasPosition(3, true))
        assertTrue(storage.hasPosition(3, false))
        assertTrue(storage.setEnabled(3, true))
        assertTrue(storage.isEnabled(3))
        assertFalse(storage.setEnabled(3, true))
        assertTrue(storage.isEnabled(3))
        assertTrue(storage.hasPosition(3))
        assertTrue(storage.hasPosition(3, true))
        assertTrue(storage.hasPosition(3, true))
        storage.invalidateHandler(3)
        assertFalse(storage.isEnabled(3))
        assertFalse(storage.setEnabled(3, true))
        assertFalse(storage.setEnabled(3, false))
        assertFalse(storage.hasPosition(3))
        assertFalse(storage.hasPosition(3, true))
        assertFalse(storage.hasPosition(3, false))
        storage.close()
        val size: Long = temp.length()
        storage = PositionTrackingStorage(3, temp)
        val pos2 = PositionTracking("pos2", 3, 2, 3)
        assertEquals(4, storage.addOrReusePosition(pos2).orElse(0))
        storage.close()
        assertEquals(size, temp.length())
        storage = PositionTrackingStorage(3, temp)
        assertEquals(4, storage.addOrReusePosition(pos2).orElse(0))
        assertEquals(5, storage.addOrReusePosition(pos1).orElse(0))
    }
}