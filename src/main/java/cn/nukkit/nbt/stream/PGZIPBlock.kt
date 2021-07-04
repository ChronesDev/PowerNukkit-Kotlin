package cn.nukkit.nbt.stream

import java.util.concurrent.Callable

class PGZIPBlock(parent: PGZIPOutputStream) : Callable<ByteArray?> {
    /**
     * This ThreadLocal avoids the recycling of a lot of memory, causing lumpy performance.
     */
    protected val STATE: ThreadLocal<PGZIPState>

    // private final int index;
    val `in` = ByteArray(SIZE)
    var in_length = 0

    /*
     public Block(@Nonnegative int index) {
     this.index = index;
     }
     */
    // Only on worker thread
    @Override
    @Throws(Exception::class)
    fun call(): ByteArray {
        // LOG.info("Processing " + this + " on " + Thread.currentThread());
        val state: PGZIPState = STATE.get()
        // ByteArrayOutputStream buf = new ByteArrayOutputStream(in.length);   // Overestimate output size required.
        // DeflaterOutputStream def = newDeflaterOutputStream(buf);
        state.def.reset()
        state.buf.reset()
        state.str.write(`in`, 0, in_length)
        state.str.flush()

        // return Arrays.copyOf(in, in_length);
        return state.buf.toByteArray()
    }

    @Override
    override fun toString(): String {
        return "Block" /* + index */ + "(" + in_length + "/" + `in`.size + " bytes)"
    }

    companion object {
        const val SIZE = 64 * 1024
    }

    init {
        STATE = PGZIPThreadLocal(parent)
    }
}