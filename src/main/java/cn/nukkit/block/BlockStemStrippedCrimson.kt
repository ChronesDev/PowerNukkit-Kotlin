package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockStemStrippedCrimson @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockStemStripped(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = STRIPPED_CRIMSON_STEM

    @get:Override
    override val name: String
        get() = "Stripped Crimson Stem"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.CRIMSON_STEM_BLOCK_COLOR
}