package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockWarpedWartBlock @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockSolid() {
    @get:Override
    override val name: String
        get() = "Warped Wart Block"

    @get:Override
    override val id: Int
        get() = WARPED_WART_BLOCK//TODO Correct type is hoe

    // TODO Fix it in https://github.com/PowerNukkit/PowerNukkit/pull/370, the same for BlockNetherWartBlock
    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_HANDS_ONLY //TODO Correct type is hoe

    @get:Override
    override val resistance: Double
        get() = 1

    @get:Override
    override val hardness: Double
        get() = 1

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WARPED_WART_BLOCK_COLOR
}