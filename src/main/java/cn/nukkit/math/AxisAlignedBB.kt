package cn.nukkit.math

import cn.nukkit.api.PowerNukkitOnly

interface AxisAlignedBB : Cloneable {
    fun setBounds(minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double): AxisAlignedBB? {
        this.minX = minX
        this.minY = minY
        this.minZ = minZ
        this.maxX = maxX
        this.maxY = maxY
        this.maxZ = maxZ
        return this
    }

    fun addCoord(x: Double, y: Double, z: Double): AxisAlignedBB? {
        var minX = minX
        var minY = minY
        var minZ = minZ
        var maxX = maxX
        var maxY = maxY
        var maxZ = maxZ
        if (x < 0) minX += x
        if (x > 0) maxX += x
        if (y < 0) minY += y
        if (y > 0) maxY += y
        if (z < 0) minZ += z
        if (z > 0) maxZ += z
        return SimpleAxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ)
    }

    fun grow(x: Double, y: Double, z: Double): AxisAlignedBB? {
        return SimpleAxisAlignedBB(minX - x, minY - y, minZ - z, maxX + x, maxY + y, maxZ + z)
    }

    fun expand(x: Double, y: Double, z: Double): AxisAlignedBB? {
        minX = minX - x
        minY = minY - y
        minZ = minZ - z
        maxX = maxX + x
        maxY = maxY + y
        maxZ = maxZ + z
        return this
    }

    fun offset(x: Double, y: Double, z: Double): AxisAlignedBB? {
        minX = minX + x
        minY = minY + y
        minZ = minZ + z
        maxX = maxX + x
        maxY = maxY + y
        maxZ = maxZ + z
        return this
    }

    fun shrink(x: Double, y: Double, z: Double): AxisAlignedBB? {
        return SimpleAxisAlignedBB(minX + x, minY + y, minZ + z, maxX - x, maxY - y, maxZ - z)
    }

    fun contract(x: Double, y: Double, z: Double): AxisAlignedBB? {
        minX = minX + x
        minY = minY + y
        minZ = minZ + z
        maxX = maxX - x
        maxY = maxY - y
        maxZ = maxZ - z
        return this
    }

    fun setBB(bb: AxisAlignedBB): AxisAlignedBB? {
        minX = bb.minX
        minY = bb.minY
        minZ = bb.minZ
        maxX = bb.maxX
        maxY = bb.maxY
        maxZ = bb.maxZ
        return this
    }

    fun getOffsetBoundingBox(face: BlockFace, x: Double, y: Double, z: Double): AxisAlignedBB? {
        return getOffsetBoundingBox(face.getXOffset() * x, face.getYOffset() * y, face.getZOffset() * z)
    }

    fun getOffsetBoundingBox(x: Double, y: Double, z: Double): AxisAlignedBB? {
        return SimpleAxisAlignedBB(minX + x, minY + y, minZ + z, maxX + x, maxY + y, maxZ + z)
    }

    fun calculateXOffset(bb: AxisAlignedBB, x: Double): Double {
        var x = x
        if (bb.maxY <= minY || bb.minY >= maxY) {
            return x
        }
        if (bb.maxZ <= minZ || bb.minZ >= maxZ) {
            return x
        }
        if (x > 0 && bb.maxX <= minX) {
            val x1 = minX - bb.maxX
            if (x1 < x) {
                x = x1
            }
        }
        if (x < 0 && bb.minX >= maxX) {
            val x2 = maxX - bb.minX
            if (x2 > x) {
                x = x2
            }
        }
        return x
    }

    fun calculateYOffset(bb: AxisAlignedBB, y: Double): Double {
        var y = y
        if (bb.maxX <= minX || bb.minX >= maxX) {
            return y
        }
        if (bb.maxZ <= minZ || bb.minZ >= maxZ) {
            return y
        }
        if (y > 0 && bb.maxY <= minY) {
            val y1 = minY - bb.maxY
            if (y1 < y) {
                y = y1
            }
        }
        if (y < 0 && bb.minY >= maxY) {
            val y2 = maxY - bb.minY
            if (y2 > y) {
                y = y2
            }
        }
        return y
    }

    fun calculateZOffset(bb: AxisAlignedBB, z: Double): Double {
        var z = z
        if (bb.maxX <= minX || bb.minX >= maxX) {
            return z
        }
        if (bb.maxY <= minY || bb.minY >= maxY) {
            return z
        }
        if (z > 0 && bb.maxZ <= minZ) {
            val z1 = minZ - bb.maxZ
            if (z1 < z) {
                z = z1
            }
        }
        if (z < 0 && bb.minZ >= maxZ) {
            val z2 = maxZ - bb.minZ
            if (z2 > z) {
                z = z2
            }
        }
        return z
    }

    fun intersectsWith(bb: AxisAlignedBB): Boolean {
        if (bb.maxY > minY && bb.minY < maxY) {
            if (bb.maxX > minX && bb.minX < maxX) {
                return bb.maxZ > minZ && bb.minZ < maxZ
            }
        }
        return false
    }

    fun isVectorInside(vector: Vector3): Boolean {
        return vector.x >= minX && vector.x <= maxX && vector.y >= minY && vector.y <= maxY && vector.z >= minZ && vector.z <= maxZ
    }

    val averageEdgeLength: Double
        get() = (maxX - minX + maxY - minY + maxZ - minZ) / 3

    fun isVectorInYZ(vector: Vector3): Boolean {
        return vector.y >= minY && vector.y <= maxY && vector.z >= minZ && vector.z <= maxZ
    }

    fun isVectorInXZ(vector: Vector3): Boolean {
        return vector.x >= minX && vector.x <= maxX && vector.z >= minZ && vector.z <= maxZ
    }

    fun isVectorInXY(vector: Vector3): Boolean {
        return vector.x >= minX && vector.x <= maxX && vector.y >= minY && vector.y <= maxY
    }

    fun calculateIntercept(pos1: Vector3, pos2: Vector3): MovingObjectPosition? {
        var v1: Vector3? = pos1.getIntermediateWithXValue(pos2, minX)
        var v2: Vector3? = pos1.getIntermediateWithXValue(pos2, maxX)
        var v3: Vector3? = pos1.getIntermediateWithYValue(pos2, minY)
        var v4: Vector3? = pos1.getIntermediateWithYValue(pos2, maxY)
        var v5: Vector3? = pos1.getIntermediateWithZValue(pos2, minZ)
        var v6: Vector3? = pos1.getIntermediateWithZValue(pos2, maxZ)
        if (v1 != null && !isVectorInYZ(v1)) {
            v1 = null
        }
        if (v2 != null && !isVectorInYZ(v2)) {
            v2 = null
        }
        if (v3 != null && !isVectorInXZ(v3)) {
            v3 = null
        }
        if (v4 != null && !isVectorInXZ(v4)) {
            v4 = null
        }
        if (v5 != null && !isVectorInXY(v5)) {
            v5 = null
        }
        if (v6 != null && !isVectorInXY(v6)) {
            v6 = null
        }
        var vector: Vector3? = null

        //if (v1 != null && (vector == null || pos1.distanceSquared(v1) < pos1.distanceSquared(vector))) {
        if (v1 != null) {
            vector = v1
        }
        if (v2 != null && (vector == null || pos1.distanceSquared(v2) < pos1.distanceSquared(vector))) {
            vector = v2
        }
        if (v3 != null && (vector == null || pos1.distanceSquared(v3) < pos1.distanceSquared(vector))) {
            vector = v3
        }
        if (v4 != null && (vector == null || pos1.distanceSquared(v4) < pos1.distanceSquared(vector))) {
            vector = v4
        }
        if (v5 != null && (vector == null || pos1.distanceSquared(v5) < pos1.distanceSquared(vector))) {
            vector = v5
        }
        if (v6 != null && (vector == null || pos1.distanceSquared(v6) < pos1.distanceSquared(vector))) {
            vector = v6
        }
        if (vector == null) {
            return null
        }
        var f: BlockFace? = null
        if (vector === v1) {
            f = BlockFace.WEST
        } else if (vector === v2) {
            f = BlockFace.EAST
        } else if (vector === v3) {
            f = BlockFace.DOWN
        } else if (vector === v4) {
            f = BlockFace.UP
        } else if (vector === v5) {
            f = BlockFace.NORTH
        } else if (vector === v6) {
            f = BlockFace.SOUTH
        }
        return MovingObjectPosition.fromBlock(0, 0, 0, f, vector)
    }

    var minX: Double
        set(minX) {
            throw UnsupportedOperationException("Not mutable")
        }
    var minY: Double
        set(minY) {
            throw UnsupportedOperationException("Not mutable")
        }
    var minZ: Double
        set(minZ) {
            throw UnsupportedOperationException("Not mutable")
        }
    var maxX: Double
        set(maxX) {
            throw UnsupportedOperationException("Not mutable")
        }
    var maxY: Double
        set(maxY) {
            throw UnsupportedOperationException("Not mutable")
        }
    var maxZ: Double
        set(maxZ) {
            throw UnsupportedOperationException("Not mutable")
        }

    fun clone(): AxisAlignedBB?
    fun forEach(action: BBConsumer<*>) {
        val minX: Int = NukkitMath.floorDouble(minX)
        val minY: Int = NukkitMath.floorDouble(minY)
        val minZ: Int = NukkitMath.floorDouble(minZ)
        val maxX: Int = NukkitMath.floorDouble(maxX)
        val maxY: Int = NukkitMath.floorDouble(maxY)
        val maxZ: Int = NukkitMath.floorDouble(maxZ)
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    action.accept(x, y, z)
                }
            }
        }
    }

    interface BBConsumer<T> {
        fun accept(x: Int, y: Int, z: Int)
        fun get(): T? {
            return null
        }
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EMPTY_ARRAY = arrayOfNulls<AxisAlignedBB>(0)
    }
}