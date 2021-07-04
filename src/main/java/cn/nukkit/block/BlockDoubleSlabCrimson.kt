package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockDoubleSlabCrimson @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockDoubleSlabBase(0) {
    @get:Override
    override val id: Int
        get() = CRIMSON_DOUBLE_SLAB

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = BlockSlab.SIMPLE_SLAB_PROPERTIES

    @get:Override
    override val slabName: String
        get() = "Crimson"

    @get:Override
    override val singleSlabId: Int
        get() = CRIMSON_SLAB

    //TODO Adjust or remove this when merging https://github.com/PowerNukkit/PowerNukkit/pull/370
    @Override
    protected override fun isCorrectTool(item: Item?): Boolean {
        return true
    }

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val resistance: Double
        get() = 3

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

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