package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockPressurePlateCrimson @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockPressurePlateWood(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = CRIMSON_PRESSURE_PLATE

    @get:Override
    override val name: String
        get() = "Crimson Pressure Plate"

    @get:Override
    override val burnChance: Int
        get() = 0

    @get:Override
    override val burnAbility: Int
        get() = 0
}