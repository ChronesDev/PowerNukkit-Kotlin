package cn.nukkit.math

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author MagicDroidX (Nukkit Project)
 */
object VectorMath {
    fun getDirection2D(azimuth: Double): Vector2 {
        return Vector2(Math.cos(azimuth), Math.sin(azimuth))
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun calculateAxis(base: Vector3, side: Vector3): BlockFace.Axis {
        val vector: Vector3 = side.subtract(base)
        return if (vector.x !== 0) BlockFace.Axis.X else if (vector.z !== 0) BlockFace.Axis.Z else BlockFace.Axis.Y
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun calculateFace(base: Vector3, side: Vector3): BlockFace? {
        val vector: Vector3 = side.subtract(base)
        val axis: BlockFace.Axis = if (vector.x !== 0) BlockFace.Axis.X else if (vector.z !== 0) BlockFace.Axis.Z else BlockFace.Axis.Y
        val direction: Double = vector.getAxis(axis)
        return BlockFace.fromAxis(if (direction < 0) BlockFace.AxisDirection.NEGATIVE else BlockFace.AxisDirection.POSITIVE, axis)
    }
}