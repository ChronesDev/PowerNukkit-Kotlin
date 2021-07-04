package cn.nukkit.level.generator

import cn.nukkit.Server

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
class Flat @JvmOverloads constructor(options: Map<String, Object> = HashMap()) : Generator() {
    @get:Override
    override val id: Int
        get() = TYPE_FLAT
    private var level: ChunkManager? = null
    private var random: NukkitRandom? = null
    private val populators: List<Populator> = ArrayList()
    private var structure: Array<IntArray?>
    private val options: Map<String, Object>
    private var floorLevel = 0
    private var preset: String? = "2;7,2x3,2;1;"
    private var init = false
    private var biome = 0

    @get:Override
    override val chunkManager: ChunkManager?
        get() = level

    @get:Override
    override val settings: Map<String?, Any?>?
        get() = options

    @get:Override
    override val name: String?
        get() = "flat"

    protected fun parsePreset(preset: String?, chunkX: Int, chunkZ: Int) {
        try {
            this.preset = preset
            val presetArray: Array<String> = preset.split(";")
            val version: Int = Integer.parseInt(presetArray[0])
            val blocks = if (presetArray.size > 1) presetArray[1] else ""
            biome = if (presetArray.size > 2) Integer.parseInt(presetArray[2]) else 1
            val options = if (presetArray.size > 3) presetArray[1] else ""
            structure = arrayOfNulls(256)
            var y = 0
            for (block in blocks.split(",")) {
                var id: Int
                var meta = 0
                var cnt = 1
                if (Pattern.matches("^[0-9]{1,3}x[0-9]$", block)) {
                    //AxB
                    val s: Array<String> = block.split("x")
                    cnt = Integer.parseInt(s[0])
                    id = Integer.parseInt(s[1])
                } else if (Pattern.matches("^[0-9]{1,3}:[0-9]{0,2}$", block)) {
                    //A:B
                    val s: Array<String> = block.split(":")
                    id = Integer.parseInt(s[0])
                    meta = Integer.parseInt(s[1])
                } else if (Pattern.matches("^[0-9]{1,3}$", block)) {
                    //A
                    id = Integer.parseInt(block)
                } else {
                    continue
                }
                var cY = y
                y += cnt
                if (y > 0xFF) {
                    y = 0xFF
                }
                while (cY < y) {
                    structure[cY] = intArrayOf(id, meta)
                    ++cY
                }
            }
            floorLevel = y
            while (y <= 0xFF) {
                structure[y] = intArrayOf(0, 0)
                ++y
            }
            for (option in options.split(",")) {
                if (Pattern.matches("^[0-9a-z_]+$", option)) {
                    this.options.put(option, true)
                } else if (Pattern.matches("^[0-9a-z_]+\\([0-9a-z_ =]+\\)$", option)) {
                    val name: String = option.substring(0, option.indexOf("("))
                    val extra: String = option.substring(option.indexOf("(") + 1, option.indexOf(")"))
                    val map: Map<String, Float> = HashMap()
                    for (kv in extra.split(" ")) {
                        val data: Array<String> = kv.split("=")
                        map.put(data[0], Float.valueOf(data[1]))
                    }
                    this.options.put(name, map)
                }
            }
        } catch (e: Exception) {
            log.error("error while parsing the preset", e)
            throw RuntimeException(e)
        }
    }

    @Override
    override fun init(level: ChunkManager?, random: NukkitRandom?) {
        this.level = level
        this.random = random
    }

    @Override
    override fun generateChunk(chunkX: Int, chunkZ: Int) {
        if (!init) {
            init = true
            if (options.containsKey("preset") && !"".equals(options["preset"])) {
                parsePreset(options["preset"] as String?, chunkX, chunkZ)
            } else {
                parsePreset(preset, chunkX, chunkZ)
            }
        }
        this.generateChunk(level.getChunk(chunkX, chunkZ))
    }

    private fun generateChunk(chunk: FullChunk) {
        chunk.setGenerated()
        for (Z in 0..15) {
            for (X in 0..15) {
                chunk.setBiomeId(X, Z, biome)
                for (y in 0..255) {
                    val k = structure[y]!![0]
                    val l = structure[y]!![1]
                    chunk.setBlock(X, y, Z, structure[y]!![0], structure[y]!![1])
                }
            }
        }
    }

    @Override
    override fun populateChunk(chunkX: Int, chunkZ: Int) {
        val chunk: BaseFullChunk = level.getChunk(chunkX, chunkZ)
        random.setSeed(-0x21524111 xor (chunkX shl 8) xor chunkZ xor level.getSeed())
        for (populator in populators) {
            populator.populate(level, chunkX, chunkZ, random, chunk)
        }
    }

    @get:Override
    override val spawn: Vector3
        get() = Vector3(128, floorLevel, 128)

    init {
        this.options = options
        if (this.options.containsKey("decoration")) {
            val ores = PopulatorOre(BlockID.STONE, arrayOf<OreType>(
                    OreType(Block.get(BlockID.COAL_ORE), 20, 16, 0, 128),
                    OreType(Block.get(BlockID.IRON_ORE), 20, 8, 0, 64),
                    OreType(Block.get(BlockID.REDSTONE_ORE), 8, 7, 0, 16),
                    OreType(Block.get(BlockID.LAPIS_ORE), 1, 6, 0, 32),
                    OreType(Block.get(BlockID.GOLD_ORE), 2, 8, 0, 32),
                    OreType(Block.get(BlockID.DIAMOND_ORE), 1, 7, 0, 16),
                    OreType(Block.get(BlockID.DIRT), 20, 32, 0, 128),
                    OreType(Block.get(BlockID.GRAVEL), 20, 16, 0, 128)))
            populators.add(ores)
        }
    }
}