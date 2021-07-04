package cn.nukkit.block

import cn.nukkit.Player

/**
 * Implements the main logic of all nether vines.
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
abstract class BlockVinesNether : BlockTransparentMeta {
    /**
     * Creates a nether vine with age `0`.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() {
    }

    /**
     * Creates a nether vine from a meta compatible with [.getProperties].
     * @throws InvalidBlockPropertyMetaException If the meta is incompatible
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(meta: Int) : super(meta) {
    }

    /**
     * The direction that the vine will grow, vertical direction is expected but future implementations
     * may also add horizontal directions.
     * @return Normally, up or down.
     */
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    abstract val growthDirection: BlockFace
    /**
     * The current age of this block.
     */
    /**
     * Changes the age of this block.
     * @param vineAge The new age
     * @throws InvalidBlockPropertyValueException If the value is outside the accepted range from `0` to [.getMaxVineAge], both inclusive.
     */
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Throws(InvalidBlockPropertyValueException::class)
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    abstract var vineAge: Int

    /**
     * The maximum accepted age of this block.
     * @return Positive, inclusive value.
     */
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    abstract val maxVineAge: Int

    /**
     * Changes the current vine age to a random new random age.
     *
     * @param pseudorandom If the the randomization should be pseudorandom.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun randomizeVineAge(pseudorandom: Boolean) {
        if (pseudorandom) {
            vineAge = ThreadLocalRandom.current().nextInt(maxVineAge)
            return
        }
        var chance = 1.0
        var age: Int
        val random: ThreadLocalRandom = ThreadLocalRandom.current()
        age = 0
        while (random.nextDouble() < chance) {
            chance *= 0.826
            ++age
        }
        vineAge = age
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        val support: Block = getSide(growthDirection.getOpposite())
        if (!isSupportValid(support)) {
            return false
        }
        if (support.getId() === getId()) {
            vineAge = Math.min(maxVineAge, (support as BlockVinesNether).vineAge + 1)
        } else {
            randomizeVineAge(true)
        }
        return super.place(item, block, target, face, fx, fy, fz, player)
    }

    @Override
    override fun onUpdate(type: Int): Int {
        return when (type) {
            Level.BLOCK_UPDATE_RANDOM -> {
                val maxVineAge = maxVineAge
                if (vineAge < maxVineAge && ThreadLocalRandom.current().nextInt(10) === 0 && findVineAge(true).orElse(maxVineAge) < maxVineAge) {
                    grow()
                }
                Level.BLOCK_UPDATE_RANDOM
            }
            Level.BLOCK_UPDATE_SCHEDULED -> {
                getLevel().useBreakOn(this, null, null, true)
                Level.BLOCK_UPDATE_SCHEDULED
            }
            Level.BLOCK_UPDATE_NORMAL -> {
                if (!isSupportValid) {
                    getLevel().scheduleUpdate(this, 1)
                }
                Level.BLOCK_UPDATE_NORMAL
            }
            else -> 0
        }
    }

    /**
     * Grow a single vine if possible. Calls [BlockGrowEvent] passing the positioned new state and the source block.
     * @return If the vine grew successfully.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun grow(): Boolean {
        val pos: Block = getSide(growthDirection)
        if (pos.getId() !== AIR || pos.y < 0 || 255 < pos.y) {
            return false
        }
        val growing = clone()
        growing.x = pos.x
        growing.y = pos.y
        growing.z = pos.z
        growing.vineAge = Math.min(vineAge + 1, maxVineAge)
        val ev = BlockGrowEvent(this, growing)
        Server.getInstance().getPluginManager().callEvent(ev)
        if (ev.isCancelled()) {
            return false
        }
        if (level.setBlock(pos, growing)) {
            increaseRootAge()
            return true
        }
        return false
    }

    /**
     * Grow a random amount of vines.
     * Calls [BlockGrowEvent] passing the positioned new state and the source block for each new vine being added
     * to the world, if one of the events gets cancelled the growth gets interrupted.
     * @return How many vines grew
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun growMultiple(): Int {
        val growthDirection: BlockFace = growthDirection
        var age = vineAge + 1
        val maxAge = maxVineAge
        val growing = clone()
        growing.randomizeVineAge(false)
        val blocksToGrow = growing.vineAge
        var grew = 0
        for (distance in 1..blocksToGrow) {
            val pos: Block = getSide(growthDirection, distance)
            if (pos.getId() !== AIR || pos.y < 0 || 255 < pos.y) {
                break
            }
            growing.vineAge = Math.min(age++, maxAge)
            growing.x = pos.x
            growing.y = pos.y
            growing.z = pos.z
            val ev = BlockGrowEvent(this, growing.clone())
            Server.getInstance().getPluginManager().callEvent(ev)
            if (ev.isCancelled()) {
                break
            }
            if (!level.setBlock(pos, ev.getNewState())) {
                break
            }
            grew++
        }
        if (grew > 0) {
            increaseRootAge()
        }
        return grew
    }

    /**
     * Attempt to get the age of the root or the head of the vine.
     * @param base True to get the age of the base (oldest block), false to get the age of the head (newest block)
     * @return Empty if the target could not be reached. The age of the target if it was found.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun findVineAge(base: Boolean): OptionalInt {
        return findVineBlock(base)
                .map { vine -> OptionalInt.of(vine.getVineAge()) }
                .orElse(OptionalInt.empty())
    }

    /**
     * Attempt to find the root or the head of the vine transversing the growth direction for up to 256 blocks.
     * @param base True to find the base (oldest block), false to find the head (newest block)
     * @return Empty if the target could not be reached or the block there isn't an instance of [BlockVinesNether].
     * The positioned block of the target if it was found.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun findVineBlock(base: Boolean): Optional<BlockVinesNether> {
        return findVine(base)
                .map(Position::getLevelBlock)
                .filter(BlockVinesNether::class.java::isInstance)
                .map(BlockVinesNether::class.java::cast)
    }

    /**
     * Attempt to find the root or the head of the vine transversing the growth direction for up to 256 blocks.
     * @param base True to find the base (oldest block), false to find the head (newest block)
     * @return Empty if the target could not be reached. The position of the target if it was found.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun findVine(base: Boolean): Optional<Position> {
        var supportFace: BlockFace = growthDirection
        if (base) {
            supportFace = supportFace.getOpposite()
        }
        var result: Position = getLocation()
        val id: Int = getId()
        var limit = 256
        while (--limit > 0) {
            val next: Position = result.getSide(supportFace)
            result = if (next.getLevelBlockState().getBlockId() === id) {
                next
            } else {
                break
            }
        }
        return if (limit == -1) Optional.empty() else Optional.of(result)
    }

    /**
     * Attempts to increase the age of the base of the nether vine.
     * @return
     *  * `EMPTY` if the base could not be reached or have an invalid instance type
     *  * `TRUE` if the base was changed successfully
     *  * `FALSE` if the base was already in the max age or the block change was refused
     *
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun increaseRootAge(): OptionalBoolean {
        val base: Block = findVine(true).map(Position::getLevelBlock).orElse(null) as? BlockVinesNether
                ?: return OptionalBoolean.EMPTY
        val baseVine = base as BlockVinesNether
        val vineAge = baseVine.vineAge
        if (vineAge < baseVine.maxVineAge) {
            baseVine.vineAge = vineAge + 1
            if (getLevel().setBlock(baseVine, baseVine)) {
                return OptionalBoolean.TRUE
            }
        }
        return OptionalBoolean.FALSE
    }

    @Override
    override fun onActivate(@Nonnull item: Item, @Nullable player: Player?): Boolean {
        if (!item.isFertilizer()) {
            return false
        }
        getLevel().addParticle(BoneMealParticle(this))
        findVineBlock(false).ifPresent { obj: BlockVinesNether -> obj.growMultiple() }
        if (player != null && !player.isCreative()) {
            item.count--
        }
        return true
    }

    @Override
    override fun getDrops(item: Item): Array<Item> {
        // They have a 33% (3/9) chance to drop a single weeping vine when broken, 
        // increased to 55% (5/9) with Fortune I, 
        // 77% (7/9) with Fortune II, 
        // and 100% with Fortune III. 
        // 
        // They always drop a single weeping vine when broken with shears or a tool enchanted with Silk Touch.
        var enchantmentLevel: Int
        if (item.isShears() || item.getEnchantmentLevel(Enchantment.ID_FORTUNE_DIGGING).also { enchantmentLevel = it } >= 3) {
            return arrayOf<Item>(toItem())
        }
        val chance = 3 + enchantmentLevel * 2
        return if (ThreadLocalRandom.current().nextInt(9) < chance) {
            arrayOf<Item>(toItem())
        } else Item.EMPTY_ARRAY
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun isSupportValid(@Nonnull support: Block): Boolean {
        return support.getId() === getId() || !support.isTransparent()
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val isSupportValid: Boolean
        get() = isSupportValid(getSide(growthDirection.getOpposite()))

    @Override
    override fun onEntityCollide(entity: Entity) {
        entity.resetFallDistance()
    }

    @Override
    override fun hasEntityCollision(): Boolean {
        return true
    }

    @get:Override
    override val hardness: Double
        get() = 0

    @get:Override
    override val resistance: Double
        get() = 0

    @Override
    override fun canBeClimbed(): Boolean {
        return true
    }

    @Override
    override fun canBeFlowedInto(): Boolean {
        return true
    }

    @get:Override
    override val isSolid: Boolean
        get() = false

    @get:Override
    override val minX: Double
        get() = x + 4 / 16.0

    @get:Override
    override val minZ: Double
        get() = z + 4 / 16.0

    @get:Override
    override val maxX: Double
        get() = x + 12 / 16.0

    @get:Override
    override val maxZ: Double
        get() = z + 12 / 16.0

    @get:Override
    override val maxY: Double
        get() = y + 15 / 16.0

    @Override
    override fun canPassThrough(): Boolean {
        return true
    }

    @Override
    override fun sticksToPiston(): Boolean {
        return false
    }

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @Override
    override fun clone(): BlockVinesNether {
        return super.clone() as BlockVinesNether
    }
}