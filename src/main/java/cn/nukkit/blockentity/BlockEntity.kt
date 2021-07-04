package cn.nukkit.blockentity

import cn.nukkit.Server

/**
 * @author MagicDroidX
 */
@Log4j2
abstract class BlockEntity(chunk: FullChunk?, nbt: CompoundTag) : Position() {
    var chunk: FullChunk?
    var name: String
    var id: Long
    var isMovable = false
    var closed = false
    var namedTag: CompoundTag

    @Deprecated
    @DeprecationDetails(since = "1.3.1.2-PN", reason = "Not necessary and causes slowdown")
    @PowerNukkitDifference(info = "Not updated anymore", since = "1.3.1.2-PN")
    protected var lastUpdate: Long = 0
    protected var server: Server
    protected var timing: Timing
    protected fun initBlockEntity() {}
    val saveId: String
        get() = knownBlockEntities.inverse().get(getClass())

    fun saveNBT() {
        namedTag.putString("id", saveId)
        namedTag.putInt("x", this.getX() as Int)
        namedTag.putInt("y", this.getY() as Int)
        namedTag.putInt("z", this.getZ() as Int)
        namedTag.putBoolean("isMovable", isMovable)
    }

    val cleanedNBT: CompoundTag?
        get() {
            saveNBT()
            val tag: CompoundTag = namedTag.clone()
            tag.remove("x").remove("y").remove("z").remove("id")
            return if (tag.getTags().size() > 0) {
                tag
            } else {
                null
            }
        }
    val block: Block
        get() = this.getLevelBlock()
    abstract val isBlockEntityValid: Boolean
    fun onUpdate(): Boolean {
        return false
    }

    fun scheduleUpdate() {
        this.level.scheduleBlockEntityUpdate(this)
    }

    fun close() {
        if (!closed) {
            closed = true
            if (chunk != null) {
                chunk.removeBlockEntity(this)
            }
            if (this.level != null) {
                this.level.removeBlockEntity(this)
            }
            this.level = null
        }
    }

    fun onBreak() {}
    fun onBreak(isSilkTouch: Boolean) {
        onBreak()
    }

    fun setDirty() {
        chunk.setChanged()
        if (this.getLevelBlock().getId() !== BlockID.AIR) {
            this.level.updateComparatorOutputLevelSelective(this, isObservable)
        }
    }

    /**
     * Indicates if an observer blocks that are looking at this block should blink when [.setDirty] is called.
     */
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val isObservable: Boolean
        get() = true

    @get:Override
    @get:Nullable
    val levelBlockEntity: BlockEntity
        get() = super.getLevelBlockEntity()

    companion object {
        //WARNING: DO NOT CHANGE ANY NAME HERE, OR THE CLIENT WILL CRASH
        const val CHEST = "Chest"
        const val ENDER_CHEST = "EnderChest"
        const val FURNACE = "Furnace"

        @PowerNukkitOnly
        val BLAST_FURNACE = "BlastFurnace"

        @PowerNukkitOnly
        val SMOKER = "Smoker"
        const val SIGN = "Sign"
        const val MOB_SPAWNER = "MobSpawner"
        const val ENCHANT_TABLE = "EnchantTable"
        const val SKULL = "Skull"
        const val FLOWER_POT = "FlowerPot"
        const val BREWING_STAND = "BrewingStand"
        const val DAYLIGHT_DETECTOR = "DaylightDetector"
        const val MUSIC = "Music"
        const val ITEM_FRAME = "ItemFrame"
        const val CAULDRON = "Cauldron"
        const val BEACON = "Beacon"
        const val PISTON_ARM = "PistonArm"
        const val MOVING_BLOCK = "MovingBlock"
        const val COMPARATOR = "Comparator"
        const val HOPPER = "Hopper"
        const val BED = "Bed"
        const val JUKEBOX = "Jukebox"
        const val SHULKER_BOX = "ShulkerBox"
        const val BANNER = "Banner"

        @PowerNukkitOnly
        val LECTERN = "Lectern"

        @PowerNukkitOnly
        val BEEHIVE = "Beehive"

        @PowerNukkitOnly
        val CONDUIT = "Conduit"

        @PowerNukkitOnly
        val BARREL = "Barrel"

        @PowerNukkitOnly
        val CAMPFIRE = "Campfire"

        @PowerNukkitOnly
        val BELL = "Bell"

        @PowerNukkitOnly
        val DISPENSER = "Dispenser"

        @PowerNukkitOnly
        val DROPPER = "Dropper"

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val NETHER_REACTOR = "NetherReactor"

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val LODESTONE = "Lodestone"

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val TARGET = "Target"
        var count: Long = 1
        private val knownBlockEntities: BiMap<String, Class<out BlockEntity>> = HashBiMap.create(21)
        fun createBlockEntity(type: String?, position: Position, vararg args: Object?): BlockEntity {
            return createBlockEntity(type, position, getDefaultCompound(position, type), *args)
        }

        fun createBlockEntity(type: String?, pos: Position, nbt: CompoundTag?, vararg args: Object?): BlockEntity {
            return createBlockEntity(type, pos.getLevel().getChunk(pos.getFloorX() shr 4, pos.getFloorZ() shr 4), nbt, args)
        }

        fun createBlockEntity(type: String, chunk: FullChunk?, nbt: CompoundTag?, vararg args: Object?): BlockEntity? {
            var type = type
            type = type.replaceFirst("BlockEntity", "") //TODO: Remove this after the first release
            var blockEntity: BlockEntity? = null
            val clazz: Class<out BlockEntity> = knownBlockEntities.get(type)
            if (clazz != null) {
                var exceptions: List<Exception?>? = null
                for (constructor in clazz.getConstructors()) {
                    if (blockEntity != null) {
                        break
                    }
                    if (constructor.getParameterCount() !== (if (args == null) 2 else args.size + 2)) {
                        continue
                    }
                    try {
                        if (args == null || args.size == 0) {
                            blockEntity = constructor.newInstance(chunk, nbt)
                        } else {
                            val objects: Array<Object?> = arrayOfNulls<Object>(args.size + 2)
                            objects[0] = chunk
                            objects[1] = nbt
                            System.arraycopy(args, 0, objects, 2, args.size)
                            blockEntity = constructor.newInstance(objects)
                        }
                    } catch (e: Exception) {
                        if (exceptions == null) {
                            exceptions = ArrayList()
                        }
                        exceptions.add(e)
                    }
                }
                if (blockEntity == null) {
                    val cause: Exception = IllegalArgumentException("Could not create a block entity of type $type", if (exceptions != null && exceptions.size() > 0) exceptions[0] else null)
                    if (exceptions != null && exceptions.size() > 1) {
                        for (i in 1 until exceptions.size()) {
                            cause.addSuppressed(exceptions[i])
                        }
                    }
                    log.error("Could not create a block entity of type {} with {} args", type, args?.size ?: 0, cause)
                }
            } else {
                log.debug("Block entity type {} is unknown", type)
            }
            return blockEntity
        }

        fun registerBlockEntity(name: String?, c: Class<out BlockEntity?>?): Boolean {
            if (c == null) {
                return false
            }
            knownBlockEntities.put(name, c)
            return true
        }

        fun getDefaultCompound(pos: Vector3, id: String?): CompoundTag {
            return CompoundTag()
                    .putString("id", id)
                    .putInt("x", pos.getFloorX())
                    .putInt("y", pos.getFloorY())
                    .putInt("z", pos.getFloorZ())
        }
    }

    init {
        if (chunk == null || chunk.getProvider() == null) {
            throw ChunkException("Invalid garbage Chunk given to Block Entity")
        }
        timing = Timings.getBlockEntityTiming(this)
        server = chunk.getProvider().getLevel().getServer()
        this.chunk = chunk
        this.setLevel(chunk.getProvider().getLevel())
        namedTag = nbt
        name = ""
        id = count++
        this.x = namedTag.getInt("x")
        this.y = namedTag.getInt("y")
        this.z = namedTag.getInt("z")
        if (namedTag.contains("isMovable")) {
            isMovable = namedTag.getBoolean("isMovable")
        } else {
            isMovable = true
            namedTag.putBoolean("isMovable", true)
        }
        initBlockEntity()
        if (closed) {
            throw IllegalStateException("Could not create the entity " + getClass().getName().toString() + ", the initializer closed it on construction.")
        }
        this.chunk.addBlockEntity(this)
        this.getLevel().addBlockEntity(this)
    }
}