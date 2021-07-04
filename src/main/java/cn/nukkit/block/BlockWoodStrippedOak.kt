package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockWoodStrippedOak @PowerNukkitOnly constructor(meta: Int) : BlockWoodStripped(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = STRIPPED_OAK_LOG

    @get:Override
    override var woodType: WoodType
        get() = WoodType.OAK
        set(woodType) {
            super.woodType = woodType
        }
}