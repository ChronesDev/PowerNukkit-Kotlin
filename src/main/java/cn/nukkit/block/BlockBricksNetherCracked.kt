package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockBricksNetherCracked @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockBricksNether() {
    @get:Override
    override val id: Int
        get() = CRACKED_NETHER_BRICKS

    @get:Override
    override val name: String
        get() = "Cracked Nether Bricks"
}