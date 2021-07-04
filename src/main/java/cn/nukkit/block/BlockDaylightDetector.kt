package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author CreeperFace
 * @since 2015/11/22
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
class BlockDaylightDetector : BlockTransparentMeta(), RedstoneComponent, BlockEntityHolder<BlockEntityDaylightDetector?> {
    @get:Override
    override val id: Int
        get() = DAYLIGHT_DETECTOR

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Daylight Detector"

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.DAYLIGHT_DETECTOR

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityDaylightDetector?>
        get() = BlockEntityDaylightDetector::class.java

    @get:Override
    override val hardness: Double
        get() = 0.2

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WOOD_BLOCK_COLOR

    @Override
    override fun toItem(): Item {
        return ItemBlock(this, 0)
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        val detector: BlockEntityDaylightDetector = BlockEntityHolder.setBlockAndCreateEntity(this) ?: return false
        if (getLevel().getDimension() === Level.DIMENSION_OVERWORLD) {
            updatePower()
        }
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        val block = BlockDaylightDetectorInverted()
        getLevel().setBlock(this, block, true, true)
        block.updatePower()
        return true
    }

    @Override
    override fun onBreak(item: Item?): Boolean {
        if (super.onBreak(item)) {
            if (getLevel().getDimension() === Level.DIMENSION_OVERWORLD) {
                updateAroundRedstone()
            }
            return true
        }
        return false
    }

    @Override
    override fun getWeakPower(face: BlockFace?): Int {
        return getLevel().getBlockDataAt(getFloorX(), getFloorY(), getFloorZ())
    }

    @get:Override
    override val isPowerSource: Boolean
        get() = true
    val isInverted: Boolean
        get() = false

    fun updatePower() {
        var i: Int
        if (getLevel().getDimension() === Level.DIMENSION_OVERWORLD) {
            i = getLevel().getBlockSkyLightAt(x as Int, y as Int, z as Int) - getLevel().calculateSkylightSubtracted(1.0f)
            var f: Float = getLevel().getCelestialAngle(1.0f)
            if (isInverted) {
                i = 15 - i
            }
            if (i > 0 && !isInverted) {
                val f1 = if (f < Math.PI as Float) 0.0f else Math.PI as Float * 2f
                f = f + (f1 - f) * 0.2f
                i = Math.round(i.toFloat() * MathHelper.cos(f))
            }
            i = MathHelper.clamp(i, 0, 15)
        } else i = 0
        if (i != getLevel().getBlockDataAt(getFloorX(), getFloorY(), getFloorZ())) {
            getLevel().setBlockDataAt(getFloorX(), getFloorY(), getFloorZ(), i)
            updateAroundRedstone()
        }
    }

    companion object {
        @Since("1.5.0.0-PN")
        @PowerNukkitOnly
        val PROPERTIES: BlockProperties = CommonBlockProperties.REDSTONE_SIGNAL_BLOCK_PROPERTY
    }
}