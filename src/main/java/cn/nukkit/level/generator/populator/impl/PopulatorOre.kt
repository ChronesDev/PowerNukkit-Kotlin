package cn.nukkit.level.generator.populator.impl

import cn.nukkit.api.NewRakNetOnly

/**
 * @author DaPorkchop_
 */
class PopulatorOre : Populator {
    private val replaceId: Int
    private var oreTypes: Array<OreType> = OreType.EMPTY_ARRAY

    /**
     * @implNote Removed from the new-raknet branch
     */
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    constructor() : this(Block.STONE) {
    }

    /**
     * @implNote Removed from the new-raknet branch
     */
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    constructor(id: Int) {
        replaceId = id
    }

    @NewRakNetOnly
    constructor(replaceId: Int, oreTypes: Array<OreType>) {
        this.replaceId = replaceId
        this.oreTypes = oreTypes
    }

    @Override
    fun populate(level: ChunkManager, chunkX: Int, chunkZ: Int, random: NukkitRandom?, chunk: FullChunk?) {
        val sx = chunkX shl 4
        val ex = sx + 15
        val sz = chunkZ shl 4
        val ez = sz + 15
        for (type in oreTypes) {
            for (i in 0 until type.clusterCount) {
                val x: Int = NukkitMath.randomRange(random, sx, ex)
                val z: Int = NukkitMath.randomRange(random, sz, ez)
                val y: Int = NukkitMath.randomRange(random, type.minHeight, type.maxHeight)
                if (level.getBlockIdAt(x, y, z) !== replaceId) {
                    continue
                }
                type.spawn(level, random, replaceId, x, y, z)
            }
        }
    }

    /**
     * @implNote Removed from the new-raknet branch
     */
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    fun setOreTypes(oreTypes: Array<OreType>) {
        this.oreTypes = oreTypes
    }
}