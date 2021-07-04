package cn.nukkit.level.format.updater

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
internal class OldWoodBarkUpdater @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(section: ChunkSection, fromLog: Int, increment: Int) : Updater {
    private val section: ChunkSection
    private val fromLog: Int
    private val increment: Int

    @Override
    override fun update(offsetX: Int, offsetY: Int, offsetZ: Int, x: Int, y: Int, z: Int, state: BlockState): Boolean {
        if (state.getBlockId() !== fromLog) {
            return false
        }
        @SuppressWarnings("deprecation") val legacyDamage: Int = state.getLegacyDamage()
        if (legacyDamage and 12 != 12) {
            return false
        }
        section.setBlockState(x, y, z, BlockState.of(BlockID.WOOD, (legacyDamage and 3) + increment))
        return true
    }

    init {
        this.section = section
        this.fromLog = fromLog
        this.increment = increment
    }
}