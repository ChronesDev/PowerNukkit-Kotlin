package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author xtypr
 * @since 2015/12/6
 * @apiNote Implements BlockConnectable only in PowerNukkit
 */
@PowerNukkitDifference(info = "Made it implement BlockConnectable")
abstract class BlockThin protected constructor() : BlockTransparent(), BlockConnectable {
    @get:Override
    override val isSolid: Boolean
        get() = false

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace?): Boolean {
        return false
    }

    protected override fun recalculateBoundingBox(): AxisAlignedBB {
        val offNW = 7.0 / 16.0
        val offSE = 9.0 / 16.0
        val onNW = 0.0
        val onSE = 1.0
        var w = offNW
        var e = offSE
        var n = offNW
        var s = offSE
        try {
            val north = canConnect(this.north())
            val south = canConnect(this.south())
            val west = canConnect(this.west())
            val east = canConnect(this.east())
            w = if (west) onNW else offNW
            e = if (east) onSE else offSE
            n = if (north) onNW else offNW
            s = if (south) onSE else offSE
        } catch (ignore: LevelException) {
            //null sucks
        }
        return SimpleAxisAlignedBB(
                this.x + w,
                this.y,
                this.z + n,
                this.x + e,
                this.y + 1,
                this.z + s
        )
    }

    @PowerNukkitDifference(info = "Fixed connection logic for BE 1.16.0", since = "1.3.0.0-PN")
    @Override
    fun canConnect(block: Block): Boolean {
        return when (block.getId()) {
            GLASS_PANE, STAINED_GLASS_PANE, IRON_BARS, COBBLE_WALL -> true
            else -> {
                if (block is BlockTrapdoor) {
                    val trapdoor: BlockTrapdoor = block
                    return trapdoor.isOpen() && trapdoor.getBlockFace() === calculateFace(this, trapdoor)
                }
                block.isSolid()
            }
        }
    }
}