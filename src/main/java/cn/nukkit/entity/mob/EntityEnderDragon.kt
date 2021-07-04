package cn.nukkit.entity.mob

import cn.nukkit.Player

/**
 * @author PikyCZ
 */
class EntityEnderDragon(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt) {
    @get:Override
    override val width: Float
        get() = 13f

    @get:Override
    override val height: Float
        get() = 4f

    @Override
    override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(200)
    }

    @Override
    protected override fun applyNameTag(@Nonnull player: Player?, @Nonnull item: Item?): Boolean {
        return false
    }

    @PowerNukkitOnly
    @Deprecated
    @Override
    override fun applyNameTag(item: Item?): Boolean {
        return false
    }

    @get:Override
    override val name: String
        get() = "EnderDragon"

    companion object {
        @get:Override
        val networkId = 53
            get() = Companion.field
    }
}