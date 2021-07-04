package cn.nukkit.entity.passive

import cn.nukkit.entity.Entity

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class EntityAnimal(chunk: FullChunk?, nbt: CompoundTag?) : EntityCreature(chunk, nbt), EntityAgeable {
    @get:Override
    val isBaby: Boolean
        get() = this.getDataFlag(DATA_FLAGS, Entity.DATA_FLAG_BABY)

    fun isBreedingItem(item: Item): Boolean {
        return item.getId() === Item.WHEAT //default
    }
}