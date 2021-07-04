package cn.nukkit.entity.passive

import cn.nukkit.item.Item

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
class EntityWolf(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    @get:Override
    val width: Float
        get() = 0.6f

    @get:Override
    val height: Float
        get() = 0.8f

    @get:Override
    val name: String
        get() = "Wolf"

    @Override
    fun initEntity() {
        super.initEntity()
        this.setMaxHealth(8)
    }

    @Override
    override fun isBreedingItem(item: Item?): Boolean {
        return false //only certain food
    }

    companion object {
        @get:Override
        val networkId = 14
            get() = Companion.field
    }
}