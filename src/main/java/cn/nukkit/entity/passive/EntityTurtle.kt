package cn.nukkit.entity.passive

import cn.nukkit.level.format.FullChunk

/**
 * @author PetteriM1
 */
class EntityTurtle(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    val name: String
        get() = "Turtle"

    @get:Override
    val width: Float
        get() = if (this.isBaby()) {
            0.6f
        } else 1.2f

    @get:Override
    val height: Float
        get() = if (this.isBaby()) {
            0.2f
        } else 0.4f

    @Override
    fun initEntity() {
        super.initEntity()
        this.setMaxHealth(30)
    }

    fun setBreedingAge(ticks: Int) {}
    fun setHomePos(pos: Vector3?) {}

    companion object {
        @get:Override
        val networkId = 74
            get() = Companion.field
    }
}