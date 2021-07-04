package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

class BlockStonecutter : BlockSolid() {
    @get:Override
    override val id: Int
        get() = STONECUTTER

    @get:Override
    override val name: String
        get() = "Stonecutter"

    @get:Override
    override val hardness: Double
        get() = 3.5

    @get:Override
    override val resistance: Double
        get() = 17.5

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1
}