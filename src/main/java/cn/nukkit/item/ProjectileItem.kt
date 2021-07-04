package cn.nukkit.item

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
abstract class ProjectileItem(id: Int, meta: Integer?, count: Int, name: String?) : Item(id, meta, count, name) {
    abstract fun getProjectileEntityType(): String?
    abstract fun getThrowForce(): Float
    override fun onClickAir(player: Player, directionVector: Vector3): Boolean {
        val nbt: CompoundTag = CompoundTag()
                .putList(ListTag<DoubleTag>("Pos")
                        .add(DoubleTag("", player.x))
                        .add(DoubleTag("", player.y + player.getEyeHeight() - 0.30000000149011612))
                        .add(DoubleTag("", player.z)))
                .putList(ListTag<DoubleTag>("Motion")
                        .add(DoubleTag("", directionVector.x))
                        .add(DoubleTag("", directionVector.y))
                        .add(DoubleTag("", directionVector.z)))
                .putList(ListTag<FloatTag>("Rotation")
                        .add(FloatTag("", player.yaw as Float))
                        .add(FloatTag("", player.pitch as Float)))
        correctNBT(nbt)
        var projectile: Entity = Entity.createEntity(getProjectileEntityType(), player.getLevel().getChunk(player.getFloorX() shr 4, player.getFloorZ() shr 4), nbt, player)
        if (projectile != null) {
            projectile = correctProjectile(player, projectile)
            if (projectile == null) {
                return false
            }
            projectile.setMotion(projectile.getMotion().multiply(getThrowForce()))
            if (projectile is EntityProjectile) {
                val ev = ProjectileLaunchEvent(projectile as EntityProjectile)
                player.getServer().getPluginManager().callEvent(ev)
                if (ev.isCancelled()) {
                    projectile.kill()
                } else {
                    if (!player.isCreative()) {
                        this.count--
                    }
                    if (projectile is EntityEnderPearl) {
                        player.onThrowEnderPearl()
                    }
                    projectile.spawnToAll()
                    addThrowSound(player)
                }
            }
        } else {
            return false
        }
        return true
    }

    protected fun addThrowSound(player: Player) {
        player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacketV2.SOUND_THROW, -1, "minecraft:player", false, false)
    }

    protected fun correctProjectile(player: Player?, projectile: Entity): Entity {
        return projectile
    }

    protected fun correctNBT(nbt: CompoundTag?) {}
}