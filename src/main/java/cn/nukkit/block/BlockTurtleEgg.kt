package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockTurtleEgg @PowerNukkitOnly constructor(meta: Int) : BlockFlowable(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = TURTLE_EGG

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Turtle Egg"

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:PowerNukkitOnly
    var cracks: CrackState
        get() = getPropertyValue(CRACK_STATE)
        set(cracks) {
            setPropertyValue(CRACK_STATE, cracks)
        }

    @get:Override
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val hardness: Double
        get() = 0.5

    @get:Override
    override val resistance: Double
        get() = 2.5

    @get:PowerNukkitOnly
    @set:PowerNukkitOnly
    var eggCount: Int
        get() = getPropertyValue(EGG_COUNT)
        set(eggCount) {
            setPropertyValue(EGG_COUNT, eggCount)
        }

    @get:PowerNukkitOnly
    @get:DeprecationDetails(since = "1.4.0.0-PN", reason = "Magic values", replaceWith = "getCracks()")
    @get:Deprecated
    @set:PowerNukkitOnly
    @set:DeprecationDetails(since = "1.4.0.0-PN", reason = "Magic values", replaceWith = "setCracks(CrackState)")
    @set:Deprecated
    var crackState: Int
        get() = Math.min(getDamage() shr 2 and 3, CRACK_STATE_MAX_CRACKED)
        set(crackState) {
            var crackState = crackState
            crackState = MathHelper.clamp(crackState, 0, 2)
            setDamage(getDamage() and (DATA_MASK xor 12) or (crackState shl 2))
        }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (item.getBlock() != null && item.getBlockId() === TURTLE_EGG && (player == null || !player.isSneaking())) {
            val eggCount = eggCount
            if (eggCount >= 4) {
                return false
            }
            val newState: Block = getCurrentState().withProperty(EGG_COUNT, eggCount + 1).getBlock(this)
            val placeEvent = BlockPlaceEvent(
                    player,
                    newState,
                    this,
                    down(),
                    item
            )
            if (placeEvent.isCancelled()) {
                return false
            }
            if (!this.level.setBlock(this, placeEvent.getBlock(), true, true)) {
                return false
            }
            val placeBlock: Block = placeEvent.getBlock()
            this.level.addLevelSoundEvent(this,
                    LevelSoundEventPacket.SOUND_PLACE,
                    placeBlock.getRuntimeId())
            item.setCount(item.getCount() - 1)
            if (down().getId() === SAND) {
                this.level.addParticle(BoneMealParticle(this))
            }
            return true
        }
        return false
    }

    @Override
    override fun hasEntityCollision(): Boolean {
        return true
    }

    @get:Override
    override val minX: Double
        get() = x + 3.0 / 16

    @get:Override
    override val minZ: Double
        get() = z + 3.0 / 16

    @get:Override
    override val maxX: Double
        get() = x + 12.0 / 16

    @get:Override
    override val maxZ: Double
        get() = z + 12.0 / 16

    @get:Override
    override val maxY: Double
        get() = y + 7.0 / 16

    @Override
    protected override fun recalculateCollisionBoundingBox(): AxisAlignedBB {
        return SimpleAxisAlignedBB(minX, getMinY(), minZ, maxX, maxY + 0.25, maxZ)
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (down().getId() === BlockID.SAND) {
                val celestialAngle: Float = level.calculateCelestialAngle(level.getTime(), 1)
                val random: ThreadLocalRandom = ThreadLocalRandom.current()
                if (0.70 > celestialAngle && celestialAngle > 0.65 || random.nextInt(500) === 0) {
                    val crackState: CrackState = cracks
                    if (crackState !== CrackState.MAX_CRACKED) {
                        val newState = clone()
                        newState.cracks = crackState.getNext()
                        val event = BlockGrowEvent(this, newState)
                        this.level.getServer().getPluginManager().callEvent(event)
                        if (!event.isCancelled()) {
                            level.addSound(this, Sound.BLOCK_TURTLE_EGG_CRACK, 0.7f, 0.9f + random.nextFloat() * 0.2f)
                            this.level.setBlock(this, event.getNewState(), true, true)
                        }
                    } else {
                        hatch()
                    }
                }
            }
            return type
        }
        return 0
    }

    @PowerNukkitOnly
    fun hatch() {
        hatch(eggCount)
    }

    @PowerNukkitOnly
    fun hatch(eggs: Int) {
        hatch(eggs, BlockAir())
    }

    @PowerNukkitOnly
    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    fun hatch(eggs: Int, newState: Block?) {
        val turtleEggHatchEvent = TurtleEggHatchEvent(this, eggs, newState)
        //TODO Cancelled by default because EntityTurtle doesn't have AI yet, remove it when AI is added
        turtleEggHatchEvent.setCancelled(true)
        this.level.getServer().getPluginManager().callEvent(turtleEggHatchEvent)
        val eggsHatching: Int = turtleEggHatchEvent.getEggsHatching()
        if (!turtleEggHatchEvent.isCancelled()) {
            level.addSound(this, Sound.BLOCK_TURTLE_EGG_CRACK)
            var hasFailure = false
            for (i in 0 until eggsHatching) {
                this.level.addSound(this, Sound.BLOCK_TURTLE_EGG_CRACK)
                val creatureSpawnEvent = CreatureSpawnEvent(
                        EntityTurtle.NETWORK_ID,
                        add(0.3 + i * 0.2,
                                0,
                                0.3
                        ),
                        CreatureSpawnEvent.SpawnReason.TURTLE_EGG)
                this.level.getServer().getPluginManager().callEvent(creatureSpawnEvent)
                if (!creatureSpawnEvent.isCancelled()) {
                    val turtle: EntityTurtle = Entity.createEntity(
                            creatureSpawnEvent.getEntityNetworkId(),
                            creatureSpawnEvent.getPosition()) as EntityTurtle
                    if (turtle != null) {
                        turtle.setBreedingAge(-24000)
                        turtle.setHomePos(Vector3(x, y, z))
                        turtle.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_BABY, true)
                        turtle.setScale(0.16f)
                        turtle.spawnToAll()
                        continue
                    }
                }
                if (turtleEggHatchEvent.isRecalculateOnFailure()) {
                    turtleEggHatchEvent.setEggsHatching(turtleEggHatchEvent.getEggsHatching() - 1)
                    hasFailure = true
                }
            }
            if (hasFailure) {
                turtleEggHatchEvent.recalculateNewState()
            }
            this.level.setBlock(this, turtleEggHatchEvent.getNewState(), true, true)
        }
    }

    @Override
    override fun onEntityCollide(entity: Entity) {
        if (entity is EntityLiving
                && entity !is EntityChicken
                && entity !is EntityBat
                && entity !is EntityGhast
                && entity !is EntityPhantom
                && entity.getY() >= maxY) {
            val ev: Event
            if (entity is Player) {
                ev = PlayerInteractEvent(entity as Player, null, this, null, PlayerInteractEvent.Action.PHYSICAL)
            } else {
                ev = EntityInteractEvent(entity, this)
            }
            ev.setCancelled(ThreadLocalRandom.current().nextInt(200) > 0)
            this.level.getServer().getPluginManager().callEvent(ev)
            if (!ev.isCancelled()) {
                this.level.useBreakOn(this, null, null, true)
            }
        }
    }

    @Override
    override fun toItem(): Item {
        return ItemBlock(BlockTurtleEgg())
    }

    @Override
    override fun onBreak(item: Item): Boolean {
        var eggCount = eggCount
        if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) == null) {
            this.level.addSound(this, Sound.BLOCK_TURTLE_EGG_CRACK)
        }
        return if (eggCount == 1) {
            super.onBreak(item)
        } else {
            eggCount = eggCount - 1
            this.level.setBlock(this, this, true, true)
        }
    }

    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (!isValidSupport(block.down(1, 0))) {
            return false
        }
        return if (this.level.setBlock(this, this, true, true)) {
            if (down().getId() === BlockID.SAND) {
                this.level.addParticle(BoneMealParticle(this))
            }
            true
        } else {
            false
        }
    }

    @PowerNukkitOnly
    fun isValidSupport(support: Block): Boolean {
        return support.isSolid(BlockFace.UP) || support is BlockWallBase
    }

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return Item.EMPTY_ARRAY
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @Override
    override fun canPassThrough(): Boolean {
        return false
    }

    @Override
    override fun canBeFlowedInto(): Boolean {
        return false
    }

    @Override
    override fun clone(): BlockTurtleEgg {
        return super.clone() as BlockTurtleEgg
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.SAND_BLOCK_COLOR

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EGG_COUNT: BlockProperty<Integer> = ArrayBlockProperty("turtle_egg_count", false, arrayOf(1, 2, 3, 4), 2, "turtle_egg_count", false, arrayOf("one_egg", "two_egg", "three_egg", "four_egg"))

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val CRACK_STATE: ArrayBlockProperty<CrackState> = ArrayBlockProperty("cracked_state", false, CrackState::class.java)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(EGG_COUNT, CRACK_STATE)

        @PowerNukkitOnly
        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "New property system", replaceWith = "CrackState.NO_CRACKS")
        val CRACK_STATE_NO_CRACKS = 0

        @PowerNukkitOnly
        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "New property system", replaceWith = "CrackState.CRACKED")
        val CRACK_STATE_CRACKED = 1

        @PowerNukkitOnly
        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "New property system", replaceWith = "CrackState.MAX_CRACKED")
        val CRACK_STATE_MAX_CRACKED = 2
    }
}