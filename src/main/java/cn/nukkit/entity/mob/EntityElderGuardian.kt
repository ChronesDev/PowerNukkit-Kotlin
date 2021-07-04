package cn.nukkit.entity.mob

import cn.nukkit.Player

/**
 * @author PikyCZ
 */
class EntityElderGuardian(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt) {
    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(80)
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ELDER, true)
    }

    @get:Override
    override val width: Float
        get() = 1.99f

    @get:Override
    override val height: Float
        get() = 1.99f

    @get:Override
    override val name: String
        get() = "Elder Guardian"

    @Override
    override fun isPreventingSleep(player: Player?): Boolean {
        return true
    }

    companion object {
        @get:Override
        val networkId = 50
            get() = Companion.field
    }
}