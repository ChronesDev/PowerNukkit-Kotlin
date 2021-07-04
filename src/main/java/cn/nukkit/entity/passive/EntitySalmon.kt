package cn.nukkit.entity.passive

import cn.nukkit.level.format.FullChunk

/**
 * @author PetteriM1
 */
class EntitySalmon(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    val name: String
        get() = "Salmon"

    @get:Override
    val width: Float
        get() = 0.5f

    @get:Override
    val height: Float
        get() = 0.5f

    @Override
    fun initEntity() {
        super.initEntity()
        this.setMaxHealth(3)
    }

    companion object {
        @get:Override
        val networkId = 109
            get() = Companion.field
    }
}