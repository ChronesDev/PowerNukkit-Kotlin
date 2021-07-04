package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockRootsWarped @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockRoots() {
    @get:Override
    override val id: Int
        get() = WARPED_ROOTS

    @get:Override
    override val name: String
        get() = "Warped Roots"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.CYAN_BLOCK_COLOR
}