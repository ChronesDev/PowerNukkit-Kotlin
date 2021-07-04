package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockDoorWarped @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockDoorWood(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val name: String
        get() = "Warped Door Block"

    @get:Override
    override val id: Int
        get() = WARPED_DOOR_BLOCK

    @Override
    override fun toItem(): Item {
        return Item.get(ItemID.WARPED_DOOR)
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.CYAN_BLOCK_COLOR

    @get:Override
    override val burnChance: Int
        get() = 0

    @get:Override
    override val burnAbility: Int
        get() = 0
}