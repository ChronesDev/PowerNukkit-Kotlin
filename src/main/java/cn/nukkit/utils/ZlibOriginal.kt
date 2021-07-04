package cn.nukkit.utils

import cn.nukkit.nbt.stream.FastByteArrayOutputStream

class ZlibOriginal : ZlibProvider {
    @Override
    @Throws(IOException::class)
    fun deflate(datas: Array<ByteArray?>, level: Int): ByteArray {
        val deflater = Deflater(level)
        val buffer = ByteArray(1024)
        val bos: FastByteArrayOutputStream = ThreadCache.fbaos.get()
        bos.reset()
        try {
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
            deflater.end()
        }
        return bos.toByteArray()
    }

    @Override
    @Throws(IOException::class)
    override fun deflate(data: ByteArray?, level: Int): ByteArray {
        val deflater = Deflater(level)
        deflater.setInput(data)
        deflater.finish()
        val bos: FastByteArrayOutputStream = ThreadCache.fbaos.get()
        bos.reset()
        val buf = ByteArray(1024)
        try {
            while (!deflater.finished()) {
                val i: Int = deflater.deflate(buf)
                bos.write(buf, 0, i)
            }
        } finally {
            deflater.end()
        }
        return bos.toByteArray()
    }

    @Override
    @Throws(IOException::class)
    override fun inflate(data: ByteArray?, maxSize: Int): ByteArray {
        val inflater = Inflater()
        inflater.setInput(data)
        inflater.finished()
        val bos: FastByteArrayOutputStream = ThreadCache.fbaos.get()
        bos.reset()
        val buffer = ByteArray(1024)
        return try {
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
    }
}