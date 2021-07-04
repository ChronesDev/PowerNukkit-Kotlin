package cn.nukkit.level.format.anvil.palette

import cn.nukkit.Server

/**
 * @author https://github.com/boy0001/
 */
class BlockDataPalette @JvmOverloads constructor(rawData: CharArray = CharArray(BLOCK_SIZE)) : Cloneable {
    @Volatile
    private var rawData: CharArray?

    @Volatile
    private var encodedData: BitArray4096? = null

    @Volatile
    private var palette: CharPalette? = null
    private val cachedRaw: CharArray?
        private get() {
            val raw = rawData
            if (raw != null) {
                return raw
            } else if (!Server.getInstance().isPrimaryThread()) {
                return raw
            }
            return rawData
        }

    @get:Synchronized
    val raw: CharArray?
        get() {
            val palette: CharPalette? = palette
            val encodedData: BitArray4096? = encodedData
            this.encodedData = null
            this.palette = null
            var raw = rawData
            if (raw == null && palette != null) {
                raw = if (encodedData != null) {
                    encodedData.toRaw()
                } else {
                    CharArray(BLOCK_SIZE)
                }
                for (i in 0 until BLOCK_SIZE) {
                    raw[i] = palette.getKey(raw[i].toInt())
                }
            } else {
                raw = CharArray(BLOCK_SIZE)
            }
            rawData = raw
            return rawData
        }

    private fun getIndex(x: Int, y: Int, z: Int): Int {
        return (x shl 8) + (z shl 4) + y // XZY = Bedrock format
    }

    fun getBlockData(x: Int, y: Int, z: Int): Int {
        return getFullBlock(x, y, z) and 0xF
    }

    fun getBlockId(x: Int, y: Int, z: Int): Int {
        return getFullBlock(x, y, z) shr Block.DATA_BITS
    }

    fun setBlockId(x: Int, y: Int, z: Int, id: Int) {
        setFullBlock(x, y, z, (id shl Block.DATA_BITS) as Char.toInt())
    }

    @Synchronized
    fun setBlockData(x: Int, y: Int, z: Int, data: Int) {
        val index = getIndex(x, y, z)
        val raw = cachedRaw
        if (raw != null) {
            val fullId = raw[index].toInt()
            raw[index] = (fullId and (0xFFF shl Block.DATA_BITS) or data).toChar()
        }
        if (palette != null && encodedData != null) {
            val fullId: Char = palette.getKey(encodedData.getAt(index))
            if (fullId and Block.DATA_MASK !== data) {
                setPaletteFullBlock(index, (fullId.toInt() and (0xFFF shl Block.DATA_BITS) or data).toChar())
            }
        } else {
            throw IllegalStateException("Raw data and pallete was null")
        }
    }

    fun getFullBlock(x: Int, y: Int, z: Int): Int {
        return getFullBlock(getIndex(x, y, z))
    }

    fun setFullBlock(x: Int, y: Int, z: Int, value: Int) {
        this.setFullBlock(getIndex(x, y, z), value.toChar())
    }

    fun getAndSetFullBlock(x: Int, y: Int, z: Int, value: Int): Int {
        return getAndSetFullBlock(getIndex(x, y, z), value.toChar())
    }

    private fun getAndSetFullBlock(index: Int, value: Char): Int {
        val raw = cachedRaw
        return if (raw != null) {
            val result = raw[index]
            raw[index] = value
            result.toInt()
        } else if (palette != null && encodedData != null) {
            val result: Char = palette.getKey(encodedData.getAt(index))
            if (result != value) {
                setPaletteFullBlock(index, value)
            }
            result.toInt()
        } else {
            throw IllegalStateException("Raw data and pallete was null")
        }
    }

    private fun getFullBlock(index: Int): Int {
        val raw = cachedRaw
        return if (raw != null) {
            raw[index].toInt()
        } else if (palette != null && encodedData != null) {
            palette.getKey(encodedData.getAt(index)).toInt()
        } else {
            throw IllegalStateException("Raw data and pallete was null")
        }
    }

    private fun setFullBlock(index: Int, value: Char) {
        val raw = cachedRaw
        if (raw != null) {
            raw[index] = value
        } else if (!setPaletteFullBlock(index, value)) {
            throw IllegalStateException("Raw data and pallete was null")
        }
    }

    @Synchronized
    private fun setPaletteFullBlock(index: Int, value: Char): Boolean {
        val palette: CharPalette? = palette
        val encodedData: BitArray4096? = encodedData
        if (palette != null && encodedData != null) {
            val encodedValue: Char = palette.getValue(value)
            if (encodedValue != Character.MAX_VALUE) {
                encodedData.setAt(index, encodedValue.toInt())
            } else {
                val raw: CharArray = encodedData.toRaw()
                for (i in 0 until BLOCK_SIZE) {
                    raw[i] = palette.getKey(raw[i].toInt())
                }
                raw[index] = value
                rawData = raw
                this.encodedData = null
                this.palette = null
            }
            return true
        }
        return false
    }

    @Synchronized
    fun compress(): Boolean {
        val raw = rawData
        if (raw != null) {
            var unique = 0.toChar()
            val countTable: BooleanArray = ThreadCache.boolCache4096.get()
            val mapFullTable: CharArray = ThreadCache.charCache4096.get()
            val mapBitTable: CharArray = ThreadCache.charCache4096v2.get()
            Arrays.fill(countTable, false)
            for (c in raw) {
                if (!countTable[c.toInt()]) {
                    mapBitTable[unique.toInt()] = c
                    countTable[c.toInt()] = true
                    unique++
                }
            }
            val keys: CharArray = Arrays.copyOfRange(mapBitTable, 0, unique)
            if (keys.size > 1) {
                Arrays.sort(keys)
                for (c in keys.indices) {
                    mapFullTable[keys[c.toInt()].toInt()] = c
                }
            } else {
                mapFullTable[keys[0].toInt()] = 0
            }
            val palette = CharPalette()
            palette.set(keys)
            val bits: Int = MathHelper.log2(unique.toInt() - 1)
            val encodedData = BitArray4096(bits)
            for (i in raw.indices) {
                mapBitTable[i] = mapFullTable[raw[i].toInt()]
            }
            encodedData.fromRaw(mapBitTable)
            this.palette = palette
            this.encodedData = encodedData
            rawData = null
            return true
        }
        return false
    }

    @Synchronized
    fun clone(): BlockDataPalette {
        val raw = raw
        return BlockDataPalette(raw.clone())
    }

    companion object {
        private const val BLOCK_SIZE = 4096
    }

    // TODO compress unused sections
    // private byte[] compressedData;
    init {
        Preconditions.checkArgument(rawData.size == BLOCK_SIZE, "Data is not 4096")
        this.rawData = rawData
    }
}