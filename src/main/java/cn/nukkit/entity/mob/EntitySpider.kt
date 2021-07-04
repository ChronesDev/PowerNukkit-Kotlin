package cn.nukkit.entity.mob

import cn.nukkit.Player

/**
 * @author PikyCZ
 */
class EntitySpider(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt), EntityArthropod {
    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(16)
    }

    @get:Override
    override val width: Float
        get() = 1.4f

    @get:Override
    override val height: Float
        get() = 0.9f

    @get:Override
    override val name: String
        get() = "Spider"

    @get:Override
    override val drops: Array<Any>
        get() = arrayOf(Item.get(Item.STRING, Item.SPIDER_EYE))

    @Override
    override fun isPreventingSleep(player: Player?): Boolean {
        return true
    }

    companion object {
        @get:Override
        val networkId = 35
            get() = Companion.field
    }
}