package cn.nukkit.math

import cn.nukkit.api.PowerNukkitOnly

class Vector3f @JvmOverloads constructor(var south: Float = 0f, var up: Float = 0f, var west: Float = 0f) : Cloneable {
    val floorX: Int
        get() = NukkitMath.floorFloat(south)
    val floorY: Int
        get() = NukkitMath.floorFloat(up)
    val floorZ: Int
        get() = NukkitMath.floorFloat(west)

    @JvmOverloads
    fun add(x: Float, y: Float = 0f, z: Float = 0f): Vector3f {
        return Vector3f(south + x, up + y, west + z)
    }

    fun add(x: Vector3f): Vector3f {
        return Vector3f(south + x.south, up + x.up, west + x.west)
    }

    @JvmOverloads
    fun subtract(x: Float = 0f, y: Float = 0f, z: Float = 0f): Vector3f {
        return this.add(-x, -y, -z)
    }

    fun subtract(x: Vector3f): Vector3f {
        return this.add(-x.south, -x.up, -x.west)
    }

    fun multiply(number: Float): Vector3f {
        return Vector3f(south * number, up * number, west * number)
    }

    fun divide(number: Float): Vector3f {
        return Vector3f(south / number, up / number, west / number)
    }

    fun ceil(): Vector3f {
        return Vector3f((Math.ceil(south) as Int).toFloat(), (Math.ceil(up) as Int).toFloat(), (Math.ceil(west) as Int).toFloat())
    }

    fun floor(): Vector3f {
        return Vector3f(floorX.toFloat(), floorY.toFloat(), floorZ.toFloat())
    }

    fun round(): Vector3f {
        return Vector3f(Math.round(south), Math.round(up), Math.round(west))
    }

    fun abs(): Vector3f {
        return Vector3f((Math.abs(south) as Int).toFloat(), (Math.abs(up) as Int).toFloat(), (Math.abs(west) as Int).toFloat())
    }

    fun getSide(side: Int): Vector3f {
        return this.getSide(side, 1)
    }

    fun getSide(side: Int, step: Int): Vector3f {
        return when (side) {
            SIDE_DOWN -> Vector3f(south, up - step, west)
            SIDE_UP -> Vector3f(south, up + step, west)
            SIDE_NORTH -> Vector3f(south, up, west - step)
            SIDE_SOUTH -> Vector3f(south, up, west + step)
            SIDE_WEST -> Vector3f(south - step, up, west)
            SIDE_EAST -> Vector3f(south + step, up, west)
            else -> this
        }
    }

    fun distance(pos: Vector3f): Double {
        return Math.sqrt(distanceSquared(pos))
    }

    fun distanceSquared(pos: Vector3f): Double {
        return Math.pow(south - pos.south, 2) + Math.pow(up - pos.up, 2) + Math.pow(west - pos.west, 2)
    }

    @JvmOverloads
    fun maxPlainDistance(x: Float = 0f, z: Float = 0f): Float {
        return Math.max(Math.abs(south - x), Math.abs(west - z))
    }

    fun maxPlainDistance(vector: Vector2f): Float {
        return this.maxPlainDistance(vector.x, vector.y)
    }

    fun maxPlainDistance(x: Vector3f): Float {
        return this.maxPlainDistance(x.south, x.west)
    }

    fun length(): Double {
        return Math.sqrt(lengthSquared())
    }

    fun lengthSquared(): Float {
        return south * south + up * up + west * west
    }

    fun normalize(): Vector3f {
        val len = lengthSquared()
        return if (len > 0) {
            divide(Math.sqrt(len) as Float)
        } else Vector3f(0, 0, 0)
    }

    fun dot(v: Vector3f): Float {
        return south * v.south + up * v.up + west * v.west
    }

    fun cross(v: Vector3f): Vector3f {
        return Vector3f(
                up * v.west - west * v.up,
                west * v.south - south * v.west,
                south * v.up - up * v.south
        )
    }

    /*
     * Returns a new vector with x value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    fun getIntermediateWithXValue(v: Vector3f, x: Float): Vector3f? {
        val xDiff = v.south - south
        val yDiff = v.up - up
        val zDiff = v.west - west
        if (xDiff * xDiff < 0.0000001) {
            return null
        }
        val f = (x - south) / xDiff
        return if (f < 0 || f > 1) {
            null
        } else {
            Vector3f(south + xDiff * f, up + yDiff * f, west + zDiff * f)
        }
    }

    /*
     * Returns a new vector with y value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    fun getIntermediateWithYValue(v: Vector3f, y: Float): Vector3f? {
        val xDiff = v.south - south
        val yDiff = v.up - up
        val zDiff = v.west - west
        if (yDiff * yDiff < 0.0000001) {
            return null
        }
        val f = (y - up) / yDiff
        return if (f < 0 || f > 1) {
            null
        } else {
            Vector3f(south + xDiff * f, up + yDiff * f, west + zDiff * f)
        }
    }

    /*
     * Returns a new vector with z value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    fun getIntermediateWithZValue(v: Vector3f, z: Float): Vector3f? {
        val xDiff = v.south - south
        val yDiff = v.up - up
        val zDiff = v.west - west
        if (zDiff * zDiff < 0.0000001) {
            return null
        }
        val f = (z - west) / zDiff
        return if (f < 0 || f > 1) {
            null
        } else {
            Vector3f(south + xDiff * f, up + yDiff * f, west + zDiff * f)
        }
    }

    fun setComponents(x: Float, y: Float, z: Float): Vector3f {
        south = x
        up = y
        west = z
        return this
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getAxis(axis: BlockFace.Axis?): Float {
        return when (axis) {
            X -> south
            Y -> up
            else -> west
        }
    }

    @Override
    override fun toString(): String {
        return "Vector3(x=" + south + ",y=" + up + ",z=" + west + ")"
    }

    @Override
    override fun equals(obj: Object): Boolean {
        if (obj !is Vector3f) {
            return false
        }
        val other = obj as Vector3f
        return south == other.south && up == other.up && west == other.west
    }

    fun rawHashCode(): Int {
        return super.hashCode()
    }

    @Override
    fun clone(): Vector3f? {
        return try {
            super.clone() as Vector3f?
        } catch (e: CloneNotSupportedException) {
            null
        }
    }

    fun asVector3(): Vector3 {
        return Vector3(south.toDouble(), up.toDouble(), west.toDouble())
    }

    fun asBlockVector3(): BlockVector3 {
        return BlockVector3(floorX, floorY, floorZ)
    }

    companion object {
        const val SIDE_DOWN = 0
        const val SIDE_UP = 1
        const val SIDE_NORTH = 2
        const val SIDE_SOUTH = 3
        const val SIDE_WEST = 4
        const val SIDE_EAST = 5
        fun getOppositeSide(side: Int): Int {
            return when (side) {
                SIDE_DOWN -> SIDE_UP
                SIDE_UP -> SIDE_DOWN
                SIDE_NORTH -> SIDE_SOUTH
                SIDE_SOUTH -> SIDE_NORTH
                SIDE_WEST -> SIDE_EAST
                SIDE_EAST -> SIDE_WEST
                else -> -1
            }
        }
    }
}