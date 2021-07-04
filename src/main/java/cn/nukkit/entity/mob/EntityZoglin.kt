package cn.nukkit.entity.mob

import cn.nukkit.Player

/**
 * @author Erik Miller | EinBexiii
 */
@Since("1.3.1.0-PN")
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements EntityAgeable only in PowerNukkit!")
class EntityZoglin(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt), EntityAgeable {
    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(40)
    }

    @get:Override
    override val width: Float
        get() = if (isBaby) {
            0.85f
        } else 0.9f

    @get:Override
    override val height: Float
        get() = if (isBaby) {
            0.85f
        } else 0.9f

    @get:Override
    override val name: String
        get() = "Zoglin"

    @get:Override
    override val isUndead: Boolean
        get() = true

    @Override
    override fun isPreventingSleep(player: Player?): Boolean {
        return true
    }

    @get:Override
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isBaby: Boolean
        get() = this.getDataFlag(DATA_FLAGS, DATA_FLAG_BABY)
        set(baby) {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_BABY, baby)
            this.setScale(if (baby) 0.5f else 1.0f)
        }

    companion object {
        @get:Override
        val networkId = 126
            get() = Companion.field
    }
}