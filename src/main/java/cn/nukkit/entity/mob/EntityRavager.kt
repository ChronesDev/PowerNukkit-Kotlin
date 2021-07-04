package cn.nukkit.entity.mob

import cn.nukkit.Player

class EntityRavager(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt) {
    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(100)
    }

    @get:Override
    override val height: Float
        get() = 1.9f

    @get:Override
    override val width: Float
        get() = 1.2f

    @get:Override
    override val name: String
        get() = "Ravager"

    @Override
    override fun isPreventingSleep(player: Player?): Boolean {
        return true
    }

    companion object {
        @get:Override
        val networkId = 59
            get() = Companion.field
    }
}