package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@Since("1.4.0.0-PN")
@PowerNukkitOnly
class BlockHyphaeWarped @Since("1.4.0.0-PN") @PowerNukkitOnly constructor(meta: Int) : BlockStem(meta) {
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = WARPED_HYPHAE

    @get:Override
    override val name: String
        get() = "Warped Hyphae"

    @get:Override
    protected override val strippedState: BlockState
        protected get() = getCurrentState().withBlockId(STRIPPED_WARPED_HYPHAE)

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WARPED_HYPHAE_BLOCK_COLOR
}