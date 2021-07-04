package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author good777LUCKY
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockObsidianCrying @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockSolid() {
    @get:Override
    override val id: Int
        get() = CRYING_OBSIDIAN

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val name: String
        get() = "Crying Obsidian"

    @get:Override
    override val hardness: Double
        get() = 50

    @get:Override
    override val resistance: Double
        get() = 1200

    @get:Override
    override val lightLevel: Int
        get() = 10

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val toolTier: Int
        get() = ItemTool.TIER_DIAMOND

    @Override
    override fun canBePushed(): Boolean {
        return false
    }

    @Override
    override fun canBePulled(): Boolean {
        return false
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.BLACK_BLOCK_COLOR
}