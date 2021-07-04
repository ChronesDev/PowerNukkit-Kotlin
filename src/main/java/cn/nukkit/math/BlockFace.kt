package cn.nukkit.math

import cn.nukkit.api.PowerNukkitOnly

enum class BlockFace(
        /**
         * Ordering index for D-U-N-S-W-E
         */
        val index: Int,
        /**
         * Index of the opposite BlockFace in the VALUES array
         */
        private val opposite: Int,
        /**
         * Ordering index for the HORIZONTALS field (S-W-N-E)
         */
        val horizontalIndex: Int,
        /**
         * The name of this BlockFace (up, down, north, etc.)
         */
        override val name: String,
        /**
         * Get the AxisDirection of this BlockFace
         *
         * @return axis direction
         */
        val axisDirection: AxisDirection, unitVector: Vector3) {
    DOWN(0, 1, -1, "down", AxisDirection.NEGATIVE, Vector3(0, -1, 0)), UP(1, 0, -1, "up", AxisDirection.POSITIVE, Vector3(0, 1, 0)), NORTH(2, 3, 2, "north", AxisDirection.NEGATIVE, Vector3(0, 0, -1)), SOUTH(3, 2, 0, "south", AxisDirection.POSITIVE, Vector3(0, 0, 1)), WEST(4, 5, 1, "west", AxisDirection.NEGATIVE, Vector3(-1, 0, 0)), EAST(5, 4, 3, "east", AxisDirection.POSITIVE, Vector3(1, 0, 0));

    companion object {
        /**
         * All faces in D-U-N-S-W-E order
         */
        private val VALUES = arrayOfNulls<BlockFace>(6)

        /**
         * All faces with horizontal axis in order S-W-N-E
         */
        private val HORIZONTALS = arrayOfNulls<BlockFace>(4)

        /**
         * Get a BlockFace by it's index (0-5). The order is D-U-N-S-W-E
         *
         * @param index BlockFace index
         * @return block face
         */
        fun fromIndex(index: Int): BlockFace? {
            return VALUES[MathHelper.abs(index % VALUES.size)]
        }

        /**
         * Get a BlockFace by it's horizontal index (0-3). The order is S-W-N-E
         *
         * @param index BlockFace index
         * @return block face
         */
        fun fromHorizontalIndex(index: Int): BlockFace? {
            return HORIZONTALS[MathHelper.abs(index % HORIZONTALS.size)]
        }

        /**
         * Get the BlockFace corresponding to the given angle (0-360). An angle of 0 is SOUTH, an angle of 90 would be WEST
         *
         * @param angle horizontal angle
         * @return block face
         */
        fun fromHorizontalAngle(angle: Double): BlockFace? {
            return fromHorizontalIndex(NukkitMath.floorDouble(angle / 90.0 + 0.5) and 3)
        }

        fun fromAxis(axisDirection: AxisDirection, axis: Axis): BlockFace? {
            for (face in VALUES) {
                if (face!!.axisDirection == axisDirection && face.axis == axis) {
                    return face
                }
            }
            throw RuntimeException("Unable to get face from axis: $axisDirection $axis")
        }

        /**
         * Choose a random BlockFace using the given Random
         *
         * @param rand random number generator
         * @return block face
         */
        fun random(rand: Random): BlockFace? {
            return VALUES[rand.nextInt(VALUES.size)]
        }

        init {
            //Circular dependency
            DOWN.axis = Axis.Y
            UP.axis = Axis.Y
            NORTH.axis = Axis.Z
            SOUTH.axis = Axis.Z
            WEST.axis = Axis.X
            EAST.axis = Axis.X
            for (face in values()) {
                VALUES[cn.nukkit.math.face.index] = cn.nukkit.math.face
                if (cn.nukkit.math.face.getAxis().isHorizontal()) {
                    HORIZONTALS[cn.nukkit.math.face.horizontalIndex] = cn.nukkit.math.face
                }
            }
        }
    }
    /**
     * Get the index of this BlockFace (0-5). The order is D-U-N-S-W-E
     *
     * @return index
     */

    /**
     * Get the horizontal index of this BlockFace (0-3). The order is S-W-N-E
     *
     * @return horizontal index
     */
    /**
     * Get the name of this BlockFace (up, down, north, etc.)
     *
     * @return name
     */

    /**
     * Get the Axis of this BlockFace
     *
     * @return axis
     */
    var axis: Axis? = null
        private set

    /**
     * Normalized vector that points in the direction of this BlockFace
     */
    private val unitVector: Vector3

    /**
     * Get the angle of this BlockFace (0-360)
     *
     * @return horizontal angle
     */
    val horizontalAngle: Float
        get() = ((horizontalIndex and 3) * 90).toFloat()

    /**
     * Get the unit vector of this BlockFace
     *
     * @return vector
     */
    fun getUnitVector(): Vector3 {
        return unitVector
    }

    /**
     * Returns an offset that addresses the block in front of this BlockFace
     *
     * @return x offset
     */
    val xOffset: Int
        get() = if (axis == Axis.X) axisDirection.offset else 0

    /**
     * Returns an offset that addresses the block in front of this BlockFace
     *
     * @return y offset
     */
    val yOffset: Int
        get() = if (axis == Axis.Y) axisDirection.offset else 0

    /**
     * Returns an offset that addresses the block in front of this BlockFace
     *
     * @return x offset
     */
    val zOffset: Int
        get() = if (axis == Axis.Z) axisDirection.offset else 0

    /**
     * Get the opposite BlockFace (e.g. DOWN ==&gt; UP)
     *
     * @return block face
     */
    fun getOpposite(): BlockFace? {
        return fromIndex(opposite)
    }

    /**
     * Rotate this BlockFace around the Y axis clockwise (NORTH =&gt; EAST =&gt; SOUTH =&gt; WEST =&gt; NORTH)
     *
     * @return block face
     */
    fun rotateY(): BlockFace {
        return when (this) {
            NORTH -> EAST
            EAST -> SOUTH
            SOUTH -> WEST
            WEST -> NORTH
            else -> throw RuntimeException("Unable to get Y-rotated face of $this")
        }
    }

    /**
     * Rotate this BlockFace around the Y axis counter-clockwise (NORTH =&gt; WEST =&gt; SOUTH =&gt; EAST =&gt; NORTH)
     *
     * @return block face
     */
    fun rotateYCCW(): BlockFace {
        return when (this) {
            NORTH -> WEST
            EAST -> NORTH
            SOUTH -> EAST
            WEST -> SOUTH
            else -> throw RuntimeException("Unable to get counter-clockwise Y-rotated face of $this")
        }
    }

    val compassRoseDirection: cn.nukkit.math.CompassRoseDirection?
        @PowerNukkitOnly @Since("1.4.0.0-PN") get() = when (this) {
            NORTH -> CompassRoseDirection.NORTH
            SOUTH -> CompassRoseDirection.SOUTH
            WEST -> CompassRoseDirection.WEST
            EAST -> CompassRoseDirection.EAST
            else -> null
        }

    override fun toString(): String {
        return name
    }

    enum class Axis(override val name: String) : Predicate<BlockFace?> {
        X("x"), Y("y"), Z("z");

        var plane: Plane? = null
            private set

        companion object {
            init {
                //Circular dependency
                X.plane = Plane.HORIZONTAL
                Y.plane = Plane.VERTICAL
                Z.plane = Plane.HORIZONTAL
            }
        }

        val isVertical: Boolean
            get() = plane == Plane.VERTICAL
        val isHorizontal: Boolean
            get() = plane == Plane.HORIZONTAL

        fun test(face: BlockFace?): Boolean {
            return face != null && face.axis == this
        }

        override fun toString(): String {
            return name
        }
    }

    enum class AxisDirection(val offset: Int, private val description: String) {
        POSITIVE(1, "Towards positive"), NEGATIVE(-1, "Towards negative");

        override fun toString(): String {
            return description
        }
    }

    enum class Plane : Predicate<BlockFace?>, Iterable<BlockFace?> {
        HORIZONTAL, VERTICAL;

        companion object {
            init {
                //Circular dependency
                HORIZONTAL.faces = arrayOf(NORTH, EAST, SOUTH, WEST)
                VERTICAL.faces = arrayOf(UP, DOWN)
            }
        }

        private var faces: Array<BlockFace>
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun random(): BlockFace {
            return faces[ThreadLocalRandom.current().nextInt(faces.size)]
        }

        fun random(rand: NukkitRandom): BlockFace {
            return faces[rand.nextBoundedInt(faces.size)]
        }

        fun test(face: BlockFace?): Boolean {
            return face != null && face.axis!!.plane == this
        }

        override fun iterator(): Iterator<BlockFace> {
            return Iterators.forArray(faces)
        }
    }

    init {
        this.unitVector = unitVector
    }
}