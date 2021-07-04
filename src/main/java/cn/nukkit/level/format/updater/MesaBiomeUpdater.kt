package cn.nukkit.level.format.updater

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class MesaBiomeUpdater @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(section: ChunkSection) : Updater {
    private val section: ChunkSection

    @SuppressWarnings("deprecation")
    @Override
    override fun update(offsetX: Int, offsetY: Int, offsetZ: Int, x: Int, y: Int, z: Int, state: BlockState): Boolean {
        if (state.getBlockId() === 48 && state.getLegacyDamage() === 48) {
            section.setBlockState(x, y, z, BlockState.of(BlockID.RED_SANDSTONE))
            return true
        }
        return false
    }

    init {
        this.section = section
    }
}