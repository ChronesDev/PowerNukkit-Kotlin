package cn.nukkit.math

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author MagicDroidX (Nukkit Project)
 */
class Vector2 @JvmOverloads constructor(val x: Double = 0.0, val y: Double = 0.0) {
    val floorX: Int
        get() = Math.floor(x)
    val floorY: Int
        get() = Math.floor(y)

    @JvmOverloads
    fun add(x: Double, y: Double = 0.0): Vector2 {
        return Vector2(this.x + x, this.y + y)
    }

    fun add(x: Vector2): Vector2 {
        return this.add(x.x, x.y)
    }

    @JvmOverloads
    fun subtract(x: Double, y: Double = 0.0): Vector2 {
        return this.add(-x, -y)
    }

    fun subtract(x: Vector2): Vector2 {
        return this.add(-x.x, -x.y)
    }

    fun ceil(): Vector2 {
        return Vector2((x + 1).toInt(), (y + 1).toInt())
    }

    fun floor(): Vector2 {
        return Vector2((Math.floor(x) as Int).toDouble(), (Math.floor(y) as Int).toDouble())
    }

    fun round(): Vector2 {
        return Vector2(Math.round(x), Math.round(y))
    }

    fun abs(): Vector2 {
        return Vector2(Math.abs(x), Math.abs(y))
    }

    fun multiply(number: Double): Vector2 {
        return Vector2(x * number, y * number)
    }

    fun divide(number: Double): Vector2 {
        return Vector2(x / number, y / number)
    }

    @JvmOverloads
    fun distance(x: Double, y: Double = 0.0): Double {
        return Math.sqrt(this.distanceSquared(x, y))
    }

    fun distance(vector: Vector2): Double {
        return distance(vector.x, vector.y)
    }

    @JvmOverloads
    fun distanceSquared(x: Double, y: Double = 0.0): Double {
        val ex = this.x - x
        val ey = this.y - y
        return ey * ey + ex * ex
    }

    fun distanceSquared(vector: Vector2): Double {
        return this.distanceSquared(vector.x, vector.y)
    }

    fun length(): Double {
        return Math.sqrt(lengthSquared())
    }

    fun lengthSquared(): Double {
        return x * x + y * y
    }

    fun normalize(): Vector2 {
        val len = lengthSquared()
        return if (len != 0.0) {
            divide(Math.sqrt(len))
        } else Vector2(0, 0)
    }

    fun dot(v: Vector2): Double {
        return x * v.x + y * v.y
    }

    @Override
    override fun toString(): String {
        return "Vector2(x=" + x + ",y=" + y + ")"
    }
}