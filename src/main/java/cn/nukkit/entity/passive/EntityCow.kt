package cn.nukkit.entity.passive

import cn.nukkit.item.Item

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
class EntityCow(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    @get:Override
    val width: Float
        get() = if (this.isBaby()) {
            0.45f
        } else 0.9f

    @get:Override
    val height: Float
        get() = if (this.isBaby()) {
            0.65f
        } else 1.3f

    @get:Override
    val name: String
        get() = "Cow"

    @get:Override
    val drops: Array<Any>
        get() = arrayOf(Item.get(Item.LEATHER), Item.get(if (this.isOnFire()) Item.COOKED_BEEF else Item.RAW_BEEF))

    @Override
    protected fun initEntity() {
        super.initEntity()
        this.setMaxHealth(10)
    }

    companion object {
        @get:Override
        val networkId = 11
            get() = Companion.field
    }
}