package cn.nukkit.entity.mob

import cn.nukkit.Player

/**
 * @author PikyCZ
 */
class EntityWitherSkeleton(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt), EntitySmite {
    @Override
    protected override fun initEntity() {
        super.initEntity()
    }

    @get:Override
    override val width: Float
        get() = 0.72f

    @get:Override
    override val height: Float
        get() = 2.01f

    @get:Override
    override val name: String
        get() = "WitherSkeleton"

    @get:Override
    override val isUndead: Boolean
        get() = true

    @Override
    override fun isPreventingSleep(player: Player?): Boolean {
        return true
    }

    companion object {
        @get:Override
        val networkId = 48
            get() = Companion.field
    }
}