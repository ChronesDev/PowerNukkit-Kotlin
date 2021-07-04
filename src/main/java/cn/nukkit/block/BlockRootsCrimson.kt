package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockRootsCrimson @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockRoots() {
    @get:Override
    override val id: Int
        get() = CRIMSON_ROOTS

    @get:Override
    override val name: String
        get() = "Crimson Roots"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.NETHERRACK_BLOCK_COLOR
}