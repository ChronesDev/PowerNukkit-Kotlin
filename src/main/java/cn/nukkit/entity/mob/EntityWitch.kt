package cn.nukkit.entity.mob

import cn.nukkit.Player

/**
 * @author PikyCZ
 */
class EntityWitch(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt) {
    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(26)
    }

    @get:Override
    override val width: Float
        get() = 0.6f

    @get:Override
    override val height: Float
        get() = 1.9f

    @get:Override
    override val name: String
        get() = "Witch"

    @Override
    override fun isPreventingSleep(player: Player?): Boolean {
        return true
    }

    companion object {
        @get:Override
        val networkId = 45
            get() = Companion.field
    }
}