package cn.nukkit.entity.mob

import cn.nukkit.Player

/**
 * @author Erik Miller | EinBexiii
 */
@Since("1.3.1.0-PN")
class EntityPiglin(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt), EntityAgeable {
    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(16)
    }

    @get:Override
    override val width: Float
        get() = 0.6f

    @get:Override
    override val height: Float
        get() = 1.9f

    @get:Override
    override val name: String
        get() = "Piglin"

    @get:Override
    val isBaby: Boolean
        get() = this.getDataFlag(DATA_FLAGS, Entity.DATA_FLAG_BABY)

    @Override
    override fun isPreventingSleep(player: Player?): Boolean {
        return !isBaby /*TODO: Should this check player's golden armor?*/
    }

    companion object {
        @get:Override
        val networkId = 123
            get() = Companion.field
    }
}