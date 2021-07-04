package cn.nukkit.level.generator.noise.vanilla.f

import cn.nukkit.math.NukkitRandom

class NoiseGeneratorSimplexF @JvmOverloads constructor(p_i45471_1_: NukkitRandom = NukkitRandom(System.currentTimeMillis())) {
    private val p: IntArray
    var xo: Float
    var yo: Float
    var zo: Float
    fun getValue(p_151605_1_: Float, p_151605_3_: Float): Float {
        val d3 = 0.5f * (SQRT_3 - 1.0f)
        val d4 = (p_151605_1_ + p_151605_3_) * d3
        val i = fastFloor(p_151605_1_ + d4)
        val j = fastFloor(p_151605_3_ + d4)
        val d5 = (3.0f - SQRT_3) / 6.0f
        val d6 = (i + j).toFloat() * d5
        val d7 = i.toFloat() - d6
        val d8 = j.toFloat() - d6
        val d9 = p_151605_1_ - d7
        val d10 = p_151605_3_ - d8
        val k: Int
        val l: Int
        if (d9 > d10) {
            k = 1
            l = 0
        } else {
            k = 0
            l = 1
        }
        val d11 = d9 - k.toFloat() + d5
        val d12 = d10 - l.toFloat() + d5
        val d13 = d9 - 1.0f + 2.0f * d5
        val d14 = d10 - 1.0f + 2.0f * d5
        val i1 = i and 255
        val j1 = j and 255
        val k1 = p[i1 + p[j1]] % 12
        val l1 = p[i1 + k + p[j1 + l]] % 12
        val i2 = p[i1 + 1 + p[j1 + 1]] % 12
        var d15 = 0.5f - d9 * d9 - d10 * d10
        val d0: Float
        if (d15 < 0.0f) {
            d0 = 0.0f
        } else {
            d15 = d15 * d15
            d0 = d15 * d15 * dot(grad3[k1], d9, d10)
        }
        var d16 = 0.5f - d11 * d11 - d12 * d12
        val d1: Float
        if (d16 < 0.0f) {
            d1 = 0.0f
        } else {
            d16 = d16 * d16
            d1 = d16 * d16 * dot(grad3[l1], d11, d12)
        }
        var d17 = 0.5f - d13 * d13 - d14 * d14
        val d2: Float
        if (d17 < 0.0f) {
            d2 = 0.0f
        } else {
            d17 = d17 * d17
            d2 = d17 * d17 * dot(grad3[i2], d13, d14)
        }
        return 70.0f * (d0 + d1 + d2)
    }

    fun add(p_151606_1_: FloatArray, p_151606_2_: Float, p_151606_4_: Float, p_151606_6_: Int, p_151606_7_: Int, p_151606_8_: Float, p_151606_10_: Float, p_151606_12_: Float) {
        var i = 0
        for (j in 0 until p_151606_7_) {
            val d0 = (p_151606_4_ + j as Float) * p_151606_10_ + yo
            for (k in 0 until p_151606_6_) {
                val d1 = (p_151606_2_ + k as Float) * p_151606_8_ + xo
                val d5 = (d1 + d0) * F2
                val l = fastFloor(d1 + d5)
                val i1 = fastFloor(d0 + d5)
                val d6 = (l + i1).toFloat() * G2
                val d7 = l.toFloat() - d6
                val d8 = i1.toFloat() - d6
                val d9 = d1 - d7
                val d10 = d0 - d8
                var j1: Int
                var k1: Int
                if (d9 > d10) {
                    j1 = 1
                    k1 = 0
                } else {
                    j1 = 0
                    k1 = 1
                }
                val d11 = d9 - j1.toFloat() + G2
                val d12 = d10 - k1.toFloat() + G2
                val d13 = d9 - 1.0f + 2.0f * G2
                val d14 = d10 - 1.0f + 2.0f * G2
                val l1 = l and 255
                val i2 = i1 and 255
                val j2 = p[l1 + p[i2]] % 12
                val k2 = p[l1 + j1 + p[i2 + k1]] % 12
                val l2 = p[l1 + 1 + p[i2 + 1]] % 12
                var d15 = 0.5f - d9 * d9 - d10 * d10
                var d2: Float
                if (d15 < 0.0f) {
                    d2 = 0.0f
                } else {
                    d15 = d15 * d15
                    d2 = d15 * d15 * dot(grad3[j2], d9, d10)
                }
                var d16 = 0.5f - d11 * d11 - d12 * d12
                var d3: Float
                if (d16 < 0.0f) {
                    d3 = 0.0f
                } else {
                    d16 = d16 * d16
                    d3 = d16 * d16 * dot(grad3[k2], d11, d12)
                }
                var d17 = 0.5f - d13 * d13 - d14 * d14
                var d4: Float
                if (d17 < 0.0f) {
                    d4 = 0.0f
                } else {
                    d17 = d17 * d17
                    d4 = d17 * d17 * dot(grad3[l2], d13, d14)
                }
                val i3 = i++
                p_151606_1_[i3] += 70.0f * (d2 + d3 + d4) * p_151606_12_
            }
        }
    }

    companion object {
        val SQRT_3 = Math.sqrt(3.0f) as Float
        private val grad3 = arrayOf(intArrayOf(1, 1, 0), intArrayOf(-1, 1, 0), intArrayOf(1, -1, 0), intArrayOf(-1, -1, 0), intArrayOf(1, 0, 1), intArrayOf(-1, 0, 1), intArrayOf(1, 0, -1), intArrayOf(-1, 0, -1), intArrayOf(0, 1, 1), intArrayOf(0, -1, 1), intArrayOf(0, 1, -1), intArrayOf(0, -1, -1))
        private val F2 = 0.5f * (SQRT_3 - 1.0f)
        private val G2 = (3.0f - SQRT_3) / 6.0f
        private fun fastFloor(value: Float): Int {
            return if (value > 0.0f) value.toInt() else value.toInt() - 1
        }

        private fun dot(p_151604_0_: IntArray, p_151604_1_: Float, p_151604_3_: Float): Float {
            return p_151604_0_[0].toFloat() * p_151604_1_ + p_151604_0_[1].toFloat() * p_151604_3_
        }
    }

    init {
        p = IntArray(512)
        xo = p_i45471_1_.nextFloat() * 256.0f
        yo = p_i45471_1_.nextFloat() * 256.0f
        zo = p_i45471_1_.nextFloat() * 256.0f
        var i = 0
        while (i < 256) {
            p[i] = i++
        }
        for (l in 0..255) {
            val j: Int = p_i45471_1_.nextBoundedInt(256 - l) + l
            val k = p[l]
            p[l] = p[j]
            p[j] = k
            p[l + 256] = p[l]
        }
    }
}