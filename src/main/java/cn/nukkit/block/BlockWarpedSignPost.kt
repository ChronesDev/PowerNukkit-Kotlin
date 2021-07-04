package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockWarpedSignPost : BlockSignPost {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(meta: Int) : super(meta) {
    }

    @get:Override
    override val id: Int
        get() = WARPED_STANDING_SIGN

    @get:Override
    override val wallId: Int
        get() = WARPED_WALL_SIGN

    @get:Override
    override val name: String
        get() = "Warped Sign Post"

    @Override
    override fun toItem(): Item {
        return ItemWarpedSign()
    }

    @get:Override
    override val burnChance: Int
        get() = 0

    @get:Override
    override val burnAbility: Int
        get() = 0
}