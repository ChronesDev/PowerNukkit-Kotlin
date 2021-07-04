package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
abstract class BlockDoor protected constructor(meta: Int) : BlockTransparentMeta(meta), RedstoneComponent, Faceable {
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
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Override
    override val isSolid: Boolean
        get() = false

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace?): Boolean {
        return false
    }

    @get:DeprecationDetails(reason = "Limited amount of state data", since = "1.4.0.0-PN", replaceWith = "getCurrentState()")
    @get:Deprecated
    val fullDamage: Int
        get() = getSignedBigDamage()

    @Override
    protected override fun recalculateBoundingBox(): AxisAlignedBB {
        val position: BlockFace = blockFace.getOpposite()
        val isOpen = isOpen
        val isRight = isRightHinged
        return if (isOpen) {
            recalculateBoundingBoxWithPos(if (isRight) position.rotateYCCW() else position.rotateY())
        } else {
            recalculateBoundingBoxWithPos(position)
        }
    }

    private fun recalculateBoundingBoxWithPos(pos: BlockFace): AxisAlignedBB {
        return if (pos.getAxisDirection() === AxisDirection.NEGATIVE) {
            SimpleAxisAlignedBB(
                    this.x,
                    this.y,
                    this.z,
                    this.x + 1 + pos.getXOffset() - THICKNESS * pos.getXOffset(),
                    this.y + 1,
                    this.z + 1 + pos.getZOffset() - THICKNESS * pos.getZOffset()
            )
        } else {
            SimpleAxisAlignedBB(
                    this.x + pos.getXOffset() - THICKNESS * pos.getXOffset(),
                    this.y,
                    this.z + pos.getZOffset() - THICKNESS * pos.getZOffset(),
                    this.x + 1,
                    this.y + 1,
                    this.z + 1
            )
        }
    }

    @PowerNukkitDifference(info = "Will drop the iron door item if the support is broken", since = "1.3.1.2-PN")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            onNormalUpdate()
            return type
        }
        if (type == Level.BLOCK_UPDATE_REDSTONE && level.getServer().isRedstoneEnabled()) {
            onRedstoneUpdate()
            return type
        }
        return 0
    }

    private fun onNormalUpdate() {
        val down: Block = this.down()
        if (isTop) {
            if (down.getId() !== this.getId() || down.getBooleanValue(UPPER_BLOCK)) {
                level.setBlock(this, Block.get(AIR), false)
            }

            /* Doesn't work with redstone
            boolean downIsOpen = down.getBooleanValue(OPEN);
            if (downIsOpen != isOpen()) {
                setOpen(downIsOpen);
                level.setBlock(this, this, false, true);
            }*/return
        }
        if (down.getId() === AIR) {
            level.useBreakOn(this, if (getToolType() === ItemTool.TYPE_PICKAXE) Item.get(ItemID.DIAMOND_PICKAXE) else null)
        }
    }

    @PowerNukkitDifference(info = "Checking if the door was opened/closed manually.")
    private fun onRedstoneUpdate() {
        if (isOpen != isGettingPower && !manualOverride) {
            if (isOpen != isGettingPower) {
                level.getServer().getPluginManager().callEvent(BlockRedstoneEvent(this, if (isOpen) 15 else 0, if (isOpen) 0 else 15))
                setOpen(null, isGettingPower)
            }
        } else if (manualOverride && isGettingPower == isOpen) {
            manualOverride = false
        }
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var manualOverride: Boolean
        get() {
            val down: Location
            val up: Location
            if (isTop) {
                down = down().getLocation()
                up = getLocation()
            } else {
                down = getLocation()
                up = up().getLocation()
            }
            return manualOverrides.contains(up) || manualOverrides.contains(down)
        }
        set(val) {
            val down: Location
            val up: Location
            if (isTop) {
                down = down().getLocation()
                up = getLocation()
            } else {
                down = getLocation()
                up = up().getLocation()
            }
            if (`val`) {
                manualOverrides.add(up)
                manualOverrides.add(down)
            } else {
                manualOverrides.remove(up)
                manualOverrides.remove(down)
            }
        }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @get:Override
    override val isGettingPower: Boolean
        get() {
            val down: Location
            val up: Location
            if (isTop) {
                down = down().getLocation()
                up = getLocation()
            } else {
                down = getLocation()
                up = up().getLocation()
            }
            for (side in BlockFace.values()) {
                val blockDown: Block = down.getSide(side).getLevelBlock()
                val blockUp: Block = up.getSide(side).getLevelBlock()
                if (this.level.isSidePowered(blockDown.getLocation(), side)
                        || this.level.isSidePowered(blockUp.getLocation(), side)) {
                    return true
                }
            }
            return this.level.isBlockPowered(down) || this.level.isBlockPowered(up)
        }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, @Nullable player: Player): Boolean {
        if (this.y > 254 || face !== BlockFace.UP) {
            return false
        }
        val blockUp: Block = this.up()
        val blockDown: Block = this.down()
        if (!blockUp.canBeReplaced() || !blockDown.isSolid(BlockFace.UP) && blockDown !is BlockCauldron) {
            return false
        }
        val direction: BlockFace = player.getDirection()
        blockFace = direction
        val left: Block = this.getSide(direction.rotateYCCW())
        val right: Block = this.getSide(direction.rotateY())
        if (left.getId() === this.getId() || !right.isTransparent() && left.isTransparent()) { //Door hinge
            isRightHinged = true
        }
        isTop = false
        level.setBlock(block, this, true, false) //Bottom
        if (blockUp is BlockLiquid && blockUp.usesWaterLogging()) {
            level.setBlock(blockUp, 1, blockUp, true, false)
        }
        val doorTop = clone() as BlockDoor
        doorTop.y++
        doorTop.isTop = true
        level.setBlock(doorTop, doorTop, true, true) //Top
        level.updateAround(block)
        if (level.getServer().isRedstoneEnabled() && !isOpen && isGettingPower) {
            setOpen(null, true)
        }
        return true
    }

    @Override
    override fun onBreak(item: Item?): Boolean {
        manualOverride = false
        if (isTop) {
            val down: Block = this.down()
            if (down.getId() === this.getId() && !down.getBooleanValue(UPPER_BLOCK)) {
                level.setBlock(down, Block.get(AIR), true)
            }
        } else {
            val up: Block = this.up()
            if (up.getId() === this.getId() && up.getBooleanValue(UPPER_BLOCK)) {
                level.setBlock(up, Block.get(BlockID.AIR), true)
            }
        }
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true)
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        return toggle(player)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun playOpenCloseSound() {
        if (isTop && down() is BlockDoor) {
            if ((down() as BlockDoor).isOpen) {
                playOpenSound()
            } else {
                playCloseSound()
            }
        } else if (up() is BlockDoor) {
            if (isOpen) {
                playOpenSound()
            } else {
                playCloseSound()
            }
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

    @PowerNukkitDifference(info = "Just call the #setOpen() method.", since = "1.4.0.0-PN")
    fun toggle(player: Player?): Boolean {
        return setOpen(player, !isOpen)
    }

    @PowerNukkitDifference(info = "Using direct values, instead of toggling (fixes a redstone bug, that the door won't open). " +
            "Also adding possibility to detect, whether a player or redstone recently opened/closed the door.", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    fun setOpen(player: Player?, open: Boolean): Boolean {
        var player: Player? = player
        if (open == isOpen) {
            return false
        }
        val event = DoorToggleEvent(this, player)
        level.getServer().getPluginManager().callEvent(event)
        if (event.isCancelled()) {
            return false
        }
        player = event.getPlayer()
        val down: Block
        val up: Block
        if (isTop) {
            down = down()
            up = this
        } else {
            down = this
            up = up()
        }
        up.setBooleanValue(OPEN, open)
        up.level.setBlock(up, up, true, true)
        down.setBooleanValue(OPEN, open)
        down.level.setBlock(down, down, true, true)
        if (player != null) {
            manualOverride = isGettingPower || isOpen
        }
        playOpenCloseSound()
        return true
    }

    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isOpen: Boolean
        get() = getBooleanValue(OPEN)
        set(open) {
            setBooleanValue(OPEN, open)
        }

    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isTop: Boolean
        get() = getBooleanValue(UPPER_BLOCK)
        set(top) {
            setBooleanValue(UPPER_BLOCK, top)
        }

    @Deprecated
    @DeprecationDetails(reason = "Use the properties API instead", since = "1.4.0.0-PN")
    fun isTop(meta: Int): Boolean {
        return PROPERTIES.getBooleanValue(meta, UPPER_BLOCK.getName())
    }

    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isRightHinged: Boolean
        get() = getBooleanValue(DOOR_HINGE)
        set(rightHinged) {
            setBooleanValue(DOOR_HINGE, rightHinged)
        }

    @get:Override
    @set:Override
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var blockFace: BlockFace
        get() = getPropertyValue(DOOR_DIRECTION)
        set(face) {
            setPropertyValue(DOOR_DIRECTION, face)
        }

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @Override
    override fun sticksToPiston(): Boolean {
        return false
    }

    companion object {
        private const val THICKNESS = 3.0 / 16

        // Contains a list of positions of doors, which have been opened by hand (by a player).
        // It is used to detect on redstone update, if the door should be closed if redstone is off on the update,
        // previously the door always closed, when placing an unpowered redstone at the door, this fixes it
        // and gives the vanilla behavior; no idea how to make this better :d
        private val manualOverrides: List<Location> = ArrayList()

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val DOOR_HINGE: BooleanBlockProperty = BooleanBlockProperty("door_hinge_bit", false)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val DOOR_DIRECTION: BlockProperty<BlockFace> = ArrayBlockProperty("direction", false, arrayOf<BlockFace>(
                BlockFace.EAST, BlockFace.SOUTH,
                BlockFace.WEST, BlockFace.NORTH
        )).ordinal(true)
        protected val PROPERTIES: BlockProperties = BlockProperties(DOOR_DIRECTION, OPEN, UPPER_BLOCK, DOOR_HINGE)

        @Deprecated
        @DeprecationDetails(reason = "Use the accessors or properties instead", since = "1.4.0.0-PN", replaceWith = "CommonBlockProperties.OPEN")
        val DOOR_OPEN_BIT: Int = PROPERTIES.getOffset(OPEN.getName())

        @Deprecated
        @DeprecationDetails(reason = "Use the accessors or properties instead", since = "1.4.0.0-PN", replaceWith = "UPPER_BLOCK")
        val DOOR_TOP_BIT: Int = PROPERTIES.getOffset(UPPER_BLOCK.getName())

        @Deprecated
        @DeprecationDetails(reason = "Use the accessors or properties instead", since = "1.4.0.0-PN", replaceWith = "DOOR_HINGE")
        val DOOR_HINGE_BIT: Int = PROPERTIES.getOffset(DOOR_HINGE.getName())

        @Deprecated
        @DeprecationDetails(reason = "Was removed from the game", since = "1.4.0.0-PN", replaceWith = "#isGettingPower()")
        val DOOR_POWERED_BIT: Int = PROPERTIES.getBitSize()
    }
}