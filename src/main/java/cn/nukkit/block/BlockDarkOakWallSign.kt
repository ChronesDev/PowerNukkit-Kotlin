package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockDarkOakWallSign @PowerNukkitOnly constructor(meta: Int) : BlockWallSign(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = DARKOAK_WALL_SIGN

    @get:Override
    protected override val postId: Int
        protected get() = DARKOAK_STANDING_SIGN

    @get:Override
    override val name: String
        get() = "Dark Oak Wall Sign"

    @Override
    override fun toItem(): Item {
        return ItemDarkOakSign()
    }
}