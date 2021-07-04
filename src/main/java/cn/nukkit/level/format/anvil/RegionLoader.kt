package cn.nukkit.level.format.anvil

import cn.nukkit.level.format.FullChunk

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
class RegionLoader(level: LevelProvider?, regionX: Int, regionZ: Int) : BaseRegionLoader(level, regionX, regionZ, "mca") {
    @Override
    protected fun isChunkGenerated(index: Int): Boolean {
        val array: IntArray = this.primitiveLocationTable.get(index)
        return !(array[0] == 0 || array[1] == 0)
    }

    @Override
    @Throws(IOException::class)
    fun readChunk(x: Int, z: Int): Chunk? {
        val index = getChunkOffset(x, z)
        if (index < 0 || index >= 4096) {
            return null
        }
        this.lastUsed = System.currentTimeMillis()
        return if (!isChunkGenerated(index)) {
            null
        } else try {
            val table: IntArray = this.primitiveLocationTable.get(index)
            val raf: RandomAccessFile = this.getRandomAccessFile()
            raf.seek(table[0].toLong() shl 12L)
            val length: Int = raf.readInt()
            val compression: Byte = raf.readByte()
            if (length <= 0 || length >= MAX_SECTOR_LENGTH) {
                if (length >= MAX_SECTOR_LENGTH) {
                    table[0] = ++this.lastSector
                    table[1] = 1
                    this.primitiveLocationTable.put(index, table)
                    log.error("Corrupted chunk header detected")
                }
                return null
            }
            if (length > table[1] shl 12) {
                log.error("Corrupted bigger chunk detected")
                table[1] = length shr 12
                this.primitiveLocationTable.put(index, table)
                writeLocationIndex(index)
            } else if (compression != COMPRESSION_ZLIB && compression != COMPRESSION_GZIP) {
                log.error("Invalid compression type")
                return null
            }
            val data = ByteArray(length - 1)
            raf.readFully(data)
            val chunk: Chunk? = unserializeChunk(data)
            if (chunk != null) {
                chunk
            } else {
                log.error("Corrupted chunk detected at ({}, {}) in {}", x, z, levelProvider.getName())
                null
            }
        } catch (e: EOFException) {
            log.error("Your world is corrupt, because some code is bad and corrupted it. oops. ")
            null
        }
    }

    @Override
    protected fun unserializeChunk(data: ByteArray?): Chunk? {
        return Chunk.fromBinary(data, this.levelProvider)
    }

    @Override
    fun chunkExists(x: Int, z: Int): Boolean {
        return isChunkGenerated(getChunkOffset(x, z))
    }

    @Override
    @Throws(IOException::class)
    protected fun saveChunk(x: Int, z: Int, chunkData: ByteArray) {
        val length = chunkData.size + 1
        if (length + 4 > MAX_SECTOR_LENGTH) {
            throw ChunkException("Chunk is too big! " + (length + 4) + " > " + MAX_SECTOR_LENGTH)
        }
        val sectors = Math.ceil((length + 4) / 4096.0) as Int
        val index = getChunkOffset(x, z)
        var indexChanged = false
        val table: IntArray = this.primitiveLocationTable.get(index)
        if (table[1] < sectors) {
            table[0] = this.lastSector + 1
            this.primitiveLocationTable.put(index, table)
            this.lastSector += sectors
            indexChanged = true
        } else if (table[1] != sectors) {
            indexChanged = true
        }
        table[1] = sectors
        table[2] = (System.currentTimeMillis() / 1000.0) as Int
        this.primitiveLocationTable.put(index, table)
        val raf: RandomAccessFile = this.getRandomAccessFile()
        raf.seek(table[0].toLong() shl 12L)
        val stream = BinaryStream()
        stream.put(Binary.writeInt(length))
        stream.putByte(COMPRESSION_ZLIB)
        stream.put(chunkData)
        var data: ByteArray = stream.getBuffer()
        if (data.size < sectors shl 12) {
            val newData = ByteArray(sectors shl 12)
            System.arraycopy(data, 0, newData, 0, data.size)
            data = newData
        }
        raf.write(data)
        if (indexChanged) {
            writeLocationIndex(index)
        }
    }

    @Override
    fun removeChunk(x: Int, z: Int) {
        val index = getChunkOffset(x, z)
        val table: IntArray = this.primitiveLocationTable.get(0)
        table[0] = 0
        table[1] = 0
        this.primitiveLocationTable.put(index, table)
    }

    @Override
    @Throws(Exception::class)
    fun writeChunk(chunk: FullChunk) {
        this.lastUsed = System.currentTimeMillis()
        val chunkData: ByteArray = chunk.toBinary()!!
        saveChunk(chunk.getX() and 0x1f, chunk.getZ() and 0x1f, chunkData)
    }

    @Override
    @Throws(IOException::class)
    fun close() {
        writeLocationTable()
        this.levelProvider = null
        super.close()
    }

    @Override
    @Throws(Exception::class)
    fun doSlowCleanUp(): Int {
        val raf: RandomAccessFile = this.getRandomAccessFile()
        for (i in 0..1023) {
            var table: IntArray = this.primitiveLocationTable.get(i)
            if (table[0] == 0 || table[1] == 0) {
                continue
            }
            raf.seek(table[0].toLong() shl 12L)
            var chunk = ByteArray(table[1] shl 12)
            raf.readFully(chunk)
            val length: Int = Binary.readInt(Arrays.copyOfRange(chunk, 0, 3))
            if (length <= 1) {
                this.primitiveLocationTable.put(i, intArrayOf(0, 0, 0).also { table = it })
            }
            chunk = try {
                Zlib.inflate(Arrays.copyOf(chunk, 5))
            } catch (e: Exception) {
                this.primitiveLocationTable.put(i, intArrayOf(0, 0, 0))
                continue
            }
            chunk = Zlib.deflate(chunk, 9)
            val buffer: ByteBuffer = ByteBuffer.allocate(4 + 1 + chunk.size)
            buffer.put(Binary.writeInt(chunk.size + 1))
            buffer.put(COMPRESSION_ZLIB)
            buffer.put(chunk)
            chunk = buffer.array()
            val sectors = Math.ceil(chunk.size / 4096.0) as Int
            if (sectors > table[1]) {
                table[0] = this.lastSector + 1
                this.lastSector += sectors
                this.primitiveLocationTable.put(i, table)
            }
            raf.seek(table[0].toLong() shl 12L)
            val bytes = ByteArray(sectors shl 12)
            val buffer1: ByteBuffer = ByteBuffer.wrap(bytes)
            buffer1.put(chunk)
            raf.write(buffer1.array())
        }
        writeLocationTable()
        val n = cleanGarbage()
        writeLocationTable()
        return n
    }

    @Override
    @Throws(IOException::class)
    protected fun loadLocationTable() {
        val raf: RandomAccessFile = this.getRandomAccessFile()
        raf.seek(0)
        this.lastSector = 1
        val data = IntArray(1024 * 2) //1024 records * 2 times
        for (i in 0 until 1024 * 2) {
            data[i] = raf.readInt()
        }
        for (i in 0..1023) {
            val index = data[i]
            this.primitiveLocationTable.put(i, intArrayOf(index shr 8, index and 0xff, data[1024 + i]))
            val value: Int = this.primitiveLocationTable.get(i).get(0) + this.primitiveLocationTable.get(i).get(1) - 1
            if (value > this.lastSector) {
                this.lastSector = value
            }
        }
    }

    @Throws(IOException::class)
    private fun writeLocationTable() {
        val raf: RandomAccessFile = this.getRandomAccessFile()
        raf.seek(0)
        for (i in 0..1023) {
            val array: IntArray = this.primitiveLocationTable.get(i)
            raf.writeInt(array[0] shl 8 or array[1])
        }
        for (i in 0..1023) {
            val array: IntArray = this.primitiveLocationTable.get(i)
            raf.writeInt(array[2])
        }
    }

    @Throws(IOException::class)
    private fun cleanGarbage(): Int {
        val raf: RandomAccessFile = this.getRandomAccessFile()
        val sectors: Map<Integer, Integer> = TreeMap()
        for (entry in this.primitiveLocationTable.int2ObjectEntrySet()) {
            val index: Int = entry.getIntKey()
            val data: IntArray = entry.getValue()
            if (data[0] == 0 || data[1] == 0) {
                this.primitiveLocationTable.put(index, intArrayOf(0, 0, 0))
                continue
            }
            sectors.put(data[0], index)
        }
        if (sectors.size() === this.lastSector - 2) {
            return 0
        }
        var shift = 0
        var lastSector = 1
        raf.seek(8192)
        var s = 2
        for (sector in sectors.keySet()) {
            s = sector
            val index: Int = sectors[sector]
            if (sector - lastSector > 1) {
                shift += sector - lastSector - 1
            }
            if (shift > 0) {
                raf.seek(sector.toLong() shl 12L)
                val old = ByteArray(4096)
                raf.readFully(old)
                raf.seek((sector - shift) as Long shl 12L)
                raf.write(old)
            }
            val v: IntArray = this.primitiveLocationTable.get(index)
            v[0] -= shift
            this.primitiveLocationTable.put(index, v)
            lastSector = sector
        }
        raf.setLength(s + 1 shl 12)
        return shift
    }

    @Override
    @Throws(IOException::class)
    protected fun writeLocationIndex(index: Int) {
        val raf: RandomAccessFile = this.getRandomAccessFile()
        val array: IntArray = this.primitiveLocationTable.get(index)
        raf.seek(index shl 2)
        raf.writeInt(array[0] shl 8 or array[1])
        raf.seek(4096 + (index shl 2))
        raf.writeInt(array[2])
    }

    @Override
    @Throws(IOException::class)
    protected fun createBlank() {
        val raf: RandomAccessFile = this.getRandomAccessFile()
        raf.seek(0)
        raf.setLength(0)
        this.lastSector = 1
        val time = (System.currentTimeMillis() / 1000.0) as Int
        for (i in 0..1023) {
            this.primitiveLocationTable.put(i, intArrayOf(0, 0, time))
            raf.writeInt(0)
        }
        for (i in 0..1023) {
            raf.writeInt(time)
        }
    }

    @get:Override
    val x: Int

    @get:Override
    val z: Int

    companion object {
        protected fun getChunkOffset(x: Int, z: Int): Int {
            return x or (z shl 5)
        }
    }
}