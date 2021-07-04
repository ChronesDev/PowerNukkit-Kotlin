package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockWoodStrippedJungle @PowerNukkitOnly constructor(meta: Int) : BlockWoodStripped(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = STRIPPED_JUNGLE_LOG

    @get:Override
    override var woodType: WoodType
        get() = WoodType.JUNGLE
        set(woodType) {
            super.woodType = woodType
        }
}