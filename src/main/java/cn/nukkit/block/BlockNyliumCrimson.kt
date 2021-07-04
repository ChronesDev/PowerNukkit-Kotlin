package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockNyliumCrimson @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockNylium() {
    @get:Override
    override val name: String
        get() = "Crimson Nylium"

    @get:Override
    override val id: Int
        get() = CRIMSON_NYLIUM

    @get:Override
    override val color: BlockColor
        get() = BlockColor.CRIMSON_NYLIUM_BLOCK_COLOR
}