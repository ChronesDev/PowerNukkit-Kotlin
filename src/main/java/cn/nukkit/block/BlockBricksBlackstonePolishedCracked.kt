package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockBricksBlackstonePolishedCracked @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockBricksBlackstonePolished() {
    @get:Override
    override val id: Int
        get() = CRACKED_POLISHED_BLACKSTONE_BRICKS

    @get:Override
    override val name: String
        get() = "Cracked Polished Blackstone Bricks"
}