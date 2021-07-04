package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockCampfireSoul @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockCampfire(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = SOUL_CAMPFIRE_BLOCK

    @get:Override
    override val name: String
        get() = "Soul Campfire"

    @get:Override
    override val lightLevel: Int
        get() = if (isExtinguished()) 0 else 10

    @Override
    override fun toItem(): Item {
        return Item.get(ItemID.SOUL_CAMPFIRE)
    }
}