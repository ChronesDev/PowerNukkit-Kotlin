package cn.nukkit.entity.passive

import cn.nukkit.item.Item

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
class EntityMooshroom(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    @get:Override
    val width: Float
        get() = if (isBaby()) {
            0.45f
        } else 0.9f

    @get:Override
    val height: Float
        get() = if (isBaby()) {
            0.65f
        } else 1.3f

    @get:Override
    val name: String
        get() = "Mooshroom"

    @get:Override
    val drops: Array<Any>
        get() = arrayOf(Item.get(Item.LEATHER), Item.get(Item.RAW_BEEF))

    @Override
    protected fun initEntity() {
        super.initEntity()
        setMaxHealth(10)
    }

    companion object {
        @get:Override
        val networkId = 16
            get() = Companion.field
    }
}