package cn.nukkit.utils

import cn.nukkit.nbt.stream.FastByteArrayOutputStream

class ZlibThreadLocal : ZlibProvider {
    @Override
    @Throws(IOException::class)
    fun deflate(datas: Array<ByteArray?>, level: Int): ByteArray {
        val deflater: Deflater = DEFLATER.get()
        val bos: FastByteArrayOutputStream = ThreadCache.fbaos.get()
        try {
            deflater.reset()
            deflater.setLevel(level)
            bos.reset()
            val buffer: ByteArray = BUFFER.get()
            for (data in datas) {
                deflater.setInput(data)
                while (!deflater.needsInput()) {
                    val i: Int = deflater.deflate(buffer)
                    bos.write(buffer, 0, i)
                }
            }
            deflater.finish()
            while (!deflater.finished()) {
                val i: Int = deflater.deflate(buffer)
                bos.write(buffer, 0, i)
            }
        } finally {
            // https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6293787
            deflater.reset()
        }
        //Deflater::end is called the time when the process exits.
        return bos.toByteArray()
    }

    @Override
    @Throws(IOException::class)
    override fun deflate(data: ByteArray?, level: Int): ByteArray {
        val deflater: Deflater = DEFLATER.get()
        val bos: FastByteArrayOutputStream = ThreadCache.fbaos.get()
        try {
            deflater.reset()
            deflater.setLevel(level)
            deflater.setInput(data)
            deflater.finish()
            bos.reset()
            val buffer: ByteArray = BUFFER.get()
            while (!deflater.finished()) {
                val i: Int = deflater.deflate(buffer)
                bos.write(buffer, 0, i)
            }
        } finally {
            deflater.reset()
        }
        //Deflater::end is called the time when the process exits.
        return bos.toByteArray()
    }

    @Override
    @Throws(IOException::class)
    override fun inflate(data: ByteArray?, maxSize: Int): ByteArray {
        val inflater: Inflater = INFLATER.get()
        return try {
            inflater.reset()
            inflater.setInput(data)
            inflater.finished()
            val bos: FastByteArrayOutputStream = ThreadCache.fbaos.get()
            bos.reset()
            val buffer: ByteArray = BUFFER.get()
            try {
                var length = 0
                while (!inflater.finished()) {
                    val i: Int = inflater.inflate(buffer)
                    length += i
                    if (maxSize > 0 && length >= maxSize) {
                        throw IOException("Inflated data exceeds maximum size")
                    }
                    bos.write(buffer, 0, i)
                }
                bos.toByteArray()
            } catch (e: DataFormatException) {
                throw IOException("Unable to inflate zlib stream", e)
            }
        } finally {
            inflater.reset()
        }
    }

    companion object {
        private val INFLATER: ThreadLocal<Inflater> = ThreadLocal.withInitial { Inflater() }
        private val DEFLATER: ThreadLocal<Deflater> = ThreadLocal.withInitial { Deflater() }
        private val BUFFER: ThreadLocal<ByteArray> = ThreadLocal.withInitial { ByteArray(8192) }
    }
}