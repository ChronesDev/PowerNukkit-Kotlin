package cn.nukkit.level.format.anvil.palette

import cn.nukkit.math.MathHelper

@Deprecated
class BiomePalette {
    private var biome = 0
    private var encodedData: BitArray256? = null
    private var palette: IntPalette? = null

    private constructor(clone: BiomePalette) {
        biome = clone.biome
        if (clone.encodedData != null) {
            encodedData = clone.encodedData.clone()
            palette = clone.palette!!.clone()
        }
    }

    constructor(biomeColors: IntArray) {
        for (i in 0..255) {
            set(i, biomeColors[i])
        }
    }

    constructor() {
        biome = Integer.MIN_VALUE
    }

    operator fun get(x: Int, z: Int): Int {
        return get(getIndex(x, z))
    }

    @Synchronized
    operator fun get(index: Int): Int {
        return if (encodedData == null) biome else palette!!.getKey(encodedData.getAt(index))
    }

    operator fun set(x: Int, z: Int, value: Int) {
        set(getIndex(x, z), value)
    }

    @Synchronized
    operator fun set(index: Int, value: Int) {
        if (encodedData == null) {
            if (value == biome) return
            if (biome == Integer.MIN_VALUE) {
                biome = value
                return
            }
            synchronized(this) {
                palette = IntPalette()
                palette.add(biome)
                palette.add(value)
                encodedData = BitArray256(1)
                if (value < biome) {
                    Arrays.fill(encodedData.data, -1)
                    encodedData.setAt(index, 0)
                } else {
                    encodedData.setAt(index, 1)
                }
                return
            }
        }
        val encodedValue: Int = palette!!.getValue(value)
        if (encodedValue != Integer.MIN_VALUE) {
            encodedData!!.setAt(index, encodedValue)
        } else {
            synchronized(this) {
                val raw: IntArray = encodedData!!.toRaw(ThreadCache.intCache256.get())

                // TODO skip remapping of raw data and use grow instead if `remap`
                // boolean remap = value < palette.getValue(palette.length() - 1);
                for (i in 0..255) {
                    raw[i] = palette!!.getKey(raw[i])
                }
                val oldRaw = raw[4]
                raw[index] = value
                palette!!.add(value)
                val oldBits: Int = MathHelper.log2(palette!!.length() - 2)
                val newBits: Int = MathHelper.log2(palette!!.length() - 1)
                if (oldBits != newBits) {
                    encodedData = BitArray256(newBits)
                }
                for (i in raw.indices) {
                    raw[i] = palette!!.getValue(raw[i])
                }
                encodedData!!.fromRaw(raw)
            }
        }
    }

    @Synchronized
    fun toRaw(): IntArray {
        var buffer: IntArray = ThreadCache.intCache256.get()
        if (encodedData == null) {
            Arrays.fill(buffer, biome)
        } else {
            synchronized(this) {
                buffer = encodedData.toRaw(buffer)
                for (i in 0..255) {
                    buffer[i] = palette!!.getKey(buffer[i])
                }
            }
        }
        return buffer
    }

    fun getIndex(x: Int, z: Int): Int {
        return z shl 4 or x
    }

    @Synchronized
    fun clone(): BiomePalette {
        return BiomePalette(this)
    }
}