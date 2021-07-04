package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class BlockLiquid protected constructor(meta: Int) : BlockTransparentMeta(meta) {
    var adjacentSources = 0
    protected var flowVector: Vector3? = null
    private val flowCostVisited: Long2ByteMap = Long2ByteOpenHashMap()

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @Override
    override fun canBeFlowedInto(): Boolean {
        return true
    }

    @Override
    protected override fun recalculateBoundingBox(): AxisAlignedBB? {
        return null
    }

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return Item.EMPTY_ARRAY
    }

    @Override
    override fun hasEntityCollision(): Boolean {
        return true
    }

    @Override
    override fun isBreakable(item: Item?): Boolean {
        return false
    }

    @Override
    override fun canBeReplaced(): Boolean {
        return true
    }

    @get:Override
    override val isSolid: Boolean
        get() = false

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace?): Boolean {
        return false
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val boundingBox: AxisAlignedBB?
        get() = null

    @get:Override
    override val maxY: Double
        get() = this.y + 1 - fluidHeightPercent

    @Override
    protected override fun recalculateCollisionBoundingBox(): AxisAlignedBB {
        return this
    }

    fun usesWaterLogging(): Boolean {
        return false
    }

    val fluidHeightPercent: Float
        get() {
            var d = liquidDepth.toFloat()
            if (d >= 8) {
                d = 0f
            }
            return (d + 1) / 9f
        }

    protected fun getFlowDecay(block: Block): Int {
        if (block.getId() !== this.getId()) {
            val layer1: Block = block.getLevelBlockAtLayer(1)
            return if (layer1.getId() !== this.getId()) {
                -1
            } else {
                (layer1 as BlockLiquid).liquidDepth
            }
        }
        return (block as BlockLiquid).liquidDepth
    }

    protected fun getEffectiveFlowDecay(block: Block): Int {
        var block: Block = block
        if (block.getId() !== this.getId()) {
            block = block.getLevelBlockAtLayer(1)
            if (block.getId() !== this.getId()) {
                return -1
            }
        }
        var decay = (block as BlockLiquid).liquidDepth
        if (decay >= 8) {
            decay = 0
        }
        return decay
    }

    fun clearCaches() {
        flowVector = null
        flowCostVisited.clear()
    }

    fun getFlowVector(): Vector3? {
        if (flowVector != null) {
            return flowVector
        }
        var vector = Vector3(0, 0, 0)
        val decay = getEffectiveFlowDecay(this)
        for (j in 0..3) {
            var x = this.x as Int
            val y = this.y as Int
            var z = this.z as Int
            when (j) {
                0 -> --x
                1 -> x++
                2 -> z--
                else -> z++
            }
            val sideBlock: Block = this.level.getBlock(x, y, z)
            var blockDecay = getEffectiveFlowDecay(sideBlock)
            if (blockDecay < 0) {
                if (!sideBlock.canBeFlowedInto()) {
                    continue
                }
                blockDecay = getEffectiveFlowDecay(this.level.getBlock(x, y - 1, z))
                if (blockDecay >= 0) {
                    val realDecay = blockDecay - (decay - 8)
                    vector.x += (sideBlock.x - x) * realDecay
                    vector.y += (sideBlock.y - y) * realDecay
                    vector.z += (sideBlock.z - z) * realDecay
                }
            } else {
                val realDecay = blockDecay - decay
                vector.x += (sideBlock.x - x) * realDecay
                vector.y += (sideBlock.y - y) * realDecay
                vector.z += (sideBlock.z - z) * realDecay
            }
        }
        if (liquidDepth >= 8) {
            if (!canFlowInto(this.level.getBlock(this.x as Int, this.y as Int, this.z as Int - 1)) ||
                    !canFlowInto(this.level.getBlock(this.x as Int, this.y as Int, this.z as Int + 1)) ||
                    !canFlowInto(this.level.getBlock(this.x as Int - 1, this.y as Int, this.z as Int)) ||
                    !canFlowInto(this.level.getBlock(this.x as Int + 1, this.y as Int, this.z as Int)) ||
                    !canFlowInto(this.level.getBlock(this.x as Int, this.y as Int + 1, this.z as Int - 1)) ||
                    !canFlowInto(this.level.getBlock(this.x as Int, this.y as Int + 1, this.z as Int + 1)) ||
                    !canFlowInto(this.level.getBlock(this.x as Int - 1, this.y as Int + 1, this.z as Int)) ||
                    !canFlowInto(this.level.getBlock(this.x as Int + 1, this.y as Int + 1, this.z as Int))) {
                vector = vector.normalize().add(0, -6, 0)
            }
        }
        return vector.normalize().also { flowVector = it }
    }

    @Override
    override fun addVelocityToEntity(entity: Entity, vector: Vector3) {
        if (entity.canBeMovedByCurrents()) {
            val flow: Vector3? = getFlowVector()
            vector.x += flow.x
            vector.y += flow.y
            vector.z += flow.z
        }
    }

    val flowDecayPerBlock: Int
        get() = 1

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            checkForHarden()
            if (usesWaterLogging() && layer > 0) {
                val layer0: Block = this.level.getBlock(this, 0)
                if (layer0.getId() === 0) {
                    this.level.setBlock(this, 1, Block.get(BlockID.AIR), false, false)
                    this.level.setBlock(this, 0, this, false, false)
                } else if (layer0.getWaterloggingLevel() <= 0 || layer0.getWaterloggingLevel() === 1 && liquidDepth > 0) {
                    this.level.setBlock(this, 1, Block.get(BlockID.AIR), true, true)
                }
            }
            this.level.scheduleUpdate(this, this.tickRate())
            return 0
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            var decay = getFlowDecay(this)
            val multiplier = flowDecayPerBlock
            if (decay > 0) {
                var smallestFlowDecay = -100
                adjacentSources = 0
                smallestFlowDecay = getSmallestFlowDecay(this.level.getBlock(this.x as Int, this.y as Int, this.z as Int - 1), smallestFlowDecay)
                smallestFlowDecay = getSmallestFlowDecay(this.level.getBlock(this.x as Int, this.y as Int, this.z as Int + 1), smallestFlowDecay)
                smallestFlowDecay = getSmallestFlowDecay(this.level.getBlock(this.x as Int - 1, this.y as Int, this.z as Int), smallestFlowDecay)
                smallestFlowDecay = getSmallestFlowDecay(this.level.getBlock(this.x as Int + 1, this.y as Int, this.z as Int), smallestFlowDecay)
                var newDecay = smallestFlowDecay + multiplier
                if (newDecay >= 8 || smallestFlowDecay < 0) {
                    newDecay = -1
                }
                val topFlowDecay = getFlowDecay(this.level.getBlock(this.x as Int, this.y as Int + 1, this.z as Int))
                if (topFlowDecay >= 0) {
                    newDecay = topFlowDecay or 0x08
                }
                if (adjacentSources >= 2 && this is BlockWater) {
                    var bottomBlock: Block = this.level.getBlock(this.x as Int, this.y as Int - 1, this.z as Int)
                    if (bottomBlock.isSolid()) {
                        newDecay = 0
                    } else if (bottomBlock is BlockWater && bottomBlock.getLiquidDepth() === 0) {
                        newDecay = 0
                    } else {
                        bottomBlock = bottomBlock.getLevelBlockAtLayer(1)
                        if (bottomBlock is BlockWater && bottomBlock.getLiquidDepth() === 0) {
                            newDecay = 0
                        }
                    }
                }
                if (newDecay != decay) {
                    decay = newDecay
                    val decayed = decay < 0
                    val to: Block
                    to = if (decayed) {
                        Block.get(BlockID.AIR)
                    } else {
                        getBlock(decay)
                    }
                    val event = BlockFromToEvent(this, to)
                    level.getServer().getPluginManager().callEvent(event)
                    if (!event.isCancelled()) {
                        this.level.setBlock(this, layer, event.getTo(), true, true)
                        if (!decayed) {
                            this.level.scheduleUpdate(this, this.tickRate())
                        }
                    }
                }
            }
            if (decay >= 0) {
                val bottomBlock: Block = this.level.getBlock(this.x as Int, this.y as Int - 1, this.z as Int)
                flowIntoBlock(bottomBlock, decay or 0x08)
                if (decay == 0 || !(if (usesWaterLogging()) bottomBlock.canWaterloggingFlowInto() else bottomBlock.canBeFlowedInto())) {
                    val adjacentDecay: Int
                    adjacentDecay = if (decay >= 8) {
                        1
                    } else {
                        decay + multiplier
                    }
                    if (adjacentDecay < 8) {
                        val flags = optimalFlowDirections
                        if (flags[0]) {
                            flowIntoBlock(this.level.getBlock(this.x as Int - 1, this.y as Int, this.z as Int), adjacentDecay)
                        }
                        if (flags[1]) {
                            flowIntoBlock(this.level.getBlock(this.x as Int + 1, this.y as Int, this.z as Int), adjacentDecay)
                        }
                        if (flags[2]) {
                            flowIntoBlock(this.level.getBlock(this.x as Int, this.y as Int, this.z as Int - 1), adjacentDecay)
                        }
                        if (flags[3]) {
                            flowIntoBlock(this.level.getBlock(this.x as Int, this.y as Int, this.z as Int + 1), adjacentDecay)
                        }
                    }
                }
                checkForHarden()
            }
        }
        return 0
    }

    protected fun flowIntoBlock(block: Block, newFlowDecay: Int) {
        var block: Block = block
        if (canFlowInto(block) && block !is BlockLiquid) {
            if (usesWaterLogging()) {
                val layer1: Block = block.getLevelBlockAtLayer(1)
                if (layer1 is BlockLiquid) {
                    return
                }
                if (block.getWaterloggingLevel() > 1) {
                    block = layer1
                }
            }
            val event = LiquidFlowEvent(block, this, newFlowDecay)
            level.getServer().getPluginManager().callEvent(event)
            if (!event.isCancelled()) {
                if (block.layer === 0 && block.getId() > 0) {
                    this.level.useBreakOn(block, if (block.getId() === COBWEB) Item.get(Item.WOODEN_SWORD) else null)
                }
                this.level.setBlock(block, block.layer, getBlock(newFlowDecay), true, true)
                this.level.scheduleUpdate(block, this.tickRate())
            }
        }
    }

    private fun calculateFlowCost(blockX: Int, blockY: Int, blockZ: Int, accumulatedCost: Int, maxCost: Int, originOpposite: Int, lastOpposite: Int): Int {
        var cost = 1000
        for (j in 0..3) {
            if (j == originOpposite || j == lastOpposite) {
                continue
            }
            var x = blockX
            var z = blockZ
            if (j == 0) {
                --x
            } else if (j == 1) {
                ++x
            } else if (j == 2) {
                --z
            } else if (j == 3) {
                ++z
            }
            val hash: Long = Level.blockHash(x, blockY, z)
            if (!flowCostVisited.containsKey(hash)) {
                val blockSide: Block = this.level.getBlock(x, blockY, z)
                if (!canFlowInto(blockSide)) {
                    flowCostVisited.put(hash, BLOCKED)
                } else if (if (usesWaterLogging()) this.level.getBlock(x, blockY - 1, z).canWaterloggingFlowInto() else this.level.getBlock(x, blockY - 1, z).canBeFlowedInto()) {
                    flowCostVisited.put(hash, CAN_FLOW_DOWN)
                } else {
                    flowCostVisited.put(hash, CAN_FLOW)
                }
            }
            val status: Byte = flowCostVisited.get(hash)
            if (status == BLOCKED) {
                continue
            } else if (status == CAN_FLOW_DOWN) {
                return accumulatedCost
            }
            if (accumulatedCost >= maxCost) {
                continue
            }
            val realCost = calculateFlowCost(x, blockY, z, accumulatedCost + 1, maxCost, originOpposite, j xor 0x01)
            if (realCost < cost) {
                cost = realCost
            }
        }
        return cost
    }

    @get:Override
    override val hardness: Double
        get() = 100.0

    @get:Override
    override val resistance: Double
        get() = 500
    private val optimalFlowDirections: BooleanArray
        private get() {
            val flowCost = intArrayOf(
                    1000,
                    1000,
                    1000,
                    1000
            )
            var maxCost = 4 / flowDecayPerBlock
            for (j in 0..3) {
                var x = this.x as Int
                val y = this.y as Int
                var z = this.z as Int
                if (j == 0) {
                    --x
                } else if (j == 1) {
                    ++x
                } else if (j == 2) {
                    --z
                } else {
                    ++z
                }
                val block: Block = this.level.getBlock(x, y, z)
                if (!canFlowInto(block)) {
                    flowCostVisited.put(Level.blockHash(x, y, z), BLOCKED)
                } else if (if (usesWaterLogging()) this.level.getBlock(x, y - 1, z).canWaterloggingFlowInto() else this.level.getBlock(x, y - 1, z).canBeFlowedInto()) {
                    flowCostVisited.put(Level.blockHash(x, y, z), CAN_FLOW_DOWN)
                    maxCost = 0
                    flowCost[j] = maxCost
                } else if (maxCost > 0) {
                    flowCostVisited.put(Level.blockHash(x, y, z), CAN_FLOW)
                    flowCost[j] = calculateFlowCost(x, y, z, 1, maxCost, j xor 0x01, j xor 0x01)
                    maxCost = Math.min(maxCost, flowCost[j])
                }
            }
            flowCostVisited.clear()
            var minCost = Double.MAX_VALUE
            for (i in 0..3) {
                val d = flowCost[i].toDouble()
                if (d < minCost) {
                    minCost = d
                }
            }
            val isOptimalFlowDirection = BooleanArray(4)
            for (i in 0..3) {
                isOptimalFlowDirection[i] = flowCost[i] == minCost
            }
            return isOptimalFlowDirection
        }

    private fun getSmallestFlowDecay(block: Block, decay: Int): Int {
        var blockDecay = getFlowDecay(block)
        if (blockDecay < 0) {
            return decay
        } else if (blockDecay == 0) {
            ++adjacentSources
        } else if (blockDecay >= 8) {
            blockDecay = 0
        }
        return if (decay >= 0 && blockDecay >= decay) decay else blockDecay
    }

    protected fun checkForHarden() {}
    protected fun triggerLavaMixEffects(pos: Vector3) {
        val random: Random = ThreadLocalRandom.current()
        this.getLevel().addLevelEvent(pos.add(0.5, 0.5, 0.5), LevelEventPacket.EVENT_SOUND_FIZZ, ((random.nextFloat() - random.nextFloat()) * 800) as Int + 2600)
        for (i in 0..7) {
            this.getLevel().addParticle(SmokeParticle(pos.add(Math.random(), 1.2, Math.random())))
        }
    }

    abstract fun getBlock(meta: Int): BlockLiquid

    @Override
    override fun canPassThrough(): Boolean {
        return true
    }

    @Override
    override fun onEntityCollide(entity: Entity) {
        entity.resetFallDistance()
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    fun liquidCollide(cause: Block?, result: Block?): Boolean {
        val event = BlockFromToEvent(this, result)
        this.level.getServer().getPluginManager().callEvent(event)
        if (event.isCancelled()) {
            return false
        }
        this.level.setBlock(this, event.getTo(), true, true)
        this.level.setBlock(this, 1, Block.get(BlockID.AIR), true, true)
        this.getLevel().addSound(this.add(0.5, 0.5, 0.5), Sound.RANDOM_FIZZ)
        return true
    }

    protected fun canFlowInto(block: Block): Boolean {
        if (usesWaterLogging()) {
            if (block.canWaterloggingFlowInto()) {
                val blockLayer1: Block = block.getLevelBlockAtLayer(1)
                return !(block is BlockLiquid && block.liquidDepth == 0) && !(blockLayer1 is BlockLiquid && blockLayer1.liquidDepth == 0)
            }
        }
        return block.canBeFlowedInto() && !(block is BlockLiquid && block.liquidDepth == 0)
    }

    @Override
    override fun toItem(): Item {
        return ItemBlock(Block.get(BlockID.AIR))
    }

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @Override
    override fun sticksToPiston(): Boolean {
        return false
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var liquidDepth: Int
        get() = getPropertyValue(LIQUID_DEPTH)
        set(liquidDepth) {
            setPropertyValue(LIQUID_DEPTH, liquidDepth)
        }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val isSource: Boolean
        get() = liquidDepth == 0

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val depthOnTop: Int
        get() {
            var liquidDepth = liquidDepth
            if (liquidDepth > 8) {
                liquidDepth -= 8
            }
            return liquidDepth
        }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val isFlowingDown: Boolean
        get() = liquidDepth >= 8

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val isSourceOrFlowingDown: Boolean
        get() {
            val liquidDepth = liquidDepth
            return liquidDepth == 0 || liquidDepth == 8
        }

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val lightFilter: Int
        get() = 2

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val LIQUID_DEPTH: IntBlockProperty = IntBlockProperty("liquid_depth", false, 15)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(LIQUID_DEPTH)
        private const val CAN_FLOW_DOWN: Byte = 1
        private const val CAN_FLOW: Byte = 0
        private const val BLOCKED: Byte = -1
    }
}