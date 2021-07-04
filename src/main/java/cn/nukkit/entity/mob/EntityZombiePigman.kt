package cn.nukkit.entity.mob

import cn.nukkit.Player

/**
 * @author PikyCZ
 */
class EntityZombiePigman(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt), EntitySmite {
    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(20)
    }

    @get:Override
    override val width: Float
        get() = 0.6f

    @get:Override
    override val height: Float
        get() = 1.9f

    @get:Override
    override val name: String
        get() = "ZombiePigman"

    @get:Override
    override val isUndead: Boolean
        get() = true

    @Override
    override fun isPreventingSleep(player: Player?): Boolean {
        return this.getDataPropertyBoolean(DATA_FLAG_ANGRY)
    }

    companion object {
        @get:Override
        val networkId = 36
            get() = Companion.field
    }
}