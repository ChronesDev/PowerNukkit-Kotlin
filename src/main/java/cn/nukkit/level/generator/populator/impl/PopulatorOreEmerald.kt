package cn.nukkit.level.generator.populator.impl

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author GoodLucky777
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class PopulatorOreEmerald : Populator() {
    @Override
    fun populate(level: ChunkManager?, chunkX: Int, chunkZ: Int, random: NukkitRandom, chunk: FullChunk) {
        for (i in 0..10) {
            val x: Int = random.nextBoundedInt(16)
            val z: Int = random.nextBoundedInt(16)
            val y: Int = NukkitMath.randomRange(random, 0, 32)
            if (chunk.getBlockState(x, y, z) !== STATE_STONE) {
                continue
            }
            chunk.setBlockState(x, y, z, STATE_EMERALD_ORE)
        }
    }

    companion object {
        private val STATE_STONE: BlockState = BlockState.of(STONE)
        private val STATE_EMERALD_ORE: BlockState = BlockState.of(EMERALD_ORE)
    }
}