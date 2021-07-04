package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockBell @PowerNukkitOnly constructor(meta: Int) : BlockTransparentMeta(meta), RedstoneComponent, Faceable, BlockEntityHolder<BlockEntityBell?> {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val name: String
        get() = "Bell"

    @get:Override
    override val id: Int
        get() = BELL

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityBell?>
        get() = BlockEntityBell::class.java

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.BELL

    private fun isConnectedTo(connectedFace: BlockFace, attachmentType: AttachmentType, blockFace: BlockFace): Boolean {
        val faceAxis: BlockFace.Axis = connectedFace.getAxis()
        when (attachmentType) {
            STANDING -> return if (faceAxis === BlockFace.Axis.Y) {
                connectedFace === BlockFace.DOWN
            } else {
                blockFace.getAxis() !== faceAxis
            }
            HANGING -> return connectedFace === BlockFace.UP
            SIDE -> return connectedFace === blockFace.getOpposite()
            MULTIPLE -> return connectedFace === blockFace || connectedFace === blockFace.getOpposite()
            else -> {
            }
        }
        return false
    }

    @Override
    protected override fun recalculateBoundingBox(): AxisAlignedBB {
        val attachmentType: AttachmentType = attachment
        val blockFace: BlockFace = blockFace
        val north = isConnectedTo(BlockFace.NORTH, attachmentType, blockFace)
        val south = isConnectedTo(BlockFace.SOUTH, attachmentType, blockFace)
        val west = isConnectedTo(BlockFace.WEST, attachmentType, blockFace)
        val east = isConnectedTo(BlockFace.EAST, attachmentType, blockFace)
        val up = isConnectedTo(BlockFace.UP, attachmentType, blockFace)
        val down = isConnectedTo(BlockFace.DOWN, attachmentType, blockFace)
        val n: Double = if (north) 0 else 0.25
        val s: Double = if (south) 1 else 0.75
        val w: Double = if (west) 0 else 0.25
        val e: Double = if (east) 1 else 0.75
        val d: Double = if (down) 0 else 0.25
        val u: Double = if (up) 1 else 0.75
        return SimpleAxisAlignedBB(
                this.x + w,
                this.y + d,
                this.z + n,
                this.x + e,
                this.y + u,
                this.z + s
        )
    }

    @Override
    override fun onEntityCollide(entity: Entity) {
        if (entity is EntityItem && entity.positionChanged) {
            val boundingBox: AxisAlignedBB = entity.getBoundingBox()
            val blockBoundingBox: AxisAlignedBB = this.getCollisionBoundingBox()
            if (boundingBox.intersectsWith(blockBoundingBox)) {
                val entityCenter = Vector3(
                        (boundingBox.getMaxX() - boundingBox.getMinX()) / 2,
                        (boundingBox.getMaxY() - boundingBox.getMinY()) / 2,
                        (boundingBox.getMaxZ() - boundingBox.getMinZ()) / 2
                )
                val blockCenter = Vector3(
                        (blockBoundingBox.getMaxX() - blockBoundingBox.getMinX()) / 2,
                        (blockBoundingBox.getMaxY() - blockBoundingBox.getMinY()) / 2,
                        (blockBoundingBox.getMaxZ() - blockBoundingBox.getMinZ()) / 2
                )
                val entityPos: Vector3 = entity.add(entityCenter)
                val blockPos: Vector3 = this.add(
                        blockBoundingBox.getMinX() - x + blockCenter.x,
                        blockBoundingBox.getMinY() - y + blockCenter.y,
                        blockBoundingBox.getMinZ() - z + blockCenter.z
                )
                var entityVector: Vector3 = entityPos.subtract(blockPos)
                entityVector = entityVector.normalize().multiply(0.4)
                entityVector.y = Math.max(0.15, entityVector.y)
                if (ring(entity, BellRingEvent.RingCause.DROPPED_ITEM)) {
                    entity.setMotion(entityVector)
                }
            }
        }
    }

    @Override
    override fun hasEntityCollision(): Boolean {
        return true
    }

    @Override
    protected override fun recalculateCollisionBoundingBox(): AxisAlignedBB {
        return recalculateBoundingBox().expand(0.000001, 0.000001, 0.000001)
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        return ring(player, if (player != null) BellRingEvent.RingCause.HUMAN_INTERACTION else BellRingEvent.RingCause.UNKNOWN)
    }

    @PowerNukkitOnly
    fun ring(causeEntity: Entity?, cause: BellRingEvent.RingCause?): Boolean {
        return ring(causeEntity, cause, null)
    }

    @PowerNukkitOnly
    fun ring(causeEntity: Entity?, cause: BellRingEvent.RingCause?, hitFace: BlockFace?): Boolean {
        var hitFace: BlockFace? = hitFace
        val bell: BlockEntityBell = getOrCreateBlockEntity()
        var addException = true
        val blockFace: BlockFace = blockFace
        if (hitFace == null) {
            if (causeEntity != null) {
                if (causeEntity is EntityItem) {
                    val blockMid: Position = add(0.5, 0.5, 0.5)
                    val vector: Vector3 = causeEntity.subtract(blockMid).normalize()
                    var x = if (vector.x < 0) -1 else if (vector.x > 0) 1 else 0
                    var z = if (vector.z < 0) -1 else if (vector.z > 0) 1 else 0
                    if (x != 0 && z != 0) {
                        if (Math.abs(vector.x) < Math.abs(vector.z)) {
                            x = 0
                        } else {
                            z = 0
                        }
                    }
                    hitFace = blockFace
                    for (face in BlockFace.values()) {
                        if (face.getXOffset() === x && face.getZOffset() === z) {
                            hitFace = face
                            break
                        }
                    }
                } else {
                    hitFace = causeEntity.getDirection()
                }
            } else {
                hitFace = blockFace
            }
        }
        when (attachment) {
            STANDING -> if (hitFace.getAxis() !== blockFace.getAxis()) {
                return false
            }
            MULTIPLE -> if (hitFace.getAxis() === blockFace.getAxis()) {
                return false
            }
            SIDE -> if (hitFace.getAxis() === blockFace.getAxis()) {
                addException = false
            }
            else -> {
            }
        }
        val event = BellRingEvent(this, cause, causeEntity)
        this.level.getServer().getPluginManager().callEvent(event)
        if (event.isCancelled()) {
            return false
        }
        bell.setDirection(hitFace.getOpposite().getHorizontalIndex())
        bell.setTicks(0)
        bell.setRinging(true)
        if (addException && causeEntity is Player) {
            bell.spawnExceptions.add(causeEntity as Player?)
        }
        return true
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private fun checkSupport(): Boolean {
        when (attachment) {
            STANDING -> if (checkSupport(down(), BlockFace.UP)) {
                return true
            }
            HANGING -> if (checkSupport(up(), BlockFace.DOWN)) {
                return true
            }
            MULTIPLE -> {
                val blockFace: BlockFace = blockFace
                if (checkSupport(getSide(blockFace), blockFace.getOpposite()) &&
                        checkSupport(getSide(blockFace.getOpposite()), blockFace)) {
                    return true
                }
            }
            SIDE -> {
                blockFace = blockFace
                if (checkSupport(getSide(blockFace.getOpposite()), blockFace)) {
                    return true
                }
            }
            else -> {
            }
        }
        return false
    }

    private fun checkSupport(support: Block, attachmentFace: BlockFace): Boolean {
        if (BlockLever.isSupportValid(support, attachmentFace)) {
            return true
        }
        if (attachmentFace === BlockFace.DOWN) {
            return when (support.getId()) {
                CHAIN_BLOCK, HOPPER_BLOCK, IRON_BARS -> true
                else -> support is BlockFence || support is BlockWallBase
            }
        }
        return if (support is BlockCauldron) {
            attachmentFace === BlockFace.UP
        } else false
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!checkSupport()) {
                this.level.useBreakOn(this)
            }
            return type
        } else if (type == Level.BLOCK_UPDATE_REDSTONE && this.level.getServer().isRedstoneEnabled()) {
            if (isGettingPower) {
                if (!isToggled) {
                    isToggled = true
                    this.level.setBlock(this, this, true, true)
                    ring(null, BellRingEvent.RingCause.REDSTONE)
                }
            } else if (isToggled) {
                isToggled = false
                this.level.setBlock(this, this, true, true)
            }
            return type
        }
        return 0
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @get:Override
    override val isGettingPower: Boolean
        get() {
            for (side in BlockFace.values()) {
                val b: Block = this.getSide(side)
                if (b.getId() === Block.REDSTONE_WIRE && b.getDamage() > 0 && b.y >= this.getY()) {
                    return true
                }
                if (this.level.isSidePowered(b, side)) {
                    return true
                }
            }
            return this.level.isBlockPowered(this.getLocation())
        }

    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block, @Nonnull target: Block?, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        var face: BlockFace = face
        if (block.canBeReplaced() && block.getId() !== AIR && block.getId() !== BUBBLE_COLUMN && block !is BlockLiquid) {
            face = BlockFace.UP
        }
        val playerDirection: BlockFace = if (player != null) player.getDirection() else BlockFace.EAST
        when (face) {
            UP -> {
                attachment = AttachmentType.STANDING
                blockFace = playerDirection.getOpposite()
            }
            DOWN -> {
                attachment = AttachmentType.HANGING
                blockFace = playerDirection.getOpposite()
            }
            else -> {
                blockFace = face
                attachment = if (checkSupport(block.getSide(face), face.getOpposite())) {
                    AttachmentType.MULTIPLE
                } else {
                    AttachmentType.SIDE
                }
            }
        }
        return if (!checkSupport()) {
            false
        } else BlockEntityHolder.setBlockAndCreateEntity(this) != null
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun onProjectileHit(@Nonnull projectile: Entity, @Nonnull position: Position?, @Nonnull motion: Vector3?): Boolean {
        ring(projectile, BellRingEvent.RingCause.PROJECTILE)
        if (projectile.isOnFire() && projectile is EntityArrow && level.getBlock(projectile).getId() === BlockID.AIR) {
            level.setBlock(projectile, Block.get(BlockID.FIRE), true)
        }
        return true
    }

    @get:Override
    @set:Override
    @set:Since("1.3.0.0-PN")
    @set:PowerNukkitOnly
    var blockFace: BlockFace
        get() = getPropertyValue(DIRECTION)
        set(face) {
            setPropertyValue(DIRECTION, face)
        }

    @get:Since("1.5.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.5.0.0-PN")
    @set:PowerNukkitOnly
    var attachment: AttachmentType
        get() = getPropertyValue(ATTACHMENT_TYPE)
        set(attachmentType) {
            setPropertyValue(ATTACHMENT_TYPE, attachmentType)
        }

    @get:DeprecationDetails(since = "1.4.0.0-PN", reason = "Magic values.", replaceWith = "getAttachment()")
    @get:Deprecated
    @set:DeprecationDetails(since = "1.4.0.0-PN", reason = "Magic values.", replaceWith = "setAttachment(AttachmentType)")
    @set:Deprecated
    var attachmentType: Int
        get() = attachment.ordinal()
        set(attachmentType) {
            attachment = AttachmentType.values().get(attachmentType)
        }

    @get:PowerNukkitOnly
    @set:PowerNukkitOnly
    var isToggled: Boolean
        get() = getBooleanValue(TOGGLE)
        set(toggled) {
            setBooleanValue(TOGGLE, toggled)
        }

    @Override
    override fun toItem(): Item {
        return ItemBlock(BlockBell())
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val hardness: Double
        get() = 1

    @get:Override
    override val resistance: Double
        get() = 25

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val color: BlockColor
        get() = BlockColor.GOLD_BLOCK_COLOR

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val ATTACHMENT_TYPE: BlockProperty<AttachmentType> = ArrayBlockProperty("attachment", false, AttachmentType::class.java)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(DIRECTION, ATTACHMENT_TYPE, TOGGLE)

        @PowerNukkitOnly
        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic values", replaceWith = "BellAttachmentType.STANDING")
        val TYPE_ATTACHMENT_STANDING = 0

        @PowerNukkitOnly
        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic values", replaceWith = "BellAttachmentType.HANGING")
        val TYPE_ATTACHMENT_HANGING = 1

        @PowerNukkitOnly
        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic values", replaceWith = "BellAttachmentType.SIDE")
        val TYPE_ATTACHMENT_SIDE = 2

        @PowerNukkitOnly
        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic values", replaceWith = "BellAttachmentType.MULTIPLE")
        val TYPE_ATTACHMENT_MULTIPLE = 3
    }
}