package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockIcePacked : BlockIce() {
    @get:Override
    override val id: Int
        get() = PACKED_ICE

    @get:Override
    override val name: String
        get() = "Packed Ice"

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @Override
    override fun onUpdate(type: Int): Int {
        return 0 //not being melted
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return true
    }

    @Override
    override fun onBreak(item: Item?): Boolean {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true) //no water
        return true
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @get:Override
    @get:PowerNukkitDifference(since = "1.4.0.0-PN", info = "Returns false ")
    override val isTransparent: Boolean
        get() = false

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val lightFilter: Int
        get() = 15
}