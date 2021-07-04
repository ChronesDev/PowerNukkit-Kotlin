package cn.nukkit.item

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class ItemWarpedSign @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Integer?, count: Int) : ItemSign(WARPED_SIGN, meta, count, "Warped Sign", BlockWarpedSignPost()) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0, 1) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(meta: Integer?) : this(meta, 1) {
    }
}