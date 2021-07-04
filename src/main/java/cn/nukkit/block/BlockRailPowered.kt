package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author Snake1999, larryTheCoder (Nukkit Project, Minecart and Riding Project)
 * @since 2016/1/11
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
class BlockRailPowered(meta: Int) : BlockRail(meta), RedstoneComponent {
    constructor() : this(0) {
        canBePowered = true
    }

    @get:Override
    override val id: Int
        get() = POWERED_RAIL

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = ACTIVABLE_PROPERTIES

    @get:Override
    override val name: String
        get() = "Powered Rail"

    @Override
    @PowerNukkitDifference(info = "Using new method for checking if powered", since = "1.4.0.0-PN")
    override fun onUpdate(type: Int): Int {
        // Warning: I din't recommended this on slow networks server or slow client
        //          Network below 86Kb/s. This will became unresponsive to clients 
        //          When updating the block state. Espicially on the world with many rails. 
        //          Trust me, I tested this on my server.
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE || type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (super.onUpdate(type) === Level.BLOCK_UPDATE_NORMAL) {
                return 0 // Already broken
            }
            if (!this.level.getServer().isRedstoneEnabled()) {
                return 0
            }
            val wasPowered = isActive
            val isPowered = (this.isGettingPower()
                    || checkSurrounding(this, true, 0)
                    || checkSurrounding(this, false, 0))

            // Avoid Block mistake
            if (wasPowered != isPowered) {
                setActive(isPowered)
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
    private fun checkSurrounding(pos: Vector3, relative: Boolean, power: Int): Boolean {
        // The powered rail can power up to 8 blocks only
        if (power >= 8) {
            return false
        }
        // The position of the floor numbers
        var dx: Int = pos.getFloorX()
        var dy: Int = pos.getFloorY()
        var dz: Int = pos.getFloorZ()
        // First: get the base block
        val block: BlockRail
        val block2: Block = level.getBlock(Vector3(dx, dy, dz))

        // Second: check if the rail is Powered rail
        block = if (Rail.isRailBlock(block2)) {
            block2 as BlockRail
        } else {
            return false
        }

        // Used to check if the next ascending rail should be what
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
            else ->                 // Unable to determinate the rail orientation
                // Wrong rail?
                return false
        }
        // Next check the if rail is on power state
        return (canPowered(Vector3(dx, dy, dz), base, power, relative)
                || onStraight && canPowered(Vector3(dx, dy - 1.0, dz), base, power, relative))
    }

    @PowerNukkitDifference(info = "Using new method for checking if powered", since = "1.4.0.0-PN")
    protected fun canPowered(pos: Vector3, state: Rail.Orientation?, power: Int, relative: Boolean): Boolean {
        val block: Block = level.getBlock(pos) as? BlockRailPowered ?: return false
        // What! My block is air??!! Impossible! XD

        // Sometimes the rails are diffrent orientation
        val base: Rail.Orientation = (block as BlockRailPowered).getOrientation()

        // Possible way how to know when the rail is activated is rail were directly powered
        // OR recheck the surrounding... Which will returns here =w=        
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