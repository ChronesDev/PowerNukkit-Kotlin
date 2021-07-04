package cn.nukkit.level.util

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

interface BitArray {
    operator fun set(index: Int, value: Int)
    operator fun get(index: Int): Int
    fun size(): Int
    val words: IntArray
    val version: cn.nukkit.level.util.BitArrayVersion
    fun copy(): BitArray
}