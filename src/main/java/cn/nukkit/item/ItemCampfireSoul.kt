package cn.nukkit.item

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class ItemCampfireSoul @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Integer?, count: Int) : Item(SOUL_CAMPFIRE, meta, count, "Soul Campfire") {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0, 1) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(meta: Integer?) : this(meta, 1) {
    }

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }

    init {
        this.block = BlockCampfireSoul()
    }
}