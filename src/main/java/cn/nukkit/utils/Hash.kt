package cn.nukkit.utils

import cn.nukkit.math.Vector3

object Hash {
    fun hashBlock(x: Int, y: Int, z: Int): Long {
        return y + (x.toLong() and 0x3FFFFFF shl 8) + (z.toLong() and 0x3FFFFFF shl 34)
    }

    fun hashBlockX(triple: Long): Int {
        return (triple shr 8 and 0x3FFFFFF shl 38 shr 38).toInt()
    }

    fun hashBlockY(triple: Long): Int {
        return (triple and 0xFF).toInt()
    }

    fun hashBlockZ(triple: Long): Int {
        return (triple shr 34 and 0x3FFFFFF shl 38 shr 38).toInt()
    }

    /**
     * @since 1.2.1.0-PN
     */
    fun hashBlock(blockPos: Vector3): Long {
        return hashBlock(blockPos.getFloorX(), blockPos.getFloorY(), blockPos.getFloorZ())
    }
}