package cn.nukkit.level

import cn.nukkit.math.MathHelper

/**
 * @author Adam Matthew (Nukkit Project)
 */
class ChunkPosition(val x: Int, val y: Int, val z: Int) {
    constructor(vec3d: Vector3) : this(MathHelper.floor(vec3d.x), MathHelper.floor(vec3d.y), MathHelper.floor(vec3d.z)) {}

    @Override
    override fun equals(`object`: Object): Boolean {
        return if (`object` !is ChunkPosition) {
            false
        } else {
            val chunkposition = `object` as ChunkPosition
            chunkposition.x == x && chunkposition.y == y && chunkposition.z == z
        }
    }

    @Override
    override fun hashCode(): Int {
        return x * 8976890 + y * 981131 + z
    }
}