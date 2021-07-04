package cn.nukkit.entity.passive

import cn.nukkit.entity.EntityCreature

class EntityWanderingTrader(chunk: FullChunk?, nbt: CompoundTag?) : EntityCreature(chunk, nbt), EntityNPC {
    @get:Override
    override val width: Float
        get() = 0.6f

    @get:Override
    override val height: Float
        get() = 1.9f

    @get:Override
    override val name: String
        get() = "Wandering Trader"

    @Override
    override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(20)
    }

    companion object {
        @get:Override
        val networkId = 118
            get() = Companion.field
    }
}