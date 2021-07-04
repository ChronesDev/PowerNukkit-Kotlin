package cn.nukkit.entity.passive

import cn.nukkit.entity.Entity

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class EntityWaterAnimal(chunk: FullChunk?, nbt: CompoundTag?) : EntityCreature(chunk, nbt), EntityAgeable {
    @get:Override
    val isBaby: Boolean
        get() = this.getDataFlag(DATA_FLAGS, Entity.DATA_FLAG_BABY)
}