package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockCrimsonWallSign @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockWallSign(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = CRIMSON_WALL_SIGN

    @get:Override
    protected override val postId: Int
        protected get() = CRIMSON_STANDING_SIGN

    @get:Override
    override val name: String
        get() = "Crimson Wall Sign"

    @Override
    override fun toItem(): Item {
        return Item.get(ItemID.CRIMSON_SIGN)
    }

    @get:Override
    override val burnChance: Int
        get() = 0

    @get:Override
    override val burnAbility: Int
        get() = 0
}