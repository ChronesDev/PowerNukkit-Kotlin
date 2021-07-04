package cn.nukkit.test

import cn.nukkit.utils.Zlib

@DisplayName("Zlib")
internal class ZlibTest {
    @DisplayName("Inflate and Deflate")
    @Test
    @Throws(Exception::class)
    fun testAll() {
        val `in`: ByteArray = "lmlstarqaq".getBytes()
        val compressed: ByteArray = Zlib.deflate(`in`)
        val out: ByteArray = Zlib.inflate(compressed)
        assertArrayEquals(`in`, out)
    }
}