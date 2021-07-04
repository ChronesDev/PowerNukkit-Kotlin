package cn.nukkit.level.format

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author MagicDroidX (Nukkit Project)
 */
interface Chunk : FullChunk {
    fun isSectionEmpty(fY: Float): Boolean
    fun getSection(fY: Float): ChunkSection?
    fun setSection(fY: Float, section: ChunkSection?): Boolean
    val sections: Array<cn.nukkit.level.format.ChunkSection?>?

    class Entry(val chunkX: Int, val chunkZ: Int)
    companion object {
        const val SECTION_COUNT: Byte = 16
    }
}