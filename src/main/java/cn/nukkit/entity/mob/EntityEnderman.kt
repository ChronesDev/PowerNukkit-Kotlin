package cn.nukkit.entity.mob

import cn.nukkit.Player

/**
 * @author PikyCZ
 */
class EntityEnderman(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt) {
    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(40)
    }

    @get:Override
    override val width: Float
        get() = 0.6f

    @get:Override
    override val height: Float
        get() = 2.9f

    @get:Override
    override val name: String
        get() = "Enderman"

    @Override
    override fun isPreventingSleep(player: Player?): Boolean {
        return this.getDataPropertyBoolean(DATA_FLAG_ANGRY)
    }

    companion object {
        @get:Override
        val networkId = 38
            get() = Companion.field
    }
}