package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockSoulLantern @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockLantern(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = SOUL_LANTERN

    @get:Override
    override val name: String
        get() = "Soul Lantern"

    @get:Override
    override val lightLevel: Int
        get() = 10
}