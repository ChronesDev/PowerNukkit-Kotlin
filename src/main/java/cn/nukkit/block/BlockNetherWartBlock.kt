package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockNetherWartBlock @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockSolid() {
    @get:Override
    override val name: String
        get() = "Nether Wart Block"

    @get:Override
    override val id: Int
        get() = BLOCK_NETHER_WART_BLOCK

    @get:Override
    override val resistance: Double
        get() = 5

    @get:Override
    override val hardness: Double
        get() = 1//TODO Correct type is hoe

    // TODO Fix it in https://github.com/PowerNukkit/PowerNukkit/pull/370, the same for BlockNetherWartBlock
    @get:Override
    @get:PowerNukkitDifference(info = "It's now hoe instead of none", since = "1.4.0.0-PN")
    override val toolType: Int
        get() = ItemTool.TYPE_HANDS_ONLY //TODO Correct type is hoe

    @get:Override
    override val color: BlockColor
        get() = BlockColor.RED_BLOCK_COLOR
}