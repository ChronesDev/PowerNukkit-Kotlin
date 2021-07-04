package cn.nukkit.entity.item

import cn.nukkit.entity.Entity

/**
 * @author PetteriM1
 */
class EntityEndCrystal(chunk: FullChunk?, nbt: CompoundTag?) : Entity(chunk, nbt), EntityExplosive {
    /**
     * @since 1.2.1.0-PN
     */
    protected var detonated = false
    @Override
    protected override fun initEntity() {
        super.initEntity()
        if (this.namedTag.contains("ShowBottom")) {
            setShowBase(this.namedTag.getBoolean("ShowBottom"))
        }
        this.fireProof = true
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_FIRE_IMMUNE, true)
    }

    @Override
    override fun saveNBT() {
        super.saveNBT()
        this.namedTag.putBoolean("ShowBottom", showBase())
    }

    @get:Override
    override val height: Float
        get() = 0.98f

    @get:Override
    override val width: Float
        get() = 0.98f

    @Override
    override fun attack(source: EntityDamageEvent): Boolean {
        if (isClosed()) {
            return false
        }
        if (source.getCause() === EntityDamageEvent.DamageCause.FIRE || source.getCause() === EntityDamageEvent.DamageCause.FIRE_TICK || source.getCause() === EntityDamageEvent.DamageCause.LAVA) {
            return false
        }
        if (!super.attack(source)) {
            return false
        }
        explode()
        return true
    }

    @Override
    fun explode() {
        if (!detonated) {
            detonated = true
            val pos: Position = this.getPosition()
            val explode = Explosion(pos, 6, this)
            this.close()
            if (this.level.getGameRules().getBoolean(GameRule.MOB_GRIEFING)) {
                explode.explodeA()
                explode.explodeB()
            }
        }
    }

    @Override
    fun canCollideWith(entity: Entity?): Boolean {
        return false
    }

    fun showBase(): Boolean {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_SHOWBASE)
    }

    fun setShowBase(value: Boolean) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHOWBASE, value)
    }

    companion object {
        @get:Override
        val networkId = 71
            get() = Companion.field
    }
}