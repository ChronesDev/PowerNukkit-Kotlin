package cn.nukkit.utils

import cn.nukkit.api.API

/**
 * INTERNAL helper class of railway
 *
 *
 * By lmlstarqaq http://snake1999.com/
 * Creation time: 2017/7/1 17:42.
 */
@API(usage = API.Usage.BLEEDING, definition = API.Definition.INTERNAL)
object Rail {
    fun isRailBlock(block: Block): Boolean {
        Objects.requireNonNull(block, "Rail block predicate can not accept null block")
        return isRailBlock(block.getId())
    }

    fun isRailBlock(blockId: Int): Boolean {
        return when (blockId) {
            Block.RAIL, Block.POWERED_RAIL, Block.ACTIVATOR_RAIL, Block.DETECTOR_RAIL -> true
            else -> false
        }
    }

    enum class Orientation(private val meta: Int, private val state: State, from: BlockFace, to: BlockFace, ascendingDirection: BlockFace?) {
        STRAIGHT_NORTH_SOUTH(0, STRAIGHT, NORTH, SOUTH, null), STRAIGHT_EAST_WEST(1, STRAIGHT, EAST, WEST, null), ASCENDING_EAST(2, ASCENDING, EAST, WEST, EAST), ASCENDING_WEST(3, ASCENDING, EAST, WEST, WEST), ASCENDING_NORTH(4, ASCENDING, NORTH, SOUTH, NORTH), ASCENDING_SOUTH(5, ASCENDING, NORTH, SOUTH, SOUTH), CURVED_SOUTH_EAST(6, CURVED, SOUTH, EAST, null), CURVED_SOUTH_WEST(7, CURVED, SOUTH, WEST, null), CURVED_NORTH_WEST(8, CURVED, NORTH, WEST, null), CURVED_NORTH_EAST(9, CURVED, NORTH, EAST, null);

        private val connectingDirections: List<BlockFace>
        private val ascendingDirection: BlockFace?
        fun metadata(): Int {
            return meta
        }

        fun hasConnectingDirections(vararg faces: BlockFace?): Boolean {
            return Stream.of(faces).allMatch(connectingDirections::contains)
        }

        fun connectingDirections(): List<BlockFace> {
            return connectingDirections
        }

        fun ascendingDirection(): Optional<BlockFace> {
            return Optional.ofNullable(ascendingDirection)
        }

        enum class State {
            STRAIGHT, ASCENDING, CURVED
        }

        val isStraight: Boolean
            get() = state == STRAIGHT
        val isAscending: Boolean
            get() = state == ASCENDING
        val isCurved: Boolean
            get() = state == CURVED

        companion object {
            private val META_LOOKUP = arrayOfNulls<Orientation>(values().size)
            fun byMetadata(meta: Int): Orientation? {
                var meta = meta
                if (meta < 0 || meta >= META_LOOKUP.size) {
                    meta = 0
                }
                return META_LOOKUP[meta]
            }

            fun straight(face: BlockFace?): Orientation {
                when (face) {
                    NORTH, SOUTH -> return STRAIGHT_NORTH_SOUTH
                    EAST, WEST -> return STRAIGHT_EAST_WEST
                }
                return STRAIGHT_NORTH_SOUTH
            }

            fun ascending(face: BlockFace?): Orientation {
                when (face) {
                    NORTH -> return ASCENDING_NORTH
                    SOUTH -> return ASCENDING_SOUTH
                    EAST -> return ASCENDING_EAST
                    WEST -> return ASCENDING_WEST
                }
                return ASCENDING_EAST
            }

            fun curved(f1: BlockFace, f2: BlockFace): Orientation {
                for (o in arrayOf(CURVED_SOUTH_EAST, CURVED_SOUTH_WEST, CURVED_NORTH_WEST, CURVED_NORTH_EAST)) {
                    if (o.connectingDirections.contains(f1) && o.connectingDirections.contains(f2)) {
                        return o
                    }
                }
                return CURVED_SOUTH_EAST
            }

            fun straightOrCurved(f1: BlockFace, f2: BlockFace): Orientation {
                for (o in arrayOf(STRAIGHT_NORTH_SOUTH, STRAIGHT_EAST_WEST, CURVED_SOUTH_EAST, CURVED_SOUTH_WEST, CURVED_NORTH_WEST, CURVED_NORTH_EAST)) {
                    if (o.connectingDirections.contains(f1) && o.connectingDirections.contains(f2)) {
                        return o
                    }
                }
                return STRAIGHT_NORTH_SOUTH
            }

            init {
                for (o in values()) {
                    META_LOOKUP[cn.nukkit.utils.o.meta] = cn.nukkit.utils.o
                }
            }
        }

        init {
            connectingDirections = Arrays.asList(from, to)
            this.ascendingDirection = ascendingDirection
        }
    }
}