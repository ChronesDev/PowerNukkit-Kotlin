package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockBirchWallSign @PowerNukkitOnly constructor(meta: Int) : BlockWallSign(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = BIRCH_WALL_SIGN

    @get:Override
    protected override val postId: Int
        protected get() = BIRCH_STANDING_SIGN

    @get:Override
    override val name: String
        get() = "Birch Wall Sign"

    @Override
    override fun toItem(): Item {
        return ItemBirchSign()
    }
}