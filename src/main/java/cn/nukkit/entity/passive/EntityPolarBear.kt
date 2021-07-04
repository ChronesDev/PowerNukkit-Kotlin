package cn.nukkit.entity.passive

import cn.nukkit.item.Item

/**
 * @author PikyCZ
 */
class EntityPolarBear(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    @get:Override
    val width: Float
        get() = if (this.isBaby()) {
            0.65f
        } else 1.3f

    @get:Override
    val height: Float
        get() = if (this.isBaby()) {
            0.7f
        } else 1.4f

    @Override
    fun initEntity() {
        super.initEntity()
        this.setMaxHealth(30)
    }

    @get:Override
    val drops: Array<Any>
        get() = arrayOf(Item.get(Item.RAW_FISH), Item.get(Item.RAW_SALMON))

    companion object {
        @get:Override
        val networkId = 28
            get() = Companion.field
    }
}