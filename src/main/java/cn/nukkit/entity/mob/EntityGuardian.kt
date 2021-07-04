package cn.nukkit.entity.mob

import cn.nukkit.Player

/**
 * @author PikyCZ
 */
class EntityGuardian(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt) {
    @Override
    override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(30)
    }

    @get:Override
    override val name: String
        get() = "Guardian"

    @get:Override
    override val width: Float
        get() = 0.85f

    @get:Override
    override val height: Float
        get() = 0.85f

    @Override
    override fun isPreventingSleep(player: Player?): Boolean {
        return true
    }

    companion object {
        @get:Override
        val networkId = 49
            get() = Companion.field
    }
}