package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Nukkit Project Team
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
class BlockLever @JvmOverloads constructor(meta: Int = 0) : BlockFlowable(meta), RedstoneComponent, Faceable {
    @get:Override
    override val name: String
        get() = "Lever"

    @get:Override
    override val id: Int
        get() = LEVER

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    override val hardness: Double
        get() = 0.5

    @get:Override
    override val resistance: Double
        get() = 2.5

    @Override
    override fun toItem(): Item {
        return ItemBlock(this, 0)
    }

    @set:Since("1.4.0.0-P`N")
    @set:PowerNukkitOnly
    var isPowerOn: Boolean
        get() = getBooleanValue(OPEN)
        set(powerOn) {
            setBooleanValue(OPEN, powerOn)
        }

    @get:Since("1.4.0.0-P`N")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-P`N")
    @set:PowerNukkitOnly
    var leverOrientation: LeverOrientation?
        get() = getPropertyValue(LEVER_DIRECTION)
        set(value) {
            setPropertyValue(LEVER_DIRECTION, value)
        }

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        this.level.getServer().getPluginManager().callEvent(BlockRedstoneEvent(this, if (isPowerOn) 15 else 0, if (isPowerOn) 0 else 15))
        toggleBooleanProperty(OPEN)
        this.getLevel().setBlock(this, this, false, true)
        this.getLevel().addSound(this, Sound.RANDOM_CLICK, 0.8f, if (isPowerOn) 0.58f else 0.5f)
        val orientation = leverOrientation!!
        val face: BlockFace = orientation.getFacing()
        if (this.level.getServer().isRedstoneEnabled()) {
            updateAroundRedstone()
            RedstoneComponent.updateAroundRedstone(getSide(face.getOpposite()), face)
        }
        return true
    }

    @PowerNukkitDifference(info = "Now, can be placed on solid blocks", since = "1.4.0.0-PN")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val blockFace: BlockFace = leverOrientation!!.getFacing().getOpposite()
            val side: Block = this.getSide(blockFace)
            if (!isSupportValid(side, blockFace.getOpposite())) {
                this.level.useBreakOn(this)
            }
        }
        return 0
    }

    @PowerNukkitDifference(info = "Allows to be placed on walls", since = "1.3.0.0-PN")
    @PowerNukkitDifference(info = "Now, can be placed on solid blocks and always returns false if the placement fails", since = "1.4.0.0-PN")
    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, player: Player): Boolean {
        var target: Block = target
        var face: BlockFace = face
        if (target.canBeReplaced()) {
            target = target.down()
            face = BlockFace.UP
        }
        if (!isSupportValid(target, face)) {
            return false
        }
        leverOrientation = LeverOrientation.forFacings(face, player.getHorizontalFacing())
        return this.getLevel().setBlock(block, this, true, true)
    }

    @Override
    @PowerNukkitDifference(info = "Update redstone", since = "1.4.0.0-PN")
    override fun onBreak(item: Item?): Boolean {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true)
        if (isPowerOn) {
            val face: BlockFace = leverOrientation!!.getFacing()
            this.level.updateAround(this.getLocation().getSide(face.getOpposite()))
            if (level.getServer().isRedstoneEnabled()) {
                updateAroundRedstone()
                RedstoneComponent.updateAroundRedstone(getSide(face.getOpposite()), face)
            }
        }
        return true
    }

    @Override
    override fun getWeakPower(side: BlockFace?): Int {
        return if (isPowerOn) 15 else 0
    }

    override fun getStrongPower(side: BlockFace): Int {
        return if (!isPowerOn) 0 else if (leverOrientation!!.getFacing() === side) 15 else 0
    }

    @get:Override
    override val isPowerSource: Boolean
        get() = true

    enum class LeverOrientation(val metadata: Int, override val name: String, face: BlockFace) {
        DOWN_X(0, "down_x", BlockFace.DOWN), EAST(1, "east", BlockFace.EAST), WEST(2, "west", BlockFace.WEST), SOUTH(3, "south", BlockFace.SOUTH), NORTH(4, "north", BlockFace.NORTH), UP_Z(5, "up_z", BlockFace.UP), UP_X(6, "up_x", BlockFace.UP), DOWN_Z(7, "down_z", BlockFace.DOWN);

        private val facing: BlockFace
        fun getFacing(): BlockFace {
            return facing
        }

        override fun toString(): String {
            return name
        }

        companion object {
            private val META_LOOKUP = arrayOfNulls<LeverOrientation>(values().size)
            fun byMetadata(meta: Int): LeverOrientation? {
                var meta = meta
                if (meta < 0 || meta >= META_LOOKUP.size) {
                    meta = 0
                }
                return META_LOOKUP[meta]
            }

            fun forFacings(clickedSide: BlockFace, playerDirection: BlockFace): LeverOrientation {
                return when (clickedSide) {
                    DOWN -> when (playerDirection.getAxis()) {
                        X -> DOWN_X
                        Z -> DOWN_Z
                        else -> throw IllegalArgumentException("Invalid entityFacing $playerDirection for facing $clickedSide")
                    }
                    UP -> when (playerDirection.getAxis()) {
                        X -> UP_X
                        Z -> UP_Z
                        else -> throw IllegalArgumentException("Invalid entityFacing $playerDirection for facing $clickedSide")
                    }
                    NORTH -> NORTH
                    SOUTH -> SOUTH
                    WEST -> WEST
                    EAST -> EAST
                    else -> throw IllegalArgumentException("Invalid facing: $clickedSide")
                }
            }

            init {
                for (face in values()) {
                    META_LOOKUP[cn.nukkit.block.face.getMetadata()] = cn.nukkit.block.face
                }
            }
        }

        init {
            facing = face
        }
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 2

    @Override
    override fun canBeFlowedInto(): Boolean {
        return false
    }

    @get:Override
    @get:PowerNukkitDifference(info = "Fixed the directions", since = "1.3.0.0-PN")
    val blockFace: BlockFace
        get() = leverOrientation!!.getFacing()

    @get:Override
    override val color: BlockColor
        get() = BlockColor.AIR_BLOCK_COLOR

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val LEVER_DIRECTION: ArrayBlockProperty<LeverOrientation> = ArrayBlockProperty("lever_direction", false,
                LeverOrientation.values(), 3, "lever_direction", false, arrayOf(
                "down_east_west", "east", "west", "south", "north", "up_north_south", "up_east_west", "down_north_south"
        ))

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(LEVER_DIRECTION, OPEN)

        /**
         * Check if the given block and its block face is a valid support for a lever
         * @param support The block that the lever is being placed against
         * @param face The face that the torch will be touching the block
         * @return If the support and face combinations can hold the lever
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun isSupportValid(support: Block, face: BlockFace?): Boolean {
            when (support.getId()) {
                FARMLAND, GRASS_PATH -> return true
                else -> {
                }
            }
            if (face === BlockFace.DOWN) {
                return support.isSolid(BlockFace.DOWN) && (support.isFullBlock() || !support.isTransparent())
            }
            if (support.isSolid(face)) {
                return true
            }
            return if (support is BlockWallBase || support is BlockFence) {
                face === BlockFace.UP
            } else false
        }
    }
}