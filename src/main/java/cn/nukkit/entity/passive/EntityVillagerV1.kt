package cn.nukkit.entity.passive

import cn.nukkit.entity.Entity

/**
 * @author Pub4Game
 * @since 21.06.2016
 */
class EntityVillagerV1(chunk: FullChunk?, nbt: CompoundTag?) : EntityCreature(chunk, nbt), EntityNPC, EntityAgeable {
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
        if (!this.namedTag.contains("Profession")) {
            profession = PROFESSION_GENERIC
        }
    }

    var profession: Int
        get() = this.namedTag.getInt("Profession")
        set(profession) {
            this.namedTag.putInt("Profession", profession)
        }

    @get:Override
    val isBaby: Boolean
        get() = this.getDataFlag(DATA_FLAGS, Entity.DATA_FLAG_BABY)

    companion object {
        const val PROFESSION_FARMER = 0
        const val PROFESSION_LIBRARIAN = 1
        const val PROFESSION_PRIEST = 2
        const val PROFESSION_BLACKSMITH = 3
        const val PROFESSION_BUTCHER = 4
        const val PROFESSION_GENERIC = 5

        @get:Override
        val networkId = 15
            get() = Companion.field
    }
}