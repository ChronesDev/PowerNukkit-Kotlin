package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockWoodStrippedAcacia @PowerNukkitOnly constructor(meta: Int) : BlockWoodStripped(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = STRIPPED_ACACIA_LOG

    @get:Override
    override var woodType: WoodType
        get() = WoodType.ACACIA
        set(woodType) {
            super.woodType = woodType
        }
}