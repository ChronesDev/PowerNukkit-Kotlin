package cn.nukkit.entity.passive

import cn.nukkit.level.format.FullChunk

class EntityPanda(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    @get:Override
    val width: Float
        get() = 1.7f

    @get:Override
    val height: Float
        get() = 1.5f

    @Override
    fun initEntity() {
        super.initEntity()
        this.setMaxHealth(20)
    }

    companion object {
        @get:Override
        val networkId = 113
            get() = Companion.field
    }
}