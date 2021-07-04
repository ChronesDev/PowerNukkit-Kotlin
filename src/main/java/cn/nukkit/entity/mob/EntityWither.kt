package cn.nukkit.entity.mob

import cn.nukkit.Player

/**
 * @author PikyCZ
 */
class EntityWither(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt), EntitySmite {
    @get:Override
    override val width: Float
        get() = 1.0f

    @get:Override
    override val height: Float
        get() = 3.0f

    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(600)
    }

    @get:Override
    override val name: String
        get() = "Wither"

    @get:Override
    override val isUndead: Boolean
        get() = true

    @Override
    override fun isPreventingSleep(player: Player?): Boolean {
        return true
    }

    companion object {
        @get:Override
        val networkId = 52
            get() = Companion.field
    }
}