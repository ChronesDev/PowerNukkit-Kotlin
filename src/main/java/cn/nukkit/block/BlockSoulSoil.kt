package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockSoulSoil @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockSolid() {
    @get:Override
    override val id: Int
        get() = SOUL_SOIL

    @get:Override
    override val name: String
        get() = "Soul Soil"

    @get:Override
    override val hardness: Double
        get() = 1

    @get:Override
    override val resistance: Double
        get() = 1

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_SHOVEL

    @Override
    override fun canHarvestWithHand(): Boolean {
        return true
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.BROWN_BLOCK_COLOR

    @get:Override
    override val isSoulSpeedCompatible: Boolean
        get() = true
}