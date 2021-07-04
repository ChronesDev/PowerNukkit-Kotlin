package cn.nukkit.level.format.updater

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class DoorUpdater @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(chunk: Chunk, section: ChunkSection) : Updater {
    private val chunk: Chunk
    private val section: ChunkSection

    @Override
    override fun update(offsetX: Int, offsetY: Int, offsetZ: Int, x: Int, y: Int, z: Int, state: BlockState): Boolean {
        when (state.getBlockId()) {
            WOODEN_DOOR_BLOCK, DARK_OAK_DOOR_BLOCK, ACACIA_DOOR_BLOCK, BIRCH_DOOR_BLOCK, JUNGLE_DOOR_BLOCK, SPRUCE_DOOR_BLOCK, IRON_DOOR_BLOCK -> {
            }
            else -> return false
        }
        @SuppressWarnings("deprecation") val legacy: Int = state.getLegacyDamage()
        val mutableState: MutableBlockState = BlockStateRegistry.createMutableState(state.getBlockId())
        if (legacy and DOOR_TOP_BIT > 0) {
            mutableState.setBooleanValue(CommonBlockProperties.UPPER_BLOCK, true)
            mutableState.setBooleanValue(BlockDoor.DOOR_HINGE, legacy and DOOR_HINGE_BIT > 0)
            val underY = offsetY + y - 1
            if (underY >= 0) {
                val underState: BlockState = chunk.getBlockState(x, underY, z)
                if (underState.getBlockId() === state.getBlockId()) {
                    mutableState.setPropertyValue(BlockDoor.DOOR_DIRECTION, underState.getPropertyValue(BlockDoor.DOOR_DIRECTION))
                    mutableState.setBooleanValue(CommonBlockProperties.OPEN, underState.getPropertyValue(CommonBlockProperties.OPEN))
                }
            }
        } else {
            mutableState.setBooleanValue(CommonBlockProperties.UPPER_BLOCK, false)
            mutableState.setPropertyValue(BlockDoor.DOOR_DIRECTION, BlockDoor.DOOR_DIRECTION.getValueForMeta(legacy and 0x3))
            mutableState.setBooleanValue(CommonBlockProperties.OPEN, legacy and DOOR_OPEN_BIT > 0)
        }
        return section.setBlockState(x, y, z, mutableState.getCurrentState())
    }

    companion object {
        private const val DOOR_OPEN_BIT = 0x04
        private const val DOOR_TOP_BIT = 0x08
        private const val DOOR_HINGE_BIT = 0x01
    }

    init {
        this.chunk = chunk
        this.section = section
    }
}