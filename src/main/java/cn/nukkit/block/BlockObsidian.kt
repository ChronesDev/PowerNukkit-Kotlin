package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author xtypr
 * @since 2015/12/2
 */
class BlockObsidian : BlockSolid() {
    @get:Override
    override val name: String
        get() = "Obsidian"

    @get:Override
    override val id: Int
        get() = OBSIDIAN

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_DIAMOND

    //TODO Should be 50 but the break time calculation is broken
    @get:Override
    override val hardness: Double
        get() = 35 //TODO Should be 50 but the break time calculation is broken

    @get:Override
    override val resistance: Double
        get() = 6000

    @Override
    override fun onBreak(item: Item?): Boolean {
        //destroy the nether portal
        val nearby: Array<Block> = arrayOf<Block>(
                this.up(), this.down(),
                this.north(), south(),
                this.west(), this.east())
        for (aNearby in nearby) {
            if (aNearby != null && aNearby.getId() === NETHER_PORTAL) {
                aNearby.onBreak(item)
            }
        }
        return super.onBreak(item)
    }

    @Since("1.2.1.0-PN")
    @PowerNukkitOnly
    @Override
    override fun afterRemoval(newBlock: Block?, update: Boolean) {
        if (update) {
            onBreak(Item.get(BlockID.AIR))
        }
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.OBSIDIAN_BLOCK_COLOR

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
}