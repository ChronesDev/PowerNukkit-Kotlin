package cn.nukkit.level.format.updater

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class SnowLayerUpdater @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(level: Level, section: ChunkSection) : Updater {
    private val level: Level
    private val section: ChunkSection

    @Override
    override fun update(offsetX: Int, offsetY: Int, offsetZ: Int, x: Int, y: Int, z: Int, state: BlockState): Boolean {
        if (state.getBlockId() !== BlockID.SNOW_LAYER) {
            return false
        }
        if (y > 0) {
            if (section.getBlockId(x, y - 1, z) === BlockID.GRASS) {
                section.setBlockState(x, y, z, state.withProperty(BlockSnowLayer.COVERED, true))
                return true
            }
        } else if (offsetY == 0) {
            return false
        }
        if (level.getBlockIdAt(offsetX + x, offsetY + y - 1, offsetZ + z, 0) === BlockID.GRASS) {
            section.setBlockState(x, y, z, state.withProperty(BlockSnowLayer.COVERED, true))
            return true
        }
        return false
    }

    init {
        this.level = level
        this.section = section
    }
}