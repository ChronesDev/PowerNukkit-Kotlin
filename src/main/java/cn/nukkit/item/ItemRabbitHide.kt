package cn.nukkit.item

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class ItemRabbitHide @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Integer?, count: Int) : Item(RABBIT_HIDE, meta, count, "Rabbit Hide") {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0, 1) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(meta: Integer?) : this(meta, 1) {
    }
}