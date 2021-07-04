package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockSmoker @PowerNukkitOnly constructor(meta: Int) : BlockSmokerBurning(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val name: String
        get() = "Smoker"

    @get:Override
    override val id: Int
        get() = SMOKER

    @get:Override
    override val lightLevel: Int
        get() = 0
}