package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author Nukkit Project Team
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
class BlockRailActivator(meta: Int) : BlockRail(meta), RedstoneComponent {
    constructor() : this(0) {
        canBePowered = true
    }

    @get:Override
    override val name: String
        get() = "Activator Rail"

    @get:Override
    override val id: Int
        get() = ACTIVATOR_RAIL

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = ACTIVABLE_PROPERTIES

    @PowerNukkitDifference(info = "Using new method for checking if powered", since = "1.4.0.0-PN")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE || type == Level.BLOCK_UPDATE_SCHEDULED) {
            super.onUpdate(type)
            val wasPowered = isActive
            val isPowered = (this.isGettingPower()
                    || checkSurrounding(this, true, 0)
                    || checkSurrounding(this, false, 0))
            var hasUpdate = false
            if (wasPowered != isPowered) {
                setActive(isPowered)
                hasUpdate = true
            }
            if (hasUpdate) {
                level.updateAround(down())
                if (getOrientation().isAscending()) {
                    level.updateAround(up())
                }
            }
            return type
        }
        return 0
    }

    /**
     * Check the surrounding of the rail
     *
     * @param pos      The rail position
     * @param relative The relative of the rail that will be checked
     * @param power    The count of the rail that had been counted
     * @return Boolean of the surrounding area. Where the powered rail on!
     */
    protected fun checkSurrounding(pos: Vector3, relative: Boolean, power: Int): Boolean {
        if (power >= 8) {
            return false
        }
        var dx: Int = pos.getFloorX()
        var dy: Int = pos.getFloorY()
        var dz: Int = pos.getFloorZ()
        val block: BlockRail
        val block2: Block = level.getBlock(Vector3(dx, dy, dz))
        block = if (Rail.isRailBlock(block2)) {
            block2 as BlockRail
        } else {
            return false
        }
        var base: Rail.Orientation? = null
        var onStraight = true
        when (block.getOrientation()) {
            STRAIGHT_NORTH_SOUTH -> if (relative) {
                dz++
            } else {
                dz--
            }
            STRAIGHT_EAST_WEST -> if (relative) {
                dx--
            } else {
                dx++
            }
            ASCENDING_EAST -> {
                if (relative) {
                    dx--
                } else {
                    dx++
                    dy++
                    onStraight = false
                }
                base = Rail.Orientation.STRAIGHT_EAST_WEST
            }
            ASCENDING_WEST -> {
                if (relative) {
                    dx--
                    dy++
                    onStraight = false
                } else {
                    dx++
                }
                base = Rail.Orientation.STRAIGHT_EAST_WEST
            }
            ASCENDING_NORTH -> {
                if (relative) {
                    dz++
                } else {
                    dz--
                    dy++
                    onStraight = false
                }
                base = Rail.Orientation.STRAIGHT_NORTH_SOUTH
            }
            ASCENDING_SOUTH -> {
                if (relative) {
                    dz++
                    dy++
                    onStraight = false
                } else {
                    dz--
                }
                base = Rail.Orientation.STRAIGHT_NORTH_SOUTH
            }
            else -> return false
        }
        return (canPowered(Vector3(dx, dy, dz), base, power, relative)
                || onStraight && canPowered(Vector3(dx, dy - 1.0, dz), base, power, relative))
    }

    @PowerNukkitDifference(info = "Using new method for checking if powered", since = "1.4.0.0-PN")
    protected fun canPowered(pos: Vector3, state: Rail.Orientation?, power: Int, relative: Boolean): Boolean {
        val block: Block = level.getBlock(pos) as? BlockRailActivator ?: return false
        val base: Rail.Orientation = (block as BlockRailActivator).getOrientation()
        return ((state !== Rail.Orientation.STRAIGHT_EAST_WEST
                || base !== Rail.Orientation.STRAIGHT_NORTH_SOUTH && base !== Rail.Orientation.ASCENDING_NORTH && base !== Rail.Orientation.ASCENDING_SOUTH)
                && (state !== Rail.Orientation.STRAIGHT_NORTH_SOUTH
                || base !== Rail.Orientation.STRAIGHT_EAST_WEST && base !== Rail.Orientation.ASCENDING_EAST && base !== Rail.Orientation.ASCENDING_WEST)
                && (this.isGettingPower() || checkSurrounding(pos, relative, power + 1)))
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