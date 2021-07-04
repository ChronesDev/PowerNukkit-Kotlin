package cn.nukkit.level

import cn.nukkit.math.Vector3

/**
 * @author MagicDroidX (Nukkit Project)
 */
class Location @JvmOverloads constructor(x: Double = 0.0, y: Double = 0.0, z: Double = 0.0, yaw: Double = 0.0, pitch: Double = 0.0, level: Level? = null) : Position() {
    var yaw: Double
    var pitch: Double

    constructor(x: Double, y: Double, z: Double, level: Level?) : this(x, y, z, 0.0, 0.0, level) {}

    fun setYaw(yaw: Double): Location {
        this.yaw = yaw
        return this
    }

    fun setPitch(pitch: Double): Location {
        this.pitch = pitch
        return this
    }

    @Override
    override fun toString(): String {
        return "Location (level=" + (if (this.isValid()) this.getLevel().getName() else "null") + ", x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", yaw=" + yaw + ", pitch=" + pitch + ")"
    }

    @get:Override
    @get:Nonnull
    override val location: Location
        get() = if (this.isValid()) Location(this.x, this.y, this.z, yaw, pitch, this.level) else throw LevelException("Undefined Level reference")

    @Override
    override fun add(x: Double): Location {
        return this.add(x, 0.0, 0.0)
    }

    @Override
    override fun add(x: Double, y: Double): Location {
        return this.add(x, y, 0.0)
    }

    @Override
    override fun add(x: Double, y: Double, z: Double): Location {
        return Location(x + x, y + y, z + z, yaw, pitch, this.level)
    }

    @Override
    override fun add(x: Vector3): Location {
        return Location(x + x.getX(), this.y + x.getY(), this.z + x.getZ(), yaw, pitch, this.level)
    }

    @Override
    override fun subtract(): Location {
        return this.subtract(0.0, 0.0, 0.0)
    }

    @Override
    override fun subtract(x: Double): Location {
        return this.subtract(x, 0.0, 0.0)
    }

    @Override
    override fun subtract(x: Double, y: Double): Location {
        return this.subtract(x, y, 0.0)
    }

    @Override
    override fun subtract(x: Double, y: Double, z: Double): Location {
        return this.add(-x, -y, -z)
    }

    @Override
    override fun subtract(x: Vector3): Location {
        return this.add(-x.getX(), -x.getY(), -x.getZ())
    }

    @Override
    override fun multiply(number: Double): Location {
        return Location(this.x * number, this.y * number, this.z * number, yaw, pitch, this.level)
    }

    @Override
    override fun divide(number: Double): Location {
        return Location(this.x / number, this.y / number, this.z / number, yaw, pitch, this.level)
    }

    @Override
    override fun ceil(): Location {
        return Location((Math.ceil(this.x) as Int).toDouble(), (Math.ceil(this.y) as Int).toDouble(), (Math.ceil(this.z) as Int).toDouble(), yaw, pitch, this.level)
    }

    @Override
    override fun floor(): Location {
        return Location(this.getFloorX(), this.getFloorY(), this.getFloorZ(), yaw, pitch, this.level)
    }

    @Override
    override fun round(): Location {
        return Location(Math.round(this.x), Math.round(this.y), Math.round(this.z), yaw, pitch, this.level)
    }

    @Override
    override fun abs(): Location {
        return Location((Math.abs(this.x) as Int).toDouble(), (Math.abs(this.y) as Int).toDouble(), (Math.abs(this.z) as Int).toDouble(), yaw, pitch, this.level)
    }

    val directionVector: cn.nukkit.math.Vector3
        get() {
            val pitch: Double = (pitch + 90) * Math.PI / 180
            val yaw: Double = (yaw + 90) * Math.PI / 180
            val x: Double = Math.sin(pitch) * Math.cos(yaw)
            val z: Double = Math.sin(pitch) * Math.sin(yaw)
            val y: Double = Math.cos(pitch)
            return Vector3(x, y, z).normalize()
        }

    @Override
    override fun clone(): Location {
        return super.clone() as Location
    }

    companion object {
        fun fromObject(pos: Vector3): Location {
            return fromObject(pos, null, 0.0, 0.0)
        }

        fun fromObject(pos: Vector3, level: Level?): Location {
            return fromObject(pos, level, 0.0, 0.0)
        }

        fun fromObject(pos: Vector3, level: Level?, yaw: Double): Location {
            return fromObject(pos, level, yaw, 0.0)
        }

        fun fromObject(pos: Vector3, level: Level?, yaw: Double, pitch: Double): Location {
            return Location(pos.x, pos.y, pos.z, yaw, pitch, if (level == null) if (pos is Position) (pos as Position).level else null else level)
        }
    }

    init {
        x = x
        y = y
        z = z
        this.yaw = yaw
        this.pitch = pitch
        level = level
    }
}