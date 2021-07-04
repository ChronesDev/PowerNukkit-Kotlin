package cn.nukkit.entity.passive

import cn.nukkit.item.Item

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
class EntityOcelot(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    @get:Override
    val width: Float
        get() = if (this.isBaby()) {
            0.3f
        } else 0.6f

    @get:Override
    val height: Float
        get() = if (this.isBaby()) {
            0.35f
        } else 0.7f

    @get:Override
    val name: String
        get() = "Ocelot"

    @Override
    fun initEntity() {
        super.initEntity()
        setMaxHealth(10)
    }

    @Override
    override fun isBreedingItem(item: Item): Boolean {
        return item.getId() === Item.RAW_FISH
    }

    companion object {
        @get:Override
        val networkId = 22
            get() = Companion.field
    }
}