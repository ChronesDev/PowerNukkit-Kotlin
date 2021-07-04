package cn.nukkit.positiontracking

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class PositionTracking @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(@Nonnull levelName: String?, x: Double, y: Double, z: Double) : NamedPosition(x, y, z) {
    @Nonnull
    private var levelName: String? = null

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull level: Level, x: Double, y: Double, z: Double) : this(level.getName(), x, y, z) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull level: Level?, v: Vector3) : this(level, v.x, v.y, v.z) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull levelName: String?, v: Vector3) : this(levelName, v.x, v.y, v.z) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull pos: Position) : this(pos.getLevel(), pos.x, pos.y, pos.z) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull pos: NamedPosition) : this(pos.getLevelName(), pos.x, pos.y, pos.z) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    override fun getLevelName(): String? {
        return levelName
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setLevelName(@Nonnull levelName: String?) {
        this.levelName = levelName
    }

    @Nonnull
    @Override
    fun add(x: Double): PositionTracking {
        return add(x, 0.0, 0.0)
    }

    @Override
    fun add(x: Double, y: Double): PositionTracking {
        return add(x, y, 0.0)
    }

    @Override
    fun add(x: Double, y: Double, z: Double): PositionTracking {
        return PositionTracking(levelName, x + x, y + y, z + z)
    }

    @Override
    fun add(v: Vector3): PositionTracking {
        return PositionTracking(levelName, x + v.x, y + v.y, z + v.z)
    }

    @Override
    fun subtract(): PositionTracking {
        return PositionTracking(levelName, x, y, z)
    }

    @Override
    fun subtract(x: Double): PositionTracking {
        return subtract(x, 0.0, 0.0)
    }

    @Override
    fun subtract(x: Double, y: Double): PositionTracking {
        return subtract(x, y, 0.0)
    }

    @Override
    fun subtract(x: Double, y: Double, z: Double): PositionTracking {
        return add(-x, -y, -z)
    }

    @Override
    fun subtract(v: Vector3): PositionTracking {
        return add(-v.x, -v.y, -v.z)
    }

    @Override
    fun multiply(number: Double): PositionTracking {
        return PositionTracking(levelName, x * number, y * number, z * number)
    }

    @Override
    fun divide(number: Double): PositionTracking {
        return PositionTracking(levelName, x * number, y * number, z * number)
    }

    @Override
    fun ceil(): PositionTracking {
        return PositionTracking(levelName, Math.ceil(x), Math.ceil(y), Math.ceil(z))
    }

    @Override
    fun floor(): PositionTracking {
        return PositionTracking(levelName, Math.floor(x), Math.floor(y), Math.floor(z))
    }

    @Override
    fun round(): PositionTracking {
        return PositionTracking(levelName, Math.round(this.x), Math.round(this.y), Math.round(this.z))
    }

    @Override
    fun abs(): PositionTracking {
        return PositionTracking(levelName, Math.abs(this.x), Math.abs(this.y), Math.abs(this.z))
    }

    @Override
    fun getSide(face: BlockFace): PositionTracking {
        return getSide(face, 1)
    }

    @Override
    fun getSide(face: BlockFace, step: Int): PositionTracking {
        return PositionTracking(levelName, x + face.getXOffset() * step, y + face.getYOffset() * step, z + face.getZOffset() * step)
    }

    @Override
    fun up(): PositionTracking {
        return up(1)
    }

    @Override
    fun up(step: Int): PositionTracking {
        return getSide(BlockFace.UP, step)
    }

    @Override
    fun down(): PositionTracking {
        return down(1)
    }

    @Override
    fun down(step: Int): PositionTracking {
        return getSide(BlockFace.DOWN, step)
    }

    @Override
    fun north(): PositionTracking {
        return north(1)
    }

    @Override
    fun north(step: Int): PositionTracking {
        return getSide(BlockFace.NORTH, step)
    }

    @Override
    fun south(): PositionTracking {
        return south(1)
    }

    @Override
    fun south(step: Int): PositionTracking {
        return getSide(BlockFace.SOUTH, step)
    }

    @Override
    fun east(): PositionTracking {
        return east(1)
    }

    @Override
    fun east(step: Int): PositionTracking {
        return getSide(BlockFace.EAST, step)
    }

    @Override
    fun west(): PositionTracking {
        return west(1)
    }

    @Override
    fun west(step: Int): PositionTracking {
        return getSide(BlockFace.WEST, step)
    }

    @Nullable
    @Override
    fun getIntermediateWithXValue(@Nonnull v: Vector3?, x: Double): PositionTracking? {
        val intermediateWithXValue: Vector3 = super.getIntermediateWithXValue(v, x) ?: return null
        return PositionTracking(levelName, intermediateWithXValue)
    }

    @Nullable
    @Override
    fun getIntermediateWithYValue(@Nonnull v: Vector3?, y: Double): Vector3? {
        val intermediateWithYValue: Vector3 = super.getIntermediateWithYValue(v, y) ?: return null
        return PositionTracking(levelName, intermediateWithYValue)
    }

    @Nullable
    @Override
    fun getIntermediateWithZValue(@Nonnull v: Vector3?, z: Double): Vector3? {
        val intermediateWithZValue: Vector3 = super.getIntermediateWithZValue(v, z) ?: return null
        return PositionTracking(levelName, intermediateWithZValue)
    }

    @Nullable
    @Override
    fun setComponents(x: Double, y: Double, z: Double): PositionTracking {
        super.setComponents(x, y, z)
        return this
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun setComponents(pos: Vector3?): PositionTracking {
        super.setComponents(pos)
        return this
    }

    @Override
    override fun clone(): PositionTracking {
        return super.clone() as PositionTracking
    }

    init {
        this.levelName = levelName
    }
}