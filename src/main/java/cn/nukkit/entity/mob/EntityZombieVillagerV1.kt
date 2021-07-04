package cn.nukkit.entity.mob

import cn.nukkit.entity.EntitySmite

/**
 * @author PikyCZ
 */
class EntityZombieVillagerV1(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt), EntitySmite {
    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(20)
    }

    @get:Override
    override val width: Float
        get() = 0.6f

    @get:Override
    override val height: Float
        get() = 1.9f

    @get:Override
    override val name: String
        get() = "Zombie Villager"

    @get:Override
    override val isUndead: Boolean
        get() = true

    companion object {
        @get:Override
        val networkId = 44
            get() = Companion.field
    }
}