package cn.nukkit.entity.mob

import cn.nukkit.Player

/**
 * @author joserobjr
 * @since 2020-11-20
 */
@Since("1.4.0.0-PN")
@PowerNukkitOnly
class EntityPiglinBrute @Since("1.4.0.0-PN") @PowerNukkitOnly constructor(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt) {
    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(50)
    }

    @get:Override
    override val width: Float
        get() = 0.6f

    @get:Override
    override val height: Float
        get() = 1.9f

    @Override
    override fun isPreventingSleep(player: Player?): Boolean {
        return true
    }

    @get:Override
    override val name: String
        get() = "Piglin Brute"

    companion object {
        @get:Override
        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val networkId = 127
            get() = Companion.field
    }
}