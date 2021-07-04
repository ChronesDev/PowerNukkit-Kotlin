package cn.nukkit.entity.mob

import cn.nukkit.level.format.FullChunk

/**
 * @author PikyCZ
 */
class EntityMagmaCube(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt) {
    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(16)
    }

    @get:Override
    override val width: Float
        get() = 2.04f

    @get:Override
    override val height: Float
        get() = 2.04f

    @get:Override
    override val name: String
        get() = "Magma Cube"

    companion object {
        @get:Override
        val networkId = 42
            get() = Companion.field
    }
}