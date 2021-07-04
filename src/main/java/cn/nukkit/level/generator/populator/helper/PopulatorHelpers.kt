package cn.nukkit.level.generator.populator.helper

import cn.nukkit.block.BlockID

/**
 * @author DaPorkchop_
 */
object PopulatorHelpers : BlockID {
    private val nonSolidBlocks: IntSet = IntOpenHashSet()
    fun canGrassStay(x: Int, y: Int, z: Int, chunk: FullChunk): Boolean {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk)
    }

    fun isNonSolid(id: Int): Boolean {
        return nonSolidBlocks.contains(id)
    }

    init {
        nonSolidBlocks.add(AIR)
        nonSolidBlocks.add(LEAVES)
        nonSolidBlocks.add(LEAVES2)
        nonSolidBlocks.add(SNOW_LAYER)
    }
}