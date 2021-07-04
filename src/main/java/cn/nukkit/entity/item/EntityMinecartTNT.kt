package cn.nukkit.entity.item

import cn.nukkit.Player

/**
 * @author Adam Matthew [larryTheCoder] (Nukkit Project)
 */
class EntityMinecartTNT(chunk: FullChunk?, nbt: CompoundTag?) : EntityMinecartAbstract(chunk, nbt), EntityExplosive {
    private var fuse = 0

    @get:Override
    override val isRideable: Boolean
        get() = false

    @Override
    override fun initEntity() {
        super.initEntity()
        fuse = if (namedTag.contains("TNTFuse")) {
            namedTag.getByte("TNTFuse")
        } else {
            80
        }
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_CHARGED, false)
    }

    @Override
    override fun onUpdate(currentTick: Int): Boolean {
        this.timing.startTiming()
        if (fuse < 80) {
            val tickDiff: Int = currentTick - lastUpdate
            lastUpdate = currentTick
            if (fuse % 5 == 0) {
                setDataProperty(IntEntityData(DATA_FUSE_LENGTH, fuse))
            }
            fuse -= tickDiff
            if (isAlive() && fuse <= 0) {
                if (this.level.getGameRules().getBoolean(GameRule.TNT_EXPLODES)) {
                    this.explode(ThreadLocalRandom.current().nextInt(5))
                }
                this.close()
                return false
            }
        }
        this.timing.stopTiming()
        return super.onUpdate(currentTick)
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    override fun activate(x: Int, y: Int, z: Int, flag: Boolean) {
        level.addSound(this, Sound.FIRE_IGNITE)
        fuse = 79
    }

    @Override
    fun explode() {
        explode(0.0)
    }

    fun explode(square: Double) {
        var root: Double = Math.sqrt(square)
        if (root > 5.0) {
            root = 5.0
        }
        val event = EntityExplosionPrimeEvent(this, 4.0 + ThreadLocalRandom.current().nextDouble() * 1.5 * root)
        server.getPluginManager().callEvent(event)
        if (event.isCancelled()) {
            return
        }
        val explosion = Explosion(this, event.getForce(), this)
        explosion.setFireChance(event.getFireChance())
        if (event.isBlockBreaking()) {
            explosion.explodeA()
        }
        explosion.explodeB()
        this.close()
    }

    @Override
    override fun dropItem() {
        level.dropItem(this, ItemMinecartTNT())
    }

    @get:Override
    val name: String
        get() = type.getName()

    @get:Override
    override val type: MinecartType
        get() = MinecartType.valueOf(3)

    @Override
    override fun saveNBT() {
        super.saveNBT()
        super.namedTag.putInt("TNTFuse", fuse)
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    override fun onInteract(player: Player?, item: Item, clickedPos: Vector3?): Boolean {
        val interact: Boolean = super.onInteract(player, item, clickedPos)
        if (item.getId() === Item.FLINT_AND_STEEL || item.getId() === Item.FIRE_CHARGE) {
            level.addSound(this, Sound.FIRE_IGNITE)
            fuse = 79
            return true
        }
        return interact
    }

    @Override
    fun mountEntity(entity: Entity?, mode: Byte): Boolean {
        return false
    }

    companion object {
        @get:Override
        val networkId = 97
            get() = Companion.field
    }

    init {
        super.setDisplayBlock(Block.get(BlockID.TNT), false)
    }
}