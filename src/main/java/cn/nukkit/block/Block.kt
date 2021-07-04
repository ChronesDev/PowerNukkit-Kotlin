package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(info = "Implements IMutableBlockState only on PowerNukkit", since = "1.4.0.0-PN")
@SuppressWarnings(["java:S2160", "java:S3400"])
@Log4j2
abstract class Block protected constructor() : Position(), Metadatable, Cloneable, AxisAlignedBB, BlockID, IMutableBlockState {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    private var mutableState: MutableBlockState? = null

    @PowerNukkitOnly
    var layer = 0
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    protected fun getMutableState(): MutableBlockState? {
        if (mutableState == null) {
            mutableState = properties.createMutableState(id)
        }
        return mutableState
    }

    /**
     * Place and initialize a this block correctly in the world.
     *
     * The current instance must have level, x, y, z, and layer properties already set before calling this method.
     * @param item The item being used to place the block. Should be used as an optional reference, may mismatch the block that is being placed depending on plugin implementations.
     * @param block The current block that is in the world and is getting replaced by this instance. It has the same x, y, z, layer, and level as this block.
     * @param target The block that was clicked to create the place action in this block position.
     * @param face The face that was clicked in the target block
     * @param fx The detailed X coordinate of the clicked target block face
     * @param fy The detailed Y coordinate of the clicked target block face
     * @param fz The detailed Z coordinate of the clicked target block face
     * @param player The player that is placing the block. May be null.
     * @return `true` if the block was properly place. The implementation is responsible for reverting any partial change.
     */
    fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        return this.getLevel().setBlock(this, this, true, true)
    }

    //http://minecraft.gamepedia.com/Breaking
    fun canHarvestWithHand(): Boolean {  //used for calculating breaking time
        return true
    }

    fun isBreakable(item: Item?): Boolean {
        return true
    }

    fun tickRate(): Int {
        return 10
    }

    fun onBreak(item: Item?): Boolean {
        return this.getLevel().setBlock(this, layer, Companion[BlockID.AIR], true, true)
    }

    fun onUpdate(type: Int): Int {
        return 0
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun onTouch(@Nullable player: Player?, action: PlayerInteractEvent.Action?): Int {
        return onUpdate(Level.BLOCK_UPDATE_TOUCH)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun onNeighborChange(@Nonnull side: BlockFace?) {
    }

    fun onActivate(@Nonnull item: Item?): Boolean {
        return this.onActivate(item, null)
    }

    fun onActivate(@Nonnull item: Item?, @Nullable player: Player?): Boolean {
        return false
    }

    @Since("1.2.1.0-PN")
    @PowerNukkitOnly
    fun afterRemoval(newBlock: Block?, update: Boolean) {
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val isSoulSpeedCompatible: Boolean
        get() = false
    val hardness: Double
        get() = 10
    val resistance: Double
        get() = 1
    val burnChance: Int
        get() = 0
    val burnAbility: Int
        get() = 0
    val toolType: Int
        get() = ItemTool.TYPE_NONE
    val frictionFactor: Double
        get() = 0.6
    val lightLevel: Int
        get() = 0

    fun canBePlaced(): Boolean {
        return true
    }

    fun canBeReplaced(): Boolean {
        return false
    }

    val isTransparent: Boolean
        get() = false
    val isSolid: Boolean
        get() = true

    /**
     * Check if blocks can be attached in the given side.
     */
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    fun isSolid(side: BlockFace?): Boolean {
        return isSideFull(side)
    }

    // https://minecraft.gamepedia.com/Opacity#Lighting
    fun diffusesSkyLight(): Boolean {
        return false
    }

    fun canBeFlowedInto(): Boolean {
        return false
    }

    @get:PowerNukkitOnly
    val waterloggingLevel: Int
        get() = 0

    fun canWaterloggingFlowInto(): Boolean {
        return canBeFlowedInto() || waterloggingLevel > 1
    }

    fun canBeActivated(): Boolean {
        return false
    }

    fun hasEntityCollision(): Boolean {
        return false
    }

    fun canPassThrough(): Boolean {
        return false
    }

    fun canBePushed(): Boolean {
        return true
    }

    fun canBePulled(): Boolean {
        return true
    }

    fun breaksWhenMoved(): Boolean {
        return false
    }

    fun sticksToPiston(): Boolean {
        return true
    }

    fun hasComparatorInputOverride(): Boolean {
        return false
    }

    val comparatorInputOverride: Int
        get() = 0

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun canHarvest(item: Item): Boolean {
        return toolTier == 0 || toolType == 0 || correctTool0(toolType, item, id) && item.getTier() >= toolTier
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val toolTier: Int
        get() = 0

    fun canBeClimbed(): Boolean {
        return false
    }

    val color: BlockColor
        get() = BlockColor.VOID_BLOCK_COLOR
    abstract val name: String
    abstract val id: Int
    val itemId: Int
        get() {
            val id = id
            return if (id > 255) {
                255 - id
            } else {
                id
            }
        }

    /**
     * The full id is a combination of the id and data.
     * @return full id
     */
    @get:Deprecated("PowerNukkit: The meta is limited to 32 bits")
    @get:DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    @get:Deprecated
    val fullId: Int
        get() = if (mutableState == null) 0 else mutableState.getFullId()

    /**
     * The properties that fully describe all possible and valid states that this block can have.
     */
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @get:Nonnull
    val properties: BlockProperties
        get() = CommonBlockProperties.EMPTY_PROPERTIES

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val currentState: BlockState
        get() = if (mutableState == null) BlockState.of(id) else mutableState.getCurrentState()

    @get:Since("1.3.0.0-PN")
    @get:PowerNukkitOnly
    val runtimeId: Int
        get() = currentState.getRuntimeId()

    fun addVelocityToEntity(entity: Entity?, vector: Vector3?) {}

    @get:DeprecationDetails(reason = "Limited to 32 bits", since = "1.4.0.0-PN")
    @get:Deprecated
    @set:DeprecationDetails(reason = "Limited to 32 bits", since = "1.4.0.0-PN")
    @set:Deprecated
    var damage: Int
        get() = if (mutableState == null) 0 else mutableState.getBigDamage()
        set(meta) {
            if (meta == 0 && isDefaultState) {
                return
            }
            getMutableState().setDataStorageFromInt(meta)
        }

    @Deprecated
    @DeprecationDetails(reason = "Limited to 32 bits", since = "1.4.0.0-PN")
    fun setDamage(meta: Integer?) {
        damage = if (meta == null) 0 else meta and 0x0f
    }

    fun position(v: Position) {
        this.x = v.x as Int
        this.y = v.y as Int
        this.z = v.z as Int
        this.level = v.level
    }

    fun getDrops(item: Item): Array<Item> {
        if (id < 0 || id > list!!.size) { //Unknown blocks
            return Item.EMPTY_ARRAY
        } else if (canHarvestWithHand() || canHarvest(item)) {
            return arrayOf<Item>(
                    toItem()
            )
        }
        return Item.EMPTY_ARRAY
    }

    private fun toolBreakTimeBonus0(item: Item): Double {
        return toolBreakTimeBonus0(toolType0(item, id), item.getTier(), id)
    }

    @Nonnull
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun calculateBreakTime(@Nonnull item: Item): Double {
        return calculateBreakTime(item, null)
    }

    @Nonnull
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun calculateBreakTime(@Nonnull item: Item, @Nullable player: Player?): Double {
        var seconds = 0.0
        val blockHardness = hardness
        val canHarvest = canHarvest(item)
        seconds = if (canHarvest) {
            blockHardness * 1.5
        } else {
            blockHardness * 5
        }
        var speedMultiplier = 1.0
        var hasConduitPower = false
        var hasAquaAffinity = false
        var hasteEffectLevel = 0
        var miningFatigueLevel = 0
        if (player != null) {
            hasConduitPower = player.hasEffect(Effect.CONDUIT_POWER)
            hasAquaAffinity = Optional.ofNullable(player.getInventory().getHelmet().getEnchantment(Enchantment.ID_WATER_WORKER))
                    .map(Enchantment::getLevel).map { l -> l >= 1 }.orElse(false)
            hasteEffectLevel = Optional.ofNullable(player.getEffect(Effect.HASTE))
                    .map(Effect::getAmplifier).orElse(0)
            miningFatigueLevel = Optional.ofNullable(player.getEffect(Effect.MINING_FATIGUE))
                    .map(Effect::getAmplifier).orElse(0)
        }
        if (correctTool0(toolType, item, id)) {
            speedMultiplier = toolBreakTimeBonus0(item)
            val efficiencyLevel: Int = Optional.ofNullable(item.getEnchantment(Enchantment.ID_EFFICIENCY))
                    .map(Enchantment::getLevel).orElse(0)
            if (canHarvest && efficiencyLevel > 0) {
                speedMultiplier += (efficiencyLevel xor 2 + 1).toDouble()
            }
            if (hasConduitPower) hasteEffectLevel = Integer.max(hasteEffectLevel, 2)
            if (hasteEffectLevel > 0) {
                speedMultiplier *= 1 + 0.2 * hasteEffectLevel
            }
        }
        if (miningFatigueLevel > 0) {
            speedMultiplier /= (3 xor miningFatigueLevel).toDouble()
        }
        seconds /= speedMultiplier
        if (player != null) {
            if (player.isInsideOfWater() && !hasAquaAffinity) {
                seconds *= if (hasConduitPower && blockHardness >= 0.5) 2.5 else 5
            }
            if (!player.isOnGround()) {
                seconds *= 5.0
            }
        }
        return seconds
    }

    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Not completely accurate", replaceWith = "calculateBreakeTime()")
    @Deprecated
    @PowerNukkitDifference(info = "Special condition for the leaves", since = "1.4.0.0-PN")
    fun getBreakTime(item: Item, player: Player): Double {
        Objects.requireNonNull(item, "getBreakTime: Item can not be null")
        Objects.requireNonNull(player, "getBreakTime: Player can not be null")
        val blockHardness = hardness
        if (blockHardness == 0.0) {
            return 0
        }
        val blockId = id
        val correctTool = correctTool0(toolType, item, blockId)
        val canHarvestWithHand = canHarvestWithHand()
        val itemToolType = toolType0(item, blockId)
        val itemTier: Int = item.getTier()
        val efficiencyLoreLevel: Int = Optional.ofNullable(item.getEnchantment(Enchantment.ID_EFFICIENCY))
                .map(Enchantment::getLevel).orElse(0)
        var hasteEffectLevel: Int = Optional.ofNullable(player.getEffect(Effect.HASTE))
                .map(Effect::getAmplifier).orElse(0)
        //TODO Fix the break time with CONDUIT_POWER, it's not right
        val conduitPowerLevel: Int = Optional.ofNullable(player.getEffect(Effect.CONDUIT_POWER))
                .map { e ->  /*(e.getAmplifier() +1) * 4*/e.getAmplifier() }
                .orElse(0)
        hasteEffectLevel += conduitPowerLevel
        val insideOfWaterWithoutAquaAffinity = player.isInsideOfWater() && conduitPowerLevel <= 0 &&
                Optional.ofNullable(player.getInventory().getHelmet().getEnchantment(Enchantment.ID_WATER_WORKER))
                        .map(Enchantment::getLevel).map { l -> l >= 1 }.orElse(false)
        val outOfWaterButNotOnGround = !player.isInsideOfWater() && !player.isOnGround()
        return breakTime0(blockHardness, correctTool, canHarvestWithHand, blockId, itemToolType, itemTier,
                efficiencyLoreLevel, hasteEffectLevel, insideOfWaterWithoutAquaAffinity, outOfWaterButNotOnGround)
    }

    /**
     * @param item item used
     * @return break time
     */
    @PowerNukkitDifference(info = "Special condition for the hoe and netherie support", since = "1.4.0.0-PN")
    @Deprecated
    @Deprecated("""This function is lack of Player class and is not accurate enough, use {@link #getBreakTime(Item, Player)}
      """)
    fun getBreakTime(item: Item): Double {
        var base = this.hardness * 1.5
        if (canBeBrokenWith(item)) {
            if (toolType == ItemTool.TYPE_SHEARS && item.isShears() ||
                    toolType == ItemTool.TYPE_SHEARS && item.isHoe()) {
                base /= 15.0
            } else if (toolType == ItemTool.TYPE_PICKAXE && item.isPickaxe() ||
                    toolType == ItemTool.TYPE_AXE && item.isAxe() ||
                    toolType == ItemTool.TYPE_SHOVEL && item.isShovel() ||
                    toolType == ItemTool.TYPE_HOE && item.isHoe()) {
                val tier: Int = item.getTier()
                when (tier) {
                    ItemTool.TIER_WOODEN -> base /= 2.0
                    ItemTool.TIER_STONE -> base /= 4.0
                    ItemTool.TIER_IRON -> base /= 6.0
                    ItemTool.TIER_DIAMOND -> base /= 8.0
                    ItemTool.TIER_NETHERITE -> base /= 9.0
                    ItemTool.TIER_GOLD -> base /= 12.0
                }
            }
        } else {
            base *= 3.33
        }
        if (item.isSword()) {
            base *= 0.5
        }
        return base
    }

    fun canBeBrokenWith(item: Item?): Boolean {
        return this.hardness != -1.0
    }

    fun getSide(face: BlockFace): Block {
        return getSideAtLayer(layer, face)
    }

    fun getSide(face: BlockFace?, step: Int): Block {
        return getSideAtLayer(layer, face, step)
    }

    fun getSideAtLayer(layer: Int, face: BlockFace): Block {
        return if (this.isValid()) {
            this.getLevel().getBlock(x as Int + face.getXOffset(), y as Int + face.getYOffset(), z as Int + face.getZOffset(), layer)
        } else this.getSide(face, 1)
    }

    fun getSideAtLayer(layer: Int, face: BlockFace?, step: Int): Block {
        if (this.isValid()) {
            return if (step == 1) {
                this.getLevel().getBlock(x as Int + face.getXOffset(), y as Int + face.getYOffset(), z as Int + face.getZOffset(), layer)
            } else {
                this.getLevel().getBlock(x as Int + face.getXOffset() * step, y as Int + face.getYOffset() * step, z as Int + face.getZOffset() * step, layer)
            }
        }
        val block: Block = get(Item.AIR, 0)
        block.x = x as Int + face.getXOffset() * step
        block.y = y as Int + face.getYOffset() * step
        block.z = z as Int + face.getZOffset() * step
        block.layer = layer
        return block
    }

    @JvmOverloads
    fun up(step: Int = 1): Block {
        return getSide(BlockFace.UP, step)
    }

    fun up(step: Int, layer: Int): Block {
        return getSideAtLayer(layer, BlockFace.UP, step)
    }

    @JvmOverloads
    fun down(step: Int = 1): Block {
        return getSide(BlockFace.DOWN, step)
    }

    fun down(step: Int, layer: Int): Block {
        return getSideAtLayer(layer, BlockFace.DOWN, step)
    }

    @JvmOverloads
    fun north(step: Int = 1): Block {
        return getSide(BlockFace.NORTH, step)
    }

    fun north(step: Int, layer: Int): Block {
        return getSideAtLayer(layer, BlockFace.NORTH, step)
    }

    @JvmOverloads
    fun south(step: Int = 1): Block {
        return getSide(BlockFace.SOUTH, step)
    }

    fun south(step: Int, layer: Int): Block {
        return getSideAtLayer(layer, BlockFace.SOUTH, step)
    }

    @JvmOverloads
    fun east(step: Int = 1): Block {
        return getSide(BlockFace.EAST, step)
    }

    fun east(step: Int, layer: Int): Block {
        return getSideAtLayer(layer, BlockFace.EAST, step)
    }

    @JvmOverloads
    fun west(step: Int = 1): Block {
        return getSide(BlockFace.WEST, step)
    }

    fun west(step: Int, layer: Int): Block {
        return getSideAtLayer(layer, BlockFace.WEST, step)
    }

    @Override
    override fun toString(): String {
        return "Block[" + name + "] (" + id + ":" + (if (mutableState != null) mutableState.getDataStorage() else "0") + ")" +
                if (isValid()) " at " + super.toString() else ""
    }

    fun collidesWithBB(bb: AxisAlignedBB): Boolean {
        return collidesWithBB(bb, false)
    }

    fun collidesWithBB(bb: AxisAlignedBB, collisionBB: Boolean): Boolean {
        val bb1: AxisAlignedBB = if (collisionBB) collisionBoundingBox else boundingBox
        return bb1 != null && bb.intersectsWith(bb1)
    }

    fun onEntityCollide(entity: Entity?) {}
    val boundingBox: AxisAlignedBB
        get() = recalculateBoundingBox()
    val collisionBoundingBox: AxisAlignedBB
        get() = recalculateCollisionBoundingBox()

    protected fun recalculateBoundingBox(): AxisAlignedBB {
        return this
    }

    @get:Override
    val minX: Double
        get() = this.x

    @get:Override
    val minY: Double
        get() = this.y

    @get:Override
    val minZ: Double
        get() = this.z

    @get:Override
    val maxX: Double
        get() = this.x + 1

    @get:Override
    val maxY: Double
        get() = this.y + 1

    @get:Override
    val maxZ: Double
        get() = this.z + 1

    protected fun recalculateCollisionBoundingBox(): AxisAlignedBB {
        return boundingBox
    }

    fun calculateIntercept(pos1: Vector3, pos2: Vector3?): MovingObjectPosition? {
        val bb: AxisAlignedBB = boundingBox ?: return null
        var v1: Vector3? = pos1.getIntermediateWithXValue(pos2, bb.getMinX())
        var v2: Vector3? = pos1.getIntermediateWithXValue(pos2, bb.getMaxX())
        var v3: Vector3? = pos1.getIntermediateWithYValue(pos2, bb.getMinY())
        var v4: Vector3? = pos1.getIntermediateWithYValue(pos2, bb.getMaxY())
        var v5: Vector3? = pos1.getIntermediateWithZValue(pos2, bb.getMinZ())
        var v6: Vector3? = pos1.getIntermediateWithZValue(pos2, bb.getMaxZ())
        if (v1 != null && !bb.isVectorInYZ(v1)) {
            v1 = null
        }
        if (v2 != null && !bb.isVectorInYZ(v2)) {
            v2 = null
        }
        if (v3 != null && !bb.isVectorInXZ(v3)) {
            v3 = null
        }
        if (v4 != null && !bb.isVectorInXZ(v4)) {
            v4 = null
        }
        if (v5 != null && !bb.isVectorInXY(v5)) {
            v5 = null
        }
        if (v6 != null && !bb.isVectorInXY(v6)) {
            v6 = null
        }
        var vector: Vector3? = v1
        if (v2 != null && (vector == null || pos1.distanceSquared(v2) < pos1.distanceSquared(vector))) {
            vector = v2
        }
        if (v3 != null && (vector == null || pos1.distanceSquared(v3) < pos1.distanceSquared(vector))) {
            vector = v3
        }
        if (v4 != null && (vector == null || pos1.distanceSquared(v4) < pos1.distanceSquared(vector))) {
            vector = v4
        }
        if (v5 != null && (vector == null || pos1.distanceSquared(v5) < pos1.distanceSquared(vector))) {
            vector = v5
        }
        if (v6 != null && (vector == null || pos1.distanceSquared(v6) < pos1.distanceSquared(vector))) {
            vector = v6
        }
        if (vector == null) {
            return null
        }
        var f: BlockFace? = null
        if (vector === v1) {
            f = BlockFace.WEST
        } else if (vector === v2) {
            f = BlockFace.EAST
        } else if (vector === v3) {
            f = BlockFace.DOWN
        } else if (vector === v4) {
            f = BlockFace.UP
        } else if (vector === v5) {
            f = BlockFace.NORTH
        } else if (vector === v6) {
            f = BlockFace.SOUTH
        }
        return MovingObjectPosition.fromBlock(this.x as Int, this.y as Int, this.z as Int, f, vector.add(this.x, this.y, this.z))
    }

    val saveId: String
        get() {
            val name: String = getClass().getName()
            return name.substring(16)
        }

    @Override
    @Throws(Exception::class)
    fun setMetadata(metadataKey: String?, newMetadataValue: MetadataValue?) {
        if (this.getLevel() != null) {
            this.getLevel().getBlockMetadata().setMetadata(this, metadataKey, newMetadataValue)
        }
    }

    @Override
    @Throws(Exception::class)
    fun getMetadata(metadataKey: String?): List<MetadataValue>? {
        return if (this.getLevel() != null) {
            this.getLevel().getBlockMetadata().getMetadata(this, metadataKey)
        } else null
    }

    @Override
    @Throws(Exception::class)
    fun hasMetadata(metadataKey: String?): Boolean {
        return this.getLevel() != null && this.getLevel().getBlockMetadata().hasMetadata(this, metadataKey)
    }

    @Override
    @Throws(Exception::class)
    fun removeMetadata(metadataKey: String?, owningPlugin: Plugin?) {
        if (this.getLevel() != null) {
            this.getLevel().getBlockMetadata().removeMetadata(this, metadataKey, owningPlugin)
        }
    }

    fun clone(): Block {
        val clone = super.clone() as Block
        clone.mutableState = if (mutableState != null) mutableState.copy() else null
        return clone
    }

    fun getWeakPower(face: BlockFace?): Int {
        return 0
    }

    fun getStrongPower(side: BlockFace?): Int {
        return 0
    }

    val isPowerSource: Boolean
        get() = false
    val locationHash: String
        get() = this.getFloorX().toString() + ":" + this.getFloorY() + ":" + this.getFloorZ()
    val dropExp: Int
        get() = 0

    /**
     * Check if the block is not transparent, is solid and can't provide redstone power.
     */
    val isNormalBlock: Boolean
        get() = !isTransparent && isSolid && !isPowerSource

    /**
     * Check if the block is not transparent, is solid and is a full cube like a stone block.
     */
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val isSimpleBlock: Boolean
        get() = !isTransparent && isSolid && isFullBlock

    /**
     * Check if the given face is fully occupied by the block bounding box.
     * @param face The face to be checked
     * @return If and ony if the bounding box completely cover the face
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun isSideFull(face: BlockFace?): Boolean {
        val boundingBox: AxisAlignedBB = boundingBox ?: return false
        if (face.getAxis().getPlane() === BlockFace.Plane.HORIZONTAL) {
            if (boundingBox.getMinY() !== getY() || boundingBox.getMaxY() !== getY() + 1) {
                return false
            }
            var offset: Int = face.getXOffset()
            if (offset < 0) {
                return boundingBox.getMinX() === getX() && boundingBox.getMinZ() === getZ() && boundingBox.getMaxZ() === getZ() + 1
            } else if (offset > 0) {
                return boundingBox.getMaxX() === getX() + 1 && boundingBox.getMaxZ() === getZ() + 1 && boundingBox.getMinZ() === getZ()
            }
            offset = face.getZOffset()
            return if (offset < 0) {
                boundingBox.getMinZ() === getZ() && boundingBox.getMinX() === getX() && boundingBox.getMaxX() === getX() + 1
            } else boundingBox.getMaxZ() === getZ() + 1 && boundingBox.getMaxX() === getX() + 1 && boundingBox.getMinX() === getX()
        }
        if (boundingBox.getMinX() !== getX() || boundingBox.getMaxX() !== getX() + 1 || boundingBox.getMinZ() !== getZ() || boundingBox.getMaxZ() !== getZ() + 1) {
            return false
        }
        return if (face.getYOffset() < 0) {
            boundingBox.getMinY() === getY()
        } else boundingBox.getMaxY() === getY() + 1
    }

    /**
     * Check if the block occupies the entire block space, like a stone and normal glass blocks
     */
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val isFullBlock: Boolean
        get() {
            val boundingBox: AxisAlignedBB = boundingBox ?: return false
            return boundingBox.getMinX() === getX() && boundingBox.getMaxX() === getX() + 1 && boundingBox.getMinY() === getY() && boundingBox.getMaxY() === getY() + 1 && boundingBox.getMinZ() === getZ() && boundingBox.getMaxZ() === getZ() + 1
        }

    @PowerNukkitDifference(info = "Prevents players from getting invalid items by limiting the return to the maximum damage defined in getMaxItemDamage()", since = "1.4.0.0-PN")
    fun toItem(): Item {
        return asItemBlock(1)
    }

    /**
     * If the block, when in item form, is resistant to lava and fire and can float on lava like if it was on water.
     * @since 1.4.0.0-PN
     */
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val isLavaResistant: Boolean
        get() = false

    @Nonnull
    @Override
    fun asItemBlock(): ItemBlock {
        return asItemBlock(1)
    }

    fun canSilkTouch(): Boolean {
        return false
    }

    @PowerNukkitOnly
    @Since("1.2.1.0-PN")
    fun mustSilkTouch(vector: Vector3?, layer: Int, face: BlockFace?, item: Item?, player: Player?): Boolean {
        return false
    }

    @PowerNukkitOnly
    @Since("1.2.1.0-PN")
    fun mustDrop(vector: Vector3?, layer: Int, face: BlockFace?, item: Item?, player: Player?): Boolean {
        return false
    }

    fun firstInLayers(condition: Predicate<Block?>): Optional<Block> {
        return firstInLayers(0, condition)
    }

    fun firstInLayers(startingLayer: Int, condition: Predicate<Block?>): Optional<Block> {
        val maximumLayer: Int = this.level.requireProvider().getMaximumLayer()
        for (layer in startingLayer..maximumLayer) {
            val block: Block = this.getLevelBlockAtLayer(layer)
            if (condition.test(block)) {
                return Optional.of(block)
            }
        }
        return Optional.empty()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    @Throws(InvalidBlockStateException::class)
    fun setState(@Nonnull state: IBlockState) {
        if (state.getBlockId() === id && isDefaultState && state.isDefaultState()) {
            return
        }
        getMutableState().setState(state)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    fun setDataStorageFromInt(@Nonnegative storage: Int) {
        if (storage == 0 && isDefaultState) {
            return
        }
        getMutableState().setDataStorageFromInt(storage)
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun setDataStorage(@Nonnegative @Nonnull storage: Number?, repair: Boolean, callback: Consumer<BlockStateRepair?>?): Boolean {
        return if (NukkitMath.isZero(storage) && isDefaultState) {
            false
        } else getMutableState().setDataStorage(storage, repair, callback)
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun setDataStorageFromInt(@Nonnegative storage: Int, repair: Boolean, callback: Consumer<BlockStateRepair?>?): Boolean {
        return if (storage == 0 && isDefaultState) {
            false
        } else getMutableState().setDataStorageFromInt(storage, repair, callback)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    fun setPropertyValue(@Nonnull propertyName: String?, @Nullable value: Serializable?) {
        if (isDefaultState && properties.isDefaultValue(propertyName, value)) {
            return
        }
        getMutableState().setPropertyValue(propertyName, value)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    fun setBooleanValue(@Nonnull propertyName: String?, value: Boolean) {
        if (isDefaultState && properties.isDefaultBooleanValue(propertyName, value)) {
            return
        }
        getMutableState().setBooleanValue(propertyName, value)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    fun setIntValue(@Nonnull propertyName: String?, value: Int) {
        if (isDefaultState && properties.isDefaultIntValue(propertyName, value)) {
            return
        }
        getMutableState().setIntValue(propertyName, value)
    }

    @get:DeprecationDetails(reason = "Does the same as getId() but the other is compatible with NukkitX and this is not", since = "1.4.0.0-PN")
    @get:Deprecated
    @get:Override
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @get:Nonnegative
    val blockId: Int
        get() = id

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @get:Nonnegative
    @set:Override
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var dataStorage: Number?
        get() = if (mutableState == null) 0 else mutableState.getDataStorage()
        set(storage) {
            if (NukkitMath.isZero(storage) && isDefaultState) {
                return
            }
            getMutableState().setDataStorage(storage)
        }

    @get:Override
    @get:DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @get:Deprecated
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @get:Nonnegative
    val legacyDamage: Int
        get() = if (mutableState == null) 0 else mutableState.getLegacyDamage()

    @get:Override
    @get:DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @get:Deprecated
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @get:Unsigned
    val bigDamage: Int
        get() = if (mutableState == null) 0 else mutableState.getBigDamage()

    @get:Override
    @get:DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @get:Deprecated
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    @get:Nonnegative
    val signedBigDamage: Int
        get() = if (mutableState == null) 0 else mutableState.getSignedBigDamage()

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @get:Nonnegative
    val hugeDamage: BigInteger
        get() = if (mutableState == null) BigInteger.ZERO else mutableState.getHugeDamage()

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    fun getPropertyValue(@Nonnull propertyName: String?): Serializable {
        return if (isDefaultState) {
            properties.getBlockProperty(propertyName).getDefaultValue()
        } else getMutableState().getPropertyValue(propertyName)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    fun getIntValue(@Nonnull propertyName: String?): Int {
        return if (isDefaultState) {
            properties.getBlockProperty(propertyName).getDefaultIntValue()
        } else getMutableState().getIntValue(propertyName)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    fun getBooleanValue(@Nonnull propertyName: String?): Boolean {
        return if (isDefaultState) {
            properties.getBlockProperty(propertyName).getDefaultBooleanValue()
        } else getMutableState().getBooleanValue(propertyName)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    fun getPersistenceValue(@Nonnull propertyName: String?): String {
        return if (isDefaultState) {
            properties.getBlockProperty(propertyName).getPersistenceValueForMeta(0)
        } else getMutableState().getPersistenceValue(propertyName)
    }

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    val exactIntStorage: Int
        get() = if (mutableState == null) 0 else mutableState.getExactIntStorage()

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun isBreakable(@Nonnull vector: Vector3?, layer: Int, @Nonnull face: BlockFace?, @Nonnull item: Item?, @Nullable player: Player?, setBlockDestroy: Boolean): Boolean {
        return true
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val isBlockChangeAllowed: Boolean
        get() = getChunk().isBlockChangeAllowed(getFloorX() and 0xF, getFloorY(), getFloorZ() and 0xF)

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun isBlockChangeAllowed(@Nullable player: Player?): Boolean {
        return if (isBlockChangeAllowed) {
            true
        } else player != null && player.isCreative() && player.isOp()
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val lightFilter: Int
        get() = if (isSolid && !isTransparent) 15 else 1

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun canRandomTick(): Boolean {
        return Level.canRandomTick(id)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun onProjectileHit(@Nonnull projectile: Entity?, @Nonnull position: Position?, @Nonnull motion: Vector3?): Boolean {
        return false
    }

    @get:Override
    @get:Nonnull
    val block: Block
        get() = clone()

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    val isDefaultState: Boolean
        get() = mutableState == null || mutableState.isDefaultState()

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val itemMaxStackSize: Int
        get() = 64

    /**
     * Check if a block is getting powered threw a block or directly.
     * @return if the gets powered.
     */
    @get:PowerNukkitDifference(info = "Used so often, why not create own method here?", since = "1.4.0.0-PN")
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val isGettingPower: Boolean
        get() {
            if (!this.level.getServer().isRedstoneEnabled()) return false
            for (side in BlockFace.values()) {
                val b: Block = this.getSide(side).getLevelBlock()
                if (this.level.isSidePowered(b.getLocation(), side)) {
                    return true
                }
            }
            return this.level.isBlockPowered(this.getLocation())
        }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EMPTY_ARRAY = arrayOfNulls<Block>(0)

        //<editor-fold desc="static fields" defaultstate="collapsed">
        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "It is being replaced by an other solution that don't require a fixed size")
        @PowerNukkitOnly
        val MAX_BLOCK_ID: Int = dynamic(600)

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "It's not a constant value, it may be changed on major updates and" +
                " plugins will have to be recompiled in order to update this value in the binary files, " +
                "it's also being replaced by the BlockState system")
        @PowerNukkitOnly
        val DATA_BITS: Int = dynamic(4)

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "It's not a constant value, it may be changed on major updates and" +
                " plugins will have to be recompiled in order to update this value in the binary files, " +
                "it's also being replaced by the BlockState system")
        @PowerNukkitOnly
        val DATA_SIZE: Int = dynamic(1 shl DATA_BITS)

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "It's not a constant value, it may be changed on major updates and" +
                " plugins will have to be recompiled in order to update this value in the binary files, " +
                "it's also being replaced by the BlockState system")
        @PowerNukkitOnly
        val DATA_MASK: Int = dynamic(DATA_SIZE - 1)

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "Not encapsulated, easy to break", replaceWith = "Block.get(int).getClass(), to register new blocks use registerBlockImplementation()")
        @SuppressWarnings(["java:S1444", "java:S2386"])
        var list: Array<Class<out Block>>? = null

        @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN", replaceWith = "To register/override implementations use registerBlockImplementation(), " +
                "to get the block with a given state use BlockState.of and than BlockState.getBlock()")
        @Deprecated
        @SuppressWarnings(["java:S1444", "java:S2386", "java:S1123", "java:S1133", "DeprecatedIsStillUsed"])
        var fullList: Array<Block?>? = null

        @Deprecated
        @DeprecationDetails(reason = "Not encapsulated, easy to break", since = "1.4.0.0-PN", replaceWith = "Block.getLightLevel()")
        @SuppressWarnings(["java:S1444", "java:S2386"])
        var light: IntArray? = null

        @Deprecated
        @DeprecationDetails(reason = "Not encapsulated, easy to break", since = "1.4.0.0-PN", replaceWith = "Block.getLightFilter()")
        @SuppressWarnings(["java:S1444", "java:S2386"])
        var lightFilter: IntArray? = null

        @Deprecated
        @DeprecationDetails(reason = "Not encapsulated, easy to break", since = "1.4.0.0-PN", replaceWith = "Block.isSolid()")
        @SuppressWarnings(["java:S1444", "java:S2386"])
        var solid: BooleanArray? = null

        @Deprecated
        @DeprecationDetails(reason = "Not encapsulated, easy to break", since = "1.4.0.0-PN", replaceWith = "Block.getHardness()")
        @SuppressWarnings(["java:S1444", "java:S2386"])
        var hardness: DoubleArray? = null

        @Deprecated
        @DeprecationDetails(reason = "Not encapsulated, easy to break", since = "1.4.0.0-PN", replaceWith = "Block.isTransparent()")
        @SuppressWarnings(["java:S1444", "java:S2386"])
        var transparent: BooleanArray? = null

        @Deprecated
        @DeprecationDetails(reason = "Not encapsulated, easy to break", since = "1.4.0.0-PN", replaceWith = "Block.diffusesSkyLight()")
        @SuppressWarnings(["java:S1444", "java:S2386"])
        var diffusesSkyLight: BooleanArray? = null

        /**
         * if a block has can have variants
         */
        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "It's being replaced by the BlockState system")
        @SuppressWarnings(["java:S1444", "java:S2386"])
        var hasMeta: BooleanArray? = null

        @get:Since("1.3.0.0-PN")
        @get:PowerNukkitOnly
        var isInitializing = false
            private set

        //</editor-fold>
        //<editor-fold desc="initialization" defaultstate="collapsed">
        @SuppressWarnings("unchecked")
        fun init() {
            if (list == null) {
                list = arrayOfNulls<Class>(MAX_BLOCK_ID)
                fullList = arrayOfNulls(MAX_BLOCK_ID * (1 shl DATA_BITS))
                light = IntArray(MAX_BLOCK_ID)
                lightFilter = IntArray(MAX_BLOCK_ID)
                solid = BooleanArray(MAX_BLOCK_ID)
                hardness = DoubleArray(MAX_BLOCK_ID)
                transparent = BooleanArray(MAX_BLOCK_ID)
                diffusesSkyLight = BooleanArray(MAX_BLOCK_ID)
                hasMeta = BooleanArray(MAX_BLOCK_ID)
                list!![AIR] = BlockAir::class.java //0
                list!![STONE] = BlockStone::class.java //1
                list!![GRASS] = BlockGrass::class.java //2
                list!![DIRT] = BlockDirt::class.java //3
                list!![COBBLESTONE] = BlockCobblestone::class.java //4
                list!![PLANKS] = BlockPlanks::class.java //5
                list!![SAPLING] = BlockSapling::class.java //6
                list!![BEDROCK] = BlockBedrock::class.java //7
                list!![WATER] = BlockWater::class.java //8
                list!![STILL_WATER] = BlockWaterStill::class.java //9
                list!![LAVA] = BlockLava::class.java //10
                list!![STILL_LAVA] = BlockLavaStill::class.java //11
                list!![SAND] = BlockSand::class.java //12
                list!![GRAVEL] = BlockGravel::class.java //13
                list!![GOLD_ORE] = BlockOreGold::class.java //14
                list!![IRON_ORE] = BlockOreIron::class.java //15
                list!![COAL_ORE] = BlockOreCoal::class.java //16
                list!![WOOD] = BlockWood::class.java //17
                list!![LEAVES] = BlockLeaves::class.java //18
                list!![SPONGE] = BlockSponge::class.java //19
                list!![GLASS] = BlockGlass::class.java //20
                list!![LAPIS_ORE] = BlockOreLapis::class.java //21
                list!![LAPIS_BLOCK] = BlockLapis::class.java //22
                list!![DISPENSER] = BlockDispenser::class.java //23
                list!![SANDSTONE] = BlockSandstone::class.java //24
                list!![NOTEBLOCK] = BlockNoteblock::class.java //25
                list!![BED_BLOCK] = BlockBed::class.java //26
                list!![POWERED_RAIL] = BlockRailPowered::class.java //27
                list!![DETECTOR_RAIL] = BlockRailDetector::class.java //28
                list!![STICKY_PISTON] = BlockPistonSticky::class.java //29
                list!![COBWEB] = BlockCobweb::class.java //30
                list!![TALL_GRASS] = BlockTallGrass::class.java //31
                list!![DEAD_BUSH] = BlockDeadBush::class.java //32
                list!![PISTON] = BlockPiston::class.java //33
                list!![PISTON_HEAD] = BlockPistonHead::class.java //34
                list!![WOOL] = BlockWool::class.java //35
                list!![DANDELION] = BlockDandelion::class.java //37
                list!![FLOWER] = BlockFlower::class.java //38
                list!![BROWN_MUSHROOM] = BlockMushroomBrown::class.java //39
                list!![RED_MUSHROOM] = BlockMushroomRed::class.java //40
                list!![GOLD_BLOCK] = BlockGold::class.java //41
                list!![IRON_BLOCK] = BlockIron::class.java //42
                list!![DOUBLE_STONE_SLAB] = BlockDoubleSlabStone::class.java //43
                list!![STONE_SLAB] = BlockSlabStone::class.java //44
                list!![BRICKS_BLOCK] = BlockBricks::class.java //45
                list!![TNT] = BlockTNT::class.java //46
                list!![BOOKSHELF] = BlockBookshelf::class.java //47
                list!![MOSS_STONE] = BlockMossStone::class.java //48
                list!![OBSIDIAN] = BlockObsidian::class.java //49
                list!![TORCH] = BlockTorch::class.java //50
                list!![FIRE] = BlockFire::class.java //51
                list!![MONSTER_SPAWNER] = BlockMobSpawner::class.java //52
                list!![WOOD_STAIRS] = BlockStairsWood::class.java //53
                list!![CHEST] = BlockChest::class.java //54
                list!![REDSTONE_WIRE] = BlockRedstoneWire::class.java //55
                list!![DIAMOND_ORE] = BlockOreDiamond::class.java //56
                list!![DIAMOND_BLOCK] = BlockDiamond::class.java //57
                list!![WORKBENCH] = BlockCraftingTable::class.java //58
                list!![WHEAT_BLOCK] = BlockWheat::class.java //59
                list!![FARMLAND] = BlockFarmland::class.java //60
                list!![FURNACE] = BlockFurnace::class.java //61
                list!![BURNING_FURNACE] = BlockFurnaceBurning::class.java //62
                list!![SIGN_POST] = BlockSignPost::class.java //63
                list!![WOOD_DOOR_BLOCK] = BlockDoorWood::class.java //64
                list!![LADDER] = BlockLadder::class.java //65
                list!![RAIL] = BlockRail::class.java //66
                list!![COBBLESTONE_STAIRS] = BlockStairsCobblestone::class.java //67
                list!![WALL_SIGN] = BlockWallSign::class.java //68
                list!![LEVER] = BlockLever::class.java //69
                list!![STONE_PRESSURE_PLATE] = BlockPressurePlateStone::class.java //70
                list!![IRON_DOOR_BLOCK] = BlockDoorIron::class.java //71
                list!![WOODEN_PRESSURE_PLATE] = BlockPressurePlateWood::class.java //72
                list!![REDSTONE_ORE] = BlockOreRedstone::class.java //73
                list!![GLOWING_REDSTONE_ORE] = BlockOreRedstoneGlowing::class.java //74
                list!![UNLIT_REDSTONE_TORCH] = BlockRedstoneTorchUnlit::class.java
                list!![REDSTONE_TORCH] = BlockRedstoneTorch::class.java //76
                list!![STONE_BUTTON] = BlockButtonStone::class.java //77
                list!![SNOW_LAYER] = BlockSnowLayer::class.java //78
                list!![ICE] = BlockIce::class.java //79
                list!![SNOW_BLOCK] = BlockSnow::class.java //80
                list!![CACTUS] = BlockCactus::class.java //81
                list!![CLAY_BLOCK] = BlockClay::class.java //82
                list!![SUGARCANE_BLOCK] = BlockSugarcane::class.java //83
                list!![JUKEBOX] = BlockJukebox::class.java //84
                list!![FENCE] = BlockFence::class.java //85
                list!![PUMPKIN] = BlockPumpkin::class.java //86
                list!![NETHERRACK] = BlockNetherrack::class.java //87
                list!![SOUL_SAND] = BlockSoulSand::class.java //88
                list!![GLOWSTONE_BLOCK] = BlockGlowstone::class.java //89
                list!![NETHER_PORTAL] = BlockNetherPortal::class.java //90
                list!![LIT_PUMPKIN] = BlockPumpkinLit::class.java //91
                list!![CAKE_BLOCK] = BlockCake::class.java //92
                list!![UNPOWERED_REPEATER] = BlockRedstoneRepeaterUnpowered::class.java //93
                list!![POWERED_REPEATER] = BlockRedstoneRepeaterPowered::class.java //94
                list!![INVISIBLE_BEDROCK] = BlockBedrockInvisible::class.java //95
                list!![TRAPDOOR] = BlockTrapdoor::class.java //96
                list!![MONSTER_EGG] = BlockMonsterEgg::class.java //97
                list!![STONE_BRICKS] = BlockBricksStone::class.java //98
                list!![BROWN_MUSHROOM_BLOCK] = BlockHugeMushroomBrown::class.java //99
                list!![RED_MUSHROOM_BLOCK] = BlockHugeMushroomRed::class.java //100
                list!![IRON_BARS] = BlockIronBars::class.java //101
                list!![GLASS_PANE] = BlockGlassPane::class.java //102
                list!![MELON_BLOCK] = BlockMelon::class.java //103
                list!![PUMPKIN_STEM] = BlockStemPumpkin::class.java //104
                list!![MELON_STEM] = BlockStemMelon::class.java //105
                list!![VINE] = BlockVine::class.java //106
                list!![FENCE_GATE] = BlockFenceGate::class.java //107
                list!![BRICK_STAIRS] = BlockStairsBrick::class.java //108
                list!![STONE_BRICK_STAIRS] = BlockStairsStoneBrick::class.java //109
                list!![MYCELIUM] = BlockMycelium::class.java //110
                list!![WATER_LILY] = BlockWaterLily::class.java //111
                list!![NETHER_BRICKS] = BlockBricksNether::class.java //112
                list!![NETHER_BRICK_FENCE] = BlockFenceNetherBrick::class.java //113
                list!![NETHER_BRICKS_STAIRS] = BlockStairsNetherBrick::class.java //114
                list!![NETHER_WART_BLOCK] = BlockNetherWart::class.java //115
                list!![ENCHANTING_TABLE] = BlockEnchantingTable::class.java //116
                list!![BREWING_STAND_BLOCK] = BlockBrewingStand::class.java //117
                list!![CAULDRON_BLOCK] = BlockCauldron::class.java //118
                list!![END_PORTAL] = BlockEndPortal::class.java //119
                list!![END_PORTAL_FRAME] = BlockEndPortalFrame::class.java //120
                list!![END_STONE] = BlockEndStone::class.java //121
                list!![DRAGON_EGG] = BlockDragonEgg::class.java //122
                list!![REDSTONE_LAMP] = BlockRedstoneLamp::class.java //123
                list!![LIT_REDSTONE_LAMP] = BlockRedstoneLampLit::class.java //124
                list!![DROPPER] = BlockDropper::class.java //125
                list!![ACTIVATOR_RAIL] = BlockRailActivator::class.java //126
                list!![COCOA] = BlockCocoa::class.java //127
                list!![SANDSTONE_STAIRS] = BlockStairsSandstone::class.java //128
                list!![EMERALD_ORE] = BlockOreEmerald::class.java //129
                list!![ENDER_CHEST] = BlockEnderChest::class.java //130
                list!![TRIPWIRE_HOOK] = BlockTripWireHook::class.java
                list!![TRIPWIRE] = BlockTripWire::class.java //132
                list!![EMERALD_BLOCK] = BlockEmerald::class.java //133
                list!![SPRUCE_WOOD_STAIRS] = BlockStairsSpruce::class.java //134
                list!![BIRCH_WOOD_STAIRS] = BlockStairsBirch::class.java //135
                list!![JUNGLE_WOOD_STAIRS] = BlockStairsJungle::class.java //136
                list!![BEACON] = BlockBeacon::class.java //138
                list!![STONE_WALL] = BlockWall::class.java //139
                list!![FLOWER_POT_BLOCK] = BlockFlowerPot::class.java //140
                list!![CARROT_BLOCK] = BlockCarrot::class.java //141
                list!![POTATO_BLOCK] = BlockPotato::class.java //142
                list!![WOODEN_BUTTON] = BlockButtonWooden::class.java //143
                list!![SKULL_BLOCK] = BlockSkull::class.java //144
                list!![ANVIL] = BlockAnvil::class.java //145
                list!![TRAPPED_CHEST] = BlockTrappedChest::class.java //146
                list!![LIGHT_WEIGHTED_PRESSURE_PLATE] = BlockWeightedPressurePlateLight::class.java //147
                list!![HEAVY_WEIGHTED_PRESSURE_PLATE] = BlockWeightedPressurePlateHeavy::class.java //148
                list!![UNPOWERED_COMPARATOR] = BlockRedstoneComparatorUnpowered::class.java //149
                list!![POWERED_COMPARATOR] = BlockRedstoneComparatorPowered::class.java //149
                list!![DAYLIGHT_DETECTOR] = BlockDaylightDetector::class.java //151
                list!![REDSTONE_BLOCK] = BlockRedstone::class.java //152
                list!![QUARTZ_ORE] = BlockOreQuartz::class.java //153
                list!![HOPPER_BLOCK] = BlockHopper::class.java //154
                list!![QUARTZ_BLOCK] = BlockQuartz::class.java //155
                list!![QUARTZ_STAIRS] = BlockStairsQuartz::class.java //156
                list!![DOUBLE_WOOD_SLAB] = BlockDoubleSlabWood::class.java //157
                list!![WOOD_SLAB] = BlockSlabWood::class.java //158
                list!![STAINED_TERRACOTTA] = BlockTerracottaStained::class.java //159
                list!![STAINED_GLASS_PANE] = BlockGlassPaneStained::class.java //160
                list!![LEAVES2] = BlockLeaves2::class.java //161
                list!![WOOD2] = BlockWood2::class.java //162
                list!![ACACIA_WOOD_STAIRS] = BlockStairsAcacia::class.java //163
                list!![DARK_OAK_WOOD_STAIRS] = BlockStairsDarkOak::class.java //164
                list!![SLIME_BLOCK] = BlockSlime::class.java //165
                list!![IRON_TRAPDOOR] = BlockTrapdoorIron::class.java //167
                list!![PRISMARINE] = BlockPrismarine::class.java //168
                list!![SEA_LANTERN] = BlockSeaLantern::class.java //169
                list!![HAY_BALE] = BlockHayBale::class.java //170
                list!![CARPET] = BlockCarpet::class.java //171
                list!![TERRACOTTA] = BlockTerracotta::class.java //172
                list!![COAL_BLOCK] = BlockCoal::class.java //173
                list!![PACKED_ICE] = BlockIcePacked::class.java //174
                list!![DOUBLE_PLANT] = BlockDoublePlant::class.java //175
                list!![STANDING_BANNER] = BlockBanner::class.java //176
                list!![WALL_BANNER] = BlockWallBanner::class.java //177
                list!![DAYLIGHT_DETECTOR_INVERTED] = BlockDaylightDetectorInverted::class.java //178
                list!![RED_SANDSTONE] = BlockRedSandstone::class.java //179
                list!![RED_SANDSTONE_STAIRS] = BlockStairsRedSandstone::class.java //180
                list!![DOUBLE_RED_SANDSTONE_SLAB] = BlockDoubleSlabRedSandstone::class.java //181
                list!![RED_SANDSTONE_SLAB] = BlockSlabRedSandstone::class.java //182
                list!![FENCE_GATE_SPRUCE] = BlockFenceGateSpruce::class.java //183
                list!![FENCE_GATE_BIRCH] = BlockFenceGateBirch::class.java //184
                list!![FENCE_GATE_JUNGLE] = BlockFenceGateJungle::class.java //185
                list!![FENCE_GATE_DARK_OAK] = BlockFenceGateDarkOak::class.java //186
                list!![FENCE_GATE_ACACIA] = BlockFenceGateAcacia::class.java //187
                list!![SPRUCE_DOOR_BLOCK] = BlockDoorSpruce::class.java //193
                list!![BIRCH_DOOR_BLOCK] = BlockDoorBirch::class.java //194
                list!![JUNGLE_DOOR_BLOCK] = BlockDoorJungle::class.java //195
                list!![ACACIA_DOOR_BLOCK] = BlockDoorAcacia::class.java //196
                list!![DARK_OAK_DOOR_BLOCK] = BlockDoorDarkOak::class.java //197
                list!![GRASS_PATH] = BlockGrassPath::class.java //198
                list!![ITEM_FRAME_BLOCK] = BlockItemFrame::class.java //199
                list!![CHORUS_FLOWER] = BlockChorusFlower::class.java //200
                list!![PURPUR_BLOCK] = BlockPurpur::class.java //201
                list!![PURPUR_STAIRS] = BlockStairsPurpur::class.java //203
                list!![UNDYED_SHULKER_BOX] = BlockUndyedShulkerBox::class.java //205
                list!![END_BRICKS] = BlockBricksEndStone::class.java //206
                list!![ICE_FROSTED] = BlockIceFrosted::class.java //207
                list!![END_ROD] = BlockEndRod::class.java //208
                list!![END_GATEWAY] = BlockEndGateway::class.java //209
                list!![ALLOW] = BlockAllow::class.java //210
                list!![DENY] = BlockDeny::class.java //211
                list!![BORDER_BLOCK] = BlockBorder::class.java //212
                list!![MAGMA] = BlockMagma::class.java //213
                list!![BLOCK_NETHER_WART_BLOCK] = BlockNetherWartBlock::class.java //214
                list!![RED_NETHER_BRICK] = BlockBricksRedNether::class.java //215
                list!![BONE_BLOCK] = BlockBone::class.java //216
                list!![STRUCTURE_VOID] = BlockStructureVoid::class.java //217
                list!![SHULKER_BOX] = BlockShulkerBox::class.java //218
                list!![PURPLE_GLAZED_TERRACOTTA] = BlockTerracottaGlazedPurple::class.java //219
                list!![WHITE_GLAZED_TERRACOTTA] = BlockTerracottaGlazedWhite::class.java //220
                list!![ORANGE_GLAZED_TERRACOTTA] = BlockTerracottaGlazedOrange::class.java //221
                list!![MAGENTA_GLAZED_TERRACOTTA] = BlockTerracottaGlazedMagenta::class.java //222
                list!![LIGHT_BLUE_GLAZED_TERRACOTTA] = BlockTerracottaGlazedLightBlue::class.java //223
                list!![YELLOW_GLAZED_TERRACOTTA] = BlockTerracottaGlazedYellow::class.java //224
                list!![LIME_GLAZED_TERRACOTTA] = BlockTerracottaGlazedLime::class.java //225
                list!![PINK_GLAZED_TERRACOTTA] = BlockTerracottaGlazedPink::class.java //226
                list!![GRAY_GLAZED_TERRACOTTA] = BlockTerracottaGlazedGray::class.java //227
                list!![SILVER_GLAZED_TERRACOTTA] = BlockTerracottaGlazedSilver::class.java //228
                list!![CYAN_GLAZED_TERRACOTTA] = BlockTerracottaGlazedCyan::class.java //229
                list!![BLUE_GLAZED_TERRACOTTA] = BlockTerracottaGlazedBlue::class.java //231
                list!![BROWN_GLAZED_TERRACOTTA] = BlockTerracottaGlazedBrown::class.java //232
                list!![GREEN_GLAZED_TERRACOTTA] = BlockTerracottaGlazedGreen::class.java //233
                list!![RED_GLAZED_TERRACOTTA] = BlockTerracottaGlazedRed::class.java //234
                list!![BLACK_GLAZED_TERRACOTTA] = BlockTerracottaGlazedBlack::class.java //235
                list!![CONCRETE] = BlockConcrete::class.java //236
                list!![CONCRETE_POWDER] = BlockConcretePowder::class.java //237
                list!![CHORUS_PLANT] = BlockChorusPlant::class.java //240
                list!![STAINED_GLASS] = BlockGlassStained::class.java //241
                list!![PODZOL] = BlockPodzol::class.java //243
                list!![BEETROOT_BLOCK] = BlockBeetroot::class.java //244
                list!![STONECUTTER] = BlockStonecutter::class.java //245
                list!![GLOWING_OBSIDIAN] = BlockObsidianGlowing::class.java //246
                list!![NETHER_REACTOR] = BlockNetherReactor::class.java //247 Should not be removed
                list!![MOVING_BLOCK] = BlockMoving::class.java //250
                list!![OBSERVER] = BlockObserver::class.java //251
                list!![STRUCTURE_BLOCK] = BlockStructure::class.java //252
                list!![PRISMARINE_STAIRS] = BlockStairsPrismarine::class.java //257
                list!![DARK_PRISMARINE_STAIRS] = BlockStairsDarkPrismarine::class.java //258
                list!![PRISMARINE_BRICKS_STAIRS] = BlockStairsPrismarineBrick::class.java //259
                list!![STRIPPED_SPRUCE_LOG] = BlockWoodStrippedSpruce::class.java //260
                list!![STRIPPED_BIRCH_LOG] = BlockWoodStrippedBirch::class.java //261
                list!![STRIPPED_JUNGLE_LOG] = BlockWoodStrippedJungle::class.java //262
                list!![STRIPPED_ACACIA_LOG] = BlockWoodStrippedAcacia::class.java //263
                list!![STRIPPED_DARK_OAK_LOG] = BlockWoodStrippedDarkOak::class.java //264
                list!![STRIPPED_OAK_LOG] = BlockWoodStrippedOak::class.java //265
                list!![BLUE_ICE] = BlockBlueIce::class.java //266
                list!![SEAGRASS] = BlockSeagrass::class.java //385
                list!![CORAL] = BlockCoral::class.java //386
                list!![CORAL_BLOCK] = BlockCoralBlock::class.java //387
                list!![CORAL_FAN] = BlockCoralFan::class.java //388
                list!![CORAL_FAN_DEAD] = BlockCoralFanDead::class.java //389
                list!![CORAL_FAN_HANG] = BlockCoralFanHang::class.java //390
                list!![CORAL_FAN_HANG2] = BlockCoralFanHang2::class.java //391
                list!![CORAL_FAN_HANG3] = BlockCoralFanHang3::class.java //392
                list!![BLOCK_KELP] = BlockKelp::class.java //393
                list!![DRIED_KELP_BLOCK] = BlockDriedKelpBlock::class.java //394
                list!![ACACIA_BUTTON] = BlockButtonAcacia::class.java //395
                list!![BIRCH_BUTTON] = BlockButtonBirch::class.java //396
                list!![DARK_OAK_BUTTON] = BlockButtonDarkOak::class.java //397
                list!![JUNGLE_BUTTON] = BlockButtonJungle::class.java //398
                list!![SPRUCE_BUTTON] = BlockButtonSpruce::class.java //399
                list!![ACACIA_TRAPDOOR] = BlockTrapdoorAcacia::class.java //400
                list!![BIRCH_TRAPDOOR] = BlockTrapdoorBirch::class.java //401
                list!![DARK_OAK_TRAPDOOR] = BlockTrapdoorDarkOak::class.java //402
                list!![JUNGLE_TRAPDOOR] = BlockTrapdoorJungle::class.java //403
                list!![SPRUCE_TRAPDOOR] = BlockTrapdoorSpruce::class.java //404
                list!![ACACIA_PRESSURE_PLATE] = BlockPressurePlateAcacia::class.java //405
                list!![BIRCH_PRESSURE_PLATE] = BlockPressurePlateBirch::class.java //406
                list!![DARK_OAK_PRESSURE_PLATE] = BlockPressurePlateDarkOak::class.java //407
                list!![JUNGLE_PRESSURE_PLATE] = BlockPressurePlateJungle::class.java //408
                list!![SPRUCE_PRESSURE_PLATE] = BlockPressurePlateSpruce::class.java //409
                list!![CARVED_PUMPKIN] = BlockCarvedPumpkin::class.java //410
                list!![SEA_PICKLE] = BlockSeaPickle::class.java //411
                list!![CONDUIT] = BlockConduit::class.java //412
                list!![TURTLE_EGG] = BlockTurtleEgg::class.java //414
                list!![BUBBLE_COLUMN] = BlockBubbleColumn::class.java //415
                list!![BARRIER] = BlockBarrier::class.java //416
                list!![STONE_SLAB3] = BlockSlabStone3::class.java //417
                list!![BAMBOO] = BlockBamboo::class.java //418
                list!![BAMBOO_SAPLING] = BlockBambooSapling::class.java //419
                list!![SCAFFOLDING] = BlockScaffolding::class.java //420
                list!![STONE_SLAB4] = BlockSlabStone4::class.java //421
                list!![DOUBLE_STONE_SLAB3] = BlockDoubleSlabStone3::class.java //422
                list!![DOUBLE_STONE_SLAB4] = BlockDoubleSlabStone4::class.java //422
                list!![GRANITE_STAIRS] = BlockStairsGranite::class.java //424
                list!![DIORITE_STAIRS] = BlockStairsDiorite::class.java //425
                list!![ANDESITE_STAIRS] = BlockStairsAndesite::class.java //426
                list!![POLISHED_GRANITE_STAIRS] = BlockStairsGranitePolished::class.java //427
                list!![POLISHED_DIORITE_STAIRS] = BlockStairsDioritePolished::class.java //428
                list!![POLISHED_ANDESITE_STAIRS] = BlockStairsAndesitePolished::class.java //429
                list!![MOSSY_STONE_BRICK_STAIRS] = BlockStairsMossyStoneBrick::class.java //430
                list!![SMOOTH_RED_SANDSTONE_STAIRS] = BlockStairsSmoothRedSandstone::class.java //431
                list!![SMOOTH_SANDSTONE_STAIRS] = BlockStairsSmoothSandstone::class.java //432
                list!![END_BRICK_STAIRS] = BlockStairsEndBrick::class.java //433
                list!![MOSSY_COBBLESTONE_STAIRS] = BlockStairsMossyCobblestone::class.java //434
                list!![NORMAL_STONE_STAIRS] = BlockStairsStone::class.java //435
                list!![SMOOTH_STONE] = BlockSmoothStone::class.java //438
                list!![RED_NETHER_BRICK_STAIRS] = BlockStairsRedNetherBrick::class.java //439
                list!![SMOOTH_QUARTZ_STAIRS] = BlockStairsSmoothQuartz::class.java //440
                list!![SPRUCE_STANDING_SIGN] = BlockSpruceSignPost::class.java //436
                list!![SPRUCE_WALL_SIGN] = BlockSpruceWallSign::class.java //437
                list!![BIRCH_STANDING_SIGN] = BlockBirchSignPost::class.java //441
                list!![BIRCH_WALL_SIGN] = BlockBirchWallSign::class.java //442
                list!![JUNGLE_STANDING_SIGN] = BlockJungleSignPost::class.java //443
                list!![JUNGLE_WALL_SIGN] = BlockJungleWallSign::class.java //444
                list!![ACACIA_STANDING_SIGN] = BlockAcaciaSignPost::class.java //445
                list!![ACACIA_WALL_SIGN] = BlockAcaciaWallSign::class.java //446
                list!![DARKOAK_STANDING_SIGN] = BlockDarkOakSignPost::class.java //447
                list!![DARKOAK_WALL_SIGN] = BlockDarkOakWallSign::class.java //448
                list!![LECTERN] = BlockLectern::class.java //449
                list!![GRINDSTONE] = BlockGrindstone::class.java //450
                list!![BLAST_FURNACE] = BlockBlastFurnace::class.java //451
                list!![STONECUTTER_BLOCK] = BlockStonecutterBlock::class.java //452
                list!![SMOKER] = BlockSmoker::class.java //453
                list!![LIT_SMOKER] = BlockSmokerBurning::class.java //454
                list!![CARTOGRAPHY_TABLE] = BlockCartographyTable::class.java //455
                list!![FLETCHING_TABLE] = BlockFletchingTable::class.java //456
                list!![SMITHING_TABLE] = BlockSmithingTable::class.java //457
                list!![BARREL] = BlockBarrel::class.java //458
                list!![LOOM] = BlockLoom::class.java //459
                list!![BELL] = BlockBell::class.java //462
                list!![SWEET_BERRY_BUSH] = BlockSweetBerryBush::class.java //462
                list!![LANTERN] = BlockLantern::class.java //463
                list!![CAMPFIRE_BLOCK] = BlockCampfire::class.java //464
                list!![LAVA_CAULDRON] = BlockCauldronLava::class.java //465
                list!![JIGSAW] = BlockJigsaw::class.java //466
                list!![WOOD_BARK] = BlockWoodBark::class.java //467
                list!![COMPOSTER] = BlockComposter::class.java //468
                list!![LIT_BLAST_FURNACE] = BlockBlastFurnaceBurning::class.java //469
                list!![LIGHT_BLOCK] = BlockLight::class.java //470
                list!![WITHER_ROSE] = BlockWitherRose::class.java //471
                list!![STICKYPISTONARMCOLLISION] = BlockPistonHeadSticky::class.java //472
                list!![BEE_NEST] = BlockBeeNest::class.java //473
                list!![BEEHIVE] = BlockBeehive::class.java //474
                list!![HONEY_BLOCK] = BlockHoney::class.java //475
                list!![HONEYCOMB_BLOCK] = BlockHoneycombBlock::class.java //476
                list!![LODESTONE] = BlockLodestone::class.java //477
                list!![CRIMSON_ROOTS] = BlockRootsCrimson::class.java //478
                list!![WARPED_ROOTS] = BlockRootsWarped::class.java //479
                list!![CRIMSON_STEM] = BlockStemCrimson::class.java //480
                list!![WARPED_STEM] = BlockStemWarped::class.java //481
                list!![WARPED_WART_BLOCK] = BlockWarpedWartBlock::class.java //482 
                list!![CRIMSON_FUNGUS] = BlockFungusCrimson::class.java //483
                list!![WARPED_FUNGUS] = BlockFungusWarped::class.java //484
                list!![SHROOMLIGHT] = BlockShroomlight::class.java //485
                list!![WEEPING_VINES] = BlockVinesWeeping::class.java //486
                list!![CRIMSON_NYLIUM] = BlockNyliumCrimson::class.java //487
                list!![WARPED_NYLIUM] = BlockNyliumWarped::class.java //488
                list!![BASALT] = BlockBasalt::class.java //489
                list!![POLISHED_BASALT] = BlockPolishedBasalt::class.java //490
                list!![SOUL_SOIL] = BlockSoulSoil::class.java //491
                list!![SOUL_FIRE] = BlockFireSoul::class.java //492
                list!![NETHER_SPROUTS_BLOCK] = BlockNetherSprout::class.java //493 
                list!![TARGET] = BlockTarget::class.java //494
                list!![STRIPPED_CRIMSON_STEM] = BlockStemStrippedCrimson::class.java //495
                list!![STRIPPED_WARPED_STEM] = BlockStemStrippedWarped::class.java //496
                list!![CRIMSON_PLANKS] = BlockPlanksCrimson::class.java //497
                list!![WARPED_PLANKS] = BlockPlanksWarped::class.java //498
                list!![CRIMSON_DOOR_BLOCK] = BlockDoorCrimson::class.java //499
                list!![WARPED_DOOR_BLOCK] = BlockDoorWarped::class.java //500
                list!![CRIMSON_TRAPDOOR] = BlockTrapdoorCrimson::class.java //501
                list!![WARPED_TRAPDOOR] = BlockTrapdoorWarped::class.java //502
                // 503
                // 504
                list!![CRIMSON_STANDING_SIGN] = BlockCrimsonSignPost::class.java //505
                list!![CRIMSON_WALL_SIGN] = BlockCrimsonWallSign::class.java //506
                list!![WARPED_STANDING_SIGN] = BlockWarpedSignPost::class.java //507
                list!![WARPED_WALL_SIGN] = BlockWarpedWallSign::class.java //508
                list!![CRIMSON_STAIRS] = BlockStairsCrimson::class.java //509
                list!![WARPED_STAIRS] = BlockStairsWarped::class.java //510
                list!![CRIMSON_FENCE] = BlockFenceCrimson::class.java //511
                list!![WARPED_FENCE] = BlockFenceWarped::class.java //512
                list!![CRIMSON_FENCE_GATE] = BlockFenceGateCrimson::class.java //513
                list!![WARPED_FENCE_GATE] = BlockFenceGateWarped::class.java //514
                list!![CRIMSON_BUTTON] = BlockButtonCrimson::class.java //515
                list!![WARPED_BUTTON] = BlockButtonWarped::class.java //516
                list!![CRIMSON_PRESSURE_PLATE] = BlockPressurePlateCrimson::class.java //517
                list!![WARPED_PRESSURE_PLATE] = BlockPressurePlateWarped::class.java //518
                list!![CRIMSON_SLAB] = BlockSlabCrimson::class.java //519
                list!![WARPED_SLAB] = BlockSlabWarped::class.java //520
                list!![CRIMSON_DOUBLE_SLAB] = BlockDoubleSlabCrimson::class.java //521
                list!![WARPED_DOUBLE_SLAB] = BlockDoubleSlabWarped::class.java //522
                list!![SOUL_TORCH] = BlockSoulTorch::class.java //523
                list!![SOUL_LANTERN] = BlockSoulLantern::class.java //524
                list!![NETHERITE_BLOCK] = BlockNetheriteBlock::class.java //525
                list!![ANCIENT_DERBRIS] = BlockAncientDebris::class.java //526
                list!![RESPAWN_ANCHOR] = BlockRespawnAnchor::class.java //527
                list!![BLACKSTONE] = BlockBlackstone::class.java //528
                list!![POLISHED_BLACKSTONE_BRICKS] = BlockBricksBlackstonePolished::class.java //529
                list!![POLISHED_BLACKSTONE_BRICK_STAIRS] = BlockStairsBrickBlackstonePolished::class.java //530
                list!![BLACKSTONE_STAIRS] = BlockStairsBlackstone::class.java //531
                list!![BLACKSTONE_WALL] = BlockWallBlackstone::class.java //532
                list!![POLISHED_BLACKSTONE_BRICK_WALL] = BlockWallBrickBlackstonePolished::class.java //533
                list!![CHISELED_POLISHED_BLACKSTONE] = BlockBlackstonePolishedChiseled::class.java //534
                list!![CRACKED_POLISHED_BLACKSTONE_BRICKS] = BlockBricksBlackstonePolishedCracked::class.java //535
                list!![GILDED_BLACKSTONE] = BlockBlackstoneGilded::class.java //536
                list!![BLACKSTONE_SLAB] = BlockSlabBlackstone::class.java //537
                list!![BLACKSTONE_DOUBLE_SLAB] = BlockDoubleSlabBlackstone::class.java //538
                list!![POLISHED_BLACKSTONE_BRICK_SLAB] = BlockSlabBrickBlackstonePolished::class.java //539
                list!![POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB] = BlockDoubleSlabBrickBlackstonePolished::class.java //540
                list!![CHAIN_BLOCK] = BlockChain::class.java //541
                list!![TWISTING_VINES] = BlockVinesTwisting::class.java //542
                list!![NETHER_GOLD_ORE] = BlockOreGoldNether::class.java //543
                list!![CRYING_OBSIDIAN] = BlockObsidianCrying::class.java //544
                list!![SOUL_CAMPFIRE_BLOCK] = BlockCampfireSoul::class.java //545
                list!![POLISHED_BLACKSTONE] = BlockBlackstonePolished::class.java //546
                list!![POLISHED_BLACKSTONE_STAIRS] = BlockStairsBlackstonePolished::class.java //547
                list!![POLISHED_BLACKSTONE_SLAB] = BlockSlabBlackstonePolished::class.java //548
                list!![POLISHED_BLACKSTONE_DOUBLE_SLAB] = BlockDoubleSlabBlackstonePolished::class.java //549
                list!![POLISHED_BLACKSTONE_PRESSURE_PLATE] = BlockPressurePlateBlackstonePolished::class.java //550
                list!![POLISHED_BLACKSTONE_BUTTON] = BlockButtonBlackstonePolished::class.java //551
                list!![POLISHED_BLACKSTONE_WALL] = BlockWallBlackstonePolished::class.java //552
                list!![WARPED_HYPHAE] = BlockHyphaeWarped::class.java //553
                list!![CRIMSON_HYPHAE] = BlockHyphaeCrimson::class.java //554
                list!![STRIPPED_CRIMSON_HYPHAE] = BlockHyphaeStrippedCrimson::class.java //555
                list!![STRIPPED_WARPED_HYPHAE] = BlockHyphaeStrippedWarped::class.java //556
                list!![CHISELED_NETHER_BRICKS] = BlockBricksNetherChiseled::class.java //557
                list!![CRACKED_NETHER_BRICKS] = BlockBricksNetherCracked::class.java //558
                list!![QUARTZ_BRICKS] = BlockBricksQuartz::class.java //559
                isInitializing = true
                for (id in 0 until MAX_BLOCK_ID) {
                    val c: Class<out Block> = list!![id]
                    if (c != null) {
                        var block: Block?
                        try {
                            block = c.getDeclaredConstructor().newInstance()
                            val persistenceName: String = block.getPersistenceName()
                            BlockStateRegistry.registerPersistenceName(id, persistenceName)
                            try {
                                val constructor: Constructor<out Block> = c.getDeclaredConstructor(Int::class.javaPrimitiveType)
                                constructor.setAccessible(true)
                                for (data in 0 until (1 shl DATA_BITS)) {
                                    val fullId: Int = id shl DATA_BITS or data
                                    var b: Block
                                    try {
                                        b = constructor.newInstance(data)
                                        if (b.damage != data) {
                                            b = BlockUnknown(id, data)
                                        }
                                    } catch (wrapper: InvocationTargetException) {
                                        val uncaught: Throwable = wrapper.getTargetException()
                                        if (uncaught !is InvalidBlockDamageException) {
                                            log.error("Error while registering {} with meta {}", c.getName(), data, uncaught)
                                        }
                                        b = BlockUnknown(id, data)
                                    }
                                    fullList!![fullId] = b
                                }
                                hasMeta!![id] = true
                            } catch (ignore: NoSuchMethodException) {
                                var data = 0
                                while (data < DATA_SIZE) {
                                    val fullId: Int = id shl DATA_BITS or data
                                    fullList!![fullId] = block
                                    ++data
                                }
                            }
                        } catch (e: Exception) {
                            log.error("Error while registering {}", c.getName(), e)
                            var data = 0
                            while (data < DATA_SIZE) {
                                fullList!![id shl DATA_BITS or data] = BlockUnknown(id, data)
                                ++data
                            }
                            block = fullList!![id shl DATA_BITS]
                        }
                        solid!![id] = block!!.isSolid
                        transparent!![id] = block.isTransparent
                        diffusesSkyLight!![id] = block.diffusesSkyLight()
                        hardness!![id] = block.hardness
                        light!![id] = block.lightLevel
                        lightFilter!![id] = block.lightFilter
                    } else {
                        lightFilter!![id] = 1
                        for (data in 0 until DATA_SIZE) {
                            fullList!![id shl DATA_BITS or data] = BlockUnknown(id, data)
                        }
                    }
                }
                isInitializing = false
            }
        }

        //</editor-fold>
        //<editor-fold desc="static getters" defaultstate="collapsed">
        operator fun get(id: Int): Block {
            var id = id
            if (id < 0) {
                id = 255 - id
            }
            return fullList!![id shl DATA_BITS]!!.clone()
        }

        @Deprecated
        @DeprecationDetails(reason = "The meta is limited to 32 bits", replaceWith = "BlockState.getBlock()", since = "1.4.0.0-PN")
        operator fun get(id: Int, meta: Integer?): Block {
            var id = id
            if (id < 0) {
                id = 255 - id
            }
            return if (meta != null) {
                val iMeta: Int = meta
                if (iMeta <= DATA_SIZE) {
                    fullList!![id shl DATA_BITS or meta]!!.clone()
                } else {
                    val block = fullList!![id shl DATA_BITS]!!.clone()
                    block.damage = iMeta
                    block
                }
            } else {
                fullList!![id shl DATA_BITS]!!.clone()
            }
        }

        @Deprecated
        @DeprecationDetails(reason = "The meta is limited to 32 bits", replaceWith = "BlockState.getBlock()", since = "1.4.0.0-PN")
        operator fun get(id: Int, meta: Integer?, pos: Position?): Block {
            return Companion[id, meta, pos, 0]
        }

        @Deprecated
        @DeprecationDetails(reason = "The meta is limited to 32 bits", replaceWith = "BlockState.getBlock()", since = "1.4.0.0-PN")
        @SuppressWarnings("unchecked")
        operator fun get(id: Int, meta: Integer?, pos: Position?, layer: Int): Block {
            var id = id
            if (id < 0) {
                id = 255 - id
            }
            val block: Block
            if (meta != null && meta > DATA_SIZE) {
                block = fullList!![id shl DATA_BITS]!!.clone()
                block.damage = meta
            } else {
                block = fullList!![id shl DATA_BITS or (if (meta == null) 0 else meta)]!!.clone()
            }
            if (pos != null) {
                block.x = pos.x
                block.y = pos.y
                block.z = pos.z
                block.level = pos.level
                block.layer = layer
            }
            return block
        }

        @Deprecated
        @DeprecationDetails(reason = "The meta is limited to 32 bits", replaceWith = "BlockState.getBlock()", since = "1.4.0.0-PN")
        operator fun get(id: Int, data: Int): Block {
            var id = id
            if (id < 0) {
                id = 255 - id
            }
            return if (data < DATA_SIZE) {
                fullList!![id shl DATA_BITS or data]!!.clone()
            } else {
                val block = fullList!![id shl DATA_BITS]!!.clone()
                block.damage = data
                block
            }
        }

        @Deprecated
        @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
        operator fun get(fullId: Int, level: Level?, x: Int, y: Int, z: Int): Block {
            return get(fullId, level, x, y, z, 0)
        }

        @Deprecated
        @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
        operator fun get(fullId: Int, level: Level, x: Int, y: Int, z: Int, layer: Int): Block {
            val block = fullList!![fullId]!!.clone()
            block.x = x
            block.y = y
            block.z = z
            block.level = level
            block.layer = layer
            return block
        }

        @Deprecated
        @DeprecationDetails(reason = "The meta is limited to 32 bits", replaceWith = "BlockState.getBlock()", since = "1.4.0.0-PN")
        @PowerNukkitOnly
        @Since("1.3.0.0-PN")
        operator fun get(id: Int, meta: Int, level: Level, x: Int, y: Int, z: Int): Block {
            return Companion[id, meta, level, x, y, z, 0]
        }

        @Deprecated
        @DeprecationDetails(reason = "The meta is limited to 32 bits", replaceWith = "BlockState.getBlock()", since = "1.4.0.0-PN")
        @PowerNukkitOnly
        @Since("1.3.0.0-PN")
        operator fun get(id: Int, meta: Int, level: Level, x: Int, y: Int, z: Int, layer: Int): Block {
            val block: Block
            if (meta <= DATA_SIZE) {
                block = fullList!![id shl DATA_BITS or meta]!!.clone()
            } else {
                block = fullList!![id shl DATA_BITS]!!.clone()
                block.damage = meta
            }
            block.x = x
            block.y = y
            block.z = z
            block.level = level
            block.layer = layer
            return block
        }
        //</editor-fold>
        /**
         * Register a new block implementation overriding the existing one.
         * @param blockId The block ID that will be registered. Can't be negative.
         * @param blockClass The class that overrides [Block] and implements this block,
         * it must have a constructor without params and optionally one that accepts `Number` or `int`
         * @param persistenceName The block persistence name, must use the format namespace:block_name
         * @param receivesRandomTick If the block should receive random ticks from the level
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun registerBlockImplementation(blockId: Int, @Nonnull blockClass: Class<out Block?>, @Nonnull persistenceName: String?, receivesRandomTick: Boolean) {
            Preconditions.checkArgument(blockId >= 0, "Negative block id %s", blockId)
            Preconditions.checkNotNull(blockClass, "blockClass was null")
            Preconditions.checkNotNull(persistenceName, "persistenceName was null")
            Preconditions.checkArgument(blockId < MAX_BLOCK_ID, "blockId %s must be less than %s", blockId, MAX_BLOCK_ID)
            val mainBlock: Block
            val properties: BlockProperties
            try {
                mainBlock = blockClass.getConstructor().newInstance()
                mainBlock.clone() // Make sure clone works
                properties = mainBlock.properties
            } catch (e: Exception) {
                throw IllegalArgumentException("Could not create the main block of $blockClass", e)
            }
            list!![blockId] = blockClass
            solid!![blockId] = mainBlock.isSolid
            transparent!![blockId] = mainBlock.isTransparent
            diffusesSkyLight!![blockId] = mainBlock.diffusesSkyLight()
            hardness!![blockId] = mainBlock.hardness
            light!![blockId] = mainBlock.lightLevel
            lightFilter!![blockId] = mainBlock.lightFilter
            fullList!![blockId shl DATA_BITS] = mainBlock
            var metaAdded = false
            if (properties.getBitSize() > 0) {
                for (data in 0 until (1 shl DATA_BITS)) {
                    val fullId = blockId shl DATA_BITS or data
                    var constructor: Constructor<out Block?>? = null
                    var exception: Exception? = null
                    try {
                        val testing: Constructor<out Block?> = blockClass.getDeclaredConstructor(Number::class.java)
                        testing.newInstance(0).clone()
                        constructor = testing
                    } catch (e: ReflectiveOperationException) {
                        exception = e
                        try {
                            val testing: Constructor<out Block?> = blockClass.getDeclaredConstructor(Int::class.javaPrimitiveType)
                            testing.newInstance(0).clone()
                            constructor = testing
                            exception = null
                        } catch (e2: ReflectiveOperationException) {
                            e.addSuppressed(e2)
                            try {
                                val testing: Constructor<out Block?> = blockClass.getDeclaredConstructor(Integer::class.java)
                                testing.newInstance(0).clone()
                                constructor = testing
                                exception = null
                            } catch (e3: ReflectiveOperationException) {
                                e.addSuppressed(e3)
                            }
                        }
                    }
                    var b: Block? = null
                    if (constructor != null) {
                        try {
                            b = constructor.newInstance(data)
                            if (b.damage != data) {
                                b = BlockUnknown(blockId, data)
                            }
                        } catch (wrapper: InvocationTargetException) {
                            val uncaught: Throwable = wrapper.getTargetException()
                            if (uncaught is InvalidBlockStateException) {
                                b = BlockUnknown(blockId, data)
                            }
                        } catch (e: ReflectiveOperationException) {
                            exception = e
                        }
                    }
                    if (b == null) {
                        try {
                            b = BlockState.of(blockId, data).getBlock()
                        } catch (e: InvalidBlockStateException) {
                            b = BlockUnknown(blockId, data)
                        } catch (e: Exception) {
                            b = BlockUnknown(blockId, data)
                            if (exception != null) {
                                exception.addSuppressed(e)
                            } else {
                                log.error("Error while registering {} with meta {}", blockClass.getName(), data, exception)
                            }
                        }
                    }
                    if (!metaAdded && b !is BlockUnknown) {
                        metaAdded = true
                    }
                    fullList!![fullId] = b
                }
                hasMeta!![blockId] = metaAdded
            } else {
                hasMeta!![blockId] = false
            }
            Level.setCanRandomTick(blockId, receivesRandomTick)
        }

        private fun toolBreakTimeBonus0(toolType: Int, toolTier: Int, blockId: Int): Double {
            if (toolType == ItemTool.TYPE_SWORD) {
                if (blockId == BlockID.COBWEB) {
                    return 15.0
                }
                return if (blockId == BlockID.BAMBOO) {
                    30.0
                } else 1.0
            }
            if (toolType == ItemTool.TYPE_SHEARS) {
                if (blockId == WOOL || blockId == LEAVES || blockId == LEAVES2) {
                    return 5.0
                } else if (blockId == COBWEB) {
                    return 15.0
                }
                return 1.0
            }
            return if (toolType == ItemTool.TYPE_NONE) 1.0 else when (toolTier) {
                ItemTool.TIER_WOODEN -> 2.0
                ItemTool.TIER_STONE -> 4.0
                ItemTool.TIER_IRON -> 6.0
                ItemTool.TIER_DIAMOND -> 8.0
                ItemTool.TIER_NETHERITE -> 9.0
                ItemTool.TIER_GOLD -> 12.0
                else -> {
                    if (toolTier == ItemTool.TIER_NETHERITE) {
                        9.0
                    } else 1.0
                }
            }
        }

        private fun speedBonusByEfficiencyLore0(efficiencyLoreLevel: Int): Double {
            return if (efficiencyLoreLevel == 0) 0 else (efficiencyLoreLevel * efficiencyLoreLevel + 1).toDouble()
        }

        private fun speedRateByHasteLore0(hasteLoreLevel: Int): Double {
            return 1.0 + 0.2 * hasteLoreLevel
        }

        @PowerNukkitDifference(info = "Special condition for the leaves", since = "1.4.0.0-PN")
        private fun toolType0(item: Item, blockId: Int): Int {
            if (blockId == LEAVES && item.isHoe() || blockId == LEAVES2 && item.isHoe()) return ItemTool.TYPE_SHEARS
            if (item.isSword()) return ItemTool.TYPE_SWORD
            if (item.isShovel()) return ItemTool.TYPE_SHOVEL
            if (item.isPickaxe()) return ItemTool.TYPE_PICKAXE
            if (item.isAxe()) return ItemTool.TYPE_AXE
            if (item.isHoe()) return ItemTool.TYPE_HOE
            return if (item.isShears()) ItemTool.TYPE_SHEARS else ItemTool.TYPE_NONE
        }

        @PowerNukkitDifference(info = "Special condition for the leaves", since = "1.4.0.0-PN")
        private fun correctTool0(blockToolType: Int, item: Item, blockId: Int): Boolean {
            return if (blockId == LEAVES && item.isHoe() ||
                    blockId == LEAVES2 && item.isHoe()) {
                blockToolType == ItemTool.TYPE_SHEARS && item.isHoe()
            } else if (blockId == BAMBOO && item.isSword()) {
                blockToolType == ItemTool.TYPE_AXE && item.isSword()
            } else blockToolType == ItemTool.TYPE_SWORD && item.isSword() ||
                    blockToolType == ItemTool.TYPE_SHOVEL && item.isShovel() ||
                    blockToolType == ItemTool.TYPE_PICKAXE && item.isPickaxe() ||
                    blockToolType == ItemTool.TYPE_AXE && item.isAxe() ||
                    blockToolType == ItemTool.TYPE_HOE && item.isHoe() ||
                    blockToolType == ItemTool.TYPE_SHEARS && item.isShears() || blockToolType == ItemTool.TYPE_NONE
        }

        //http://minecraft.gamepedia.com/Breaking
        private fun breakTime0(blockHardness: Double, correctTool: Boolean, canHarvestWithHand: Boolean,
                               blockId: Int, toolType: Int, toolTier: Int, efficiencyLoreLevel: Int, hasteEffectLevel: Int,
                               insideOfWaterWithoutAquaAffinity: Boolean, outOfWaterButNotOnGround: Boolean): Double {
            val baseTime = (if (correctTool || canHarvestWithHand) 1.5 else 5.0) * blockHardness
            var speed = 1.0 / baseTime
            if (correctTool) speed *= toolBreakTimeBonus0(toolType, toolTier, blockId)
            speed += speedBonusByEfficiencyLore0(efficiencyLoreLevel)
            speed *= speedRateByHasteLore0(hasteEffectLevel)
            if (insideOfWaterWithoutAquaAffinity) speed *= 0.2
            if (outOfWaterButNotOnGround) speed *= 0.2
            return 1.0 / speed
        }

        @JvmOverloads
        fun equals(b1: Block?, b2: Block?, checkDamage: Boolean = true): Boolean {
            if (b1 == null || b2 == null || b1.id != b2.id) {
                return false
            }
            return if (checkDamage) {
                val b1Default = b1.isDefaultState
                val b2Default = b2.isDefaultState
                if (b1Default != b2Default) {
                    false
                } else if (b1Default) { // both are default
                    true
                } else {
                    b1.getMutableState().equals(b2.getMutableState())
                }
            } else {
                true
            }
        }
    }
}