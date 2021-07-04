package cn.nukkit.level.format.updater

import cn.nukkit.block.BlockHyphaeStrippedCrimson

/**
 * @author joserobjr
 */
@ExtendWith(PowerNukkitExtension::class)
internal class StemStrippedUpdaterTest {
    @SuppressWarnings("Convert2MethodRef")
    @Test
    fun testDebugPropertyRemoval() {
        val oldProperties = BlockProperties(PILLAR_AXIS, DEPRECATED)
        val mutableState: MutableBlockState = oldProperties.createMutableState(BlockID.STRIPPED_CRIMSON_HYPHAE)
        mutableState.setPropertyValue(PILLAR_AXIS, BlockFace.Axis.Z)
        mutableState.setPropertyValue(DEPRECATED, 1)
        val oldState: BlockState = mutableState.getCurrentState()
        val chunkSection = ChunkSection(0)
        chunkSection.setBlockState(1, 2, 3, BlockState.of(BlockID.STONE))
        chunkSection.delayPaletteUpdates()
        chunkSection.setBlockState(1, 2, 3, oldState)
        chunkSection.setContentVersion(9)
        val updater = StemStrippedUpdater(chunkSection)
        updater.update(0, 0, 0, 1, 2, 3, chunkSection.getBlockState(1, 2, 3))
        val updated: BlockState = chunkSection.getBlockState(1, 2, 3)
        assertEquals(BlockState.of(BlockID.STRIPPED_CRIMSON_HYPHAE).withProperty(PILLAR_AXIS, BlockFace.Axis.Z), updated)
        val block: BlockHyphaeStrippedCrimson = assertDoesNotThrow { updated.getBlock() } as BlockHyphaeStrippedCrimson
        assertEquals(BlockFace.Axis.Z, block.getPillarAxis())
    }
}