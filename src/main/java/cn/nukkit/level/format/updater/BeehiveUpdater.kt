package cn.nukkit.level.format.updater

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BeehiveUpdater @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(section: ChunkSection, updateDirection: Boolean) : Updater {
    private val section: ChunkSection
    private val updateDirection: Boolean

    @Override
    override fun update(offsetX: Int, offsetY: Int, offsetZ: Int, x: Int, y: Int, z: Int, state: BlockState): Boolean {
        val blockId: Int = state.getBlockId()
        if (blockId == BlockID.BEEHIVE || blockId == BlockID.BEE_NEST) {
            @SuppressWarnings("deprecation") val meta: Int = state.getLegacyDamage()
            val face: BlockFace
            var honeyLevel: Int
            if (updateDirection) {
                face = BlockFace.fromIndex(meta and 7)
                honeyLevel = meta shr 3 and 7
            } else {
                face = BlockFace.fromHorizontalIndex(meta and 3)
                honeyLevel = meta shr 2 and 7
            }
            if (honeyLevel > 5) {
                honeyLevel = 5
            }
            val newMeta = honeyLevel shl 2 or face.getHorizontalIndex()
            if (newMeta != meta) {
                section.setBlockState(x, y, z, state.withData(newMeta))
                return true
            }
        }
        return false
    }

    init {
        this.section = section
        this.updateDirection = updateDirection
    }
}