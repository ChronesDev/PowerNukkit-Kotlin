package cn.nukkit.entity.mob

import cn.nukkit.Player

/**
 * @author PikyCZ
 */
class EntityVex(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt) {
    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(14)
    }

    @get:Override
    override val width: Float
        get() = 0.4f

    @get:Override
    override val height: Float
        get() = 0.8f

    @get:Override
    override val name: String
        get() = "Vex"

    @Override
    override fun isPreventingSleep(player: Player?): Boolean {
        return true
    }

    companion object {
        @get:Override
        val networkId = 105
            get() = Companion.field
    }
}