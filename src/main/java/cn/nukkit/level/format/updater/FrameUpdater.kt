package cn.nukkit.level.format.updater

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 * @since 2021-05-25
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
internal class FrameUpdater @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(section: ChunkSection) : Updater {
    private val section: ChunkSection

    @Override
    override fun update(offsetX: Int, offsetY: Int, offsetZ: Int, x: Int, y: Int, z: Int, state: BlockState): Boolean {
        return if (state.getBlockId() !== BlockID.ITEM_FRAME_BLOCK) {
            false
        } else section.setBlockStateAtLayer(x, y, z, 0, state.withData(getNewData(state.getExactIntStorage())))
    }

    private fun getNewData(fromData: Int): Int {
        return when (fromData) {
            0 -> 5 //minecraft:frame;facing_direction=5;item_frame_map_bit=0
            1 -> 4 //minecraft:frame;facing_direction=4;item_frame_map_bit=0
            2 -> 3 //minecraft:frame;facing_direction=3;item_frame_map_bit=0
            3 -> 2 //minecraft:frame;facing_direction=2;item_frame_map_bit=0
            4 -> 8 + 5 //minecraft:frame;facing_direction=5;item_frame_map_bit=1
            5 -> 8 + 4 //minecraft:frame;facing_direction=4;item_frame_map_bit=1
            6 -> 8 + 3 //minecraft:frame;facing_direction=3;item_frame_map_bit=1
            7 -> 8 + 2 //minecraft:frame;facing_direction=2;item_frame_map_bit=1
            else -> fromData
        }
    }

    init {
        this.section = section
    }
}