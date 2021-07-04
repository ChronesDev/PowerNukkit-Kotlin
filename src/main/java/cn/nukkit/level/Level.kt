package cn.nukkit.level

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
class Level @PowerNukkitOnly("Makes easier to create tests") @Since("1.4.0.0-PN") internal constructor(server: Server, name: String?, path: String?, usesChunkSection: BooleanSupplier, provider: BiFunction<Level?, String?, LevelProvider?>) : ChunkManager, Metadatable {
    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EMPTY_ARRAY = arrayOfNulls<Level>(0)
        private var levelIdCounter = 1
        private var chunkLoaderCounter = 1

        @SuppressWarnings(["java:S1444", "java:S3008"])
        var COMPRESSION_LEVEL = 8
        const val BLOCK_UPDATE_NORMAL = 1
        const val BLOCK_UPDATE_RANDOM = 2
        const val BLOCK_UPDATE_SCHEDULED = 3
        const val BLOCK_UPDATE_WEAK = 4
        const val BLOCK_UPDATE_TOUCH = 5
        const val BLOCK_UPDATE_REDSTONE = 6
        const val BLOCK_UPDATE_TICK = 7

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val BLOCK_UPDATE_MOVED: Int = dynamic(1000000)
        const val TIME_DAY = 0
        const val TIME_NOON = 6000
        const val TIME_SUNSET = 12000
        const val TIME_NIGHT = 14000
        const val TIME_MIDNIGHT = 18000
        const val TIME_SUNRISE = 23000
        const val TIME_FULL = 24000
        const val DIMENSION_OVERWORLD = 0
        const val DIMENSION_NETHER = 1
        const val DIMENSION_THE_END = 2

        // Lower values use less memory
        const val MAX_BLOCK_CACHE = 512

        // The blocks that can randomly tick
        private val randomTickBlocks = BooleanArray(Block.MAX_BLOCK_ID)
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun canRandomTick(blockId: Int): Boolean {
            return blockId < randomTickBlocks.size && randomTickBlocks[blockId]
        }

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun setCanRandomTick(blockId: Int, newValue: Boolean) {
            randomTickBlocks[blockId] = newValue
        }

        private const val LCG_CONSTANT = 1013904223
        fun chunkHash(x: Int, z: Int): Long {
            return x.toLong() shl 32 or (z and 0xffffffffL).toLong()
        }

        fun blockHash(x: Int, y: Int, z: Int): Long {
            if (y < 0 || y >= 256) {
                throw IllegalArgumentException("Y coordinate y is out of range!")
            }
            return x.toLong() and 0xFFFFFFF.toLong() shl 36 or (y.toLong() and 0xFF.toLong() shl 28) or (z.toLong() and 0xFFFFFFF.toLong())
        }

        fun localBlockHash(x: Double, y: Double, z: Double): Char {
            val hi = ((x.toInt() and 15) + (z.toInt() and 15 shl 4)).toByte()
            val lo = y.toByte()
            return (hi and 0xFF shl 8 or (lo and 0xFF))
        }

        fun getBlockXYZ(chunkHash: Long, blockHash: Char): Vector3 {
            val hi: Int = (blockHash.toInt() ushr 8) as Byte.toInt()
            val lo: Int = blockHash as Byte.toInt()
            val y = lo and 0xFF
            val x = (hi and 0xF) + (getHashX(chunkHash) shl 4)
            val z = (hi shr 4 and 0xF) + (getHashZ(chunkHash) shl 4)
            return Vector3(x, y, z)
        }

        fun chunkBlockHash(x: Int, y: Int, z: Int): Int {
            return x shl 12 or (z shl 8) or y
        }

        fun getHashX(hash: Long): Int {
            return (hash shr 32).toInt()
        }

        fun getHashZ(hash: Long): Int {
            return hash.toInt()
        }

        fun getBlockXYZ(hash: BlockVector3): Vector3 {
            return Vector3(hash.x, hash.y, hash.z)
        }

        fun getChunkXZ(hash: Long): Chunk.Entry {
            return Entry(getHashX(hash), getHashZ(hash))
        }

        fun generateChunkLoaderId(loader: ChunkLoader): Int {
            return if (loader.getLoaderId() === 0) {
                chunkLoaderCounter++
            } else {
                throw IllegalStateException("ChunkLoader has a loader id already assigned: " + loader.getLoaderId())
            }
        }

        private val ENTITY_BUFFER: Array<Entity?> = arrayOfNulls<Entity>(512)

        init {
            Timings.isTimingsEnabled() // Fixes Concurrency issues on static initialization
        }

        init {
            randomTickBlocks[BlockID.GRASS] = true
            randomTickBlocks[BlockID.FARMLAND] = true
            randomTickBlocks[BlockID.MYCELIUM] = true
            randomTickBlocks[BlockID.SAPLING] = true
            randomTickBlocks[BlockID.LEAVES] = true
            randomTickBlocks[BlockID.LEAVES2] = true
            randomTickBlocks[BlockID.SNOW_LAYER] = true
            randomTickBlocks[BlockID.ICE] = true
            randomTickBlocks[BlockID.LAVA] = true
            randomTickBlocks[BlockID.STILL_LAVA] = true
            randomTickBlocks[BlockID.CACTUS] = true
            randomTickBlocks[BlockID.BEETROOT_BLOCK] = true
            randomTickBlocks[BlockID.CARROT_BLOCK] = true
            randomTickBlocks[BlockID.POTATO_BLOCK] = true
            randomTickBlocks[BlockID.MELON_STEM] = true
            randomTickBlocks[BlockID.PUMPKIN_STEM] = true
            randomTickBlocks[BlockID.WHEAT_BLOCK] = true
            randomTickBlocks[BlockID.SUGARCANE_BLOCK] = true
            randomTickBlocks[BlockID.RED_MUSHROOM] = true
            randomTickBlocks[BlockID.BROWN_MUSHROOM] = true
            randomTickBlocks[BlockID.NETHER_WART_BLOCK] = true
            randomTickBlocks[BlockID.FIRE] = true
            randomTickBlocks[BlockID.GLOWING_REDSTONE_ORE] = true
            randomTickBlocks[BlockID.COCOA_BLOCK] = true
            randomTickBlocks[BlockID.VINE] = true
            randomTickBlocks[BlockID.CORAL_FAN] = true
            randomTickBlocks[BlockID.CORAL_FAN_DEAD] = true
            randomTickBlocks[BlockID.BLOCK_KELP] = true
            randomTickBlocks[BlockID.SWEET_BERRY_BUSH] = true
            randomTickBlocks[BlockID.TURTLE_EGG] = true
            randomTickBlocks[BlockID.BAMBOO] = true
            randomTickBlocks[BlockID.BAMBOO_SAPLING] = true
            randomTickBlocks[BlockID.CRIMSON_NYLIUM] = true
            randomTickBlocks[BlockID.WARPED_NYLIUM] = true
            randomTickBlocks[BlockID.TWISTING_VINES] = true
        }
    }

    private val blockEntities: Long2ObjectOpenHashMap<BlockEntity> = Long2ObjectOpenHashMap()
    private val players: Long2ObjectOpenHashMap<Player> = Long2ObjectOpenHashMap()
    private val entities: Long2ObjectOpenHashMap<Entity> = Long2ObjectOpenHashMap()
    val updateEntities: Long2ObjectOpenHashMap<Entity> = Long2ObjectOpenHashMap()
    private val updateBlockEntities: ConcurrentLinkedQueue<BlockEntity> = ConcurrentLinkedQueue()
    private var cacheChunks = false
    private val server: Server
    val id: Int
    private var provider: LevelProvider? = null
    private val loaders: Int2ObjectOpenHashMap<ChunkLoader> = Int2ObjectOpenHashMap()
    private val loaderCounter: Int2IntMap = Int2IntOpenHashMap()
    private val chunkLoaders: Long2ObjectOpenHashMap<Map<Integer, ChunkLoader>> = Long2ObjectOpenHashMap()
    private val playerLoaders: Long2ObjectOpenHashMap<Map<Integer, Player>> = Long2ObjectOpenHashMap()
    private val chunkPackets: Long2ObjectOpenHashMap<Deque<DataPacket>> = Long2ObjectOpenHashMap()
    private val unloadQueue: Long2LongMap = Long2LongMaps.synchronize(Long2LongOpenHashMap())
    private var time = 0f
    var stopTime = false
    var skyLightSubtracted = 0f
    var folderName: String? = null
    private val mutableBlock: Vector3? = null

    // Avoid OOM, gc'd references result in whole chunk being sent (possibly higher cpu)
    private val changedBlocks: Long2ObjectOpenHashMap<SoftReference<Map<Character, Object>>> = Long2ObjectOpenHashMap()

    // Storing the vector is redundant
    private val changeBlocksPresent: Object = Object()

    // Storing extra blocks past 512 is redundant
    private val changeBlocksFullMap: Map<Character, Object> = object : HashMap<Character?, Object?>() {
        @Override
        fun size(): Int {
            return Character.MAX_VALUE
        }
    }
    private val updateQueue: BlockUpdateScheduler
    private val normalUpdateQueue: Queue<QueuedUpdate> = ConcurrentLinkedDeque()

    //    private final TreeSet<BlockUpdateEntry> updateQueue = new TreeSet<>();
    //    private final List<BlockUpdateEntry> nextTickUpdates = Lists.newArrayList();
    //private final Map<BlockVector3, Integer> updateQueueIndex = new HashMap<>();
    private val chunkSendQueue: ConcurrentMap<Long, Int2ObjectMap<Player>> = ConcurrentHashMap()
    private val chunkSendTasks: LongSet = LongOpenHashSet()
    private val chunkPopulationQueue: Long2ObjectOpenHashMap<Boolean> = Long2ObjectOpenHashMap()
    private val chunkPopulationLock: Long2ObjectOpenHashMap<Boolean> = Long2ObjectOpenHashMap()
    private val chunkGenerationQueue: Long2ObjectOpenHashMap<Boolean> = Long2ObjectOpenHashMap()
    private var chunkGenerationQueueSize = 8
    private var chunkPopulationQueueSize = 2
    var autoSave = false
    private var blockMetadata: BlockMetadataStore? = null
    private var useSections = false
    private var temporalPosition: Position? = null
    private var temporalVector: Vector3? = null
    var sleepTicks = 0
    private var chunkTickRadius = 0
    private val chunkTickList: Long2IntMap = Long2IntOpenHashMap()
    private var chunksPerTicks = 0
    private var clearChunksOnTick = false
    var updateLCG: Int = ThreadLocalRandom.current().nextInt()
        get() = field * 3 xor LCG_CONSTANT.also { field = it }
        private set
    var timings: LevelTimings? = null
    var tickRate = 0
    var tickRateTime = 0
    var tickRateCounter = 0
    private var generatorClass: Class<out Generator?>? = null
    private val generators: IterableThreadLocal<Generator> = object : IterableThreadLocal<Generator?>() {
        @Override
        fun init(): Generator? {
            return try {
                val generator: Generator = generatorClass.getConstructor(Map::class.java).newInstance(requireProvider().getGeneratorOptions())
                val rand = NukkitRandom(seed)
                var manager: ChunkManager
                if (Server.getInstance().isPrimaryThread()) {
                    generator.init(this@Level, rand)
                }
                generator.init(PopChunkManager(seed), rand)
                generator
            } catch (e: Throwable) {
                e.printStackTrace()
                null
            }
        }
    }
    var isRaining = false
        private set
    var rainTime = 0
    private var thundering = false
    var thunderTime = 0
    var currentTick: Long = 0
        private set
    var dimension = 0
        private set
    var gameRules: GameRules? = null

    constructor(server: Server, name: String?, path: String?, provider: Class<out LevelProvider?>) : this(server, name, path,
            {
                try {
                    return@`this` provider.getMethod("usesChunkSection").invoke(null)
                } catch (e: ReflectiveOperationException) {
                    throw LevelException("usesChunkSection of $provider failed", e)
                }
            },
            { level, levelPath ->
                try {
                    return@`this` provider.getConstructor(Level::class.java, String::class.java).newInstance(level, levelPath)
                } catch (e: ReflectiveOperationException) {
                    throw LevelException("Constructor of $provider failed", e)
                }
            }
    ) {
    }

    @PowerNukkitOnly("Makes easier to create tests")
    @Since("1.4.0.0-PN")
    internal constructor(server: Server, name: String?, path: File, usesChunkSection: Boolean, provider: LevelProvider?) : this(server, name, path.getAbsolutePath().toString() + "/", { usesChunkSection }, { lvl, p -> provider }) {
    }

    fun initLevel() {
        val generator: Generator = generators.get()
        dimension = generator.getDimension()
        gameRules = requireProvider().getGamerules()
        log.info("Preparing start region for level \"{}\"", folderName)
        val spawn: Position = spawnLocation
        populateChunk(spawn.getChunkX(), spawn.getChunkZ(), true)
    }

    val generator: Generator
        get() = generators.get()

    fun getBlockMetadata(): BlockMetadataStore? {
        return blockMetadata
    }

    fun getServer(): Server {
        return server
    }

    fun getProvider(): LevelProvider? {
        return provider
    }

    /**
     * Returns the level provider if it exists. Tries to close and unregister the level and then throw an exception if it doesn't.
     * @throws LevelException If the level is already closed
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun requireProvider(): LevelProvider {
        val levelProvider: LevelProvider? = getProvider()
        if (levelProvider == null) {
            val levelException = LevelException("The level \"$folderName\" is already closed (have no providers)")
            try {
                close()
            } catch (e: Exception) {
                levelException.addSuppressed(e)
            }
            throw levelException
        }
        return levelProvider
    }

    fun close() {
        val levelProvider: LevelProvider? = provider
        if (levelProvider != null) {
            if (autoSave) {
                save(true)
            }
            levelProvider.close()
        }
        provider = null
        blockMetadata = null
        temporalPosition = null
        server.getLevels().remove(id)
        generators.clean()
    }

    fun addSound(pos: Vector3?, sound: Sound) {
        this.addSound(pos, sound, 1f, 1f, *(null as Array<Player?>?)!!)
    }

    fun addSound(pos: Vector3?, sound: Sound, volume: Float, pitch: Float) {
        this.addSound(pos, sound, volume, pitch, *(null as Array<Player?>?)!!)
    }

    fun addSound(pos: Vector3?, sound: Sound?, volume: Float, pitch: Float, players: Collection<Player?>) {
        this.addSound(pos, sound, volume, pitch, players.toArray(Player.EMPTY_ARRAY))
    }

    fun addSound(pos: Vector3?, sound: Sound, volume: Float, pitch: Float, vararg players: Player?) {
        Preconditions.checkArgument(volume >= 0 && volume <= 1, "Sound volume must be between 0 and 1")
        Preconditions.checkArgument(pitch >= 0, "Sound pitch must be higher than 0")
        val packet = PlaySoundPacket()
        packet.name = sound.getSound()
        packet.volume = volume
        packet.pitch = pitch
        packet.x = pos.getFloorX()
        packet.y = pos.getFloorY()
        packet.z = pos.getFloorZ()
        if (players == null || players.size == 0) {
            addChunkPacket(pos.getFloorX() shr 4, pos.getFloorZ() shr 4, packet)
        } else {
            Server.broadcastPacket(players, packet)
        }
    }

    @JvmOverloads
    fun addLevelEvent(type: Int, data: Int, pos: Vector3? = null) {
        if (pos == null) {
            addLevelEvent(type, data, 0f, 0f, 0f)
        } else {
            addLevelEvent(type, data, pos.x as Float, pos.y as Float, pos.z as Float)
        }
    }

    fun addLevelEvent(type: Int, data: Int, x: Float, y: Float, z: Float) {
        val packet = LevelEventPacket()
        packet.evid = type
        packet.x = x
        packet.y = y
        packet.z = z
        packet.data = data
        addChunkPacket(NukkitMath.floorFloat(x) shr 4, NukkitMath.floorFloat(z) shr 4, packet)
    }

    fun addLevelEvent(pos: Vector3?, event: Int) {
        this.addLevelEvent(pos, event, 0)
    }

    @Since("1.4.0.0-PN")
    fun addLevelEvent(pos: Vector3, event: Int, data: Int) {
        val pk = LevelEventPacket()
        pk.evid = event
        pk.x = pos.x
        pk.y = pos.y
        pk.z = pos.z
        pk.data = data
        addChunkPacket(pos.getFloorX() shr 4, pos.getFloorZ() shr 4, pk)
    }

    @PowerNukkitDifference(info = "Default sound method changed to addSound", since = "1.4.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "Old method, use addSound(pos, Sound.<SOUND_VALUE>).", since = "1.4.0.0-PN")
    fun addLevelSoundEvent(pos: Vector3, type: Int, data: Int, entityType: Int) {
        addLevelSoundEvent(pos, type, data, entityType, false, false)
    }

    @PowerNukkitDifference(info = "Default sound method changed to addSound", since = "1.4.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "Old method, use addSound(pos, Sound.<SOUND_VALUE>).", since = "1.4.0.0-PN")
    fun addLevelSoundEvent(pos: Vector3, type: Int, data: Int, entityType: Int, isBaby: Boolean, isGlobal: Boolean) {
        val identifier: String = AddEntityPacket.LEGACY_IDS.getOrDefault(entityType, ":")
        addLevelSoundEvent(pos, type, data, identifier, isBaby, isGlobal)
    }

    @PowerNukkitDifference(info = "Default sound method changed to addSound", since = "1.4.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "Old method, use addSound(pos, Sound.<SOUND_VALUE>).", since = "1.4.0.0-PN")
    fun addLevelSoundEvent(pos: Vector3, type: Int) {
        this.addLevelSoundEvent(pos, type, -1)
    }

    /**
     * Broadcasts sound to players
     *
     * @param pos  position where sound should be played
     * @param type ID of the sound from [cn.nukkit.network.protocol.LevelSoundEventPacket]
     * @param data generic data that can affect sound
     */
    @PowerNukkitDifference(info = "Default sound method changed to addSound", since = "1.4.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "Old method, use addSound(pos, Sound.<SOUND_VALUE>).", since = "1.4.0.0-PN")
    fun addLevelSoundEvent(pos: Vector3, type: Int, data: Int) {
        this.addLevelSoundEvent(pos, type, data, ":", false, false)
    }

    @PowerNukkitDifference(info = "Default sound method changed to addSound", since = "1.4.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "Old method, use addSound(pos, Sound.<SOUND_VALUE>).", since = "1.4.0.0-PN")
    fun addLevelSoundEvent(pos: Vector3, type: Int, data: Int, identifier: String, isBaby: Boolean, isGlobal: Boolean) {
        val pk = LevelSoundEventPacket()
        pk.sound = type
        pk.extraData = data
        pk.entityIdentifier = identifier
        pk.x = pos.x
        pk.y = pos.y
        pk.z = pos.z
        pk.isGlobal = isGlobal
        pk.isBabyMob = isBaby
        addChunkPacket(pos.getFloorX() shr 4, pos.getFloorZ() shr 4, pk)
    }

    fun addParticle(particle: Particle?) {
        this.addParticle(particle, null as Array<Player?>?)
    }

    fun addParticle(particle: Particle?, player: Player) {
        this.addParticle(particle, arrayOf<Player>(player))
    }

    fun addParticle(particle: Particle, players: Array<Player?>?) {
        val packets: Array<DataPacket> = particle.encode()
        if (players == null) {
            if (packets != null) {
                for (packet in packets) {
                    addChunkPacket(particle.x as Int shr 4, particle.z as Int shr 4, packet)
                }
            }
        } else {
            if (packets != null) {
                if (packets.size == 1) {
                    Server.broadcastPacket(players, packets[0])
                } else {
                    server.batchPackets(players, packets, false)
                }
            }
        }
    }

    fun addParticle(particle: Particle?, players: Collection<Player?>) {
        this.addParticle(particle, players.toArray(Player.EMPTY_ARRAY))
    }

    fun addParticleEffect(pos: Vector3, particleEffect: ParticleEffect) {
        this.addParticleEffect(pos, particleEffect, -1, dimension, *(null as Array<Player?>?)!!)
    }

    fun addParticleEffect(pos: Vector3, particleEffect: ParticleEffect, uniqueEntityId: Long) {
        this.addParticleEffect(pos, particleEffect, uniqueEntityId, dimension, *(null as Array<Player?>?)!!)
    }

    fun addParticleEffect(pos: Vector3, particleEffect: ParticleEffect, uniqueEntityId: Long, dimensionId: Int) {
        this.addParticleEffect(pos, particleEffect, uniqueEntityId, dimensionId, *(null as Array<Player?>?)!!)
    }

    fun addParticleEffect(pos: Vector3?, particleEffect: ParticleEffect?, uniqueEntityId: Long, dimensionId: Int, players: Collection<Player?>) {
        this.addParticleEffect(pos, particleEffect, uniqueEntityId, dimensionId, players.toArray(Player.EMPTY_ARRAY))
    }

    fun addParticleEffect(pos: Vector3, particleEffect: ParticleEffect, uniqueEntityId: Long, dimensionId: Int, vararg players: Player?) {
        this.addParticleEffect(pos.asVector3f(), particleEffect.getIdentifier(), uniqueEntityId, dimensionId, players)
    }

    fun addParticleEffect(pos: Vector3f, identifier: String, uniqueEntityId: Long, dimensionId: Int, vararg players: Player?) {
        val pk = SpawnParticleEffectPacket()
        pk.identifier = identifier
        pk.uniqueEntityId = uniqueEntityId
        pk.dimensionId = dimensionId
        pk.position = pos
        if (players == null || players.size == 0) {
            addChunkPacket(pos.getFloorX() shr 4, pos.getFloorZ() shr 4, pk)
        } else {
            Server.broadcastPacket(players, pk)
        }
    }

    @JvmOverloads
    fun unload(force: Boolean = false): Boolean {
        val ev = LevelUnloadEvent(this)
        if (this === server.getDefaultLevel() && !force) {
            ev.setCancelled()
        }
        server.getPluginManager().callEvent(ev)
        if (!force && ev.isCancelled()) {
            return false
        }
        log.info(server.getLanguage().translateString("nukkit.level.unloading",
                TextFormat.GREEN + name + TextFormat.WHITE))
        val defaultLevel: Level = server.getDefaultLevel()
        for (player in ArrayList(getPlayers().values())) {
            if (this === defaultLevel || defaultLevel == null) {
                player.close(player.getLeaveMessage(), "Forced default level unload")
            } else {
                player.teleport(server.getDefaultLevel().getSafeSpawn())
            }
        }
        if (this === defaultLevel) {
            server.setDefaultLevel(null)
        }
        close()
        return true
    }

    fun getChunkPlayers(chunkX: Int, chunkZ: Int): Map<Integer, Player> {
        val index = chunkHash(chunkX, chunkZ)
        return if (playerLoaders.containsKey(index)) {
            HashMap(playerLoaders.get(index))
        } else {
            HashMap()
        }
    }

    fun getChunkLoaders(chunkX: Int, chunkZ: Int): Array<ChunkLoader?> {
        val index = chunkHash(chunkX, chunkZ)
        return if (chunkLoaders.containsKey(index)) {
            chunkLoaders.get(index).values().toArray(ChunkLoader.EMPTY_ARRAY)
        } else {
            ChunkLoader.EMPTY_ARRAY
        }
    }

    fun addChunkPacket(chunkX: Int, chunkZ: Int, packet: DataPacket?) {
        val index = chunkHash(chunkX, chunkZ)
        synchronized(chunkPackets) {
            val packets: Deque<DataPacket> = chunkPackets.computeIfAbsent(index) { i -> ArrayDeque() }
            packets.add(packet)
        }
    }

    fun registerChunkLoader(loader: ChunkLoader, chunkX: Int, chunkZ: Int) {
        this.registerChunkLoader(loader, chunkX, chunkZ, true)
    }

    fun registerChunkLoader(loader: ChunkLoader, chunkX: Int, chunkZ: Int, autoLoad: Boolean) {
        val hash: Int = loader.getLoaderId()
        val index = chunkHash(chunkX, chunkZ)
        if (!chunkLoaders.containsKey(index)) {
            chunkLoaders.put(index, HashMap())
            playerLoaders.put(index, HashMap())
        } else if (chunkLoaders.get(index).containsKey(hash)) {
            return
        }
        chunkLoaders.get(index).put(hash, loader)
        if (loader is Player) {
            playerLoaders.get(index).put(hash, loader as Player)
        }
        if (!loaders.containsKey(hash)) {
            loaderCounter.put(hash, 1)
            loaders.put(hash, loader)
        } else {
            loaderCounter.put(hash, loaderCounter.get(hash) + 1)
        }
        this.cancelUnloadChunkRequest(hash.toLong())
        if (autoLoad) {
            loadChunk(chunkX, chunkZ)
        }
    }

    fun unregisterChunkLoader(loader: ChunkLoader, chunkX: Int, chunkZ: Int) {
        val hash: Int = loader.getLoaderId()
        val index = chunkHash(chunkX, chunkZ)
        val chunkLoadersIndex: Map<Integer, ChunkLoader> = chunkLoaders.get(index)
        if (chunkLoadersIndex != null) {
            val oldLoader: ChunkLoader = chunkLoadersIndex.remove(hash)
            if (oldLoader != null) {
                if (chunkLoadersIndex.isEmpty()) {
                    chunkLoaders.remove(index)
                    playerLoaders.remove(index)
                    unloadChunkRequest(chunkX, chunkZ, true)
                } else {
                    val playerLoadersIndex: Map<Integer, Player> = playerLoaders.get(index)
                    playerLoadersIndex.remove(hash)
                }
                var count: Int = loaderCounter.get(hash)
                if (--count == 0) {
                    loaderCounter.remove(hash)
                    loaders.remove(hash)
                } else {
                    loaderCounter.put(hash, count)
                }
            }
        }
    }

    fun checkTime() {
        if (!stopTime && gameRules!!.getBoolean(GameRule.DO_DAYLIGHT_CYCLE)) {
            time += tickRate.toFloat()
        }
    }

    fun sendTime(vararg players: Player?) {
        val pk = SetTimePacket()
        pk.time = time.toInt()
        Server.broadcastPacket(players, pk)
    }

    fun sendTime() {
        sendTime(players.values().toArray(Player.EMPTY_ARRAY))
    }

    fun getGameRules(): GameRules? {
        return gameRules
    }

    fun doTick(currentTick: Int) {
        timings.doTick.startTiming()
        requireProvider()
        updateBlockLight(lightQueue)
        checkTime()
        if (currentTick % 1200 == 0) { // Send time to client every 60 seconds to make sure it stay in sync
            this.sendTime()
        }

        // Tick Weather
        if (dimension != DIMENSION_NETHER && dimension != DIMENSION_THE_END && gameRules!!.getBoolean(GameRule.DO_WEATHER_CYCLE)) {
            rainTime--
            if (rainTime <= 0) {
                if (!setRaining(!isRaining)) {
                    rainTime = if (isRaining) {
                        ThreadLocalRandom.current().nextInt(12000) + 12000
                    } else {
                        ThreadLocalRandom.current().nextInt(168000) + 12000
                    }
                }
            }
            thunderTime--
            if (thunderTime <= 0) {
                if (!setThundering(!thundering)) {
                    thunderTime = if (thundering) {
                        ThreadLocalRandom.current().nextInt(12000) + 3600
                    } else {
                        ThreadLocalRandom.current().nextInt(168000) + 12000
                    }
                }
            }
            if (isThundering()) {
                val chunks: Map<Long, FullChunk?> = chunks
                if (chunks is Long2ObjectOpenHashMap) {
                    val fastChunks: Long2ObjectOpenHashMap<out FullChunk?> = chunks as Long2ObjectOpenHashMap
                    val iter: ObjectIterator<out Long2ObjectMap.Entry<out FullChunk?>?> = fastChunks.long2ObjectEntrySet().fastIterator()
                    while (iter.hasNext()) {
                        val entry: Long2ObjectMap.Entry<out FullChunk?> = iter.next()
                        performThunder(entry.getLongKey(), entry.getValue())
                    }
                } else {
                    for (entry in chunks.entrySet()) {
                        performThunder(entry.getKey(), entry.getValue())
                    }
                }
            }
        }
        skyLightSubtracted = calculateSkylightSubtracted(1f).toFloat()
        this.currentTick++
        this.unloadChunks()
        timings.doTickPending.startTiming()
        val polled = 0
        updateQueue.tick(this.currentTick)
        timings.doTickPending.stopTiming()
        while (!normalUpdateQueue.isEmpty()) {
            val queuedUpdate: QueuedUpdate = normalUpdateQueue.poll()
            val block: Block = getBlock(queuedUpdate.block, queuedUpdate.block.layer)
            val event = BlockUpdateEvent(block)
            server.getPluginManager().callEvent(event)
            if (!event.isCancelled()) {
                block.onUpdate(BLOCK_UPDATE_NORMAL)
                if (queuedUpdate.neighbor != null) {
                    block.onNeighborChange(queuedUpdate.neighbor.getOpposite())
                }
            }
        }
        TimingsHistory.entityTicks += updateEntities.size()
        timings.entityTick.startTiming()
        if (!updateEntities.isEmpty()) {
            for (id in ArrayList(updateEntities.keySet())) {
                val entity: Entity = updateEntities.get(id)
                if (entity == null) {
                    updateEntities.remove(id)
                    continue
                }
                if (entity.closed || !entity.onUpdate(currentTick)) {
                    updateEntities.remove(id)
                }
            }
        }
        timings.entityTick.stopTiming()
        TimingsHistory.tileEntityTicks += updateBlockEntities.size()
        timings.blockEntityTick.startTiming()
        updateBlockEntities.removeIf { blockEntity -> !blockEntity.isValid() || !blockEntity.onUpdate() }
        timings.blockEntityTick.stopTiming()
        timings.tickChunks.startTiming()
        tickChunks()
        timings.tickChunks.stopTiming()
        synchronized(changedBlocks) {
            if (!changedBlocks.isEmpty()) {
                if (!players.isEmpty()) {
                    val iter: ObjectIterator<Long2ObjectMap.Entry<SoftReference<Map<Character, Object>>>> = changedBlocks.long2ObjectEntrySet().fastIterator()
                    while (iter.hasNext()) {
                        val entry: Long2ObjectMap.Entry<SoftReference<Map<Character, Object>>> = iter.next()
                        val index: Long = entry.getLongKey()
                        val blocks: Map<Character, Object> = entry.getValue().get()
                        val chunkX = getHashX(index)
                        val chunkZ = getHashZ(index)
                        if (blocks == null || blocks.size() > MAX_BLOCK_CACHE) {
                            val chunk: FullChunk = this.getChunk(chunkX, chunkZ)
                            for (p in getChunkPlayers(chunkX, chunkZ).values()) {
                                p.onChunkChanged(chunk)
                            }
                        } else {
                            val toSend: Collection<Player> = getChunkPlayers(chunkX, chunkZ).values()
                            val playerArray: Array<Player?> = toSend.toArray(Player.EMPTY_ARRAY)
                            val blocksArray: Array<Vector3?> = arrayOfNulls<Vector3>(blocks.size())
                            var i = 0
                            for (blockHash in blocks.keySet()) {
                                val hash: Vector3 = getBlockXYZ(index, blockHash)
                                blocksArray[i++] = hash
                            }
                            this.sendBlocks(playerArray, blocksArray, UpdateBlockPacket.FLAG_ALL)
                        }
                    }
                }
                changedBlocks.clear()
            }
        }
        processChunkRequest()
        if (sleepTicks > 0 && --sleepTicks <= 0) {
            checkSleep()
        }
        synchronized(chunkPackets) {
            for (index in chunkPackets.keySet()) {
                val chunkX = getHashX(index)
                val chunkZ = getHashZ(index)
                val chunkPlayers: Array<Player> = getChunkPlayers(chunkX, chunkZ).values().toArray(Player.EMPTY_ARRAY)
                if (chunkPlayers.size > 0) {
                    for (pk in chunkPackets.get(index)) {
                        Server.broadcastPacket(chunkPlayers, pk)
                    }
                }
            }
            chunkPackets.clear()
        }
        if (gameRules!!.isStale()) {
            val packet = GameRulesChangedPacket()
            packet.gameRules = gameRules
            Server.broadcastPacket(players.values().toArray(Player.EMPTY_ARRAY), packet)
            gameRules!!.refresh()
        }
        timings.doTick.stopTiming()
    }

    private fun performThunder(index: Long, chunk: FullChunk) {
        if (areNeighboringChunksLoaded(index)) return
        if (ThreadLocalRandom.current().nextInt(10000) === 0) {
            val LCG = updateLCG shr 2
            val chunkX: Int = chunk.getX() * 16
            val chunkZ: Int = chunk.getZ() * 16
            val vector: Vector3 = adjustPosToNearbyEntity(Vector3(chunkX + (LCG and 0xf), 0, chunkZ + (LCG shr 8 and 0xf)))
            val biome: Biome = Biome.getBiome(getBiomeId(vector.getFloorX(), vector.getFloorZ()))
            if (!biome.canRain()) {
                return
            }
            val bId = this.getBlockIdAt(vector.getFloorX(), vector.getFloorY(), vector.getFloorZ())
            if (bId != Block.TALL_GRASS && bId != Block.WATER) vector.y += 1
            val nbt: CompoundTag = CompoundTag()
                    .putList(ListTag<DoubleTag>("Pos").add(DoubleTag("", vector.x))
                            .add(DoubleTag("", vector.y)).add(DoubleTag("", vector.z)))
                    .putList(ListTag<DoubleTag>("Motion").add(DoubleTag("", 0))
                            .add(DoubleTag("", 0)).add(DoubleTag("", 0)))
                    .putList(ListTag<FloatTag>("Rotation").add(FloatTag("", 0))
                            .add(FloatTag("", 0)))
            val bolt = EntityLightning(chunk, nbt)
            val ev = LightningStrikeEvent(this, bolt)
            getServer().getPluginManager().callEvent(ev)
            if (!ev.isCancelled()) {
                bolt.spawnToAll()
            } else {
                bolt.setEffect(false)
            }
            this.addLevelSoundEvent(vector, LevelSoundEventPacket.SOUND_THUNDER, -1, EntityLightning.NETWORK_ID)
            this.addLevelSoundEvent(vector, LevelSoundEventPacket.SOUND_EXPLODE, -1, EntityLightning.NETWORK_ID)
        }
    }

    fun adjustPosToNearbyEntity(pos: Vector3): Vector3 {
        var pos: Vector3 = pos
        pos.y = getHighestBlockAt(pos.getFloorX(), pos.getFloorZ())
        val axisalignedbb: AxisAlignedBB = SimpleAxisAlignedBB(pos.x, pos.y, pos.z, pos.getX(), 255, pos.getZ()).expand(3, 3, 3)
        val list: List<Entity> = ArrayList()
        for (entity in this.getCollidingEntities(axisalignedbb)) {
            if (entity.isAlive() && canBlockSeeSky(entity)) {
                list.add(entity)
            }
        }
        return if (!list.isEmpty()) {
            list[ThreadLocalRandom.current().nextInt(list.size())].getPosition()
        } else {
            if (pos.getY() === -1) {
                pos = pos.up(2)
            }
            pos
        }
    }

    fun checkSleep() {
        if (players.isEmpty()) {
            return
        }
        var resetTime = true
        for (p in getPlayers().values()) {
            if (!p.isSleeping()) {
                resetTime = false
                break
            }
        }
        if (resetTime) {
            val time = getTime() % TIME_FULL
            if (time >= TIME_NIGHT && time < TIME_SUNRISE) {
                setTime(getTime() + TIME_FULL - time)
                for (p in getPlayers().values()) {
                    p.stopSleep()
                }
            }
        }
    }

    fun sendBlockExtraData(x: Int, y: Int, z: Int, id: Int, data: Int) {
        this.sendBlockExtraData(x, y, z, id, data, getChunkPlayers(x shr 4, z shr 4).values())
    }

    fun sendBlockExtraData(x: Int, y: Int, z: Int, id: Int, data: Int, players: Collection<Player?>) {
        sendBlockExtraData(x, y, z, id, data, players.toArray(Player.EMPTY_ARRAY))
    }

    fun sendBlockExtraData(x: Int, y: Int, z: Int, id: Int, data: Int, players: Array<Player?>?) {
        val pk = LevelEventPacket()
        pk.evid = LevelEventPacket.EVENT_SET_DATA
        pk.x = x + 0.5f
        pk.y = y + 0.5f
        pk.z = z + 0.5f
        pk.data = data shl 8 or id
        Server.broadcastPacket(players, pk)
    }

    fun sendBlocks(target: Array<Player?>?, blocks: Array<Vector3?>) {
        this.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_NONE, 0)
        this.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_NONE, 1)
    }

    fun sendBlocks(target: Array<Player?>?, blocks: Array<Vector3?>, flags: Int) {
        this.sendBlocks(target, blocks, flags, 0)
        this.sendBlocks(target, blocks, flags, 1)
    }

    fun sendBlocks(target: Array<Player?>?, blocks: Array<Vector3?>, flags: Int, optimizeRebuilds: Boolean) {
        this.sendBlocks(target, blocks, flags, 0, optimizeRebuilds)
        this.sendBlocks(target, blocks, flags, 1, optimizeRebuilds)
    }

    fun sendBlocks(target: Array<Player?>?, blocks: Array<Vector3?>, flags: Int, dataLayer: Int) {
        this.sendBlocks(target, blocks, flags, dataLayer, false)
    }

    fun sendBlocks(target: Array<Player?>?, blocks: Array<Vector3?>, flags: Int, dataLayer: Int, optimizeRebuilds: Boolean) {
        var size = 0
        for (block in blocks) {
            if (block != null) size++
        }
        var packetIndex = 0
        val packets: Array<UpdateBlockPacket?> = arrayOfNulls<UpdateBlockPacket>(size)
        var chunks: LongSet? = null
        if (optimizeRebuilds) {
            chunks = LongOpenHashSet()
        }
        for (b in blocks) {
            if (b == null) {
                continue
            }
            var first = !optimizeRebuilds
            if (optimizeRebuilds) {
                val index = chunkHash(b.x as Int shr 4, b.z as Int shr 4)
                if (!chunks.contains(index)) {
                    chunks.add(index)
                    first = true
                }
            }
            val updateBlockPacket = UpdateBlockPacket()
            updateBlockPacket.x = b.x
            updateBlockPacket.y = b.y
            updateBlockPacket.z = b.z
            updateBlockPacket.flags = if (first) flags else UpdateBlockPacket.FLAG_NONE
            updateBlockPacket.dataLayer = dataLayer
            var runtimeId: Int
            runtimeId = if (b is Block) {
                (b as Block).getRuntimeId()
            } else {
                getBlockRuntimeId(b.x as Int, b.y as Int, b.z as Int, dataLayer)
            }
            try {
                updateBlockPacket.blockRuntimeId = runtimeId
            } catch (e: NoSuchElementException) {
                throw IllegalStateException("Unable to create BlockUpdatePacket at (" +
                        b.x.toString() + ", " + b.y.toString() + ", " + b.z.toString() + ") in " + name, e)
            }
            packets[packetIndex++] = updateBlockPacket
        }
        server.batchPackets(target, packets)
    }

    private fun tickChunks() {
        if (chunksPerTicks <= 0 || loaders.isEmpty()) {
            chunkTickList.clear()
            return
        }
        val chunksPerLoader: Int = Math.min(200, Math.max(1, ((chunksPerTicks - loaders.size()) as Double / loaders.size() + 0.5) as Int))
        var randRange = 3 + chunksPerLoader / 30
        randRange = Math.min(randRange, chunkTickRadius)
        val random: ThreadLocalRandom = ThreadLocalRandom.current()
        if (!loaders.isEmpty()) {
            for (loader in loaders.values()) {
                val chunkX = loader.getX() as Int shr 4
                val chunkZ = loader.getZ() as Int shr 4
                val index = chunkHash(chunkX, chunkZ)
                val existingLoaders: Int = Math.max(0, chunkTickList.getOrDefault(index, 0))
                chunkTickList.put(index, existingLoaders + 1)
                for (chunk in 0 until chunksPerLoader) {
                    val dx: Int = random.nextInt(2 * randRange) - randRange
                    val dz: Int = random.nextInt(2 * randRange) - randRange
                    val hash = chunkHash(dx + chunkX, dz + chunkZ)
                    if (!chunkTickList.containsKey(hash) && requireProvider().isChunkLoaded(hash)) {
                        chunkTickList.put(hash, -1)
                    }
                }
            }
        }
        var blockTest = false
        if (!chunkTickList.isEmpty()) {
            val iter: ObjectIterator<Long2IntMap.Entry> = chunkTickList.long2IntEntrySet().iterator()
            while (iter.hasNext()) {
                val entry: Long2IntMap.Entry = iter.next()
                val index: Long = entry.getLongKey()
                if (!areNeighboringChunksLoaded(index)) {
                    iter.remove()
                    continue
                }
                val loaders: Int = entry.getIntValue()
                val chunkX = getHashX(index)
                val chunkZ = getHashZ(index)
                var chunk: FullChunk
                if (this.getChunk(chunkX, chunkZ, false).also { chunk = it } == null) {
                    iter.remove()
                    continue
                } else if (loaders <= 0) {
                    iter.remove()
                }
                for (entity in chunk.getEntities().values()) {
                    entity.scheduleUpdate()
                }
                val tickSpeed: Int = gameRules!!.getInteger(GameRule.RANDOM_TICK_SPEED)
                if (tickSpeed > 0) {
                    if (useSections) {
                        for (section in (chunk as Chunk).getSections()) {
                            if (section !is EmptyChunkSection) {
                                val Y: Int = section.getY()
                                for (i in 0 until tickSpeed) {
                                    val lcg = updateLCG
                                    val x = lcg and 0x0f
                                    val y = lcg ushr 8 and 0x0f
                                    val z = lcg ushr 16 and 0x0f
                                    val state: BlockState = section.getBlockState(x, y, z)
                                    if (randomTickBlocks[state.getBlockId()]) {
                                        val block: Block = state.getBlockRepairing(this, chunkX * 16 + x, (Y shl 4) + y, chunkZ * 16 + z)
                                        block.onUpdate(BLOCK_UPDATE_RANDOM)
                                    }
                                }
                            }
                        }
                    } else {
                        var Y = 0
                        while (Y < 8 && (Y < 3 || blockTest)) {
                            blockTest = false
                            for (i in 0 until tickSpeed) {
                                val lcg = updateLCG
                                val x = lcg and 0x0f
                                val y = lcg ushr 8 and 0x0f
                                val z = lcg ushr 16 and 0x0f
                                val state: BlockState = chunk.getBlockState(x, y + (Y shl 4), z)
                                blockTest = blockTest || !state.equals(BlockState.AIR)
                                if (randomTickBlocks[state.getBlockId()]) {
                                    val block: Block = state.getBlockRepairing(this, x, y + (Y shl 4), z)
                                    block.onUpdate(BLOCK_UPDATE_RANDOM)
                                }
                            }
                            ++Y
                        }
                    }
                }
            }
        }
        if (clearChunksOnTick) {
            chunkTickList.clear()
        }
    }

    @JvmOverloads
    fun save(force: Boolean = false): Boolean {
        if (!autoSave && !force) {
            return false
        }
        server.getPluginManager().callEvent(LevelSaveEvent(this))
        val levelProvider: LevelProvider = requireProvider()
        levelProvider.setTime(time.toInt())
        levelProvider.setRaining(isRaining)
        levelProvider.setRainTime(rainTime)
        levelProvider.setThundering(thundering)
        levelProvider.setThunderTime(thunderTime)
        levelProvider.setCurrentTick(currentTick)
        levelProvider.setGameRules(gameRules)
        saveChunks()
        if (levelProvider is BaseLevelProvider) {
            levelProvider.saveLevelData()
        }
        return true
    }

    fun saveChunks() {
        requireProvider().saveChunks()
    }

    @Deprecated
    @DeprecationDetails(reason = "Was moved to RedstoneComponent", since = "1.4.0.0-PN", replaceWith = "RedstoneComponent#updateAroundRedstone", by = "PowerNukkit")
    fun updateAroundRedstone(pos: Vector3, face: BlockFace?) {
        val loc = Location(pos.x, pos.y, pos.z, this)
        RedstoneComponent.updateAroundRedstone(loc, face)
    }

    fun updateComparatorOutputLevel(v: Vector3?) {
        updateComparatorOutputLevelSelective(v, true)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun updateComparatorOutputLevelSelective(v: Vector3?, observer: Boolean) {
        for (face in Plane.HORIZONTAL) {
            temporalVector.setComponentsAdding(v, face)
            if (isChunkLoaded(temporalVector.x as Int shr 4, temporalVector.z as Int shr 4)) {
                var block1: Block = this.getBlock(temporalVector)
                if (block1.getId() === BlockID.OBSERVER) {
                    if (observer) {
                        block1.onNeighborChange(face.getOpposite())
                    }
                } else if (BlockRedstoneDiode.isDiode(block1)) {
                    block1.onUpdate(BLOCK_UPDATE_REDSTONE)
                } else if (block1.isNormalBlock()) {
                    block1 = this.getBlock(temporalVector.setComponentsAdding(temporalVector, face))
                    if (BlockRedstoneDiode.isDiode(block1)) {
                        block1.onUpdate(BLOCK_UPDATE_REDSTONE)
                    }
                }
            }
        }
        if (!observer) {
            return
        }
        for (face in Plane.VERTICAL) {
            val block1: Block = this.getBlock(temporalVector.setComponentsAdding(v, face))
            if (block1.getId() === BlockID.OBSERVER) {
                block1.onNeighborChange(face.getOpposite())
            }
        }
    }

    fun updateAround(pos: Vector3?) {
        val block: Block = getBlock(pos)
        for (face in BlockFace.values()) {
            val side: Block = block.getSideAtLayer(0, face)
            normalUpdateQueue.add(QueuedUpdate(side, face))
            normalUpdateQueue.add(QueuedUpdate(side.getLevelBlockAtLayer(1), face))
        }
    }

    fun updateAround(x: Int, y: Int, z: Int) {
        updateAround(Vector3(x, y, z))
    }

    fun scheduleUpdate(pos: Block, delay: Int) {
        this.scheduleUpdate(pos, pos, delay, 0, true)
    }

    fun scheduleUpdate(block: Block, pos: Vector3, delay: Int) {
        this.scheduleUpdate(block, pos, delay, 0, true)
    }

    fun scheduleUpdate(block: Block, pos: Vector3, delay: Int, priority: Int) {
        this.scheduleUpdate(block, pos, delay, priority, true)
    }

    fun scheduleUpdate(block: Block, pos: Vector3, delay: Int, priority: Int, checkArea: Boolean) {
        if (block.getId() === 0 || checkArea && !isChunkLoaded(block.getFloorX() shr 4, block.getFloorZ() shr 4)) {
            return
        }
        val entry = BlockUpdateEntry(pos.floor(), block, delay.toLong() + currentTick, priority)
        if (!updateQueue.contains(entry)) {
            updateQueue.add(entry)
        }
    }

    fun cancelSheduledUpdate(pos: Vector3?, block: Block?): Boolean {
        return updateQueue.remove(BlockUpdateEntry(pos, block))
    }

    fun isUpdateScheduled(pos: Vector3?, block: Block?): Boolean {
        return updateQueue.contains(BlockUpdateEntry(pos, block))
    }

    fun isBlockTickPending(pos: Vector3?, block: Block?): Boolean {
        return updateQueue.isBlockTickPending(pos, block)
    }

    fun getPendingBlockUpdates(chunk: FullChunk): Set<BlockUpdateEntry> {
        val minX: Int = (chunk.getX() shl 4) - 2
        val maxX = minX + 16 + 2
        val minZ: Int = (chunk.getZ() shl 4) - 2
        val maxZ = minZ + 16 + 2
        return this.getPendingBlockUpdates(SimpleAxisAlignedBB(minX, 0, minZ, maxX, 256, maxZ))
    }

    fun getPendingBlockUpdates(boundingBox: AxisAlignedBB?): Set<BlockUpdateEntry> {
        return updateQueue.getPendingBlockUpdates(boundingBox)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun scanBlocks(@Nonnull bb: AxisAlignedBB, @Nonnull condition: BiPredicate<BlockVector3?, BlockState?>?): List<Block> {
        val min = BlockVector3(NukkitMath.floorDouble(bb.getMinX()), NukkitMath.floorDouble(bb.getMinY()), NukkitMath.floorDouble(bb.getMinZ()))
        val max = BlockVector3(NukkitMath.floorDouble(bb.getMaxX()), NukkitMath.floorDouble(bb.getMaxY()), NukkitMath.floorDouble(bb.getMaxZ()))
        val minChunk: ChunkVector2 = min.getChunkVector()
        val maxChunk: ChunkVector2 = max.getChunkVector()
        return IntStream.rangeClosed(minChunk.getX(), maxChunk.getX())
                .mapToObj { x -> IntStream.rangeClosed(minChunk.getZ(), maxChunk.getZ()).mapToObj { z -> ChunkVector2(x, z) } }
                .flatMap(Function.identity()).parallel()
                .map(this::getChunk).filter(Objects::nonNull)
                .flatMap { chunk -> chunk.scanBlocks(min, max, condition) }
                .collect(Collectors.toList())
    }

    fun getCollisionBlocks(bb: AxisAlignedBB): Array<Block> {
        return this.getCollisionBlocks(bb, false)
    }

    fun getCollisionBlocks(bb: AxisAlignedBB, targetFirst: Boolean): Array<Block> {
        return getCollisionBlocks(bb, targetFirst, false)
    }

    fun getCollisionBlocks(bb: AxisAlignedBB, targetFirst: Boolean, ignoreCollidesCheck: Boolean): Array<Block> {
        return getCollisionBlocks(bb, targetFirst, ignoreCollidesCheck, Predicate<Block> { block -> block.getId() !== 0 })
    }

    fun getCollisionBlocks(bb: AxisAlignedBB, targetFirst: Boolean, ignoreCollidesCheck: Boolean, condition: Predicate<Block?>): Array<Block> {
        val minX: Int = NukkitMath.floorDouble(bb.getMinX())
        val minY: Int = NukkitMath.floorDouble(bb.getMinY())
        val minZ: Int = NukkitMath.floorDouble(bb.getMinZ())
        val maxX: Int = NukkitMath.ceilDouble(bb.getMaxX())
        val maxY: Int = NukkitMath.ceilDouble(bb.getMaxY())
        val maxZ: Int = NukkitMath.ceilDouble(bb.getMaxZ())
        val collides: List<Block> = ArrayList()
        if (targetFirst) {
            for (z in minZ..maxZ) {
                for (x in minX..maxX) {
                    for (y in minY..maxY) {
                        val block: Block = this.getBlock(temporalVector.setComponents(x, y, z), false)
                        if (block != null && condition.test(block) && (ignoreCollidesCheck || block.collidesWithBB(bb))) {
                            return arrayOf<Block>(block)
                        }
                    }
                }
            }
        } else {
            for (z in minZ..maxZ) {
                for (x in minX..maxX) {
                    for (y in minY..maxY) {
                        val block: Block = this.getBlock(temporalVector.setComponents(x, y, z), false)
                        if (block != null && condition.test(block) && (ignoreCollidesCheck || block.collidesWithBB(bb))) {
                            collides.add(block)
                        }
                    }
                }
            }
        }
        return collides.toArray(Block.EMPTY_ARRAY)
    }

    fun isFullBlock(pos: Vector3): Boolean {
        val bb: AxisAlignedBB
        bb = if (pos is Block) {
            if ((pos as Block).isSolid()) {
                return true
            }
            (pos as Block).getBoundingBox()
        } else {
            this.getBlock(pos).getBoundingBox()
        }
        return bb != null && bb.getAverageEdgeLength() >= 1
    }

    fun getCollisionCubes(entity: Entity?, bb: AxisAlignedBB): Array<AxisAlignedBB> {
        return this.getCollisionCubes(entity, bb, true)
    }

    fun getCollisionCubes(entity: Entity?, bb: AxisAlignedBB, entities: Boolean): Array<AxisAlignedBB> {
        return getCollisionCubes(entity, bb, entities, false)
    }

    fun getCollisionCubes(entity: Entity?, bb: AxisAlignedBB, entities: Boolean, solidEntities: Boolean): Array<AxisAlignedBB> {
        val minX: Int = NukkitMath.floorDouble(bb.getMinX())
        val minY: Int = NukkitMath.floorDouble(bb.getMinY())
        val minZ: Int = NukkitMath.floorDouble(bb.getMinZ())
        val maxX: Int = NukkitMath.ceilDouble(bb.getMaxX())
        val maxY: Int = NukkitMath.ceilDouble(bb.getMaxY())
        val maxZ: Int = NukkitMath.ceilDouble(bb.getMaxZ())
        val collides: List<AxisAlignedBB> = ArrayList()
        for (z in minZ..maxZ) {
            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    val block: Block = this.getBlock(temporalVector.setComponents(x, y, z), false)
                    if (!block.canPassThrough() && block.collidesWithBB(bb)) {
                        collides.add(block.getBoundingBox())
                    }
                }
            }
        }
        if (entities || solidEntities) {
            for (ent in this.getCollidingEntities(bb.grow(0.25f, 0.25f, 0.25f), entity)) {
                if (solidEntities && !ent.canPassThrough()) {
                    collides.add(ent.boundingBox.clone())
                }
            }
        }
        return collides.toArray(AxisAlignedBB.EMPTY_ARRAY)
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Rounds the AABB to have precision 4 before checking for collision, fix PowerNukkit#506")
    fun hasCollision(entity: Entity?, bb: AxisAlignedBB, entities: Boolean): Boolean {
        val minX: Int = NukkitMath.floorDouble(NukkitMath.round(bb.getMinX(), 4))
        val minY: Int = NukkitMath.floorDouble(NukkitMath.round(bb.getMinY(), 4))
        val minZ: Int = NukkitMath.floorDouble(NukkitMath.round(bb.getMinZ(), 4))
        val maxX: Int = NukkitMath.ceilDouble(NukkitMath.round(bb.getMaxX(), 4) - 0.00001)
        val maxY: Int = NukkitMath.ceilDouble(NukkitMath.round(bb.getMaxY(), 4) - 0.00001)
        val maxZ: Int = NukkitMath.ceilDouble(NukkitMath.round(bb.getMaxZ(), 4) - 0.00001)
        for (z in minZ..maxZ) {
            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    val block: Block = this.getBlock(temporalVector.setComponents(x, y, z))
                    if (!block.canPassThrough() && block.collidesWithBB(bb)) {
                        return true
                    }
                }
            }
        }
        return if (entities) {
            this.getCollidingEntities(bb.grow(0.25f, 0.25f, 0.25f), entity).size > 0
        } else false
    }

    fun getFullLight(pos: Vector3): Int {
        val chunk: FullChunk = this.getChunk(pos.x as Int shr 4, pos.z as Int shr 4, false)
        var level = 0
        if (chunk != null) {
            level = chunk.getBlockSkyLight(pos.x as Int and 0x0f, pos.y as Int and 0xff, pos.z as Int and 0x0f)
            level -= skyLightSubtracted.toInt()
            if (level < 15) {
                level = Math.max(chunk.getBlockLight(pos.x as Int and 0x0f, pos.y as Int and 0xff, pos.z as Int and 0x0f),
                        level)
            }
        }
        return level
    }

    fun calculateSkylightSubtracted(tickDiff: Float): Int {
        val angle = getCelestialAngle(tickDiff)
        var light: Float = 1.0f - (MathHelper.cos(angle * (Math.PI as Float * 2f)) * 2.0f + 0.5f)
        light = MathHelper.clamp(light, 0.0f, 1.0f)
        light = 1.0f - light
        light = (light.toDouble() * (1.0 - (getRainStrength(tickDiff) * 5.0f).toDouble() / 16.0)).toFloat()
        light = (light.toDouble() * (1.0 - (getThunderStrength(tickDiff) * 5.0f).toDouble() / 16.0)).toFloat()
        light = 1.0f - light
        return (light * 11.0f).toInt()
    }

    fun getRainStrength(tickDiff: Float): Float {
        return if (isRaining) 1 else 0 // TODO: real implementation
    }

    fun getThunderStrength(tickDiff: Float): Float {
        return if (isThundering()) 1 else 0 // TODO: real implementation
    }

    fun getCelestialAngle(tickDiff: Float): Float {
        return calculateCelestialAngle(getTime(), tickDiff)
    }

    fun calculateCelestialAngle(time: Int, tickDiff: Float): Float {
        val i = (time % 24000L).toInt()
        var angle = (i.toFloat() + tickDiff) / 24000.0f - 0.25f
        if (angle < 0.0f) {
            ++angle
        }
        if (angle > 1.0f) {
            --angle
        }
        val f1 = 1.0f - ((Math.cos(angle.toDouble() * Math.PI) + 1.0) / 2.0) as Float
        angle = angle + (f1 - angle) / 3.0f
        return angle
    }

    fun getMoonPhase(worldTime: Long): Int {
        return (worldTime / 24000 % 8 + 8).toInt() % 8
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    fun getFullBlock(x: Int, y: Int, z: Int): Int {
        return getFullBlock(x, y, z, 0)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    fun getFullBlock(x: Int, y: Int, z: Int, layer: Int): Int {
        return this.getChunk(x shr 4, z shr 4, false).getFullBlock(x and 0x0f, y and 0xff, z and 0x0f, layer)
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    fun getBlockRuntimeId(x: Int, y: Int, z: Int): Int {
        return getBlockRuntimeId(x, y, z, 0)
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    fun getBlockRuntimeId(x: Int, y: Int, z: Int, layer: Int): Int {
        return this.getChunk(x shr 4, z shr 4, false).getBlockRuntimeId(x and 0x0f, y and 0xff, z and 0x0f, layer)
    }

    @Synchronized
    fun getBlock(pos: Vector3?): Block {
        return getBlock(pos, 0)
    }

    @Synchronized
    fun getBlock(pos: Vector3?, layer: Int): Block {
        return this.getBlock(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), layer)
    }

    @Synchronized
    fun getBlock(pos: Vector3, load: Boolean): Block {
        return getBlock(pos, 0, load)
    }

    @Synchronized
    fun getBlock(pos: Vector3, layer: Int, load: Boolean): Block {
        return this.getBlock(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), layer, load)
    }

    @Synchronized
    fun getBlock(x: Int, y: Int, z: Int): Block {
        return getBlock(x, y, z, 0)
    }

    @Synchronized
    fun getBlock(x: Int, y: Int, z: Int, layer: Int): Block {
        return getBlock(x, y, z, layer, true)
    }

    @Synchronized
    fun getBlock(x: Int, y: Int, z: Int, load: Boolean): Block {
        return getBlock(x, y, z, 0, load)
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will automatically repair broken block states")
    @Synchronized
    fun getBlock(x: Int, y: Int, z: Int, layer: Int, load: Boolean): Block {
        val fullState: BlockState
        if (y >= 0 && y < 256) {
            val cx = x shr 4
            val cz = z shr 4
            val chunk: BaseFullChunk
            chunk = if (load) {
                getChunk(cx, cz)
            } else {
                getChunkIfLoaded(cx, cz)
            }
            fullState = if (chunk != null) {
                chunk.getBlockState(x and 0xF, y, z and 0xF, layer)
            } else {
                BlockState.AIR
            }
        } else {
            fullState = BlockState.AIR
        }
        val valid: OptionalBoolean = fullState.getCachedValidation()
        if (valid === OptionalBoolean.TRUE) {
            return fullState.getBlock(this, x, y, z, layer)
        }
        if (valid === OptionalBoolean.EMPTY) {
            try {
                return fullState.getBlock(this, x, y, z, layer)
            } catch (ignored: InvalidBlockStateException) {
            }
        }
        val block: Block = fullState.getBlockRepairing(this, x, y, z, layer)
        setBlock(x, y, z, layer, block, false, false) // Update set to false to fix PowerNukkit#650 
        return block
    }

    fun updateAllLight(pos: Vector3) {
        updateBlockSkyLight(pos.x as Int, pos.y as Int, pos.z as Int)
        addLightUpdate(pos.x as Int, pos.y as Int, pos.z as Int)
    }

    fun updateBlockSkyLight(x: Int, y: Int, z: Int) {
        val chunk: BaseFullChunk = getChunkIfLoaded(x shr 4, z shr 4) ?: return
        val oldHeightMap: Int = chunk.getHeightMap(x and 0xf, z and 0xf)
        val sourceId = getBlockIdAt(x, y, z)
        val yPlusOne = y + 1
        val newHeightMap: Int
        newHeightMap = if (yPlusOne == oldHeightMap) { // Block changed directly beneath the heightmap. Check if a block was removed or changed to a different light-filter
            chunk.recalculateHeightMapColumn(x and 0x0f, z and 0x0f)
        } else if (yPlusOne > oldHeightMap) { // Block changed above the heightmap
            if (Block.lightFilter.get(sourceId) > 1 || Block.diffusesSkyLight.get(sourceId)) {
                chunk.setHeightMap(x and 0xf, y and 0xf, yPlusOne)
                yPlusOne
            } else { // Block changed which has no effect on direct sky light, for example placing or removing glass.
                return
            }
        } else { // Block changed below heightmap
            oldHeightMap
        }
        if (newHeightMap > oldHeightMap) { // Heightmap increase, block placed, remove sky light
            for (i in y downTo oldHeightMap) {
                setBlockSkyLightAt(x, i, z, 0)
            }
        } else if (newHeightMap < oldHeightMap) { // Heightmap decrease, block changed or removed, add sky light
            for (i in y downTo newHeightMap) {
                setBlockSkyLightAt(x, i, z, 15)
            }
        } else { // No heightmap change, block changed "underground"
            setBlockSkyLightAt(x, y, z, Math.max(0, getHighestAdjacentBlockSkyLight(x, y, z) - Block.lightFilter.get(sourceId)))
        }
    }

    /**
     * Returns the highest block skylight level available in the positions adjacent to the specified block coordinates.
     */
    fun getHighestAdjacentBlockSkyLight(x: Int, y: Int, z: Int): Int {
        val lightLevels = intArrayOf(
                getBlockSkyLightAt(x + 1, y, z),
                getBlockSkyLightAt(x - 1, y, z),
                getBlockSkyLightAt(x, y + 1, z),
                getBlockSkyLightAt(x, y - 1, z),
                getBlockSkyLightAt(x, y, z + 1),
                getBlockSkyLightAt(x, y, z - 1))
        var maxValue = lightLevels[0]
        for (i in 1 until lightLevels.size) {
            if (lightLevels[i] > maxValue) {
                maxValue = lightLevels[i]
            }
        }
        return maxValue
    }

    fun updateBlockLight(map: Map<Long?, Map<Character?, Object?>?>) {
        var size: Int = map.size()
        if (size == 0) {
            return
        }
        val lightPropagationQueue: Queue<Long> = ConcurrentLinkedQueue()
        val lightRemovalQueue: Queue<Array<Object>> = ConcurrentLinkedQueue()
        val visited: Long2ObjectOpenHashMap<Object> = Long2ObjectOpenHashMap()
        val removalVisited: Long2ObjectOpenHashMap<Object> = Long2ObjectOpenHashMap()
        val iter: Iterator<Map.Entry<Long, Map<Character, Object>>> = map.entrySet().iterator()
        while (iter.hasNext() && size-- > 0) {
            val entry: Map.Entry<Long, Map<Character, Object>> = iter.next()
            iter.remove()
            val index: Long = entry.getKey()
            val blocks: Map<Character, Object> = entry.getValue()
            val chunkX = getHashX(index)
            val chunkZ = getHashZ(index)
            val bx = chunkX shl 4
            val bz = chunkZ shl 4
            for (blockHash in blocks.keySet()) {
                val hi: Int = (blockHash.toInt() ushr 8) as Byte.toInt()
                val lo: Int = blockHash as Byte.toInt()
                val y = lo and 0xFF
                val x = (hi and 0xF) + bx
                val z = (hi shr 4 and 0xF) + bz
                val chunk: BaseFullChunk = getChunk(x shr 4, z shr 4, false)
                if (chunk != null) {
                    val lcx = x and 0xF
                    val lcz = z and 0xF
                    val oldLevel: Int = chunk.getBlockLight(lcx, y, lcz)
                    val newLevel: Int = chunk.getBlockState(lcx, y, lcz).getBlock(this, x, y, z, 0, true).getLightLevel()
                    if (oldLevel != newLevel) {
                        setBlockLightAt(x, y, z, newLevel)
                        if (newLevel < oldLevel) {
                            removalVisited.put(Hash.hashBlock(x, y, z), changeBlocksPresent)
                            lightRemovalQueue.add(arrayOf(Hash.hashBlock(x, y, z), oldLevel))
                        } else {
                            visited.put(Hash.hashBlock(x, y, z), changeBlocksPresent)
                            lightPropagationQueue.add(Hash.hashBlock(x, y, z))
                        }
                    }
                }
            }
        }
        while (!lightRemovalQueue.isEmpty()) {
            val `val`: Array<Object> = lightRemovalQueue.poll()
            val node = `val`[0] as Long
            val x: Int = Hash.hashBlockX(node)
            val y: Int = Hash.hashBlockY(node)
            val z: Int = Hash.hashBlockZ(node)
            val lightLevel = `val`[1] as Int
            computeRemoveBlockLight(x - 1, y, z, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited)
            computeRemoveBlockLight(x + 1, y, z, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited)
            computeRemoveBlockLight(x, y - 1, z, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited)
            computeRemoveBlockLight(x, y + 1, z, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited)
            computeRemoveBlockLight(x, y, z - 1, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited)
            computeRemoveBlockLight(x, y, z + 1, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited)
        }
        while (!lightPropagationQueue.isEmpty()) {
            val node: Long = lightPropagationQueue.poll()
            val x: Int = Hash.hashBlockX(node)
            val y: Int = Hash.hashBlockY(node)
            val z: Int = Hash.hashBlockZ(node)
            val lightLevel: Int = (getBlockLightAt(x, y, z)
                    - Block.lightFilter.get(this.getBlockIdAt(x, y, z)))
            if (lightLevel >= 1) {
                computeSpreadBlockLight(x - 1, y, z, lightLevel, lightPropagationQueue, visited)
                computeSpreadBlockLight(x + 1, y, z, lightLevel, lightPropagationQueue, visited)
                computeSpreadBlockLight(x, y - 1, z, lightLevel, lightPropagationQueue, visited)
                computeSpreadBlockLight(x, y + 1, z, lightLevel, lightPropagationQueue, visited)
                computeSpreadBlockLight(x, y, z - 1, lightLevel, lightPropagationQueue, visited)
                computeSpreadBlockLight(x, y, z + 1, lightLevel, lightPropagationQueue, visited)
            }
        }
    }

    private fun computeRemoveBlockLight(x: Int, y: Int, z: Int, currentLight: Int, queue: Queue<Array<Object>>,
                                        spreadQueue: Queue<Long>, visited: Map<Long, Object>, spreadVisited: Map<Long, Object>) {
        val current = getBlockLightAt(x, y, z)
        val index: Long = Hash.hashBlock(x, y, z)
        if (current != 0 && current < currentLight) {
            setBlockLightAt(x, y, z, 0)
            if (current > 1) {
                if (!visited.containsKey(index)) {
                    visited.put(index, changeBlocksPresent)
                    queue.add(arrayOf(Hash.hashBlock(x, y, z), current))
                }
            }
        } else if (current >= currentLight) {
            if (!spreadVisited.containsKey(index)) {
                spreadVisited.put(index, changeBlocksPresent)
                spreadQueue.add(Hash.hashBlock(x, y, z))
            }
        }
    }

    private fun computeSpreadBlockLight(x: Int, y: Int, z: Int, currentLight: Int, queue: Queue<Long>,
                                        visited: Map<Long, Object>) {
        val current = getBlockLightAt(x, y, z)
        val index: Long = Hash.hashBlock(x, y, z)
        if (current < currentLight - 1) {
            setBlockLightAt(x, y, z, currentLight)
            if (!visited.containsKey(index)) {
                visited.put(index, changeBlocksPresent)
                if (currentLight > 1) {
                    queue.add(Hash.hashBlock(x, y, z))
                }
            }
        }
    }

    private val lightQueue: Map<Long?, Map<Character?, Object>> = ConcurrentHashMap(8, 0.9f, 1)
    fun addLightUpdate(x: Int, y: Int, z: Int) {
        val index = chunkHash(x shr 4, z shr 4)
        var currentMap: Map<Character?, Object?>? = lightQueue[index]
        if (currentMap == null) {
            currentMap = ConcurrentHashMap(8, 0.9f, 1)
            lightQueue.put(index, currentMap)
        }
        currentMap.put(localBlockHash(x.toDouble(), y.toDouble(), z.toDouble()), changeBlocksPresent)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    @Synchronized
    override fun setBlockFullIdAt(x: Int, y: Int, z: Int, fullId: Int) {
        setBlockFullIdAt(x, y, z, 0, fullId)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    @Synchronized
    override fun setBlockFullIdAt(x: Int, y: Int, z: Int, layer: Int, fullId: Int) {
        setBlock(x, y, z, layer, Block.fullList.get(fullId), false, false)
    }

    @Synchronized
    fun setBlock(pos: Vector3?, block: Block?): Boolean {
        return setBlock(pos, 0, block)
    }

    @Synchronized
    fun setBlock(pos: Vector3?, layer: Int, block: Block?): Boolean {
        return this.setBlock(pos, layer, block, false)
    }

    @Synchronized
    fun setBlock(pos: Vector3?, block: Block?, direct: Boolean): Boolean {
        return this.setBlock(pos, 0, block, direct)
    }

    @Synchronized
    fun setBlock(pos: Vector3, layer: Int, block: Block, direct: Boolean): Boolean {
        return this.setBlock(pos, layer, block, direct, true)
    }

    @Synchronized
    fun setBlock(pos: Vector3, block: Block, direct: Boolean, update: Boolean): Boolean {
        return setBlock(pos, 0, block, direct, update)
    }

    @Synchronized
    fun setBlock(pos: Vector3, layer: Int, block: Block, direct: Boolean, update: Boolean): Boolean {
        return setBlock(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), layer, block, direct, update)
    }

    @Synchronized
    fun setBlock(x: Int, y: Int, z: Int, block: Block, direct: Boolean, update: Boolean): Boolean {
        return setBlock(x, y, z, 0, block, direct, update)
    }

    @Synchronized
    fun setBlock(x: Int, y: Int, z: Int, layer: Int, block: Block, direct: Boolean, update: Boolean): Boolean {
        var block: Block = block
        if (y < 0 || y >= 256 || layer < 0 || layer > requireProvider().getMaximumLayer()) {
            return false
        }
        val state: BlockState = block.getCurrentState()
        val chunk: BaseFullChunk = this.getChunk(x shr 4, z shr 4, true)
        val statePrevious: BlockState = chunk.getAndSetBlockState(x and 0xF, y, z and 0xF, layer, state)
        if (state.equals(statePrevious)) {
            return false
        }
        block.x = x
        block.y = y
        block.z = z
        block.level = this
        block.layer = layer
        val blockPrevious: Block = statePrevious.getBlockRepairing(this, x, y, z, layer)
        val cx = x shr 4
        val cz = z shr 4
        val index = chunkHash(cx, cz)
        if (direct) {
            this.sendBlocks(getChunkPlayers(cx, cz).values().toArray(Player.EMPTY_ARRAY), arrayOf<Block>(block), UpdateBlockPacket.FLAG_ALL_PRIORITY, block.layer)
            //this.sendBlocks(this.getChunkPlayers(cx, cz).values().toArray(new Player[0]), new Block[]{block.getLevelBlockAtLayer(0)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0);
            //this.sendBlocks(this.getChunkPlayers(cx, cz).values().toArray(new Player[0]), new Block[]{block.getLevelBlockAtLayer(1)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
        } else {
            addBlockChange(index, x, y, z)
        }
        for (loader in getChunkLoaders(cx, cz)) {
            loader!!.onBlockChanged(block)
        }
        if (update) {
            updateAllLight(block)

            /*if (blockPrevious.isTransparent() != block.isTransparent() || blockPrevious.getLightLevel() != block.getLightLevel()) {
                addLightUpdate(x, y, z);
            }*/
            val ev = BlockUpdateEvent(block)
            server.getPluginManager().callEvent(ev)
            if (!ev.isCancelled()) {
                for (entity in this.getNearbyEntities(SimpleAxisAlignedBB(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1))) {
                    entity.scheduleUpdate()
                }
                block = ev.getBlock()
                block.onUpdate(BLOCK_UPDATE_NORMAL)
                block.getLevelBlockAtLayer(if (layer == 0) 1 else 0).onUpdate(BLOCK_UPDATE_NORMAL)
                this.updateAround(x, y, z)
                if (block.hasComparatorInputOverride()) {
                    updateComparatorOutputLevel(block)
                }
            }
        }
        blockPrevious.afterRemoval(block, update)
        return true
    }

    private fun addBlockChange(x: Int, y: Int, z: Int) {
        val index = chunkHash(x shr 4, z shr 4)
        addBlockChange(index, x, y, z)
    }

    private fun addBlockChange(index: Long, x: Int, y: Int, z: Int) {
        synchronized(changedBlocks) {
            val current: SoftReference<Map<Character, Object>> = changedBlocks.computeIfAbsent(index) { k -> SoftReference(HashMap()) }
            val currentMap: Map<Character, Object> = current.get()
            if (currentMap !== changeBlocksFullMap && currentMap != null) {
                if (currentMap.size() > MAX_BLOCK_CACHE) {
                    changedBlocks.put(index, SoftReference(changeBlocksFullMap))
                } else {
                    currentMap.put(localBlockHash(x.toDouble(), y.toDouble(), z.toDouble()), changeBlocksPresent)
                }
            }
        }
    }

    fun dropItem(source: Vector3, item: Item) {
        this.dropItem(source, item, null)
    }

    fun dropItem(source: Vector3, item: Item, motion: Vector3?) {
        this.dropItem(source, item, motion, 10)
    }

    fun dropItem(source: Vector3, item: Item, motion: Vector3?, delay: Int) {
        this.dropItem(source, item, motion, false, delay)
    }

    fun dropItem(source: Vector3, item: Item, motion: Vector3?, dropAround: Boolean, delay: Int) {
        var motion: Vector3? = motion
        if (motion == null) {
            if (dropAround) {
                val f: Float = ThreadLocalRandom.current().nextFloat() * 0.5f
                val f1: Float = ThreadLocalRandom.current().nextFloat() * (Math.PI as Float * 2)
                motion = Vector3(-MathHelper.sin(f1) * f, 0.20000000298023224, MathHelper.cos(f1) * f)
            } else {
                motion = Vector3(Random().nextDouble() * 0.2 - 0.1, 0.2,
                        Random().nextDouble() * 0.2 - 0.1)
            }
        }
        if (item.getId() !== 0 && item.getCount() > 0) {
            val itemEntity: EntityItem = Entity.createEntity("Item",
                    this.getChunk(source.getX() as Int shr 4, source.getZ() as Int shr 4, true),
                    Entity.getDefaultNBT(source, motion, Random().nextFloat() * 360, 0)
                            .putShort("Health", 5)
                            .putCompound("Item", NBTIO.putItemHelper(item))
                            .putShort("PickupDelay", delay)) as EntityItem
            if (itemEntity != null) {
                itemEntity.spawnToAll()
            }
        }
    }

    @Since("1.4.0.0-PN")
    @Nullable
    fun dropAndGetItem(@Nonnull source: Vector3, @Nonnull item: Item): EntityItem? {
        return this.dropAndGetItem(source, item, null)
    }

    @Since("1.4.0.0-PN")
    @Nullable
    fun dropAndGetItem(@Nonnull source: Vector3, @Nonnull item: Item, @Nullable motion: Vector3?): EntityItem? {
        return this.dropAndGetItem(source, item, motion, 10)
    }

    @Since("1.4.0.0-PN")
    @Nullable
    fun dropAndGetItem(@Nonnull source: Vector3, @Nonnull item: Item, @Nullable motion: Vector3?, delay: Int): EntityItem? {
        return this.dropAndGetItem(source, item, motion, false, delay)
    }

    @Since("1.4.0.0-PN")
    @Nullable
    fun dropAndGetItem(@Nonnull source: Vector3, @Nonnull item: Item, @Nullable motion: Vector3?, dropAround: Boolean, delay: Int): EntityItem? {
        var motion: Vector3? = motion
        var itemEntity: EntityItem? = null
        if (motion == null) {
            if (dropAround) {
                val f: Float = ThreadLocalRandom.current().nextFloat() * 0.5f
                val f1: Float = ThreadLocalRandom.current().nextFloat() * (Math.PI as Float * 2)
                motion = Vector3(-MathHelper.sin(f1) * f, 0.20000000298023224, MathHelper.cos(f1) * f)
            } else {
                motion = Vector3(Random().nextDouble() * 0.2 - 0.1, 0.2,
                        Random().nextDouble() * 0.2 - 0.1)
            }
        }
        val itemTag: CompoundTag = NBTIO.putItemHelper(item)
        itemTag.setName("Item")
        if (item.getId() !== 0 && item.getCount() > 0) {
            itemEntity = Entity.createEntity("Item",
                    this.getChunk(source.getX() as Int shr 4, source.getZ() as Int shr 4, true),
                    CompoundTag().putList(ListTag<DoubleTag>("Pos").add(DoubleTag("", source.getX()))
                            .add(DoubleTag("", source.getY())).add(DoubleTag("", source.getZ())))
                            .putList(ListTag<DoubleTag>("Motion").add(DoubleTag("", motion.x))
                                    .add(DoubleTag("", motion.y)).add(DoubleTag("", motion.z)))
                            .putList(ListTag<FloatTag>("Rotation")
                                    .add(FloatTag("", ThreadLocalRandom.current().nextFloat() * 360))
                                    .add(FloatTag("", 0)))
                            .putShort("Health", 5).putCompound("Item", itemTag).putShort("PickupDelay", delay)) as EntityItem
            if (itemEntity != null) {
                itemEntity.spawnToAll()
            }
        }
        return itemEntity
    }

    fun useBreakOn(vector: Vector3): Item? {
        return this.useBreakOn(vector, null)
    }

    fun useBreakOn(vector: Vector3, item: Item?): Item? {
        return this.useBreakOn(vector, item, null)
    }

    fun useBreakOn(vector: Vector3, item: Item?, player: Player?): Item? {
        return this.useBreakOn(vector, item, player, false)
    }

    fun useBreakOn(vector: Vector3, item: Item?, player: Player?, createParticles: Boolean): Item? {
        return useBreakOn(vector, null, item, player, createParticles)
    }

    fun useBreakOn(vector: Vector3, face: BlockFace?, item: Item?, player: Player?, createParticles: Boolean): Item? {
        return useBreakOn(vector, face, item, player, createParticles, false)
    }

    fun useBreakOn(vector: Vector3, face: BlockFace?, item: Item?, player: Player?, createParticles: Boolean, setBlockDestroy: Boolean): Item? {
        return if (vector is Block) {
            useBreakOn(vector, (vector as Block).layer, face, item, player, createParticles, setBlockDestroy)
        } else {
            useBreakOn(vector, 0, face, item, player, createParticles, setBlockDestroy)
        }
    }

    fun useBreakOn(vector: Vector3, layer: Int, face: BlockFace?, item: Item?, player: Player?, createParticles: Boolean, setBlockDestroy: Boolean): Item? {
        var item: Item? = item
        if (player != null && player.getGamemode() > 2) {
            return null
        }
        val target: Block = this.getBlock(vector, layer)
        if (player != null && !target.isBlockChangeAllowed(player)) {
            return null
        }
        val drops: Array<Item>
        var dropExp: Int = target.getDropExp()
        if (item == null) {
            item = ItemBlock(Block.get(BlockID.AIR), 0, 0)
        }
        if (!target.isBreakable(vector, layer, face, item, player, setBlockDestroy)) {
            return null
        }
        val mustDrop: Boolean = target.mustDrop(vector, layer, face, item, player)
        val mustSilkTouch: Boolean = target.mustSilkTouch(vector, layer, face, item, player)
        val isSilkTouch = mustSilkTouch || item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null
        if (player != null) {
            if (player.getGamemode() === 2) {
                val tag: Tag = item.getNamedTagEntry("CanDestroy")
                var canBreak = false
                if (tag is ListTag) {
                    for (v in (tag as ListTag<out Tag?>).getAll()) {
                        if (v is StringTag) {
                            val entry: Item = Item.fromString((v as StringTag).data)
                            if (entry.getId() > 0 && entry.getBlock() != null && entry.getBlock().getId() === target.getId()) {
                                canBreak = true
                                break
                            }
                        }
                    }
                }
                if (!canBreak) {
                    return null
                }
            }
            var breakTime: Double = target.calculateBreakTime(item, player)
            // this in
            // block
            // class
            if ((setBlockDestroy || player.isCreative()) && breakTime > 0.15) {
                breakTime = 0.15
            }
            breakTime -= 0.15
            val eventDrops: Array<Item>
            if (!mustDrop && !setBlockDestroy && !player.isSurvival()) {
                eventDrops = Item.EMPTY_ARRAY
            } else if (mustSilkTouch || isSilkTouch && target.canSilkTouch()) {
                eventDrops = arrayOf<Item>(target.toItem())
            } else {
                eventDrops = target.getDrops(item)
            }
            if (!setBlockDestroy) {
                val fastBreak: Boolean = Long.sum(player.lastBreak, breakTime.toLong() * 1000) > Long.sum(System.currentTimeMillis(), 1000.toLong())
                val ev = BlockBreakEvent(player, target, face, item, eventDrops, player.isCreative(),
                        fastBreak)
                if (player.isSurvival() && !target.isBreakable(item)) {
                    ev.setCancelled()
                } else if (!player.isOp() && isInSpawnRadius(target)) {
                    ev.setCancelled()
                } else if (!ev.getInstaBreak() && ev.isFastBreak()) {
                    ev.setCancelled()
                }
                server.getPluginManager().callEvent(ev)
                if (ev.isCancelled()) {
                    return null
                }
                if (!ev.getInstaBreak() && ev.isFastBreak()) {
                    return null
                }
                player.lastBreak = System.currentTimeMillis()
                drops = ev.getDrops()
                dropExp = ev.getDropExp()
            } else {
                drops = eventDrops
            }
        } else if (!target.isBreakable(item)) {
            return null
        } else if (isSilkTouch) {
            drops = arrayOf<Item>(target.toItem())
        } else {
            drops = target.getDrops(item)
        }
        val above: Block = this.getBlock(Vector3(target.x, target.y + 1, target.z), 0)
        if (above != null) {
            if (above.getId() === Item.FIRE) {
                this.setBlock(above, Block.get(BlockID.AIR), true)
            }
        }
        if (createParticles) {
            val players: Map<Integer, Player> = getChunkPlayers(target.x as Int shr 4, target.z as Int shr 4)
            this.addParticle(DestroyBlockParticle(target.add(0.5), target), players.values())
            if (player != null && !setBlockDestroy) {
                players.remove(player.getLoaderId())
            }
        }

        // Close BlockEntity before we check onBreak
        if (layer == 0) {
            val blockEntity: BlockEntity = this.getBlockEntity(target)
            if (blockEntity != null) {
                blockEntity.onBreak(isSilkTouch)
                blockEntity.close()
                updateComparatorOutputLevel(target)
            }
        }
        target.onBreak(item)
        item.useOn(target)
        if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
            if (player != null) {
                addSound(player, Sound.RANDOM_BREAK)
            }
            item = ItemBlock(Block.get(BlockID.AIR), 0, 0)
        }
        if (gameRules!!.getBoolean(GameRule.DO_TILE_DROPS)) {
            if (!isSilkTouch && (mustDrop || player != null && (player.isSurvival() || setBlockDestroy)) && dropExp > 0 && drops.size != 0) {
                this.dropExpOrb(vector.add(0.5, 0.5, 0.5), dropExp)
            }
            if (mustDrop || player == null || setBlockDestroy || player.isSurvival()) {
                for (drop in drops) {
                    if (drop.getCount() > 0) {
                        this.dropItem(vector.add(0.5, 0.5, 0.5), drop)
                    }
                }
            }
        }
        return item
    }

    fun dropExpOrb(source: Vector3, exp: Int) {
        dropExpOrb(source, exp, null)
    }

    fun dropExpOrb(source: Vector3, exp: Int, motion: Vector3?) {
        dropExpOrb(source, exp, motion, 10)
    }

    fun dropExpOrb(source: Vector3, exp: Int, motion: Vector3?, delay: Int) {
        dropExpOrbAndGetEntities(source, exp, motion, delay)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun dropExpOrbAndGetEntities(source: Vector3, exp: Int): List<EntityXPOrb> {
        return dropExpOrbAndGetEntities(source, exp, null)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun dropExpOrbAndGetEntities(source: Vector3, exp: Int, motion: Vector3?): List<EntityXPOrb> {
        return dropExpOrbAndGetEntities(source, exp, motion, 10)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun dropExpOrbAndGetEntities(source: Vector3, exp: Int, motion: Vector3?, delay: Int): List<EntityXPOrb> {
        val rand: Random = ThreadLocalRandom.current()
        val drops: List<Integer> = EntityXPOrb.splitIntoOrbSizes(exp)
        val entities: List<EntityXPOrb> = ArrayList(drops.size())
        for (split in drops) {
            val nbt: CompoundTag = Entity.getDefaultNBT(source, if (motion == null) Vector3(
                    (rand.nextDouble() * 0.2 - 0.1) * 2,
                    rand.nextDouble() * 0.4,
                    (rand.nextDouble() * 0.2 - 0.1) * 2) else motion,
                    rand.nextFloat() * 360f, 0)
            nbt.putShort("Value", split)
            nbt.putShort("PickupDelay", delay)
            val entity: EntityXPOrb = Entity.createEntity("XpOrb", this.getChunk(source.getChunkX(), source.getChunkZ()), nbt) as EntityXPOrb
            if (entity != null) {
                entities.add(entity)
                entity.spawnToAll()
            }
        }
        return entities
    }

    fun useItemOn(vector: Vector3?, item: Item, face: BlockFace, fx: Float, fy: Float, fz: Float): Item? {
        return this.useItemOn(vector, item, face, fx, fy, fz, null)
    }

    fun useItemOn(vector: Vector3?, item: Item, face: BlockFace, fx: Float, fy: Float, fz: Float, player: Player?): Item? {
        return this.useItemOn(vector, item, face, fx, fy, fz, player, true)
    }

    @PowerNukkitDifference(info = "PowerNukkit#403", since = "1.3.1.2-PN")
    @PowerNukkitDifference(info = "Fixed PowerNukkit#716, block stops placing when towering up", since = "1.4.0.0-PN")
    fun useItemOn(vector: Vector3?, item: Item, face: BlockFace, fx: Float, fy: Float, fz: Float, player: Player?, playSound: Boolean): Item? {
        var item: Item = item
        val target: Block = this.getBlock(vector)
        var block: Block = target.getSide(face)
        if (item.getBlock() is BlockScaffolding && face === BlockFace.UP && block.getId() === BlockID.SCAFFOLDING) {
            while (block is BlockScaffolding) {
                block = block.up()
            }
        }
        if (block.y > 255 || block.y < 0) {
            return null
        }
        if (block.y > 127 && dimension == DIMENSION_NETHER) {
            return null
        }
        if (target.getId() === Item.AIR) {
            return null
        }
        if (player != null) {
            val ev = PlayerInteractEvent(player, item, target, face,
                    if (target.getId() === 0) Action.RIGHT_CLICK_AIR else Action.RIGHT_CLICK_BLOCK)
            if (player.getGamemode() > 2) {
                ev.setCancelled()
            }
            if (!player.isOp() && isInSpawnRadius(target)) {
                ev.setCancelled()
            }
            server.getPluginManager().callEvent(ev)
            if (!ev.isCancelled()) {
                target.onTouch(player, ev.getAction())
                if ((!player.isSneaking() || player.getInventory().getItemInHand().isNull()) && target.canBeActivated() && target.onActivate(item, player)) {
                    if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
                        addSound(player, Sound.RANDOM_BREAK)
                        item = ItemBlock(Block.get(BlockID.AIR), 0, 0)
                    }
                    return item
                }
                if (item.canBeActivated() && item.onActivate(this, player, block, target, face, fx, fy, fz)) {
                    if (item.getCount() <= 0) {
                        item = ItemBlock(Block.get(BlockID.AIR), 0, 0)
                        return item
                    }
                }
            } else {
                if (item is ItemBucket && (item as ItemBucket).isWater()) {
                    player.getLevel().sendBlocks(arrayOf<Player?>(player), arrayOf<Block>(Block.get(Block.AIR, 0, target.getLevelBlockAtLayer(1))), UpdateBlockPacket.FLAG_ALL_PRIORITY, 1)
                }
                return null
            }
            if (item is ItemBucket && (item as ItemBucket).isWater()) {
                player.getLevel().sendBlocks(arrayOf<Player?>(player), arrayOf<Block>(target.getLevelBlockAtLayer(1)), UpdateBlockPacket.FLAG_ALL_PRIORITY, 1)
            }
        } else if (target.canBeActivated() && target.onActivate(item, player)) {
            if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
                item = ItemBlock(Block.get(BlockID.AIR), 0, 0)
            }
            return item
        }
        val hand: Block
        if (item.canBePlaced()) {
            hand = item.getBlock()
            hand.position(block)
        } else {
            return null
        }
        if (!(block.canBeReplaced() || hand is BlockSlab && hand.getId() === block.getId())) {
            return null
        }
        if (target.canBeReplaced()) {
            block = target
            hand.position(block)
        }
        if (!hand.canPassThrough() && hand.getBoundingBox() != null) {
            val entities: Array<Entity?> = this.getCollidingEntities(hand.getBoundingBox())
            var realCount = 0
            for (e in entities) {
                if (e is EntityArrow
                        || e is EntityItem
                        || e is Player && (e as Player?).isSpectator()
                        || player === e) {
                    continue
                }
                ++realCount
            }
            if (player != null) {
                val diff: Vector3 = player.getNextPosition().subtract(player.getPosition())
                //if (diff.lengthSquared() > 0.00001) {
                val bb: AxisAlignedBB = player.getBoundingBox().getOffsetBoundingBox(diff.x, diff.y, diff.z)
                if (hand.getBoundingBox().intersectsWith(bb)) {
                    ++realCount
                }
                //}
            }
            if (realCount > 0) {
                return null // Entity in block
            }
        }
        if (player != null) {
            if (!block.isBlockChangeAllowed(player)) {
                return null
            }
            val event = BlockPlaceEvent(player, hand, block, target, item)
            if (player.getGamemode() === 2) {
                val tag: Tag = item.getNamedTagEntry("CanPlaceOn")
                var canPlace = false
                if (tag is ListTag) {
                    for (v in (tag as ListTag<Tag?>).getAll()) {
                        if (v is StringTag) {
                            val entry: Item = Item.fromString((v as StringTag).data)
                            if (entry.getId() > 0 && entry.getBlock() != null && entry.getBlock().getId() === target.getId()) {
                                canPlace = true
                                break
                            }
                        }
                    }
                }
                if (!canPlace) {
                    event.setCancelled()
                }
            }
            if (!player.isOp() && isInSpawnRadius(target)) {
                event.setCancelled()
            }
            server.getPluginManager().callEvent(event)
            if (event.isCancelled()) {
                return null
            }
        }
        if (hand.getWaterloggingLevel() === 0 && hand.canBeFlowedInto() && (block is BlockLiquid || block.getLevelBlockAtLayer(1) is BlockLiquid)) {
            return null
        }
        var liquidMoved = false
        if (block is BlockLiquid && (block as BlockLiquid).usesWaterLogging()) {
            liquidMoved = true
            this.setBlock(block, 1, block, false, false)
            this.setBlock(block, 0, Block.get(BlockID.AIR), false, false)
            this.scheduleUpdate(block, 1)
        }
        if (!hand.place(item, block, target, face, fx, fy, fz, player)) {
            if (liquidMoved) {
                this.setBlock(block, 0, block, false, false)
                this.setBlock(block, 1, Block.get(BlockID.AIR), false, false)
            }
            return null
        }
        if (player != null) {
            if (!player.isCreative()) {
                item.setCount(item.getCount() - 1)
            }
        }
        if (playSound) {
            this.addLevelSoundEvent(hand, LevelSoundEventPacket.SOUND_PLACE, GlobalBlockPalette.getOrCreateRuntimeId(hand.getId(), hand.getDamage()))
        }
        if (item.getCount() <= 0) {
            item = ItemBlock(Block.get(BlockID.AIR), 0, 0)
        }
        return item
    }

    fun isInSpawnRadius(vector3: Vector3): Boolean {
        val distance: Int = server.getSpawnRadius()
        if (distance > -1) {
            val t = Vector2(vector3.x, vector3.z)
            val s = Vector2(spawnLocation.x, spawnLocation.z)
            return t.distance(s) <= distance
        }
        return false
    }

    fun getEntity(entityId: Long): Entity? {
        return if (entities.containsKey(entityId)) entities.get(entityId) else null
    }

    fun getEntities(): Array<Entity> {
        return entities.values().toArray(Entity.EMPTY_ARRAY)
    }

    fun getCollidingEntities(bb: AxisAlignedBB): Array<Entity?> {
        return this.getCollidingEntities(bb, null)
    }

    fun getCollidingEntities(bb: AxisAlignedBB, entity: Entity?): Array<Entity?> {
        var index = 0
        var overflow: ArrayList<Entity>? = null
        if (entity == null || entity.canCollide()) {
            val minX: Int = NukkitMath.floorDouble((bb.getMinX() - 2) / 16)
            val maxX: Int = NukkitMath.ceilDouble((bb.getMaxX() + 2) / 16)
            val minZ: Int = NukkitMath.floorDouble((bb.getMinZ() - 2) / 16)
            val maxZ: Int = NukkitMath.ceilDouble((bb.getMaxZ() + 2) / 16)
            for (x in minX..maxX) {
                for (z in minZ..maxZ) {
                    for (ent in this.getChunkEntities(x, z, false).values()) {
                        if ((entity == null || ent !== entity && entity.canCollideWith(ent))
                                && ent.boundingBox.intersectsWith(bb)) {
                            overflow = addEntityToBuffer(index, overflow, ent)
                            index++
                        }
                    }
                }
            }
        }
        return getEntitiesFromBuffer(index, overflow)
    }

    fun getNearbyEntities(bb: AxisAlignedBB): Array<Entity?> {
        return this.getNearbyEntities(bb, null)
    }

    fun getNearbyEntities(bb: AxisAlignedBB, entity: Entity?): Array<Entity?> {
        return getNearbyEntities(bb, entity, false)
    }

    fun getNearbyEntities(bb: AxisAlignedBB, entity: Entity?, loadChunks: Boolean): Array<Entity?> {
        var index = 0
        val minX: Int = NukkitMath.floorDouble((bb.getMinX() - 2) * 0.0625)
        val maxX: Int = NukkitMath.ceilDouble((bb.getMaxX() + 2) * 0.0625)
        val minZ: Int = NukkitMath.floorDouble((bb.getMinZ() - 2) * 0.0625)
        val maxZ: Int = NukkitMath.ceilDouble((bb.getMaxZ() + 2) * 0.0625)
        var overflow: ArrayList<Entity>? = null
        for (x in minX..maxX) {
            for (z in minZ..maxZ) {
                for (ent in this.getChunkEntities(x, z, loadChunks).values()) {
                    if (ent !== entity && ent.boundingBox.intersectsWith(bb)) {
                        overflow = addEntityToBuffer(index, overflow, ent)
                        index++
                    }
                }
            }
        }
        return getEntitiesFromBuffer(index, overflow)
    }

    private fun addEntityToBuffer(index: Int, overflow: ArrayList<Entity>?, ent: Entity): ArrayList<Entity>? {
        var overflow: ArrayList<Entity>? = overflow
        if (index < ENTITY_BUFFER.size) {
            ENTITY_BUFFER[index] = ent
        } else {
            if (overflow == null) overflow = ArrayList(1024)
            overflow.add(ent)
        }
        return overflow
    }

    private fun getEntitiesFromBuffer(index: Int, overflow: ArrayList<Entity>?): Array<Entity?> {
        if (index == 0) return Entity.EMPTY_ARRAY
        val copy: Array<Entity?>
        if (overflow == null) {
            copy = Arrays.copyOfRange(ENTITY_BUFFER, 0, index)
            Arrays.fill(ENTITY_BUFFER, 0, index, null)
        } else {
            copy = arrayOfNulls<Entity>(ENTITY_BUFFER.size + overflow.size())
            System.arraycopy(ENTITY_BUFFER, 0, copy, 0, ENTITY_BUFFER.size)
            for (i in 0 until overflow.size()) {
                copy[ENTITY_BUFFER.size + i] = overflow.get(i)
            }
        }
        return copy
    }

    fun getBlockEntities(): Map<Long, BlockEntity> {
        return blockEntities
    }

    fun getBlockEntityById(blockEntityId: Long): BlockEntity? {
        return if (blockEntities.containsKey(blockEntityId)) blockEntities.get(blockEntityId) else null
    }

    fun getPlayers(): Map<Long, Player> {
        return players
    }

    fun getLoaders(): Map<Integer, ChunkLoader> {
        return loaders
    }

    fun getBlockEntity(pos: Vector3): BlockEntity {
        return getBlockEntity(pos.asBlockVector3())
    }

    fun getBlockEntity(pos: BlockVector3): BlockEntity? {
        val chunk: FullChunk = this.getChunk(pos.x shr 4, pos.z shr 4, false)
        return if (chunk != null) {
            chunk.getTile(pos.x and 0x0f, pos.y and 0xff, pos.z and 0x0f)
        } else null
    }

    fun getBlockEntityIfLoaded(pos: Vector3): BlockEntity? {
        val chunk: FullChunk = getChunkIfLoaded(pos.x as Int shr 4, pos.z as Int shr 4)
        return if (chunk != null) {
            chunk.getTile(pos.x as Int and 0x0f, pos.y as Int and 0xff, pos.z as Int and 0x0f)
        } else null
    }

    fun getChunkEntities(X: Int, Z: Int): Map<Long, Entity> {
        return getChunkEntities(X, Z, true)
    }

    fun getChunkEntities(X: Int, Z: Int, loadChunks: Boolean): Map<Long, Entity> {
        val chunk: FullChunk = if (loadChunks) this.getChunk(X, Z) else getChunkIfLoaded(X, Z)
        return if (chunk != null) chunk.getEntities() else Collections.emptyMap()
    }

    fun getChunkBlockEntities(X: Int, Z: Int): Map<Long, BlockEntity> {
        var chunk: FullChunk
        return if (this.getChunk(X, Z).also { chunk = it } != null) chunk.getBlockEntities() else Collections.emptyMap()
    }

    @Override
    override fun getBlockStateAt(x: Int, y: Int, z: Int, layer: Int): BlockState {
        return getChunk(x shr 4, z shr 4, true).getBlockStateAt(x and 0x0f, y and 0xff, z and 0x0f, layer)
    }

    @Override
    override fun getBlockIdAt(x: Int, y: Int, z: Int): Int {
        return getBlockIdAt(x, y, z, 0)
    }

    @Override
    @Synchronized
    override fun getBlockIdAt(x: Int, y: Int, z: Int, layer: Int): Int {
        return this.getChunk(x shr 4, z shr 4, true).getBlockId(x and 0x0f, y and 0xff, z and 0x0f, layer)
    }

    @Override
    override fun setBlockIdAt(x: Int, y: Int, z: Int, id: Int) {
        setBlockIdAt(x, y, z, 0, id)
    }

    @Override
    @Synchronized
    override fun setBlockIdAt(x: Int, y: Int, z: Int, layer: Int, id: Int) {
        this.getChunk(x shr 4, z shr 4, true).setBlockId(x and 0x0f, y and 0xff, z and 0x0f, layer, id and 0xfff)
        addBlockChange(x, y, z)
        temporalVector.setComponents(x, y, z)
        for (loader in getChunkLoaders(x shr 4, z shr 4)) {
            loader!!.onBlockChanged(temporalVector)
        }
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    @Synchronized
    override fun setBlockAt(x: Int, y: Int, z: Int, id: Int, data: Int) {
        setBlockAtLayer(x, y, z, 0, id, data)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    @Synchronized
    override fun setBlockAtLayer(x: Int, y: Int, z: Int, layer: Int, id: Int, data: Int): Boolean {
        return setBlockStateAt(x, y, z, layer, BlockState.of(id, data))
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Synchronized
    override fun setBlockStateAt(x: Int, y: Int, z: Int, layer: Int, state: BlockState?): Boolean {
        val chunk: BaseFullChunk = this.getChunk(x shr 4, z shr 4, true)
        val changed: Boolean = chunk.setBlockStateAtLayer(x and 0x0f, y and 0xff, z and 0x0f, layer, state)
        addBlockChange(x, y, z)
        temporalVector.setComponents(x, y, z)
        for (loader in getChunkLoaders(x shr 4, z shr 4)) {
            loader!!.onBlockChanged(temporalVector)
        }
        return changed
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    override fun getBlockDataAt(x: Int, y: Int, z: Int): Int {
        return getBlockDataAt(x, y, z, 0)
    }

    @Synchronized
    fun getBlockExtraDataAt(x: Int, y: Int, z: Int): Int {
        return this.getChunk(x shr 4, z shr 4, true).getBlockExtraData(x and 0x0f, y and 0xff, z and 0x0f)
    }

    @Synchronized
    fun setBlockExtraDataAt(x: Int, y: Int, z: Int, id: Int, data: Int) {
        this.getChunk(x shr 4, z shr 4, true).setBlockExtraData(x and 0x0f, y and 0xff, z and 0x0f, data shl 8 or id)
        this.sendBlockExtraData(x, y, z, id, data)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    @Synchronized
    override fun getBlockDataAt(x: Int, y: Int, z: Int, layer: Int): Int {
        return this.getChunk(x shr 4, z shr 4, true).getBlockData(x and 0x0f, y and 0xff, z and 0x0f, layer)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    override fun setBlockDataAt(x: Int, y: Int, z: Int, data: Int) {
        setBlockDataAt(x, y, z, 0, data)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    @Synchronized
    override fun setBlockDataAt(x: Int, y: Int, z: Int, layer: Int, data: Int) {
        this.getChunk(x shr 4, z shr 4, true).setBlockData(x and 0x0f, y and 0xff, z and 0x0f, layer, data)
        addBlockChange(x, y, z)
        temporalVector.setComponents(x, y, z)
        for (loader in getChunkLoaders(x shr 4, z shr 4)) {
            loader!!.onBlockChanged(temporalVector)
        }
    }

    @Synchronized
    fun getBlockSkyLightAt(x: Int, y: Int, z: Int): Int {
        return this.getChunk(x shr 4, z shr 4, true).getBlockSkyLight(x and 0x0f, y and 0xff, z and 0x0f)
    }

    @Synchronized
    fun setBlockSkyLightAt(x: Int, y: Int, z: Int, level: Int) {
        this.getChunk(x shr 4, z shr 4, true).setBlockSkyLight(x and 0x0f, y and 0xff, z and 0x0f, level and 0x0f)
    }

    @Synchronized
    fun getBlockLightAt(x: Int, y: Int, z: Int): Int {
        return this.getChunk(x shr 4, z shr 4, true).getBlockLight(x and 0x0f, y and 0xff, z and 0x0f)
    }

    @Synchronized
    fun setBlockLightAt(x: Int, y: Int, z: Int, level: Int) {
        this.getChunk(x shr 4, z shr 4, true).setBlockLight(x and 0x0f, y and 0xff, z and 0x0f, level and 0x0f)
    }

    fun getBiomeId(x: Int, z: Int): Int {
        return this.getChunk(x shr 4, z shr 4, true).getBiomeId(x and 0x0f, z and 0x0f)
    }

    fun setBiomeId(x: Int, z: Int, biomeId: Byte) {
        this.getChunk(x shr 4, z shr 4, true).setBiomeId(x and 0x0f, z and 0x0f, biomeId)
    }

    fun getHeightMap(x: Int, z: Int): Int {
        return this.getChunk(x shr 4, z shr 4, true).getHeightMap(x and 0x0f, z and 0x0f)
    }

    fun setHeightMap(x: Int, z: Int, value: Int) {
        this.getChunk(x shr 4, z shr 4, true).setHeightMap(x and 0x0f, z and 0x0f, value and 0x0f)
    }

    val chunks: Map<Long, Any?>
        get() = requireProvider().getLoadedChunks()

    @Override
    override fun getChunk(chunkX: Int, chunkZ: Int): BaseFullChunk {
        return this.getChunk(chunkX, chunkZ, false)
    }

    fun getChunk(chunkX: Int, chunkZ: Int, create: Boolean): BaseFullChunk {
        val index = chunkHash(chunkX, chunkZ)
        var chunk: BaseFullChunk = requireProvider().getLoadedChunk(index)
        if (chunk == null) {
            chunk = forceLoadChunk(index, chunkX, chunkZ, create)
        }
        return chunk
    }

    fun getChunkIfLoaded(chunkX: Int, chunkZ: Int): BaseFullChunk {
        val index = chunkHash(chunkX, chunkZ)
        return requireProvider().getLoadedChunk(index)
    }

    fun generateChunkCallback(x: Int, z: Int, chunk: BaseFullChunk) {
        generateChunkCallback(x, z, chunk, true)
    }

    fun generateChunkCallback(x: Int, z: Int, chunk: BaseFullChunk, isPopulated: Boolean) {
        var chunk: BaseFullChunk = chunk
        Timings.generationCallbackTimer.startTiming()
        val index = chunkHash(x, z)
        val levelProvider: LevelProvider = requireProvider()
        if (chunkPopulationQueue.containsKey(index)) {
            val oldChunk: FullChunk = this.getChunk(x, z, false)
            for (xx in -1..1) {
                for (zz in -1..1) {
                    chunkPopulationLock.remove(chunkHash(x + xx, z + zz))
                }
            }
            chunkPopulationQueue.remove(index)
            chunk.setProvider(levelProvider)
            this.setChunk(x, z, chunk, false)
            chunk = this.getChunk(x, z, false)
            if (chunk != null && (oldChunk == null || !isPopulated) && chunk.isPopulated()
                    && chunk.getProvider() != null) {
                server.getPluginManager().callEvent(ChunkPopulateEvent(chunk))
                for (loader in getChunkLoaders(x, z)) {
                    loader!!.onChunkPopulated(chunk)
                }
            }
        } else if (chunkGenerationQueue.containsKey(index) || chunkPopulationLock.containsKey(index)) {
            chunkGenerationQueue.remove(index)
            chunkPopulationLock.remove(index)
            chunk.setProvider(levelProvider)
            this.setChunk(x, z, chunk, false)
        } else {
            chunk.setProvider(levelProvider)
            this.setChunk(x, z, chunk, false)
        }
        Timings.generationCallbackTimer.stopTiming()
    }

    @Override
    override fun setChunk(chunkX: Int, chunkZ: Int) {
        this.setChunk(chunkX, chunkZ, null)
    }

    @Override
    override fun setChunk(chunkX: Int, chunkZ: Int, chunk: BaseFullChunk?) {
        this.setChunk(chunkX, chunkZ, chunk, true)
    }

    fun setChunk(chunkX: Int, chunkZ: Int, chunk: BaseFullChunk, unload: Boolean) {
        if (chunk == null) {
            return
        }
        val index = chunkHash(chunkX, chunkZ)
        val oldChunk: FullChunk = this.getChunk(chunkX, chunkZ, false)
        if (oldChunk !== chunk) {
            if (unload && oldChunk != null) {
                this.unloadChunk(chunkX, chunkZ, false, false)
                requireProvider().setChunk(chunkX, chunkZ, chunk)
            } else {
                val oldEntities: Map<Long, Entity> = if (oldChunk != null) oldChunk.getEntities() else Collections.emptyMap()
                val oldBlockEntities: Map<Long, BlockEntity> = if (oldChunk != null) oldChunk.getBlockEntities() else Collections.emptyMap()
                if (!oldEntities.isEmpty()) {
                    val iter: Iterator<Map.Entry<Long, Entity>> = oldEntities.entrySet().iterator()
                    while (iter.hasNext()) {
                        val entry: Map.Entry<Long, Entity> = iter.next()
                        val entity: Entity = entry.getValue()
                        chunk.addEntity(entity)
                        if (oldChunk != null) {
                            iter.remove()
                            oldChunk.removeEntity(entity)
                            entity.chunk = chunk
                        }
                    }
                }
                if (!oldBlockEntities.isEmpty()) {
                    val iter: Iterator<Map.Entry<Long, BlockEntity>> = oldBlockEntities.entrySet().iterator()
                    while (iter.hasNext()) {
                        val entry: Map.Entry<Long, BlockEntity> = iter.next()
                        val blockEntity: BlockEntity = entry.getValue()
                        chunk.addBlockEntity(blockEntity)
                        if (oldChunk != null) {
                            iter.remove()
                            oldChunk.removeBlockEntity(blockEntity)
                            blockEntity.chunk = chunk
                        }
                    }
                }
                requireProvider().setChunk(chunkX, chunkZ, chunk)
            }
        }
        chunk.setChanged()
        if (!this.isChunkInUse(index)) {
            unloadChunkRequest(chunkX, chunkZ)
        } else {
            for (loader in getChunkLoaders(chunkX, chunkZ)) {
                loader!!.onChunkChanged(chunk)
            }
        }
    }

    fun getHighestBlockAt(x: Int, z: Int): Int {
        return this.getChunk(x shr 4, z shr 4, true).getHighestBlockAt(x and 0x0f, z and 0x0f)
    }

    fun getMapColorAt(x: Int, z: Int): BlockColor {
        var y = getHighestBlockAt(x, z)
        while (y > 1) {
            val block: Block = getBlock(Vector3(x, y, z))
            val blockColor: BlockColor = block.getColor()
            if (blockColor.getAlpha() === 0x00) {
                y--
            } else {
                return blockColor
            }
        }
        return BlockColor.VOID_BLOCK_COLOR
    }

    fun isChunkLoaded(x: Int, z: Int): Boolean {
        return requireProvider().isChunkLoaded(x, z)
    }

    private fun areNeighboringChunksLoaded(hash: Long): Boolean {
        val levelProvider: LevelProvider = requireProvider()
        return levelProvider.isChunkLoaded(hash + 1) &&
                levelProvider.isChunkLoaded(hash - 1) &&
                levelProvider.isChunkLoaded(hash + (1L shl 32)) &&
                levelProvider.isChunkLoaded(hash - (1L shl 32))
    }

    fun isChunkGenerated(x: Int, z: Int): Boolean {
        val chunk: FullChunk = this.getChunk(x, z)
        return chunk != null && chunk.isGenerated()
    }

    fun isChunkPopulated(x: Int, z: Int): Boolean {
        val chunk: FullChunk = this.getChunk(x, z)
        return chunk != null && chunk.isPopulated()
    }

    var spawnLocation: cn.nukkit.level.Position
        get() = Position.fromObject(requireProvider().getSpawn(), this)
        set(pos) {
            val previousSpawn: Position = spawnLocation
            requireProvider().setSpawn(pos)
            server.getPluginManager().callEvent(SpawnChangeEvent(this, previousSpawn))
            val pk = SetSpawnPositionPacket()
            pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN
            pk.x = pos.getFloorX()
            pk.y = pos.getFloorY()
            pk.z = pos.getFloorZ()
            pk.dimension = dimension
            for (p in getPlayers().values()) {
                p.dataPacket(pk)
            }
        }
    val fuzzySpawnLocation: cn.nukkit.level.Position
        get() {
            var spawn: Position = spawnLocation
            val radius: Int = gameRules!!.getInteger(GameRule.SPAWN_RADIUS)
            if (radius > 0) {
                val random: ThreadLocalRandom = ThreadLocalRandom.current()
                val negativeFlags: Int = random.nextInt(4)
                spawn = spawn.add(
                        radius * random.nextDouble() * if (negativeFlags and 1 > 0) -1 else 1,
                        0,
                        radius * random.nextDouble() * if (negativeFlags and 2 > 0) -1 else 1
                )
            }
            return spawn
        }

    fun requestChunk(x: Int, z: Int, player: Player) {
        Preconditions.checkState(player.getLoaderId() > 0, player.getName().toString() + " has no chunk loader")
        val index = chunkHash(x, z)
        chunkSendQueue.putIfAbsent(index, Int2ObjectOpenHashMap())
        chunkSendQueue.get(index).put(player.getLoaderId(), player)
    }

    private fun sendChunk(x: Int, z: Int, index: Long, packet: DataPacket) {
        if (chunkSendTasks.contains(index)) {
            for (player in chunkSendQueue.get(index).values()) {
                if (player.isConnected() && player.usedChunks.containsKey(index)) {
                    player.sendChunk(x, z, packet)
                }
            }
            chunkSendQueue.remove(index)
            chunkSendTasks.remove(index)
        }
    }

    private fun processChunkRequest() {
        timings.syncChunkSendTimer.startTiming()
        for (index in chunkSendQueue.keySet()) {
            if (chunkSendTasks.contains(index)) {
                continue
            }
            val x = getHashX(index)
            val z = getHashZ(index)
            chunkSendTasks.add(index)
            val chunk: BaseFullChunk = getChunk(x, z)
            if (chunk != null) {
                val packet: BatchPacket = chunk.getChunkPacket()
                if (packet != null) {
                    sendChunk(x, z, index, packet)
                    continue
                }
            }
            timings.syncChunkSendPrepareTimer.startTiming()
            val task: AsyncTask = requireProvider().requestChunkTask(x, z)
            if (task != null) {
                server.getScheduler().scheduleAsyncTask(task)
            }
            timings.syncChunkSendPrepareTimer.stopTiming()
        }
        timings.syncChunkSendTimer.stopTiming()
    }

    fun chunkRequestCallback(timestamp: Long, x: Int, z: Int, subChunkCount: Int, payload: ByteArray?) {
        timings.syncChunkSendTimer.startTiming()
        val index = chunkHash(x, z)
        if (cacheChunks) {
            val data: BatchPacket = Player.getChunkCacheFromData(x, z, subChunkCount, payload)
            val chunk: BaseFullChunk = getChunk(x, z, false)
            if (chunk != null && chunk.getChanges() <= timestamp) {
                chunk.setChunkPacket(data)
            }
            sendChunk(x, z, index, data)
            timings.syncChunkSendTimer.stopTiming()
            return
        }
        if (chunkSendTasks.contains(index)) {
            for (player in chunkSendQueue.get(index).values()) {
                if (player.isConnected() && player.usedChunks.containsKey(index)) {
                    player.sendChunk(x, z, subChunkCount, payload)
                }
            }
            chunkSendQueue.remove(index)
            chunkSendTasks.remove(index)
        }
        timings.syncChunkSendTimer.stopTiming()
    }

    fun removeEntity(entity: Entity) {
        if (entity.getLevel() !== this) {
            throw LevelException("Invalid Entity level")
        }
        if (entity is Player) {
            players.remove(entity.getId())
            checkSleep()
        } else {
            entity.close()
        }
        entities.remove(entity.getId())
        updateEntities.remove(entity.getId())
    }

    fun addEntity(entity: Entity) {
        if (entity.getLevel() !== this) {
            throw LevelException("Invalid Entity level")
        }
        if (entity is Player) {
            players.put(entity.getId(), entity as Player)
        }
        entities.put(entity.getId(), entity)
    }

    fun addBlockEntity(blockEntity: BlockEntity) {
        if (blockEntity.getLevel() !== this) {
            throw LevelException("Invalid Block Entity level")
        }
        blockEntities.put(blockEntity.getId(), blockEntity)
    }

    fun scheduleBlockEntityUpdate(entity: BlockEntity) {
        Preconditions.checkNotNull(entity, "entity")
        Preconditions.checkArgument(entity.getLevel() === this, "BlockEntity is not in this level")
        if (!updateBlockEntities.contains(entity)) {
            updateBlockEntities.add(entity)
        }
    }

    fun removeBlockEntity(entity: BlockEntity) {
        Preconditions.checkNotNull(entity, "entity")
        Preconditions.checkArgument(entity.getLevel() === this, "BlockEntity is not in this level")
        blockEntities.remove(entity.getId())
        updateBlockEntities.remove(entity)
    }

    fun isChunkInUse(x: Int, z: Int): Boolean {
        return isChunkInUse(chunkHash(x, z))
    }

    fun isChunkInUse(hash: Long): Boolean {
        return chunkLoaders.containsKey(hash) && !chunkLoaders.get(hash).isEmpty()
    }

    @JvmOverloads
    fun loadChunk(x: Int, z: Int, generate: Boolean = true): Boolean {
        val index = chunkHash(x, z)
        return if (requireProvider().isChunkLoaded(index)) {
            true
        } else forceLoadChunk(index, x, z, generate) != null
    }

    @Synchronized
    private fun forceLoadChunk(index: Long, x: Int, z: Int, generate: Boolean): BaseFullChunk {
        timings.syncChunkLoadTimer.startTiming()
        val chunk: BaseFullChunk = requireProvider().getChunk(x, z, generate)
        if (chunk == null) {
            if (generate) {
                throw IllegalStateException("Could not create new Chunk")
            }
            timings.syncChunkLoadTimer.stopTiming()
            return chunk
        }
        if (chunk.getProvider() != null) {
            server.getPluginManager().callEvent(ChunkLoadEvent(chunk, !chunk.isGenerated()))
        } else {
            this.unloadChunk(x, z, false)
            timings.syncChunkLoadTimer.stopTiming()
            return chunk
        }
        chunk.backwardCompatibilityUpdate(this)
        chunk.initChunk()
        if (!chunk.isLightPopulated() && chunk.isPopulated()
                && getServer().getConfig("chunk-ticking.light-updates", false)) {
            getServer().getScheduler().scheduleAsyncTask(LightPopulationTask(this, chunk))
        }
        if (this.isChunkInUse(index)) {
            unloadQueue.remove(index)
            for (loader in getChunkLoaders(x, z)) {
                loader!!.onChunkLoaded(chunk)
            }
        } else {
            unloadQueue.put(index, System.currentTimeMillis())
        }
        timings.syncChunkLoadTimer.stopTiming()
        return chunk
    }

    private fun queueUnloadChunk(x: Int, z: Int) {
        val index = chunkHash(x, z)
        unloadQueue.put(index, System.currentTimeMillis())
    }

    @JvmOverloads
    fun unloadChunkRequest(x: Int, z: Int, safe: Boolean = true): Boolean {
        if (safe && this.isChunkInUse(x, z) || isSpawnChunk(x, z)) {
            return false
        }
        queueUnloadChunk(x, z)
        return true
    }

    fun cancelUnloadChunkRequest(x: Int, z: Int) {
        this.cancelUnloadChunkRequest(chunkHash(x, z))
    }

    fun cancelUnloadChunkRequest(hash: Long) {
        unloadQueue.remove(hash)
    }

    @JvmOverloads
    fun unloadChunk(x: Int, z: Int, safe: Boolean = true): Boolean {
        return this.unloadChunk(x, z, safe, true)
    }

    @Synchronized
    fun unloadChunk(x: Int, z: Int, safe: Boolean, trySave: Boolean): Boolean {
        if (safe && this.isChunkInUse(x, z)) {
            return false
        }
        if (!isChunkLoaded(x, z)) {
            return true
        }
        timings.doChunkUnload.startTiming()
        val chunk: BaseFullChunk = this.getChunk(x, z)
        if (chunk != null && chunk.getProvider() != null) {
            val ev = ChunkUnloadEvent(chunk)
            server.getPluginManager().callEvent(ev)
            if (ev.isCancelled()) {
                timings.doChunkUnload.stopTiming()
                return false
            }
        }
        try {
            val levelProvider: LevelProvider = requireProvider()
            if (chunk != null) {
                if (trySave && autoSave) {
                    var entities = 0
                    for (e in chunk.getEntities().values()) {
                        if (e is Player) {
                            continue
                        }
                        ++entities
                    }
                    if (chunk.hasChanged() || !chunk.getBlockEntities().isEmpty() || entities > 0) {
                        levelProvider.setChunk(x, z, chunk)
                        levelProvider.saveChunk(x, z)
                    }
                }
                for (loader in getChunkLoaders(x, z)) {
                    loader!!.onChunkUnloaded(chunk)
                }
            }
            levelProvider.unloadChunk(x, z, safe)
        } catch (e: Exception) {
            log.error(server.getLanguage().translateString("nukkit.level.chunkUnloadError", e.toString()), e)
        }
        timings.doChunkUnload.stopTiming()
        return true
    }

    fun isSpawnChunk(X: Int, Z: Int): Boolean {
        val spawn: Vector3 = requireProvider().getSpawn()
        return Math.abs(X - (spawn.getFloorX() shr 4)) <= 1 && Math.abs(Z - (spawn.getFloorZ() shr 4)) <= 1
    }

    val safeSpawn: cn.nukkit.level.Position?
        get() = getSafeSpawn(null)

    fun getSafeSpawn(spawn: Vector3?): Position? {
        var spawn: Vector3? = spawn
        if (spawn == null || spawn.y < 1) {
            spawn = fuzzySpawnLocation
        }
        if (spawn != null) {
            val v: Vector3 = spawn.floor()
            val chunk: FullChunk = this.getChunk(v.x as Int shr 4, v.z as Int shr 4, false)
            val x = v.x as Int and 0x0f
            val z = v.z as Int and 0x0f
            if (chunk != null && chunk.isGenerated()) {
                var y = NukkitMath.clamp(v.y, 1, 254) as Int
                var wasAir = chunk.getBlockId(x, y - 1, z) === 0
                while (y > 0) {
                    val state: BlockState = chunk.getBlockState(x, y, z)
                    val block: Block = state.getBlockRepairing(this, x, y, z)
                    if (isFullBlock(block)) {
                        if (wasAir) {
                            y++
                            break
                        }
                    } else {
                        wasAir = true
                    }
                    --y
                }
                while (y >= 0 && y < 255) {
                    var state: BlockState = chunk.getBlockState(x, y + 1, z)
                    var block: Block = state.getBlockRepairing(this, x, y + 1, z)
                    if (!isFullBlock(block)) {
                        state = chunk.getBlockState(x, y, z)
                        block = state.getBlockRepairing(this, x, y, z)
                        if (!isFullBlock(block)) {
                            return Position(spawn.x, (if (y == spawn.y as Int) spawn.y else y).toDouble(), spawn.z, this)
                        }
                    } else {
                        ++y
                    }
                    y++
                }
                v.y = y
            }
            return Position(spawn.x, v.y, spawn.z, this)
        }
        return null
    }

    fun getTime(): Int {
        return time.toInt()
    }

    val isDaytime: Boolean
        get() = skyLightSubtracted < 4
    val name: String
        get() = requireProvider().getName()

    fun setTime(time: Int) {
        this.time = time.toFloat()
        this.sendTime()
    }

    fun stopTime() {
        stopTime = true
        this.sendTime()
    }

    fun startTime() {
        stopTime = false
        this.sendTime()
    }

    @get:Override
    override val seed: Long
        get() = requireProvider().getSeed()

    fun setSeed(seed: Int) {
        requireProvider().setSeed(seed)
    }

    @JvmOverloads
    fun populateChunk(x: Int, z: Int, force: Boolean = false): Boolean {
        val index = chunkHash(x, z)
        if (chunkPopulationQueue.containsKey(index) || chunkPopulationQueue.size() >= chunkPopulationQueueSize && !force) {
            return false
        }
        val chunk: BaseFullChunk = this.getChunk(x, z, true)
        var populate: Boolean
        if (!chunk.isPopulated()) {
            Timings.populationTimer.startTiming()
            populate = true
            for (xx in -1..1) {
                for (zz in -1..1) {
                    if (chunkPopulationLock.containsKey(chunkHash(x + xx, z + zz))) {
                        populate = false
                        break
                    }
                }
            }
            if (populate) {
                if (!chunkPopulationQueue.containsKey(index)) {
                    chunkPopulationQueue.put(index, Boolean.TRUE)
                    for (xx in -1..1) {
                        for (zz in -1..1) {
                            chunkPopulationLock.put(chunkHash(x + xx, z + zz), Boolean.TRUE)
                        }
                    }
                    val task = PopulationTask(this, chunk)
                    server.getScheduler().scheduleAsyncTask(task)
                }
            }
            Timings.populationTimer.stopTiming()
            return false
        }
        return true
    }

    @JvmOverloads
    fun generateChunk(x: Int, z: Int, force: Boolean = false) {
        if (chunkGenerationQueue.size() >= chunkGenerationQueueSize && !force) {
            return
        }
        val index = chunkHash(x, z)
        if (!chunkGenerationQueue.containsKey(index)) {
            Timings.generationTimer.startTiming()
            chunkGenerationQueue.put(index, Boolean.TRUE)
            val task = GenerationTask(this, this.getChunk(x, z, true))
            server.getScheduler().scheduleAsyncTask(task)
            Timings.generationTimer.stopTiming()
        }
    }

    fun regenerateChunk(x: Int, z: Int) {
        this.unloadChunk(x, z, false)
        this.cancelUnloadChunkRequest(x, z)
        val levelProvider: LevelProvider = requireProvider()
        val chunk: BaseFullChunk = levelProvider.getEmptyChunk(x, z)
        levelProvider.setChunk(x, z, chunk)
        generateChunk(x, z)
    }

    fun doChunkGarbageCollection() {
        timings.doChunkGC.startTiming()
        // remove all invaild block entities.
        if (!blockEntities.isEmpty()) {
            val iter: ObjectIterator<BlockEntity> = blockEntities.values().iterator()
            while (iter.hasNext()) {
                val blockEntity: BlockEntity = iter.next()
                if (blockEntity != null) {
                    if (!blockEntity.isValid()) {
                        iter.remove()
                        blockEntity.close()
                    }
                } else {
                    iter.remove()
                }
            }
        }
        for (entry in requireProvider().getLoadedChunks().entrySet()) {
            val index: Long = entry.getKey()
            if (!unloadQueue.containsKey(index)) {
                val chunk: FullChunk = entry.getValue()
                val X: Int = chunk.getX()
                val Z: Int = chunk.getZ()
                if (!isSpawnChunk(X, Z)) {
                    unloadChunkRequest(X, Z, true)
                }
            }
        }
        requireProvider().doGarbageCollection()
        timings.doChunkGC.stopTiming()
    }

    fun doGarbageCollection(allocatedTime: Long) {
        var allocatedTime = allocatedTime
        val start: Long = System.currentTimeMillis()
        if (unloadChunks(start, allocatedTime, false)) {
            allocatedTime = allocatedTime - (System.currentTimeMillis() - start)
            requireProvider().doGarbageCollection(allocatedTime)
        }
    }

    @JvmOverloads
    fun unloadChunks(force: Boolean = false) {
        unloadChunks(96, force)
    }

    fun unloadChunks(maxUnload: Int, force: Boolean) {
        var maxUnload = maxUnload
        if (!unloadQueue.isEmpty()) {
            val now: Long = System.currentTimeMillis()
            var toRemove: LongList? = null
            for (entry in unloadQueue.long2LongEntrySet()) {
                val index: Long = entry.getLongKey()
                if (isChunkInUse(index)) {
                    continue
                }
                if (!force) {
                    val time: Long = entry.getLongValue()
                    if (maxUnload <= 0) {
                        break
                    } else if (time > now - 30000) {
                        continue
                    }
                }
                if (toRemove == null) toRemove = LongArrayList()
                toRemove.add(index)
            }
            if (toRemove != null) {
                val size: Int = toRemove.size()
                for (i in 0 until size) {
                    val index: Long = toRemove.getLong(i)
                    val X = getHashX(index)
                    val Z = getHashZ(index)
                    if (this.unloadChunk(X, Z, true)) {
                        unloadQueue.remove(index)
                        --maxUnload
                    }
                }
            }
        }
    }

    private var lastUnloadIndex = 0

    /**
     * @param now
     * @param allocatedTime
     * @param force
     * @return true if there is allocated time remaining
     */
    private fun unloadChunks(now: Long, allocatedTime: Long, force: Boolean): Boolean {
        return if (!unloadQueue.isEmpty()) {
            var result = true
            val maxIterations: Int = unloadQueue.size()
            if (lastUnloadIndex > maxIterations) lastUnloadIndex = 0
            var iter: ObjectIterator<Long2LongMap.Entry?> = unloadQueue.long2LongEntrySet().iterator()
            if (lastUnloadIndex != 0) iter.skip(lastUnloadIndex)
            var toUnload: LongList? = null
            for (i in 0 until maxIterations) {
                if (!iter.hasNext()) {
                    iter = unloadQueue.long2LongEntrySet().iterator()
                }
                val entry: Long2LongMap.Entry = iter.next()
                val index: Long = entry.getLongKey()
                if (isChunkInUse(index)) {
                    continue
                }
                if (!force) {
                    val time: Long = entry.getLongValue()
                    if (time > now - 30000) {
                        continue
                    }
                }
                if (toUnload == null) toUnload = LongArrayList()
                toUnload.add(index)
            }
            if (toUnload != null) {
                val arr: LongArray = toUnload.toLongArray()
                for (index in arr) {
                    val X = getHashX(index)
                    val Z = getHashZ(index)
                    if (this.unloadChunk(X, Z, true)) {
                        unloadQueue.remove(index)
                        if (System.currentTimeMillis() - now >= allocatedTime) {
                            result = false
                            break
                        }
                    }
                }
            }
            result
        } else {
            true
        }
    }

    @Override
    @Throws(Exception::class)
    fun setMetadata(metadataKey: String?, newMetadataValue: MetadataValue?) {
        server.getLevelMetadata().setMetadata(this, metadataKey, newMetadataValue)
    }

    @Override
    @Throws(Exception::class)
    fun getMetadata(metadataKey: String?): List<MetadataValue> {
        return server.getLevelMetadata().getMetadata(this, metadataKey)
    }

    @Override
    @Throws(Exception::class)
    fun hasMetadata(metadataKey: String?): Boolean {
        return server.getLevelMetadata().hasMetadata(this, metadataKey)
    }

    @Override
    @Throws(Exception::class)
    fun removeMetadata(metadataKey: String?, owningPlugin: Plugin?) {
        server.getLevelMetadata().removeMetadata(this, metadataKey, owningPlugin)
    }

    fun addPlayerMovement(entity: Entity, x: Double, y: Double, z: Double, yaw: Double, pitch: Double, headYaw: Double) {
        val pk = MovePlayerPacket()
        pk.eid = entity.getId()
        pk.x = x.toFloat()
        pk.y = y.toFloat()
        pk.z = z.toFloat()
        pk.yaw = yaw.toFloat()
        pk.headYaw = headYaw.toFloat()
        pk.pitch = pitch.toFloat()
        Server.broadcastPacket(entity.getViewers().values(), pk)
    }

    fun addEntityMovement(entity: Entity, x: Double, y: Double, z: Double, yaw: Double, pitch: Double, headYaw: Double) {
        val pk = MoveEntityAbsolutePacket()
        pk.eid = entity.getId()
        pk.x = x.toFloat()
        pk.y = y.toFloat()
        pk.z = z.toFloat()
        pk.yaw = yaw.toFloat()
        pk.headYaw = headYaw.toFloat()
        pk.pitch = pitch.toFloat()
        pk.onGround = entity.onGround
        Server.broadcastPacket(entity.getViewers().values(), pk)
    }

    fun setRaining(raining: Boolean): Boolean {
        val ev = WeatherChangeEvent(this, raining)
        getServer().getPluginManager().callEvent(ev)
        if (ev.isCancelled()) {
            return false
        }
        isRaining = raining
        val pk = LevelEventPacket()
        // These numbers are from Minecraft
        if (raining) {
            pk.evid = LevelEventPacket.EVENT_START_RAIN
            val time: Int = ThreadLocalRandom.current().nextInt(12000) + 12000
            pk.data = time
            rainTime = time
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_RAIN
            rainTime = ThreadLocalRandom.current().nextInt(168000) + 12000
        }
        Server.broadcastPacket(getPlayers().values(), pk)
        return true
    }

    fun isThundering(): Boolean {
        return isRaining && thundering
    }

    fun setThundering(thundering: Boolean): Boolean {
        val ev = ThunderChangeEvent(this, thundering)
        getServer().getPluginManager().callEvent(ev)
        if (ev.isCancelled()) {
            return false
        }
        if (thundering && !isRaining) {
            setRaining(true)
        }
        this.thundering = thundering
        val pk = LevelEventPacket()
        // These numbers are from Minecraft
        if (thundering) {
            pk.evid = LevelEventPacket.EVENT_START_THUNDER
            val time: Int = ThreadLocalRandom.current().nextInt(12000) + 3600
            pk.data = time
            thunderTime = time
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_THUNDER
            thunderTime = ThreadLocalRandom.current().nextInt(168000) + 12000
        }
        Server.broadcastPacket(getPlayers().values(), pk)
        return true
    }

    fun sendWeather(players: Array<Player?>?) {
        var players: Array<Player?>? = players
        if (players == null) {
            players = getPlayers().values().toArray(Player.EMPTY_ARRAY)
        }
        val pk = LevelEventPacket()
        if (isRaining) {
            pk.evid = LevelEventPacket.EVENT_START_RAIN
            pk.data = rainTime
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_RAIN
        }
        Server.broadcastPacket(players, pk)
        if (isThundering()) {
            pk.evid = LevelEventPacket.EVENT_START_THUNDER
            pk.data = thunderTime
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_THUNDER
        }
        Server.broadcastPacket(players, pk)
    }

    fun sendWeather(player: Player?) {
        if (player != null) {
            this.sendWeather(arrayOf<Player>(player))
        }
    }

    fun sendWeather(players: Collection<Player?>?) {
        var players: Collection<Player?>? = players
        if (players == null) {
            players = getPlayers().values()
        }
        this.sendWeather(players.toArray(Player.EMPTY_ARRAY))
    }

    fun canBlockSeeSky(pos: Vector3): Boolean {
        return getHighestBlockAt(pos.getFloorX(), pos.getFloorZ()) < pos.getY()
    }

    fun getStrongPower(pos: Vector3?, direction: BlockFace?): Int {
        return this.getBlock(pos).getStrongPower(direction)
    }

    @PowerNukkitDifference(info = "Check if the block to check is a piston, then return 0.", since = "1.4.0.0-PN")
    fun getStrongPower(pos: Vector3?): Int {
        if (pos is BlockPistonBase || this.getBlock(pos) is BlockPistonBase) return 0
        var i = 0
        for (face in BlockFace.values()) {
            i = Math.max(i, this.getStrongPower(temporalVector.setComponentsAdding(pos, face), face))
            if (i >= 15) {
                return i
            }
        }
        return i
        //        i = Math.max(i, this.getStrongPower(pos.down(), BlockFace.DOWN));
//
//        if (i >= 15) {
//            return i;
//        } else {
//            i = Math.max(i, this.getStrongPower(pos.up(), BlockFace.UP));
//
//            if (i >= 15) {
//                return i;
//            } else {
//                i = Math.max(i, this.getStrongPower(pos.north(), BlockFace.NORTH));
//
//                if (i >= 15) {
//                    return i;
//                } else {
//                    i = Math.max(i, this.getStrongPower(pos.south(), BlockFace.SOUTH));
//
//                    if (i >= 15) {
//                        return i;
//                    } else {
//                        i = Math.max(i, this.getStrongPower(pos.west(), BlockFace.WEST));
//
//                        if (i >= 15) {
//                            return i;
//                        } else {
//                            i = Math.max(i, this.getStrongPower(pos.east(), BlockFace.EAST));
//                            return i >= 15 ? i : i;
//                        }
//                    }
//                }
//            }
//        }
    }

    fun isSidePowered(pos: Vector3, face: BlockFace?): Boolean {
        return getRedstonePower(pos, face) > 0
    }

    fun getRedstonePower(pos: Vector3, face: BlockFace?): Int {
        var pos: Vector3 = pos
        val block: Block
        if (pos is Block) {
            block = pos as Block
            pos = pos.add(0)
        } else {
            block = this.getBlock(pos)
        }
        return if (block.isNormalBlock()) this.getStrongPower(pos) else block.getWeakPower(face)
    }

    fun isBlockPowered(pos: Vector3?): Boolean {
        for (face in BlockFace.values()) {
            if (getRedstonePower(temporalVector.setComponentsAdding(pos, face), face) > 0) {
                return true
            }
        }
        return false
    }

    fun isBlockIndirectlyGettingPowered(pos: Vector3?): Int {
        var power = 0
        for (face in BlockFace.values()) {
            val blockPower = getRedstonePower(temporalVector.setComponentsAdding(pos, face), face)
            if (blockPower >= 15) {
                return 15
            }
            if (blockPower > power) {
                power = blockPower
            }
        }
        return power
    }

    fun isAreaLoaded(bb: AxisAlignedBB): Boolean {
        if (bb.getMaxY() < 0 || bb.getMinY() >= 256) {
            return false
        }
        val minX: Int = NukkitMath.floorDouble(bb.getMinX()) shr 4
        val minZ: Int = NukkitMath.floorDouble(bb.getMinZ()) shr 4
        val maxX: Int = NukkitMath.floorDouble(bb.getMaxX()) shr 4
        val maxZ: Int = NukkitMath.floorDouble(bb.getMaxZ()) shr 4
        for (x in minX..maxX) {
            for (z in minZ..maxZ) {
                if (!isChunkLoaded(x, z)) {
                    return false
                }
            }
        }
        return true
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    fun createPortal(target: Block): Boolean {
        val maxPortalSize = 23
        val targX: Int = target.getFloorX()
        val targY: Int = target.getFloorY()
        val targZ: Int = target.getFloorZ()
        //check if there's air above (at least 3 blocks)
        for (i in 1..3) {
            if (this.getBlockIdAt(targX, targY + i, targZ) != BlockID.AIR) {
                return false
            }
        }
        var sizePosX = 0
        var sizeNegX = 0
        var sizePosZ = 0
        var sizeNegZ = 0
        for (i in 1 until maxPortalSize) {
            if (this.getBlockIdAt(targX + i, targY, targZ) == BlockID.OBSIDIAN) {
                sizePosX++
            } else {
                break
            }
        }
        for (i in 1 until maxPortalSize) {
            if (this.getBlockIdAt(targX - i, targY, targZ) == BlockID.OBSIDIAN) {
                sizeNegX++
            } else {
                break
            }
        }
        for (i in 1 until maxPortalSize) {
            if (this.getBlockIdAt(targX, targY, targZ + i) == BlockID.OBSIDIAN) {
                sizePosZ++
            } else {
                break
            }
        }
        for (i in 1 until maxPortalSize) {
            if (this.getBlockIdAt(targX, targY, targZ - i) == BlockID.OBSIDIAN) {
                sizeNegZ++
            } else {
                break
            }
        }
        //plus one for target block
        val sizeX = sizePosX + sizeNegX + 1
        val sizeZ = sizePosZ + sizeNegZ + 1
        if (sizeX >= 2 && sizeX <= maxPortalSize) {
            //start scan from 1 block above base
            //find pillar or end of portal to start scan
            var scanX = targX
            val scanY = targY + 1
            for (i in 0 until sizePosX + 1) {
                //this must be air
                if (this.getBlockIdAt(scanX + i, scanY, targZ) != BlockID.AIR) {
                    return false
                }
                if (this.getBlockIdAt(scanX + i + 1, scanY, targZ) == BlockID.OBSIDIAN) {
                    scanX += i
                    break
                }
            }
            //make sure that the above loop finished
            if (this.getBlockIdAt(scanX + 1, scanY, targZ) != BlockID.OBSIDIAN) {
                return false
            }
            var innerWidth = 0
            LOOP@ for (i in 0 until maxPortalSize - 2) {
                val id = this.getBlockIdAt(scanX - i, scanY, targZ)
                when (id) {
                    BlockID.AIR -> innerWidth++
                    BlockID.OBSIDIAN -> break@LOOP
                    else -> return false
                }
            }
            var innerHeight = 0
            LOOP@ for (i in 0 until maxPortalSize - 2) {
                val id = this.getBlockIdAt(scanX, scanY + i, targZ)
                when (id) {
                    BlockID.AIR -> innerHeight++
                    BlockID.OBSIDIAN -> break@LOOP
                    else -> return false
                }
            }
            if (!(innerWidth <= maxPortalSize - 2 && innerWidth >= 2 && innerHeight <= maxPortalSize - 2 && innerHeight >= 3)) {
                return false
            }
            for (height in 0 until innerHeight + 1) {
                if (height == innerHeight) {
                    for (width in 0 until innerWidth) {
                        if (this.getBlockIdAt(scanX - width, scanY + height, targZ) != BlockID.OBSIDIAN) {
                            return false
                        }
                    }
                } else {
                    if (this.getBlockIdAt(scanX + 1, scanY + height, targZ) != BlockID.OBSIDIAN
                            || this.getBlockIdAt(scanX - innerWidth, scanY + height, targZ) != BlockID.OBSIDIAN) {
                        return false
                    }
                    for (width in 0 until innerWidth) {
                        if (this.getBlockIdAt(scanX - width, scanY + height, targZ) != BlockID.AIR) {
                            return false
                        }
                    }
                }
            }
            for (height in 0 until innerHeight) {
                for (width in 0 until innerWidth) {
                    this.setBlock(Vector3(scanX - width, scanY + height, targZ), Block.get(BlockID.NETHER_PORTAL))
                }
            }
            this.addSound(target, Sound.FIRE_IGNITE)
            return true
        } else if (sizeZ >= 2 && sizeZ <= maxPortalSize) {
            //start scan from 1 block above base
            //find pillar or end of portal to start scan
            val scanY = targY + 1
            var scanZ = targZ
            for (i in 0 until sizePosZ + 1) {
                //this must be air
                if (this.getBlockIdAt(targX, scanY, scanZ + i) != BlockID.AIR) {
                    return false
                }
                if (this.getBlockIdAt(targX, scanY, scanZ + i + 1) == BlockID.OBSIDIAN) {
                    scanZ += i
                    break
                }
            }
            //make sure that the above loop finished
            if (this.getBlockIdAt(targX, scanY, scanZ + 1) != BlockID.OBSIDIAN) {
                return false
            }
            var innerWidth = 0
            LOOP@ for (i in 0 until maxPortalSize - 2) {
                val id = this.getBlockIdAt(targX, scanY, scanZ - i)
                when (id) {
                    BlockID.AIR -> innerWidth++
                    BlockID.OBSIDIAN -> break@LOOP
                    else -> return false
                }
            }
            var innerHeight = 0
            LOOP@ for (i in 0 until maxPortalSize - 2) {
                val id = this.getBlockIdAt(targX, scanY + i, scanZ)
                when (id) {
                    BlockID.AIR -> innerHeight++
                    BlockID.OBSIDIAN -> break@LOOP
                    else -> return false
                }
            }
            if (!(innerWidth <= maxPortalSize - 2 && innerWidth >= 2 && innerHeight <= maxPortalSize - 2 && innerHeight >= 3)) {
                return false
            }
            for (height in 0 until innerHeight + 1) {
                if (height == innerHeight) {
                    for (width in 0 until innerWidth) {
                        if (this.getBlockIdAt(targX, scanY + height, scanZ - width) != BlockID.OBSIDIAN) {
                            return false
                        }
                    }
                } else {
                    if (this.getBlockIdAt(targX, scanY + height, scanZ + 1) != BlockID.OBSIDIAN
                            || this.getBlockIdAt(targX, scanY + height, scanZ - innerWidth) != BlockID.OBSIDIAN) {
                        return false
                    }
                    for (width in 0 until innerWidth) {
                        if (this.getBlockIdAt(targX, scanY + height, scanZ - width) != BlockID.AIR) {
                            return false
                        }
                    }
                }
            }
            for (height in 0 until innerHeight) {
                for (width in 0 until innerWidth) {
                    this.setBlock(Vector3(targX, scanY + height, scanZ - width), Block.get(BlockID.NETHER_PORTAL))
                }
            }
            this.addSound(target, Sound.FIRE_IGNITE)
            return true
        }
        return false
    }

    @Override
    override fun toString(): String {
        return "Level{" +
                "folderName='" + folderName + '\'' +
                ", dimension=" + dimension +
                '}'
    }

    @AllArgsConstructor
    @Data
    private class QueuedUpdate {
        @Nonnull
        val block: Block? = null
        val neighbor: BlockFace? = null
    } //    private static void orderGetRidings(Entity entity, LongSet set) {

    //        if (entity.riding != null) {
    //            if(!set.add(entity.riding.getId())) {
    //                throw new RuntimeException("Circular entity link detected (id = "+entity.riding.getId()+")");
    //            }
    //            orderGetRidings(entity.riding, set);
    //        }
    //    }
    //
    //    public List<Entity> orderChunkEntitiesForSpawn(int chunkX, int chunkZ) {
    //        return orderChunkEntitiesForSpawn(getChunk(chunkX, chunkZ, false));
    //    }
    //
    //    public List<Entity> orderChunkEntitiesForSpawn(BaseFullChunk chunk) {
    //        Comparator<Entity> comparator = (o1, o2) -> {
    //            if (o1.riding == null) {
    //                if(o2 == null) {
    //                    return 0;
    //                }
    //
    //                return -1;
    //            }
    //
    //            if (o2.riding == null) {
    //                return 1;
    //            }
    //
    //            LongSet ridings = new LongOpenHashSet();
    //            orderGetRidings(o1, ridings);
    //
    //            if(ridings.contains(o2.getId())) {
    //                return 1;
    //            }
    //
    //            ridings.clear();
    //            orderGetRidings(o2, ridings);
    //
    //            if(ridings.contains(o1.getId())) {
    //                return -1;
    //            }
    //
    //            return 0;
    //        };
    //
    //        List<Entity> sorted = new ArrayList<>(chunk.getEntities().values());
    //        sorted.sort(comparator);
    //
    //        return sorted;
    //    }
    init {
        id = levelIdCounter++
        blockMetadata = BlockMetadataStore(this)
        this.server = server
        autoSave = server.getAutoSave()
        this.provider = provider.apply(this, path)
        val levelProvider: LevelProvider = requireProvider()
        timings = LevelTimings(this)
        levelProvider.updateLevelName(name)
        log.info(this.server.getLanguage().translateString("nukkit.level.preparing",
                TextFormat.GREEN + levelProvider.getName() + TextFormat.WHITE))
        generatorClass = Generator.getGenerator(levelProvider.getGenerator())
        useSections = usesChunkSection.getAsBoolean()
        folderName = name
        time = levelProvider.getTime()
        isRaining = levelProvider.isRaining()
        rainTime = requireProvider().getRainTime()
        if (rainTime <= 0) {
            rainTime = ThreadLocalRandom.current().nextInt(168000) + 12000
        }
        thundering = levelProvider.isThundering()
        thunderTime = levelProvider.getThunderTime()
        if (thunderTime <= 0) {
            thunderTime = ThreadLocalRandom.current().nextInt(168000) + 12000
        }
        currentTick = levelProvider.getCurrentTick()
        updateQueue = BlockUpdateScheduler(this, currentTick)
        chunkTickRadius = Math.min(this.server.getViewDistance(),
                Math.max(1, this.server.getConfig("chunk-ticking.tick-radius", 4)))
        chunksPerTicks = this.server.getConfig("chunk-ticking.per-tick", 40)
        chunkGenerationQueueSize = this.server.getConfig("chunk-generation.queue-size", 8)
        chunkPopulationQueueSize = this.server.getConfig("chunk-generation.population-queue-size", 2)
        chunkTickList.clear()
        clearChunksOnTick = this.server.getConfig("chunk-ticking.clear-tick-list", true)
        cacheChunks = this.server.getConfig("chunk-sending.cache-chunks", false)
        temporalPosition = Position(0, 0, 0, this)
        temporalVector = Vector3(0, 0, 0)
        tickRate = 1
        skyLightSubtracted = calculateSkylightSubtracted(1f).toFloat()
    }
}