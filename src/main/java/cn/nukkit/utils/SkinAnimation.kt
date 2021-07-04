package cn.nukkit.utils

import cn.nukkit.api.DeprecationDetails

@ToString
class SkinAnimation @Since("1.4.0.0-PN") constructor(image: SerializedImage, type: Int, frames: Float, expression: Int) {
    val image: SerializedImage
    val type: Int
    val frames: Float

    @Since("1.4.0.0-PN")
    val expression: Int

    @PowerNukkitOnly("Re-added for backward-compatibility")
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "The expression field was added and the constructor's signature was changed", replaceWith = "SkinAnimation(SerializedImage image, int type, float frames, int expression)")
    constructor(image: SerializedImage, type: Int, frames: Float) : this(image, type, frames, 0) {
    }

    init {
        this.image = image
        this.type = type
        this.frames = frames
        this.expression = expression
    }
}