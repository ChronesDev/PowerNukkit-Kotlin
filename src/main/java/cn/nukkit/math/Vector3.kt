package cn.nukkit.math

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
class Vector3 @JvmOverloads constructor(var south: Double = 0.0, var up: Double = 0.0, var west: Double = 0.0) : Cloneable {
    get()
    {
        return field
    }
    get()
    {
        return field
    }

    val floorX: Int
        get() = Math.floor(south)
    val floorY: Int
        get() = Math.floor(up)
    val floorZ: Int
        get() = Math.floor(west)
    val chunkX: Int
        get() = floorX shr 4
    val chunkZ: Int
        get() = floorZ shr 4

    @get:PowerNukkitOnly
    val chunkSectionY: Int
        @PowerNukkitOnly @Since("1.4.0.0-PN") get() = floorY shr 4
    val chunkVector: cn.nukkit.math.ChunkVector2
        @PowerNukkitOnly @Since("1.4.0.0-PN") get() = ChunkVector2(chunkX, chunkZ)

    @JvmOverloads
    fun add(x: Double, y: Double = 0.0, z: Double = 0.0): Vector3 {
        return Vector3(south + x, up + y, west + z)
    }

    fun add(x: Vector3): Vector3 {
        return Vector3(south + x.south, up + x.up, west + x.west)
    }

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Makes no sense", replaceWith = "clone()")
    fun subtract(): Vector3 {
        return this.subtract(0.0, 0.0, 0.0)
    }

    @JvmOverloads
    fun subtract(x: Double, y: Double = 0.0, z: Double = 0.0): Vector3 {
        return this.add(-x, -y, -z)
    }

    fun subtract(x: Vector3): Vector3 {
        return this.add(-x.south, -x.up, -x.west)
    }

    fun multiply(number: Double): Vector3 {
        return Vector3(south * number, up * number, west * number)
    }

    fun divide(number: Double): Vector3 {
        return Vector3(south / number, up / number, west / number)
    }

    fun ceil(): Vector3 {
        return Vector3((Math.ceil(south) as Int).toDouble(), (Math.ceil(up) as Int).toDouble(), (Math.ceil(west) as Int).toDouble())
    }

    fun floor(): Vector3 {
        return Vector3(floorX.toDouble(), floorY.toDouble(), floorZ.toDouble())
    }

    fun round(): Vector3 {
        return Vector3(Math.round(south), Math.round(up), Math.round(west))
    }

    fun abs(): Vector3 {
        return Vector3((Math.abs(south) as Int).toDouble(), (Math.abs(up) as Int).toDouble(), (Math.abs(west) as Int).toDouble())
    }

    fun getSide(face: BlockFace): Vector3 {
        return this.getSide(face, 1)
    }

    fun getSide(face: BlockFace, step: Int): Vector3 {
        return Vector3(south + face.getXOffset() * step, up + face.getYOffset() * step, west + face.getZOffset() * step)
    }

    @JvmOverloads
    fun up(step: Int = 1): Vector3 {
        return getSide(BlockFace.UP, step)
    }

    @JvmOverloads
    fun down(step: Int = 1): Vector3 {
        return getSide(BlockFace.DOWN, step)
    }

    @JvmOverloads
    fun north(step: Int = 1): Vector3 {
        return getSide(BlockFace.NORTH, step)
    }

    @JvmOverloads
    fun south(step: Int = 1): Vector3 {
        return getSide(BlockFace.SOUTH, step)
    }

    @JvmOverloads
    fun east(step: Int = 1): Vector3 {
        return getSide(BlockFace.EAST, step)
    }

    @JvmOverloads
    fun west(step: Int = 1): Vector3 {
        return getSide(BlockFace.WEST, step)
    }

    fun distance(pos: Vector3): Double {
        return distance(pos.south, pos.up, pos.west)
    }

    fun distanceSquared(pos: Vector3): Double {
        return distanceSquared(pos.south, pos.up, pos.west)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun distance(x: Double, y: Double, z: Double): Double {
        return Math.sqrt(distanceSquared(x, y, z))
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun distanceSquared(x: Double, y: Double, z: Double): Double {
        val ex = south - x
        val ey = up - y
        val ez = west - z
        return ex * ex + ey * ey + ez * ez
    }

    @JvmOverloads
    fun maxPlainDistance(x: Double = 0.0, z: Double = 0.0): Double {
        return Math.max(Math.abs(south - x), Math.abs(west - z))
    }

    fun maxPlainDistance(vector: Vector2): Double {
        return this.maxPlainDistance(vector.x, vector.y)
    }

    fun maxPlainDistance(x: Vector3): Double {
        return this.maxPlainDistance(x.south, x.west)
    }

    fun length(): Double {
        return Math.sqrt(lengthSquared())
    }

    fun lengthSquared(): Double {
        return south * south + up * up + west * west
    }

    fun normalize(): Vector3 {
        val len = lengthSquared()
        return if (len > 0) {
            divide(Math.sqrt(len))
        } else Vector3(0, 0, 0)
    }

    fun dot(v: Vector3): Double {
        return south * v.south + up * v.up + west * v.west
    }

    fun cross(v: Vector3): Vector3 {
        return Vector3(
                up * v.west - west * v.up,
                west * v.south - south * v.west,
                south * v.up - up * v.south
        )
    }

    /**
     * Returns a new vector with x value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     *
     * @param v vector
     * @param x x value
     * @return intermediate vector
     */
    @Nullable
    fun getIntermediateWithXValue(@Nonnull v: Vector3, x: Double): Vector3? {
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
            Vector3(south + xDiff * f, up + yDiff * f, west + zDiff * f)
        }
    }

    /**
     * Returns a new vector with y value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     *
     * @param v vector
     * @param y y value
     * @return intermediate vector
     */
    @Nullable
    fun getIntermediateWithYValue(@Nonnull v: Vector3, y: Double): Vector3? {
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
            Vector3(south + xDiff * f, up + yDiff * f, west + zDiff * f)
        }
    }

    /**
     * Returns a new vector with z value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     *
     * @param v vector
     * @param z z value
     * @return intermediate vector
     */
    @Nullable
    fun getIntermediateWithZValue(@Nonnull v: Vector3, z: Double): Vector3? {
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
            Vector3(south + xDiff * f, up + yDiff * f, west + zDiff * f)
        }
    }

    fun setComponents(x: Double, y: Double, z: Double): Vector3 {
        south = x
        up = y
        west = z
        return this
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun setComponentsAdding(x: Double, y: Double, z: Double, ax: Double, ay: Double, az: Double): Vector3 {
        south = x + ax
        up = y + ay
        west = z + az
        return this
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun setComponentsAdding(@Nonnull pos: Vector3, @Nonnull face: BlockFace): Vector3 {
        return setComponentsAdding(pos.south, pos.up, pos.west, face.getXOffset(), face.getYOffset(), face.getZOffset())
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun setComponents(@Nonnull pos: Vector3): Vector3 {
        south = pos.south
        up = pos.up
        west = pos.west
        return this
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getAxis(axis: BlockFace.Axis?): Double {
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
        if (obj !is Vector3) {
            return false
        }
        val other = obj as Vector3
        return south == other.south && up == other.up && west == other.west
    }

    @Override
    override fun hashCode(): Int {
        return south.toInt() xor (west.toInt() shl 12) xor (up.toInt() shl 24)
    }

    fun rawHashCode(): Int {
        return super.hashCode()
    }

    @Override
    fun clone(): Vector3? {
        return try {
            super.clone() as Vector3?
        } catch (e: CloneNotSupportedException) {
            null
        }
    }

    fun asVector3f(): Vector3f {
        return Vector3f(south.toFloat(), up.toFloat(), west.toFloat())
    }

    fun asBlockVector3(): BlockVector3 {
        return BlockVector3(floorX, floorY, floorZ)
    }
}