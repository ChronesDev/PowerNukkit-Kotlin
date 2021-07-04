package cn.nukkit.event.entity

import cn.nukkit.entity.Entity

/**
 * @author Box (Nukkit Project)
 */
class EntityShootBowEvent(shooter: EntityLiving?, bow: Item, projectile: EntityProjectile, force: Double) : EntityEvent(), Cancellable {
    private val bow: Item
    private var projectile: EntityProjectile
    var force: Double

    @Override
    override fun getEntity(): EntityLiving {
        return this.entity as EntityLiving
    }

    fun getBow(): Item {
        return bow
    }

    fun getProjectile(): EntityProjectile {
        return projectile
    }

    fun setProjectile(projectile: Entity) {
        if (projectile !== this.projectile) {
            if (this.projectile.getViewers().size() === 0) {
                this.projectile.kill()
                this.projectile.close()
            }
            this.projectile = projectile as EntityProjectile
        }
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.entity = shooter
        this.bow = bow
        this.projectile = projectile
        this.force = force
    }
}