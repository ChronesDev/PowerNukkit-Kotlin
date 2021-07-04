package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Nukkit Project Team
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
class BlockRedstoneLamp : BlockSolid(), RedstoneComponent {
    @get:Override
    override val name: String
        get() = "Redstone Lamp"

    @get:Override
    override val id: Int
        get() = REDSTONE_LAMP

    @get:Override
    override val hardness: Double
        get() = 0.3

    @get:Override
    override val resistance: Double
        get() = 1.5

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @PowerNukkitDifference(info = "Using new method for checking if powered", since = "1.4.0.0-PN")
    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        if (this.isGettingPower()) {
            this.level.setBlock(this, Block.get(BlockID.LIT_REDSTONE_LAMP), false, true)
        } else {
            this.level.setBlock(this, this, false, true)
        }
        return true
    }

    @PowerNukkitDifference(info = "Redstone Event after Block powered check + use #isGettingPower() method" +
            " + trigger observer.", since = "1.4.0.0-PN")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
            if (!this.level.getServer().isRedstoneEnabled()) {
                return 0
            }
            if (this.isGettingPower()) {
                // Redstone event
                val ev = RedstoneUpdateEvent(this)
                getLevel().getServer().getPluginManager().callEvent(ev)
                if (ev.isCancelled()) {
                    return 0
                }
                this.level.updateComparatorOutputLevelSelective(this, true)
                this.level.setBlock(this, Block.get(BlockID.LIT_REDSTONE_LAMP), false, false)
                return 1
            }
        }
        return 0
    }

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return arrayOf<Item>(
                ItemBlock(Block.get(BlockID.REDSTONE_LAMP))
        )
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.AIR_BLOCK_COLOR
}