package cn.nukkit.level.generator

import cn.nukkit.block.BlockID

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class Generator : BlockID {
    abstract val id: Int
    val dimension: Int
        get() = Level.DIMENSION_OVERWORLD

    abstract fun init(level: ChunkManager?, random: NukkitRandom?)
    abstract fun generateChunk(chunkX: Int, chunkZ: Int)
    abstract fun populateChunk(chunkX: Int, chunkZ: Int)
    abstract val settings: Map<String?, Any?>?
    abstract val name: String?
    abstract val spawn: Vector3?
    abstract val chunkManager: ChunkManager?

    companion object {
        const val TYPE_OLD = 0
        const val TYPE_INFINITE = 1
        const val TYPE_FLAT = 2
        const val TYPE_NETHER = 3
        private val nameList: Map<String, Class<out Generator?>> = HashMap()
        private val typeList: Map<Integer, Class<out Generator?>> = HashMap()
        fun addGenerator(clazz: Class<out Generator?>?, name: String, type: Int): Boolean {
            var name = name
            name = name.toLowerCase()
            if (clazz != null && !nameList.containsKey(name)) {
                nameList.put(name, clazz)
                if (!typeList.containsKey(type)) {
                    typeList.put(type, clazz)
                }
                return true
            }
            return false
        }

        val generatorList: Array<String>
            get() {
                val keys = arrayOfNulls<String>(nameList.size())
                return nameList.keySet().toArray(keys)
            }

        fun getGenerator(name: String): Class<out Generator?>? {
            var name = name
            name = name.toLowerCase()
            return if (nameList.containsKey(name)) {
                nameList[name]
            } else Normal::class.java
        }

        fun getGenerator(type: Int): Class<out Generator?>? {
            return if (typeList.containsKey(type)) {
                typeList[type]
            } else Normal::class.java
        }

        fun getGeneratorName(c: Class<out Generator?>?): String {
            for (key in nameList.keySet()) {
                if (nameList[key].equals(c)) {
                    return key
                }
            }
            return "unknown"
        }

        fun getGeneratorType(c: Class<out Generator?>?): Int {
            for (key in typeList.keySet()) {
                if (typeList[key].equals(c)) {
                    return key
                }
            }
            return TYPE_INFINITE
        }
    }
}