package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author Pub4Game
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
class BlockRedstoneLampLit : BlockRedstoneLamp(), RedstoneComponent {
    @get:Override
    override val name: String
        get() = "Lit Redstone Lamp"

    @get:Override
    override val id: Int
        get() = LIT_REDSTONE_LAMP

    @get:Override
    override val lightLevel: Int
        get() = 15

    @Override
    override fun toItem(): Item {
        return ItemBlock(Block.get(BlockID.REDSTONE_LAMP))
    }

    @PowerNukkitDifference(info = "Redstone Event on scheduled update part + use #isGettingPower() method" +
            " + trigger observer.", since = "1.4.0.0-PN")
    @Override
    override fun onUpdate(type: Int): Int {
        if (!this.level.getServer().isRedstoneEnabled()) {
            return 0
        }
        if ((type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) && !this.isGettingPower()) {
            this.level.scheduleUpdate(this, 4)
            return 1
        }
        if (type == Level.BLOCK_UPDATE_SCHEDULED && !this.isGettingPower()) {
            // Redstone event
            val ev = RedstoneUpdateEvent(this)
            this.level.getServer().getPluginManager().callEvent(ev)
            if (ev.isCancelled()) {
                return 0
            }
            this.level.updateComparatorOutputLevelSelective(this, true)
            this.level.setBlock(this, Block.get(BlockID.REDSTONE_LAMP), false, false)
        }
        return 0
    }
}