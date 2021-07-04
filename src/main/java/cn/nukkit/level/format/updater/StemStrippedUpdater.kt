package cn.nukkit.level.format.updater

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class StemStrippedUpdater @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(section: ChunkSection) : Updater {
    private val section: ChunkSection
    private val oldProperties: BlockProperties = BlockProperties(PILLAR_AXIS, DEPRECATED)

    @Override
    override fun update(offsetX: Int, offsetY: Int, offsetZ: Int, x: Int, y: Int, z: Int, state: BlockState): Boolean {
        when (state.getBlockId()) {
            BlockID.STRIPPED_WARPED_HYPHAE, BlockID.STRIPPED_WARPED_STEM, BlockID.STRIPPED_CRIMSON_HYPHAE, BlockID.STRIPPED_CRIMSON_STEM -> {
            }
            else -> return false
        }
        val currentStorage: Int = state.getExactIntStorage()
        if (oldProperties.getIntValue(currentStorage, DEPRECATED.getName()) === 0) {
            return false
        }
        val newStorage: Int = oldProperties.setIntValue(currentStorage, DEPRECATED.getName(), 0)
        section.setBlockState(x, y, z, state.withData(newStorage))
        return true
    }

    init {
        this.section = section
    }
}