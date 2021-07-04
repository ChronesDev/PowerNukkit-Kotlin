package cn.nukkit.entity.item

import cn.nukkit.entity.Entity

class EntityPotionLingering : EntityPotion {
    constructor(chunk: FullChunk?, nbt: CompoundTag?) : super(chunk, nbt) {}
    constructor(chunk: FullChunk?, nbt: CompoundTag?, shootingEntity: Entity?) : super(chunk, nbt, shootingEntity) {}

    @Override
    protected override fun initEntity() {
        super.initEntity()
        setDataFlag(DATA_FLAGS, DATA_FLAG_LINGER, true)
    }

    @Override
    protected override fun splash(collidedWith: Entity?) {
        super.splash(collidedWith)
        saveNBT()
        val pos: ListTag<*> = namedTag.getList("Pos", CompoundTag::class.java).copy() as ListTag<*>
        val entity: EntityAreaEffectCloud = Entity.createEntity("AreaEffectCloud", getChunk(),
                CompoundTag().putList(pos)
                        .putList(ListTag("Rotation")
                                .add(FloatTag("", 0))
                                .add(FloatTag("", 0))
                        )
                        .putList(ListTag("Motion")
                                .add(DoubleTag("", 0))
                                .add(DoubleTag("", 0))
                                .add(DoubleTag("", 0))
                        )
                        .putShort("PotionId", potionId)
        ) as EntityAreaEffectCloud
        val effect: Effect = Potion.getEffect(potionId, true)
        if (effect != null && entity != null) {
            entity.cloudEffects.add(effect.setDuration(1).setVisible(false).setAmbient(false))
            entity.spawnToAll()
        }
    }

    companion object {
        const val NETWORK_ID = 101
    }
}