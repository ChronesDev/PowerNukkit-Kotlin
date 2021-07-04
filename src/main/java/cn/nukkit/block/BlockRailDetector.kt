package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author CreeperFace (Nukkit Project), larryTheCoder (Minecart and Riding Project)
 * @since 2015/11/22
 */
class BlockRailDetector(meta: Int) : BlockRail(meta) {
    constructor() : this(0) {
        canBePowered = true
    }

    @get:Override
    override val id: Int
        get() = DETECTOR_RAIL

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = ACTIVABLE_PROPERTIES

    @get:Override
    override val name: String
        get() = "Detector Rail"

    @get:Override
    override val isPowerSource: Boolean
        get() = true

    @Override
    override fun getWeakPower(side: BlockFace?): Int {
        return if (isActive) 15 else 0
    }

    @Override
    override fun getStrongPower(side: BlockFace): Int {
        return if (isActive) 0 else if (side === BlockFace.UP) 15 else 0
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            updateState()
            return type
        }
        return super.onUpdate(type)
    }

    @Override
    override fun onEntityCollide(entity: Entity?) {
        updateState()
    }

    protected fun updateState() {
        val wasPowered = isActive
        var isPowered = false
        for (entity in level.getNearbyEntities(SimpleAxisAlignedBB(
                getFloorX() + 0.125,
                getFloorY(),
                getFloorZ() + 0.125,
                getFloorX() + 0.875,
                getFloorY() + 0.525,
                getFloorZ() + 0.875))) {
            if (entity is EntityMinecartAbstract) {
                isPowered = true
                break
            }
        }
        if (isPowered && !wasPowered) {
            setActive(true)
            level.scheduleUpdate(this, this, 0)
            level.scheduleUpdate(this, this.down(), 0)
        }
        if (!isPowered && wasPowered) {
            setActive(false)
            level.scheduleUpdate(this, this, 0)
            level.scheduleUpdate(this, this.down(), 0)
        }
        level.updateComparatorOutputLevel(this)
    }

    @get:Override
    override var isActive: Boolean
        get() = getBooleanValue(ACTIVE)
        set(isActive) {
            super.isActive = isActive
        }

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    @set:Override
    @set:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    override var isRailActive: OptionalBoolean
        get() = OptionalBoolean.of(getBooleanValue(ACTIVE))
        set(active) {
            setBooleanValue(ACTIVE, active)
        }
}