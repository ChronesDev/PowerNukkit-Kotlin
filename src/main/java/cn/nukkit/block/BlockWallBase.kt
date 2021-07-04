package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@Log4j2
abstract class BlockWallBase @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockTransparentMeta(meta), BlockConnectable {
    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val isSolid: Boolean
        get() = false

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace?): Boolean {
        return false
    }

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val resistance: Double
        get() = 30

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    private fun shouldBeTall(above: Block, face: BlockFace): Boolean {
        return when (above.getId()) {
            AIR, SKULL_BLOCK -> false
            BELL -> {
                val bell: BlockBell = above as BlockBell
                (bell.getAttachment() === AttachmentType.STANDING
                        && bell.getBlockFace().getAxis() !== face.getAxis())
            }
            else -> {
                if (above is BlockWallBase) {
                    return above.getConnectionType(face) !== WallConnectionType.NONE
                } else if (above is BlockConnectable) {
                    return (above as BlockConnectable).isConnected(face)
                } else if (above is BlockPressurePlateBase || above is BlockStairs) {
                    return true
                }
                above.isSolid() && !above.isTransparent() || shouldBeTallBasedOnBoundingBox(above, face)
            }
        }
    }

    private fun shouldBeTallBasedOnBoundingBox(above: Block, face: BlockFace): Boolean {
        var boundingBox: AxisAlignedBB = above.getBoundingBox()
        if (boundingBox == null) {
            return false
        }
        boundingBox = boundingBox.getOffsetBoundingBox(-above.x, -above.y, -above.z)
        if (boundingBox.getMinY() > 0) {
            return false
        }
        var offset: Int = face.getXOffset()
        if (offset < 0) {
            return boundingBox.getMinX() < MIN_POST_BB && boundingBox.getMinZ() < MIN_POST_BB && MAX_POST_BB < boundingBox.getMaxZ()
        } else if (offset > 0) {
            return MAX_POST_BB < boundingBox.getMaxX() && MAX_POST_BB < boundingBox.getMaxZ() && boundingBox.getMinZ() < MAX_POST_BB
        } else {
            offset = face.getZOffset()
            if (offset < 0) {
                return boundingBox.getMinZ() < MIN_POST_BB && boundingBox.getMinX() < MIN_POST_BB && MIN_POST_BB < boundingBox.getMaxX()
            } else if (offset > 0) {
                return MAX_POST_BB < boundingBox.getMaxZ() && MAX_POST_BB < boundingBox.getMaxX() && boundingBox.getMinX() < MAX_POST_BB
            }
        }
        return false
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun autoConfigureState(): Boolean {
        val previousMeta: Number = getDataStorage()
        isWallPost = true
        val above: Block = up(1, 0)
        for (blockFace in BlockFace.Plane.HORIZONTAL) {
            val side: Block = getSideAtLayer(0, blockFace)
            if (canConnect(side)) {
                try {
                    connect(blockFace, above, false)
                } catch (e: RuntimeException) {
                    log.error("Failed to connect the block {} at {} to {} which is {} at {}",
                            this, getLocation(), blockFace, side, side.getLocation(), e)
                    throw e
                }
            } else {
                disconnect(blockFace)
            }
        }
        recheckPostConditions(above)
        return !getDataStorage().equals(previousMeta)
    }

    @PowerNukkitDifference(info = "Will connect as expected", since = "1.4.0.0-PN")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (autoConfigureState()) {
                level.setBlock(this, this, true)
            }
            return type
        }
        return 0
    }

    @PowerNukkitDifference(info = "Will be placed on the right state", since = "1.4.0.0-PN")
    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        autoConfigureState()
        return super.place(item, block, target, face, fx, fy, fz, player)
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isWallPost: Boolean
        get() = getBooleanValue(WALL_POST_BIT)
        set(wallPost) {
            setBooleanValue(WALL_POST_BIT, wallPost)
        }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun clearConnections() {
        setPropertyValue(WALL_CONNECTION_TYPE_EAST, WallConnectionType.NONE)
        setPropertyValue(WALL_CONNECTION_TYPE_WEST, WallConnectionType.NONE)
        setPropertyValue(WALL_CONNECTION_TYPE_NORTH, WallConnectionType.NONE)
        setPropertyValue(WALL_CONNECTION_TYPE_SOUTH, WallConnectionType.NONE)
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val wallConnections: Map<Any, Any>
        get() {
            val connections: EnumMap<BlockFace, WallConnectionType> = EnumMap(BlockFace::class.java)
            for (blockFace in BlockFace.Plane.HORIZONTAL) {
                val connectionType: WallConnectionType = getConnectionType(blockFace)
                if (connectionType !== WallConnectionType.NONE) {
                    connections.put(blockFace, connectionType)
                }
            }
            return connections
        }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getConnectionType(blockFace: BlockFace?): WallConnectionType {
        return when (blockFace) {
            NORTH -> getPropertyValue(WALL_CONNECTION_TYPE_NORTH)
            SOUTH -> getPropertyValue(WALL_CONNECTION_TYPE_SOUTH)
            WEST -> getPropertyValue(WALL_CONNECTION_TYPE_WEST)
            EAST -> getPropertyValue(WALL_CONNECTION_TYPE_EAST)
            else -> WallConnectionType.NONE
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setConnection(blockFace: BlockFace?, type: WallConnectionType?): Boolean {
        return when (blockFace) {
            NORTH -> {
                setPropertyValue(WALL_CONNECTION_TYPE_NORTH, type)
                true
            }
            SOUTH -> {
                setPropertyValue(WALL_CONNECTION_TYPE_SOUTH, type)
                true
            }
            WEST -> {
                setPropertyValue(WALL_CONNECTION_TYPE_WEST, type)
                true
            }
            EAST -> {
                setPropertyValue(WALL_CONNECTION_TYPE_EAST, type)
                true
            }
            else -> false
        }
    }

    /**
     * @return true if it should be a post
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun autoUpdatePostFlag() {
        isWallPost = recheckPostConditions(up(1, 0))
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun hasConnections(): Boolean {
        return getPropertyValue(WALL_CONNECTION_TYPE_EAST) !== WallConnectionType.NONE || getPropertyValue(WALL_CONNECTION_TYPE_WEST) !== WallConnectionType.NONE || getPropertyValue(WALL_CONNECTION_TYPE_NORTH) !== WallConnectionType.NONE || getPropertyValue(WALL_CONNECTION_TYPE_SOUTH) !== WallConnectionType.NONE
    }

    private fun recheckPostConditions(above: Block): Boolean {
        // If nothing is connected, it should be a post
        if (!hasConnections()) {
            return true
        }

        // If it's not straight, it should be a post
        val connections: Map<BlockFace, WallConnectionType> = wallConnections
        if (connections.size() !== 2) {
            return true
        }
        val iterator: Iterator<Map.Entry<BlockFace, WallConnectionType>> = connections.entrySet().iterator()
        val entryA: Map.Entry<BlockFace, WallConnectionType> = iterator.next()
        val entryB: Map.Entry<BlockFace, WallConnectionType> = iterator.next()
        if (entryA.getValue() !== entryB.getValue() || entryA.getKey().getOpposite() !== entryB.getKey()) {
            return true
        }
        val axis: BlockFace.Axis = entryA.getKey().getAxis()
        when (above.getId()) {
            FLOWER_POT_BLOCK, SKULL_BLOCK, CONDUIT, STANDING_BANNER, TURTLE_EGG -> return true
            END_ROD -> if ((above as Faceable).getBlockFace() === BlockFace.UP) {
                return true
            }
            BELL -> {
                val bell: BlockBell = above as BlockBell
                if (bell.getAttachment() === AttachmentType.STANDING
                        && bell.getBlockFace().getAxis() === axis) {
                    return true
                }
            }
            else -> if (above is BlockWallBase) {
                // If the wall above is a post, it should also be a post
                if (above.isWallPost) {
                    return true
                }
            } else if (above is BlockLantern) {
                // Lanterns makes this become a post if they are not hanging
                if (!above.isHanging()) {
                    return true
                }
            } else if (above.getId() === LEVER || above is BlockTorch || above is BlockButton) {
                // These blocks make this become a post if they are placed down (facing up)
                if ((above as Faceable).getBlockFace() === BlockFace.UP) {
                    return true
                }
            } else if (above is BlockFenceGate) {
                // If the gate don't follow the path, make it a post
                if ((above as Faceable).getBlockFace().getAxis() === axis) {
                    return true
                }
            } else if (above is BlockConnectable) {
                // If the connectable block above don't share 2 equal connections, then this should be a post
                var shared = 0
                for (connection in (above as BlockConnectable).getConnections()) {
                    if (connections.containsKey(connection) && ++shared == 2) {
                        break
                    }
                }
                if (shared < 2) {
                    return true
                }
            }
        }

        // Sign posts always makes the wall become a post
        return above is BlockSignPost
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val isSameHeightStraight: Boolean
        get() {
            val connections: Map<BlockFace, WallConnectionType> = wallConnections
            if (connections.size() !== 2) {
                return false
            }
            val iterator: Iterator<Map.Entry<BlockFace, WallConnectionType>> = connections.entrySet().iterator()
            val a: Map.Entry<BlockFace, WallConnectionType> = iterator.next()
            val b: Map.Entry<BlockFace, WallConnectionType> = iterator.next()
            return a.getValue() === b.getValue() && a.getKey().getOpposite() === b.getKey()
        }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun connect(blockFace: BlockFace): Boolean {
        return connect(blockFace, true)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun connect(blockFace: BlockFace, recheckPost: Boolean): Boolean {
        if (blockFace.getHorizontalIndex() < 0) {
            return false
        }
        val above: Block = getSideAtLayer(0, BlockFace.UP)
        return connect(blockFace, above, recheckPost)
    }

    private fun connect(blockFace: BlockFace, above: Block, recheckPost: Boolean): Boolean {
        val type: WallConnectionType = if (shouldBeTall(above, blockFace)) WallConnectionType.TALL else WallConnectionType.SHORT
        if (setConnection(blockFace, type)) {
            if (recheckPost) {
                recheckPostConditions(above)
            }
            return true
        }
        return false
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun disconnect(blockFace: BlockFace): Boolean {
        if (blockFace.getHorizontalIndex() < 0) {
            return false
        }
        if (setConnection(blockFace, WallConnectionType.NONE)) {
            autoUpdatePostFlag()
            return true
        }
        return false
    }

    @Override
    protected override fun recalculateBoundingBox(): AxisAlignedBB {
        val north = canConnect(this.getSide(BlockFace.NORTH))
        val south = canConnect(this.getSide(BlockFace.SOUTH))
        val west = canConnect(this.getSide(BlockFace.WEST))
        val east = canConnect(this.getSide(BlockFace.EAST))
        var n: Double = if (north) 0 else 0.25
        var s: Double = if (south) 1 else 0.75
        var w: Double = if (west) 0 else 0.25
        var e: Double = if (east) 1 else 0.75
        if (north && south && !west && !east) {
            w = 0.3125
            e = 0.6875
        } else if (!north && !south && west && east) {
            n = 0.3125
            s = 0.6875
        }
        return SimpleAxisAlignedBB(
                this.x + w,
                this.y,
                this.z + n,
                this.x + e,
                this.y + 1.5,
                this.z + s
        )
    }

    @PowerNukkitDifference(info = "Will connect to glass panes, iron bars and fence gates", since = "1.4.0.0-PN")
    @Override
    fun canConnect(block: Block): Boolean {
        return when (block.getId()) {
            GLASS_PANE, STAINED_GLASS_PANE, IRON_BARS, GLASS, STAINED_GLASS -> true
            else -> {
                if (block is BlockWallBase) {
                    return true
                }
                if (block is BlockFenceGate) {
                    return block.getBlockFace().getAxis() !== calculateAxis(this, block)
                }
                if (block is BlockStairs) {
                    return block.getBlockFace().getOpposite() === calculateFace(this, block)
                }
                if (block is BlockTrapdoor) {
                    val trapdoor: BlockTrapdoor = block
                    return trapdoor.isOpen() && trapdoor.getBlockFace() === calculateFace(this, trapdoor)
                }
                block.isSolid() && !block.isTransparent()
            }
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    override fun isConnected(face: BlockFace?): Boolean {
        return getConnectionType(face) !== WallConnectionType.NONE
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val WALL_CONNECTION_TYPE_SOUTH: BlockProperty<WallConnectionType> = ArrayBlockProperty("wall_connection_type_south", false, WallConnectionType::class.java)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val WALL_CONNECTION_TYPE_WEST: BlockProperty<WallConnectionType> = ArrayBlockProperty("wall_connection_type_west", false, WallConnectionType::class.java)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val WALL_CONNECTION_TYPE_NORTH: BlockProperty<WallConnectionType> = ArrayBlockProperty("wall_connection_type_north", false, WallConnectionType::class.java)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val WALL_CONNECTION_TYPE_EAST: BlockProperty<WallConnectionType> = ArrayBlockProperty("wall_connection_type_east", false, WallConnectionType::class.java)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val WALL_POST_BIT: BooleanBlockProperty = BooleanBlockProperty("wall_post_bit", false)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(
                WALL_CONNECTION_TYPE_SOUTH,
                WALL_CONNECTION_TYPE_WEST,
                WALL_CONNECTION_TYPE_NORTH,
                WALL_CONNECTION_TYPE_EAST,
                WALL_POST_BIT
        )
        private const val MIN_POST_BB = 5.0 / 16
        private const val MAX_POST_BB = 11.0 / 16
    }
}