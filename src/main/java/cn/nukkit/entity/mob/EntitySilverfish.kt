package cn.nukkit.entity.mob

import cn.nukkit.Player

/**
 * @author PikyCZ
 */
class EntitySilverfish(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt), EntityArthropod {
    @get:Override
    override val name: String
        get() = "Silverfish"

    @get:Override
    override val width: Float
        get() = 0.4f

    @get:Override
    override val height: Float
        get() = 0.3f

    @Override
    override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(8)
    }

    @Override
    override fun isPreventingSleep(player: Player?): Boolean {
        return true
    }

    companion object {
        @get:Override
        val networkId = 39
            get() = Companion.field
    }
}