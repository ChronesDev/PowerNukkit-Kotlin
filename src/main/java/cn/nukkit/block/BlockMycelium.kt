package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Pub4Game
 * @since 03.01.2016
 */
class BlockMycelium : BlockSolid() {
    @get:Override
    override val name: String
        get() = "Mycelium"

    @get:Override
    override val id: Int
        get() = MYCELIUM

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_SHOVEL

    @get:Override
    override val hardness: Double
        get() = 0.6

    @get:Override
    override val resistance: Double
        get() = 2.5

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return arrayOf<Item>(
                ItemBlock(Block.get(BlockID.DIRT))
        )
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (getLevel().getFullLight(add(0, 1, 0)) >= BlockCrops.MINIMUM_LIGHT_LEVEL) {
                //TODO: light levels
                val random = NukkitRandom()
                x = random.nextRange(x as Int - 1, x as Int + 1)
                y = random.nextRange(y as Int - 1, y as Int + 1)
                z = random.nextRange(z as Int - 1, z as Int + 1)
                val block: Block = this.getLevel().getBlock(Vector3(x, y, z))
                if (block.getId() === Block.DIRT && block.getDamage() === 0) {
                    if (block.up().isTransparent()) {
                        val ev = BlockSpreadEvent(block, this, Block.get(BlockID.MYCELIUM))
                        Server.getInstance().getPluginManager().callEvent(ev)
                        if (!ev.isCancelled()) {
                            this.getLevel().setBlock(block, ev.getNewState())
                        }
                    }
                }
            }
        }
        return 0
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.PURPLE_BLOCK_COLOR

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (!this.up().canBeReplaced()) {
            return false
        }
        if (item.isShovel()) {
            item.useOn(this)
            this.getLevel().setBlock(this, Block.get(BlockID.GRASS_PATH))
            if (player != null) {
                player.getLevel().addSound(player, Sound.USE_GRASS)
            }
            return true
        }
        return false
    }
}