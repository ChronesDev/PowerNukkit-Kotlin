package cn.nukkit.item

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class ItemNetherSprouts @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Integer?, count: Int) : Item(NETHER_SPROUTS, 0, count, "Nether Sprouts") {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0, 1) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(meta: Integer?) : this(meta, 1) {
    }

    init {
        block = Block.get(BlockID.NETHER_SPROUTS_BLOCK)
    }
}