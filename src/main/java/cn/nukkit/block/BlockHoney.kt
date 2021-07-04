package cn.nukkit.block

import cn.nukkit.AdventureSettings

class BlockHoney : BlockSolid() {
    @get:Override
    override val name: String
        get() = "Honey Block"

    @get:Override
    override val id: Int
        get() = HONEY_BLOCK

    @get:Override
    override val hardness: Double
        get() = 0

    @get:Override
    override val resistance: Double
        get() = 0

    @Override
    override fun hasEntityCollision(): Boolean {
        return true
    }

    @Override
    override fun onEntityCollide(entity: Entity) {
        if (!entity.onGround && entity.motionY <= 0.08 &&
                (entity !is Player
                        || !(entity as Player).getAdventureSettings().get(AdventureSettings.Type.FLYING))) {
            val ex: Double = Math.abs(x + 0.5 - entity.x)
            val ez: Double = Math.abs(z + 0.5 - entity.z)
            val width = 0.4375 + (entity.getWidth() / 2.0f) as Double
            if (ex + 1.0E-3 > width || ez + 1.0E-3 > width) {
                val motion: Vector3 = entity.getMotion()
                motion.y = -0.05
                if (entity.motionY < -0.13) {
                    val m: Double = -0.05 / entity.motionY
                    motion.x *= m
                    motion.z *= m
                }
                if (!entity.getMotion().equals(motion)) {
                    entity.setMotion(motion)
                }
                entity.resetFallDistance()
                if (RANDOM.nextInt(10) === 0) {
                    level.addSound(entity, Sound.LAND_SLIME)
                }
            }
        }
    }

    @Override
    protected override fun recalculateCollisionBoundingBox(): AxisAlignedBB {
        return SimpleAxisAlignedBB(x, y, z, x + 1, y + 1, z + 1)
    }

    @get:Override
    override val minX: Double
        get() = x + 0.1

    @get:Override
    override val maxX: Double
        get() = x + 0.9

    @get:Override
    override val minZ: Double
        get() = z + 0.1

    @get:Override
    override val maxZ: Double
        get() = z + 0.9

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val lightFilter: Int
        get() = 1

    companion object {
        private val RANDOM: Random = Random()
    }
}