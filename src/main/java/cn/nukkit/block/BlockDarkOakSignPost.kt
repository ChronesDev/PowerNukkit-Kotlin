package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockDarkOakSignPost : BlockSignPost {
    @PowerNukkitOnly
    constructor() {
    }

    @PowerNukkitOnly
    constructor(meta: Int) : super(meta) {
    }

    @get:Override
    override val id: Int
        get() = DARKOAK_STANDING_SIGN

    @get:Override
    override val wallId: Int
        get() = DARKOAK_WALL_SIGN

    @get:Override
    override val name: String
        get() = "Dark Oak Sign Post"

    @Override
    override fun toItem(): Item {
        return ItemDarkOakSign()
    }
}