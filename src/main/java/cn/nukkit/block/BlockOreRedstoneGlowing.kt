package cn.nukkit.block

import cn.nukkit.event.block.BlockFadeEvent

//和pm源码有点出入，这里参考了wiki
/**
 * @author xtypr
 * @since 2015/12/6
 */
class BlockOreRedstoneGlowing : BlockOreRedstone() {
    @get:Override
    override val name: String
        get() = "Glowing Redstone Ore"

    @get:Override
    override val id: Int
        get() = GLOWING_REDSTONE_ORE

    @get:Override
    override val lightLevel: Int
        get() = 9

    @Override
    override fun toItem(): Item {
        return ItemBlock(Block.get(BlockID.REDSTONE_ORE))
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_SCHEDULED || type == Level.BLOCK_UPDATE_RANDOM) {
            val event = BlockFadeEvent(this, get(REDSTONE_ORE))
            level.getServer().getPluginManager().callEvent(event)
            if (!event.isCancelled()) {
                level.setBlock(this, event.getNewState(), false, true)
            }
            return Level.BLOCK_UPDATE_WEAK
        }
        return 0
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }
}