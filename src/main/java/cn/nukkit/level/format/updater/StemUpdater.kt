package cn.nukkit.level.format.updater

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
internal class StemUpdater @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(level: Level, section: ChunkSection, stemId: Int, productId: Int) : Updater {
    private val level: Level
    private val section: ChunkSection
    private val stemId: Int
    private val productId: Int

    @Override
    override fun update(offsetX: Int, offsetY: Int, offsetZ: Int, x: Int, y: Int, z: Int, state: BlockState): Boolean {
        if (state.getBlockId() !== stemId) {
            return false
        }
        for (blockFace in BlockFace.Plane.HORIZONTAL) {
            val sideId: Int = level.getBlockIdAt(
                    offsetX + x + blockFace.getXOffset(),
                    offsetY + y,
                    offsetZ + z + blockFace.getZOffset()
            )
            if (sideId == productId) {
                val blockStem: Block = state.getBlock(level, offsetX + x, offsetY + y, offsetZ + z, 0)
                (blockStem as Faceable).setBlockFace(blockFace)
                section.setBlockStateAtLayer(x, y, z, 0, blockStem.getCurrentState())
                return true
            }
        }
        return false
    }

    init {
        this.level = level
        this.section = section
        this.stemId = stemId
        this.productId = productId
    }
}