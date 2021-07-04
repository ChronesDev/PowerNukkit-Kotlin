package cn.nukkit.entity.passive

import cn.nukkit.level.format.FullChunk

class EntityCat(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    @get:Override
    val width: Float
        get() = if (this.isBaby()) {
            0.3f
        } else 0.6f

    @get:Override
    val height: Float
        get() = if (this.isBaby()) {
            0.35f
        } else 0.7f

    @Override
    fun initEntity() {
        super.initEntity()
        this.setMaxHealth(10)
    }

    companion object {
        @get:Override
        val networkId = 75
            get() = Companion.field
    }
}