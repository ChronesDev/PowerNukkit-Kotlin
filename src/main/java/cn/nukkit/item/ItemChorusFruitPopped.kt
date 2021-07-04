package cn.nukkit.item

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class ItemChorusFruitPopped(meta: Integer?, count: Int) : Item(POPPED_CHORUS_FRUIT, meta, count, "Popped Chorus Fruit") {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0, 1) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(meta: Integer?) : this(meta, 1) {
    }
}