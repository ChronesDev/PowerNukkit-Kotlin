package cn.nukkit.block

import cn.nukkit.Player

class BlockWitherRose @JvmOverloads constructor(meta: Int = 0) : BlockFlower(0) {
    @get:Override
    override val id: Int
        get() = WITHER_ROSE

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = CommonBlockProperties.EMPTY_PROPERTIES

    @Override
    override fun canPlantOn(block: Block): Boolean {
        return super.canPlantOn(block) || block.getId() === BlockID.NETHERRACK || block.getId() === BlockID.SOUL_SAND
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        return false
    }

    @Override
    override fun onEntityCollide(entity: Entity) {
        if (level.getServer().getDifficulty() !== 0 && entity is EntityLiving) {
            val living: EntityLiving = entity as EntityLiving
            if (!living.invulnerable && !living.hasEffect(Effect.WITHER)
                    && (living !is Player || !(living as Player).isCreative() && !(living as Player).isSpectator())) {
                val effect: Effect = Effect.getEffect(Effect.WITHER)
                effect.setDuration(40)
                effect.setAmplifier(1)
                living.addEffect(effect)
            }
        }
    }

    @Override
    protected override fun recalculateCollisionBoundingBox(): AxisAlignedBB {
        return this
    }

    @Override
    override fun hasEntityCollision(): Boolean {
        return true
    }

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    @set:Override
    @set:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    override var flowerType: SmallFlowerType
        get() = SmallFlowerType.WITHER_ROSE
        set(flowerType) {
            setOnSingleFlowerType(SmallFlowerType.WITHER_ROSE, flowerType)
        }
}