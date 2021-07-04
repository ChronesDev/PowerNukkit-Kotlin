package cn.nukkit.entity.passive

import cn.nukkit.entity.EntitySmite

/**
 * @author PikyCZ
 */
class EntitySkeletonHorse(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt), EntitySmite {
    @get:Override
    val width: Float
        get() = 1.4f

    @get:Override
    val height: Float
        get() = 1.6f

    @Override
    fun initEntity() {
        super.initEntity()
        this.setMaxHealth(15)
    }

    @get:Override
    val drops: Array<Any>
        get() = arrayOf(Item.get(Item.BONE))

    @get:Override
    val isUndead: Boolean
        get() = true

    companion object {
        @get:Override
        val networkId = 26
            get() = Companion.field
    }
}