package cn.nukkit.item

import cn.nukkit.Player

class ItemEnderPearl @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ProjectileItem(ENDER_PEARL, 0, count, "Ender Pearl") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxStackSize(): Int {
        return 16
    }

    @Override
    override fun getProjectileEntityType(): String {
        return "EnderPearl"
    }

    @Override
    override fun getThrowForce(): Float {
        return 1.5f
    }

    @Override
    protected override fun correctProjectile(player: Player, projectile: Entity): Entity? {
        if (projectile is EntityEnderPearl) {
            if (player.getServer().getTick() - player.getLastEnderPearlThrowingTick() < 20) {
                projectile.kill()
                return null
            }
            return projectile
        }
        return null
    }
}