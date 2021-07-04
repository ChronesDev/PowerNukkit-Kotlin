package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/12/2
 */
class BlockTorch @JvmOverloads constructor(meta: Int = 0) : BlockFlowable(meta), Faceable {
    @get:Override
    override val name: String
        get() = "Torch"

    @get:Override
    override val id: Int
        get() = TORCH

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val lightLevel: Int
        get() = 14

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed the block update logic to follow the same behaviour has vanilla")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val torchAttachment = torchAttachment!!
            val support: Block = this.getSide(torchAttachment.attachedFace)
            if (!BlockLever.isSupportValid(support, torchAttachment.torchDirection)) {
                this.getLevel().useBreakOn(this)
                return Level.BLOCK_UPDATE_NORMAL
            }
        }
        return 0
    }

    @Nullable
    private fun findValidSupport(): BlockFace? {
        for (horizontalFace in BlockFace.Plane.HORIZONTAL) {
            if (BlockLever.isSupportValid(getSide(horizontalFace.getOpposite()), horizontalFace)) {
                return horizontalFace
            }
        }
        return if (BlockLever.isSupportValid(down(), BlockFace.UP)) {
            BlockFace.UP
        } else null
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed the logic to follow the same behaviour has vanilla")
    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        var target: Block = target
        var face: BlockFace = face
        if (target.canBeReplaced()) {
            target = target.down()
            face = BlockFace.UP
        }
        if (face === BlockFace.DOWN || !BlockLever.isSupportValid(target, face)) {
            val valid: BlockFace = findValidSupport() ?: return false
            face = valid
        }
        blockFace = face
        this.getLevel().setBlock(block, this, true, true)
        return true
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.AIR_BLOCK_COLOR

    /**
     * Sets the direction that the flame is pointing.
     */
    @get:Override
    @set:Override
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var blockFace: BlockFace?
        get() = torchAttachment!!.torchDirection
        set(face) {
            var torchAttachment = TorchAttachment.getByTorchDirection(face)
            if (torchAttachment == null) {
                throw InvalidBlockPropertyValueException(TORCH_FACING_DIRECTION, torchAttachment, face, "The give BlockFace can't be mapped to TorchFace")
            }
            torchAttachment = torchAttachment
        }

    @Deprecated
    @DeprecationDetails(reason = "Using magic value", replaceWith = "getBlockFace()", since = "1.4.0.0-PN")
    fun getBlockFace(meta: Int): BlockFace {
        return TORCH_FACING_DIRECTION.getValueForMeta(meta).getTorchDirection()
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var torchAttachment: TorchAttachment?
        get() = getPropertyValue(TORCH_FACING_DIRECTION)
        set(face) {
            setPropertyValue(TORCH_FACING_DIRECTION, face)
        }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @RequiredArgsConstructor
    enum class TorchAttachment {
        UNKNOWN(BlockFace.UP), WEST(BlockFace.EAST), EAST(BlockFace.WEST), NORTH(BlockFace.SOUTH), SOUTH(BlockFace.NORTH), TOP(BlockFace.UP);

        /**
         * The direction that the flame is pointing.
         */
        @get:Since("1.4.0.0-PN")
        @get:PowerNukkitOnly
        val torchDirection: BlockFace? = null

        /**
         * The direction that is touching the attached block.
         */
        @get:Nonnull
        @get:Since("1.4.0.0-PN")
        @get:PowerNukkitOnly
        val attachedFace: BlockFace
            get() = when (this) {
                UNKNOWN, TOP -> BlockFace.DOWN
                EAST -> BlockFace.EAST
                WEST -> BlockFace.WEST
                SOUTH -> BlockFace.SOUTH
                NORTH -> BlockFace.NORTH
                else -> BlockFace.DOWN
            }

        companion object {
            @PowerNukkitOnly
            @Since("1.4.0.0-PN")
            @Nullable
            fun getByTorchDirection(@Nonnull face: BlockFace?): TorchAttachment? {
                return when (face) {
                    DOWN -> null
                    UP -> TOP
                    EAST -> WEST
                    WEST -> EAST
                    SOUTH -> NORTH
                    NORTH -> SOUTH
                    else -> null
                }
            }

            @PowerNukkitOnly
            @Since("1.4.0.0-PN")
            @Nullable
            fun getByAttachedFace(@Nonnull face: BlockFace?): TorchAttachment? {
                return when (face) {
                    UP -> null
                    DOWN -> TOP
                    SOUTH -> SOUTH
                    NORTH -> NORTH
                    EAST -> EAST
                    WEST -> WEST
                    else -> null
                }
            }
        }
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val TORCH_FACING_DIRECTION: BlockProperty<TorchAttachment> = ArrayBlockProperty("torch_facing_direction", false, TorchAttachment::class.java)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(TORCH_FACING_DIRECTION)
    }
}