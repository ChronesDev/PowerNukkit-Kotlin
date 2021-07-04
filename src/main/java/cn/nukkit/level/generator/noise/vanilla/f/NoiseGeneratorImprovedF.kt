package cn.nukkit.level.generator.noise.vanilla.f

import cn.nukkit.math.NukkitRandom

class NoiseGeneratorImprovedF @JvmOverloads constructor(p_i45469_1_: NukkitRandom = NukkitRandom(System.currentTimeMillis())) {
    private val permutations: IntArray
    var xCoord: Float
    var yCoord: Float
    var zCoord: Float
    fun lerp(p_76311_1_: Float, p_76311_3_: Float, p_76311_5_: Float): Float {
        return p_76311_3_ + p_76311_1_ * (p_76311_5_ - p_76311_3_)
    }

    fun grad2(p_76309_1_: Int, p_76309_2_: Float, p_76309_4_: Float): Float {
        val i = p_76309_1_ and 15
        return GRAD_2X[i] * p_76309_2_ + GRAD_2Z[i] * p_76309_4_
    }

    fun grad(p_76310_1_: Int, p_76310_2_: Float, p_76310_4_: Float, p_76310_6_: Float): Float {
        val i = p_76310_1_ and 15
        return GRAD_X[i] * p_76310_2_ + GRAD_Y[i] * p_76310_4_ + GRAD_Z[i] * p_76310_6_
    }

    /*
     * noiseArray should be xSize*ySize*zSize in size
     */
    fun populateNoiseArray(noiseArray: FloatArray, xOffset: Float, yOffset: Float, zOffset: Float, xSize: Int, ySize: Int, zSize: Int, xScale: Float, yScale: Float, zScale: Float, noiseScale: Float) {
        if (ySize == 1) {
            var i5 = 0
            var j5 = 0
            var j = 0
            var k5 = 0
            var d14 = 0.0f
            var d15 = 0.0f
            var l5 = 0
            val d16 = 1.0f / noiseScale
            for (j2 in 0 until xSize) {
                var d17 = xOffset + j2 as Float * xScale + xCoord
                var i6 = d17.toInt()
                if (d17 < i6.toFloat()) {
                    --i6
                }
                val k2 = i6 and 255
                d17 = d17 - i6.toFloat()
                val d18 = d17 * d17 * d17 * (d17 * (d17 * 6.0f - 15.0f) + 10.0f)
                for (j6 in 0 until zSize) {
                    var d19 = zOffset + j6 as Float * zScale + zCoord
                    var k6 = d19.toInt()
                    if (d19 < k6.toFloat()) {
                        --k6
                    }
                    val l6 = k6 and 255
                    d19 = d19 - k6.toFloat()
                    val d20 = d19 * d19 * d19 * (d19 * (d19 * 6.0f - 15.0f) + 10.0f)
                    i5 = permutations[k2]
                    j5 = permutations[i5] + l6
                    j = permutations[k2 + 1]
                    k5 = permutations[j] + l6
                    d14 = lerp(d18, grad2(permutations[j5], d17, d19), grad(permutations[k5], d17 - 1.0f, 0.0f, d19))
                    d15 = lerp(d18, grad(permutations[j5 + 1], d17, 0.0f, d19 - 1.0f), grad(permutations[k5 + 1], d17 - 1.0f, 0.0f, d19 - 1.0f))
                    val d21 = lerp(d20, d14, d15)
                    val i7 = l5++
                    noiseArray[i7] += d21 * d16
                }
            }
        } else {
            var i = 0
            val d0 = 1.0f / noiseScale
            var k = -1
            var l = 0
            var i1 = 0
            var j1 = 0
            var k1 = 0
            var l1 = 0
            var i2 = 0
            var d1 = 0.0f
            var d2 = 0.0f
            var d3 = 0.0f
            var d4 = 0.0f
            for (l2 in 0 until xSize) {
                var d5 = xOffset + l2 as Float * xScale + xCoord
                var i3 = d5.toInt()
                if (d5 < i3.toFloat()) {
                    --i3
                }
                val j3 = i3 and 255
                d5 = d5 - i3.toFloat()
                val d6 = d5 * d5 * d5 * (d5 * (d5 * 6.0f - 15.0f) + 10.0f)
                for (k3 in 0 until zSize) {
                    var d7 = zOffset + k3 as Float * zScale + zCoord
                    var l3 = d7.toInt()
                    if (d7 < l3.toFloat()) {
                        --l3
                    }
                    val i4 = l3 and 255
                    d7 = d7 - l3.toFloat()
                    val d8 = d7 * d7 * d7 * (d7 * (d7 * 6.0f - 15.0f) + 10.0f)
                    for (j4 in 0 until ySize) {
                        var d9 = yOffset + j4 as Float * yScale + yCoord
                        var k4 = d9.toInt()
                        if (d9 < k4.toFloat()) {
                            --k4
                        }
                        val l4 = k4 and 255
                        d9 = d9 - k4.toFloat()
                        val d10 = d9 * d9 * d9 * (d9 * (d9 * 6.0f - 15.0f) + 10.0f)
                        if (j4 == 0 || l4 != k) {
                            k = l4
                            l = permutations[j3] + l4
                            i1 = permutations[l] + i4
                            j1 = permutations[l + 1] + i4
                            k1 = permutations[j3 + 1] + l4
                            l1 = permutations[k1] + i4
                            i2 = permutations[k1 + 1] + i4
                            d1 = lerp(d6, grad(permutations[i1], d5, d9, d7), grad(permutations[l1], d5 - 1.0f, d9, d7))
                            d2 = lerp(d6, grad(permutations[j1], d5, d9 - 1.0f, d7), grad(permutations[i2], d5 - 1.0f, d9 - 1.0f, d7))
                            d3 = lerp(d6, grad(permutations[i1 + 1], d5, d9, d7 - 1.0f), grad(permutations[l1 + 1], d5 - 1.0f, d9, d7 - 1.0f))
                            d4 = lerp(d6, grad(permutations[j1 + 1], d5, d9 - 1.0f, d7 - 1.0f), grad(permutations[i2 + 1], d5 - 1.0f, d9 - 1.0f, d7 - 1.0f))
                        }
                        val d11 = lerp(d10, d1, d2)
                        val d12 = lerp(d10, d3, d4)
                        val d13 = lerp(d8, d11, d12)
                        val j7 = i++
                        noiseArray[j7] += d13 * d0
                    }
                }
            }
        }
    }

    companion object {
        private val GRAD_X = floatArrayOf(1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f)
        private val GRAD_Y = floatArrayOf(1.0f, 1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f)
        private val GRAD_Z = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, -1.0f)
        private val GRAD_2X = floatArrayOf(1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f)
        private val GRAD_2Z = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, -1.0f)
    }

    init {
        permutations = IntArray(512)
        xCoord = p_i45469_1_.nextFloat() * 256.0f
        yCoord = p_i45469_1_.nextFloat() * 256.0f
        zCoord = p_i45469_1_.nextFloat() * 256.0f
        var i = 0
        while (i < 256) {
            permutations[i] = i++
        }
        for (l in 0..255) {
            val j: Int = p_i45469_1_.nextBoundedInt(256 - l) + l
            val k = permutations[l]
            permutations[l] = permutations[j]
            permutations[j] = k
            permutations[l + 256] = permutations[l]
        }
    }
}