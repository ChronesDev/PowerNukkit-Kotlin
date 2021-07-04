package cn.nukkit.entity.item

import cn.nukkit.Player

/**
 * @author Snake1999
 * @since 2016/1/30
 */
class EntityMinecartEmpty(chunk: FullChunk?, nbt: CompoundTag?) : EntityMinecartAbstract(chunk, nbt) {
    @get:Override
    val name: String
        get() = type.getName()

    @get:Override
    override val type: MinecartType
        get() = MinecartType.valueOf(0)

    @get:Override
    override val interactButtonText: String
        get() = "action.interact.ride.minecart"

    @get:Override
    override val isRideable: Boolean
        get() = true

    @Override
    protected override fun activate(x: Int, y: Int, z: Int, flag: Boolean) {
        if (flag) {
            if (this.riding != null) {
                mountEntity(riding)
            }
            // looks like MCPE and MCPC not same XD
            // removed rolling feature from here because of MCPE logic?
        }
    }

    @Override
    override fun onUpdate(currentTick: Int): Boolean {
        var update: Boolean = super.onUpdate(currentTick)
        if (this.passengers.isEmpty()) {
            for (entity in this.level.getCollidingEntities(this.boundingBox.grow(0.20000000298023224, 0.0, 0.20000000298023224), this)) {
                if (entity.riding != null || entity !is EntityLiving || entity is Player || entity is EntityWaterAnimal) {
                    continue
                }
                this.mountEntity(entity)
                update = true
                break
            }
        }
        return update
    }

    companion object {
        @get:Override
        val networkId = 84
            get() = Companion.field
    }
}