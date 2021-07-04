package cn.nukkit.math

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author MagicDroidX (Nukkit Project)
 */
class Vector2f @JvmOverloads constructor(val x: Float = 0f, val y: Float = 0f) {
    val floorX: Int
        get() = NukkitMath.floorFloat(x)
    val floorY: Int
        get() = NukkitMath.floorFloat(y)

    @JvmOverloads
    fun add(x: Float, y: Float = 0f): Vector2f {
        return Vector2f(this.x + x, this.y + y)
    }

    fun add(x: Vector2f): Vector2f {
        return this.add(x.x, x.y)
    }

    @JvmOverloads
    fun subtract(x: Float, y: Float = 0f): Vector2f {
        return this.add(-x, -y)
    }

    fun subtract(x: Vector2f): Vector2f {
        return this.add(-x.x, -x.y)
    }

    fun ceil(): Vector2f {
        return Vector2f((x + 1).toInt(), (y + 1).toInt())
    }

    fun floor(): Vector2f {
        return Vector2f(floorX.toFloat(), floorY.toFloat())
    }

    fun round(): Vector2f {
        return Vector2f(Math.round(x), Math.round(y))
    }

    fun abs(): Vector2f {
        return Vector2f(Math.abs(x), Math.abs(y))
    }

    fun multiply(number: Float): Vector2f {
        return Vector2f(x * number, y * number)
    }

    fun divide(number: Float): Vector2f {
        return Vector2f(x / number, y / number)
    }

    @JvmOverloads
    fun distance(x: Float, y: Float = 0f): Double {
        return Math.sqrt(this.distanceSquared(x, y))
    }

    fun distance(vector: Vector2f): Double {
        return Math.sqrt(this.distanceSquared(vector.x, vector.y))
    }

    @JvmOverloads
    fun distanceSquared(x: Float, y: Float = 0f): Double {
        return Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2)
    }

    fun distanceSquared(vector: Vector2f): Double {
        return this.distanceSquared(vector.x, vector.y)
    }

    fun length(): Double {
        return Math.sqrt(lengthSquared())
    }

    fun lengthSquared(): Float {
        return x * x + y * y
    }

    fun normalize(): Vector2f {
        val len = lengthSquared()
        return if (len != 0f) {
            divide(Math.sqrt(len) as Float)
        } else Vector2f(0, 0)
    }

    fun dot(v: Vector2f): Float {
        return x * v.x + y * v.y
    }

    @Override
    override fun toString(): String {
        return "Vector2(x=" + x + ",y=" + y + ")"
    }
}