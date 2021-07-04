package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockDoubleSlabBrickBlackstonePolished : BlockDoubleSlabBlackstonePolished {
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
        get() = POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB

    @get:Override
    override val singleSlabId: Int
        get() = POLISHED_BLACKSTONE_BRICK_SLAB

    @get:Override
    override val slabName: String
        get() = "Polished Blackstone Brick"

    @get:Override
    override val hardness: Double
        get() = 2
}