package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockCrimsonSignPost : BlockSignPost {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() {
        // Does nothing
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(meta: Int) : super(meta) {
    }

    @get:Override
    override val id: Int
        get() = CRIMSON_STANDING_SIGN

    @get:Override
    override val wallId: Int
        get() = CRIMSON_WALL_SIGN

    @get:Override
    override val name: String
        get() = "Crimson Sign Post"

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