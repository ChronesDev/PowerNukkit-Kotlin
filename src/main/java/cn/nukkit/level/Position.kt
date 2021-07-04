package cn.nukkit.level

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Overrides NamedPosition instead of Vector3")
class Position @JvmOverloads constructor(x: Double = 0.0, y: Double = 0.0, z: Double = 0.0, level: Level? = null) : NamedPosition() {
    var level: Level?
    fun getLevel(): Level? {
        return level
    }

    fun setLevel(level: Level?): Position {
        this.level = level
        return this
    }

    val isValid: Boolean
        get() = level != null

    fun setStrong(): Boolean {
        return false
    }

    fun setWeak(): Boolean {
        return false
    }

    fun getSide(face: BlockFace?): Position {
        return this.getSide(face, 1)
    }

    fun getSide(face: BlockFace?, step: Int): Position {
        return fromObject(super.getSide(face, step), validLevel)
    }

    @Override
    override fun toString(): String {
        return "Position(level=" + (if (isValid) getLevel().getName() else "null") + ",x=" + this.x + ",y=" + this.y + ",z=" + this.z + ")"
    }

    @Override
    fun setComponents(x: Double, y: Double, z: Double): Position {
        x = x
        y = y
        z = z
        return this
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun setComponents(pos: Vector3?): Position {
        super.setComponents(pos)
        return this
    }

    @get:Nullable
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val levelBlockEntity: BlockEntity
        get() = validLevel.getBlockEntity(this)

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    fun <T : BlockEntity?> getTypedBlockEntity(@Nonnull type: Class<T>): T? {
        val blockEntity: BlockEntity = validLevel.getBlockEntity(this)
        return if (type.isInstance(blockEntity)) type.cast(blockEntity) else null
    }

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val levelBlockState: BlockState
        get() = getLevelBlockState(0)

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getLevelBlockState(layer: Int): BlockState {
        return validLevel.getBlockStateAt(getFloorX(), getFloorY(), getFloorZ(), layer)
    }

    val levelBlock: Block
        get() = validLevel.getBlock(this)

    fun getLevelBlockAtLayer(layer: Int): Block {
        return validLevel.getBlock(this, layer)
    }

    @get:Nonnull
    val location: cn.nukkit.level.Location
        get() = Location(this.x, this.y, this.z, 0, 0, validLevel)

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    val levelName: String
        get() = validLevel.getName()

    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    val validLevel: cn.nukkit.level.Level
        get() = level ?: throw LevelException("Undefined Level reference")

    @Override
    fun add(x: Double): Position {
        return this.add(x, 0.0, 0.0)
    }

    @Override
    fun add(x: Double, y: Double): Position {
        return this.add(x, y, 0.0)
    }

    @Override
    fun add(x: Double, y: Double, z: Double): Position {
        return Position(x + x, y + y, z + z, level)
    }

    @Override
    fun add(x: Vector3): Position {
        return Position(x + x.getX(), this.y + x.getY(), this.z + x.getZ(), level)
    }

    @Override
    fun subtract(): Position {
        return this.subtract(0.0, 0.0, 0.0)
    }

    @Override
    fun subtract(x: Double): Position {
        return this.subtract(x, 0.0, 0.0)
    }

    @Override
    fun subtract(x: Double, y: Double): Position {
        return this.subtract(x, y, 0.0)
    }

    @Override
    fun subtract(x: Double, y: Double, z: Double): Position {
        return this.add(-x, -y, -z)
    }

    @Override
    fun subtract(x: Vector3): Position {
        return this.add(-x.getX(), -x.getY(), -x.getZ())
    }

    @Override
    fun multiply(number: Double): Position {
        return Position(this.x * number, this.y * number, this.z * number, level)
    }

    @Override
    fun divide(number: Double): Position {
        return Position(this.x / number, this.y / number, this.z / number, level)
    }

    @Override
    fun ceil(): Position {
        return Position((Math.ceil(this.x) as Int).toDouble(), (Math.ceil(this.y) as Int).toDouble(), (Math.ceil(this.z) as Int).toDouble(), level)
    }

    @Override
    fun floor(): Position {
        return Position(this.getFloorX(), this.getFloorY(), this.getFloorZ(), level)
    }

    @Override
    fun round(): Position {
        return Position(Math.round(this.x), Math.round(this.y), Math.round(this.z), level)
    }

    @Override
    fun abs(): Position {
        return Position((Math.abs(this.x) as Int).toDouble(), (Math.abs(this.y) as Int).toDouble(), (Math.abs(this.z) as Int).toDouble(), level)
    }

    @Override
    fun clone(): Position {
        return super.clone() as Position
    }

    @get:Nullable
    val chunk: FullChunk?
        get() = if (isValid) level!!.getChunk(getChunkX(), getChunkZ()) else null

    companion object {
        fun fromObject(pos: Vector3): Position {
            return fromObject(pos, null)
        }

        fun fromObject(pos: Vector3, level: Level?): Position {
            return Position(pos.x, pos.y, pos.z, level)
        }
    }

    init {
        x = x
        y = y
        z = z
        this.level = level
    }
}