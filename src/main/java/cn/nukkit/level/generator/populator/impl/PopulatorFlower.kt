package cn.nukkit.level.generator.populator.impl

import cn.nukkit.block.Block

/**
 * @author Angelic47, Niall Lindsay (Niall7459), Nukkit Project
 */
class PopulatorFlower : PopulatorSurfaceBlock() {
    val types: List<IntArray> = ArrayList()
    fun addType(a: Int, b: Int) {
        val c = IntArray(2)
        c[0] = a
        c[1] = b
        types.add(c)
    }

    @Override
    protected fun placeBlock(x: Int, y: Int, z: Int, id: Int, chunk: FullChunk, random: NukkitRandom?) {
        if (types.size() !== 0) {
            val type = types[ThreadLocalRandom.current().nextInt(types.size())]
            chunk.setFullBlockId(x, y, z, type[0] shl Block.DATA_BITS or type[1])
            if (type[0] == DOUBLE_PLANT) {
                chunk.setFullBlockId(x, y + 1, z, type[0] shl Block.DATA_BITS or (8 or type[1]))
            }
        }
    }

    @Override
    protected fun canStay(x: Int, y: Int, z: Int, chunk: FullChunk?): Boolean {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk)
    }

    @Override
    protected fun getBlockId(x: Int, z: Int, random: NukkitRandom?, chunk: FullChunk?): Int {
        return 0
    }
}