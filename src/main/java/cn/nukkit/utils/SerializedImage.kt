package cn.nukkit.utils

import io.netty.util.internal.EmptyArrays

@ToString(exclude = ["data"])
class SerializedImage(val width: Int, val height: Int, val data: ByteArray) {
    companion object {
        val EMPTY = SerializedImage(0, 0, EmptyArrays.EMPTY_BYTES)
        fun fromLegacy(skinData: ByteArray): SerializedImage {
            Objects.requireNonNull(skinData, "skinData")
            when (skinData.size) {
                SINGLE_SKIN_SIZE -> return SerializedImage(64, 32, skinData)
                DOUBLE_SKIN_SIZE -> return SerializedImage(64, 64, skinData)
                SKIN_128_64_SIZE -> return SerializedImage(128, 64, skinData)
                SKIN_128_128_SIZE -> return SerializedImage(128, 128, skinData)
            }
            throw IllegalArgumentException("Unknown legacy skin size")
        }
    }
}