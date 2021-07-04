package cn.nukkit.entity.passive

import cn.nukkit.level.format.FullChunk

/**
 * @author PetteriM1
 */
class EntityTropicalFish(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    val name: String
        get() = "Tropical Fish"

    @get:Override
    val width: Float
        get() = 0.4f

    @get:Override
    val height: Float
        get() = 0.4f

    @Override
    fun initEntity() {
        super.initEntity()
        this.setMaxHealth(6)
    }

    companion object {
        @get:Override
        val networkId = 111
            get() = Companion.field
    }
}