package cn.nukkit.level.biome

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class Biome : BlockID {
    private val populators: ArrayList<Populator> = ArrayList()
    var id = 0
    var baseHeight = 0.1f
    var heightVariation = 0.3f
    fun clearPopulators() {
        populators.clear()
    }

    fun addPopulator(populator: Populator?) {
        populators.add(populator)
    }

    fun populateChunk(level: ChunkManager, chunkX: Int, chunkZ: Int, random: NukkitRandom?) {
        val chunk: FullChunk = level.getChunk(chunkX, chunkZ)
        for (populator in populators) {
            populator.populate(level, chunkX, chunkZ, random, chunk)
        }
    }

    fun getPopulators(): ArrayList<Populator> {
        return populators
    }

    abstract val name: String

    @Override
    override fun hashCode(): Int {
        return id
    }

    @Override
    override fun equals(obj: Object): Boolean {
        return hashCode() == obj.hashCode()
    }

    //whether or not water should freeze into ice on generation
    val isFreezing: Boolean
        get() = false

    /**
     * Whether or not overhangs should generate in this biome (places where solid blocks generate over air)
     *
     * This should probably be used with a custom max elevation or things can look stupid
     *
     * @return overhang
     */
    fun doesOverhang(): Boolean {
        return false
    }

    /**
     * How much offset should be added to the min/max heights at this position
     *
     * @param x x
     * @param z z
     * @return height offset
     */
    fun getHeightOffset(x: Int, z: Int): Int {
        return 0
    }

    fun canRain(): Boolean {
        return true
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val isDry: Boolean
        get() = false

    companion object {
        const val MAX_BIOMES = 256
        val biomes = arrayOfNulls<Biome>(MAX_BIOMES)
        val unorderedBiomes: List<Biome> = ArrayList()
        fun register(id: Int, biome: Biome) {
            biome.id = id
            biomes[id] = biome
            unorderedBiomes.add(biome)
        }

        fun getBiome(id: Int): Biome {
            val biome = biomes[id]
            return biome ?: EnumBiome.OCEAN.biome
        }

        /**
         * Get Biome by name.
         *
         * @param name Name of biome. Name could contain symbol "_" instead of space
         * @return Biome. Null - when biome was not found
         */
        fun getBiome(name: String): Biome? {
            for (biome in biomes) {
                if (biome != null) {
                    if (biome.name.equalsIgnoreCase(name.replace("_", " "))) return biome
                }
            }
            return null
        }
    }
}