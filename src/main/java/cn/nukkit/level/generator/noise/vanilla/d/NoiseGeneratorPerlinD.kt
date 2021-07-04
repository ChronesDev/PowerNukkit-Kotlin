package cn.nukkit.level.generator.noise.vanilla.d

import cn.nukkit.math.NukkitRandom

class NoiseGeneratorPerlinD(p_i45470_1_: NukkitRandom, private val levels: Int) {
    private val noiseLevels: Array<NoiseGeneratorSimplexD?>
    fun getValue(p_151601_1_: Double, p_151601_3_: Double): Double {
        var d0 = 0.0
        var d1 = 1.0
        for (i in 0 until levels) {
            d0 += noiseLevels[i]!!.getValue(p_151601_1_ * d1, p_151601_3_ * d1) / d1
            d1 /= 2.0
        }
        return d0
    }

    fun getRegion(p_151599_1_: DoubleArray?, p_151599_2_: Double, p_151599_4_: Double, p_151599_6_: Int, p_151599_7_: Int, p_151599_8_: Double, p_151599_10_: Double, p_151599_12_: Double): DoubleArray {
        return this.getRegion(p_151599_1_, p_151599_2_, p_151599_4_, p_151599_6_, p_151599_7_, p_151599_8_, p_151599_10_, p_151599_12_, 0.5)
    }

    fun getRegion(p_151600_1_: DoubleArray?, p_151600_2_: Double, p_151600_4_: Double, p_151600_6_: Int, p_151600_7_: Int, p_151600_8_: Double, p_151600_10_: Double, p_151600_12_: Double, p_151600_14_: Double): DoubleArray {
        var p_151600_1_ = p_151600_1_
        if (p_151600_1_ != null && p_151600_1_.size >= p_151600_6_ * p_151600_7_) {
            for (i in p_151600_1_.indices) {
                p_151600_1_[i] = 0.0
            }
        } else {
            p_151600_1_ = DoubleArray(p_151600_6_ * p_151600_7_)
        }
        var d1 = 1.0
        var d0 = 1.0
        for (j in 0 until levels) {
            noiseLevels[j]!!.add(p_151600_1_, p_151600_2_, p_151600_4_, p_151600_6_, p_151600_7_, p_151600_8_ * d0 * d1, p_151600_10_ * d0 * d1, 0.55 / d1)
            d0 *= p_151600_12_
            d1 *= p_151600_14_
        }
        return p_151600_1_
    }

    init {
        noiseLevels = arrayOfNulls<NoiseGeneratorSimplexD>(levels)
        for (i in 0 until levels) {
            noiseLevels[i] = NoiseGeneratorSimplexD(p_i45470_1_)
        }
    }
}