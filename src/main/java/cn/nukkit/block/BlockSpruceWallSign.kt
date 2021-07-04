package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockSpruceWallSign @PowerNukkitOnly constructor(meta: Int) : BlockWallSign(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = SPRUCE_WALL_SIGN

    @get:Override
    protected override val postId: Int
        protected get() = SPRUCE_STANDING_SIGN

    @get:Override
    override val name: String
        get() = "Spruce Wall Sign"

    @Override
    override fun toItem(): Item {
        return ItemSpruceSign()
    }
}