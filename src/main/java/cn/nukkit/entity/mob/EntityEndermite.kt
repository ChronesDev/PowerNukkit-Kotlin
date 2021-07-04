package cn.nukkit.entity.mob

import cn.nukkit.Player

/**
 * @author Box.
 */
class EntityEndermite(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt), EntityArthropod {
    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(8)
    }

    @get:Override
    override val width: Float
        get() = 0.4f

    @get:Override
    override val height: Float
        get() = 0.3f

    @get:Override
    override val name: String
        get() = "Endermite"

    @Override
    override fun isPreventingSleep(player: Player?): Boolean {
        return true
    }

    companion object {
        @get:Override
        val networkId = 55
            get() = Companion.field
    }
}