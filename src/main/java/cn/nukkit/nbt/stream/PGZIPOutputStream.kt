package cn.nukkit.nbt.stream

import cn.nukkit.api.PowerNukkitOnly

/**
 * A multi-threaded version of [java.util.zip.GZIPOutputStream].
 *
 * @author shevek
 */
class PGZIPOutputStream(out: OutputStream?, executor: ExecutorService, nthreads: Int) : FilterOutputStream(out) {
    // todo: remove after block guessing is implemented
    // array list that contains the block sizes
    private val blockSizes: IntList = IntArrayList()
    private var level: Int = Deflater.BEST_SPEED
    private var strategy: Int = Deflater.DEFAULT_STRATEGY
    fun newDeflater(): Deflater {
        val def = Deflater(level, true)
        def.setStrategy(strategy)
        return def
    }

    fun setStrategy(strategy: Int) {
        this.strategy = strategy
    }

    fun setLevel(level: Int) {
        this.level = level
    }

    // TODO: Share, daemonize.
    private val executor: ExecutorService
    private val nthreads: Int
    private val crc: CRC32 = CRC32()
    private val emitQueue: BlockingQueue<Future<ByteArray>>
    private var block: PGZIPBlock = PGZIPBlock(this /* 0 */)

    /**
     * Used as a sentinel for 'closed'.
     */
    private var bytesWritten = 0

    /**
     * Creates a PGZIPOutputStream
     * using [PGZIPOutputStream.getSharedThreadPool].
     *
     * @param out the eventual output stream for the compressed data.
     * @throws java.io.IOException if it all goes wrong.
     */
    constructor(out: OutputStream?, nthreads: Int) : this(out, getSharedThreadPool(), nthreads) {}

    /**
     * Creates a PGZIPOutputStream
     * using [PGZIPOutputStream.getSharedThreadPool]
     * and [Runtime.availableProcessors].
     *
     * @param out the eventual output stream for the compressed data.
     * @throws java.io.IOException if it all goes wrong.
     */
    constructor(out: OutputStream?) : this(out, Runtime.getRuntime().availableProcessors()) {}

    /*
     * @see http://www.gzip.org/zlib/rfc-gzip.html#file-format
     */
    @Throws(IOException::class)
    private fun writeHeader() {
        out.write(byteArrayOf(
                GZIP_MAGIC.toByte(),  // ID1: Magic number (little-endian short)
                (GZIP_MAGIC shr 8).toByte(),  // ID2: Magic number (little-endian short)
                Deflater.DEFLATED,  // CM: Compression method
                0,  // FLG: Flags (byte)
                0, 0, 0, 0,  // MTIME: Modification time (int)
                0,  // XFL: Extra flags
                3 // OS: Operating system (3 = Linux)
        ))
    }

    // Master thread only
    @Override
    @Throws(IOException::class)
    fun write(b: Int) {
        val single = ByteArray(1)
        single[0] = (b and 0xFF).toByte()
        write(single)
    }

    // Master thread only
    @Override
    @Throws(IOException::class)
    fun write(b: ByteArray) {
        write(b, 0, b.size)
    }

    // Master thread only
    @Override
    @Throws(IOException::class)
    fun write(b: ByteArray?, off: Int, len: Int) {
        var off = off
        var len = len
        crc.update(b, off, len)
        bytesWritten += len
        while (len > 0) {
            // assert block.in_length < block.in.length
            val capacity: Int = block.`in`.length - block.in_length
            if (len >= capacity) {
                System.arraycopy(b, off, block.`in`, block.in_length, capacity)
                block.in_length += capacity // == block.in.length
                off += capacity
                len -= capacity
                submit()
            } else {
                System.arraycopy(b, off, block.`in`, block.in_length, len)
                block.in_length += len
                // off += len;
                // len = 0;
                break
            }
        }
    }

    // Master thread only
    @Throws(IOException::class)
    private fun submit() {
        emitUntil(nthreads - 1)
        emitQueue.add(executor.submit(block))
        block = PGZIPBlock(this /* block.index + 1 */)
    }

    // Emit If Available - submit always
    // Emit At Least one - submit when executor is full
    // Emit All Remaining - flush(), close()
    // Master thread only
    @Throws(IOException::class, InterruptedException::class, ExecutionException::class)
    private fun tryEmit() {
        while (true) {
            val future: Future<ByteArray> = emitQueue.peek() ?: return
            // LOG.info("Peeked future " + future);
            if (!future.isDone()) return
            // It's an ordered queue. This MUST be the same element as above.
            emitQueue.remove()
            val toWrite: ByteArray = future.get()
            blockSizes.add(toWrite.size) // todo: remove after block guessing is implemented
            out.write(toWrite)
        }
    }
    // Master thread only
    /**
     * Emits any opportunistically available blocks. Furthermore, emits blocks until the number of executing tasks is less than taskCountAllowed.
     */
    @Throws(IOException::class)
    private fun emitUntil(taskCountAllowed: Int) {
        try {
            while (emitQueue.size() > taskCountAllowed) {
                // LOG.info("Waiting for taskCount=" + emitQueue.size() + " -> " + taskCountAllowed);
                val future: Future<ByteArray> = emitQueue.remove() // Valid because emitQueue.size() > 0
                val toWrite: ByteArray = future.get() // Blocks until this task is done.
                blockSizes.add(toWrite.size) // todo: remove after block guessing is implemented
                out.write(toWrite)
            }
            // We may have achieved more opportunistically available blocks
            // while waiting for a block above. Let's emit them here.
            tryEmit()
        } catch (e: ExecutionException) {
            throw IOException(e)
        } catch (e: InterruptedException) {
            throw InterruptedIOException()
        }
    }

    // Master thread only
    @Override
    @Throws(IOException::class)
    fun flush() {
        // LOG.info("Flush: " + block);
        if (block.in_length > 0) submit()
        emitUntil(0)
        super.flush()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Throws(IOException::class)
    fun finish() {
        if (bytesWritten >= 0) {
            flush()
            newDeflaterOutputStream(out, newDeflater()).finish()
            val buf: ByteBuffer = ByteBuffer.allocate(8)
            buf.order(ByteOrder.LITTLE_ENDIAN)
            // LOG.info("CRC is " + crc.getValue());
            buf.putInt(crc.getValue() as Int)
            buf.putInt(bytesWritten)
            out.write(buf.array()) // allocate() guarantees a backing array.
            // LOG.info("trailer is " + Arrays.toString(buf.array()));
        }
    }

    // Master thread only
    @Override
    @Throws(IOException::class)
    fun close() {
        // LOG.info("Closing: bytesWritten=" + bytesWritten);
        if (bytesWritten >= 0) {
            finish()
            out.flush()
            out.close()
            bytesWritten = Integer.MIN_VALUE
            // } else {
            // LOG.warn("Already closed.");
        }
    }

    companion object {
        private val EXECUTOR: ExecutorService = Executors.newCachedThreadPool()
        val sharedThreadPool: ExecutorService
            get() = EXECUTOR

        fun getSharedThreadPool(): ExecutorService {
            return EXECUTOR
        }

        // private static final Logger LOG = LoggerFactory.getLogger(PGZIPOutputStream.class);
        private const val GZIP_MAGIC = 0x8b1f
        fun newDeflaterOutputStream(out: OutputStream?, deflater: Deflater?): DeflaterOutputStream {
            return DeflaterOutputStream(out, deflater, 512, true)
        }
    }

    // Master thread only
    init {
        this.executor = executor
        this.nthreads = nthreads
        emitQueue = ArrayBlockingQueue<Future<ByteArray>>(nthreads)
        writeHeader()
    }
}