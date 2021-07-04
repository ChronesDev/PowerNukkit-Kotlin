package cn.nukkit.level.generator.populator.impl

import cn.nukkit.block.Block

/**
 * @author Niall Lindsay (Niall7459, Nukkit Project)
 */
class PopulatorSugarcane : PopulatorSurfaceBlock() {
    private fun findWater(x: Int, y: Int, z: Int, level: Level): Boolean {
        var count = 0
        for (i in x - 4 until x + 4) {
            for (j in z - 4 until z + 4) {
                val b: Int = level.getBlockIdAt(i, y, j)
                if (b == Block.WATER || b == Block.STILL_WATER) {
                    count++
                }
                if (count > 10) {
                    return true
                }
            }
        }
        return count > 10
    }

    @Override
    protected fun canStay(x: Int, y: Int, z: Int, chunk: FullChunk): Boolean {
        return EnsureCover.ensureCover(x, y, z, chunk) && (EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk) || EnsureBelow.ensureBelow(x, y, z, SAND, chunk)) && findWater(x, y - 1, z, chunk.getProvider().getLevel())
    }

    @Override
    protected fun getBlockId(x: Int, z: Int, random: NukkitRandom?, chunk: FullChunk?): Int {
        return SUGARCANE_BLOCK shl Block.DATA_BITS or 1
    }
}