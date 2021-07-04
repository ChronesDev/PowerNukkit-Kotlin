package cn.nukkit.level.format.updater

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@UtilityClass
@Log4j2
class ChunkUpdater {
    /**
     * Version history:
     * <dl>
     * <dt>0</dt><dd>Before 1.3.0.0-PN or from Cloudburst Nukkit</dd>
     * <dt>1</dt><dd>Melon Stem, Pumpkin Stem and Cobblestone Walls are now rendered server side</dd>
     * <dt>2, 3, 4</dt><dd>Re-render the cobblestone walls to fix connectivity issues</dd>
     * <dt>6</dt><dd>Beehive and bee_nest now uses BlockFace.horizontalIndex instead of BlockFace.index (parallel change)</dd>
     * <dt>5, 7</dt><dd>Beehive and bee_nest honey level is now limited to 5, was up to 7 (parallel change)</dd>
     * <dt>8</dt><dd>Sync beehive and bee_nest parallel changes</dd>
     * <dt>9</dt><dd>Re-render cobblestone walls to connect to glass, stained glass, and other wall types like border and blackstone wall</dd>
     * <dt>10</dt><dd>Re-render snow layers to make them cover grass blocks and fix leaves2 issue: https://github.com/PowerNukkit/PowerNukkit/issues/482</dd>
     * <dt>11</dt><dd>The debug block property was removed from stripped_warped_hyphae, stripped_warped_stem, stripped_crimson_hyphae, and stripped_crimson_stem</dd>
     * <dt>12</dt><dd>Upgraded the block frame data values to match the vanilla data, allowing to place up and down and have map</dd>
    </dl> *
     */
    @get:SuppressWarnings("java:S3400")
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val currentContentVersion: Int
        get() = 12

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun backwardCompatibilityUpdate(level: Level, chunk: BaseChunk) {
        var updated = false
        for (section in chunk.getSections()) {
            if (section.getContentVersion() >= currentContentVersion) {
                continue
            }
            if (section.getContentVersion() < 5) {
                updated = updateToV8FromV0toV5(level, chunk, updated, section, section.getContentVersion())
            } else if (section.getContentVersion() === 5 || section.getContentVersion() === 7) {
                updated = updateBeehiveFromV5or6or7toV8(chunk, updated, section, false)
            } else if (section.getContentVersion() === 6) {
                updated = updateBeehiveFromV5or6or7toV8(chunk, updated, section, true)
            }
            if (section.getContentVersion() === 8) {
                updated = upgradeWallsFromV8toV9(level, chunk, updated, section)
            }
            if (section.getContentVersion() === 9) {
                updated = upgradeSnowLayersFromV9toV10(level, chunk, updated, section)
            }
            if (section.getContentVersion() === 10) {
                updated = upgradeStrippedStemsFromV10toV11(chunk, updated, section)
            }
            if (section.getContentVersion() === 11) {
                updated = upgradeFrameFromV11toV12(chunk, section, updated)
            }
        }
        if (updated) {
            chunk.setChanged()
        }
    }

    private fun upgradeStrippedStemsFromV10toV11(chunk: BaseChunk, updated: Boolean, section: ChunkSection): Boolean {
        var updated = updated
        updated = walk(chunk, section, StemStrippedUpdater(section)) || updated
        section.setContentVersion(11)
        return updated
    }

    private fun upgradeWallsFromV8toV9(level: Level, chunk: BaseChunk, updated: Boolean, section: ChunkSection): Boolean {
        var updated = updated
        updated = walk(chunk, section, WallUpdater(level, section)) || updated
        section.setContentVersion(9)
        return updated
    }

    private fun upgradeSnowLayersFromV9toV10(level: Level, chunk: BaseChunk, updated: Boolean, section: ChunkSection): Boolean {
        var updated = updated
        updated = updated or walk(chunk, section, GroupedUpdaters(
                NewLeafUpdater(section),
                SnowLayerUpdater(level, section)
        ))
        section.setContentVersion(10)
        return updated
    }

    private fun updateBeehiveFromV5or6or7toV8(chunk: BaseChunk, updated: Boolean, section: ChunkSection, updateDirection: Boolean): Boolean {
        var updated = updated
        if (walk(chunk, section, BeehiveUpdater(section, updateDirection))) {
            updated = true
        }
        section.setContentVersion(8)
        return updated
    }

    private fun updateToV8FromV0toV5(level: Level, chunk: BaseChunk, updated: Boolean, section: ChunkSection, contentVersion: Int): Boolean {
        var updated = updated
        val wallUpdater = WallUpdater(level, section)
        var sectionUpdated = walk(chunk, section, GroupedUpdaters(
                StemStrippedUpdater(section),
                MesaBiomeUpdater(section),
                NewLeafUpdater(section),
                BeehiveUpdater(section, true),
                wallUpdater,
                (if (contentVersion < 1) StemUpdater(level, section, BlockID.MELON_STEM, BlockID.MELON_BLOCK) else null)!!,
                (if (contentVersion < 1) StemUpdater(level, section, BlockID.PUMPKIN_STEM, BlockID.PUMPKIN) else null)!!,
                (if (contentVersion < 5) OldWoodBarkUpdater(section, BlockID.LOG, 0) else null)!!,
                (if (contentVersion < 5) OldWoodBarkUpdater(section, BlockID.LOG2, 4) else null)!!,
                (if (contentVersion < 5) DoorUpdater(chunk, section) else null)!!
        ))
        updated = updated || sectionUpdated
        var attempts = 0
        while (sectionUpdated) {
            if (attempts++ >= 5) {
                val x: Int = chunk.getX() shl 4 or 0x6
                val y: Int = section.getY() shl 4 or 0x6
                val z: Int = chunk.getZ() shl 4 or 0x6
                log.error("The chunk section at x:{}, y:{}, z:{} failed to complete the backward compatibility update 1 after {} attempts", x, y, z, attempts)
                break
            }
            sectionUpdated = walk(chunk, section, wallUpdater)
        }
        section.setContentVersion(8)
        return updated
    }

    private fun walk(chunk: BaseChunk, section: ChunkSection, updater: Updater): Boolean {
        val offsetX: Int = chunk.getX() shl 4
        val offsetZ: Int = chunk.getZ() shl 4
        val offsetY: Int = section.getY() shl 4
        var updated = false
        for (x in 0..0xF) {
            for (z in 0..0xF) {
                for (y in 0..0xF) {
                    val state: BlockState = section.getBlockState(x, y, z, 0)
                    updated = updated or updater.update(offsetX, offsetY, offsetZ, x, y, z, state)
                }
            }
        }
        return updated
    }

    companion object {
        private fun upgradeFrameFromV11toV12(chunk: BaseChunk, section: ChunkSection, updated: Boolean): Boolean {
            var updated = updated
            updated = walk(chunk, section, FrameUpdater(section)) || updated
            section.setContentVersion(12)
            return updated
        }
    }
}