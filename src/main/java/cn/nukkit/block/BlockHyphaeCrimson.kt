package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@Since("1.4.0.0-PN")
@PowerNukkitOnly
class BlockHyphaeCrimson @Since("1.4.0.0-PN") @PowerNukkitOnly constructor(meta: Int) : BlockStem(meta) {
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = CRIMSON_HYPHAE

    @get:Override
    override val name: String
        get() = "Crimson Hyphae"

    @get:Override
    protected override val strippedState: BlockState
        protected get() = getCurrentState().withBlockId(STRIPPED_CRIMSON_HYPHAE)

    @get:Override
    override val hardness: Double
        get() = 0.3

    @get:Override
    override val color: BlockColor
        get() = BlockColor.CRIMSON_HYPHAE_BLOCK_COLOR
}