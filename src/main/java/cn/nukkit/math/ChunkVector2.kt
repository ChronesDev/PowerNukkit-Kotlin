/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cn.nukkit.math

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 * @since 2020-09-20
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class ChunkVector2 @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(var x: Int, var z: Int) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0, 0) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(x: Int) : this(x, 0) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun add(x: Int): ChunkVector2 {
        return this.add(x, 0)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun add(x: Int, y: Int): ChunkVector2 {
        return ChunkVector2(this.x + x, z + y)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun add(x: ChunkVector2): ChunkVector2 {
        return this.add(x.x, x.z)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun subtract(x: Int): ChunkVector2 {
        return this.subtract(x, 0)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun subtract(x: Int, y: Int): ChunkVector2 {
        return this.add(-x, -y)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun subtract(x: ChunkVector2): ChunkVector2 {
        return this.add(-x.x, -x.z)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun abs(): ChunkVector2 {
        return ChunkVector2(Math.abs(x), Math.abs(z))
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun multiply(number: Int): ChunkVector2 {
        return ChunkVector2(x * number, z * number)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun divide(number: Int): ChunkVector2 {
        return ChunkVector2(x / number, z / number)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun distance(x: Double): Double {
        return this.distance(x, 0.0)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun distance(x: Double, y: Double): Double {
        return Math.sqrt(this.distanceSquared(x, y))
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun distance(vector: ChunkVector2): Double {
        return Math.sqrt(this.distanceSquared(vector.x.toDouble(), vector.z.toDouble()))
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun distanceSquared(x: Double): Double {
        return this.distanceSquared(x, 0.0)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun distanceSquared(x: Double, y: Double): Double {
        return Math.pow(this.x - x, 2) + Math.pow(z - y, 2)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun distanceSquared(vector: ChunkVector2): Double {
        return this.distanceSquared(vector.x.toDouble(), vector.z.toDouble())
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun length(): Double {
        return Math.sqrt(lengthSquared())
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun lengthSquared(): Int {
        return x * x + z * z
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun dot(v: ChunkVector2): Int {
        return x * v.x + z * v.z
    }

    @Override
    override fun toString(): String {
        return "MutableChunkVector(x=" + x + ",z=" + z + ")"
    }
}