package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockSpruceSignPost : BlockSignPost {
    @PowerNukkitOnly
    constructor() {
    }

    @PowerNukkitOnly
    constructor(meta: Int) : super(meta) {
    }

    @get:Override
    override val id: Int
        get() = SPRUCE_STANDING_SIGN

    @get:Override
    override val wallId: Int
        get() = SPRUCE_WALL_SIGN

    @get:Override
    override val name: String
        get() = "Spruce Sign Post"

    @Override
    override fun toItem(): Item {
        return ItemSpruceSign()
    }
}