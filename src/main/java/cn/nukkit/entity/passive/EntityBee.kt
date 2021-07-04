package cn.nukkit.entity.passive

import cn.nukkit.Player

/**
 * @author joserobjr
 */
@Since("1.1.1.0-PN")
class EntityBee @Since("1.1.1.0-PN") constructor(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    private var beehiveTimer = 600

    @get:Override
    val width: Float
        get() = if (this.isBaby()) {
            0.275f
        } else 0.55f

    @get:Override
    val height: Float
        get() = if (this.isBaby()) {
            0.25f
        } else 0.5f

    @get:Since("1.1.1.0-PN")
    @set:Since("1.1.1.0-PN")
    var hasNectar: Boolean
        get() = false
        set(hasNectar) {}

    @get:Since("1.1.1.0-PN")
    @set:Since("1.1.1.0-PN")
    var isAngry: Boolean
        get() = false
        set(angry) {}

    @Override
    fun onUpdate(currentTick: Int): Boolean {
        if (--beehiveTimer <= 0) {
            var closestBeehive: BlockEntityBeehive? = null
            var closestDistance = Double.MAX_VALUE
            val flower: Optional<Block> = Arrays.stream(level.getCollisionBlocks(getBoundingBox().grow(4, 4, 4), false, true))
                    .filter { block -> block is BlockFlower }
                    .findFirst()
            for (collisionBlock in level.getCollisionBlocks(getBoundingBox().grow(1.5, 1.5, 1.5))) {
                if (collisionBlock is BlockBeehive) {
                    val beehive: BlockEntityBeehive = (collisionBlock as BlockBeehive).getOrCreateBlockEntity()
                    var distance: Double
                    if (beehive.getOccupantsCount() < 4 && beehive.distanceSquared(this).also { distance = it } < closestDistance) {
                        closestBeehive = beehive
                        closestDistance = distance
                    }
                }
            }
            if (closestBeehive != null) {
                val occupant: BlockEntityBeehive.Occupant = closestBeehive.addOccupant(this)
                if (flower.isPresent()) {
                    occupant.setTicksLeftToStay(2400)
                    occupant.setHasNectar(true)
                }
            }
        }
        return true
    }

    @Override
    protected fun initEntity() {
        super.initEntity()
        this.setMaxHealth(10)
    }

    @Since("1.1.1.0-PN")
    fun nectarDelivered(blockEntityBeehive: BlockEntityBeehive?) {
    }

    @Since("1.1.1.0-PN")
    fun leftBeehive(blockEntityBeehive: BlockEntityBeehive?) {
    }

    @Since("1.1.1.0-PN")
    fun setAngry(player: Player?) {
    }

    companion object {
        @get:Override
        @Since("1.1.1.0-PN")
        val networkId = 122
            get() = Companion.field
    }
}