package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockBlackstonePolished @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockBlackstone() {
    @get:Override
    override val name: String
        get() = "Polished Blackstone"

    @get:Override
    override val id: Int
        get() = POLISHED_BLACKSTONE

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val hardness: Double
        get() = 1.5
}