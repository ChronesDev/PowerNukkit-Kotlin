package cn.nukkit.level.generator.populator.helper

import cn.nukkit.level.format.FullChunk

/**
 * @author DaPorkchop_
 */
interface EnsureCover {
    companion object {
        fun ensureCover(x: Int, y: Int, z: Int, chunk: FullChunk): Boolean {
            val id: Int = chunk.getBlockId(x, y, z)
            return id == AIR || id == SNOW_LAYER
        }
    }
}