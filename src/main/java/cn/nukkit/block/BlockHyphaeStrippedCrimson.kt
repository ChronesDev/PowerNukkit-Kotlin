package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@Since("1.4.0.0-PN")
@PowerNukkitOnly
class BlockHyphaeStrippedCrimson @Since("1.4.0.0-PN") @PowerNukkitOnly constructor(meta: Int) : BlockStemStripped(meta) {
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = STRIPPED_CRIMSON_HYPHAE

    @get:Override
    override val name: String
        get() = "Crimson Stripped Hyphae"

    @get:Override
    override val hardness: Double
        get() = 0.4

    @get:Override
    override val color: BlockColor
        get() = BlockColor.CRIMSON_HYPHAE_BLOCK_COLOR
}