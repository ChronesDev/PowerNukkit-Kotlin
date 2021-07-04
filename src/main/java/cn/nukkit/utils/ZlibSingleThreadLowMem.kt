package cn.nukkit.utils

import cn.nukkit.nbt.stream.FastByteArrayOutputStream

class ZlibSingleThreadLowMem : ZlibProvider {
    @Override
    @Synchronized
    @Throws(IOException::class)
    fun deflate(datas: Array<ByteArray?>, level: Int): ByteArray {
        DEFLATER.reset()
        val bos: FastByteArrayOutputStream = ThreadCache.fbaos.get()
        bos.reset()
        for (data in datas) {
            DEFLATER.setInput(data)
            while (!DEFLATER.needsInput()) {
                val i: Int = DEFLATER.deflate(BUFFER)
                bos.write(BUFFER, 0, i)
            }
        }
        DEFLATER.finish()
        while (!DEFLATER.finished()) {
            val i: Int = DEFLATER.deflate(BUFFER)
            bos.write(BUFFER, 0, i)
        }
        //Deflater::end is called the time when the process exits.
        return bos.toByteArray()
    }

    @Override
    @Synchronized
    @Throws(IOException::class)
    override fun deflate(data: ByteArray?, level: Int): ByteArray {
        DEFLATER.reset()
        DEFLATER.setInput(data)
        DEFLATER.finish()
        val bos: FastByteArrayOutputStream = ThreadCache.fbaos.get()
        bos.reset()
        try {
            while (!DEFLATER.finished()) {
                val i: Int = DEFLATER.deflate(BUFFER)
                bos.write(BUFFER, 0, i)
            }
        } finally {
            //deflater.end();
        }
        return bos.toByteArray()
    }

    @Override
    @Synchronized
    @Throws(IOException::class)
    override fun inflate(data: ByteArray?, maxSize: Int): ByteArray {
        INFLATER.reset()
        INFLATER.setInput(data)
        INFLATER.finished()
        val bos: FastByteArrayOutputStream = ThreadCache.fbaos.get()
        bos.reset()
        return try {
            var length = 0
            while (!INFLATER.finished()) {
                val i: Int = INFLATER.inflate(BUFFER)
                length += i
                if (maxSize > 0 && length >= maxSize) {
                    throw IOException("Inflated data exceeds maximum size")
                }
                bos.write(BUFFER, 0, i)
            }
            bos.toByteArray()
        } catch (e: DataFormatException) {
            throw IOException("Unable to inflate zlib stream", e)
        }
    }

    companion object {
        private const val BUFFER_SIZE = 8192
        private val DEFLATER: Deflater = Deflater(Deflater.BEST_COMPRESSION)
        private val INFLATER: Inflater = Inflater()
        private val BUFFER = ByteArray(BUFFER_SIZE)
    }
}