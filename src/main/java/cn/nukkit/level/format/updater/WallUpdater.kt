package cn.nukkit.level.format.updater

import cn.nukkit.Server

@PowerNukkitOnly
@Since("1.4.0.0-PN")
internal class WallUpdater @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(level: Level, section: ChunkSection) : Updater {
    private val level: Level
    private val section: ChunkSection

    @Override
    override fun update(offsetX: Int, offsetY: Int, offsetZ: Int, x: Int, y: Int, z: Int, state: BlockState): Boolean {
        if (state.getBlockId() !== BlockID.COBBLE_WALL) {
            return false
        }
        val levelX = offsetX + x
        val levelY = offsetY + y
        val levelZ = offsetZ + z
        var block: Block
        try {
            block = state.getBlock(level, levelX, levelY, levelZ, 0)
        } catch (e: InvalidBlockStateException) {
            // Block was on an invalid state, clearing the state but keeping the material type
            try {
                var data: Int = state.getLegacyDamage() and 0xF
                try {
                    BlockWall.WALL_BLOCK_TYPE.validateMeta(data, 0)
                } catch (ignored: InvalidBlockPropertyMetaException) {
                    // Oh no! Somehow the wall type became invalid :/
                    // Unfortunately, our only option now is to convert it to a regular cobblestone wall
                    data = 0
                }
                block = state.withData(data).getBlock(level, levelX, levelY, levelZ, 0)
            } catch (e2: InvalidBlockStateException) {
                e.addSuppressed(e2)
                val server: Server = Server.getInstance()
                log.warn("Failed to update the block X:{}, Y:{}, Z:{} at {}, could not cast it to BlockWall. Section Version:{}, Updating To:{}, Server Version:{} {}",
                        levelX, levelY, levelZ, level, section.getContentVersion(), ChunkUpdater.getCurrentContentVersion(),
                        server.getNukkitVersion(), server.getGitCommit(), e)
                return false
            }
        }
        val blockWall: BlockWall = block as BlockWall
        if (blockWall.autoConfigureState()) {
            section.setBlockStateAtLayer(x, y, z, 0, blockWall.getCurrentState())
            return true
        }
        return false
    }

    companion object {
        private val log: Logger = org.apache.logging.log4j.LogManager.getLogger(WallUpdater::class.java)
    }

    init {
        this.level = level
        this.section = section
    }
}