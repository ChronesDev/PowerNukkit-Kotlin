package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockSoulTorch(meta: Int) : BlockTorch(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val name: String
        get() = "Soul Torch"

    @get:Override
    override val id: Int
        get() = SOUL_TORCH

    @get:Override
    override val lightLevel: Int
        get() = 10
}