package cn.nukkit.entity.passive

import cn.nukkit.entity.EntityAgeable

class EntityVillager(chunk: FullChunk?, nbt: CompoundTag?) : EntityCreature(chunk, nbt), EntityNPC, EntityAgeable {
    @get:Override
    val width: Float
        get() = if (isBaby) {
            0.3f
        } else 0.6f

    @get:Override
    val height: Float
        get() = if (isBaby) {
            0.95f
        } else 1.9f

    @get:Override
    val name: String
        get() = "Villager"

    @Override
    fun initEntity() {
        super.initEntity()
        this.setMaxHealth(20)
    }

    @get:Override
    override var isBaby: Boolean
        get() = this.getDataFlag(DATA_FLAGS, DATA_FLAG_BABY)
        set(baby) {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_BABY, baby)
            this.setScale(if (baby) 0.5f else 1)
        }

    companion object {
        @get:Override
        val networkId = 115
            get() = Companion.field
    }
}