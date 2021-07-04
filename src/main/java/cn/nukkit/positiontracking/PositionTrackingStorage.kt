package cn.nukkit.positiontracking

import cn.nukkit.api.PowerNukkitOnly

/**
 * Stores a sequential range of [PositionTracking] objects in a file. The read operation is cached.
 *
 * This object holds a file handler and must be closed when it is no longer needed.
 *
 * Once closed the instance cannot be reused.
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
class PositionTrackingStorage @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(startIndex: Int, persistenceFile: File, maxStorage: Int) : Closeable {
    private val startIndex: Int
    private val maxStorage = 0
    private var garbagePos: Long = 0
    private var stringHeapPos: Long = 0
    private val persistence: RandomAccessFile?
    private val cache: Cache<Integer, Optional<PositionTracking>> = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).concurrencyLevel(1).build()
    private var nextIndex = 0

    /**
     * Opens or create the file and all directories in the path automatically. The given start index will be used
     * in new files and will be checked when opening files. If the file being opened don't matches this value
     * internally than an `IllegalArgumentException` will be thrown.
     * @param startIndex The number of the first handler. Must be higher than 0 and must match the number of the existing file.
     * @param persistenceFile The file being opened or created. Parent directories will also be created if necessary.
     * @throws IOException If an error has occurred while reading, parsing or creating the file
     * @throws IllegalArgumentException If opening an existing file and the internal startIndex don't match the given startIndex
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(startIndex: Int, persistenceFile: File) : this(startIndex, persistenceFile, 0) {
    }

    private fun getAxisPos(trackingHandler: Int): Long {
        //                    max str cur  on  nam len  x   y   z 
        return HEADER.size + 4 + 4 + 4 + (1 + 8 + 4 + 8 + 8 + 8) * (trackingHandler - startIndex).toLong()
    }

    private fun validateHandler(trackingHandler: Int) {
        Preconditions.checkArgument(trackingHandler >= startIndex, "The trackingHandler {} is too low for this storage (starts at {})", trackingHandler, startIndex)
        val limit = startIndex + maxStorage
        Preconditions.checkArgument(trackingHandler <= limit, "The trackingHandler {} is too high for this storage (ends at {})", trackingHandler, limit)
    }

    /**
     * Retrieves the [PositionTracking] object that is assigned to the given trackingHandler.
     * The handler must be valid for this storage.
     *
     * This call may return a cached result but the returned object can be modified freely.
     * @param trackingHandler A valid handler for this storage
     * @return A clone of the cached result.
     * @throws IOException If an error has occurred while accessing the file
     * @throws IllegalArgumentException If the trackingHandler is not valid for this storage
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    @Throws(IOException::class)
    fun getPosition(trackingHandler: Int): PositionTracking {
        validateHandler(trackingHandler)
        return try {
            cache.get(trackingHandler) { loadPosition(trackingHandler, true) }
                    .map(PositionTracking::clone)
                    .orElse(null)
        } catch (e: ExecutionException) {
            throw handleExecutionException(e)
        }
    }

    /**
     * Retrieves the [PositionTracking] object that is assigned to the given trackingHandler.
     * The handler must be valid for this storage.
     *
     * This call may return a cached result but the returned object can be modified freely.
     * @param trackingHandler A valid handler for this storage
     * @param onlyEnabled When false, disabled positions that wasn't invalidated may be returned.
     * Caching only works when this is set to true
     * @return A clone of the cached result.
     * @throws IOException If an error has occurred while accessing the file
     * @throws IllegalArgumentException If the trackingHandler is not valid for this storage
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    @Throws(IOException::class)
    fun getPosition(trackingHandler: Int, onlyEnabled: Boolean): PositionTracking {
        if (onlyEnabled) {
            return getPosition(trackingHandler)
        }
        validateHandler(trackingHandler)
        return loadPosition(trackingHandler, false).orElse(null)
    }

    /**
     * Attempts to reuse an existing and enabled trackingHandler for the given position, if none is found than a new handler is created
     * if the limit was not exceeded.
     * @param position The position that needs a handler
     * @return The trackingHandler assigned to the position or an empty OptionalInt if none was found and this storage is full
     * @throws IOException If an error occurred while reading or writing the file
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Throws(IOException::class)
    fun addOrReusePosition(position: NamedPosition): OptionalInt {
        val handler: OptionalInt = findTrackingHandler(position)
        return if (handler.isPresent()) {
            handler
        } else addNewPosition(position)
    }

    /**
     * Adds the given position as a new entry in this storage, even if the position is already registered and enabled.
     * @param position The position that needs a handler
     * @return The trackingHandler assigned to the position or an empty OptionalInt if none was found and this storage is full
     * @throws IOException If an error occurred while reading or writing the file
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Synchronized
    @Throws(IOException::class)
    fun addNewPosition(position: NamedPosition): OptionalInt {
        return addNewPosition(position, true)
    }

    /**
     * Adds the given position as a new entry in this storage, even if the position is already registered and enabled.
     * @param position The position that needs a handler
     * @param enabled If the position will be added as enabled or disabled
     * @return The trackingHandler assigned to the position or an empty OptionalInt if none was found and this storage is full
     * @throws IOException If an error occurred while reading or writing the file
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Synchronized
    @Throws(IOException::class)
    fun addNewPosition(position: NamedPosition, enabled: Boolean): OptionalInt {
        val handler: OptionalInt = addNewPos(position, enabled)
        if (!handler.isPresent()) {
            return handler
        }
        if (enabled) {
            cache.put(handler.getAsInt(), Optional.of(PositionTracking(position)))
        }
        return handler
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Throws(IOException::class)
    fun findTrackingHandler(position: NamedPosition): OptionalInt {
        val cached: OptionalInt = cache.asMap().entrySet().stream()
                .filter { e -> e.getValue().filter(position::matchesNamedPosition).isPresent() }
                .mapToInt(Map.Entry::getKey)
                .findFirst()
        if (cached.isPresent()) {
            return cached
        }
        val handlers: IntList = findTrackingHandlers(position, true, 1)
        if (handlers.isEmpty()) {
            return OptionalInt.empty()
        }
        val found: Int = handlers.getInt(0)
        cache.put(found, Optional.of(PositionTracking(position)))
        return OptionalInt.of(found)
    }

    private fun handleExecutionException(e: ExecutionException): IOException {
        val cause: Throwable = e.getCause()
        return if (cause is IOException) {
            cause as IOException
        } else IOException(e)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Synchronized
    @Throws(IOException::class)
    fun invalidateHandler(trackingHandler: Int) {
        validateHandler(trackingHandler)
        invalidatePos(trackingHandler)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Synchronized
    @Throws(IOException::class)
    fun isEnabled(trackingHandler: Int): Boolean {
        validateHandler(trackingHandler)
        persistence.seek(getAxisPos(trackingHandler))
        return persistence.readBoolean()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Synchronized
    @Throws(IOException::class)
    fun setEnabled(trackingHandler: Int, enabled: Boolean): Boolean {
        validateHandler(trackingHandler)
        val pos = getAxisPos(trackingHandler)
        persistence.seek(pos)
        if (persistence.readBoolean() === enabled) {
            return false
        }
        if (persistence.readLong() === 0 && enabled) {
            return false
        }
        persistence.seek(pos)
        persistence.writeBoolean(enabled)
        cache.invalidate(trackingHandler)
        return true
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Synchronized
    @Throws(IOException::class)
    fun hasPosition(trackingHandler: Int): Boolean {
        return hasPosition(trackingHandler, true)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Synchronized
    @Throws(IOException::class)
    fun hasPosition(trackingHandler: Int, onlyEnabled: Boolean): Boolean {
        validateHandler(trackingHandler)
        persistence.seek(getAxisPos(trackingHandler))
        val enabled: Boolean = persistence.readBoolean()
        return if (!enabled && onlyEnabled) {
            false
        } else persistence.readLong() !== 0
    }

    @Synchronized
    @Throws(IOException::class)
    private fun invalidatePos(trackingHandler: Int) {
        val pos = getAxisPos(trackingHandler)
        persistence.seek(pos)
        persistence.writeBoolean(false)
        val buf = ByteArray(8 + 4)
        persistence.readFully(buf)
        val buffer: ByteBuffer = ByteBuffer.wrap(buf)
        val namePos: Long = buffer.getLong()
        val nameLen: Int = buffer.getInt()
        persistence.seek(pos + 1)
        persistence.write(ByteArray(8 + 4))
        cache.put(trackingHandler, Optional.empty())
        addGarbage(namePos, nameLen)
    }

    @Synchronized
    @Throws(IOException::class)
    private fun addGarbage(pos: Long, len: Int) {
        persistence.seek(garbagePos)
        val count: Int = persistence.readInt()
        if (count >= 15) {
            return
        }
        val buf = ByteArray(4 + 8)
        val buffer: ByteBuffer = ByteBuffer.wrap(buf)
        if (count > 0) {
            for (attempt in 0..14) {
                persistence.readFully(buf)
                buffer.rewind()
                val garbage: Long = buffer.getLong()
                val garbageLen: Int = buffer.getInt()
                if (garbage != 0L) {
                    if (garbage + garbageLen == pos) {
                        persistence.seek(persistence.getFilePointer() - 4 - 8)
                        buffer.rewind()
                        buffer.putLong(garbage)
                                .putInt(garbageLen + len)
                        persistence.write(buf)
                        return
                    } else if (pos + len == garbage) {
                        persistence.seek(persistence.getFilePointer() - 4 - 8)
                        buffer.rewind()
                        buffer.putLong(pos)
                                .putInt(garbageLen + len)
                        persistence.write(buf)
                        return
                    }
                }
            }
            persistence.seek(garbagePos + 4)
        }
        for (attempt in 0..14) {
            persistence.readFully(buf)
            buffer.rewind()
            val garbage: Long = buffer.getLong()
            if (garbage == 0L) {
                persistence.seek(persistence.getFilePointer() - 4 - 8)
                buffer.rewind()
                buffer.putLong(pos).putInt(len)
                persistence.write(buf)
                persistence.seek(garbagePos)
                persistence.writeInt(count + 1)
                return
            }
        }
    }

    @Synchronized
    @Throws(IOException::class)
    private fun findSpaceInStringHeap(len: Int): Long {
        persistence.seek(garbagePos)
        val remaining: Int = persistence.readInt()
        if (remaining <= 0) {
            return persistence.length()
        }
        val buf = ByteArray(4 + 8)
        val buffer: ByteBuffer = ByteBuffer.wrap(buf)
        for (attempt in 0..14) {
            persistence.readFully(buf)
            buffer.rewind()
            val garbage: Long = buffer.getLong()
            val garbageLen: Int = buffer.getInt()
            if (garbage >= stringHeapPos && len <= garbageLen) {
                persistence.seek(persistence.getFilePointer() - 4 - 8)
                if (garbageLen == len) {
                    persistence.write(ByteArray(8 + 4))
                    persistence.seek(garbagePos)
                    persistence.writeInt(remaining - 1)
                } else {
                    buffer.rewind()
                    buffer.putLong(garbage + len).putInt(garbageLen - len)
                    persistence.write(buf)
                }
                return garbage
            }
        }
        return persistence.length()
    }

    @Synchronized
    @Throws(IOException::class)
    private fun addNewPos(pos: NamedPosition, enabled: Boolean): OptionalInt {
        if (nextIndex - startIndex >= maxStorage) {
            return OptionalInt.empty()
        }
        val handler = nextIndex++
        writePos(handler, pos, enabled)
        persistence.seek(HEADER.size + 4)
        persistence.writeInt(nextIndex)
        return OptionalInt.of(handler)
    }

    @Synchronized
    @Throws(IOException::class)
    private fun writePos(trackingHandler: Int, pos: NamedPosition, enabled: Boolean) {
        val name: ByteArray = pos.getLevelName().getBytes(StandardCharsets.UTF_8)
        val namePos = addLevelName(name)
        persistence.seek(getAxisPos(trackingHandler))
        persistence.write(ByteBuffer.allocate(1 + 8 + 4 + 8 + 8 + 8)
                .put(if (enabled) 1.toByte() else 0)
                .putLong(namePos)
                .putInt(name.size)
                .putDouble(pos.x)
                .putDouble(pos.y)
                .putDouble(pos.z)
                .array())
    }

    @Synchronized
    @Throws(IOException::class)
    private fun addLevelName(name: ByteArray): Long {
        val pos = findSpaceInStringHeap(name.size)
        persistence.seek(pos)
        persistence.write(name)
        return pos
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Synchronized
    @Throws(IOException::class)
    fun findTrackingHandlers(pos: NamedPosition): IntList {
        return findTrackingHandlers(pos, true)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Synchronized
    @Throws(IOException::class)
    fun findTrackingHandlers(pos: NamedPosition, onlyEnabled: Boolean): IntList {
        return findTrackingHandlers(pos, onlyEnabled, Integer.MAX_VALUE)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Synchronized
    @Throws(IOException::class)
    fun findTrackingHandlers(pos: NamedPosition, onlyEnabled: Boolean, limit: Int): IntList {
        persistence.seek(HEADER.size + 4 + 4 + 4)
        var handler = startIndex - 1
        val lookingX: Double = pos.x
        val lookingY: Double = pos.y
        val lookingZ: Double = pos.z
        val lookingName: ByteArray = pos.getLevelName().getBytes(StandardCharsets.UTF_8)
        val results: IntList = IntArrayList(NukkitMath.clamp(limit, 1, 16))
        val buf = ByteArray(8 + 4 + 8 + 8 + 8)
        val buffer: ByteBuffer = ByteBuffer.wrap(buf)
        while (true) {
            handler++
            if (handler >= nextIndex) {
                return results
            }
            val enabled: Boolean = persistence.readBoolean()
            if (onlyEnabled && !enabled) {
                if (persistence.skipBytes(36) !== 36) throw EOFException()
                continue
            }
            persistence.readFully(buf)
            buffer.rewind()
            val namePos: Long = buffer.getLong()
            val nameLen: Int = buffer.getInt()
            val x: Double = buffer.getDouble()
            val y: Double = buffer.getDouble()
            val z: Double = buffer.getDouble()
            if (namePos > 0 && nameLen > 0 && x == lookingX && y == lookingY && z == lookingZ) {
                val fp: Long = persistence.getFilePointer()
                val nameBytes = ByteArray(nameLen)
                persistence.seek(namePos)
                persistence.readFully(nameBytes)
                if (Arrays.equals(lookingName, nameBytes)) {
                    results.add(handler)
                    if (results.size() >= limit) {
                        return results
                    }
                }
                persistence.seek(fp)
            }
        }
    }

    @Synchronized
    @Throws(IOException::class)
    private fun loadPosition(trackingHandler: Int, onlyEnabled: Boolean): Optional<PositionTracking> {
        if (trackingHandler >= nextIndex) {
            return Optional.empty()
        }
        persistence.seek(getAxisPos(trackingHandler))
        val buf = ByteArray(1 + 8 + 4 + 8 + 8 + 8)
        persistence.readFully(buf)
        val enabled = buf[0] == 1
        if (!enabled && onlyEnabled) {
            return Optional.empty()
        }
        val buffer: ByteBuffer = ByteBuffer.wrap(buf, 1, buf.size - 1)
        val namePos: Long = buffer.getLong()
        if (namePos == 0L) {
            return Optional.empty()
        }
        val nameLen: Int = buffer.getInt()
        val x: Double = buffer.getDouble()
        val y: Double = buffer.getDouble()
        val z: Double = buffer.getDouble()
        val nameBytes = ByteArray(nameLen)
        persistence.seek(namePos)
        persistence.readFully(nameBytes)
        val name = String(nameBytes, StandardCharsets.UTF_8)
        return Optional.of(PositionTracking(name, x, y, z))
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getStartingHandler(): Int {
        return startIndex
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getMaxHandler(): Int {
        return startIndex + maxStorage - 1
    }

    @Override
    @Synchronized
    @Throws(IOException::class)
    fun close() {
        persistence.close()
    }

    @Override
    @Throws(Throwable::class)
    protected fun finalize() {
        if (persistence != null) {
            persistence.close()
        }
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val DEFAULT_MAX_STORAGE = 500
        private val HEADER = byteArrayOf(12, 32, 32, 'P'.toByte(), 'N'.toByte(), 'P'.toByte(), 'T'.toByte(), 'D'.toByte(), 'B'.toByte(), '1'.toByte())
    }

    /**
     * Opens or create the file and all directories in the path automatically. The given start index will be used
     * in new files and will be checked when opening files. If the file being opened don't matches this value
     * internally than an `IllegalArgumentException` will be thrown.
     * @param startIndex The number of the first handler. Must be higher than 0 and must match the number of the existing file.
     * @param persistenceFile The file being opened or created. Parent directories will also be created if necessary.
     * @param maxStorage The maximum amount of positions that this storage may hold. It cannot be changed after creation.
     * Ignored when loading an existing file. When zero or negative, a default value will be used.
     * @throws IOException If an error has occurred while reading, parsing or creating the file
     * @throws IllegalArgumentException If opening an existing file and the internal startIndex don't match the given startIndex
     */
    init {
        var maxStorage = maxStorage
        Preconditions.checkArgument(startIndex > 0, "Start index must be positive. Got {}", startIndex)
        this.startIndex = startIndex
        if (maxStorage <= 0) {
            maxStorage = DEFAULT_MAX_STORAGE
        }
        var created = false
        if (!persistenceFile.isFile()) {
            if (!persistenceFile.getParentFile().isDirectory() && !persistenceFile.getParentFile().mkdirs()) {
                throw FileNotFoundException("Could not create the directory " + persistenceFile.getParent())
            }
            if (!persistenceFile.createNewFile()) {
                throw FileNotFoundException("Could not create the file $persistenceFile")
            }
            created = true
        } else if (persistenceFile.length() === 0) {
            created = true
        }
        persistence = RandomAccessFile(persistenceFile, "rwd")
        try {
            if (created) {
                persistence.write(ByteBuffer.allocate(HEADER.size + 4 + 4 + 4)
                        .put(HEADER)
                        .putInt(maxStorage)
                        .putInt(startIndex)
                        .putInt(startIndex)
                        .array())
                this.maxStorage = maxStorage
                nextIndex = startIndex
            } else {
                val check = ByteArray(HEADER.size)
                var eof: EOFException? = null
                var max: Int
                var next: Int
                var start: Int
                try {
                    persistence.readFully(check)
                    val buf = ByteArray(4 + 4 + 4)
                    persistence.readFully(buf)
                    val buffer: ByteBuffer = ByteBuffer.wrap(buf)
                    max = buffer.getInt()
                    next = buffer.getInt()
                    start = buffer.getInt()
                } catch (e: EOFException) {
                    eof = e
                    max = 0
                    next = 0
                    start = 0
                }
                if (eof != null || max <= 0 || next <= 0 || start <= 0 || !Arrays.equals(check, HEADER)) {
                    throw IOException("The file $persistenceFile is not a valid PowerNukkit TrackingPositionDB persistence file.", eof)
                }
                if (start != startIndex) {
                    throw IllegalArgumentException("The start index $startIndex was given but the file $persistenceFile has start index $start")
                }
                maxStorage = max
                this.maxStorage = maxStorage
                nextIndex = next
            }
            garbagePos = getAxisPos(startIndex + maxStorage)

            //                          cnt  off len  max
            stringHeapPos = garbagePos + 4 + (8 + 4) * 15
            if (created) {
                persistence.seek(stringHeapPos - 1)
                persistence.writeByte(0)
            }
        } catch (e: Throwable) {
            try {
                persistence.close()
            } catch (e2: Throwable) {
                e.addSuppressed(e2)
            }
            throw e
        }
    }
}