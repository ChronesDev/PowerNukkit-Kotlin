package cn.nukkit.dispenser

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author CreeperFace
 */
class ProjectileDispenseBehavior(protected val entityType: String) : DefaultDispenseBehavior() {
    @Override
    @PowerNukkitDifference(info = "Implement sound.", since = "1.4.0.0-PN")
    override fun dispense(source: BlockDispenser, face: BlockFace, item: Item): Item? {
        val dispensePos: Vector3 = source.getDispensePosition()
        val nbt: CompoundTag = Entity.getDefaultNBT(dispensePos)
        correctNBT(nbt)
        val projectile: Entity = Entity.createEntity(entityType, source.level.getChunk(dispensePos.getChunkX(), dispensePos.getChunkZ()), nbt) as? EntityProjectile
                ?: return super.dispense(source, face, item)
        val motion: Vector3 = Vector3(face.getXOffset(), face.getYOffset() + 0.1f, face.getZOffset())
                .normalize()
        projectile.setMotion(motion)
        (projectile as EntityProjectile).inaccurate(accuracy)
        projectile.setMotion(projectile.getMotion().multiply(motion))
        (projectile as EntityProjectile).updateRotation()
        projectile.spawnToAll()
        source.level.addSound(source, Sound.RANDOM_BOW)
        return null
    }

    protected val motion: Double
        protected get() = 1.1
    protected val accuracy: Float
        protected get() = 6

    /**
     * you can add extra data of projectile here
     *
     * @param nbt tag
     */
    protected fun correctNBT(nbt: CompoundTag?) {}
}