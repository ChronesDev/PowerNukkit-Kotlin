package cn.nukkit.level.util

import cn.nukkit.math.MathHelper

class PaddedBitArray internal constructor(version: BitArrayVersion,
                                          /**
                                           * Number of entries in this palette (**not** the length of the words array that internally backs this palette)
                                           */
                                          private val size: Int, words: IntArray) : BitArray {
    /**
     * Array used to store data
     */
    @get:Override
    override val words: IntArray

    /**
     * Palette version information
     */
    private override val version: BitArrayVersion

    @Override
    override operator fun set(index: Int, value: Int) {
        Preconditions.checkElementIndex(index, size)
        Preconditions.checkArgument(value >= 0 && value <= version.maxEntryValue,
                "Max value: %s. Received value", version.maxEntryValue, value)
        val arrayIndex: Int = index / version.entriesPerWord
        val offset: Int = index % version.entriesPerWord * version.bits
        words[arrayIndex] = words[arrayIndex] and (version.maxEntryValue shl offset).inv() or (value and version.maxEntryValue) shl offset
    }

    @Override
    override operator fun get(index: Int): Int {
        Preconditions.checkElementIndex(index, size)
        val arrayIndex: Int = index / version.entriesPerWord
        val offset: Int = index % version.entriesPerWord * version.bits
        return words[arrayIndex] ushr offset and version.maxEntryValue
    }

    @Override
    override fun size(): Int {
        return size
    }

    @Override
    fun getVersion(): BitArrayVersion {
        return version
    }

    @Override
    override fun copy(): BitArray {
        return PaddedBitArray(version, size, Arrays.copyOf(words, words.size))
    }

    init {
        this.version = version
        this.words = words
        val expectedWordsLength: Int = MathHelper.ceil(size.toFloat() / version.entriesPerWord)
        if (words.size != expectedWordsLength) {
            throw IllegalArgumentException("Invalid length given for storage, got: " + words.size +
                    " but expected: " + expectedWordsLength)
        }
    }
}