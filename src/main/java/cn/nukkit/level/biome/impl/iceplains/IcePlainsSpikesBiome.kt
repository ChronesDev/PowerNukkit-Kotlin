package cn.nukkit.level.biome.impl.iceplains

import cn.nukkit.api.RemovedFromNewRakNet

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
class IcePlainsSpikesBiome : IcePlainsBiome() {
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    fun getSurfaceBlock(y: Int): Int {
        return if (useNewRakNetSurface()) {
            getSurfaceId(0, y, 0) shr 4
        } else SNOW_BLOCK
    }

    override val name: String
        get() = "Ice Plains Spikes"

    @get:Override
    val isFreezing: Boolean
        get() = true

    /**
     * @author DaPorkchop_
     *
     *
     * Please excuse this mess, but it runs way faster than the correct method
     */
    private class PopulatorIceSpikes : Populator() {
        @Override
        fun populate(level: ChunkManager, chunkX: Int, chunkZ: Int, random: NukkitRandom, chunk: FullChunk) {
            for (i in 0..7) {
                val x: Int = (chunkX shl 4) + random.nextBoundedInt(16)
                val z: Int = (chunkZ shl 4) + random.nextBoundedInt(16)
                val isTall = random.nextBoundedInt(16) === 0
                val height: Int = 10 + random.nextBoundedInt(16) + if (isTall) random.nextBoundedInt(31) else 0
                val startY = getHighestWorkableBlock(x, z, chunk)
                val maxY = startY + height
                if (isTall) {
                    for (y in startY until maxY) {
                        //center column
                        level.setBlockAt(x, y, z, PACKED_ICE)
                        //t shape
                        level.setBlockAt(x + 1, y, z, PACKED_ICE)
                        level.setBlockAt(x - 1, y, z, PACKED_ICE)
                        level.setBlockAt(x, y, z + 1, PACKED_ICE)
                        level.setBlockAt(x, y, z - 1, PACKED_ICE)
                        //additional blocks on the side
                        if (random.nextBoolean()) {
                            level.setBlockAt(x + 1, y, z + 1, PACKED_ICE)
                        }
                        if (random.nextBoolean()) {
                            level.setBlockAt(x + 1, y, z - 1, PACKED_ICE)
                        }
                        if (random.nextBoolean()) {
                            level.setBlockAt(x - 1, y, z + 1, PACKED_ICE)
                        }
                        if (random.nextBoolean()) {
                            level.setBlockAt(x - 1, y, z - 1, PACKED_ICE)
                        }
                    }
                    //finish with a point
                    level.setBlockAt(x + 1, maxY, z, PACKED_ICE)
                    level.setBlockAt(x - 1, maxY, z, PACKED_ICE)
                    level.setBlockAt(x, maxY, z + 1, PACKED_ICE)
                    level.setBlockAt(x, maxY, z - 1, PACKED_ICE)
                    for (y in maxY until maxY + 3) {
                        level.setBlockAt(x, y, z, PACKED_ICE)
                    }
                } else {
                    //the maximum possible radius in blocks
                    val baseWidth: Int = random.nextBoundedInt(1) + 4
                    val shrinkFactor = baseWidth / height.toFloat()
                    var currWidth = baseWidth.toFloat()
                    for (y in startY until maxY) {
                        var xx = (-currWidth).toInt()
                        while (xx < currWidth) {
                            var zz = (-currWidth).toInt()
                            while (zz < currWidth) {
                                val currDist = Math.sqrt(xx * xx + zz * zz) as Int
                                if (currWidth.toInt() != currDist && random.nextBoolean()) {
                                    level.setBlockAt(x + xx, y, z + zz, PACKED_ICE)
                                }
                                zz++
                            }
                            xx++
                        }
                        currWidth -= shrinkFactor
                    }
                }
            }
        }

        fun getHighestWorkableBlock(x: Int, z: Int, chunk: FullChunk): Int {
            return chunk.getHighestBlockAt(x and 0xF, z and 0xF) - 5
        }
    }

    init {
        val iceSpikes = PopulatorIceSpikes()
        this.addPopulator(iceSpikes)
    }
}