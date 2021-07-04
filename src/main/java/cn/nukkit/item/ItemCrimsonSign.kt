package cn.nukkit.item

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class ItemCrimsonSign @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Integer?, count: Int) : ItemSign(CRIMSON_SIGN, meta, count, "Crimson Sign", BlockCrimsonSignPost()) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0, 1) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(meta: Integer?) : this(meta, 1) {
    }
}