package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Pub4Game
 * @since 26.12.2015
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
class BlockTrapdoor @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta), RedstoneComponent, Faceable {
    @get:Override
    override val id: Int
        get() = TRAPDOOR

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Oak Trapdoor"

    @get:Override
    override val hardness: Double
        get() = 3

    @get:Override
    override val resistance: Double
        get() = 15

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    companion object {
        private const val THICKNESS = 0.1875

        // Contains a list of positions of trap doors, which have been opened by hand (by a player).
        // It is used to detect on redstone update, if the door should be closed if redstone is off on the update,
        // previously the door always closed, when placing an unpowered redstone at the door, this fixes it
        // and gives the vanilla behavior; no idea how to make this better :d
        private val manualOverrides: List<Location> = ArrayList()

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val TRAPDOOR_DIRECTION: BlockProperty<BlockFace> = ArrayBlockProperty("direction", false, arrayOf<BlockFace>( // It's basically weirdo_direction but renamed
                BlockFace.EAST, BlockFace.WEST,
                BlockFace.SOUTH, BlockFace.NORTH
        )).ordinal(true)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(TRAPDOOR_DIRECTION, UPSIDE_DOWN, OPEN)
        private val boundingBoxDamage: Array<AxisAlignedBB?> = arrayOfNulls<AxisAlignedBB>(0x1 shl PROPERTIES.getBitSize())

        @Deprecated
        @DeprecationDetails(reason = "Use the properties or the accessors", since = "1.4.0.0-PN", replaceWith = "CommonBlockProperties.OPEN")
        val TRAPDOOR_OPEN_BIT = 0x08

        @Deprecated
        @DeprecationDetails(reason = "Use the properties or the accessors", since = "1.4.0.0-PN", replaceWith = "BlockStairs.UPSIDE_DOWN")
        val TRAPDOOR_TOP_BIT = 0x04

        //<editor-fold desc="pre-computing the bounding boxes" defaultstate="collapsed">
        init {
            for (damage in boundingBoxDamage.indices) {
                var bb: AxisAlignedBB
                if (PROPERTIES.getBooleanValue(damage, OPEN.getName())) {
                    val face: BlockFace = PROPERTIES.getValue(damage, TRAPDOOR_DIRECTION.getName()) as BlockFace
                    cn.nukkit.block.face = cn.nukkit.block.face.getOpposite()
                    if (cn.nukkit.block.face.getAxisDirection() === AxisDirection.NEGATIVE) {
                        cn.nukkit.block.bb = SimpleAxisAlignedBB(
                                0,
                                0,
                                0,
                                1 + cn.nukkit.block.face.getXOffset() - THICKNESS * cn.nukkit.block.face.getXOffset(),
                                1,
                                1 + cn.nukkit.block.face.getZOffset() - THICKNESS * cn.nukkit.block.face.getZOffset()
                        )
                    } else {
                        cn.nukkit.block.bb = SimpleAxisAlignedBB(
                                cn.nukkit.block.face.getXOffset() - THICKNESS * cn.nukkit.block.face.getXOffset(),
                                0,
                                cn.nukkit.block.face.getZOffset() - THICKNESS * cn.nukkit.block.face.getZOffset(),
                                1,
                                1,
                                1
                        )
                    }
                } else if (PROPERTIES.getBooleanValue(damage, UPSIDE_DOWN.getName())) {
                    cn.nukkit.block.bb = SimpleAxisAlignedBB(
                            0,
                            1 - THICKNESS,
                            0,
                            1,
                            1,
                            1
                    )
                } else {
                    cn.nukkit.block.bb = SimpleAxisAlignedBB(
                            0,
                            0,
                            0,
                            1,
                            0 + THICKNESS,
                            1
                    )
                }
                boundingBoxDamage[damage] = cn.nukkit.block.bb
            }
        }
    }

    //</editor-fold>
    @get:PowerNukkitDifference(info = "The bounding box was fixed", since = "1.3.0.0-PN")
    private val relativeBoundingBox: AxisAlignedBB?
        private get() {
            @SuppressWarnings("deprecation") val bigDamage: Int = getSignedBigDamage()
            return boundingBoxDamage[bigDamage]
        }

    @get:Override
    override val minX: Double
        get() = this.x + relativeBoundingBox.getMinX()

    @get:Override
    override val maxX: Double
        get() = this.x + relativeBoundingBox.getMaxX()

    @get:Override
    override val minY: Double
        get() = this.y + relativeBoundingBox.getMinY()

    @get:Override
    override val maxY: Double
        get() = this.y + relativeBoundingBox.getMaxY()

    @get:Override
    override val minZ: Double
        get() = this.z + relativeBoundingBox.getMinZ()

    @get:Override
    override val maxZ: Double
        get() = this.z + relativeBoundingBox.getMaxZ()

    @PowerNukkitDifference(info = "Checking if the door was opened/closed manually and using new powered checks.", since = "1.4.0.0-PN")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_REDSTONE && this.level.getServer().isRedstoneEnabled()) {
            if (isOpen != this.isGettingPower() && !manualOverride) {
                if (isOpen != this.isGettingPower()) {
                    level.getServer().getPluginManager().callEvent(BlockRedstoneEvent(this, if (isOpen) 15 else 0, if (isOpen) 0 else 15))
                    setOpen(null, this.isGettingPower())
                }
            } else if (manualOverride && this.isGettingPower() === this.isOpen()) {
                manualOverride = false
            }
            return type
        }
        return 0
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

    @PowerNukkitDifference(info = "Will return false if setBlock fails and the direction is relative to where the player is facing", since = "1.4.0.0-PN")
    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        if (face.getAxis().isHorizontal()) {
            blockFace = if (player == null) face else player.getDirection().getOpposite()
            isTop = fy > 0.5
        } else {
            blockFace = player.getDirection().getOpposite()
            isTop = face !== BlockFace.UP
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

    @PowerNukkitDifference(info = "Just call the #setOpen() method.", since = "1.4.0.0-PN")
    fun toggle(player: Player?): Boolean {
        return setOpen(player, !isOpen)
    }

    @PowerNukkitDifference(info = "Returns false if setBlock fails", since = "1.4.0.0-PN")
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
        setBooleanValue(OPEN, open)
        if (!level.setBlock(this, this, true, true)) return false
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
        this.level.addSound(this, Sound.RANDOM_DOOR_OPEN)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun playCloseSound() {
        this.level.addSound(this, Sound.RANDOM_DOOR_CLOSE)
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WOOD_BLOCK_COLOR

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
        get() = getBooleanValue(UPSIDE_DOWN)
        set(top) {
            setBooleanValue(UPSIDE_DOWN, top)
        }

    @get:Override
    @get:PowerNukkitDifference(info = "Was returning the wrong face", since = "1.3.0.0-PN")
    @set:Override
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var blockFace: BlockFace
        get() = getPropertyValue(TRAPDOOR_DIRECTION)
        set(face) {
            setPropertyValue(TRAPDOOR_DIRECTION, face)
        }
}