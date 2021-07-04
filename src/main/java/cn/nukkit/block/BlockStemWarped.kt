package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockStemWarped @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockStem(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = WARPED_STEM

    @get:Override
    override val name: String
        get() = "Warped Stem"

    @get:Override
    protected override val strippedState: BlockState
        protected get() = getCurrentState().withBlockId(STRIPPED_WARPED_STEM)

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WARPED_STEM_BLOCK_COLOR
}