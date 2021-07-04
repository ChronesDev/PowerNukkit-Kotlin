package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockBricksBlackstonePolished @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockBlackstonePolished() {
    @get:Override
    override val id: Int
        get() = POLISHED_BLACKSTONE_BRICKS

    @get:Override
    override val name: String
        get() = "Polished Blackstone Bricks"

    @get:Override
    override val hardness: Double
        get() = 1.5
}