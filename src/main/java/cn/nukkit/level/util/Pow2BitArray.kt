package cn.nukkit.level.util

import cn.nukkit.math.MathHelper

class Pow2BitArray internal constructor(version: BitArrayVersion,
                                        /**
                                         * Number of entries in this palette (**not** the length of the words array that internally backs this palette)
                                         */
                                        private val size: Int, words: IntArray) : BitArray {
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    /**
     * Array used to store data
     */
    @get:Override
    override val words: IntArray

    /**
     * Palette version information
     */
    private override val version: BitArrayVersion

    /**
     * Sets the entry at the given location to the given value
     */
    override operator fun set(index: Int, value: Int) {
        Preconditions.checkElementIndex(index, size)
        Preconditions.checkArgument(value >= 0 && value <= version.maxEntryValue,
                "Max value: %s. Received value", version.maxEntryValue, value)
        val bitIndex: Int = index * version.bits
        val arrayIndex = bitIndex shr 5
        val offset = bitIndex and 31
        words[arrayIndex] = words[arrayIndex] and (version.maxEntryValue shl offset).inv() or (value and version.maxEntryValue) shl offset
    }

    /**
     * Gets the entry at the given index
     */
    override operator fun get(index: Int): Int {
        Preconditions.checkElementIndex(index, size)
        val bitIndex: Int = index * version.bits
        val arrayIndex = bitIndex shr 5
        val wordOffset = bitIndex and 31
        return words[arrayIndex] ushr wordOffset and version.maxEntryValue
    }

    /**
     * Gets the long array that is used to store the data in this BitArray. This is useful for sending packet data.
     */
    override fun size(): Int {
        return size
    }

    fun getVersion(): BitArrayVersion {
        return version
    }

    @Override
    override fun copy(): BitArray {
        return Pow2BitArray(version, size, Arrays.copyOf(words, words.size))
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