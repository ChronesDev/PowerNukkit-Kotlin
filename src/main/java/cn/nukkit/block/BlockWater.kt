package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockWater @JvmOverloads constructor(meta: Int = 0) : BlockLiquid(meta) {
    @get:Override
    override val id: Int
        get() = WATER

    @get:Override
    override val name: String
        get() = "Water"

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        val ret: Boolean = this.getLevel().setBlock(this, this, true, false)
        this.getLevel().scheduleUpdate(this, tickRate())
        return ret
    }

    @Since("1.2.1.0-PN")
    @PowerNukkitOnly
    @Override
    fun afterRemoval(newBlock: Block, update: Boolean) {
        if (!update) {
            return
        }
        val newId: Int = newBlock.getId()
        if (newId == WATER || newId == STILL_WATER) {
            return
        }
        val up: Block = up(1, 0)
        for (diagonalFace in BlockFace.Plane.HORIZONTAL) {
            val diagonal: Block = up.getSide(diagonalFace)
            if (diagonal.getId() === BlockID.SUGARCANE_BLOCK) {
                diagonal.onUpdate(Level.BLOCK_UPDATE_SCHEDULED)
            }
        }
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WATER_BLOCK_COLOR

    @Override
    override fun getBlock(meta: Int): BlockLiquid {
        return Block.get(BlockID.WATER, meta) as BlockLiquid
    }

    @Override
    override fun onEntityCollide(entity: Entity) {
        super.onEntityCollide(entity)
        if (entity.fireTicks > 0) {
            entity.extinguish()
        }
    }

    @Override
    override fun tickRate(): Int {
        return 5
    }

    @Override
    override fun usesWaterLogging(): Boolean {
        return true
    }
}