package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockPolishedBasalt @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockBasalt(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val name: String
        get() = "Polished Basalt"

    @get:Override
    override val id: Int
        get() = BlockID.POLISHED_BASALT
}