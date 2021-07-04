package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockDoorCrimson @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockDoorWood(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val name: String
        get() = "Crimson Door Block"

    @get:Override
    override val id: Int
        get() = CRIMSON_DOOR_BLOCK

    @Override
    override fun toItem(): Item {
        return Item.get(ItemID.CRIMSON_DOOR)
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.NETHERRACK_BLOCK_COLOR

    @get:Override
    override val burnChance: Int
        get() = 0

    @get:Override
    override val burnAbility: Int
        get() = 0
}