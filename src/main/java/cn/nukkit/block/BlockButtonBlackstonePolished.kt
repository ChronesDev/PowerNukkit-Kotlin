package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockButtonBlackstonePolished @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockButtonStone(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = POLISHED_BLACKSTONE_BUTTON

    @get:Override
    override val name: String
        get() = "Polished Blackstone Button"
}