package cn.nukkit.entity.passive

import cn.nukkit.level.format.FullChunk

/**
 * @author PetteriM1
 */
class EntityPufferfish(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    val name: String
        get() = "Pufferfish"

    @get:Override
    val width: Float
        get() = 0.8f

    @get:Override
    val height: Float
        get() = 0.8f

    @Override
    fun initEntity() {
        super.initEntity()
        this.setMaxHealth(3)
    }

    companion object {
        @get:Override
        val networkId = 108
            get() = Companion.field
    }
}