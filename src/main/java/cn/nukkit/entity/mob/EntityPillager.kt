package cn.nukkit.entity.mob

import cn.nukkit.Player

class EntityPillager(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt) {
    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(24)
    }

    @get:Override
    override val width: Float
        get() = 0.6f

    @get:Override
    override val height: Float
        get() = 1.9f

    @get:Override
    override val name: String
        get() = "Pillager"

    @Override
    override fun isPreventingSleep(player: Player?): Boolean {
        return true
    }

    companion object {
        @get:Override
        val networkId = 114
            get() = Companion.field
    }
}