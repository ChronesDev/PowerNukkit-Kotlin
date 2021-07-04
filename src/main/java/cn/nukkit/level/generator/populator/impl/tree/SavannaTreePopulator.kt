package cn.nukkit.level.generator.populator.impl.tree

import cn.nukkit.block.Block

class SavannaTreePopulator @JvmOverloads constructor(private val type: Int = BlockSapling.ACACIA) : Populator() {
    private var level: ChunkManager? = null
    private var randomAmount = 0
    private var baseAmount = 0
    fun setRandomAmount(randomAmount: Int) {
        this.randomAmount = randomAmount
    }

    fun setBaseAmount(baseAmount: Int) {
        this.baseAmount = baseAmount
    }

    @Override
    fun populate(level: ChunkManager?, chunkX: Int, chunkZ: Int, random: NukkitRandom, chunk: FullChunk?) {
        this.level = level
        val amount: Int = random.nextBoundedInt(randomAmount + 1) + baseAmount
        val v = Vector3()
        for (i in 0 until amount) {
            val x: Int = NukkitMath.randomRange(random, chunkX shl 4, (chunkX shl 4) + 15)
            val z: Int = NukkitMath.randomRange(random, chunkZ shl 4, (chunkZ shl 4) + 15)
            val y = getHighestWorkableBlock(x, z)
            if (y == -1) {
                continue
            }
            ObjectSavannaTree().generate(level, random, v.setComponents(x, y, z))
        }
    }

    private fun getHighestWorkableBlock(x: Int, z: Int): Int {
        var y: Int
        y = 127
        while (y > 0) {
            val b: Int = level.getBlockIdAt(x, y, z)
            if (b == Block.DIRT || b == Block.GRASS) {
                break
            } else if (b != Block.AIR && b != Block.SNOW_LAYER) {
                return -1
            }
            --y
        }
        return ++y
    }
}