package cn.nukkit.entity.mob

import cn.nukkit.item.Item

/**
 * @author PikyCZ
 */
class EntitySlime(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt) {
    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.setMaxHealth(16)
    }

    @get:Override
    override val width: Float
        get() = 2.04f

    @get:Override
    override val height: Float
        get() = 2.04f

    @get:Override
    override val name: String
        get() = "Slime"

    @get:Override
    override val drops: Array<Any>
        get() = arrayOf(Item.get(Item.SLIMEBALL))

    companion object {
        @get:Override
        val networkId = 37
            get() = Companion.field
    }
}