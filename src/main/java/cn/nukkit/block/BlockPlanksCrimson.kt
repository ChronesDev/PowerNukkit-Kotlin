package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockPlanksCrimson @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockSolid() {
    @get:Override
    override val id: Int
        get() = CRIMSON_PLANKS

    @get:Override
    override val name: String
        get() = "Crimson Planks"

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
        get() = BlockColor.CRIMSON_STEM_BLOCK_COLOR

    @get:Override
    override val burnChance: Int
        get() = 0

    @get:Override
    override val burnAbility: Int
        get() = 0
}