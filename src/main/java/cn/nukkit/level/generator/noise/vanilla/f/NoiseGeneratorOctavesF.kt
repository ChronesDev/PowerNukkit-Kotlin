package cn.nukkit.level.generator.noise.vanilla.f

import cn.nukkit.math.MathHelper

class NoiseGeneratorOctavesF(seed: NukkitRandom, private val octaves: Int) {
    /**
     * Collection of noise generation functions.  Output is combined to produce different octaves of noise.
     */
    private val generatorCollection: Array<NoiseGeneratorImprovedF?>

    /*
     * pars:(par2,3,4=noiseOffset ; so that adjacent noise segments connect) (pars5,6,7=x,y,zArraySize),(pars8,10,12 =
     * x,y,z noiseScale)
     */
    fun generateNoiseOctaves(noiseArray: FloatArray?, xOffset: Int, yOffset: Int, zOffset: Int, xSize: Int, ySize: Int, zSize: Int, xScale: Float, yScale: Float, zScale: Float): FloatArray {
        var noiseArray = noiseArray
        if (noiseArray == null) {
            noiseArray = FloatArray(xSize * ySize * zSize)
        } else {
            for (i in noiseArray.indices) {
                noiseArray[i] = 0.0f
            }
        }
        var d3 = 1.0f
        for (j in 0 until octaves) {
            var d0 = xOffset.toFloat() * d3 * xScale
            val d1 = yOffset.toFloat() * d3 * yScale
            var d2 = zOffset.toFloat() * d3 * zScale
            var k: Int = MathHelper.floor_float_int(d0)
            var l: Int = MathHelper.floor_float_int(d2)
            d0 = d0 - k.toFloat()
            d2 = d2 - l.toFloat()
            k = k % 16777216
            l = l % 16777216
            d0 = d0 + k.toFloat()
            d2 = d2 + l.toFloat()
            generatorCollection[j]!!.populateNoiseArray(noiseArray, d0, d1, d2, xSize, ySize, zSize, xScale * d3, yScale * d3, zScale * d3, d3)
            d3 /= 2.0f
        }
        return noiseArray
    }

    /*
     * Bouncer function to the main one with some default arguments.
     */
    fun generateNoiseOctaves(noiseArray: FloatArray?, xOffset: Int, zOffset: Int, xSize: Int, zSize: Int, xScale: Float, zScale: Float, p_76305_10_: Float): FloatArray {
        return this.generateNoiseOctaves(noiseArray, xOffset, 10, zOffset, xSize, 1, zSize, xScale, 1.0f, zScale)
    }

    init {
        generatorCollection = arrayOfNulls<NoiseGeneratorImprovedF>(octaves)
        for (i in 0 until octaves) {
            generatorCollection[i] = NoiseGeneratorImprovedF(seed)
        }
    }
}