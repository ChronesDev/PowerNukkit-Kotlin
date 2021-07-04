package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockJungleSignPost : BlockSignPost {
    @PowerNukkitOnly
    constructor() {
    }

    @PowerNukkitOnly
    constructor(meta: Int) : super(meta) {
    }

    @get:Override
    override val id: Int
        get() = JUNGLE_STANDING_SIGN

    @get:Override
    override val wallId: Int
        get() = JUNGLE_WALL_SIGN

    @get:Override
    override val name: String
        get() = "Jungle Sign Post"

    @Override
    override fun toItem(): Item {
        return ItemJungleSign()
    }
}