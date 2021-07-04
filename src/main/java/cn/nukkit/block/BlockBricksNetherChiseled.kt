package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockBricksNetherChiseled @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockBricksNether() {
    @get:Override
    override val id: Int
        get() = CHISELED_NETHER_BRICKS

    @get:Override
    override val name: String
        get() = "Chiseled Nether Bricks"
}