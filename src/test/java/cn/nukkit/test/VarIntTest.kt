package cn.nukkit.test

import cn.nukkit.utils.BinaryStream

/**
 * By lmlstarqaq http://snake1999.com/
 * Creation time: 2017/7/5 23:22.
 */
@DisplayName("VarInt")
internal class VarIntTest {
    @DisplayName("ZigZag")
    @Test
    fun testZigZag() {
        assertAll(
                { assertEquals(0x2468acf0, VarInt.encodeZigZag32(0x12345678)) },
                { assertEquals(0x2b826b1d, VarInt.encodeZigZag32(-0x15c1358f)) },
                { assertEquals(0x12345678, VarInt.decodeZigZag32(0x2468acf0)) },
                { assertEquals(-0x15c1358f, VarInt.decodeZigZag32(0x2b826b1d)) },
                { assertEquals(2623536930346282224L, VarInt.encodeZigZag64(0x1234567812345678L)) },
                { assertEquals(3135186066796324391L, VarInt.encodeZigZag64(-0x15c1358ef4131314L)) },
                { assertEquals(0x1234567812345678L, VarInt.decodeZigZag64(2623536930346282224L)) }
        ) { assertEquals(-0x15c1358ef4131314L, VarInt.decodeZigZag64(3135186066796324391L)) }
    }

    @DisplayName("Writing")
    @Test
    @Throws(IOException::class)
    fun testWrite() {
        val bs = BinaryStream()
        VarInt.writeUnsignedVarInt(bs, 237356812)
        VarInt.writeVarInt(bs, -0x15c1358f)
        VarInt.writeUnsignedVarLong(bs, 0x1234567812345678L)
        VarInt.writeVarLong(bs, -0x15c1358ef4131314L)
        assertAll(
                { assertEquals(237356812, VarInt.readUnsignedVarInt(bs)) },
                { assertEquals(-0x15c1358f, VarInt.readVarInt(bs)) },
                { assertEquals(0x1234567812345678L, VarInt.readUnsignedVarLong(bs)) }
        ) { assertEquals(-0x15c1358ef4131314L, VarInt.readVarLong(bs)) }
        val os = ByteArrayOutputStream()
        VarInt.writeUnsignedVarInt(os, 237356812)
        VarInt.writeVarInt(os, -0x15c1358f)
        VarInt.writeUnsignedVarLong(os, 0x1234567812345678L)
        VarInt.writeVarLong(os, -0x15c1358ef4131314L)
        VarInt.writeVarInt(os, 0x7FFFFFFF)
        VarInt.writeVarInt(os, -1)
        val `is` = ByteArrayInputStream(os.toByteArray())
        assertAll(
                { assertEquals(237356812, VarInt.readUnsignedVarInt(`is`)) },
                { assertEquals(-0x15c1358f, VarInt.readVarInt(`is`)) },
                { assertEquals(0x1234567812345678L, VarInt.readUnsignedVarLong(`is`)) },
                { assertEquals(-0x15c1358ef4131314L, VarInt.readVarLong(`is`)) },
                { assertEquals(0x7FFFFFFF, VarInt.readVarInt(`is`)) }
        ) { assertEquals(-1, VarInt.readVarInt(`is`)) }
    }

    @DisplayName("Write Sizes")
    @Test
    @Throws(IOException::class)
    fun testSizes() {
        sizeTest(TestConsumer<ByteArrayOutputStream> { w: ByteArrayOutputStream? -> VarInt.writeVarLong(w, 0x7FFFFFFF /* -1 >>> 1 */) }, 5)
        sizeTest(TestConsumer<ByteArrayOutputStream> { w: ByteArrayOutputStream? -> VarInt.writeVarInt(w, 0x7FFFFFFF /* -1 >>> 1 */) }, 5)
        sizeTest(TestConsumer<ByteArrayOutputStream> { w: ByteArrayOutputStream? -> VarInt.writeVarLong(w, -1) }, 1)
        sizeTest(TestConsumer<ByteArrayOutputStream> { w: ByteArrayOutputStream? -> VarInt.writeVarInt(w, -1) }, 1)
    }

    @DisplayName("Reading")
    @Test
    fun testRead() {
        assertAll(
                { assertEquals(2412, VarInt.readUnsignedVarInt(wrapBinaryStream("EC123EC456"))) },
                { assertEquals(583868, VarInt.readUnsignedVarInt(wrapBinaryStream("BCD123EFA0"))) },
                { assertEquals(1206, VarInt.readVarInt(wrapBinaryStream("EC123EC456"))) },
                { assertEquals(291934, VarInt.readVarInt(wrapBinaryStream("BCD123EFA0"))) },
                { assertEquals(6015, VarInt.readUnsignedVarLong(wrapBinaryStream("FF2EC456EC789EC012EC"))) },
                { assertEquals(3694, VarInt.readUnsignedVarLong(wrapBinaryStream("EE1CD34BCD56BCD78BCD"))) },
                { assertEquals(-3008, VarInt.readVarLong(wrapBinaryStream("FF2EC456EC789EC012EC"))) }
        ) { assertEquals(1847, VarInt.readVarLong(wrapBinaryStream("EE1CD34BCD56BCD78BCD"))) }
    }

    private interface TestConsumer<T> {
        @Throws(IOException::class)
        fun accept(t: T)
    }

    @Throws(IOException::class)
    private fun sizeTest(write: TestConsumer<ByteArrayOutputStream>, size: Int) {
        val os = ByteArrayOutputStream()
        write.accept(os)
        val `is` = ByteArrayInputStream(os.toByteArray())
        assertEquals(size, `is`.available())
    }

    companion object {
        private fun wrapBinaryStream(hex: String): BinaryStream {
            return BinaryStream(hexStringToByte(hex))
        }

        private fun hexStringToByte(hex: String): ByteArray {
            val len: Int = hex.length() / 2
            val result = ByteArray(len)
            val aChar: CharArray = hex.toCharArray()
            for (i in 0 until len) {
                val pos: Int = i * 2
                result[i] = (toByte(aChar[pos]) shl 4 or toByte(aChar[pos + 1])) as Byte
            }
            return result
        }

        private fun toByte(c: Char): Byte {
            return "0123456789ABCDEF".indexOf(c) as Byte
        }
    }
}