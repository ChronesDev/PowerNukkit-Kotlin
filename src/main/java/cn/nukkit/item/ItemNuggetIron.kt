package cn.nukkit.item

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author good777LUCKY
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class ItemNuggetIron @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Integer?, count: Int) : Item(IRON_NUGGET, meta, count, "Iron Nugget") {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0, 1) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(meta: Integer?) : this(meta, 1) {
    }
}