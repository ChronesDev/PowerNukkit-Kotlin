package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author CreeperFace
 * @since 27. 11. 2016
 */
class BlockButtonStone @JvmOverloads constructor(meta: Int = 0) : BlockButton(meta) {
    @get:Override
    override val id: Int
        get() = STONE_BUTTON

    @get:Override
    override val name: String
        get() = "Stone Button"

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will return false")
    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Requires wooden pickaxe to drop item")
    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return super.getDrops(item)
    }
}