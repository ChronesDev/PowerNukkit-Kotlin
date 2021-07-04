package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockWallBrickBlackstonePolished : BlockWallBlackstonePolished {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(meta: Int) : super(meta) {
    }

    @get:Override
    override val id: Int
        get() = POLISHED_BLACKSTONE_BRICK_WALL

    @get:Override
    override val name: String
        get() = "Polished Blackstone Brick Wall"

    @get:Override
    override val hardness: Double
        get() = 1.5
}