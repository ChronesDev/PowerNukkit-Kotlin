package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockAcaciaSignPost : BlockSignPost {
    constructor() {}
    constructor(meta: Int) : super(meta) {}

    @get:Override
    override val id: Int
        get() = ACACIA_STANDING_SIGN

    @get:Override
    override val wallId: Int
        get() = ACACIA_WALL_SIGN

    @get:Override
    override val name: String
        get() = "Acacia Sign Post"

    @Override
    override fun toItem(): Item {
        return ItemAcaciaSign()
    }
}