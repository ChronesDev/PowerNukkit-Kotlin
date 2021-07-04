package cn.nukkit.entity.passive

import cn.nukkit.item.Item

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
class EntityRabbit(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    @get:Override
    val width: Float
        get() = if (this.isBaby()) {
            0.268f
        } else 0.67f

    @get:Override
    val height: Float
        get() = if (this.isBaby()) {
            0.268f
        } else 0.67f

    @get:Override
    val name: String
        get() = "Rabbit"

    @get:Override
    val drops: Array<Any>
        get() = arrayOf(Item.get(if (this.isOnFire()) Item.COOKED_RABBIT else Item.RAW_RABBIT), Item.get(Item.RABBIT_HIDE), Item.get(Item.RABBIT_FOOT))

    @Override
    protected fun initEntity() {
        super.initEntity()
        setMaxHealth(10)
    }

    companion object {
        @get:Override
        val networkId = 18
            get() = Companion.field
    }
}