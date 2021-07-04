package cn.nukkit.level.generator.populator.helper

import cn.nukkit.level.format.FullChunk

/**
 * @author DaPorkchop_
 */
interface EnsureBelow {
    companion object {
        fun ensureBelow(x: Int, y: Int, z: Int, id: Int, chunk: FullChunk): Boolean {
            return chunk.getBlockId(x, y - 1, z) === id
        }
    }
}