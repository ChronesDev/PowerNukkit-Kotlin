package cn.nukkit.entity.passive

import cn.nukkit.item.Item

/**
 * @author PetteriM1
 */
class EntityDolphin(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    val name: String
        get() = "Dolphin"

    @get:Override
    val width: Float
        get() = 0.9f

    @get:Override
    val height: Float
        get() = 0.6f

    @Override
    fun initEntity() {
        super.initEntity()
        this.setMaxHealth(10)
    }

    @get:Override
    val drops: Array<Any>
        get() = arrayOf(Item.get(Item.RAW_FISH))

    companion object {
        @get:Override
        val networkId = 31
            get() = Companion.field
    }
}