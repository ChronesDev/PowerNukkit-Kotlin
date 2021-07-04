package cn.nukkit.entity.mob

import cn.nukkit.Player

/**
 * @author PetteriM1
 */
class EntityDrowned(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt), EntitySmite {
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
        get() = "Drowned"

    @get:Override
    override val drops: Array<Any>
        get() = arrayOf(Item.get(Item.ROTTEN_FLESH))

    @get:Override
    override val isUndead: Boolean
        get() = true

    @Override
    override fun isPreventingSleep(player: Player?): Boolean {
        return true
    }

    companion object {
        @get:Override
        val networkId = 110
            get() = Companion.field
    }
}