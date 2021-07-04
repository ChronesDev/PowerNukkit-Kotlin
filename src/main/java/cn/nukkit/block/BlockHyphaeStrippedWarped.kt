package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@Since("1.4.0.0-PN")
@PowerNukkitOnly
class BlockHyphaeStrippedWarped @Since("1.4.0.0-PN") @PowerNukkitOnly constructor(meta: Int) : BlockStemStripped(meta) {
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = STRIPPED_WARPED_HYPHAE

    @get:Override
    override val name: String
        get() = "Warped Stripped Hyphae"

    @get:Override
    override val hardness: Double
        get() = 0.4

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WARPED_HYPHAE_BLOCK_COLOR
}