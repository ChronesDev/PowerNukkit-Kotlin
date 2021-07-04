package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/11/22
 */
class BlockGrassPath : BlockGrass() {
    @get:Override
    override val id: Int
        get() = GRASS_PATH

    @get:Override
    override val name: String
        get() = "Dirt Path"

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_SHOVEL

    @get:Override
    override val maxY: Double
        get() = this.y + 1

    @get:Override
    override val hardness: Double
        get() = 0.65

    @get:Override
    override val resistance: Double
        get() = 0.65

    @get:Override
    override val color: BlockColor
        get() = BlockColor.DIRT_BLOCK_COLOR

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.up().isSolid()) {
                this.level.setBlock(this, Block.get(BlockID.DIRT), false, true)
            }
            return Level.BLOCK_UPDATE_NORMAL
        }
        return 0
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (item.isHoe()) {
            item.useOn(this)
            this.getLevel().setBlock(this, get(FARMLAND), true)
            if (player != null) {
                player.getLevel().addSound(player, Sound.USE_GRASS)
            }
            return true
        }
        return false
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace?): Boolean {
        return true
    }

    @get:Override
    @get:PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will return true")
    override val isTransparent: Boolean
        get() = true
}