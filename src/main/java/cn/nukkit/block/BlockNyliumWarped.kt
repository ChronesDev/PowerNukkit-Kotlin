package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockNyliumWarped @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockNylium() {
    @get:Override
    override val name: String
        get() = "Warped Nylium"

    @get:Override
    override val id: Int
        get() = WARPED_NYLIUM

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WARPED_NYLIUM_BLOCK_COLOR
}