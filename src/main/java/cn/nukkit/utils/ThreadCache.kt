package cn.nukkit.utils

import cn.nukkit.nbt.stream.FastByteArrayOutputStream

object ThreadCache {
    fun clean() {
        idArray.clean()
        dataArray.clean()
        byteCache6144.clean()
        boolCache4096.clean()
        charCache4096.clean()
        charCache4096v2.clean()
        fbaos.clean()
        binaryStream.clean()
        intCache256.clean()
        byteCache256.clean()
    }

    val idArray: IterableThreadLocal<Array<ByteArray>> = object : IterableThreadLocal<Array<ByteArray?>?>() {
        @Override
        override fun init(): Array<ByteArray?> {
            return arrayOfNulls(16)
        }
    }
    val dataArray: IterableThreadLocal<Array<ByteArray>> = object : IterableThreadLocal<Array<ByteArray?>?>() {
        @Override
        override fun init(): Array<ByteArray?> {
            return arrayOfNulls(16)
        }
    }
    val byteCache6144: IterableThreadLocal<ByteArray> = object : IterableThreadLocal<ByteArray?>() {
        @Override
        override fun init(): ByteArray {
            return ByteArray(6144)
        }
    }
    val byteCache256: IterableThreadLocal<ByteArray> = object : IterableThreadLocal<ByteArray?>() {
        @Override
        override fun init(): ByteArray {
            return ByteArray(256)
        }
    }
    val boolCache4096: IterableThreadLocal<BooleanArray> = object : IterableThreadLocal<BooleanArray?>() {
        @Override
        override fun init(): BooleanArray {
            return BooleanArray(4096)
        }
    }
    val charCache4096v2: IterableThreadLocal<CharArray> = object : IterableThreadLocal<CharArray?>() {
        @Override
        override fun init(): CharArray {
            return CharArray(4096)
        }
    }
    val charCache4096: IterableThreadLocal<CharArray> = object : IterableThreadLocal<CharArray?>() {
        @Override
        override fun init(): CharArray {
            return CharArray(4096)
        }
    }
    val intCache256: IterableThreadLocal<IntArray> = object : IterableThreadLocal<IntArray?>() {
        @Override
        override fun init(): IntArray {
            return IntArray(256)
        }
    }
    val fbaos: IterableThreadLocal<FastByteArrayOutputStream> = object : IterableThreadLocal<FastByteArrayOutputStream?>() {
        @Override
        override fun init(): FastByteArrayOutputStream {
            return FastByteArrayOutputStream(1024)
        }
    }
    val binaryStream: IterableThreadLocal<BinaryStream> = object : IterableThreadLocal<BinaryStream?>() {
        @Override
        override fun init(): BinaryStream {
            return BinaryStream()
        }
    }
}