package cn.nukkit.entity.passive

import cn.nukkit.level.format.FullChunk

/**
 * @author PikyCZ
 */
class EntityLlama(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    @get:Override
    val width: Float
        get() = if (this.isBaby()) {
            0.45f
        } else 0.9f

    @get:Override
    val height: Float
        get() = if (this.isBaby()) {
            0.935f
        } else 1.87f

    @get:Override
    val eyeHeight: Float
        get() = if (this.isBaby()) {
            0.65f
        } else 1.2f

    @Override
    fun initEntity() {
        super.initEntity()
        this.setMaxHealth(15)
    }

    companion object {
        @get:Override
        val networkId = 29
            get() = Companion.field
    }
}