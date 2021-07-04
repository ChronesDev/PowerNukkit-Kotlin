package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockButtonCrimson @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockButtonWooden(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = CRIMSON_BUTTON

    @get:Override
    override val name: String
        get() = "Crimson Button"

    @get:Override
    override val burnChance: Int
        get() = 0

    @get:Override
    override val burnAbility: Int
        get() = 0
}