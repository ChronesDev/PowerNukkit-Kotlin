package cn.nukkit.item

import cn.nukkit.Player

/**
 * @author PetteriM1
 */
class ItemFireCharge @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(FIRE_CHARGE, 0, count, "Fire Charge") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(level: Level, player: Player, block: Block, target: Block, face: BlockFace?, fx: Double, fy: Double, fz: Double): Boolean {
        if (player.isAdventure()) {
            return false
        }
        if (block.getId() === AIR && (target.isSolid() || target.getBurnChance() > 0)) {
            if (target.getId() === OBSIDIAN) {
                if (level.createPortal(target)) {
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
                    level.addLevelEvent(block, LevelEventPacket.EVENT_SOUND_BLAZE_SHOOT, 78642)
                    level.scheduleUpdate(fire, fire.tickRate() + ThreadLocalRandom.current().nextInt(10))
                }
                if (player.isSurvival()) {
                    val item: Item = player.getInventory().getItemInHand()
                    item.setCount(item.getCount() - 1)
                    player.getInventory().setItemInHand(item)
                }
                return true
            }
        }
        return false
    }
}