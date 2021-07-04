package cn.nukkit.entity.mob

import cn.nukkit.Player

/**
 * @author PikyCZ
 */
class EntityCaveSpider(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt), EntityArthropod {
    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(12)
    }

    @get:Override
    override val width: Float
        get() = 0.7f

    @get:Override
    override val height: Float
        get() = 0.5f

    @get:Override
    override val name: String
        get() = "CaveSpider"

    @Override
    override fun isPreventingSleep(player: Player?): Boolean {
        return true
    }

    companion object {
        @get:Override
        val networkId = 40
            get() = Companion.field
    }
}