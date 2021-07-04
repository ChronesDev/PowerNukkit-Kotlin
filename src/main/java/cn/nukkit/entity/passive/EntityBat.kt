package cn.nukkit.entity.passive

import cn.nukkit.level.format.FullChunk

/**
 * @author PikyCZ
 */
class EntityBat(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    @get:Override
    val width: Float
        get() = 0.5f

    @get:Override
    val height: Float
        get() = 0.9f

    @Override
    fun initEntity() {
        super.initEntity()
        this.setMaxHealth(6)
    }

    companion object {
        @get:Override
        val networkId = 19
            get() = Companion.field
    }
}