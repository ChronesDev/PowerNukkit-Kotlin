package cn.nukkit.math

import cn.nukkit.api.PowerNukkitOnly

/**
 * Represents a 16 direction compass rose.
 *
 * https://en.wikipedia.org/wiki/Compass_rose#/media/File:Brosen_windrose.svg
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
enum class CompassRoseDirection {
    NORTH(0, -1, BlockFace.NORTH, 0), EAST(1, 0, BlockFace.EAST, 90), SOUTH(0, 1, BlockFace.SOUTH, 180), WEST(-1, 0, BlockFace.WEST, 270), NORTH_EAST(NORTH, EAST, BlockFace.NORTH, 45), NORTH_WEST(NORTH, WEST, BlockFace.WEST, 315), SOUTH_EAST(SOUTH, EAST, BlockFace.EAST, 135), SOUTH_WEST(SOUTH, WEST, BlockFace.SOUTH, 225), WEST_NORTH_WEST(WEST, NORTH_WEST, BlockFace.WEST, 292.5), NORTH_NORTH_WEST(NORTH, NORTH_WEST, BlockFace.NORTH, 337.5), NORTH_NORTH_EAST(NORTH, NORTH_EAST, BlockFace.NORTH, 22.5), EAST_NORTH_EAST(EAST, NORTH_EAST, BlockFace.EAST, 67.5), EAST_SOUTH_EAST(EAST, SOUTH_EAST, BlockFace.EAST, 112.5), SOUTH_SOUTH_EAST(SOUTH, SOUTH_EAST, BlockFace.SOUTH, 157.5), SOUTH_SOUTH_WEST(SOUTH, SOUTH_WEST, BlockFace.SOUTH, 202.5), WEST_SOUTH_WEST(WEST, SOUTH_WEST, BlockFace.WEST, 247.5);

    /**
     * Get the amount of X-coordinates to modify to get the represented block
     *
     * @return Amount of X-coordinates to modify
     */
    val modX: Int
        @PowerNukkitOnly @Since("1.4.0.0-PN") get

    /**
     * Get the amount of Z-coordinates to modify to get the represented block
     *
     * @return Amount of Z-coordinates to modify
     */
    val modZ: Int
        @PowerNukkitOnly @Since("1.4.0.0-PN") get
    private val closestBlockFace: BlockFace

    /**
     * Gets the [cn.nukkit.entity.Entity] yaw that represents this direction.
     * @return The yaw value that can be used by entities to look at this direction.
     * @since 1.4.0.0-PN
     */
    val yaw: Float
        @PowerNukkitOnly @Since("1.4.0.0-PN") get

    constructor(modX: Int, modZ: Int, closestBlockFace: BlockFace, yaw: Double) {
        this.modX = modX
        this.modZ = modZ
        this.closestBlockFace = closestBlockFace
        this.yaw = yaw.toFloat()
    }

    constructor(face1: CompassRoseDirection, face2: CompassRoseDirection, closestBlockFace: BlockFace, yaw: Double) {
        modX = face1.modX + face2.modX
        modZ = face1.modZ + face2.modZ
        this.closestBlockFace = closestBlockFace
        this.yaw = yaw.toFloat()
    }

    /**
     * Gets the closest face for this direction. For example, NNE returns N.
     * Even directions like NE will return the direction to the left, N in this case.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getClosestBlockFace(): BlockFace {
        return closestBlockFace
    }

    val oppositeFace: CompassRoseDirection
        @PowerNukkitOnly @Since("1.4.0.0-PN") get() = when (this) {
            NORTH -> SOUTH
            SOUTH -> NORTH
            EAST -> WEST
            WEST -> EAST
            NORTH_EAST -> SOUTH_WEST
            NORTH_WEST -> SOUTH_EAST
            SOUTH_EAST -> NORTH_WEST
            SOUTH_WEST -> NORTH_EAST
            WEST_NORTH_WEST -> EAST_SOUTH_EAST
            NORTH_NORTH_WEST -> SOUTH_SOUTH_EAST
            NORTH_NORTH_EAST -> SOUTH_SOUTH_WEST
            EAST_NORTH_EAST -> WEST_SOUTH_WEST
            EAST_SOUTH_EAST -> WEST_NORTH_WEST
            SOUTH_SOUTH_EAST -> NORTH_NORTH_WEST
            SOUTH_SOUTH_WEST -> NORTH_NORTH_EAST
            WEST_SOUTH_WEST -> EAST_NORTH_EAST
            else -> throw IncompatibleClassChangeError("New values was added to the enum")
        }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @RequiredArgsConstructor
    enum class Precision {
        /**
         * North, South, East, West.
         */
        CARDINAL(4),

        /**
         * N, E, S, W, NE, NW, SE, SW.
         */
        PRIMARY_INTER_CARDINAL(8),

        /**
         * N, E, S, W, NE, NW, SE, SW, WNW, NNW, NNE, ENE, ESE, SSE, SSW, WSW.
         */
        SECONDARY_INTER_CARDINAL(16);

        val directions = 0
    }

    companion object {
        /**
         * Gets the closes direction based on the given [cn.nukkit.entity.Entity] yaw.
         * @param yaw An entity yaw
         * @return The closest direction
         * @since 1.4.0.0-PN
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun getClosestFromYaw(yaw: Double, @Nonnull precision: Precision): CompassRoseDirection {
            return BlockSignPost.GROUND_SIGN_DIRECTION.getValueForMeta(
                    Math.round(Math.round((yaw + 180.0) * precision.directions / 360.0) * (16.0 / precision.directions)) as Int and 0x0f
            )
        }

        /**
         * Gets the closes direction based on the given [cn.nukkit.entity.Entity] yaw.
         * @param yaw An entity yaw
         * @return The closest direction
         * @since 1.4.0.0-PN
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun getClosestFromYaw(yaw: Double): CompassRoseDirection {
            return getClosestFromYaw(yaw, Precision.SECONDARY_INTER_CARDINAL)
        }
    }
}