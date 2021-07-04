package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockBlastFurnace @PowerNukkitOnly constructor(meta: Int) : BlockBlastFurnaceBurning(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val name: String
        get() = "Blast Furnace"

    @get:Override
    override val id: Int
        get() = BLAST_FURNACE

    @get:Override
    override val lightLevel: Int
        get() = 0
}