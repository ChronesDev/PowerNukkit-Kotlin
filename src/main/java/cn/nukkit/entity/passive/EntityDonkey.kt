package cn.nukkit.entity.passive

import cn.nukkit.item.Item

/**
 * @author PikyCZ
 */
class EntityDonkey(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    @get:Override
    val width: Float
        get() = if (this.isBaby()) {
            0.7f
        } else 1.4f

    @get:Override
    val height: Float
        get() = if (this.isBaby()) {
            0.8f
        } else 1.6f

    @Override
    fun initEntity() {
        super.initEntity()
        this.setMaxHealth(15)
    }

    @get:Override
    val drops: Array<Any>
        get() = arrayOf(Item.get(Item.LEATHER))

    companion object {
        @get:Override
        val networkId = 24
            get() = Companion.field
    }
}