package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockSweetBerryBush @PowerNukkitOnly constructor(meta: Int) : BlockFlowable(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = SWEET_BERRY_BUSH

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Sweet Berry Bush"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.FOLIAGE_BLOCK_COLOR

    @get:Override
    override val burnChance: Int
        get() = 30

    @get:Override
    override val burnAbility: Int
        get() = 60

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    override val hardness: Double
        get() = if (getDamage() === 0) 0 else 0.25

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        val age: Int = MathHelper.clamp(getDamage(), 0, 3)
        if (age < 3 && item.isFertilizer()) {
            val block = this.clone() as BlockSweetBerryBush
            block.setDamage(block.getDamage() + 1)
            if (block.getDamage() > 3) {
                block.setDamage(3)
            }
            val ev = BlockGrowEvent(this, block)
            Server.getInstance().getPluginManager().callEvent(ev)
            if (ev.isCancelled()) {
                return false
            }
            this.getLevel().setBlock(this, ev.getNewState(), false, true)
            this.level.addParticle(BoneMealParticle(this))
            if (player != null && player.gamemode and 0x01 === 0) {
                item.count--
            }
            return true
        }
        if (age < 2) {
            return true
        }
        var amount: Int = 1 + ThreadLocalRandom.current().nextInt(2)
        if (age == 3) {
            amount++
        }
        val event = BlockHarvestEvent(this,
                BlockSweetBerryBush(1), arrayOf<Item>(ItemSweetBerries(0, amount)))
        getLevel().getServer().getPluginManager().callEvent(event)
        if (!event.isCancelled()) {
            getLevel().setBlock(this, event.getNewState(), true, true)
            val drops: Array<Item> = event.getDrops()
            if (drops != null) {
                val dropPos: Position = add(0.5, 0.5, 0.5)
                for (drop in drops) {
                    if (drop != null) {
                        getLevel().dropItem(dropPos, drop)
                    }
                }
            }
        }
        return true
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isSupportValid(down())) {
                this.getLevel().useBreakOn(this)
                return Level.BLOCK_UPDATE_NORMAL
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (getDamage() < 3 && ThreadLocalRandom.current().nextInt(5) === 0 && getLevel().getFullLight(add(0, 1, 0)) >= BlockCrops.MINIMUM_LIGHT_LEVEL) {
                val event = BlockGrowEvent(this, Block.get(id, getDamage() + 1))
                if (!event.isCancelled()) {
                    getLevel().setBlock(this, event.getNewState(), true, true)
                }
            }
            return type
        }
        return 0
    }

    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block, @Nonnull target: Block, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (target.getId() === SWEET_BERRY_BUSH || block.getId() !== AIR) {
            return false
        }
        if (isSupportValid(down())) {
            this.getLevel().setBlock(block, this, true)
            return true
        }
        return false
    }

    @Override
    override fun hasEntityCollision(): Boolean {
        return getDamage() > 0
    }

    @Override
    override fun onEntityCollide(entity: Entity) {
        if (getDamage() > 0) {
            if (entity.positionChanged && !entity.isSneaking() && ThreadLocalRandom.current().nextInt(20) === 0) {
                if (entity.attack(EntityDamageByBlockEvent(this, entity, EntityDamageEvent.DamageCause.CONTACT, 1))) {
                    getLevel().addSound(entity, Sound.BLOCK_SWEET_BERRY_BUSH_HURT)
                }
            }
        }
    }

    @get:Override
    override val collisionBoundingBox: AxisAlignedBB?
        get() = if (getDamage() > 0) this else null

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        val age: Int = MathHelper.clamp(getDamage(), 0, 3)
        var amount = 1
        if (age > 1) {
            amount = 1 + ThreadLocalRandom.current().nextInt(2)
            if (age == 3) {
                amount++
            }
        }
        return arrayOf<Item>(ItemSweetBerries(0, amount))
    }

    @Override
    override fun toItem(): Item {
        return ItemSweetBerries()
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(BlockCrops.GROWTH)
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun isSupportValid(block: Block): Boolean {
            return when (block.getId()) {
                GRASS, DIRT, PODZOL -> true
                else -> false
            }
        }
    }
}