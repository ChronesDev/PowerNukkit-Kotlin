package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockAncientDebris @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockSolid() {
    @get:Override
    override val id: Int
        get() = ANCIENT_DERBRIS

    @get:Override
    override val name: String
        get() = "Ancient Derbris"

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val toolTier: Int
        get() = ItemTool.TIER_DIAMOND

    @get:Override
    override val resistance: Double
        get() = 1200

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val hardness: Double
        get() = 30

    @get:Override
    override val color: BlockColor
        get() = BlockColor.BLACK_BLOCK_COLOR

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val isLavaResistant: Boolean
        get() = true

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}