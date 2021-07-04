package cn.nukkit.entity.passive

import cn.nukkit.item.Item

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
class EntityPig(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    @get:Override
    val width: Float
        get() = if (this.isBaby()) {
            0.45f
        } else 0.9f

    @get:Override
    val height: Float
        get() = if (this.isBaby()) {
            0.45f
        } else 0.9f

    @Override
    fun initEntity() {
        super.initEntity()
        this.setMaxHealth(10)
    }

    @get:Override
    val name: String
        get() = "Pig"

    @get:Override
    val drops: Array<Any>
        get() = arrayOf(Item.get(if (this.isOnFire()) Item.COOKED_PORKCHOP else Item.RAW_PORKCHOP))

    @Override
    override fun isBreedingItem(item: Item): Boolean {
        val id: Int = item.getId()
        return id == Item.CARROT || id == Item.POTATO || id == Item.BEETROOT
    }

    companion object {
        @get:Override
        val networkId = 12
            get() = Companion.field
    }
}