package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockStemCrimson @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockStem(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = CRIMSON_STEM

    @get:Override
    override val name: String
        get() = "Crimson Stem"

    @get:Override
    protected override val strippedState: BlockState
        protected get() = getCurrentState().withBlockId(STRIPPED_CRIMSON_STEM)

    @get:Override
    override val color: BlockColor
        get() = BlockColor.CRIMSON_STEM_BLOCK_COLOR
}