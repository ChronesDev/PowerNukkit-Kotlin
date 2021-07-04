package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockShroomlight @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockTransparent() {
    @get:Override
    override val id: Int
        get() = SHROOMLIGHT

    @get:Override
    override val name: String
        get() = "Shroomlight"

    //TODO Should be hoe, fix at https://github.com/PowerNukkit/PowerNukkit/pull/367
    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_HANDS_ONLY //TODO Should be hoe, fix at https://github.com/PowerNukkit/PowerNukkit/pull/367

    @get:Override
    override val resistance: Double
        get() = 1

    @get:Override
    override val hardness: Double
        get() = 1

    @get:Override
    override val lightLevel: Int
        get() = 15

    @get:Override
    override val color: BlockColor
        get() = BlockColor.RED_BLOCK_COLOR
}