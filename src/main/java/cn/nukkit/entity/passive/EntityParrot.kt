package cn.nukkit.entity.passive

import cn.nukkit.item.Item

/**
 * @author PikyCZ
 */
class EntityParrot(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    val name: String
        get() = "Parrot"

    @get:Override
    val width: Float
        get() = 0.5f

    @get:Override
    val height: Float
        get() = 1.0f

    @Override
    fun initEntity() {
        super.initEntity()
        this.setMaxHealth(6)
    }

    @get:Override
    val drops: Array<Any>
        get() = arrayOf(Item.get(Item.FEATHER))

    companion object {
        @get:Override
        val networkId = 30
            get() = Companion.field
    }
}