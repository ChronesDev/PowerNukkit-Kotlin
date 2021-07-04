package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockBlackstonePolishedChiseled @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockBlackstonePolished() {
    @get:Override
    override val id: Int
        get() = CHISELED_POLISHED_BLACKSTONE

    @get:Override
    override val name: String
        get() = "Chiseled Polished Blackstone"

    @get:Override
    override val hardness: Double
        get() = 1.5
}