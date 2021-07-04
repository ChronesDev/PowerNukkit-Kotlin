package cn.nukkit.entity.passive

import cn.nukkit.api.Since

/**
 * @author Kaooot
 * @since 2020-08-14
 */
@Since("1.4.0.0-PN")
class EntityFox @Since("1.4.0.0-PN") constructor(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    @get:Override
    val width: Float
        get() = 0.6f

    @get:Override
    val height: Float
        get() = 0.7f

    @Override
    protected fun initEntity() {
        super.initEntity()
        this.setMaxHealth(20)
    }

    @get:Override
    val name: String
        get() = "Fox"

    companion object {
        @get:Override
        @Since("1.4.0.0-PN")
        val networkId = 121
            get() = Companion.field
    }
}