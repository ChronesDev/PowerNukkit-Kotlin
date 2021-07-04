package cn.nukkit.level.generator.populator.helper

import cn.nukkit.level.format.FullChunk

/**
 * @author DaPorkchop_
 */
interface EnsureGrassBelow {
    companion object {
        fun ensureGrassBelow(x: Int, y: Int, z: Int, chunk: FullChunk): Boolean {
            return EnsureBelow.ensureBelow(x, y, z, GRASS, chunk)
        }
    }
}