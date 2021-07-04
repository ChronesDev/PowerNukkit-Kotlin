package cn.nukkit.level.util

import cn.nukkit.level.GlobalBlockPalette

class PalettedBlockStorage {
    private val palette: IntList
    private var bitArray: BitArray

    @JvmOverloads
    constructor(version: BitArrayVersion = BitArrayVersion.V2) {
        bitArray = version.createPalette(SIZE)
        palette = IntArrayList(16)
        palette.add(GlobalBlockPalette.getOrCreateRuntimeId(0)) // Air is at the start of every palette.
    }

    private constructor(bitArray: BitArray, palette: IntList) {
        this.palette = palette
        this.bitArray = bitArray
    }

    private fun getPaletteHeader(version: BitArrayVersion, runtime: Boolean): Int {
        return version.getId() shl 1 or if (runtime) 1 else 0
    }

    fun setBlock(index: Int, runtimeId: Int) {
        try {
            val id = idFor(runtimeId)
            bitArray.set(index, id)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Unable to set block runtime ID: $runtimeId, palette: $palette", e)
        }
    }

    fun writeTo(stream: BinaryStream) {
        stream.putByte(getPaletteHeader(bitArray.getVersion(), true).toByte())
        for (word in bitArray.getWords()) {
            stream.putLInt(word)
        }
        stream.putVarInt(palette.size())
        palette.forEach(stream::putVarInt as IntConsumer?)
    }

    private fun onResize(version: BitArrayVersion) {
        val newBitArray: BitArray = version.createPalette(SIZE)
        for (i in 0 until SIZE) {
            newBitArray.set(i, bitArray.get(i))
        }
        bitArray = newBitArray
    }

    private fun idFor(runtimeId: Int): Int {
        var index: Int = palette.indexOf(runtimeId)
        if (index != -1) {
            return index
        }
        index = palette.size()
        val version: BitArrayVersion = bitArray.getVersion()
        if (index > version.getMaxEntryValue()) {
            val next: BitArrayVersion = version.next()
            if (next != null) {
                onResize(next)
            }
        }
        palette.add(runtimeId)
        return index
    }

    val isEmpty: Boolean
        get() {
            if (palette.size() === 1) {
                return true
            }
            for (word in bitArray.getWords()) {
                if (Integer.toUnsignedLong(word) !== 0L) {
                    return false
                }
            }
            return true
        }

    fun copy(): PalettedBlockStorage {
        return PalettedBlockStorage(bitArray.copy(), IntArrayList(palette))
    }

    companion object {
        private const val SIZE = 4096
    }
}