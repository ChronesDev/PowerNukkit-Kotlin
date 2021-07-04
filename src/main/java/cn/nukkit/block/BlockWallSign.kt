package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author Pub4Game
 * @since 26.12.2015
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockWallSign @JvmOverloads constructor(meta: Int = 0) : BlockSignPost(meta) {
    @get:Override
    override val id: Int
        get() = WALL_SIGN

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val wallId: Int
        get() = id

    @get:Override
    protected override val postId: Int
        protected get() = SIGN_POST

    @get:Override
    override val name: String
        get() = "Wall Sign"

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
    override var signDirection: CompassRoseDirection
        get() = blockFace.getCompassRoseDirection()
        set(direction) {
            blockFace = direction.getClosestBlockFace()
        }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = CommonBlockProperties.FACING_DIRECTION_BLOCK_PROPERTIES
    }
}