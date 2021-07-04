package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/11/23
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
class BlockFenceGate @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta), RedstoneComponent, Faceable {
    @get:Override
    override val id: Int
        get() = FENCE_GATE_OAK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Oak Fence Gate"

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val resistance: Double
        get() = 15

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    companion object {
        // Contains a list of positions of fence gates, which have been opened by hand (by a player).
        // It is used to detect on redstone update, if the gate should be closed if redstone is off on the update,
        // previously the gate always closed, when placing an unpowered redstone at the gate, this fixes it
        // and gives the vanilla behavior; no idea how to make this better :d
        private val manualOverrides: List<Location> = ArrayList()

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val IN_WALL: BooleanBlockProperty = BooleanBlockProperty("in_wall_bit", false)

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val PROPERTIES: BlockProperties = BlockProperties(DIRECTION, OPEN, IN_WALL)
        private val offMinX = DoubleArray(2)
        private val offMinZ = DoubleArray(2)
        private val offMaxX = DoubleArray(2)
        private val offMaxZ = DoubleArray(2)

        init {
            offMinX[0] = 0
            offMinZ[0] = 0.375
            offMaxX[0] = 1
            offMaxZ[0] = 0.625
            offMinX[1] = 0.375
            offMinZ[1] = 0
            offMaxX[1] = 0.625
            offMaxZ[1] = 1
        }
    }

    private val offsetIndex: Int
        private get() = when (blockFace) {
            SOUTH, NORTH -> 0
            else -> 1
        }

    @get:Override
    override val minX: Double
        get() = this.x + offMinX[offsetIndex]

    @get:Override
    override val minZ: Double
        get() = this.z + offMinZ[offsetIndex]

    @get:Override
    override val maxX: Double
        get() = this.x + offMaxX[offsetIndex]

    @get:Override
    override val maxZ: Double
        get() = this.z + offMaxZ[offsetIndex]

    @PowerNukkitDifference(info = "InWall property is now properly set, returns false if setBlock fails", since = "1.4.0.0-PN")
    @PowerNukkitDifference(info = "Open door if redstone signal is detected.", since = "1.4.0.0-PN")
    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player): Boolean {
        val direction: BlockFace = player.getDirection()
        blockFace = direction
        if (getSide(direction.rotateY()) is BlockWallBase
                || getSide(direction.rotateYCCW()) is BlockWallBase) {
            isInWall = true
        }
        if (!this.getLevel().setBlock(block, this, true, true)) {
            return false
        }
        if (level.getServer().isRedstoneEnabled() && !isOpen && this.isGettingPower()) {
            setOpen(null, true)
        }
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        return toggle(player)
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WOOD_BLOCK_COLOR

    @PowerNukkitDifference(info = "Just call the #setOpen() method.", since = "1.4.0.0-PN")
    fun toggle(player: Player?): Boolean {
        return setOpen(player, !isOpen)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setOpen(player: Player?, open: Boolean): Boolean {
        var player: Player? = player
        if (open == isOpen) {
            return false
        }
        val event = DoorToggleEvent(this, player)
        this.getLevel().getServer().getPluginManager().callEvent(event)
        if (event.isCancelled()) {
            return false
        }
        player = event.getPlayer()
        val direction: BlockFace
        val originDirection: BlockFace = blockFace
        if (player != null) {
            val yaw: Double = player.yaw
            var rotation = (yaw - 90) % 360
            if (rotation < 0) {
                rotation += 360.0
            }
            direction = if (originDirection.getAxis() === BlockFace.Axis.Z) {
                if (rotation >= 0 && rotation < 180) {
                    BlockFace.NORTH
                } else {
                    BlockFace.SOUTH
                }
            } else {
                if (rotation >= 90 && rotation < 270) {
                    BlockFace.EAST
                } else {
                    BlockFace.WEST
                }
            }
        } else {
            direction = if (originDirection.getAxis() === BlockFace.Axis.Z) {
                BlockFace.SOUTH
            } else {
                BlockFace.WEST
            }
        }
        blockFace = direction
        toggleBooleanProperty(OPEN)
        this.level.setBlock(this, this, false, false)
        if (player != null) {
            manualOverride = this.isGettingPower() || isOpen
        }
        playOpenCloseSound()
        return true
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun playOpenCloseSound() {
        if (isOpen) {
            playOpenSound()
        } else {
            playCloseSound()
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun playOpenSound() {
        level.addSound(this, Sound.RANDOM_DOOR_OPEN)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun playCloseSound() {
        level.addSound(this, Sound.RANDOM_DOOR_CLOSE)
    }

    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isOpen: Boolean
        get() = getBooleanValue(OPEN)
        set(open) {
            setBooleanValue(OPEN, open)
        }

    @PowerNukkitDifference(info = "Will connect to walls correctly", since = "1.4.0.0-PN")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val face: BlockFace = blockFace
            val touchingWall = getSide(face.rotateY()) is BlockWallBase || getSide(face.rotateYCCW()) is BlockWallBase
            if (touchingWall != isInWall) {
                isInWall = touchingWall
                level.setBlock(this, this, true)
                return type
            }
        } else if (type == Level.BLOCK_UPDATE_REDSTONE && this.level.getServer().isRedstoneEnabled()) {
            onRedstoneUpdate()
            return type
        }
        return 0
    }

    @PowerNukkitDifference(info = "Checking if the door was opened/closed manually.", since = "1.4.0.0-PN")
    private fun onRedstoneUpdate() {
        if (isOpen != this.isGettingPower() && !manualOverride) {
            if (isOpen != this.isGettingPower()) {
                level.getServer().getPluginManager().callEvent(BlockRedstoneEvent(this, if (isOpen) 15 else 0, if (isOpen) 0 else 15))
                setOpen(null, this.isGettingPower())
            }
        } else if (manualOverride && this.isGettingPower() === this.isOpen()) {
            manualOverride = false
        }
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var manualOverride: Boolean
        get() = manualOverrides.contains(this.getLocation())
        set(val) {
            if (`val`) {
                manualOverrides.add(this.getLocation())
            } else {
                manualOverrides.remove(this.getLocation())
            }
        }

    @Override
    override fun onBreak(item: Item?): Boolean {
        manualOverride = false
        return super.onBreak(item)
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isInWall: Boolean
        get() = getBooleanValue(IN_WALL)
        set(inWall) {
            setBooleanValue(IN_WALL, inWall)
        }

    @get:Override
    @set:Override
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var blockFace: BlockFace
        get() = getPropertyValue(DIRECTION)
        set(face) {
            setPropertyValue(DIRECTION, face)
        }
}