package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockBirchSignPost : BlockSignPost {
    @PowerNukkitOnly
    constructor() {
    }

    @PowerNukkitOnly
    constructor(meta: Int) : super(meta) {
    }

    @get:Override
    override val id: Int
        get() = BIRCH_STANDING_SIGN

    @get:Override
    override val wallId: Int
        get() = BIRCH_WALL_SIGN

    @get:Override
    override val name: String
        get() = "Birch Sign Post"

    @Override
    override fun toItem(): Item {
        return ItemBirchSign()
    }
}