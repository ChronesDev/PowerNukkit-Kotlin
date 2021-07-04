package cn.nukkit.entity.mob

import cn.nukkit.level.format.FullChunk

/**
 * @author PikyCZ
 */
class EntityShulker(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt) {
    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(30)
    }

    @get:Override
    override val width: Float
        get() = 1f

    @get:Override
    override val height: Float
        get() = 1f

    @get:Override
    override val name: String
        get() = "Shulker"

    companion object {
        @get:Override
        val networkId = 54
            get() = Companion.field
    }
}