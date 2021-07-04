package cn.nukkit.utils

import java.io.IOException

/**
 *
 * @author ScraMTeam
 */
internal interface ZlibProvider {
    @Throws(IOException::class)
    fun deflate(data: Array<ByteArray?>?, level: Int): ByteArray

    @Throws(IOException::class)
    fun deflate(data: ByteArray?, level: Int): ByteArray

    @Throws(IOException::class)
    fun inflate(data: ByteArray?, maxSize: Int): ByteArray
}