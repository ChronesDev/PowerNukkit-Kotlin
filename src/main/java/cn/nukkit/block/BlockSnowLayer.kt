package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author xtypr, joserobjr
 * @since 2015/12/6
 */
@PowerNukkitDifference(info = "Extends BlockFallableMeta instead of BlockFallable")
class BlockSnowLayer : BlockFallableMeta {
    constructor() {
        // Does nothing
    }

    constructor(meta: Int) : super(meta) {}

    @get:Override
    override val name: String
        get() = "Top Snow"

    @get:Override
    override val id: Int
        get() = SNOW_LAYER

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var snowHeight: Int
        get() = getIntValue(SNOW_HEIGHT)
        set(snowHeight) {
            setIntValue(SNOW_HEIGHT, snowHeight)
        }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isCovered: Boolean
        get() = getBooleanValue(COVERED)
        set(covered) {
            setBooleanValue(COVERED, covered)
        }

    @get:Override
    @get:PowerNukkitDifference(since = "1.4.0.0-PN", info = "Returns the max Y based on the snow height")
    override val maxY: Double
        get() = y + Math.min(16, snowHeight + 1) * 2 / 16.0

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Renders a bounding box that the entities stands on top")
    @Override
    @Nullable
    protected override fun recalculateBoundingBox(): AxisAlignedBB? {
        val snowHeight = snowHeight
        if (snowHeight < 3) {
            return null
        }
        return if (snowHeight == 3 || snowHeight == SNOW_HEIGHT.getMaxValue()) {
            this
        } else SimpleAxisAlignedBB(x, y, z, x + 1, y + 8 / 16.0, z + 1)
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Renders a bounding box with the actual snow_layer height")
    @Override
    protected override fun recalculateCollisionBoundingBox(): AxisAlignedBB {
        return this
    }

    @get:Override
    override val hardness: Double
        get() = 0.1

    @get:Override
    @get:PowerNukkitDifference(since = "1.4.0.0-PN", info = "0.1 instead of 0.5")
    override val resistance: Double
        get() = 0.1

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_SHOVEL

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Returns false if it has all the 8 layers")
    @Override
    override fun canBeReplaced(): Boolean {
        return snowHeight < SNOW_HEIGHT.getMaxValue()
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will increase the layers and behave as expected in vanilla and will cover grass blocks")
    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        val increment: Optional<BlockSnowLayer> = Stream.of(target, block)
                .filter { b -> b.getId() === SNOW_LAYER }.map(BlockSnowLayer::class.java::cast)
                .filter { b -> b.getSnowHeight() < SNOW_HEIGHT.getMaxValue() }
                .findFirst()
        if (increment.isPresent()) {
            val other: BlockSnowLayer = increment.get()
            if (Arrays.stream(level.getCollidingEntities(SimpleAxisAlignedBB(
                            other.x, other.y, other.z,
                            other.x + 1, other.y + 1, other.z + 1
                    ))).anyMatch { e -> e is EntityLiving }) {
                return false
            }
            other.snowHeight = other.snowHeight + 1
            return level.setBlock(other, other, true)
        }
        val down: Block = down()
        if (!down.isSolid()) {
            return false
        }
        when (down.getId()) {
            BARRIER, STRUCTURE_VOID -> return false
            GRASS -> isCovered = true
            TALL_GRASS -> {
                if (!level.setBlock(this, 0, this, true)) {
                    return false
                }
                level.setBlock(block, 1, block, true, false)
                return true
            }
            else -> {
            }
        }
        return this.getLevel().setBlock(block, this, true)
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will move the block in layer 1 to layer 0 when breaking in layer 0")
    @Override
    override fun onBreak(item: Item?): Boolean {
        return if (layer !== 0) {
            super.onBreak(item)
        } else this.getLevel().setBlock(this, 0, getLevelBlockAtLayer(1), true, true)
    }

    @Since("1.2.1.0-PN")
    @PowerNukkitOnly
    @Override
    fun afterRemoval(newBlock: Block, update: Boolean) {
        if (layer !== 0 || newBlock.getId() === id) {
            return
        }
        val layer1: Block = getLevelBlockAtLayer(1)
        if (layer1.getId() !== TALL_GRASS) {
            return
        }

        // Clear the layer1 block and do a small hack as workaround a vanilla client rendering bug
        val level: Level = getLevel()
        level.setBlock(this, 0, layer1, true, false)
        level.setBlock(this, 1, get(AIR), true, false)
        level.setBlock(this, 0, newBlock, true, false)
        Server.getInstance().getScheduler().scheduleDelayedTask({
            val target: Array<Player> = level.getChunkPlayers(getChunkX(), getChunkZ()).values().toArray(Player.EMPTY_ARRAY)
            val blocks: Array<Vector3> = arrayOf<Vector3>(getLocation())
            level.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0, false)
            level.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1, false)
        }, 10)
        val target: Array<Player> = level.getChunkPlayers(getChunkX(), getChunkZ()).values().toArray(Player.EMPTY_ARRAY)
        val blocks: Array<Vector3> = arrayOf<Vector3>(getLocation())
        level.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0, false)
        level.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1, false)
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will melt on dry biomes and will melt gradually and will cover grass blocks")
    @Override
    override fun onUpdate(type: Int): Int {
        super.onUpdate(type)
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            val biome: Biome = Biome.getBiome(getLevel().getBiomeId(getFloorX(), getFloorZ()))
            if (biome.isDry() || this.getLevel().getBlockLightAt(getFloorX(), getFloorY(), getFloorZ()) >= 10) {
                melt()
                return Level.BLOCK_UPDATE_RANDOM
            }
        } else if (type == Level.BLOCK_UPDATE_NORMAL) {
            val covered = down().getId() === GRASS
            if (isCovered != covered) {
                isCovered = covered
                level.setBlock(this, this, true)
                return type
            }
        }
        return 0
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun melt(): Boolean {
        return melt(2)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun melt(layers: Int): Boolean {
        Preconditions.checkArgument(layers > 0, "Layers must be positive, got {}", layers)
        var toMelt: Block = this
        while (toMelt.getIntValue(SNOW_HEIGHT) === SNOW_HEIGHT.getMaxValue()) {
            val up: Block = toMelt.up()
            if (up.getId() !== SNOW_LAYER) {
                break
            }
            toMelt = up
        }
        val snowHeight: Int = toMelt.getIntValue(SNOW_HEIGHT) - layers
        val newState: Block = if (snowHeight < 0) get(AIR) else getCurrentState().withProperty(SNOW_HEIGHT, snowHeight).getBlock(toMelt)
        val event = BlockFadeEvent(toMelt, newState)
        level.getServer().getPluginManager().callEvent(event)
        return if (event.isCancelled()) {
            false
        } else level.setBlock(toMelt, event.getNewState(), true)
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Returns the snow_layer but with 0 height")
    @Override
    override fun toItem(): Item {
        return Item.getBlock(BlockID.SNOW_LAYER)
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed the amount of snowballs that are dropped")
    @Override
    override fun getDrops(item: Item): Array<Item> {
        if (!item.isShovel() || item.getTier() < ItemTool.TIER_WOODEN) {
            return Item.EMPTY_ARRAY
        }
        val amount: Int
        amount = when (snowHeight) {
            0, 1, 2 -> 1
            3, 4 -> 2
            5, 6 -> 3
            7 -> 4
            else -> 4
        }
        return arrayOf<Item>(Item.get(ItemID.SNOWBALL, 0, amount))
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.SNOW_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val isTransparent: Boolean
        get() = true

    @Override
    override fun canBeFlowedInto(): Boolean {
        return true
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Returns false when the height is 3+")
    @Override
    override fun canPassThrough(): Boolean {
        return snowHeight < 3
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace): Boolean {
        return side === BlockFace.UP && snowHeight == SNOW_HEIGHT.getMaxValue()
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val SNOW_HEIGHT: IntBlockProperty = IntBlockProperty("height", true, 7)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val COVERED: BooleanBlockProperty = BooleanBlockProperty("covered_bit", false)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(SNOW_HEIGHT, COVERED)
    }
}