package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockStairsWarped @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockStairsWood(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = WARPED_STAIRS

    @get:Override
    override val name: String
        get() = "Warped Wood Stairs"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.CYAN_BLOCK_COLOR

    @get:Override
    override val burnChance: Int
        get() = 0

    @get:Override
    override val burnAbility: Int
        get() = 0
}