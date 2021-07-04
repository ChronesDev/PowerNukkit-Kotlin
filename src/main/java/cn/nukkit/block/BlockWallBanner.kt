package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author PetteriM1
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockWallBanner @JvmOverloads constructor(meta: Int = 0) : BlockBanner(meta) {
    @get:Override
    override val id: Int
        get() = WALL_BANNER

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Wall Banner"

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.getSide(blockFace.getOpposite()).getId() === AIR) {
                this.getLevel().useBreakOn(this)
            }
            return Level.BLOCK_UPDATE_NORMAL
        }
        return 0
    }

    @get:Override
    @set:Override
    @set:Since("1.3.0.0-PN")
    @set:PowerNukkitOnly
    override var blockFace: BlockFace
        get() = getPropertyValue(FACING_DIRECTION)
        set(face) {
            setPropertyValue(FACING_DIRECTION, face)
        }

    @get:Override
    @set:Override
    override var direction: CompassRoseDirection
        get() = blockFace.getCompassRoseDirection()
        set(direction) {
            blockFace = direction.getClosestBlockFace()
        }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = CommonBlockProperties.FACING_DIRECTION_BLOCK_PROPERTIES
    }
}