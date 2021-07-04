package cn.nukkit.entity.passive

import cn.nukkit.api.Since

/**
 * @author Erik Miller | EinBexiii
 */
@Since("1.3.1.0-PN")
class EntityStrider(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    @Override
    protected fun initEntity() {
        super.initEntity()
        this.setMaxHealth(20)
    }

    @get:Override
    val width: Float
        get() = 0.9f

    @get:Override
    val height: Float
        get() = 1.7f

    @get:Override
    val name: String
        get() = "Strider"

    companion object {
        @get:Override
        val networkId = 125
            get() = Companion.field
    }
}