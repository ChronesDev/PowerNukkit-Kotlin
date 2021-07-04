package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockBeeNest @PowerNukkitOnly protected constructor(meta: Int) : BlockBeehive(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = BEE_NEST

    @get:Override
    override val name: String
        get() = "Bee Nest"

    @get:Override
    override val burnChance: Int
        get() = 30

    @get:Override
    override val burnAbility: Int
        get() = 60

    @get:Override
    override val hardness: Double
        get() = 0.3

    @get:Override
    override val resistance: Double
        get() = 1.5

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return Item.EMPTY_ARRAY
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.YELLOW_BLOCK_COLOR
}