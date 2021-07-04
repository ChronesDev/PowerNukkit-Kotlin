package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockIce : BlockTransparent() {
    @get:Override
    override val id: Int
        get() = ICE

    @get:Override
    override val name: String
        get() = "Ice"

    @get:Override
    override val resistance: Double
        get() = 2.5

    @get:Override
    override val hardness: Double
        get() = 0.5

    @get:Override
    override val frictionFactor: Double
        get() = 0.98

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will not create water when it is above air")
    @Override
    override fun onBreak(item: Item): Boolean {
        return if (level.getDimension() === Level.DIMENSION_NETHER || item.getEnchantmentLevel(Enchantment.ID_SILK_TOUCH) > 0 || down().getId() === BlockID.AIR) {
            super.onBreak(item)
        } else level.setBlock(this, Block.get(BlockID.WATER), true)
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (level.getBlockLightAt(this.x as Int, this.y as Int, this.z as Int) >= 12) {
                val event = BlockFadeEvent(this, if (level.getDimension() === Level.DIMENSION_NETHER) get(AIR) else get(WATER))
                level.getServer().getPluginManager().callEvent(event)
                if (!event.isCancelled()) {
                    level.setBlock(this, event.getNewState(), true)
                }
                return Level.BLOCK_UPDATE_RANDOM
            }
        }
        return 0
    }

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return Item.EMPTY_ARRAY
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.ICE_BLOCK_COLOR

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val lightFilter: Int
        get() = 2
}