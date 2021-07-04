package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockWoodStrippedDarkOak @PowerNukkitOnly constructor(meta: Int) : BlockWoodStripped(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = STRIPPED_DARK_OAK_LOG

    @get:Override
    override var woodType: WoodType
        get() = WoodType.DARK_OAK
        set(woodType) {
            super.woodType = woodType
        }
}