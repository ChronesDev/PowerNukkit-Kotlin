package cn.nukkit.item

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemFlintSteel @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemTool(FLINT_STEEL, meta, count, "Flint and Steel") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    override fun onActivate(level: Level, player: Player, block: Block, target: Block, face: BlockFace?, fx: Double, fy: Double, fz: Double): Boolean {
        if (player.isAdventure()) {
            return false
        }
        if (block.getId() === AIR && (target.isSolid() || target.getBurnChance() > 0)) {
            if (target.getId() === OBSIDIAN) {
                if (level.createPortal(target)) {
                    damageItem(player, block)
                    return true
                }
            }
            val fire: BlockFire = Block.get(BlockID.FIRE) as BlockFire
            fire.x = block.x
            fire.y = block.y
            fire.z = block.z
            fire.level = level
            if (fire.isBlockTopFacingSurfaceSolid(fire.down()) || fire.canNeighborBurn()) {
                val e = BlockIgniteEvent(block, null, player, BlockIgniteEvent.BlockIgniteCause.FLINT_AND_STEEL)
                block.getLevel().getServer().getPluginManager().callEvent(e)
                if (!e.isCancelled()) {
                    level.setBlock(fire, fire, true)
                    level.scheduleUpdate(fire, fire.tickRate() + ThreadLocalRandom.current().nextInt(10))
                }
                damageItem(player, block)
                return true
            }
            damageItem(player, block)
            return true
        }
        damageItem(player, block)
        return false
    }

    private fun damageItem(player: Player, block: Block) {
        if (!player.isCreative() && useOn(block)) {
            if (this.getDamage() >= getMaxDurability()) {
                this.count = 0
                player.getInventory().setItemInHand(Item.getBlock(BlockID.AIR))
            } else {
                player.getInventory().setItemInHand(this)
            }
        }
        block.getLevel().addSound(block, Sound.FIRE_IGNITE)
    }

    @Override
    override fun getMaxDurability(): Int {
        return ItemTool.DURABILITY_FLINT_STEEL
    }
}