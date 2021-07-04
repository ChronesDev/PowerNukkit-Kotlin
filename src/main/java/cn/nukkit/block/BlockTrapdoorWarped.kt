package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockTrapdoorWarped @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockTrapdoor(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = WARPED_TRAPDOOR

    @get:Override
    override val name: String
        get() = "Warped Trapdoor"

    @get:Override
    override val burnChance: Int
        get() = 0

    @get:Override
    override val burnAbility: Int
        get() = 0

    @get:Override
    override val color: BlockColor
        get() = BlockColor.CYAN_BLOCK_COLOR
}