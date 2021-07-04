package cn.nukkit.level.generator.populator.impl

import cn.nukkit.level.format.FullChunk

/**
 * @author Niall Lindsay (Niall7459, Nukkit Project)
 */
class PopulatorTallSugarcane : PopulatorSugarcane() {
    @Override
    protected fun placeBlock(x: Int, y: Int, z: Int, id: Int, chunk: FullChunk, random: NukkitRandom?) {
        val height: Int = ThreadLocalRandom.current().nextInt(3) + 1
        for (i in 0 until height) {
            chunk.setFullBlockId(x, y + i, z, id)
        }
    }
}