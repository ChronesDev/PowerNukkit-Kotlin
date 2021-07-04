package cn.nukkit.entity.mob

import cn.nukkit.Player

/**
 * @author PetteriM1
 */
class EntityPhantom(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt), EntitySmite {
    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(20)
    }

    @get:Override
    override val width: Float
        get() = 0.9f

    @get:Override
    override val height: Float
        get() = 0.5f

    @get:Override
    override val name: String
        get() = "Phantom"

    @get:Override
    override val drops: Array<Any>
        get() = arrayOf(Item.get(470))

    @get:Override
    override val isUndead: Boolean
        get() = true

    @Override
    override fun isPreventingSleep(player: Player?): Boolean {
        return true
    }

    companion object {
        @get:Override
        val networkId = 58
            get() = Companion.field
    }
}