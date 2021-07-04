package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockNetheriteBlock @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockSolid() {
    @get:Override
    override val id: Int
        get() = NETHERITE_BLOCK

    @get:Override
    override val name: String
        get() = "Netherite Block"

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    // TODO Should be 50, but the break time is glitchy (same with obsidian but less noticeable because of the texture)
    @get:Override
    override val hardness: Double
        get() = 35 // TODO Should be 50, but the break time is glitchy (same with obsidian but less noticeable because of the texture)

    @get:Override
    override val resistance: Double
        get() = 6000

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val toolTier: Int
        get() = ItemTool.TIER_DIAMOND

    @get:Override
    override val color: BlockColor
        get() = BlockColor.BLACK_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val isLavaResistant: Boolean
        get() = true
}