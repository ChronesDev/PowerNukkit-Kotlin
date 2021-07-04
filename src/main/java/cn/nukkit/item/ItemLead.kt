package cn.nukkit.item

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class ItemLead @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Integer?, count: Int) // TODO: Add Functionality
    : Item(LEAD, meta, count, "Lead") {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0, 1) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(meta: Integer?) : this(meta, 1) {
    }
}