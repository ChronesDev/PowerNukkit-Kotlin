package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author CreeperFace
 * @since 2015/11/22
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockDaylightDetectorInverted : BlockDaylightDetector() {
    @get:Override
    override val id: Int
        get() = DAYLIGHT_DETECTOR_INVERTED

    @get:Override
    override val name: String
        get() = "Daylight Detector Inverted"

    @Override
    override fun toItem(): Item {
        return ItemBlock(Block.get(BlockID.DAYLIGHT_DETECTOR), 0)
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        val block = BlockDaylightDetector()
        getLevel().setBlock(this, block, true, true)
        block.updatePower()
        return true
    }

    @get:Override
    override val isInverted: Boolean
        get() = true
}