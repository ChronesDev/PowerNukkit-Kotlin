package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockStemStrippedWarped @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockStemStripped(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = STRIPPED_WARPED_STEM

    @get:Override
    override val name: String
        get() = "Stripped Warped Stem"

    @get:Override
    override val burnChance: Int
        get() = 0

    @get:Override
    override val burnAbility: Int
        get() = 0

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WARPED_STEM_BLOCK_COLOR
}