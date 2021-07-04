package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Snake1999
 * @since 2016/1/11
 */
class BlockRail @JvmOverloads constructor(meta: Int = 0) : BlockFlowable(meta), Faceable {
    // 0x8: Set the block active
    // 0x7: Reset the block to normal
    // If the rail can be powered. So its a complex rail!
    protected var canBePowered = false

    @get:Override
    override val name: String
        get() = "Rail"

    @get:Override
    override val id: Int
        get() = RAIL

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val hardness: Double
        get() = 0.7

    @get:Override
    override val resistance: Double
        get() = 3.5

    @Override
    override fun canPassThrough(): Boolean {
        return true
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val ascendingDirection: Optional<BlockFace> = orientation.ascendingDirection()
            if (!checkCanBePlace(this.down()) || ascendingDirection.isPresent() && !checkCanBePlace(this.getSide(ascendingDirection.get()))) {
                this.getLevel().useBreakOn(this)
                return Level.BLOCK_UPDATE_NORMAL
            }
        }
        return 0
    }

    @get:Override
    override val maxY: Double
        get() = this.y + 0.125

    @Override
    override fun recalculateBoundingBox(): AxisAlignedBB {
        return this
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.AIR_BLOCK_COLOR

    //Information from http://minecraft.gamepedia.com/Rail
    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        val down: Block = this.down()
        if (!checkCanBePlace(down)) {
            return false
        }
        val railsAround: Map<BlockRail, BlockFace> = checkRailsAroundAffected()
        val rails: List<BlockRail> = ArrayList(railsAround.keySet())
        val faces: List<BlockFace> = ArrayList(railsAround.values())
        if (railsAround.size() === 1) {
            val other = rails[0]
            railDirection = this.connect(other, railsAround[other])
        } else if (railsAround.size() === 4) {
            if (isAbstract) {
                railDirection = this.connect(rails[faces.indexOf(SOUTH)], SOUTH, rails[faces.indexOf(EAST)], EAST)
            } else {
                railDirection = this.connect(rails[faces.indexOf(EAST)], EAST, rails[faces.indexOf(WEST)], WEST)
            }
        } else if (!railsAround.isEmpty()) {
            if (isAbstract) {
                if (railsAround.size() === 2) {
                    val rail1 = rails[0]
                    val rail2 = rails[1]
                    railDirection = this.connect(rail1, railsAround[rail1], rail2, railsAround[rail2])
                } else {
                    val cd: List<BlockFace> = Stream.of(CURVED_SOUTH_EAST, CURVED_NORTH_EAST, CURVED_SOUTH_WEST)
                            .filter { o -> faces.containsAll(o.connectingDirections()) }
                            .findFirst().get().connectingDirections()
                    val f1: BlockFace = cd[0]
                    val f2: BlockFace = cd[1]
                    railDirection = this.connect(rails[faces.indexOf(f1)], f1, rails[faces.indexOf(f2)], f2)
                }
            } else {
                val f: BlockFace = faces.stream().min { f1, f2 -> if (f1.getIndex() < f2.getIndex()) 1 else if (x === y) 0 else -1 }.get()
                val fo: BlockFace = f.getOpposite()
                if (faces.contains(fo)) { //Opposite connectable
                    railDirection = this.connect(rails[faces.indexOf(f)], f, rails[faces.indexOf(fo)], fo)
                } else {
                    railDirection = this.connect(rails[faces.indexOf(f)], f)
                }
            }
        }
        this.level.setBlock(this, this, true, true)
        if (!isAbstract) {
            level.scheduleUpdate(this, this, 0)
        }
        return true
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    private fun checkCanBePlace(check: Block?): Boolean {
        return if (check == null) {
            false
        } else check.isSolid(UP) || check is BlockCauldron
    }

    private fun connect(rail1: BlockRail, face1: BlockFace?, rail2: BlockRail, face2: BlockFace?): Orientation {
        this.connect(rail1, face1)
        this.connect(rail2, face2)
        if (face1.getOpposite() === face2) {
            val delta1 = (this.y - rail1.y) as Int
            val delta2 = (this.y - rail2.y) as Int
            if (delta1 == -1) {
                return Orientation.ascending(face1)
            } else if (delta2 == -1) {
                return Orientation.ascending(face2)
            }
        }
        return straightOrCurved(face1, face2)
    }

    private fun connect(other: BlockRail, face: BlockFace?): Orientation {
        val delta = (this.y - other.y) as Int
        val rails: Map<BlockRail, BlockFace> = other.checkRailsConnected()
        if (rails.isEmpty()) { //Only one
            other.orientation = if (delta == 1) ascending(face.getOpposite()) else straight(face)
            return if (delta == -1) ascending(face) else straight(face)
        } else if (rails.size() === 1) { //Already connected
            val faceConnected: BlockFace = rails.values().iterator().next()
            if (other.isAbstract && faceConnected !== face) { //Curve!
                other.orientation = curved(face.getOpposite(), faceConnected)
                return if (delta == -1) ascending(face) else straight(face)
            } else if (faceConnected === face) { //Turn!
                if (!other.orientation.isAscending()) {
                    other.orientation = if (delta == 1) ascending(face.getOpposite()) else straight(face)
                }
                return if (delta == -1) ascending(face) else straight(face)
            } else if (other.orientation.hasConnectingDirections(NORTH, SOUTH)) { //North-south
                other.orientation = if (delta == 1) ascending(face.getOpposite()) else straight(face)
                return if (delta == -1) ascending(face) else straight(face)
            }
        }
        return STRAIGHT_NORTH_SOUTH
    }

    private fun checkRailsAroundAffected(): Map<BlockRail, BlockFace> {
        val railsAround: Map<BlockRail, BlockFace> = checkRailsAround(Arrays.asList(SOUTH, EAST, WEST, NORTH))
        return railsAround.keySet().stream()
                .filter { r -> r.checkRailsConnected().size() !== 2 }
                .collect(Collectors.toMap({ r -> r }, railsAround::get))
    }

    private fun checkRailsAround(faces: Collection<BlockFace>): Map<BlockRail, BlockFace> {
        val result: Map<BlockRail, BlockFace> = HashMap()
        faces.forEach { f ->
            val b: Block = this.getSide(f)
            Stream.of(b, b.up(), b.down())
                    .filter(Rail::isRailBlock)
                    .forEach { block -> result.put(block as BlockRail, f) }
        }
        return result
    }

    protected fun checkRailsConnected(): Map<BlockRail, BlockFace> {
        val railsAround: Map<BlockRail, BlockFace> = checkRailsAround(orientation.connectingDirections())
        return railsAround.keySet().stream()
                .filter { r -> r.getOrientation().hasConnectingDirections(railsAround[r].getOpposite()) }
                .collect(Collectors.toMap({ r -> r }, railsAround::get))
    }

    val isAbstract: Boolean
        get() = id == RAIL

    fun canPowered(): Boolean {
        return canBePowered
    }

    /**
     * Changes the rail direction without changing anything else.
     * @param orientation The new orientation
     */
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var railDirection: Orientation
        get() = orientation
        set(orientation) {
            setPropertyValue(CURVED_RAIL_DIRECTION.getName(), orientation)
        }

    /**
     * Changes the rail direction and update the state in the world if the orientation changed in a single call.
     *
     * Note that the level block won't change if the current block has already the given orientation.
     *
     * @see .setRailDirection
     * @see Level.setBlock
     */
    var orientation: Orientation
        get() = getPropertyValue(CURVED_RAIL_DIRECTION.getName()) as Orientation
        set(o) {
            if (o !== orientation) {
                railDirection = o
                this.level.setBlock(this, this, false, true)
            }
        }

    // Check if this can be powered
    // Avoid modifying the value from meta (The rail orientation may be false)
    // Reason: When the rail is curved, the meta will return STRAIGHT_NORTH_SOUTH.
    // OR Null Pointer Exception
    // Return the default: This meta
    @get:DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "This hack is no longer needed after the block state implementation and is no longer maintained")
    @get:Deprecated
    val realMeta: Int
        get() =// Check if this can be powered
        // Avoid modifying the value from meta (The rail orientation may be false)
        // Reason: When the rail is curved, the meta will return STRAIGHT_NORTH_SOUTH.
                // OR Null Pointer Exception
            if (!isAbstract) {
                getDamage() and 0x7
            } else getDamage()
    // Return the default: This meta
    /**
     * Changes the active flag and update the state in the world in a single call.
     *
     * The active flag will not change if the block state don't have the [.ACTIVE] property,
     * and it will not throw exceptions related to missing block properties.
     *
     * The level block will always update.
     *
     * @see .setRailDirection
     * @see Level.setBlock
     */
    var isActive: Boolean
        get() = properties.contains(ACTIVE) && getBooleanValue(ACTIVE)
        set(active) {
            if (properties.contains(ACTIVE)) {
                isRailActive = active
            }
            level.setBlock(this, this, true, true)
        }

    /**
     * @throws NoSuchElementException If attempt to set the rail to active but it don't have the [.ACTIVE] property.
     */
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Throws(NoSuchElementException::class)
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isRailActive: OptionalBoolean
        get() = if (properties.contains(ACTIVE)) OptionalBoolean.of(getBooleanValue(ACTIVE)) else OptionalBoolean.empty()
        set(active) {
            if (!active && !properties.contains(ACTIVE)) {
                return
            }
            setBooleanValue(ACTIVE, active)
        }

    @Override
    override fun toItem(): Item {
        return ItemBlock(this, 0)
    }

    @get:Override
    val blockFace: BlockFace
        get() = BlockFace.fromHorizontalIndex(this.getDamage() and 0x07)

    @Override
    override fun canBePushed(): Boolean {
        return true
    }

    @Override
    override fun canBePulled(): Boolean {
        return true
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val ACTIVE: BooleanBlockProperty = BooleanBlockProperty("rail_data_bit", false)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val UNCURVED_RAIL_DIRECTION: BlockProperty<Rail.Orientation> = ArrayBlockProperty("rail_direction", false, arrayOf<Rail.Orientation>(
                STRAIGHT_NORTH_SOUTH, STRAIGHT_EAST_WEST,
                ASCENDING_EAST, ASCENDING_WEST,
                ASCENDING_NORTH, ASCENDING_SOUTH
        )).ordinal(true)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val CURVED_RAIL_DIRECTION: BlockProperty<Rail.Orientation> = ArrayBlockProperty("rail_direction", false, arrayOf<Rail.Orientation>(
                STRAIGHT_NORTH_SOUTH, STRAIGHT_EAST_WEST,
                ASCENDING_EAST, ASCENDING_WEST,
                ASCENDING_NORTH, ASCENDING_SOUTH,
                CURVED_SOUTH_EAST, CURVED_SOUTH_WEST,
                CURVED_NORTH_WEST, CURVED_NORTH_EAST
        )).ordinal(true)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val ACTIVABLE_PROPERTIES: BlockProperties = BlockProperties(UNCURVED_RAIL_DIRECTION, ACTIVE)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(CURVED_RAIL_DIRECTION)
    }
}