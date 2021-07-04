package cn.nukkit.level.biome.impl.mesa

import cn.nukkit.api.NewRakNetOnly

/**
 * @author DaPorkchop_
 *
 *
 * Handles the placement of stained clay for all mesa variants
 */
class MesaBiome : CoveredBiome() {
    companion object {
        private val STATE_TERRACOTTA: BlockState = BlockState.of(HARDENED_CLAY)
        private val STATE_RED_SAND: BlockState = BlockState.of(SAND, BlockSand.RED)
        private val STATE_RED_SANDSTONE: BlockState = BlockState.of(RED_SANDSTONE)
        private val STATE_STAINED_TERRACOTTA: Array<BlockState?> = arrayOfNulls<BlockState>(16)
        val colorLayer = IntArray(64)
        val redSandNoise: SimplexF = SimplexF(NukkitRandom(937478913), 2f, 1 / 4f, 1 / 4f)
        val colorNoise: SimplexF = SimplexF(NukkitRandom(193759875), 2f, 1 / 4f, 1 / 32f)
        private fun setRandomLayerColor(random: Random, sliceCount: Int, color: Int) {
            for (i in 0 until random.nextInt(4) + sliceCount) {
                var j: Int = random.nextInt(colorLayer.size)
                var k = 0
                while (k < random.nextInt(2) + 1 && j < colorLayer.size) {
                    colorLayer[j++] = color
                    k++
                }
            }
        }

        init {
            for (i in STATE_STAINED_TERRACOTTA.indices) {
                STATE_STAINED_TERRACOTTA[i] = BlockState.of(STAINED_HARDENED_CLAY, i)
            }
            val random = Random(29864)
            Arrays.fill(colorLayer, -1) // hard clay, other values are stained clay
            setRandomLayerColor(cn.nukkit.level.biome.impl.mesa.random, 14, 1) // orange
            setRandomLayerColor(cn.nukkit.level.biome.impl.mesa.random, 8, 4) // yellow
            setRandomLayerColor(cn.nukkit.level.biome.impl.mesa.random, 7, 12) // brown
            setRandomLayerColor(cn.nukkit.level.biome.impl.mesa.random, 10, 14) // red
            val i = 0
            val j = 0
            while (cn.nukkit.level.biome.impl.mesa.i < cn.nukkit.level.biome.impl.mesa.random.nextInt(3) + 3) {
                cn.nukkit.level.biome.impl.mesa.j += cn.nukkit.level.biome.impl.mesa.random.nextInt(6) + 4
                if (cn.nukkit.level.biome.impl.mesa.j >= colorLayer.size - 3) {
                    break
                }
                if (cn.nukkit.level.biome.impl.mesa.random.nextInt(2) === 0 || cn.nukkit.level.biome.impl.mesa.j < colorLayer.size - 1 && cn.nukkit.level.biome.impl.mesa.random.nextInt(2) === 0) {
                    colorLayer[cn.nukkit.level.biome.impl.mesa.j - 1] = 8 // light gray
                } else {
                    colorLayer[cn.nukkit.level.biome.impl.mesa.j] = 0 // white
                }
                cn.nukkit.level.biome.impl.mesa.i++
            }
        }
    }

    private val moundNoise: SimplexF = SimplexF(NukkitRandom(347228794), 2f, 1 / 4f, moundFrequency)
    protected var moundHeight = 0
    fun setMoundHeight(height: Int) {
        moundHeight = height
    }

    @NewRakNetOnly
    @Override
    fun getSurfaceDepth(x: Int, y: Int, z: Int): Int {
        return if (y < 71 + Math.round((redSandNoise.noise2D(x, z, true) + 1) * 1.5f)) 3 else y - 66
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun getSurfaceState(x: Int, y: Int, z: Int): BlockState {
        return if (y < 71 + Math.round((redSandNoise.noise2D(x, z, true) + 1) * 1.5f)) {
            STATE_RED_SAND
        } else {
            val meta = colorLayer[y + Math.round((colorNoise.noise2D(x, z, true) + 1) * 1.5f) and 0x3F]
            if (meta == -1) STATE_TERRACOTTA else STATE_STAINED_TERRACOTTA[Math.max(0, meta)]
        }
    }

    @NewRakNetOnly
    @Override
    fun getGroundDepth(x: Int, y: Int, z: Int): Int {
        return if (y < 71 + Math.round((redSandNoise.noise2D(x, z, true) + 1) * 1.5f)) 2 else 0
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun getGroundState(x: Int, y: Int, z: Int): BlockState {
        return STATE_RED_SANDSTONE
    }

    @get:Override
    val name: String
        get() = "Mesa"
    protected val moundFrequency: Float
        protected get() = 1 / 128f

    @Override
    fun getHeightOffset(x: Int, z: Int): Int {
        val n: Float = moundNoise.noise2D(x, z, true)
        val a = minHill()
        return if (n > a && n < a + 0.2f) ((n - a) * 5f * moundHeight).toInt() else if (n < a + 0.1f) 0 else moundHeight
    }

    protected fun minHill(): Float {
        return -0.1f
    }

    @Override
    fun canRain(): Boolean {
        return false
    }

    init {
        val cactus = PopulatorCactus()
        cactus.setBaseAmount(1)
        cactus.setRandomAmount(1)
        this.addPopulator(cactus)
        val deadBush = PopulatorDeadBush()
        deadBush.setBaseAmount(3)
        deadBush.setRandomAmount(2)
        this.addPopulator(deadBush)
        setMoundHeight(17)
    }
}