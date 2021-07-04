package cn.nukkit.entity.passive

import cn.nukkit.item.Item

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
class EntityChicken(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    @get:Override
    val width: Float
        get() = if (this.isBaby()) {
            0.3f
        } else 0.6f

    @get:Override
    val height: Float
        get() = if (this.isBaby()) {
            0.4f
        } else 0.8f

    @get:Override
    val name: String
        get() = "Chicken"

    @get:Override
    val drops: Array<Any>
        get() = arrayOf(Item.get(if (this.isOnFire()) Item.COOKED_CHICKEN else Item.RAW_CHICKEN), Item.get(Item.FEATHER))

    @Override
    protected fun initEntity() {
        super.initEntity()
        setMaxHealth(4)
    }

    @Override
    override fun isBreedingItem(item: Item): Boolean {
        val id: Int = item.getId()
        return id == Item.WHEAT_SEEDS || id == Item.MELON_SEEDS || id == Item.PUMPKIN_SEEDS || id == Item.BEETROOT_SEEDS
    }

    companion object {
        @get:Override
        val networkId = 10
            get() = Companion.field
    }
}