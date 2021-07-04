package cn.nukkit.entity.passive

import cn.nukkit.item.Item

/**
 * @author PikyCZ
 */
class EntitySquid(chunk: FullChunk?, nbt: CompoundTag?) : EntityWaterAnimal(chunk, nbt) {
    @get:Override
    val width: Float
        get() = 0.95f

    @get:Override
    val height: Float
        get() = 0.95f

    @Override
    fun initEntity() {
        super.initEntity()
        this.setMaxHealth(10)
    }

    @get:Override
    val drops: Array<Any>
        get() = arrayOf(MinecraftItemID.INK_SAC.get(1))

    companion object {
        @get:Override
        val networkId = 17
            get() = Companion.field
    }
}