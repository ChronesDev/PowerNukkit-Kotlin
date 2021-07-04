package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockAcaciaWallSign @JvmOverloads constructor(meta: Int = 0) : BlockWallSign(meta) {
    @get:Override
    override val id: Int
        get() = ACACIA_WALL_SIGN

    @get:Override
    protected override val postId: Int
        protected get() = ACACIA_STANDING_SIGN

    @get:Override
    override val name: String
        get() = "Acacia Wall Sign"

    @Override
    override fun toItem(): Item {
        return ItemAcaciaSign()
    }
}