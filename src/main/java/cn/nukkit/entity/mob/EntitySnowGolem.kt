package cn.nukkit.entity.mob

import cn.nukkit.api.Since

@Since("1.4.0.0-PN")
class EntitySnowGolem @Since("1.4.0.0-PN") constructor(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt) {
    @get:Override
    override val name: String
        get() = "Snow Golem"

    @get:Override
    override val width: Float
        get() = 0.4f

    @get:Override
    override val height: Float
        get() = 1.8f

    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(4)
    }

    companion object {
        @get:Override
        @Since("1.4.0.0-PN")
        val networkId = 21
            get() = Companion.field
    }
}