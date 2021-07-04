package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockWoodStrippedBirch @PowerNukkitOnly constructor(meta: Int) : BlockWoodStripped(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = STRIPPED_BIRCH_LOG

    @get:Override
    override var woodType: WoodType
        get() = WoodType.BIRCH
        set(woodType) {
            super.woodType = woodType
        }
}