package cn.nukkit.entity.mob

import cn.nukkit.level.format.FullChunk

/**
 * @author PikyCZ
 */
class EntityGhast(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt) {
    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(10)
    }

    @get:Override
    override val width: Float
        get() = 4

    @get:Override
    override val height: Float
        get() = 4

    @get:Override
    override val name: String
        get() = "Ghast"

    companion object {
        @get:Override
        val networkId = 41
            get() = Companion.field
    }
}