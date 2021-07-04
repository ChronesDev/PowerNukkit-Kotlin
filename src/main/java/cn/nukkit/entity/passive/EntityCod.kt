package cn.nukkit.entity.passive

import cn.nukkit.level.format.FullChunk

/**
 * @author PetteriM1
 */
class EntityCod(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    val name: String
        get() = "Cod"

    @get:Override
    val width: Float
        get() = 0.6f

    @get:Override
    val height: Float
        get() = 0.3f

    @Override
    fun initEntity() {
        super.initEntity()
        this.setMaxHealth(3)
    }

    companion object {
        @get:Override
        val networkId = 112
            get() = Companion.field
    }
}