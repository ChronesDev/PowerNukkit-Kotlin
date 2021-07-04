package cn.nukkit.math

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author MagicDroidX (Nukkit Project)
 */
class SimpleAxisAlignedBB : AxisAlignedBB {
    override var minX: Double
        @Override get
        @Override set
    override var minY: Double
        @Override get
        @Override set
    override var minZ: Double
        @Override get
        @Override set
    override var maxX: Double
        @Override get
        @Override set
    override var maxY: Double
        @Override get
        @Override set
    override var maxZ: Double
        @Override get
        @Override set

    constructor(pos1: Vector3, pos2: Vector3) {
        minX = Math.min(pos1.x, pos2.x)
        minY = Math.min(pos1.y, pos2.y)
        minZ = Math.min(pos1.z, pos2.z)
        maxX = Math.max(pos1.x, pos2.x)
        maxY = Math.max(pos1.y, pos2.y)
        maxZ = Math.max(pos1.z, pos2.z)
    }

    constructor(minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double) {
        this.minX = minX
        this.minY = minY
        this.minZ = minZ
        this.maxX = maxX
        this.maxY = maxY
        this.maxZ = maxZ
    }

    @Override
    override fun toString(): String {
        return "AxisAlignedBB(" + minX + ", " + minY + ", " + minZ + ", " + maxX + ", " + maxY + ", " + maxZ + ")"
    }

    @Override
    override fun clone(): AxisAlignedBB {
        return SimpleAxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ)
    }
}