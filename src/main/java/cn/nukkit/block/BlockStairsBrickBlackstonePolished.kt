package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockStairsBrickBlackstonePolished @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockStairsBlackstonePolished(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = POLISHED_BLACKSTONE_BRICK_STAIRS

    @get:Override
    override val name: String
        get() = "Polished Blackstone Brick Stairs"

    @get:Override
    override val hardness: Double
        get() = 1.5
}