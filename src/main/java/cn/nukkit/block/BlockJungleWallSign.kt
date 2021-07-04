package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockJungleWallSign @PowerNukkitOnly constructor(meta: Int) : BlockWallSign(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = JUNGLE_WALL_SIGN

    @get:Override
    protected override val postId: Int
        protected get() = JUNGLE_STANDING_SIGN

    @get:Override
    override val name: String
        get() = "Jungle Wall Sign"

    @Override
    override fun toItem(): Item {
        return ItemJungleSign()
    }
}