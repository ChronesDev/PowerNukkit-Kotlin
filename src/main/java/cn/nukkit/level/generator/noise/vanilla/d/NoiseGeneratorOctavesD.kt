package cn.nukkit.level.generator.noise.vanilla.d

import cn.nukkit.math.MathHelper

class NoiseGeneratorOctavesD(seed: NukkitRandom, private val octaves: Int) {
    /**
     * Collection of noise generation functions.  Output is combined to produce different octaves of noise.
     */
    private val generatorCollection: Array<NoiseGeneratorImprovedD?>

    /*
     * pars:(par2,3,4=noiseOffset ; so that adjacent noise segments connect) (pars5,6,7=x,y,zArraySize),(pars8,10,12 =
     * x,y,z noiseScale)
     */
    fun generateNoiseOctaves(noiseArray: DoubleArray?, xOffset: Int, yOffset: Int, zOffset: Int, xSize: Int, ySize: Int, zSize: Int, xScale: Double, yScale: Double, zScale: Double): DoubleArray {
        var noiseArray = noiseArray
        if (noiseArray == null) {
            noiseArray = DoubleArray(xSize * ySize * zSize)
        } else {
            for (i in noiseArray.indices) {
                noiseArray[i] = 0.0
            }
        }
        var d3 = 1.0
        for (j in 0 until octaves) {
            var d0 = xOffset.toDouble() * d3 * xScale
            val d1 = yOffset.toDouble() * d3 * yScale
            var d2 = zOffset.toDouble() * d3 * zScale
            var k: Long = MathHelper.floor_double_long(d0)
            var l: Long = MathHelper.floor_double_long(d2)
            d0 = d0 - k.toDouble()
            d2 = d2 - l.toDouble()
            k = k % 16777216L
            l = l % 16777216L
            d0 = d0 + k.toDouble()
            d2 = d2 + l.toDouble()
            generatorCollection[j]!!.populateNoiseArray(noiseArray, d0, d1, d2, xSize, ySize, zSize, xScale * d3, yScale * d3, zScale * d3, d3)
            d3 /= 2.0
        }
        return noiseArray
    }

    /*
     * Bouncer function to the main one with some default arguments.
     */
    fun generateNoiseOctaves(noiseArray: DoubleArray?, xOffset: Int, zOffset: Int, xSize: Int, zSize: Int, xScale: Double, zScale: Double, p_76305_10_: Double): DoubleArray {
        return this.generateNoiseOctaves(noiseArray, xOffset, 10, zOffset, xSize, 1, zSize, xScale, 1.0, zScale)
    }

    init {
        generatorCollection = arrayOfNulls<NoiseGeneratorImprovedD>(octaves)
        for (i in 0 until octaves) {
            generatorCollection[i] = NoiseGeneratorImprovedD(seed)
        }
    }
}