package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockSlabBrickBlackstonePolished : BlockSlabBlackstonePolished {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(meta: Int) : super(meta, POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected constructor(meta: Int, doubleSlab: Int) : super(meta, doubleSlab) {
    }

    @get:Override
    override val id: Int
        get() = POLISHED_BLACKSTONE_BRICK_SLAB

    @get:Override
    override val slabName: String
        get() = "Polished Blackstone Brick"

    @get:Override
    override val hardness: Double
        get() = 2
}