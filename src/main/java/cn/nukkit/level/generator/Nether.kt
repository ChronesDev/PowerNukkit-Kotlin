package cn.nukkit.level.generator

import cn.nukkit.block.*

class Nether @JvmOverloads constructor(options: Map<String?, Object?>? = HashMap()) : Generator() {
    private var level: ChunkManager? = null

    /**
     * @var Random
     */
    private var nukkitRandom: NukkitRandom? = null
    private var random: Random? = null
    private val lavaHeight = 32.0
    private val bedrockDepth = 5.0
    private val noiseGen: Array<SimplexF?> = arrayOfNulls<SimplexF>(3)
    private val populators: List<Populator> = ArrayList()
    private val generationPopulators: List<Populator> = ArrayList()
    private var localSeed1: Long = 0
    private var localSeed2: Long = 0

    @get:Override
    override val id: Int
        get() = Generator.TYPE_NETHER

    @get:Override
    override val dimension: Int
        get() = Level.DIMENSION_NETHER

    @get:Override
    override val name: String?
        get() = "nether"

    @get:Override
    override val settings: Map<String?, Any?>?
        get() = HashMap()

    @get:Override
    override val chunkManager: ChunkManager?
        get() = level

    @Override
    override fun init(level: ChunkManager?, random: NukkitRandom?) {
        this.level = level
        nukkitRandom = random
        this.random = Random()
        nukkitRandom.setSeed(this.level.getSeed())
        for (i in noiseGen.indices) {
            noiseGen[i] = SimplexF(nukkitRandom, 4, 1 / 4f, 1 / 64f)
        }
        nukkitRandom.setSeed(this.level.getSeed())
        localSeed1 = this.random.nextLong()
        localSeed2 = this.random.nextLong()
        val ores = PopulatorOre(Block.NETHERRACK, arrayOf<OreType>(
                OreType(Block.get(BlockID.QUARTZ_ORE), 20, 16, 0, 128),
                OreType(Block.get(BlockID.SOUL_SAND), 5, 64, 0, 128),
                OreType(Block.get(BlockID.GRAVEL), 5, 64, 0, 128),
                OreType(Block.get(BlockID.LAVA), 1, 16, 0, lavaHeight.toInt())))
        populators.add(ores)
        val groundFire = PopulatorGroundFire()
        groundFire.setBaseAmount(1)
        groundFire.setRandomAmount(1)
        populators.add(groundFire)
        val lava = PopulatorLava()
        lava.setBaseAmount(1)
        lava.setRandomAmount(2)
        populators.add(lava)
        populators.add(PopulatorGlowStone())
        val ore = PopulatorOre(Block.NETHERRACK, arrayOf<OreType>(
                OreType(Block.get(BlockID.QUARTZ_ORE), 40, 16, 0, 128, NETHERRACK),
                OreType(Block.get(BlockID.SOUL_SAND), 1, 64, 30, 35, NETHERRACK),
                OreType(Block.get(BlockID.LAVA), 32, 1, 0, 32, NETHERRACK),
                OreType(Block.get(BlockID.MAGMA), 32, 16, 26, 37, NETHERRACK)))
        populators.add(ore)
    }

    @Override
    override fun generateChunk(chunkX: Int, chunkZ: Int) {
        val baseX = chunkX shl 4
        val baseZ = chunkZ shl 4
        nukkitRandom.setSeed(chunkX * localSeed1 xor chunkZ * localSeed2 xor level.getSeed())
        val chunk: BaseFullChunk = level.getChunk(chunkX, chunkZ)
        for (x in 0..15) {
            for (z in 0..15) {
                val biome: Biome = EnumBiome.HELL.biome
                chunk.setBiomeId(x, z, biome.getId())
                chunk.setBlockId(x, 0, z, Block.BEDROCK)
                for (y in 115..126) {
                    chunk.setBlockId(x, y, z, Block.NETHERRACK)
                }
                chunk.setBlockId(x, 127, z, Block.BEDROCK)
                for (y in 1..126) {
                    if (getNoise(baseX or x, y, baseZ or z) > 0) {
                        chunk.setBlockId(x, y, z, Block.NETHERRACK)
                    } else if (y <= lavaHeight) {
                        chunk.setBlockId(x, y, z, Block.STILL_LAVA)
                        chunk.setBlockLight(x, y + 1, z, 15)
                    }
                }
            }
        }
        for (populator in generationPopulators) {
            populator.populate(level, chunkX, chunkZ, nukkitRandom, chunk)
        }
    }

    @Override
    override fun populateChunk(chunkX: Int, chunkZ: Int) {
        val chunk: BaseFullChunk = level.getChunk(chunkX, chunkZ)
        nukkitRandom.setSeed(-0x21524111 xor (chunkX shl 8) xor chunkZ xor level.getSeed())
        for (populator in populators) {
            populator.populate(level, chunkX, chunkZ, nukkitRandom, chunk)
        }
        val biome: Biome = EnumBiome.getBiome(chunk.getBiomeId(7, 7))
        biome.populateChunk(level, chunkX, chunkZ, nukkitRandom)
    }

    override val spawn: Vector3
        get() = Vector3(0, 64, 0)

    fun getNoise(x: Int, y: Int, z: Int): Float {
        var `val` = 0f
        for (i in noiseGen.indices) {
            `val` += noiseGen[i].noise3D(x shr i, y, z shr i, true)
        }
        return `val`
    }
}