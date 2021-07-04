package cn.nukkit.math

import cn.nukkit.api.PowerNukkitOnly

class BlockVector3 : Cloneable {
    var x = 0
    var y = 0
    var z = 0

    constructor(x: Int, y: Int, z: Int) {
        this.x = x
        this.y = y
        this.z = z
    }

    constructor() {}

    fun setComponents(x: Int, y: Int, z: Int): BlockVector3 {
        this.x = x
        this.y = y
        this.z = z
        return this
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setComponentsAdding(pos: Vector3, face: BlockFace): BlockVector3 {
        x = pos.getFloorX() + face.getXOffset()
        y = pos.getFloorY() + face.getYOffset()
        z = pos.getFloorZ() + face.getZOffset()
        return this
    }

    fun add(x: Double): Vector3 {
        return this.add(x, 0.0, 0.0)
    }

    fun add(x: Double, y: Double): Vector3 {
        return this.add(x, y, 0.0)
    }

    fun add(x: Double, y: Double, z: Double): Vector3 {
        return Vector3(this.x + x, this.y + y, this.z + z)
    }

    fun add(x: Vector3): Vector3 {
        return Vector3(this.x + x.getX(), y + x.getY(), z + x.getZ())
    }

    fun subtract(x: Double): Vector3 {
        return this.subtract(x, 0.0, 0.0)
    }

    fun subtract(x: Double, y: Double): Vector3 {
        return this.subtract(x, y, 0.0)
    }

    fun subtract(x: Double, y: Double, z: Double): Vector3 {
        return this.add(-x, -y, -z)
    }

    fun subtract(x: Vector3): Vector3 {
        return this.add(-x.getX(), -x.getY(), -x.getZ())
    }

    @JvmOverloads
    fun add(x: Int, y: Int = 0, z: Int = 0): BlockVector3 {
        return BlockVector3(this.x + x, this.y + y, this.z + z)
    }

    fun add(x: BlockVector3): BlockVector3 {
        return BlockVector3(this.x + x.x, y + x.y, z + x.z)
    }

    @JvmOverloads
    fun subtract(x: Int = 0, y: Int = 0, z: Int = 0): BlockVector3 {
        return this.add(-x, -y, -z)
    }

    fun subtract(x: BlockVector3): BlockVector3 {
        return this.add(-x.x, -x.y, -x.z)
    }

    fun multiply(number: Int): BlockVector3 {
        return BlockVector3(x * number, y * number, z * number)
    }

    fun divide(number: Int): BlockVector3 {
        return BlockVector3(x / number, y / number, z / number)
    }

    fun getSide(face: BlockFace): BlockVector3 {
        return this.getSide(face, 1)
    }

    fun getSide(face: BlockFace, step: Int): BlockVector3 {
        return BlockVector3(x + face.getXOffset() * step, y + face.getYOffset() * step, z + face.getZOffset() * step)
    }

    @JvmOverloads
    fun up(step: Int = 1): BlockVector3 {
        return getSide(BlockFace.UP, step)
    }

    @JvmOverloads
    fun down(step: Int = 1): BlockVector3 {
        return getSide(BlockFace.DOWN, step)
    }

    @JvmOverloads
    fun north(step: Int = 1): BlockVector3 {
        return getSide(BlockFace.NORTH, step)
    }

    @JvmOverloads
    fun south(step: Int = 1): BlockVector3 {
        return getSide(BlockFace.SOUTH, step)
    }

    @JvmOverloads
    fun east(step: Int = 1): BlockVector3 {
        return getSide(BlockFace.EAST, step)
    }

    @JvmOverloads
    fun west(step: Int = 1): BlockVector3 {
        return getSide(BlockFace.WEST, step)
    }

    fun distance(pos: Vector3): Double {
        return Math.sqrt(this.distanceSquared(pos))
    }

    fun distance(pos: BlockVector3): Double {
        return Math.sqrt(this.distanceSquared(pos))
    }

    fun distanceSquared(pos: Vector3): Double {
        return distanceSquared(pos.x, pos.y, pos.z)
    }

    fun distanceSquared(pos: BlockVector3): Double {
        return distanceSquared(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
    }

    fun distanceSquared(x: Double, y: Double, z: Double): Double {
        return Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2) + Math.pow(this.z - z, 2)
    }

    val chunkX: Int
        @PowerNukkitOnly @Since("1.4.0.0-PN") get() = x shr 4
    val chunkZ: Int
        @PowerNukkitOnly @Since("1.4.0.0-PN") get() = z shr 4
    val chunkSectionY: Int
        @PowerNukkitOnly @Since("1.4.0.0-PN") get() = y shr 4
    val chunkVector: cn.nukkit.math.ChunkVector2
        @PowerNukkitOnly @Since("1.4.0.0-PN") get() = ChunkVector2(chunkX, chunkZ)

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getAxis(axis: BlockFace.Axis?): Int {
        return when (axis) {
            X -> x
            Y -> y
            else -> z
        }
    }

    @Override
    override fun equals(o: Object?): Boolean {
        if (o == null) return false
        if (o === this) return true
        if (o !is BlockVector3) return false
        val that = o as BlockVector3
        return x == that.x && y == that.y && z == that.z
    }

    @Override
    override fun hashCode(): Int {
        return x xor (z shl 12) xor (y shl 24)
    }

    @Override
    override fun toString(): String {
        return "BlockPosition(level=" + ",x=" + x + ",y=" + y + ",z=" + z + ")"
    }

    @Override
    fun clone(): BlockVector3? {
        return try {
            super.clone() as BlockVector3?
        } catch (e: CloneNotSupportedException) {
            null
        }
    }

    fun asVector3(): Vector3 {
        return Vector3(x.toDouble(), y.toDouble(), z.toDouble())
    }

    fun asVector3f(): Vector3f {
        return Vector3f(x.toFloat(), y.toFloat(), z.toFloat())
    }
}