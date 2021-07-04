package cn.nukkit

import cn.nukkit.AdventureSettings.Type

/**
 * @author MagicDroidX &amp; Box (Nukkit Project)
 */
@Log4j2
class Player @PowerNukkitOnly constructor(interfaz: SourceInterface, clientID: Long?, socketAddress: InetSocketAddress) : EntityHuman(null, CompoundTag()), CommandSender, InventoryHolder, ChunkLoader, IPlayer {
    protected val interfaz: SourceInterface
    var playedBefore = false
    var spawned = false
    var loggedIn = false

    @Since("1.4.0.0-PN")
    var locallyInitialized = false
    var gamemode: Int
    var lastBreak: Long
    private var lastBreakPosition: BlockVector3 = BlockVector3()
    protected var windowCnt = 4
    protected val windows: BiMap<Inventory, Integer> = HashBiMap.create()
    protected val windowIndex: BiMap<Integer, Inventory> = windows.inverse()
    protected val permanentWindows: Set<Integer> = IntOpenHashSet()
    private var inventoryOpen = false

    @get:Since("1.4.0.0-PN")
    @Since("1.4.0.0-PN")
    var closingWindowId: Int = Integer.MIN_VALUE
        protected set
    protected var messageCounter = 2
    val clientSecret: String? = null
    var speed: Vector3? = null
    val achievements: HashSet<String> = HashSet()
    var craftingType = CRAFTING_SMALL
    protected var playerUIInventory: PlayerUIInventory? = null
    protected var craftingGrid: CraftingGrid? = null
    protected var craftingTransaction: CraftingTransaction? = null

    @Since("1.3.1.0-PN")
    protected var enchantTransaction: EnchantTransaction? = null

    @Since("1.4.0.0-PN")
    protected var repairItemTransaction: RepairItemTransaction? = null

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    protected var grindstoneTransaction: GrindstoneTransaction? = null

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    protected var smithingTransaction: SmithingTransaction? = null
    var creationTime: Long = 0

    /**
     * This might disappear in the future.
     * Please use getUniqueId() instead (IP + clientId + name combo, in the future it'll change to real UUID for online auth)
     * @return random client id
     */
    @get:Deprecated
    var clientId: Long = 0
        protected set
    protected var forceMovement: Vector3? = null
    protected var teleportPosition: Vector3? = null
    var isConnected = true
        protected set
    protected val socketAddress: InetSocketAddress
    var removeFormat = true
    override var name: String? = null
        protected set
    protected var iusername: String? = null
    protected var displayName: String? = null
    var startActionTick = -1
        protected set
    protected var sleeping: Vector3? = null
    protected var clientID: Long? = null

    @get:Override
    val loaderId: Int
    protected var stepHeight = 0.6f
    val usedChunks: Map<Long, Boolean> = Long2ObjectOpenHashMap()
    protected var chunkLoadCount = 0
    protected val loadQueue: Long2ObjectLinkedOpenHashMap<Boolean> = Long2ObjectLinkedOpenHashMap()
    protected var nextChunkOrderRun = 1
    protected val hiddenPlayers: Map<UUID, Player> = HashMap()
    protected var newPosition: Vector3? = null
    protected var chunkRadius: Int
    protected var viewDistance: Int
    protected val chunksPerTick: Int
    protected val spawnThreshold: Int
    protected var spawnPosition: Position? = null

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected var spawnBlockPosition: Vector3? = null
    var inAirTicks = 0
        protected set
    protected var startAirTicks = 5

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    var noShieldTicks = 0
    protected var adventureSettings: AdventureSettings? = null

    /**
     * @since 1.2.1.0-PN
     */
    var isCheckingMovement = true
        protected set
    private var perm: PermissibleBase? = null
    private var exp = 0
    var experienceLevel = 0
        private set
    protected var foodData: PlayerFood? = null
    private var killer: Entity? = null

    @get:Synchronized
    @set:Synchronized
    var locale: AtomicReference<Locale> = AtomicReference(null)
        get() = field.get()
        set(locale) {
            this.locale.set(locale)
        }
    private var hash = 0
    var buttonText = "Button"
        set(text) {
            field = text
            this.setDataProperty(StringEntityData(Entity.DATA_INTERACT_TEXT, buttonText))
        }
    protected var enableClientCommand = true
    var viewingEnderChest: BlockEnderChest? = null
        set(chest) {
            if (chest == null && viewingEnderChest != null) {
                viewingEnderChest.getViewers().remove(this)
            } else if (chest != null) {
                chest.getViewers().add(this)
            }
            field = chest
        }
    var lastEnderPearlThrowingTick = 20
        protected set
    var lastChorusFruitTeleport = 20
        protected set
    private var loginChainData: LoginChainData? = null
    var breakingBlock: Block? = null
    var pickedXPOrb = 0
    protected var formWindowCount = 0
    protected var formWindows: Map<Integer, FormWindow> = Int2ObjectOpenHashMap()
    protected var serverSettings: Map<Integer, FormWindow> = Int2ObjectOpenHashMap()
    protected var dummyBossBars: Map<Long, DummyBossBar> = Long2ObjectLinkedOpenHashMap()
    private var preLoginEventTask: AsyncTask? = null
    protected var shouldLogin = false
    var fishing: EntityFishingHook? = null
    var lastSkinChange: Long
    protected var lastRightClickTime = 0.0
    protected var lastRightClickPos: Vector3? = null

    @get:Since("1.4.0.0-PN")
    @set:Since("1.4.0.0-PN")
    var timeSinceRest = 0

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected var lastPlayerdLevelUpSoundTime = 0
    private var delayedPosTrackingUpdate: TaskHandler? = null

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    var soulSpeedMultiplier = 1f
        private set
    private var wasInSoulSandCompatible = false
    fun startAction() {
        startActionTick = server.getTick()
    }

    fun stopAction() {
        startActionTick = -1
    }

    fun onThrowEnderPearl() {
        lastEnderPearlThrowingTick = server.getTick()
    }

    fun onChorusFruitTeleport() {
        lastChorusFruitTeleport = server.getTick()
    }

    val leaveMessage: TranslationContainer
        get() = TranslationContainer(TextFormat.YELLOW.toString() + "%multiplayer.player.left", getDisplayName())

    @get:Override
    @set:Override
    override var isBanned: Boolean
        get() = server.getNameBans().isBanned(name)
        set(value) {
            if (value) {
                server.getNameBans().addBan(name, null, null, null)
                this.kick(PlayerKickEvent.Reason.NAME_BANNED, "Banned by admin")
            } else {
                server.getNameBans().remove(name)
            }
        }

    @get:Override
    @set:Override
    override var isWhitelisted: Boolean
        get() = server!!.isWhitelisted(name.toLowerCase())
        set(value) {
            if (value) {
                server!!.addWhitelist(name.toLowerCase())
            } else {
                server!!.removeWhitelist(name.toLowerCase())
            }
        }

    @get:Override
    override val player: Player?
        get() = this

    @get:Override
    override val firstPlayed: Long?
        get() = if (this.namedTag != null) this.namedTag.getLong("firstPlayed") else null

    @get:Override
    override val lastPlayed: Long?
        get() = if (this.namedTag != null) this.namedTag.getLong("lastPlayed") else null

    @Override
    override fun hasPlayedBefore(): Boolean {
        return playedBefore
    }

    fun getAdventureSettings(): AdventureSettings? {
        return adventureSettings
    }

    fun setAdventureSettings(adventureSettings: AdventureSettings) {
        this.adventureSettings = adventureSettings.clone(this)
        this.adventureSettings!!.update()
    }

    fun resetInAirTicks() {
        inAirTicks = 0
    }

    @get:Deprecated
    @set:Deprecated
    var allowFlight: Boolean
        get() = getAdventureSettings()!!.get(Type.ALLOW_FLIGHT)
        set(value) {
            getAdventureSettings()!!.set(Type.ALLOW_FLIGHT, value)
            getAdventureSettings()!!.update()
        }

    fun setAllowModifyWorld(value: Boolean) {
        getAdventureSettings()!!.set(Type.WORLD_IMMUTABLE, !value)
        getAdventureSettings()!!.set(Type.BUILD_AND_MINE, value)
        getAdventureSettings()!!.set(Type.WORLD_BUILDER, value)
        getAdventureSettings()!!.update()
    }

    fun setAllowInteract(value: Boolean) {
        setAllowInteract(value, value)
    }

    fun setAllowInteract(value: Boolean, containers: Boolean) {
        getAdventureSettings()!!.set(Type.WORLD_IMMUTABLE, !value)
        getAdventureSettings()!!.set(Type.DOORS_AND_SWITCHED, value)
        getAdventureSettings()!!.set(Type.OPEN_CONTAINERS, containers)
        getAdventureSettings()!!.update()
    }

    @Deprecated
    fun setAutoJump(value: Boolean) {
        getAdventureSettings()!!.set(Type.AUTO_JUMP, value)
        getAdventureSettings()!!.update()
    }

    @Deprecated
    fun hasAutoJump(): Boolean {
        return getAdventureSettings()!!.get(Type.AUTO_JUMP)
    }

    @Override
    fun spawnTo(player: Player) {
        if (spawned && player.spawned && this.isAlive() && player.getLevel() === this.level && player.canSee(this) && !isSpectator) {
            super.spawnTo(player)
        }
    }

    @get:Override
    override val server: cn.nukkit.Server?
    fun setRemoveFormat() {
        removeFormat = true
    }

    fun canSee(player: Player): Boolean {
        return !hiddenPlayers.containsKey(player.getUniqueId())
    }

    fun hidePlayer(player: Player) {
        if (this === player) {
            return
        }
        hiddenPlayers.put(player.getUniqueId(), player)
        player.despawnFrom(this)
    }

    fun showPlayer(player: Player) {
        if (this === player) {
            return
        }
        hiddenPlayers.remove(player.getUniqueId())
        if (player.isOnline) {
            player.spawnTo(this)
        }
    }

    @Override
    fun canCollideWith(entity: Entity?): Boolean {
        return false
    }

    @Override
    fun resetFallDistance() {
        super.resetFallDistance()
        if (inAirTicks != 0) {
            startAirTicks = 5
        }
        inAirTicks = 0
        this.highestPosition = this.y
    }

    @get:Override
    override val isOnline: Boolean
        get() = isConnected && loggedIn

    @get:Override
    @set:Override
    var isOp: Boolean
        get() = server!!.isOp(name)
        set(value) {
            if (value == isOp) {
                return
            }
            if (value) {
                server!!.addOp(name!!)
            } else {
                server!!.removeOp(name!!)
            }
            recalculatePermissions()
            getAdventureSettings()!!.update()
            sendCommandData()
        }

    @Override
    fun isPermissionSet(name: String?): Boolean {
        return perm.isPermissionSet(name)
    }

    @Override
    fun isPermissionSet(permission: Permission?): Boolean {
        return perm.isPermissionSet(permission)
    }

    @Override
    fun hasPermission(name: String?): Boolean {
        return perm != null && perm.hasPermission(name)
    }

    @Override
    fun hasPermission(permission: Permission?): Boolean {
        return perm.hasPermission(permission)
    }

    @Override
    fun addAttachment(plugin: Plugin?): PermissionAttachment {
        return this.addAttachment(plugin, null)
    }

    @Override
    fun addAttachment(plugin: Plugin?, name: String?): PermissionAttachment {
        return this.addAttachment(plugin, name, null)
    }

    @Override
    fun addAttachment(plugin: Plugin?, name: String?, value: Boolean?): PermissionAttachment {
        return perm.addAttachment(plugin, name, value)
    }

    @Override
    fun removeAttachment(attachment: PermissionAttachment?) {
        perm.removeAttachment(attachment)
    }

    @Override
    fun recalculatePermissions() {
        server!!.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_USERS, this)
        server!!.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this)
        if (perm == null) {
            return
        }
        perm.recalculatePermissions()
        if (this.hasPermission(Server.BROADCAST_CHANNEL_USERS)) {
            server!!.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_USERS, this)
        }
        if (this.hasPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE)) {
            server!!.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this)
        }
        if (isEnableClientCommand() && spawned) sendCommandData()
    }

    fun isEnableClientCommand(): Boolean {
        return enableClientCommand
    }

    fun setEnableClientCommand(enable: Boolean) {
        enableClientCommand = enable
        val pk = SetCommandsEnabledPacket()
        pk.enabled = enable
        this.dataPacket(pk)
        if (enable) sendCommandData()
    }

    fun sendCommandData() {
        if (!spawned) {
            return
        }
        val pk = AvailableCommandsPacket()
        val data: Map<String, CommandDataVersions> = HashMap()
        var count = 0
        for (command in server!!.getCommandMap().getCommands().values()) {
            if (!command.testPermissionSilent(this)) {
                continue
            }
            ++count
            val data0: CommandDataVersions = command.generateCustomCommandData(this)
            data.put(command.getName(), data0)
        }
        if (count > 0) {
            //TODO: structure checking
            pk.commands = data
            this.dataPacket(pk)
        }
    }

    @get:Override
    val effectivePermissions: Map<String, Any>
        get() = perm.getEffectivePermissions()

    constructor(interfaz: SourceInterface, clientID: Long?, ip: String, port: Int) : this(interfaz, clientID, uncheckedNewInetSocketAddress(ip, port)) {}

    @Override
    protected fun initEntity() {
        super.initEntity()
        addDefaultWindows()
    }

    fun isPlayer(): Boolean {
        return true
    }

    fun removeAchievement(achievementId: String?) {
        achievements.remove(achievementId)
    }

    fun hasAchievement(achievementId: String?): Boolean {
        return achievements.contains(achievementId)
    }

    fun getDisplayName(): String? {
        return displayName
    }

    fun setDisplayName(displayName: String?) {
        this.displayName = displayName
        if (spawned) {
            server.updatePlayerListData(this.getUniqueId(), this.getId(), getDisplayName(), this.getSkin(), getLoginChainData().getXUID())
        }
    }

    @Override
    fun setSkin(skin: Skin?) {
        super.setSkin(skin)
        if (spawned) {
            server.updatePlayerListData(this.getUniqueId(), this.getId(), getDisplayName(), skin, getLoginChainData().getXUID())
        }
    }

    val address: String
        get() = socketAddress.getAddress().getHostAddress()
    val port: Int
        get() = socketAddress.getPort()

    fun getSocketAddress(): InetSocketAddress {
        return socketAddress
    }

    val nextPosition: Position
        get() = if (newPosition != null) Position(newPosition.x, newPosition.y, newPosition.z, this.level) else this.getPosition()

    fun isSleeping(): Boolean {
        return sleeping != null
    }

    /**
     * Returns whether the player is currently using an item (right-click and hold).
     *
     * @return bool
     */
    var isUsingItem: Boolean
        get() = this.getDataFlag(DATA_FLAGS, DATA_FLAG_ACTION) && startActionTick > -1
        set(value) {
            startActionTick = if (value) server.getTick() else -1
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, value)
        }

    @JvmOverloads
    fun unloadChunk(x: Int, z: Int, level: Level? = null) {
        var level: Level? = level
        level = if (level == null) this.level else level
        val index: Long = Level.chunkHash(x, z)
        if (usedChunks.containsKey(index)) {
            for (entity in level.getChunkEntities(x, z).values()) {
                if (entity !== this) {
                    entity.despawnFrom(this)
                }
            }
            usedChunks.remove(index)
        }
        level.unregisterChunkLoader(this, x, z)
        loadQueue.remove(index)
    }

    var spawn: Position?
        get() = if (spawnPosition != null && spawnPosition.getLevel() != null) {
            spawnPosition
        } else {
            server.getDefaultLevel().getSafeSpawn()
        }
        set(pos) {
            val level: Level
            if (pos !is Position) {
                level = level
            } else {
                level = (pos as Position).getLevel()
            }
            spawnPosition = Position(pos.x, pos.y, pos.z, level)
            val pk = SetSpawnPositionPacket()
            pk.spawnType = SetSpawnPositionPacket.TYPE_PLAYER_SPAWN
            pk.x = spawnPosition.x
            pk.y = spawnPosition.y
            pk.z = spawnPosition.z
            pk.dimension = spawnPosition.level.getDimension()
            this.dataPacket(pk)
        }
    /**
     * The block that holds the player respawn position. May be null when unknown.
     * @return The position of a bed, respawn anchor, or null when unknown.
     */
    /**
     * Sets the position of the block that holds the player respawn position. May be null when unknown.
     * @param spawnBlock The position of a bed or respawn anchor
     */
    @get:Nullable
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var spawnBlock: Vector3?
        get() = spawnBlockPosition
        set(spawnBlock) {
            if (spawnBlock == null) {
                spawnBlockPosition = null
            } else {
                spawnBlockPosition = Vector3(spawnBlock.x, spawnBlock.y, spawnBlock.z)
            }
        }

    fun sendChunk(x: Int, z: Int, packet: DataPacket) {
        if (!isConnected) {
            return
        }
        usedChunks.put(Level.chunkHash(x, z), Boolean.TRUE)
        chunkLoadCount++
        this.dataPacket(packet)
        if (spawned) {
            for (entity in this.level.getChunkEntities(x, z).values()) {
                if (this !== entity && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this)
                }
            }
        }
    }

    fun sendChunk(x: Int, z: Int, subChunkCount: Int, payload: ByteArray) {
        if (!isConnected) {
            return
        }
        usedChunks.put(Level.chunkHash(x, z), true)
        chunkLoadCount++
        val pk = LevelChunkPacket()
        pk.chunkX = x
        pk.chunkZ = z
        pk.subChunkCount = subChunkCount
        pk.data = payload
        this.dataPacket(pk)
        if (spawned) {
            for (entity in this.level.getChunkEntities(x, z).values()) {
                if (this !== entity && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this)
                }
            }
        }
    }

    protected fun sendNextChunk() {
        if (!isConnected) {
            return
        }
        Timings.playerChunkSendTimer.startTiming()
        if (!loadQueue.isEmpty()) {
            var count = 0
            val iter: ObjectIterator<Long2ObjectMap.Entry<Boolean>> = loadQueue.long2ObjectEntrySet().fastIterator()
            while (iter.hasNext()) {
                val entry: Long2ObjectMap.Entry<Boolean> = iter.next()
                val index: Long = entry.getLongKey()
                if (count >= chunksPerTick) {
                    break
                }
                val chunkX: Int = Level.getHashX(index)
                val chunkZ: Int = Level.getHashZ(index)
                ++count
                usedChunks.put(index, false)
                this.level.registerChunkLoader(this, chunkX, chunkZ, false)
                if (!this.level.populateChunk(chunkX, chunkZ)) {
                    if (spawned && teleportPosition == null) {
                        continue
                    } else {
                        break
                    }
                }
                iter.remove()
                val ev = PlayerChunkRequestEvent(this, chunkX, chunkZ)
                server!!.getPluginManager().callEvent(ev)
                if (!ev.isCancelled()) {
                    this.level.requestChunk(chunkX, chunkZ, this)
                }
            }
        }
        if (chunkLoadCount >= spawnThreshold && !spawned && teleportPosition == null) {
            doFirstSpawn()
        }
        Timings.playerChunkSendTimer.stopTiming()
    }

    protected fun doFirstSpawn() {
        spawned = true
        this.inventory.sendContents(this)
        this.inventory.sendHeldItem(this)
        this.inventory.sendArmorContents(this)
        this.offhandInventory.sendContents(this)
        setEnableClientCommand(true)
        val setTimePacket = SetTimePacket()
        setTimePacket.time = this.level.getTime()
        this.dataPacket(setTimePacket)
        var pos: Location
        if (server!!.isSafeSpawn()) {
            pos = this.level.getSafeSpawn(this).getLocation()
            pos.yaw = this.yaw
            pos.pitch = this.pitch
        } else {
            pos = Location(forceMovement.x, forceMovement.y, forceMovement.z, this.yaw, this.pitch, this.level)
        }
        val respawnEvent = PlayerRespawnEvent(this, pos, true)
        server!!.getPluginManager().callEvent(respawnEvent)
        val fromEvent: Position = respawnEvent.getRespawnPosition()
        if (fromEvent is Location) {
            pos = fromEvent.getLocation()
        } else {
            pos = fromEvent.getLocation()
            pos.yaw = this.yaw
            pos.pitch = this.pitch
        }
        teleport(pos, null)
        lastYaw = yaw
        lastPitch = pitch
        sendPlayStatus(PlayStatusPacket.PLAYER_SPAWN)
        val playerJoinEvent = PlayerJoinEvent(this,
                TranslationContainer(TextFormat.YELLOW.toString() + "%multiplayer.player.joined", arrayOf(
                        getDisplayName()
                ))
        )
        server!!.getPluginManager().callEvent(playerJoinEvent)
        if (playerJoinEvent.getJoinMessage().toString().trim().length() > 0) {
            server.broadcastMessage(playerJoinEvent.getJoinMessage())
        }
        this.noDamageTicks = 60
        server!!.sendRecipeList(this)
        for (index in usedChunks.keySet()) {
            val chunkX: Int = Level.getHashX(index)
            val chunkZ: Int = Level.getHashZ(index)
            for (entity in this.level.getChunkEntities(chunkX, chunkZ).values()) {
                if (this !== entity && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this)
                }
            }
        }
        val experience = experience
        if (experience != 0) {
            sendExperience(experience)
        }
        val level = experienceLevel
        if (level != 0) {
            sendExperienceLevel(experienceLevel)
        }
        if (!isSpectator) {
            this.spawnToAll()
        }

        //todo Updater

        //Weather
        this.getLevel().sendWeather(this)

        //FoodLevel
        val food: PlayerFood? = getFoodData()
        if (food.getLevel() !== food.getMaxLevel()) {
            food!!.sendFoodLevel()
        }
        if (this.getHealth() < 1) {
            respawn()
        } else {
            updateTrackingPositions(false)
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun updateTrackingPositions() {
        updateTrackingPositions(false)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun updateTrackingPositions(delayed: Boolean) {
        val server: Server = server!!
        if (delayed) {
            if (delayedPosTrackingUpdate != null) {
                delayedPosTrackingUpdate.cancel()
            }
            delayedPosTrackingUpdate = server.getScheduler().scheduleDelayedTask(null, this::updateTrackingPositions, 10)
            return
        }
        val positionTrackingService: PositionTrackingService = server.getPositionTrackingService()
        positionTrackingService.forceRecheck(this)
    }

    protected fun orderChunks(): Boolean {
        if (!isConnected) {
            return false
        }
        Timings.playerChunkOrderTimer.startTiming()
        nextChunkOrderRun = 200
        loadQueue.clear()
        val lastChunk: Long2ObjectOpenHashMap<Boolean> = Long2ObjectOpenHashMap(usedChunks)
        val centerX = this.x as Int shr 4
        val centerZ = this.z as Int shr 4
        val radius = if (spawned) chunkRadius else Math.ceil(Math.sqrt(spawnThreshold))
        val radiusSqr = radius * radius
        var index: Long
        for (x in 0..radius) {
            val xx = x * x
            for (z in 0..x) {
                val distanceSqr = xx + z * z
                if (distanceSqr > radiusSqr) continue

                /* Top right quadrant */if (usedChunks[Level.chunkHash(centerX + x, centerZ + z).also { index = it }] !== Boolean.TRUE) {
                    loadQueue.put(index, Boolean.TRUE)
                }
                lastChunk.remove(index)
                /* Top left quadrant */if (usedChunks[Level.chunkHash(centerX - x - 1, centerZ + z).also { index = it }] !== Boolean.TRUE) {
                    loadQueue.put(index, Boolean.TRUE)
                }
                lastChunk.remove(index)
                /* Bottom right quadrant */if (usedChunks[Level.chunkHash(centerX + x, centerZ - z - 1).also { index = it }] !== Boolean.TRUE) {
                    loadQueue.put(index, Boolean.TRUE)
                }
                lastChunk.remove(index)
                /* Bottom left quadrant */if (usedChunks[Level.chunkHash(centerX - x - 1, centerZ - z - 1).also { index = it }] !== Boolean.TRUE) {
                    loadQueue.put(index, Boolean.TRUE)
                }
                lastChunk.remove(index)
                if (x != z) {
                    /* Top right quadrant mirror */
                    if (usedChunks[Level.chunkHash(centerX + z, centerZ + x).also { index = it }] !== Boolean.TRUE) {
                        loadQueue.put(index, Boolean.TRUE)
                    }
                    lastChunk.remove(index)
                    /* Top left quadrant mirror */if (usedChunks[Level.chunkHash(centerX - z - 1, centerZ + x).also { index = it }] !== Boolean.TRUE) {
                        loadQueue.put(index, Boolean.TRUE)
                    }
                    lastChunk.remove(index)
                    /* Bottom right quadrant mirror */if (usedChunks[Level.chunkHash(centerX + z, centerZ - x - 1).also { index = it }] !== Boolean.TRUE) {
                        loadQueue.put(index, Boolean.TRUE)
                    }
                    lastChunk.remove(index)
                    /* Bottom left quadrant mirror */if (usedChunks[Level.chunkHash(centerX - z - 1, centerZ - x - 1).also { index = it }] !== Boolean.TRUE) {
                        loadQueue.put(index, Boolean.TRUE)
                    }
                    lastChunk.remove(index)
                }
            }
        }
        val keys: LongIterator = lastChunk.keySet().iterator()
        while (keys.hasNext()) {
            index = keys.nextLong()
            unloadChunk(Level.getHashX(index), Level.getHashZ(index))
        }
        if (!loadQueue.isEmpty()) {
            val packet = NetworkChunkPublisherUpdatePacket()
            packet.position = this.asBlockVector3()
            packet.radius = viewDistance shl 4
            this.dataPacket(packet)
        }
        Timings.playerChunkOrderTimer.stopTiming()
        return true
    }

    @DeprecationDetails(by = "Cloudburst Nukkit", since = "1.4.0.0-PN", replaceWith = "dataPacket(DataPacket)", reason = "Batching packet is now handled near the RakNet layer")
    @Deprecated
    fun batchDataPacket(packet: DataPacket): Boolean {
        return this.dataPacket(packet)
    }

    /**
     * 0 is true
     * -1 is false
     * other is identifer
     * @param packet packet to send
     * @return packet successfully sent
     */
    fun dataPacket(packet: DataPacket): Boolean {
        if (!isConnected) {
            return false
        }
        Timings.getSendDataPacketTiming(packet).use { ignored ->
            val ev = DataPacketSendEvent(this, packet)
            server!!.getPluginManager().callEvent(ev)
            if (ev.isCancelled()) {
                return false
            }
            if (log.isTraceEnabled() && !server!!.isIgnoredPacket(packet.getClass())) {
                log.trace("Outbound {}: {}", name, packet)
            }
            interfaz.putPacket(this, packet, false, false)
        }
        return true
    }

    @DeprecationDetails(by = "Cloudburst Nukkit", since = "2019-05-08", replaceWith = "dataPacket(DataPacket)", reason = "ACKs are handled by the RakNet layer only")
    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Cloudburst changed the return values from 0/-1 to 1/0, breaking backward compatibility for no reason, " +
            "we reversed that.")
    @Deprecated
    fun dataPacket(packet: DataPacket, needACK: Boolean): Int {
        return if (dataPacket(packet)) 0 else -1
    }

    /**
     * 0 is true
     * -1 is false
     * other is identifer
     * @param packet packet to send
     * @return packet successfully sent
     */
    @Deprecated
    @DeprecationDetails(by = "Cloudburst Nukkit", since = "1.4.0.0-PN", replaceWith = "dataPacket(DataPacket)", reason = "Direct packets are no longer allowed")
    fun directDataPacket(packet: DataPacket): Boolean {
        return this.dataPacket(packet)
    }

    @DeprecationDetails(by = "Cloudburst Nukkit", since = "2019-05-08", replaceWith = "dataPacket(DataPacket)", reason = "ACK are handled by the RakNet layer and direct packets are no longer allowed")
    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Cloudburst changed the return values from 0/-1 to 1/0, breaking backward compatibility for no reason, " +
            "we reversed that.")
    @Deprecated
    fun directDataPacket(packet: DataPacket, needACK: Boolean): Int {
        return if (this.dataPacket(packet)) 0 else -1
    }

    val ping: Int
        get() = interfaz.getNetworkLatency(this)

    fun sleepOn(pos: Vector3): Boolean {
        if (!isOnline) {
            return false
        }
        for (p in this.level.getNearbyEntities(this.boundingBox.grow(2, 1, 2), this)) {
            if (p is Player) {
                if ((p as Player).sleeping != null && pos.distance((p as Player).sleeping) <= 0.1) {
                    return false
                }
            }
        }
        var ev: PlayerBedEnterEvent
        server!!.getPluginManager().callEvent(PlayerBedEnterEvent(this, this.level.getBlock(pos)).also { ev = it })
        if (ev.isCancelled()) {
            return false
        }
        sleeping = pos.clone()
        teleport(Location(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, this.yaw, this.pitch, this.level), null)
        this.setDataProperty(IntPositionEntityData(DATA_PLAYER_BED_POSITION, pos.x as Int, pos.y as Int, pos.z as Int))
        this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, true)
        spawn = pos
        this.level.sleepTicks = 60
        timeSinceRest = 0
        return true
    }

    fun stopSleep() {
        if (sleeping != null) {
            server!!.getPluginManager().callEvent(PlayerBedLeaveEvent(this, this.level.getBlock(sleeping)))
            sleeping = null
            this.setDataProperty(IntPositionEntityData(DATA_PLAYER_BED_POSITION, 0, 0, 0))
            this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, false)
            this.level.sleepTicks = 0
            val pk = AnimatePacket()
            pk.eid = this.id
            pk.action = AnimatePacket.Action.WAKE_UP
            this.dataPacket(pk)
        }
    }

    fun awardAchievement(achievementId: String?): Boolean {
        if (!Server.getInstance().getPropertyBoolean("achievements", true)) {
            return false
        }
        val achievement: Achievement = Achievement.achievements.get(achievementId)
        if (achievement == null || hasAchievement(achievementId)) {
            return false
        }
        for (id in achievement.requires) {
            if (!hasAchievement(id)) {
                return false
            }
        }
        val event = PlayerAchievementAwardedEvent(this, achievementId)
        server!!.getPluginManager().callEvent(event)
        if (event.isCancelled()) {
            return false
        }
        achievements.add(achievementId)
        achievement.broadcast(this)
        return true
    }

    fun setGamemode(gamemode: Int): Boolean {
        return this.setGamemode(gamemode, false, null)
    }

    fun setGamemode(gamemode: Int, clientSide: Boolean): Boolean {
        return this.setGamemode(gamemode, clientSide, null)
    }

    fun setGamemode(gamemode: Int, clientSide: Boolean, newSettings: AdventureSettings?): Boolean {
        var newSettings: AdventureSettings? = newSettings
        if (gamemode < 0 || gamemode > 3 || this.gamemode == gamemode) {
            return false
        }
        if (newSettings == null) {
            newSettings = getAdventureSettings()!!.clone(this)
            newSettings!!.set(Type.WORLD_IMMUTABLE, gamemode and 0x02 > 0)
            newSettings!!.set(Type.BUILD_AND_MINE, gamemode and 0x02 <= 0)
            newSettings!!.set(Type.WORLD_BUILDER, gamemode and 0x02 <= 0)
            newSettings!!.set(Type.ALLOW_FLIGHT, gamemode and 0x01 > 0)
            newSettings!!.set(Type.NO_CLIP, gamemode == 0x03)
            newSettings!!.set(Type.FLYING, gamemode == 0x03)
        }
        var ev: PlayerGameModeChangeEvent
        server!!.getPluginManager().callEvent(PlayerGameModeChangeEvent(this, gamemode, newSettings).also { ev = it })
        if (ev.isCancelled()) {
            return false
        }
        this.gamemode = gamemode
        if (isSpectator) {
            this.keepMovement = true
            this.despawnFromAll()
        } else {
            this.keepMovement = false
            this.spawnToAll()
        }
        this.namedTag.putInt("playerGameType", this.gamemode)
        if (!clientSide) {
            val pk = SetPlayerGameTypePacket()
            pk.gamemode = getClientFriendlyGamemode(gamemode)
            this.dataPacket(pk)
        }
        setAdventureSettings(ev.getNewAdventureSettings())
        if (isSpectator) {
            getAdventureSettings()!!.set(Type.FLYING, true)
            teleport(this.temporalVector.setComponents(this.x, this.y + 0.1, this.z))

            /*InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
            inventoryContentPacket.inventoryId = InventoryContentPacket.SPECIAL_CREATIVE;
            this.dataPacket(inventoryContentPacket);*/
        } else {
            if (isSurvival) {
                getAdventureSettings()!!.set(Type.FLYING, false)
            }
            /*InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
            inventoryContentPacket.inventoryId = InventoryContentPacket.SPECIAL_CREATIVE;
            inventoryContentPacket.slots = Item.getCreativeItems().toArray(new Item[0]);
            this.dataPacket(inventoryContentPacket);*/
        }
        resetFallDistance()
        this.inventory.sendContents(this)
        this.inventory.sendHeldItem(this.hasSpawned.values())
        this.offhandInventory.sendContents(this)
        this.offhandInventory.sendContents(this.getViewers().values())
        this.inventory.sendCreativeContents()
        return true
    }

    @Deprecated
    fun sendSettings() {
        getAdventureSettings()!!.update()
    }

    val isSurvival: Boolean
        get() = gamemode == SURVIVAL
    val isCreative: Boolean
        get() = gamemode == CREATIVE
    val isSpectator: Boolean
        get() = gamemode == SPECTATOR
    val isAdventure: Boolean
        get() = gamemode == ADVENTURE

    @get:Override
    val drops: Array<Any>
        get() = if (!isCreative && !isSpectator) {
            super.getDrops()
        } else Item.EMPTY_ARRAY

    @Override
    fun setDataProperty(data: EntityData): Boolean {
        return setDataProperty(data, true)
    }

    @Override
    fun setDataProperty(data: EntityData, send: Boolean): Boolean {
        if (super.setDataProperty(data, send)) {
            if (send) this.sendData(this, EntityMetadata().put(this.getDataProperty(data.getId())))
            return true
        }
        return false
    }

    @Override
    protected fun checkGroundState(movX: Double, movY: Double, movZ: Double, dx: Double, dy: Double, dz: Double) {
        if (!this.onGround || movX != 0.0 || movY != 0.0 || movZ != 0.0) {
            var onGround = false
            val bb: AxisAlignedBB = this.boundingBox.clone()
            bb.setMaxY(bb.getMinY() + 0.5)
            bb.setMinY(bb.getMinY() - 1)
            val realBB: AxisAlignedBB = this.boundingBox.clone()
            realBB.setMaxY(realBB.getMinY() + 0.1)
            realBB.setMinY(realBB.getMinY() - 0.2)
            val minX: Int = NukkitMath.floorDouble(bb.getMinX())
            val minY: Int = NukkitMath.floorDouble(bb.getMinY())
            val minZ: Int = NukkitMath.floorDouble(bb.getMinZ())
            val maxX: Int = NukkitMath.ceilDouble(bb.getMaxX())
            val maxY: Int = NukkitMath.ceilDouble(bb.getMaxY())
            val maxZ: Int = NukkitMath.ceilDouble(bb.getMaxZ())
            for (z in minZ..maxZ) {
                for (x in minX..maxX) {
                    for (y in minY..maxY) {
                        val block: Block = this.level.getBlock(this.temporalVector.setComponents(x, y, z))
                        if (!block.canPassThrough() && block.collidesWithBB(realBB)) {
                            onGround = true
                            break
                        }
                    }
                }
            }
            onGround = onGround
        }
        this.isCollided = this.onGround
    }

    @Override
    protected fun checkBlockCollision() {
        var portal = false
        var scaffolding = false
        var endPortal = false
        for (block in this.getCollisionBlocks()) {
            when (block.getId()) {
                BlockID.NETHER_PORTAL -> portal = true
                BlockID.SCAFFOLDING -> scaffolding = true
                BlockID.END_PORTAL -> endPortal = true
            }
            block.onEntityCollide(this)
            block.getLevelBlockAtLayer(1).onEntityCollide(this)
        }
        setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_IN_SCAFFOLDING, scaffolding)
        val scanBoundingBox: AxisAlignedBB = boundingBox.getOffsetBoundingBox(0, -0.125, 0)
        scanBoundingBox.setMaxY(boundingBox.getMinY())
        val scaffoldingUnder: Array<Block> = level.getCollisionBlocks(
                scanBoundingBox,
                true, true
        ) { b -> b.getId() === BlockID.SCAFFOLDING }
        setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_OVER_SCAFFOLDING, scaffoldingUnder.size > 0)
        if (endPortal) {
            if (!inEndPortal) {
                inEndPortal = true
                val ev = EntityPortalEnterEvent(this, PortalType.END)
                server!!.getPluginManager().callEvent(ev)
            }
        } else {
            inEndPortal = false
        }
        if (portal) {
            if (isCreative && this.inPortalTicks < 80) {
                this.inPortalTicks = 80
            } else {
                this.inPortalTicks++
            }
        } else {
            this.inPortalTicks = 0
        }
    }

    protected fun checkNearEntities() {
        for (entity in this.level.getNearbyEntities(this.boundingBox.grow(1, 0.5, 1), this)) {
            entity.scheduleUpdate()
            if (!entity.isAlive() || !this.isAlive()) {
                continue
            }
            pickupEntity(entity, true)
        }
    }

    protected fun processMovement(tickDiff: Int) {
        if (!this.isAlive() || !spawned || newPosition == null || teleportPosition != null || isSleeping()) {
            this.positionChanged = false
            return
        }
        val newPos: Vector3? = newPosition
        val distanceSquared: Double = newPos.distanceSquared(this)
        var revert = false
        if (distanceSquared / (tickDiff * tickDiff).toDouble() > 100 && newPos.y - this.y > -5) {
            revert = true
        } else {
            if (this.chunk == null || !this.chunk.isGenerated()) {
                var chunk: BaseFullChunk = this.level.getChunk(newPos.x as Int shr 4, newPos.z as Int shr 4, false)
                if (chunk == null || !chunk.isGenerated()) {
                    revert = true
                    nextChunkOrderRun = 0
                } else {
                    if (chunk != null) {
                        chunk.removeEntity(this)
                    }
                    chunk = chunk
                }
            }
        }
        val tdx: Double = newPos.x - this.x
        val tdz: Double = newPos.z - this.z
        var distance: Double = Math.sqrt(tdx * tdx + tdz * tdz)
        if (!revert && distanceSquared != 0.0) {
            val dx: Double = newPos.x - this.x
            val dy: Double = newPos.y - this.y
            val dz: Double = newPos.z - this.z
            this.fastMove(dx, dy, dz)
            if (newPosition == null) {
                return  //maybe solve that in better way
            }
            val diffX: Double = this.x - newPos.x
            var diffY: Double = this.y - newPos.y
            val diffZ: Double = this.z - newPos.z
            val yS: Double = 0.5 + this.ySize
            if (diffY >= -yS || diffY <= yS) {
                diffY = 0.0
            }
            if (diffX != 0.0 || diffY != 0.0 || diffZ != 0.0) {
                if (isCheckingMovement && !isOp && !server!!.getAllowFlight() && (isSurvival || isAdventure)) {
                    // Some say: I cant move my head when riding because the server
                    // blocked my movement
                    if (!isSleeping() && this.riding == null && !this.hasEffect(Effect.LEVITATION) && !this.hasEffect(Effect.SLOW_FALLING)) {
                        val diffHorizontalSqr = (diffX * diffX + diffZ * diffZ) / (tickDiff * tickDiff).toDouble()
                        if (diffHorizontalSqr > 0.5) {
                            var ev: PlayerInvalidMoveEvent
                            server!!.getPluginManager().callEvent(PlayerInvalidMoveEvent(this, true).also { ev = it })
                            if (!ev.isCancelled()) {
                                revert = ev.isRevert()
                                if (revert) {
                                    log.warn(server.getLanguage().translateString("nukkit.player.invalidMove", name))
                                }
                            }
                        }
                    }
                }
                this.x = newPos.x
                this.y = newPos.y
                this.z = newPos.z
                val radius: Double = this.getWidth() / 2
                this.boundingBox.setBounds(this.x - radius, this.y, this.z - radius, this.x + radius, this.y + this.getHeight(), this.z + radius)
            }
        }
        val from = Location(
                this.lastX,
                this.lastY,
                this.lastZ,
                this.lastYaw,
                this.lastPitch,
                this.level)
        val to: Location = this.getLocation()
        val delta: Double = Math.pow(this.lastX - to.x, 2) + Math.pow(this.lastY - to.y, 2) + Math.pow(this.z - to.z, 2)
        val deltaAngle: Double = Math.abs(this.lastYaw - to.yaw) + Math.abs(this.lastPitch - to.pitch)
        if (!revert && (delta > 0.0001 || deltaAngle > 1.0)) {
            val isFirst: Boolean = this.firstMove
            this.firstMove = false
            this.lastX = to.x
            this.lastY = to.y
            this.lastZ = to.z
            this.lastYaw = to.yaw
            this.lastPitch = to.pitch
            if (!isFirst) {
                var blocksAround: List<Block> = ArrayList(this.blocksAround)
                val collidingBlocks: List<Block> = ArrayList(this.collisionBlocks)
                val ev = PlayerMoveEvent(this, from, to)
                blocksAround = null
                this.collisionBlocks = null
                server!!.getPluginManager().callEvent(ev)
                if (!ev.isCancelled().also { revert = it }) { //Yes, this is intended
                    if (!to.equals(ev.getTo())) { //If plugins modify the destination
                        teleport(ev.getTo(), null)
                    } else {
                        addMovement(this.x, this.y, this.z, this.yaw, this.pitch, this.yaw)
                    }
                    //Biome biome = Biome.biomes[level.getBiomeId(this.getFloorX(), this.getFloorZ())];
                    //sendTip(biome.getName() + " (" + biome.doesOverhang() + " " + biome.getBaseHeight() + "-" + biome.getHeightVariation() + ")");
                } else {
                    blocksAround = blocksAround
                    this.collisionBlocks = collidingBlocks
                }
            }
            if (speed == null) speed = Vector3(from.x - to.x, from.y - to.y, from.z - to.z) else speed.setComponents(from.x - to.x, from.y - to.y, from.z - to.z)
        } else {
            if (speed == null) speed = Vector3(0, 0, 0) else speed.setComponents(0, 0, 0)
        }
        if (!revert && isFoodEnabled && server!!.getDifficulty() > 0) {
            //UpdateFoodExpLevel
            if (distance >= 0.05) {
                var jump = 0.0
                val swimming: Double = if (this.isInsideOfWater()) 0.015 * distance else 0
                if (swimming != 0.0) distance = 0.0
                if (this.isSprinting()) {  //Running
                    if (inAirTicks == 3 && swimming == 0.0) {
                        jump = 0.7
                    }
                    getFoodData()!!.updateFoodExpLevel(0.06 * distance + jump + swimming)
                } else {
                    if (inAirTicks == 3 && swimming == 0.0) {
                        jump = 0.2
                    }
                    getFoodData()!!.updateFoodExpLevel(0.01 * distance + jump + swimming)
                }
            }
        }
        if (!revert && delta > 0.0001) {
            val frostWalker: Enchantment = inventory.getBoots().getEnchantment(Enchantment.ID_FROST_WALKER)
            if (frostWalker != null && frostWalker.getLevel() > 0 && !isSpectator && this.y >= 1 && this.y <= 255) {
                val radius: Int = 2 + frostWalker.getLevel()
                for (coordX in this.getFloorX() - radius until this.getFloorX() + radius + 1) {
                    for (coordZ in this.getFloorZ() - radius until this.getFloorZ() + radius + 1) {
                        var block: Block = level.getBlock(coordX, this.getFloorY() - 1, coordZ)
                        var layer = 0
                        if (block.getId() !== Block.STILL_WATER && (block.getId() !== Block.WATER || block.getDamage() !== 0) || block.up().getId() !== Block.AIR) {
                            block = block.getLevelBlockAtLayer(1)
                            layer = 1
                            if (block.getId() !== Block.STILL_WATER && (block.getId() !== Block.WATER || block.getDamage() !== 0) || block.up().getId() !== Block.AIR) {
                                continue
                            }
                        }
                        val ev = WaterFrostEvent(block, this)
                        server!!.getPluginManager().callEvent(ev)
                        if (!ev.isCancelled()) {
                            level.setBlock(block, layer, Block.get(Block.ICE_FROSTED), true, false)
                            level.scheduleUpdate(level.getBlock(block, layer), ThreadLocalRandom.current().nextInt(20, 40))
                        }
                    }
                }
            }
        }
        if (!revert) {
            val soulSpeedLevel: Int = this.getInventory().getBoots().getEnchantmentLevel(Enchantment.ID_SOUL_SPEED)
            if (soulSpeedLevel > 0) {
                val downBlock: Block = this.getLevelBlock().down()
                if (wasInSoulSandCompatible && !downBlock.isSoulSpeedCompatible()) {
                    wasInSoulSandCompatible = false
                    soulSpeedMultiplier = 1f
                    sendMovementSpeed(this.movementSpeed)
                } else if (!wasInSoulSandCompatible && downBlock.isSoulSpeedCompatible()) {
                    wasInSoulSandCompatible = true
                    soulSpeedMultiplier = soulSpeedLevel * 0.105f + 1.3f
                    sendMovementSpeed(this.movementSpeed * soulSpeedMultiplier)
                }
            }
        }
        if (revert) {
            this.lastX = from.x
            this.lastY = from.y
            this.lastZ = from.z
            this.lastYaw = from.yaw
            this.lastPitch = from.pitch

            // We have to send slightly above otherwise the player will fall into the ground.
            this.sendPosition(from.add(0, 0.00001, 0), from.yaw, from.pitch, MovePlayerPacket.MODE_RESET)
            //this.sendSettings();
            forceMovement = Vector3(from.x, from.y + 0.00001, from.z)
        } else {
            forceMovement = null
            if (distanceSquared != 0.0 && nextChunkOrderRun > 20) {
                nextChunkOrderRun = 20
            }
        }
        newPosition = null
    }

    @Override
    fun addMovement(x: Double, y: Double, z: Double, yaw: Double, pitch: Double, headYaw: Double) {
        this.sendPosition(Vector3(x, y, z), yaw, pitch, MovePlayerPacket.MODE_NORMAL, this.getViewers().values().toArray(EMPTY_ARRAY))
    }

    @Override
    fun setMotion(motion: Vector3): Boolean {
        if (super.setMotion(motion)) {
            if (this.chunk != null) {
                this.addMotion(this.motionX, this.motionY, this.motionZ) //Send to others
                val pk = SetEntityMotionPacket()
                pk.eid = this.id
                pk.motionX = motion.x
                pk.motionY = motion.y
                pk.motionZ = motion.z
                this.dataPacket(pk) //Send to self
            }
            if (this.motionY > 0) {
                //todo: check this
                startAirTicks = (-Math.log(this.getGravity() / (this.getGravity() + this.getDrag() * this.motionY)) / this.getDrag() * 2 + 5)
            }
            return true
        }
        return false
    }

    fun sendAttributes() {
        val pk = UpdateAttributesPacket()
        pk.entityId = this.getId()
        pk.entries = arrayOf<Attribute>(
                Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(this.getMaxHealth()).setValue(if (health > 0) if (health < getMaxHealth()) health else getMaxHealth() else 0),
                Attribute.getAttribute(Attribute.MAX_HUNGER).setValue(getFoodData().getLevel()),
                Attribute.getAttribute(Attribute.MOVEMENT_SPEED).setValue(this.getMovementSpeed()),
                Attribute.getAttribute(Attribute.EXPERIENCE_LEVEL).setValue(experienceLevel),
                Attribute.getAttribute(Attribute.EXPERIENCE).setValue(experience.toFloat() / calculateRequireExperience(experienceLevel))
        )
        this.dataPacket(pk)
    }

    @Override
    fun onUpdate(currentTick: Int): Boolean {
        if (!loggedIn) {
            return false
        }
        val tickDiff: Int = currentTick - this.lastUpdate
        if (tickDiff <= 0) {
            return true
        }
        messageCounter = 2
        this.lastUpdate = currentTick
        if (fishing != null && server.getTick() % 20 === 0) {
            if (this.distance(fishing) > 33) {
                stopFishing(false)
            }
        }
        if (!this.isAlive() && spawned) {
            ++this.deadTicks
            if (this.deadTicks >= 10) {
                this.despawnFromAll()
            }
            return true
        }
        if (spawned) {
            processMovement(tickDiff)
            if (!isSpectator) {
                checkNearEntities()
            }
            entityBaseTick(tickDiff)
            if (server!!.getDifficulty() === 0 && this.level.getGameRules().getBoolean(GameRule.NATURAL_REGENERATION)) {
                if (this.getHealth() < this.getMaxHealth() && this.ticksLived % 20 === 0) {
                    this.heal(1)
                }
                val foodData: PlayerFood? = getFoodData()
                if (foodData.getLevel() < 20 && this.ticksLived % 10 === 0) {
                    foodData!!.addFoodLevel(1, 0)
                }
            }
            if (this.isOnFire() && this.lastUpdate % 10 === 0) {
                if (isCreative && !this.isInsideOfFire()) {
                    this.extinguish()
                } else if (this.getLevel().isRaining()) {
                    if (this.getLevel().canBlockSeeSky(this)) {
                        this.extinguish()
                    }
                }
            }
            if (!isSpectator && speed != null) {
                if (this.onGround) {
                    if (inAirTicks != 0) {
                        startAirTicks = 5
                    }
                    inAirTicks = 0
                    this.highestPosition = this.y
                } else {
                    if (isCheckingMovement && !this.isGliding() && !server!!.getAllowFlight() && !getAdventureSettings()!!.get(Type.ALLOW_FLIGHT) && inAirTicks > 20 && !isSleeping() && !this.isImmobile() && !this.isSwimming() && this.riding == null && !this.hasEffect(Effect.LEVITATION) && !this.hasEffect(Effect.SLOW_FALLING)) {
                        val expectedVelocity: Double = -this.getGravity() / this.getDrag() as Double - -this.getGravity() / this.getDrag() as Double * Math.exp(-(this.getDrag() as Double) * (inAirTicks - startAirTicks).toDouble())
                        val diff: Double = (speed.y - expectedVelocity) * (speed.y - expectedVelocity)
                        val block: Block = level.getBlock(this)
                        val blockId: Int = block.getId()
                        val ignore = blockId == Block.LADDER || blockId == Block.VINES || blockId == Block.COBWEB || blockId == Block.SCAFFOLDING // || (blockId == Block.SWEET_BERRY_BUSH && block.getDamage() > 0);
                        if (!this.hasEffect(Effect.JUMP) && diff > 0.6 && expectedVelocity < speed.y && !ignore) {
                            if (inAirTicks < 150) {
                                setMotion(Vector3(0, expectedVelocity, 0))
                            } else if (this.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server")) {
                                return false
                            }
                        }
                        if (ignore) {
                            resetFallDistance()
                        }
                    }
                    if (this.y > highestPosition) {
                        this.highestPosition = this.y
                    }
                    if (this.isGliding()) resetFallDistance()
                    ++inAirTicks
                }
                if (getFoodData() != null) getFoodData().update(tickDiff)
            }
            if (!isSleeping()) {
                timeSinceRest++
            }
        }
        checkTeleportPosition()
        if (currentTick % 10 == 0) {
            checkInteractNearby()
        }
        if (spawned && dummyBossBars.size() > 0 && currentTick % 100 == 0) {
            dummyBossBars.values().forEach(DummyBossBar::updateBossEntityPosition)
        }
        updateBlockingFlag()
        val foodData: PlayerFood? = getFoodData()
        if (this.ticksLived % 40 === 0 && foodData != null) {
            foodData.sendFoodLevel()
        }
        return true
    }

    @Override
    fun entityBaseTick(tickDiff: Int): Boolean {
        var hasUpdated = false
        if (isUsingItem) {
            if (noShieldTicks < NO_SHIELD_DELAY) {
                noShieldTicks = NO_SHIELD_DELAY
                hasUpdated = true
            }
        } else {
            if (noShieldTicks > 0) {
                noShieldTicks -= tickDiff
                hasUpdated = true
            }
            if (noShieldTicks < 0) {
                noShieldTicks = 0
                hasUpdated = true
            }
        }
        return super.entityBaseTick(tickDiff) || hasUpdated
    }

    fun checkInteractNearby() {
        val interactDistance = if (isCreative) 5 else 3
        buttonText = if (canInteract(this, interactDistance.toDouble())) {
            if (getEntityPlayerLookingAt(interactDistance) != null) {
                val onInteract: EntityInteractable? = getEntityPlayerLookingAt(interactDistance)
                onInteract.getInteractButtonText()
            } else {
                ""
            }
        } else {
            ""
        }
    }

    /**
     * Returns the Entity the player is looking at currently
     *
     * @param maxDistance the maximum distance to check for entities
     * @return Entity|null    either NULL if no entity is found or an instance of the entity
     */
    fun getEntityPlayerLookingAt(maxDistance: Int): EntityInteractable? {
        timing.startTiming()
        var entity: EntityInteractable? = null

        // just a fix because player MAY not be fully initialized
        if (temporalVector != null) {
            val nearbyEntities: Array<Entity> = level.getNearbyEntities(boundingBox.grow(maxDistance, maxDistance, maxDistance), this)

            // get all blocks in looking direction until the max interact distance is reached (it's possible that startblock isn't found!)
            try {
                val itr = BlockIterator(level, getPosition(), getDirectionVector(), getEyeHeight(), maxDistance)
                if (itr.hasNext()) {
                    var block: Block
                    while (itr.hasNext()) {
                        block = itr.next()
                        entity = getEntityAtPosition(nearbyEntities, block.getFloorX(), block.getFloorY(), block.getFloorZ())
                        if (entity != null) {
                            break
                        }
                    }
                }
            } catch (ex: Exception) {
                // nothing to log here!
            }
        }
        timing.stopTiming()
        return entity
    }

    private fun getEntityAtPosition(nearbyEntities: Array<Entity>, x: Int, y: Int, z: Int): EntityInteractable? {
        for (nearestEntity in nearbyEntities) {
            if (nearestEntity.getFloorX() === x && nearestEntity.getFloorY() === y && nearestEntity.getFloorZ() === z && nearestEntity is EntityInteractable
                    && (nearestEntity as EntityInteractable).canDoInteraction()) {
                return nearestEntity as EntityInteractable
            }
        }
        return null
    }

    fun checkNetwork() {
        if (!isOnline) {
            return
        }
        if (nextChunkOrderRun-- <= 0 || this.chunk == null) {
            orderChunks()
        }
        if (!loadQueue.isEmpty() || !spawned) {
            sendNextChunk()
        }
    }

    fun canInteract(pos: Vector3?, maxDistance: Double): Boolean {
        return this.canInteract(pos, maxDistance, 6.0)
    }

    fun canInteract(pos: Vector3?, maxDistance: Double, maxDiff: Double): Boolean {
        if (this.distanceSquared(pos) > maxDistance * maxDistance) {
            return false
        }
        val dV: Vector2 = this.getDirectionPlane()
        val dot: Double = dV.dot(Vector2(this.x, this.z))
        val dot1: Double = dV.dot(Vector2(pos.x, pos.z))
        return dot1 - dot >= -maxDiff
    }

    protected fun processLogin() {
        if (!server!!.isWhitelisted(name.toLowerCase())) {
            this.kick(PlayerKickEvent.Reason.NOT_WHITELISTED, "Server is white-listed")
            return
        } else if (isBanned) {
            val reason: String = server.getNameBans().getEntires().get(name.toLowerCase()).getReason()
            this.kick(PlayerKickEvent.Reason.NAME_BANNED, if (!reason.isEmpty()) "You are banned. Reason: $reason" else "You are banned")
            return
        } else if (server.getIPBans().isBanned(address)) {
            val reason: String = server.getIPBans().getEntires().get(address).getReason()
            this.kick(PlayerKickEvent.Reason.IP_BANNED, if (!reason.isEmpty()) "You are banned. Reason: $reason" else "You are banned")
            return
        }
        if (this.hasPermission(Server.BROADCAST_CHANNEL_USERS)) {
            server!!.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_USERS, this)
        }
        if (this.hasPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE)) {
            server!!.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this)
        }
        var oldPlayer: Player? = null
        for (p in ArrayList(server.getOnlinePlayers().values())) {
            if (p !== this && p.name != null && p.name.equalsIgnoreCase(name) ||
                    this.getUniqueId().equals(p.getUniqueId())) {
                oldPlayer = p
                break
            }
        }
        val nbt: CompoundTag?
        if (oldPlayer != null) {
            oldPlayer.saveNBT()
            nbt = oldPlayer.namedTag
            oldPlayer.close("", "disconnectionScreen.loggedinOtherLocation")
        } else {
            val legacyDataFile = File(server.getDataPath().toString() + "players/" + name.toLowerCase() + ".dat")
            val dataFile = File(server.getDataPath().toString() + "players/" + this.uuid.toString() + ".dat")
            if (legacyDataFile.exists() && !dataFile.exists()) {
                nbt = server.getOfflinePlayerData(name, false)
                if (!legacyDataFile.delete()) {
                    log.warn("Could not delete legacy player data for {}", name)
                }
            } else {
                nbt = server.getOfflinePlayerData(this.uuid, true)
            }
        }
        if (nbt == null) {
            this.close(leaveMessage, "Invalid data")
            return
        }
        if (loginChainData.isXboxAuthed() && server!!.getPropertyBoolean("xbox-auth") || !server!!.getPropertyBoolean("xbox-auth")) {
            server!!.updateName(this.uuid, name!!)
        }
        playedBefore = nbt.getLong("lastPlayed") - nbt.getLong("firstPlayed") > 1
        nbt.putString("NameTag", name)
        val exp: Int = nbt.getInt("EXP")
        val expLevel: Int = nbt.getInt("expLevel")
        this.setExperience(exp, expLevel)
        gamemode = nbt.getInt("playerGameType") and 0x03
        if (server.getForceGamemode()) {
            gamemode = server.getGamemode()
            nbt.putInt("playerGameType", gamemode)
        }
        adventureSettings = AdventureSettings(this)
                .set(Type.WORLD_IMMUTABLE, isAdventure || isSpectator)
                .set(Type.WORLD_BUILDER, !isAdventure && !isSpectator)
                .set(Type.AUTO_JUMP, true)
                .set(Type.ALLOW_FLIGHT, isCreative)
                .set(Type.NO_CLIP, isSpectator)
        var level: Level?
        if (server!!.getLevelByName(nbt.getString("Level")).also { level = it } == null) {
            this.setLevel(server.getDefaultLevel())
            nbt.putString("Level", level.getName())
            val spawnLocation: Position = level.getSafeSpawn()
            nbt.getList("Pos", DoubleTag::class.java)
                    .add(DoubleTag("0", spawnLocation.x))
                    .add(DoubleTag("1", spawnLocation.y))
                    .add(DoubleTag("2", spawnLocation.z))
        } else {
            this.setLevel(level)
        }
        for (achievement in nbt.getCompound("Achievements").getAllTags()) {
            if (achievement !is ByteTag) {
                continue
            }
            if ((achievement as ByteTag).getData() > 0) {
                achievements.add(achievement.getName())
            }
        }
        nbt.putLong("lastPlayed", System.currentTimeMillis() / 1000)
        val uuid: UUID = getUniqueId()
        nbt.putLong("UUIDLeast", uuid.getLeastSignificantBits())
        nbt.putLong("UUIDMost", uuid.getMostSignificantBits())
        if (server!!.getAutoSave()) {
            server.saveOfflinePlayerData(uuid, nbt, true)
        }
        sendPlayStatus(PlayStatusPacket.LOGIN_SUCCESS)
        server!!.onPlayerLogin(this)
        val posList: ListTag<DoubleTag> = nbt.getList("Pos", DoubleTag::class.java)
        super.init(level.getChunk(posList.get(0).data as Int shr 4, posList.get(2).data as Int shr 4, true), nbt)
        if (!this.namedTag.contains("foodLevel")) {
            this.namedTag.putInt("foodLevel", 20)
        }
        val foodLevel: Int = this.namedTag.getInt("foodLevel")
        if (!this.namedTag.contains("FoodSaturationLevel")) {
            this.namedTag.putFloat("FoodSaturationLevel", 20)
        }
        val foodSaturationLevel: Float = this.namedTag.getFloat("foodSaturationLevel")
        foodData = PlayerFood(this, foodLevel, foodSaturationLevel)
        if (isSpectator) this.keepMovement = true
        teleportPosition = this.getPosition()
        forceMovement = teleportPosition
        if (!this.namedTag.contains("TimeSinceRest")) {
            this.namedTag.putInt("TimeSinceRest", 0)
        }
        timeSinceRest = this.namedTag.getInt("TimeSinceRest")
        if (!server!!.isCheckMovement()) {
            isCheckingMovement = false
        }
        val infoPacket = ResourcePacksInfoPacket()
        infoPacket.resourcePackEntries = server!!.getResourcePackManager().getResourceStack()
        infoPacket.mustAccept = server.getForceResources()
        this.dataPacket(infoPacket)
    }

    protected fun completeLoginSequence() {
        var ev: PlayerLoginEvent
        server!!.getPluginManager().callEvent(PlayerLoginEvent(this, "Plugin reason").also { ev = it })
        if (ev.isCancelled()) {
            this.close(leaveMessage, ev.getKickMessage())
            return
        }
        val level: Level = server!!.getLevelByName(this.namedTag.getString("SpawnLevel"))
        if (level != null) {
            spawnPosition = Position(this.namedTag.getInt("SpawnX"), this.namedTag.getInt("SpawnY"), this.namedTag.getInt("SpawnZ"), level)
            if (this.namedTag.containsInt("SpawnBlockPositionX") && this.namedTag.containsInt("SpawnBlockPositionY") && this.namedTag.containsInt("SpawnBlockPositionZ")) {
                spawnBlockPosition = Vector3(namedTag.getInt("SpawnBlockPositionX"), namedTag.getInt("SpawnBlockPositionY"), namedTag.getInt("SpawnBlockPositionZ"))
            } else {
                spawnBlockPosition = null
            }
        } else {
            spawnPosition = level.getSafeSpawn()
            spawnBlockPosition = null
        }
        spawnPosition = spawn
        val startGamePacket = StartGamePacket()
        startGamePacket.entityUniqueId = this.id
        startGamePacket.entityRuntimeId = this.id
        startGamePacket.playerGamemode = getClientFriendlyGamemode(gamemode)
        startGamePacket.x = this.x as Float
        startGamePacket.y = this.y as Float
        startGamePacket.z = this.z as Float
        startGamePacket.yaw = this.yaw as Float
        startGamePacket.pitch = this.pitch as Float
        startGamePacket.seed = -1
        startGamePacket.dimension =  /*(byte) (this.level.getDimension() & 0xff)*/0
        startGamePacket.worldGamemode = getClientFriendlyGamemode(gamemode)
        startGamePacket.difficulty = server!!.getDifficulty()
        startGamePacket.spawnX = spawnPosition.getFloorX()
        startGamePacket.spawnY = spawnPosition.getFloorY()
        startGamePacket.spawnZ = spawnPosition.getFloorZ()
        startGamePacket.hasAchievementsDisabled = true
        startGamePacket.dayCycleStopTime = -1
        startGamePacket.rainLevel = 0
        startGamePacket.lightningLevel = 0
        startGamePacket.commandsEnabled = isEnableClientCommand()
        startGamePacket.gameRules = getLevel().getGameRules()
        startGamePacket.levelId = ""
        startGamePacket.worldName = server!!.getNetwork().getName()
        startGamePacket.generator = 1 //0 old, 1 infinite, 2 flat
        startGamePacket.dimension = getLevel().getDimension()
        //startGamePacket.isInventoryServerAuthoritative = true;
        dataPacketImmediately(startGamePacket)
        this.dataPacket(ItemComponentPacket())
        this.dataPacket(BiomeDefinitionListPacket())
        this.dataPacket(AvailableEntityIdentifiersPacket())
        this.inventory.sendCreativeContents()
        getAdventureSettings()!!.update()
        sendAttributes()
        this.sendPotionEffects(this)
        this.sendData(this)
        loggedIn = true
        level.sendTime(this)
        sendAttributes()
        this.setNameTagVisible(true)
        this.setNameTagAlwaysVisible(true)
        this.setCanClimb(true)
        log.info(server.getLanguage().translateString("nukkit.player.logIn",
                TextFormat.AQUA + name + TextFormat.WHITE,
                address,
                String.valueOf(port),
                String.valueOf(this.id),
                level.getName(),
                String.valueOf(NukkitMath.round(this.x, 4)),
                String.valueOf(NukkitMath.round(this.y, 4)),
                String.valueOf(NukkitMath.round(this.z, 4))))
        if (isOp || this.hasPermission("nukkit.textcolor")) {
            removeFormat = false
        }
        server!!.addOnlinePlayer(this)
        server!!.onPlayerCompleteLoginSequence(this)
    }

    fun handleDataPacket(packet: DataPacket) {
        if (!isConnected) {
            return
        }
        Timings.getReceiveDataPacketTiming(packet).use { ignored ->
            val ev = DataPacketReceiveEvent(this, packet)
            server!!.getPluginManager().callEvent(ev)
            if (ev.isCancelled()) {
                return
            }
            if (packet.pid() === ProtocolInfo.BATCH_PACKET) {
                server!!.getNetwork().processBatch(packet as BatchPacket, this)
                return
            }
            if (log.isTraceEnabled() && !server!!.isIgnoredPacket(packet.getClass())) {
                log.trace("Inbound {}: {}", name, packet)
            }
            packetswitch@ when (packet.pid()) {
                ProtocolInfo.LOGIN_PACKET -> {
                    if (loggedIn) {
                        break
                    }
                    val loginPacket: LoginPacket = packet as LoginPacket
                    val message: String
                    if (!ProtocolInfo.SUPPORTED_PROTOCOLS.contains(loginPacket.getProtocol())) {
                        if (loginPacket.getProtocol() < ProtocolInfo.CURRENT_PROTOCOL) {
                            message = "disconnectionScreen.outdatedClient"
                            sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_CLIENT, true)
                        } else {
                            message = "disconnectionScreen.outdatedServer"
                            sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_SERVER, true)
                        }
                        if ((packet as LoginPacket).protocol < 137) {
                            val disconnectPacket = DisconnectPacket()
                            disconnectPacket.message = message
                            disconnectPacket.encode()
                            val batch = BatchPacket()
                            batch.payload = disconnectPacket.getBuffer()
                            this.dataPacket(batch)
                            // Still want to run close() to allow the player to be removed properly
                        }
                        this.close("", message, false)
                        break
                    }
                    name = TextFormat.clean(loginPacket.username)
                    displayName = name
                    iusername = name.toLowerCase()
                    this.setDataProperty(StringEntityData(DATA_NAMETAG, name), false)
                    loginChainData = ClientChainData.read(loginPacket)
                    if (!loginChainData.isXboxAuthed() && server!!.getPropertyBoolean("xbox-auth")) {
                        this.close("", "disconnectionScreen.notAuthenticated")
                        break
                    }
                    if (server.getOnlinePlayers().size() >= server.getMaxPlayers() && this.kick(PlayerKickEvent.Reason.SERVER_FULL, "disconnectionScreen.serverFull", false)) {
                        break
                    }
                    clientId = loginPacket.clientId
                    this.uuid = loginPacket.clientUUID
                    this.rawUUID = Binary.writeUUID(this.uuid)
                    var valid = true
                    val len: Int = loginPacket.username.length()
                    if (len > 16 || len < 3) {
                        valid = false
                    }
                    var i = 0
                    while (i < len && valid) {
                        val c: Char = loginPacket.username.charAt(i)
                        if (c >= 'a' && c <= 'z' ||
                                c >= 'A' && c <= 'Z' ||
                                c >= '0' && c <= '9' || c == '_' || c == ' ') {
                            i++
                            continue
                        }
                        valid = false
                        break
                        i++
                    }
                    if (!valid || Objects.equals(iusername, "rcon") || Objects.equals(iusername, "console")) {
                        this.close("", "disconnectionScreen.invalidName")
                        break
                    }
                    if (!loginPacket.skin.isValid()) {
                        this.close("", "disconnectionScreen.invalidSkin")
                        break
                    } else {
                        val skin: Skin = loginPacket.skin
                        if (server!!.isForceSkinTrusted()) {
                            skin.setTrusted(true)
                        }
                        setSkin(skin)
                    }
                    var playerPreLoginEvent: PlayerPreLoginEvent
                    server!!.getPluginManager().callEvent(PlayerPreLoginEvent(this, "Plugin reason").also { playerPreLoginEvent = it })
                    if (playerPreLoginEvent.isCancelled()) {
                        this.close("", playerPreLoginEvent.getKickMessage())
                        break
                    }
                    val playerInstance = this
                    preLoginEventTask = object : AsyncTask() {
                        private var event: PlayerAsyncPreLoginEvent? = null
                        @Override
                        fun onRun() {
                            event = PlayerAsyncPreLoginEvent(name, uuid, loginChainData, playerInstance.getSkin(), playerInstance.address, playerInstance.port)
                            server!!.getPluginManager().callEvent(event)
                        }

                        @Override
                        fun onCompletion(server: Server?) {
                            if (playerInstance.closed) {
                                return
                            }
                            if (event.getLoginResult() === LoginResult.KICK) {
                                playerInstance.close(event.getKickMessage(), event.getKickMessage())
                            } else if (playerInstance.shouldLogin) {
                                playerInstance.setSkin(event.getSkin())
                                playerInstance.completeLoginSequence()
                                for (action in event.getScheduledActions()) {
                                    action.accept(server)
                                }
                            }
                        }
                    }
                    server!!.getScheduler().scheduleAsyncTask(preLoginEventTask)
                    processLogin()
                }
                ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET -> {
                    val responsePacket: ResourcePackClientResponsePacket = packet as ResourcePackClientResponsePacket
                    when (responsePacket.responseStatus) {
                        ResourcePackClientResponsePacket.STATUS_REFUSED -> this.close("", "disconnectionScreen.noReason")
                        ResourcePackClientResponsePacket.STATUS_SEND_PACKS -> for (entry in responsePacket.packEntries) {
                            val resourcePack: ResourcePack = server!!.getResourcePackManager().getPackById(entry.uuid)
                            if (resourcePack == null) {
                                this.close("", "disconnectionScreen.resourcePack")
                                break
                            }
                            val dataInfoPacket = ResourcePackDataInfoPacket()
                            dataInfoPacket.packId = resourcePack.getPackId()
                            dataInfoPacket.maxChunkSize = 1048576 //megabyte
                            dataInfoPacket.chunkCount = resourcePack.getPackSize() / dataInfoPacket.maxChunkSize
                            dataInfoPacket.compressedPackSize = resourcePack.getPackSize()
                            dataInfoPacket.sha256 = resourcePack.getSha256()
                            this.dataPacket(dataInfoPacket)
                        }
                        ResourcePackClientResponsePacket.STATUS_HAVE_ALL_PACKS -> {
                            val stackPacket = ResourcePackStackPacket()
                            stackPacket.mustAccept = server.getForceResources()
                            stackPacket.resourcePackStack = server!!.getResourcePackManager().getResourceStack()
                            this.dataPacket(stackPacket)
                        }
                        ResourcePackClientResponsePacket.STATUS_COMPLETED -> {
                            shouldLogin = true
                            if (preLoginEventTask.isFinished()) {
                                preLoginEventTask.onCompletion(server)
                            }
                        }
                    }
                }
                ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET -> {
                    val requestPacket: ResourcePackChunkRequestPacket = packet as ResourcePackChunkRequestPacket
                    val resourcePack: ResourcePack = server!!.getResourcePackManager().getPackById(requestPacket.packId)
                    if (resourcePack == null) {
                        this.close("", "disconnectionScreen.resourcePack")
                        break
                    }
                    val dataPacket = ResourcePackChunkDataPacket()
                    dataPacket.packId = resourcePack.getPackId()
                    dataPacket.chunkIndex = requestPacket.chunkIndex
                    dataPacket.data = resourcePack.getPackChunk(1048576 * requestPacket.chunkIndex, 1048576)
                    dataPacket.progress = 1048576 * requestPacket.chunkIndex
                    this.dataPacket(dataPacket)
                }
                ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET -> {
                    if (locallyInitialized) {
                        break
                    }
                    locallyInitialized = true
                    val locallyInitializedEvent = PlayerLocallyInitializedEvent(this)
                    server!!.getPluginManager().callEvent(locallyInitializedEvent)
                }
                ProtocolInfo.PLAYER_SKIN_PACKET -> {
                    val skinPacket: PlayerSkinPacket = packet as PlayerSkinPacket
                    val skin: Skin = skinPacket.skin
                    if (!skin.isValid()) {
                        break
                    }
                    if (server!!.isForceSkinTrusted()) {
                        skin.setTrusted(true)
                    }
                    val playerChangeSkinEvent = PlayerChangeSkinEvent(this, skin)
                    playerChangeSkinEvent.setCancelled(TimeUnit.SECONDS.toMillis(server.getPlayerSkinChangeCooldown()) > System.currentTimeMillis() - lastSkinChange)
                    server!!.getPluginManager().callEvent(playerChangeSkinEvent)
                    if (!playerChangeSkinEvent.isCancelled()) {
                        lastSkinChange = System.currentTimeMillis()
                        setSkin(skin)
                    }
                }
                ProtocolInfo.PACKET_VIOLATION_WARNING_PACKET -> {
                    val packetName: Optional<String> = Arrays.stream(ProtocolInfo::class.java.getDeclaredFields())
                            .filter { field -> field.getType() === Byte.TYPE }
                            .filter { field ->
                                try {
                                    return@filter field.getByte(null) === (packet as PacketViolationWarningPacket).packetId
                                } catch (e: IllegalAccessException) {
                                    return@filter false
                                }
                            }.name.findFirst()
                    log.warn("Violation warning from {}{}", name, packetName.map { name -> " for packet $name" }.orElse("").toString() + ": " + packet.toString())
                }
                ProtocolInfo.EMOTE_PACKET -> {
                    for (viewer in this.getViewers().values()) {
                        viewer.dataPacket(packet)
                    }
                    return
                }
                ProtocolInfo.PLAYER_INPUT_PACKET -> {
                    if (!this.isAlive() || !spawned) {
                        break
                    }
                    val ipk: PlayerInputPacket = packet as PlayerInputPacket
                    if (riding is EntityMinecartAbstract) {
                        (riding as EntityMinecartAbstract).setCurrentSpeed(ipk.motionY)
                    }
                }
                ProtocolInfo.MOVE_ENTITY_ABSOLUTE_PACKET -> {
                    if (!this.isAlive() || !spawned || this.getRiding() == null) {
                        break
                    }
                    val movePacket: MoveEntityAbsolutePacket = packet as MoveEntityAbsolutePacket
                    val movedEntity: Entity = getLevel().getEntity(movePacket.eid) as? EntityBoat ?: break
                    temporalVector.setComponents(movePacket.x, movePacket.y - (movedEntity as EntityBoat).getBaseOffset(), movePacket.z)
                    if (!movedEntity.equals(getRiding()) || !movedEntity.isControlling(this)
                            || temporalVector.distanceSquared(movedEntity) > 10 * 10) {
                        movedEntity.addMovement(movedEntity.x, movedEntity.y, movedEntity.z, movedEntity.yaw, movedEntity.pitch, movedEntity.yaw)
                        break
                    }
                    val from: Location = movedEntity.getLocation()
                    movedEntity.setPositionAndRotation(temporalVector, movePacket.headYaw, 0)
                    val to: Location = movedEntity.getLocation()
                    if (!from.equals(to)) {
                        server!!.getPluginManager().callEvent(VehicleMoveEvent(this, from, to))
                    }
                }
                ProtocolInfo.MOVE_PLAYER_PACKET -> {
                    if (teleportPosition != null) {
                        break
                    }
                    val movePlayerPacket: MovePlayerPacket = packet as MovePlayerPacket
                    val newPos = Vector3(movePlayerPacket.x, movePlayerPacket.y - this.getEyeHeight(), movePlayerPacket.z)
                    if (newPos.distanceSquared(this) < 0.01 && movePlayerPacket.yaw % 360 === this.yaw && movePlayerPacket.pitch % 360 === this.pitch) {
                        break
                    }
                    if (newPos.distanceSquared(this) > 100) {
                        this.sendPosition(this, movePlayerPacket.yaw, movePlayerPacket.pitch, MovePlayerPacket.MODE_RESET)
                        break
                    }
                    var revert = false
                    if (!this.isAlive() || !spawned) {
                        revert = true
                        forceMovement = Vector3(this.x, this.y, this.z)
                    }
                    if (forceMovement != null && (newPos.distanceSquared(forceMovement) > 0.1 || revert)) {
                        this.sendPosition(forceMovement, movePlayerPacket.yaw, movePlayerPacket.pitch, MovePlayerPacket.MODE_RESET)
                    } else {
                        movePlayerPacket.yaw %= 360
                        movePlayerPacket.pitch %= 360
                        if (movePlayerPacket.yaw < 0) {
                            movePlayerPacket.yaw += 360
                        }
                        this.setRotation(movePlayerPacket.yaw, movePlayerPacket.pitch)
                        newPosition = newPos
                        this.positionChanged = true
                        forceMovement = null
                    }
                }
                ProtocolInfo.ADVENTURE_SETTINGS_PACKET -> {
                    //TODO: player abilities, check for other changes
                    val adventureSettingsPacket: AdventureSettingsPacket = packet as AdventureSettingsPacket
                    if (!server!!.getAllowFlight() && adventureSettingsPacket.getFlag(AdventureSettingsPacket.FLYING) && !getAdventureSettings()!!.get(Type.ALLOW_FLIGHT)) {
                        this.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server")
                        break
                    }
                    val playerToggleFlightEvent = PlayerToggleFlightEvent(this, adventureSettingsPacket.getFlag(AdventureSettingsPacket.FLYING))
                    server!!.getPluginManager().callEvent(playerToggleFlightEvent)
                    if (playerToggleFlightEvent.isCancelled()) {
                        getAdventureSettings()!!.update()
                    } else {
                        getAdventureSettings()!!.set(Type.FLYING, playerToggleFlightEvent.isFlying())
                    }
                }
                ProtocolInfo.MOB_EQUIPMENT_PACKET -> {
                    if (!spawned || !this.isAlive()) {
                        break
                    }
                    val mobEquipmentPacket: MobEquipmentPacket = packet as MobEquipmentPacket
                    val inv: Inventory = getWindowById(mobEquipmentPacket.windowId)
                    if (inv == null) {
                        log.debug("Player {} has no open container with window ID {}", name, mobEquipmentPacket.windowId)
                        return
                    }
                    val item: Item = inv.getItem(mobEquipmentPacket.hotbarSlot)
                    if (!item.equals(mobEquipmentPacket.item)) {
                        log.debug("Tried to equip {} but have {} in target slot", mobEquipmentPacket.item, item)
                        inv.sendContents(this)
                        return
                    }
                    if (inv is PlayerInventory) {
                        (inv as PlayerInventory).equipItem(mobEquipmentPacket.hotbarSlot)
                    }
                    this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false)
                }
                ProtocolInfo.PLAYER_ACTION_PACKET -> {
                    val playerActionPacket: PlayerActionPacket = packet as PlayerActionPacket
                    if (!spawned || !this.isAlive() && playerActionPacket.action !== PlayerActionPacket.ACTION_RESPAWN && playerActionPacket.action !== PlayerActionPacket.ACTION_DIMENSION_CHANGE_REQUEST) {
                        break
                    }
                    playerActionPacket.entityId = this.id
                    val pos = Vector3(playerActionPacket.x, playerActionPacket.y, playerActionPacket.z)
                    val face: BlockFace = BlockFace.fromIndex(playerActionPacket.face)
                    actionswitch@ when (playerActionPacket.action) {
                        PlayerActionPacket.ACTION_START_BREAK -> {
                            val currentBreak: Long = System.currentTimeMillis()
                            val currentBreakPosition = BlockVector3(playerActionPacket.x, playerActionPacket.y, playerActionPacket.z)
                            // HACK: Client spams multiple left clicks so we need to skip them.
                            if (lastBreakPosition.equals(currentBreakPosition) && currentBreak - lastBreak < 10 || pos.distanceSquared(this) > 100) {
                                break
                            }
                            val target: Block = this.level.getBlock(pos)
                            val playerInteractEvent = PlayerInteractEvent(this, this.inventory.getItemInHand(), target, face, if (target.getId() === 0) Action.LEFT_CLICK_AIR else Action.LEFT_CLICK_BLOCK)
                            server!!.getPluginManager().callEvent(playerInteractEvent)
                            if (playerInteractEvent.isCancelled()) {
                                this.inventory.sendHeldItem(this)
                                break
                            }
                            target.onTouch(this, playerInteractEvent.getAction())
                            val block: Block = target.getSide(face)
                            if (block.getId() === BlockID.FIRE || block.getId() === BlockID.SOUL_FIRE) {
                                this.level.setBlock(block, Block.get(BlockID.AIR), true)
                                this.level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_EXTINGUISH_FIRE)
                                break
                            }
                            if (block.getId() === BlockID.SWEET_BERRY_BUSH && block.getDamage() === 0) {
                                val oldItem: Item = playerInteractEvent.getItem()
                                val i: Item = this.level.useBreakOn(block, oldItem, this, true)
                                if (isSurvival || isAdventure) {
                                    getFoodData()!!.updateFoodExpLevel(0.025)
                                    if (!i.equals(oldItem) || i.getCount() !== oldItem.getCount()) {
                                        inventory.setItemInHand(i)
                                        inventory.sendHeldItem(this.getViewers().values())
                                    }
                                }
                                break
                            }
                            if (!block.isBlockChangeAllowed(this)) {
                                break
                            }
                            if (!isCreative) {

                                //improved this to take stuff like swimming, ladders, enchanted tools into account, fix wrong tool break time calculations for bad tools (pmmp/PocketMine-MP#211)
                                //Done by lmlstarqaq
                                val breakTime: Double = Math.ceil(target.calculateBreakTime(this.inventory.getItemInHand(), this) * 20)
                                if (breakTime > 0) {
                                    val pk = LevelEventPacket()
                                    pk.evid = LevelEventPacket.EVENT_BLOCK_START_BREAK
                                    pk.x = pos.x
                                    pk.y = pos.y
                                    pk.z = pos.z
                                    pk.data = (65535 / breakTime).toInt()
                                    this.getLevel().addChunkPacket(pos.getFloorX() shr 4, pos.getFloorZ() shr 4, pk)
                                }
                            }
                            breakingBlock = target
                            lastBreak = currentBreak
                            lastBreakPosition = currentBreakPosition
                        }
                        PlayerActionPacket.ACTION_ABORT_BREAK, PlayerActionPacket.ACTION_STOP_BREAK -> {
                            val pk = LevelEventPacket()
                            pk.evid = LevelEventPacket.EVENT_BLOCK_STOP_BREAK
                            pk.x = pos.x
                            pk.y = pos.y
                            pk.z = pos.z
                            pk.data = 0
                            this.getLevel().addChunkPacket(pos.getFloorX() shr 4, pos.getFloorZ() shr 4, pk)
                            breakingBlock = null
                        }
                        PlayerActionPacket.ACTION_GET_UPDATED_BLOCK -> {
                        }
                        PlayerActionPacket.ACTION_DROP_ITEM -> {
                        }
                        PlayerActionPacket.ACTION_STOP_SLEEPING -> stopSleep()
                        PlayerActionPacket.ACTION_RESPAWN -> {
                            if (!spawned || this.isAlive() || !isOnline) {
                                break
                            }
                            respawn()
                        }
                        PlayerActionPacket.ACTION_JUMP -> {
                            val playerJumpEvent = PlayerJumpEvent(this)
                            server!!.getPluginManager().callEvent(playerJumpEvent)
                            break@packetswitch
                        }
                        PlayerActionPacket.ACTION_START_SPRINT -> {
                            val playerToggleSprintEvent = PlayerToggleSprintEvent(this, true)
                            server!!.getPluginManager().callEvent(playerToggleSprintEvent)
                            if (playerToggleSprintEvent.isCancelled()) {
                                this.sendData(this)
                            } else {
                                setSprinting(true)
                            }
                            break@packetswitch
                        }
                        PlayerActionPacket.ACTION_STOP_SPRINT -> {
                            playerToggleSprintEvent = PlayerToggleSprintEvent(this, false)
                            server!!.getPluginManager().callEvent(playerToggleSprintEvent)
                            if (playerToggleSprintEvent.isCancelled()) {
                                this.sendData(this)
                            } else {
                                setSprinting(false)
                            }
                            break@packetswitch
                        }
                        PlayerActionPacket.ACTION_START_SNEAK -> {
                            val playerToggleSneakEvent = PlayerToggleSneakEvent(this, true)
                            server!!.getPluginManager().callEvent(playerToggleSneakEvent)
                            if (playerToggleSneakEvent.isCancelled()) {
                                this.sendData(this)
                            } else {
                                this.setSneaking(true)
                            }
                            break@packetswitch
                        }
                        PlayerActionPacket.ACTION_STOP_SNEAK -> {
                            playerToggleSneakEvent = PlayerToggleSneakEvent(this, false)
                            server!!.getPluginManager().callEvent(playerToggleSneakEvent)
                            if (playerToggleSneakEvent.isCancelled()) {
                                this.sendData(this)
                            } else {
                                this.setSneaking(false)
                            }
                            break@packetswitch
                        }
                        PlayerActionPacket.ACTION_DIMENSION_CHANGE_ACK -> this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_NORMAL)
                        PlayerActionPacket.ACTION_START_GLIDE -> {
                            val playerToggleGlideEvent = PlayerToggleGlideEvent(this, true)
                            server!!.getPluginManager().callEvent(playerToggleGlideEvent)
                            if (playerToggleGlideEvent.isCancelled()) {
                                this.sendData(this)
                            } else {
                                this.setGliding(true)
                            }
                            break@packetswitch
                        }
                        PlayerActionPacket.ACTION_STOP_GLIDE -> {
                            playerToggleGlideEvent = PlayerToggleGlideEvent(this, false)
                            server!!.getPluginManager().callEvent(playerToggleGlideEvent)
                            if (playerToggleGlideEvent.isCancelled()) {
                                this.sendData(this)
                            } else {
                                this.setGliding(false)
                            }
                            break@packetswitch
                        }
                        PlayerActionPacket.ACTION_CONTINUE_BREAK -> if (isBreakingBlock()) {
                            block = this.level.getBlock(pos)
                            this.level.addParticle(PunchBlockParticle(pos, block, face))
                        }
                        PlayerActionPacket.ACTION_START_SWIMMING -> {
                            val ptse = PlayerToggleSwimEvent(this, true)
                            server!!.getPluginManager().callEvent(ptse)
                            if (ptse.isCancelled()) {
                                this.sendData(this)
                            } else {
                                this.setSwimming(true)
                            }
                        }
                        PlayerActionPacket.ACTION_STOP_SWIMMING -> {
                            ptse = PlayerToggleSwimEvent(this, false)
                            server!!.getPluginManager().callEvent(ptse)
                            if (ptse.isCancelled()) {
                                this.sendData(this)
                            } else {
                                this.setSwimming(false)
                            }
                        }
                        PlayerActionPacket.ACTION_START_SPIN_ATTACK -> {
                            if (this.inventory.getItemInHand().getId() !== ItemID.TRIDENT) {
                                this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET)
                                break
                            }
                            val riptideLevel: Int = this.inventory.getItemInHand().getEnchantmentLevel(Enchantment.ID_TRIDENT_RIPTIDE)
                            if (riptideLevel < 1) {
                                this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET)
                                break
                            }
                            if (!(this.isTouchingWater() || this.getLevel().isRaining() && this.getLevel().canBlockSeeSky(this))) {
                                this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET)
                                break
                            }
                            val playerToggleSpinAttackEvent = PlayerToggleSpinAttackEvent(this, true)
                            server!!.getPluginManager().callEvent(playerToggleSpinAttackEvent)
                            if (playerToggleSpinAttackEvent.isCancelled()) {
                                this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET)
                            } else {
                                this.setSpinAttacking(true)
                                val riptideSound: Sound
                                riptideSound = if (riptideLevel >= 3) {
                                    Sound.ITEM_TRIDENT_RIPTIDE_3
                                } else if (riptideLevel == 2) {
                                    Sound.ITEM_TRIDENT_RIPTIDE_2
                                } else {
                                    Sound.ITEM_TRIDENT_RIPTIDE_1
                                }
                                this.level.addSound(this, riptideSound)
                            }
                            break@packetswitch
                        }
                        PlayerActionPacket.ACTION_STOP_SPIN_ATTACK -> {
                            playerToggleSpinAttackEvent = PlayerToggleSpinAttackEvent(this, false)
                            server!!.getPluginManager().callEvent(playerToggleSpinAttackEvent)
                            if (playerToggleSpinAttackEvent.isCancelled()) {
                                this.sendData(this)
                            } else {
                                this.setSpinAttacking(false)
                            }
                        }
                    }
                    isUsingItem = false
                }
                ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET -> {
                }
                ProtocolInfo.MODAL_FORM_RESPONSE_PACKET -> {
                    if (!spawned || !this.isAlive()) {
                        break
                    }
                    val modalFormPacket: ModalFormResponsePacket = packet as ModalFormResponsePacket
                    if (formWindows.containsKey(modalFormPacket.formId)) {
                        val window: FormWindow = formWindows.remove(modalFormPacket.formId)
                        window.setResponse(modalFormPacket.data.trim())
                        val event = PlayerFormRespondedEvent(this, modalFormPacket.formId, window)
                        server!!.getPluginManager().callEvent(event)
                    } else if (serverSettings.containsKey(modalFormPacket.formId)) {
                        val window: FormWindow? = serverSettings[modalFormPacket.formId]
                        window.setResponse(modalFormPacket.data.trim())
                        val event = PlayerSettingsRespondedEvent(this, modalFormPacket.formId, window)
                        server!!.getPluginManager().callEvent(event)

                        //Set back new settings if not been cancelled
                        if (!event.isCancelled() && window is FormWindowCustom) (window as FormWindowCustom?).setElementsFromResponse()
                    }
                }
                ProtocolInfo.INTERACT_PACKET -> {
                    if (!spawned || !this.isAlive()) {
                        break
                    }
                    val interactPacket: InteractPacket = packet as InteractPacket
                    if (interactPacket.action !== InteractPacket.ACTION_MOUSEOVER || interactPacket.target !== 0) {
                        craftingType = CRAFTING_SMALL
                        //this.resetCraftingGridType();
                    }
                    val targetEntity: Entity = this.level.getEntity(interactPacket.target)
                    if (targetEntity == null || !this.isAlive() || !targetEntity.isAlive()) {
                        break
                    }
                    if (targetEntity is EntityItem || targetEntity is EntityArrow || targetEntity is EntityXPOrb) {
                        this.kick(PlayerKickEvent.Reason.INVALID_PVE, "Attempting to interact with an invalid entity")
                        log.warn(server.getLanguage().translateString("nukkit.player.invalidEntity", name))
                        break
                    }
                    item = this.inventory.getItemInHand()
                    when (interactPacket.action) {
                        InteractPacket.ACTION_MOUSEOVER -> {
                            if (interactPacket.target === 0) {
                                break@packetswitch
                            }
                            server!!.getPluginManager().callEvent(PlayerMouseOverEntityEvent(this, targetEntity))
                        }
                        InteractPacket.ACTION_VEHICLE_EXIT -> {
                            if (targetEntity !is EntityRideable || this.riding == null) {
                                break
                            }
                            (riding as EntityRideable).dismountEntity(this)
                        }
                        InteractPacket.ACTION_OPEN_INVENTORY -> {
                            if (targetEntity is EntityRideable) {
                                if (!(targetEntity is EntityBoat || targetEntity is EntityMinecartEmpty)) {
                                    break
                                }
                            } else if (targetEntity.getId() !== this.getId()) {
                                break
                            }
                            if (!inventoryOpen) {
                                this.inventory.open(this)
                                inventoryOpen = true
                            }
                        }
                    }
                }
                ProtocolInfo.BLOCK_PICK_REQUEST_PACKET -> {
                    val pickRequestPacket: BlockPickRequestPacket = packet as BlockPickRequestPacket
                    val block: Block = this.level.getBlock(this.temporalVector.setComponents(pickRequestPacket.x, pickRequestPacket.y, pickRequestPacket.z))
                    item = block.toItem()
                    if (pickRequestPacket.addUserData) {
                        val blockEntity: BlockEntity = this.getLevel().getBlockEntity(Vector3(pickRequestPacket.x, pickRequestPacket.y, pickRequestPacket.z))
                        if (blockEntity != null) {
                            val nbt: CompoundTag = blockEntity.getCleanedNBT()
                            if (nbt != null) {
                                item.setCustomBlockData(nbt)
                                item.setLore("+(DATA)")
                            }
                        }
                    }
                    val pickEvent = PlayerBlockPickEvent(this, block, item)
                    if (isSpectator) {
                        log.debug("Got block-pick request from {} when in spectator mode", name)
                        pickEvent.setCancelled()
                    }
                    server!!.getPluginManager().callEvent(pickEvent)
                    if (!pickEvent.isCancelled()) {
                        var itemExists = false
                        var itemSlot = -1
                        run {
                            var slot = 0
                            while (slot < this.inventory.getSize()) {
                                if (this.inventory.getItem(slot).equals(pickEvent.getItem())) {
                                    if (slot < this.inventory.getHotbarSize()) {
                                        this.inventory.setHeldItemSlot(slot)
                                    } else {
                                        itemSlot = slot
                                    }
                                    itemExists = true
                                    break
                                }
                                slot++
                            }
                        }
                        var slot = 0
                        while (slot < this.inventory.getHotbarSize()) {
                            if (this.inventory.getItem(slot).isNull()) {
                                if (!itemExists && isCreative) {
                                    this.inventory.setHeldItemSlot(slot)
                                    this.inventory.setItemInHand(pickEvent.getItem())
                                    break@packetswitch
                                } else if (itemSlot > -1) {
                                    this.inventory.setHeldItemSlot(slot)
                                    this.inventory.setItemInHand(this.inventory.getItem(itemSlot))
                                    this.inventory.clear(itemSlot, true)
                                    break@packetswitch
                                }
                            }
                            slot++
                        }
                        if (!itemExists && isCreative) {
                            val itemInHand: Item = this.inventory.getItemInHand()
                            this.inventory.setItemInHand(pickEvent.getItem())
                            if (!this.inventory.isFull()) {
                                var slot = 0
                                while (slot < this.inventory.getSize()) {
                                    if (this.inventory.getItem(slot).isNull()) {
                                        this.inventory.setItem(slot, itemInHand)
                                        break
                                    }
                                    slot++
                                }
                            }
                        } else if (itemSlot > -1) {
                            val itemInHand: Item = this.inventory.getItemInHand()
                            this.inventory.setItemInHand(this.inventory.getItem(itemSlot))
                            this.inventory.setItem(itemSlot, itemInHand)
                        }
                    }
                }
                ProtocolInfo.ANIMATE_PACKET -> {
                    if (!spawned || !this.isAlive()) {
                        break
                    }
                    var animatePacket: AnimatePacket = packet as AnimatePacket
                    val animationEvent = PlayerAnimationEvent(this, animatePacket)
                    server!!.getPluginManager().callEvent(animationEvent)
                    if (animationEvent.isCancelled()) {
                        break
                    }
                    val animation: AnimatePacket.Action = animationEvent.getAnimationType()
                    when (animation) {
                        ROW_RIGHT, ROW_LEFT -> if (this.riding is EntityBoat) {
                            (this.riding as EntityBoat).onPaddle(animation, (packet as AnimatePacket).rowingTime)
                        }
                    }
                    if (animationEvent.getAnimationType() === AnimatePacket.Action.SWING_ARM) {
                        noShieldTicks = NO_SHIELD_DELAY
                    }
                    animatePacket = AnimatePacket()
                    animatePacket.eid = this.getId()
                    animatePacket.action = animationEvent.getAnimationType()
                    animatePacket.rowingTime = animationEvent.getRowingTime()
                    Server.broadcastPacket(this.getViewers().values(), animatePacket)
                }
                ProtocolInfo.SET_HEALTH_PACKET -> {
                }
                ProtocolInfo.ENTITY_EVENT_PACKET -> {
                    if (!spawned || !this.isAlive()) {
                        break
                    }
                    val entityEventPacket: EntityEventPacket = packet as EntityEventPacket
                    if (craftingType != CRAFTING_ANVIL && entityEventPacket.event !== EntityEventPacket.ENCHANT) {
                        craftingType = CRAFTING_SMALL
                        //this.resetCraftingGridType();
                    }
                    if (entityEventPacket.event === EntityEventPacket.EATING_ITEM) {
                        if (entityEventPacket.data === 0 || entityEventPacket.eid !== this.id) {
                            break
                        }
                        entityEventPacket.eid = this.id
                        entityEventPacket.isEncoded = false
                        this.dataPacket(entityEventPacket)
                        Server.broadcastPacket(this.getViewers().values(), entityEventPacket)
                    } else if (entityEventPacket.event === EntityEventPacket.ENCHANT) {
                        if (entityEventPacket.eid !== this.id) {
                            break
                        }
                        val inventory: Inventory = getWindowById(ANVIL_WINDOW_ID)
                        if (inventory is AnvilInventory) {
                            (inventory as AnvilInventory).setCost(-entityEventPacket.data)
                        }
                    }
                }
                ProtocolInfo.COMMAND_REQUEST_PACKET -> {
                    if (!spawned || !this.isAlive()) {
                        break
                    }
                    craftingType = CRAFTING_SMALL
                    val commandRequestPacket: CommandRequestPacket = packet as CommandRequestPacket
                    val playerCommandPreprocessEvent = PlayerCommandPreprocessEvent(this, commandRequestPacket.command)
                    server!!.getPluginManager().callEvent(playerCommandPreprocessEvent)
                    if (playerCommandPreprocessEvent.isCancelled()) {
                        break
                    }
                    Timings.playerCommandTimer.startTiming()
                    server!!.dispatchCommand(playerCommandPreprocessEvent.getPlayer(), playerCommandPreprocessEvent.getMessage().substring(1))
                    Timings.playerCommandTimer.stopTiming()
                }
                ProtocolInfo.TEXT_PACKET -> {
                    if (!spawned || !this.isAlive()) {
                        break
                    }
                    val textPacket: TextPacket = packet as TextPacket
                    if (textPacket.type === TextPacket.TYPE_CHAT) {
                        var chatMessage: String = textPacket.message
                        val breakLine: Int = chatMessage.indexOf('\n')
                        // Chat messages shouldn't contain break lines so ignore text afterwards
                        if (breakLine != -1) {
                            chatMessage = chatMessage.substring(0, breakLine)
                        }
                        chat(chatMessage)
                    }
                }
                ProtocolInfo.CONTAINER_CLOSE_PACKET -> {
                    val containerClosePacket: ContainerClosePacket = packet as ContainerClosePacket
                    if (!spawned || containerClosePacket.windowId === ContainerIds.INVENTORY && !inventoryOpen) {
                        break
                    }
                    if (windowIndex.containsKey(containerClosePacket.windowId)) {
                        server!!.getPluginManager().callEvent(InventoryCloseEvent(windowIndex.get(containerClosePacket.windowId), this))
                        if (containerClosePacket.windowId === ContainerIds.INVENTORY) inventoryOpen = false
                        closingWindowId = containerClosePacket.windowId
                        this.removeWindow(windowIndex.get(containerClosePacket.windowId), true)
                        closingWindowId = Integer.MIN_VALUE
                    }
                    if (containerClosePacket.windowId === -1) {
                        craftingType = CRAFTING_SMALL
                        resetCraftingGridType()
                        this.addWindow(craftingGrid, ContainerIds.NONE)
                        val pk = ContainerClosePacket()
                        pk.wasServerInitiated = false
                        pk.windowId = -1
                        this.dataPacket(pk)
                    }
                }
                ProtocolInfo.CRAFTING_EVENT_PACKET -> {
                    val craftingEventPacket: CraftingEventPacket = packet as CraftingEventPacket
                    if (craftingType == CRAFTING_BIG && craftingEventPacket.type === CraftingEventPacket.TYPE_WORKBENCH
                            || craftingType == CRAFTING_SMALL && craftingEventPacket.type === CraftingEventPacket.TYPE_INVENTORY) {
                        if (craftingTransaction != null) {
                            craftingTransaction.setReadyToExecute(true)
                            if (craftingTransaction.getPrimaryOutput() == null) {
                                craftingTransaction.setPrimaryOutput(craftingEventPacket.output.get(0))
                            }
                        }
                    }
                }
                ProtocolInfo.BLOCK_ENTITY_DATA_PACKET -> {
                    if (!spawned || !this.isAlive()) {
                        break
                    }
                    val blockEntityDataPacket: BlockEntityDataPacket = packet as BlockEntityDataPacket
                    craftingType = CRAFTING_SMALL
                    resetCraftingGridType()
                    pos = Vector3(blockEntityDataPacket.x, blockEntityDataPacket.y, blockEntityDataPacket.z)
                    if (pos.distanceSquared(this) > 10000) {
                        break
                    }
                    val t: BlockEntity = this.level.getBlockEntity(pos)
                    if (t is BlockEntitySpawnable) {
                        val nbt: CompoundTag
                        nbt = try {
                            NBTIO.read(blockEntityDataPacket.namedTag, ByteOrder.LITTLE_ENDIAN, true)
                        } catch (e: IOException) {
                            throw RuntimeException(e)
                        }
                        if (!(t as BlockEntitySpawnable).updateCompoundTag(nbt, this)) {
                            (t as BlockEntitySpawnable).spawnTo(this)
                        }
                    }
                }
                ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET -> {
                    val requestChunkRadiusPacket: RequestChunkRadiusPacket = packet as RequestChunkRadiusPacket
                    val chunkRadiusUpdatePacket = ChunkRadiusUpdatedPacket()
                    chunkRadius = Math.max(3, Math.min(requestChunkRadiusPacket.radius, viewDistance))
                    chunkRadiusUpdatePacket.radius = chunkRadius
                    this.dataPacket(chunkRadiusUpdatePacket)
                }
                ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET -> {
                    val setPlayerGameTypePacket: SetPlayerGameTypePacket = packet as SetPlayerGameTypePacket
                    if (setPlayerGameTypePacket.gamemode !== gamemode) {
                        if (!this.hasPermission("nukkit.command.gamemode")) {
                            val setPlayerGameTypePacket1 = SetPlayerGameTypePacket()
                            setPlayerGameTypePacket1.gamemode = gamemode and 0x01
                            this.dataPacket(setPlayerGameTypePacket1)
                            getAdventureSettings()!!.update()
                            break
                        }
                        this.setGamemode(setPlayerGameTypePacket.gamemode, true)
                        Command.broadcastCommandMessage(this, TranslationContainer("commands.gamemode.success.self", Server.getGamemodeString(gamemode)))
                    }
                }
                ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET -> {
                    val itemFrameDropItemPacket: ItemFrameDropItemPacket = packet as ItemFrameDropItemPacket
                    val vector3: Vector3 = this.temporalVector.setComponents(itemFrameDropItemPacket.x, itemFrameDropItemPacket.y, itemFrameDropItemPacket.z)
                    val itemFrame: BlockEntity = this.level.getBlockEntity(vector3)
                    if (itemFrame is BlockEntityItemFrame) {
                        (itemFrame as BlockEntityItemFrame).dropItem(this)
                    }
                }
                ProtocolInfo.LECTERN_UPDATE_PACKET -> {
                    val lecternUpdatePacket: LecternUpdatePacket = packet as LecternUpdatePacket
                    val blockPosition: BlockVector3 = lecternUpdatePacket.blockPosition
                    this.temporalVector.setComponents(blockPosition.x, blockPosition.y, blockPosition.z)
                    if (lecternUpdatePacket.dropBook) {
                        val blockLectern: Block = this.getLevel().getBlock(temporalVector)
                        if (blockLectern is BlockLectern) {
                            (blockLectern as BlockLectern).dropBook(this)
                        }
                    } else {
                        val blockEntityLectern: BlockEntity = this.level.getBlockEntity(this.temporalVector)
                        if (blockEntityLectern is BlockEntityLectern) {
                            val lectern: BlockEntityLectern = blockEntityLectern as BlockEntityLectern
                            val lecternPageChangeEvent = LecternPageChangeEvent(this, lectern, lecternUpdatePacket.page)
                            server!!.getPluginManager().callEvent(lecternPageChangeEvent)
                            if (!lecternPageChangeEvent.isCancelled()) {
                                lectern.setRawPage(lecternPageChangeEvent.getNewRawPage())
                                lectern.spawnToAll()
                                val blockLectern: Block = lectern.getBlock()
                                if (blockLectern is BlockLectern) {
                                    (blockLectern as BlockLectern).executeRedstonePulse()
                                }
                            }
                        }
                    }
                }
                ProtocolInfo.MAP_INFO_REQUEST_PACKET -> {
                    val pk: MapInfoRequestPacket = packet as MapInfoRequestPacket
                    var mapItem: Item? = null
                    for (item1 in this.inventory.getContents().values()) {
                        if (item1 is ItemMap && (item1 as ItemMap).getMapId() === pk.mapId) {
                            mapItem = item1
                        }
                    }
                    if (mapItem == null) {
                        for (be in this.level.getBlockEntities().values()) {
                            if (be is BlockEntityItemFrame) {
                                val itemFrame1: BlockEntityItemFrame = be as BlockEntityItemFrame
                                if (itemFrame1.getItem() is ItemMap && (itemFrame1.getItem() as ItemMap).getMapId() === pk.mapId) {
                                    (itemFrame1.getItem() as ItemMap).sendImage(this)
                                    break
                                }
                            }
                        }
                    }
                    if (mapItem != null) {
                        var event: PlayerMapInfoRequestEvent
                        server!!.getPluginManager().callEvent(PlayerMapInfoRequestEvent(this, mapItem).also { event = it })
                        if (!event.isCancelled()) {
                            (mapItem as ItemMap).sendImage(this)
                        }
                    }
                }
                ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V1, ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V2, ProtocolInfo.LEVEL_SOUND_EVENT_PACKET -> if (!isSpectator || (packet as LevelSoundEventPacket).sound !== LevelSoundEventPacket.SOUND_HIT && (packet as LevelSoundEventPacket).sound !== LevelSoundEventPacket.SOUND_ATTACK_NODAMAGE) {
                    this.level.addChunkPacket(this.getChunkX(), this.getChunkZ(), packet)
                }
                ProtocolInfo.INVENTORY_TRANSACTION_PACKET -> {
                    if (isSpectator) {
                        sendAllInventories()
                        break
                    }
                    var transactionPacket: InventoryTransactionPacket = packet as InventoryTransactionPacket

                    // Nasty hack because the client won't change the right packet in survival when creating netherite stuff
                    // so we are emulating what Mojang should be sending
                    if (transactionPacket.transactionType === InventoryTransactionPacket.TYPE_MISMATCH && !isCreative
                            && getWindowById(SMITHING_WINDOW_ID).also { inv = it } is SmithingInventory) {
                        val smithingInventory: SmithingInventory = inv as SmithingInventory
                        if (!smithingInventory.getResult().isNull()) {
                            val fixedPacket = InventoryTransactionPacket()
                            fixedPacket.isRepairItemPart = true
                            fixedPacket.actions = arrayOfNulls<NetworkInventoryAction>(6)
                            val fromIngredient: Item = smithingInventory.getIngredient().clone()
                            val toIngredient: Item = fromIngredient.decrement(1)
                            val fromEquipment: Item = smithingInventory.getEquipment().clone()
                            val toEquipment: Item = fromEquipment.decrement(1)
                            val fromResult: Item = Item.getBlock(BlockID.AIR)
                            val toResult: Item = smithingInventory.getResult().clone()
                            var action = NetworkInventoryAction()
                            action.windowId = ContainerIds.UI
                            action.inventorySlot = SmithingInventory.SMITHING_INGREDIENT_UI_SLOT
                            action.oldItem = fromIngredient.clone()
                            action.newItem = toIngredient.clone()
                            fixedPacket.actions.get(0) = action
                            action = NetworkInventoryAction()
                            action.windowId = ContainerIds.UI
                            action.inventorySlot = SmithingInventory.SMITHING_EQUIPMENT_UI_SLOT
                            action.oldItem = fromEquipment.clone()
                            action.newItem = toEquipment.clone()
                            fixedPacket.actions.get(1) = action
                            var emptyPlayerSlot = -1
                            var slot = 0
                            while (slot < inventory.getSize()) {
                                if (inventory.getItem(slot).isNull()) {
                                    emptyPlayerSlot = slot
                                    break
                                }
                                slot++
                            }
                            if (emptyPlayerSlot == -1) {
                                sendAllInventories()
                                cursorInventory.sendContents(this)
                            } else {
                                action = NetworkInventoryAction()
                                action.windowId = ContainerIds.INVENTORY
                                action.inventorySlot = emptyPlayerSlot // Cursor
                                action.oldItem = Item.getBlock(BlockID.AIR)
                                action.newItem = toResult.clone()
                                fixedPacket.actions.get(2) = action
                                action = NetworkInventoryAction()
                                action.sourceType = NetworkInventoryAction.SOURCE_TODO
                                action.windowId = NetworkInventoryAction.SOURCE_TYPE_ANVIL_RESULT
                                action.inventorySlot = 2 // result
                                action.oldItem = toResult.clone()
                                action.newItem = fromResult.clone()
                                fixedPacket.actions.get(3) = action
                                action = NetworkInventoryAction()
                                action.sourceType = NetworkInventoryAction.SOURCE_TODO
                                action.windowId = NetworkInventoryAction.SOURCE_TYPE_ANVIL_INPUT
                                action.inventorySlot = 0 // equipment
                                action.oldItem = toEquipment.clone()
                                action.newItem = fromEquipment.clone()
                                fixedPacket.actions.get(4) = action
                                action = NetworkInventoryAction()
                                action.sourceType = NetworkInventoryAction.SOURCE_TODO
                                action.windowId = NetworkInventoryAction.SOURCE_TYPE_ANVIL_MATERIAL
                                action.inventorySlot = 1 // material
                                action.oldItem = toIngredient.clone()
                                action.newItem = fromIngredient.clone()
                                fixedPacket.actions.get(5) = action
                                transactionPacket = fixedPacket
                            }
                        }
                    }
                    val actions: List<InventoryAction> = ArrayList()
                    for (networkInventoryAction in transactionPacket.actions) {
                        if (craftingType == CRAFTING_STONECUTTER && craftingTransaction != null && networkInventoryAction.sourceType === NetworkInventoryAction.SOURCE_TODO) {
                            networkInventoryAction.windowId = NetworkInventoryAction.SOURCE_TYPE_CRAFTING_RESULT
                        } else if (craftingType == CRAFTING_CARTOGRAPHY && craftingTransaction != null && transactionPacket.actions.length === 2 && transactionPacket.actions.get(1).windowId === ContainerIds.UI && networkInventoryAction.inventorySlot === 0) {
                            val slot: Int = transactionPacket.actions.get(1).inventorySlot
                            if (slot == 50) {
                                networkInventoryAction.windowId = NetworkInventoryAction.SOURCE_TYPE_CRAFTING_RESULT
                            } else {
                                networkInventoryAction.inventorySlot = slot - 12
                            }
                        }
                        val a: InventoryAction = networkInventoryAction.createInventoryAction(this)
                        if (a == null) {
                            log.debug("Unmatched inventory action from {}: {}", name, networkInventoryAction)
                            sendAllInventories()
                            break@packetswitch
                        }
                        actions.add(a)
                    }
                    if (transactionPacket.isCraftingPart) {
                        if (craftingTransaction == null) {
                            craftingTransaction = CraftingTransaction(this, actions)
                        } else {
                            for (action in actions) {
                                craftingTransaction.addAction(action)
                            }
                        }
                        if (craftingTransaction.getPrimaryOutput() != null && (craftingTransaction.isReadyToExecute() || craftingTransaction.canExecute())) {
                            //we get the actions for this in several packets, so we can't execute it until we get the result
                            if (craftingTransaction.execute()) {
                                var sound: Sound? = null
                                when (craftingType) {
                                    CRAFTING_STONECUTTER -> sound = Sound.BLOCK_STONECUTTER_USE
                                    CRAFTING_CARTOGRAPHY -> sound = Sound.BLOCK_CARTOGRAPHY_TABLE_USE
                                }
                                if (sound != null) {
                                    val players: Collection<Player> = level.getChunkPlayers(getChunkX(), getChunkZ()).values()
                                    players.remove(this)
                                    if (!players.isEmpty()) {
                                        level.addSound(this, sound, 1f, 1f, players)
                                    }
                                }
                            }
                            craftingTransaction = null
                        }
                        return
                    } else if (transactionPacket.isEnchantingPart) {
                        if (enchantTransaction == null) {
                            enchantTransaction = EnchantTransaction(this, actions)
                        } else {
                            for (action in actions) {
                                enchantTransaction.addAction(action)
                            }
                        }
                        if (enchantTransaction.canExecute()) {
                            enchantTransaction.execute()
                            enchantTransaction = null
                        }
                        return
                    } else if (transactionPacket.isRepairItemPart) {
                        var sound: Sound? = null
                        if (GrindstoneTransaction.checkForItemPart(actions)) {
                            if (grindstoneTransaction == null) {
                                grindstoneTransaction = GrindstoneTransaction(this, actions)
                            } else {
                                for (action in actions) {
                                    grindstoneTransaction.addAction(action)
                                }
                            }
                            if (grindstoneTransaction.canExecute()) {
                                try {
                                    if (grindstoneTransaction.execute()) {
                                        sound = Sound.BLOCK_GRINDSTONE_USE
                                    }
                                } finally {
                                    grindstoneTransaction = null
                                }
                            }
                        } else if (SmithingTransaction.checkForItemPart(actions)) {
                            if (smithingTransaction == null) {
                                smithingTransaction = SmithingTransaction(this, actions)
                            } else {
                                for (action in actions) {
                                    smithingTransaction.addAction(action)
                                }
                            }
                            if (smithingTransaction.canExecute()) {
                                try {
                                    if (smithingTransaction.execute()) {
                                        sound = Sound.SMITHING_TABLE_USE
                                    }
                                } finally {
                                    smithingTransaction = null
                                }
                            }
                        } else {
                            if (repairItemTransaction == null) {
                                repairItemTransaction = RepairItemTransaction(this, actions)
                            } else {
                                for (action in actions) {
                                    repairItemTransaction.addAction(action)
                                }
                            }
                            if (repairItemTransaction.canExecute()) {
                                try {
                                    repairItemTransaction.execute()
                                } finally {
                                    repairItemTransaction = null
                                }
                            }
                        }
                        if (sound != null) {
                            val players: Collection<Player> = level.getChunkPlayers(getChunkX(), getChunkZ()).values()
                            players.remove(this)
                            if (!players.isEmpty()) {
                                level.addSound(this, sound, 1f, 1f, players)
                            }
                        }
                        return
                    } else if (craftingTransaction != null) {
                        if (craftingTransaction.checkForCraftingPart(actions)) {
                            for (action in actions) {
                                craftingTransaction.addAction(action)
                            }
                            return
                        } else {
                            log.debug("Got unexpected normal inventory action with incomplete crafting transaction from {}, refusing to execute crafting", name)
                            removeAllWindows(false)
                            sendAllInventories()
                            craftingTransaction = null
                        }
                    } else if (enchantTransaction != null) {
                        if (enchantTransaction.checkForEnchantPart(actions)) {
                            for (action in actions) {
                                enchantTransaction.addAction(action)
                            }
                            return
                        } else {
                            log.debug("Got unexpected normal inventory action with incomplete enchanting transaction from {}, refusing to execute enchant {}", name, transactionPacket.toString())
                            removeAllWindows(false)
                            sendAllInventories()
                            enchantTransaction = null
                        }
                    } else if (repairItemTransaction != null) {
                        if (RepairItemTransaction.checkForRepairItemPart(actions)) {
                            for (action in actions) {
                                repairItemTransaction.addAction(action)
                            }
                            return
                        } else {
                            log.debug("Got unexpected normal inventory action with incomplete repair item transaction from " + name + ", refusing to execute repair item " + transactionPacket.toString())
                            removeAllWindows(false)
                            sendAllInventories()
                            repairItemTransaction = null
                        }
                    } else if (grindstoneTransaction != null) {
                        if (GrindstoneTransaction.checkForItemPart(actions)) {
                            for (action in actions) {
                                grindstoneTransaction.addAction(action)
                            }
                            return
                        } else {
                            log.debug("Got unexpected normal inventory action with incomplete grindstone transaction from {}, refusing to execute use the grindstone {}", name, transactionPacket.toString())
                            removeAllWindows(false)
                            sendAllInventories()
                            grindstoneTransaction = null
                        }
                    } else if (smithingTransaction != null) {
                        if (SmithingTransaction.checkForItemPart(actions)) {
                            for (action in actions) {
                                smithingTransaction.addAction(action)
                            }
                            return
                        } else {
                            log.debug("Got unexpected normal inventory action with incomplete smithing table transaction from {}, refusing to execute use the smithing table {}", name, transactionPacket.toString())
                            removeAllWindows(false)
                            sendAllInventories()
                            smithingTransaction = null
                        }
                    }
                    when (transactionPacket.transactionType) {
                        InventoryTransactionPacket.TYPE_NORMAL -> {
                            val transaction = InventoryTransaction(this, actions)
                            if (!transaction.execute()) {
                                log.debug("Failed to execute inventory transaction from {} with actions: {}", name, Arrays.toString(transactionPacket.actions))
                                break@packetswitch  //oops!
                            }

                            //TODO: fix achievement for getting iron from furnace
                            break@packetswitch
                        }
                        InventoryTransactionPacket.TYPE_MISMATCH -> {
                            if (transactionPacket.actions.length > 0) {
                                log.debug("Expected 0 actions for mismatch, got {}, {}", transactionPacket.actions.length, Arrays.toString(transactionPacket.actions))
                            }
                            sendAllInventories()
                            break@packetswitch
                        }
                        InventoryTransactionPacket.TYPE_USE_ITEM -> {
                            val useItemData: UseItemData = transactionPacket.transactionData as UseItemData
                            val blockVector: BlockVector3 = useItemData.blockPos
                            face = useItemData.face
                            val type: Int = useItemData.actionType
                            when (type) {
                                InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_BLOCK -> {
                                    // Remove if client bug is ever fixed
                                    val spamBug = lastRightClickPos != null && System.currentTimeMillis() - lastRightClickTime < 100.0 && blockVector.distanceSquared(lastRightClickPos) < 0.00001
                                    lastRightClickPos = blockVector.asVector3()
                                    lastRightClickTime = System.currentTimeMillis()
                                    if (spamBug && this.getInventory().getItemInHand().getBlockId() === BlockID.AIR) {
                                        return
                                    }
                                    this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false)
                                    if (this.canInteract(blockVector.add(0.5, 0.5, 0.5), if (isCreative) 13 else 7.toDouble())) {
                                        if (isCreative) {
                                            val i: Item = inventory.getItemInHand()
                                            if (this.level.useItemOn(blockVector.asVector3(), i, face, useItemData.clickPos.x, useItemData.clickPos.y, useItemData.clickPos.z, this) != null) {
                                                break@packetswitch
                                            }
                                        } else if (inventory.getItemInHand().equals(useItemData.itemInHand)) {
                                            var i: Item = inventory.getItemInHand()
                                            val oldItem: Item = i.clone()
                                            //TODO: Implement adventure mode checks
                                            if (this.level.useItemOn(blockVector.asVector3(), i, face, useItemData.clickPos.x, useItemData.clickPos.y, useItemData.clickPos.z, this).also { i = it } != null) {
                                                if (!i.equals(oldItem) || i.getCount() !== oldItem.getCount()) {
                                                    inventory.setItemInHand(i)
                                                    inventory.sendHeldItem(this.getViewers().values())
                                                }
                                                break@packetswitch
                                            }
                                        }
                                    }
                                    inventory.sendHeldItem(this)
                                    if (blockVector.distanceSquared(this) > 10000) {
                                        break@packetswitch
                                    }
                                    val target: Block = this.level.getBlock(blockVector.asVector3())
                                    block = target.getSide(face)
                                    this.level.sendBlocks(arrayOf(this), arrayOf<Block>(target, block), UpdateBlockPacket.FLAG_NOGRAPHIC)
                                    this.level.sendBlocks(arrayOf(this), arrayOf<Block>(target.getLevelBlockAtLayer(1), block.getLevelBlockAtLayer(1)), UpdateBlockPacket.FLAG_NOGRAPHIC, 1)
                                    break@packetswitch
                                }
                                InventoryTransactionPacket.USE_ITEM_ACTION_BREAK_BLOCK -> {
                                    if (!spawned || !this.isAlive()) {
                                        break@packetswitch
                                    }
                                    resetCraftingGridType()
                                    var i: Item = this.getInventory().getItemInHand()
                                    val oldItem: Item = i.clone()
                                    if (this.canInteract(blockVector.add(0.5, 0.5, 0.5), if (isCreative) 13 else 7.toDouble()) && this.level.useBreakOn(blockVector.asVector3(), face, i, this, true).also { i = it } != null) {
                                        if (isSurvival || isAdventure) {
                                            getFoodData()!!.updateFoodExpLevel(0.025)
                                            if (!i.equals(oldItem) || i.getCount() !== oldItem.getCount()) {
                                                inventory.setItemInHand(i)
                                                inventory.sendHeldItem(this.getViewers().values())
                                            }
                                        }
                                        break@packetswitch
                                    }
                                    inventory.sendContents(this)
                                    target = this.level.getBlock(blockVector.asVector3())
                                    val blockEntity: BlockEntity = this.level.getBlockEntity(blockVector.asVector3())
                                    this.level.sendBlocks(arrayOf(this), arrayOf<Block>(target), UpdateBlockPacket.FLAG_ALL_PRIORITY)
                                    inventory.sendHeldItem(this)
                                    if (blockEntity is BlockEntitySpawnable) {
                                        (blockEntity as BlockEntitySpawnable).spawnTo(this)
                                    }
                                    break@packetswitch
                                }
                                InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_AIR -> {
                                    val directionVector: Vector3 = this.getDirectionVector()
                                    if (isCreative) {
                                        item = this.inventory.getItemInHand()
                                    } else if (!this.inventory.getItemInHand().equals(useItemData.itemInHand)) {
                                        this.inventory.sendHeldItem(this)
                                        break@packetswitch
                                    } else {
                                        item = this.inventory.getItemInHand()
                                    }
                                    val interactEvent = PlayerInteractEvent(this, item, directionVector, face, Action.RIGHT_CLICK_AIR)
                                    server!!.getPluginManager().callEvent(interactEvent)
                                    if (interactEvent.isCancelled()) {
                                        this.inventory.sendHeldItem(this)
                                        break@packetswitch
                                    }
                                    if (item.onClickAir(this, directionVector)) {
                                        if (!isCreative) {
                                            this.inventory.setItemInHand(item)
                                        }
                                        if (!isUsingItem) {
                                            isUsingItem = true
                                            break@packetswitch
                                        }

                                        // Used item
                                        val ticksUsed: Int = server.getTick() - startActionTick
                                        isUsingItem = false
                                        if (!item.onUse(this, ticksUsed)) {
                                            this.inventory.sendContents(this)
                                        }
                                    }
                                    break@packetswitch
                                }
                                else -> {
                                }
                            }
                        }
                        InventoryTransactionPacket.TYPE_USE_ITEM_ON_ENTITY -> {
                            val useItemOnEntityData: UseItemOnEntityData = transactionPacket.transactionData as UseItemOnEntityData
                            val target: Entity = this.level.getEntity(useItemOnEntityData.entityRuntimeId)
                            if (target == null) {
                                return
                            }
                            type = useItemOnEntityData.actionType
                            if (!useItemOnEntityData.itemInHand.equalsExact(this.inventory.getItemInHand())) {
                                this.inventory.sendHeldItem(this)
                            }
                            item = this.inventory.getItemInHand()
                            when (type) {
                                InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_INTERACT -> {
                                    val playerInteractEntityEvent = PlayerInteractEntityEvent(this, target, item, useItemOnEntityData.clickPos)
                                    if (isSpectator) playerInteractEntityEvent.setCancelled()
                                    server!!.getPluginManager().callEvent(playerInteractEntityEvent)
                                    if (playerInteractEntityEvent.isCancelled()) {
                                        break
                                    }
                                    if (target.onInteract(this, item, useItemOnEntityData.clickPos) && (isSurvival || isAdventure)) {
                                        if (item.isTool()) {
                                            if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                                                level.addSound(this, Sound.RANDOM_BREAK)
                                                item = ItemBlock(Block.get(BlockID.AIR))
                                            }
                                        } else {
                                            if (item.count > 1) {
                                                item.count--
                                            } else {
                                                item = ItemBlock(Block.get(BlockID.AIR))
                                            }
                                        }
                                        this.inventory.setItemInHand(item)
                                    }
                                }
                                InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_ATTACK -> {
                                    if (target.getId() === this.getId()) {
                                        this.kick(PlayerKickEvent.Reason.INVALID_PVP, "Attempting to attack yourself")
                                        log.warn(name.toString() + " tried to attack oneself")
                                        break
                                    }
                                    var itemDamage: Float = item.getAttackDamage()
                                    for (enchantment in item.getEnchantments()) {
                                        itemDamage += enchantment.getDamageBonus(target)
                                    }
                                    val damage: Map<DamageModifier, Float> = EnumMap(DamageModifier::class.java)
                                    damage.put(DamageModifier.BASE, itemDamage)
                                    if (!this.canInteract(target, if (isCreative) 8 else 5.toDouble())) {
                                        break
                                    } else if (target is Player) {
                                        if ((target as Player).gamemode and 0x01 > 0) {
                                            break
                                        } else if (!server!!.getPropertyBoolean("pvp") || server!!.getDifficulty() === 0) {
                                            break
                                        }
                                    }
                                    val entityDamageByEntityEvent = EntityDamageByEntityEvent(this, target, DamageCause.ENTITY_ATTACK, damage)
                                    if (isSpectator) entityDamageByEntityEvent.setCancelled()
                                    if (target is Player && !this.level.getGameRules().getBoolean(GameRule.PVP)) {
                                        entityDamageByEntityEvent.setCancelled()
                                    }
                                    if (target is EntityLiving) {
                                        (target as EntityLiving).preAttack(this)
                                    }
                                    try {
                                        if (!target.attack(entityDamageByEntityEvent)) {
                                            if (item.isTool() && isSurvival) {
                                                this.inventory.sendContents(this)
                                            }
                                            break
                                        }
                                    } finally {
                                        if (target is EntityLiving) {
                                            (target as EntityLiving).postAttack(this)
                                        }
                                    }
                                    for (enchantment in item.getEnchantments()) {
                                        enchantment.doPostAttack(this, target)
                                    }
                                    if (item.isTool() && isSurvival) {
                                        if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                                            level.addSound(this, Sound.RANDOM_BREAK)
                                            this.inventory.setItemInHand(ItemBlock(Block.get(BlockID.AIR)))
                                        } else {
                                            this.inventory.setItemInHand(item)
                                        }
                                    }
                                    return
                                }
                                else -> {
                                }
                            }
                        }
                        InventoryTransactionPacket.TYPE_RELEASE_ITEM -> {
                            if (isSpectator) {
                                sendAllInventories()
                                break@packetswitch
                            }
                            val releaseItemData: ReleaseItemData = transactionPacket.transactionData as ReleaseItemData
                            try {
                                type = releaseItemData.actionType
                                when (type) {
                                    InventoryTransactionPacket.RELEASE_ITEM_ACTION_RELEASE -> {
                                        if (isUsingItem) {
                                            item = this.inventory.getItemInHand()
                                            val ticksUsed: Int = server.getTick() - startActionTick
                                            if (!item.onRelease(this, ticksUsed)) {
                                                this.inventory.sendContents(this)
                                            }
                                            isUsingItem = false
                                        } else {
                                            this.inventory.sendContents(this)
                                        }
                                        return
                                    }
                                    InventoryTransactionPacket.RELEASE_ITEM_ACTION_CONSUME -> {
                                        log.debug("Unexpected release item action consume from {}") { name }
                                        return
                                    }
                                    else -> {
                                    }
                                }
                            } finally {
                                isUsingItem = false
                            }
                        }
                        else -> this.inventory.sendContents(this)
                    }
                }
                ProtocolInfo.PLAYER_HOTBAR_PACKET -> {
                    val hotbarPacket: PlayerHotbarPacket = packet as PlayerHotbarPacket
                    if (hotbarPacket.windowId !== ContainerIds.INVENTORY) {
                        return  //In PE this should never happen
                    }
                    this.inventory.equipItem(hotbarPacket.selectedHotbarSlot)
                }
                ProtocolInfo.SERVER_SETTINGS_REQUEST_PACKET -> {
                    val settingsRequestEvent = PlayerServerSettingsRequestEvent(this, HashMap(serverSettings))
                    server!!.getPluginManager().callEvent(settingsRequestEvent)
                    if (!settingsRequestEvent.isCancelled()) {
                        settingsRequestEvent.getSettings().forEach { id, window ->
                            val re = ServerSettingsResponsePacket()
                            re.formId = id
                            re.data = window.getJSONData()
                            this.dataPacket(re)
                        }
                    }
                }
                ProtocolInfo.RESPAWN_PACKET -> {
                    if (this.isAlive()) {
                        break
                    }
                    val respawnPacket: RespawnPacket = packet as RespawnPacket
                    if (respawnPacket.respawnState === RespawnPacket.STATE_CLIENT_READY_TO_SPAWN) {
                        val respawn1 = RespawnPacket()
                        respawn1.x = this.getX() as Float
                        respawn1.y = this.getY() as Float
                        respawn1.z = this.getZ() as Float
                        respawn1.respawnState = RespawnPacket.STATE_READY_TO_SPAWN
                        this.dataPacket(respawn1)
                    }
                }
                ProtocolInfo.BOOK_EDIT_PACKET -> {
                    val bookEditPacket: BookEditPacket = packet as BookEditPacket
                    val oldBook: Item = this.inventory.getItem(bookEditPacket.inventorySlot)
                    if (oldBook.getId() !== Item.BOOK_AND_QUILL) {
                        return
                    }
                    if (bookEditPacket.text == null || bookEditPacket.text.length() > 256) {
                        return
                    }
                    var newBook: Item = oldBook.clone()
                    val success: Boolean
                    when (bookEditPacket.action) {
                        REPLACE_PAGE -> success = (newBook as ItemBookAndQuill).setPageText(bookEditPacket.pageNumber, bookEditPacket.text)
                        ADD_PAGE -> success = (newBook as ItemBookAndQuill).insertPage(bookEditPacket.pageNumber, bookEditPacket.text)
                        DELETE_PAGE -> success = (newBook as ItemBookAndQuill).deletePage(bookEditPacket.pageNumber)
                        SWAP_PAGES -> success = (newBook as ItemBookAndQuill).swapPages(bookEditPacket.pageNumber, bookEditPacket.secondaryPageNumber)
                        SIGN_BOOK -> {
                            newBook = Item.get(Item.WRITTEN_BOOK, 0, 1, oldBook.getCompoundTag())
                            success = (newBook as ItemBookWritten).signBook(bookEditPacket.title, bookEditPacket.author, bookEditPacket.xuid, ItemBookWritten.GENERATION_ORIGINAL)
                        }
                        else -> return
                    }
                    if (success) {
                        val editBookEvent = PlayerEditBookEvent(this, oldBook, newBook, bookEditPacket.action)
                        server!!.getPluginManager().callEvent(editBookEvent)
                        if (!editBookEvent.isCancelled()) {
                            this.inventory.setItem(bookEditPacket.inventorySlot, editBookEvent.getNewBook())
                        }
                    }
                }
                ProtocolInfo.FILTER_TEXT_PACKET -> {
                    val filterTextPacket: FilterTextPacket = packet as FilterTextPacket
                    val textResponsePacket = FilterTextPacket()
                    if (craftingType == CRAFTING_ANVIL) {
                        val anvilInventory: AnvilInventory = getWindowById(ANVIL_WINDOW_ID) as AnvilInventory
                        if (anvilInventory != null) {
                            val playerTypingAnvilInventoryEvent = PlayerTypingAnvilInventoryEvent(
                                    this, anvilInventory, anvilInventory.getNewItemName(), filterTextPacket.getText()
                            )
                            server!!.getPluginManager().callEvent(playerTypingAnvilInventoryEvent)
                            anvilInventory.setNewItemName(playerTypingAnvilInventoryEvent.getTypedName())
                        }
                    }
                    textResponsePacket.text = filterTextPacket.text
                    textResponsePacket.fromServer = true
                    this.dataPacket(textResponsePacket)
                }
                ProtocolInfo.POS_TRACKING_CLIENT_REQUEST_PACKET -> {
                    val posTrackReq: PositionTrackingDBClientRequestPacket = packet as PositionTrackingDBClientRequestPacket
                    try {
                        val positionTracking: PositionTracking = server!!.getPositionTrackingService().startTracking(this, posTrackReq.getTrackingId(), true)
                        if (positionTracking != null) {
                            break
                        }
                    } catch (e: IOException) {
                        log.warn("Failed to track the trackingHandler {}", posTrackReq.getTrackingId(), e)
                    }
                    val notFound = PositionTrackingDBServerBroadcastPacket()
                    notFound.setAction(PositionTrackingDBServerBroadcastPacket.Action.NOT_FOUND)
                    notFound.setTrackingId(posTrackReq.getTrackingId())
                    dataPacket(notFound)
                }
                ProtocolInfo.TICK_SYNC_PACKET -> {
                    val tickSyncPacket: TickSyncPacket = packet as TickSyncPacket
                    val tickSyncPacketToClient = TickSyncPacket()
                    tickSyncPacketToClient.setRequestTimestamp(tickSyncPacket.getRequestTimestamp())
                    tickSyncPacketToClient.setResponseTimestamp(server.getTick())
                    dataPacketImmediately(tickSyncPacketToClient)
                }
                else -> {
                }
            }
        }
    }

    /**
     * Sends a chat message as this player. If the message begins with a / (forward-slash) it will be treated
     * as a command.
     * @param message message to send
     * @return successful
     */
    fun chat(message: String): Boolean {
        var message = message
        if (!spawned || !this.isAlive()) {
            return false
        }
        resetCraftingGridType()
        craftingType = CRAFTING_SMALL
        if (removeFormat) {
            message = TextFormat.clean(message, true)
        }
        for (msg in message.split("\n")) {
            if (!msg.trim().isEmpty() && msg.length() <= 255 && messageCounter-- > 0) {
                val chatEvent = PlayerChatEvent(this, msg)
                server!!.getPluginManager().callEvent(chatEvent)
                if (!chatEvent.isCancelled()) {
                    server.broadcastMessage(server.getLanguage().translateString(chatEvent.getFormat(), arrayOf<String>(chatEvent.getPlayer().getDisplayName(), chatEvent.getMessage())), chatEvent.getRecipients())
                }
            }
        }
        return true
    }

    fun kick(reason: String, isAdmin: Boolean): Boolean {
        return this.kick(PlayerKickEvent.Reason.UNKNOWN, reason, isAdmin)
    }

    @JvmOverloads
    fun kick(reason: String = ""): Boolean {
        return kick(PlayerKickEvent.Reason.UNKNOWN, reason)
    }

    fun kick(reason: PlayerKickEvent.Reason?): Boolean {
        return this.kick(reason, true)
    }

    fun kick(reason: PlayerKickEvent.Reason?, reasonString: String): Boolean {
        return this.kick(reason, reasonString, true)
    }

    fun kick(reason: PlayerKickEvent.Reason, isAdmin: Boolean): Boolean {
        return this.kick(reason, reason.toString(), isAdmin)
    }

    fun kick(reason: PlayerKickEvent.Reason?, reasonString: String, isAdmin: Boolean): Boolean {
        var ev: PlayerKickEvent
        server!!.getPluginManager().callEvent(PlayerKickEvent(this, reason, leaveMessage).also { ev = it })
        if (!ev.isCancelled()) {
            val message: String
            message = if (isAdmin) {
                if (!isBanned) {
                    "Kicked by admin." + if (!reasonString.isEmpty()) " Reason: $reasonString" else ""
                } else {
                    reasonString
                }
            } else {
                if (reasonString.isEmpty()) {
                    "disconnectionScreen.noReason"
                } else {
                    reasonString
                }
            }
            this.close(ev.getQuitMessage(), message)
            return true
        }
        return false
    }

    fun setViewDistance(distance: Int) {
        chunkRadius = distance
        val pk = ChunkRadiusUpdatedPacket()
        pk.radius = distance
        this.dataPacket(pk)
    }

    fun getViewDistance(): Int {
        return chunkRadius
    }

    @Override
    fun sendMessage(message: String?) {
        val pk = TextPacket()
        pk.type = TextPacket.TYPE_RAW
        pk.message = server.getLanguage().translateString(message)
        this.dataPacket(pk)
    }

    @Override
    fun sendMessage(message: TextContainer) {
        if (message is TranslationContainer) {
            this.sendTranslation(message.getText(), (message as TranslationContainer).getParameters())
            return
        }
        this.sendMessage(message.getText())
    }

    fun sendTranslation(message: String?) {
        this.sendTranslation(message, EmptyArrays.EMPTY_STRINGS)
    }

    fun sendTranslation(message: String?, parameters: Array<String?>) {
        val pk = TextPacket()
        if (!server!!.isLanguageForced()) {
            pk.type = TextPacket.TYPE_TRANSLATION
            pk.message = server.getLanguage().translateString(message, parameters, "nukkit.")
            for (i in parameters.indices) {
                parameters[i] = server.getLanguage().translateString(parameters[i], parameters, "nukkit.")
            }
            pk.parameters = parameters
        } else {
            pk.type = TextPacket.TYPE_RAW
            pk.message = server.getLanguage().translateString(message, parameters)
        }
        this.dataPacket(pk)
    }

    fun sendChat(message: String?) {
        this.sendChat("", message)
    }

    fun sendChat(source: String, message: String?) {
        val pk = TextPacket()
        pk.type = TextPacket.TYPE_CHAT
        pk.source = source
        pk.message = server.getLanguage().translateString(message)
        this.dataPacket(pk)
    }

    fun sendPopup(message: String) {
        this.sendPopup(message, "")
    }

    // TODO: Support Translation Parameters
    fun sendPopup(message: String, subtitle: String?) {
        val pk = TextPacket()
        pk.type = TextPacket.TYPE_POPUP
        pk.message = message
        this.dataPacket(pk)
    }

    fun sendTip(message: String) {
        val pk = TextPacket()
        pk.type = TextPacket.TYPE_TIP
        pk.message = message
        this.dataPacket(pk)
    }

    fun clearTitle() {
        val pk = SetTitlePacket()
        pk.type = SetTitlePacket.TYPE_CLEAR
        this.dataPacket(pk)
    }

    /**
     * Resets both title animation times and subtitle for the next shown title
     */
    fun resetTitleSettings() {
        val pk = SetTitlePacket()
        pk.type = SetTitlePacket.TYPE_RESET
        this.dataPacket(pk)
    }

    fun setSubtitle(subtitle: String) {
        val pk = SetTitlePacket()
        pk.type = SetTitlePacket.TYPE_SUBTITLE
        pk.text = subtitle
        this.dataPacket(pk)
    }

    fun setTitleAnimationTimes(fadein: Int, duration: Int, fadeout: Int) {
        val pk = SetTitlePacket()
        pk.type = SetTitlePacket.TYPE_ANIMATION_TIMES
        pk.fadeInTime = fadein
        pk.stayTime = duration
        pk.fadeOutTime = fadeout
        this.dataPacket(pk)
    }

    private fun setTitle(text: String) {
        val packet = SetTitlePacket()
        packet.text = text
        packet.type = SetTitlePacket.TYPE_TITLE
        this.dataPacket(packet)
    }

    fun sendTitle(title: String?) {
        this.sendTitle(title, null, 20, 20, 5)
    }

    fun sendTitle(title: String?, subtitle: String?) {
        this.sendTitle(title, subtitle, 20, 20, 5)
    }

    fun sendTitle(title: String?, subtitle: String?, fadeIn: Int, stay: Int, fadeOut: Int) {
        setTitleAnimationTimes(fadeIn, stay, fadeOut)
        if (!Strings.isNullOrEmpty(subtitle)) {
            setSubtitle(subtitle!!)
        }
        // title won't send if an empty string is used.
        setTitle((if (Strings.isNullOrEmpty(title)) " " else title)!!)
    }

    fun sendActionBar(title: String) {
        this.sendActionBar(title, 1, 0, 1)
    }

    fun sendActionBar(title: String, fadein: Int, duration: Int, fadeout: Int) {
        val pk = SetTitlePacket()
        pk.type = SetTitlePacket.TYPE_ACTION_BAR
        pk.text = title
        pk.fadeInTime = fadein
        pk.stayTime = duration
        pk.fadeOutTime = fadeout
        this.dataPacket(pk)
    }

    @Override
    fun close() {
        this.close("")
    }

    fun close(message: String?) {
        this.close(message, "generic")
    }

    fun close(message: String?, reason: String?) {
        this.close(message, reason, true)
    }

    fun close(message: String?, reason: String?, notify: Boolean) {
        this.close(TextContainer(message), reason, notify)
    }

    fun close(message: TextContainer?) {
        this.close(message, "generic")
    }

    fun close(message: TextContainer?, reason: String?) {
        this.close(message, reason, true)
    }

    fun close(message: TextContainer?, reason: String, notify: Boolean) {
        if (isConnected && !this.closed) {
            if (notify && reason.length() > 0) {
                val pk = DisconnectPacket()
                pk.message = reason
                dataPacketImmediately(pk) // Send DisconnectPacket before the connection is closed, so its reason will show properly
            }
            isConnected = false
            var ev: PlayerQuitEvent? = null
            if (name != null && name!!.length() > 0) {
                server!!.getPluginManager().callEvent(PlayerQuitEvent(this, message, true, reason).also { ev = it })
                if (fishing != null) {
                    stopFishing(false)
                }
            }

            // Close the temporary windows first, so they have chance to change all inventories before being disposed
            removeAllWindows(false)
            resetCraftingGridType()
            if (ev != null && loggedIn && ev.getAutoSave()) {
                save()
            }
            for (player in ArrayList(server.getOnlinePlayers().values())) {
                if (!player.canSee(this)) {
                    player.showPlayer(this)
                }
            }
            hiddenPlayers.clear()
            removeAllWindows(true)
            for (index in ArrayList(usedChunks.keySet())) {
                val chunkX: Int = Level.getHashX(index)
                val chunkZ: Int = Level.getHashZ(index)
                this.level.unregisterChunkLoader(this, chunkX, chunkZ)
                usedChunks.remove(index)
                for (entity in level.getChunkEntities(chunkX, chunkZ).values()) {
                    if (entity !== this) {
                        entity.getViewers().remove(loaderId)
                    }
                }
            }
            super.close()
            interfaz.close(this, if (notify) reason else "")
            if (loggedIn) {
                server!!.removeOnlinePlayer(this)
            }
            loggedIn = false
            if (ev != null && !Objects.equals(name, "") && spawned && !Objects.equals(ev.getQuitMessage().toString(), "")) {
                server.broadcastMessage(ev.getQuitMessage())
            }
            server!!.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_USERS, this)
            spawned = false
            log.info(server.getLanguage().translateString("nukkit.player.logOut",
                    TextFormat.AQUA.toString() + (if (name == null) "" else name) + TextFormat.WHITE,
                    address,
                    String.valueOf(port),
                    server.getLanguage().translateString(reason)))
            windows.clear()
            usedChunks.clear()
            loadQueue.clear()
            this.hasSpawned.clear()
            spawnPosition = null
            if (this.riding is EntityRideable) {
                this.riding.passengers.remove(this)
            }
            this.riding = null
        }
        if (perm != null) {
            perm.clearPermissions()
            perm = null
        }
        if (this.inventory != null) {
            this.inventory = null
        }
        this.chunk = null
        server!!.removePlayer(this)
    }

    @Override
    fun saveNBT() {
        super.saveNBT()
        if (spawnBlockPosition == null) {
            namedTag.remove("SpawnBlockPositionX").remove("SpawnBlockPositionY").remove("SpawnBlockPositionZ")
        } else {
            namedTag.putInt("SpawnBlockPositionX", spawnBlockPosition.getFloorX())
                    .putInt("SpawnBlockPositionY", spawnBlockPosition.getFloorY())
                    .putInt("SpawnBlockPositionZ", spawnBlockPosition.getFloorZ())
        }
    }

    @JvmOverloads
    fun save(async: Boolean = false) {
        if (this.closed) {
            throw IllegalStateException("Tried to save closed player")
        }
        saveNBT()
        if (this.level != null) {
            this.namedTag.putString("Level", this.level.getFolderName())
            if (spawnPosition != null && spawnPosition.getLevel() != null) {
                this.namedTag.putString("SpawnLevel", spawnPosition.getLevel().getFolderName())
                this.namedTag.putInt("SpawnX", spawnPosition.getFloorX())
                this.namedTag.putInt("SpawnY", spawnPosition.getFloorY())
                this.namedTag.putInt("SpawnZ", spawnPosition.getFloorZ())
                this.namedTag.putInt("SpawnDimension", spawnPosition.getLevel().getDimension())
            }
            val achievements = CompoundTag()
            for (achievement in this.achievements) {
                achievements.putByte(achievement, 1)
            }
            this.namedTag.putCompound("Achievements", achievements)
            this.namedTag.putInt("playerGameType", gamemode)
            this.namedTag.putLong("lastPlayed", System.currentTimeMillis() / 1000)
            this.namedTag.putString("lastIP", address)
            this.namedTag.putInt("EXP", experience)
            this.namedTag.putInt("expLevel", experienceLevel)
            this.namedTag.putInt("foodLevel", getFoodData().getLevel())
            this.namedTag.putFloat("foodSaturationLevel", getFoodData()!!.getFoodSaturationLevel())
            this.namedTag.putInt("TimeSinceRest", timeSinceRest)
            if (!name.isEmpty() && this.namedTag != null) {
                server.saveOfflinePlayerData(this.uuid, this.namedTag, async)
            }
        }
    }

    @Override
    fun kill() {
        if (!spawned) {
            return
        }
        val showMessages: Boolean = this.level.getGameRules().getBoolean(GameRule.SHOW_DEATH_MESSAGE)
        var message = ""
        val params: List<String> = ArrayList()
        val cause: EntityDamageEvent = this.getLastDamageCause()
        if (showMessages) {
            params.add(getDisplayName())
            when (if (cause == null) DamageCause.CUSTOM else cause.getCause()) {
                ENTITY_ATTACK -> if (cause is EntityDamageByEntityEvent) {
                    val e: Entity = (cause as EntityDamageByEntityEvent).getDamager()
                    killer = e
                    if (e is Player) {
                        message = "death.attack.player"
                        params.add((e as Player).getDisplayName())
                        break
                    } else if (e is EntityLiving) {
                        message = "death.attack.mob"
                        params.add(if (!Objects.equals(e.getNameTag(), "")) e.getNameTag() else e.getName())
                        break
                    } else {
                        params.add("Unknown")
                    }
                }
                PROJECTILE -> if (cause is EntityDamageByEntityEvent) {
                    val e: Entity = (cause as EntityDamageByEntityEvent).getDamager()
                    killer = e
                    if (e is Player) {
                        message = "death.attack.arrow"
                        params.add((e as Player).getDisplayName())
                    } else if (e is EntityLiving) {
                        message = "death.attack.arrow"
                        params.add(if (!Objects.equals(e.getNameTag(), "")) e.getNameTag() else e.getName())
                        break
                    } else {
                        params.add("Unknown")
                    }
                }
                VOID -> message = "death.attack.outOfWorld"
                FALL -> {
                    if (cause.getFinalDamage() > 2) {
                        message = "death.fell.accident.generic"
                        break
                    }
                    message = "death.attack.fall"
                }
                SUFFOCATION -> message = "death.attack.inWall"
                LAVA -> {
                    val block: Block = this.level.getBlock(Vector3(this.x, this.y - 1, this.z))
                    message = if (block.getId() === Block.MAGMA) {
                        "death.attack.magma"
                    } else {
                        "death.attack.lava"
                    }
                    if (killer is EntityProjectile) {
                        val shooter: Entity = (killer as EntityProjectile?).shootingEntity
                        if (shooter != null) {
                            killer = shooter
                        }
                        if (killer is EntityHuman) {
                            message += ".player"
                            params.add(if (!Objects.equals(shooter.getNameTag(), "")) shooter.getNameTag() else shooter.getName())
                        }
                    }
                }
                FIRE -> message = "death.attack.onFire"
                FIRE_TICK -> message = "death.attack.inFire"
                DROWNING -> message = "death.attack.drown"
                CONTACT -> if (cause is EntityDamageByBlockEvent) {
                    if ((cause as EntityDamageByBlockEvent).getDamager().getId() === Block.CACTUS) {
                        message = "death.attack.cactus"
                    }
                }
                BLOCK_EXPLOSION, ENTITY_EXPLOSION -> if (cause is EntityDamageByEntityEvent) {
                    val e: Entity = (cause as EntityDamageByEntityEvent).getDamager()
                    killer = e
                    if (e is Player) {
                        message = "death.attack.explosion.player"
                        params.add((e as Player).getDisplayName())
                    } else if (e is EntityLiving) {
                        message = "death.attack.explosion.player"
                        params.add(if (!Objects.equals(e.getNameTag(), "")) e.getNameTag() else e.getName())
                        break
                    } else {
                        message = "death.attack.explosion"
                    }
                } else {
                    message = "death.attack.explosion"
                }
                MAGIC -> message = "death.attack.magic"
                LIGHTNING -> message = "death.attack.lightningBolt"
                HUNGER -> message = "death.attack.starve"
                else -> message = "death.attack.generic"
            }
        }
        val ev = PlayerDeathEvent(this, drops, TranslationContainer(message, params.toArray(EmptyArrays.EMPTY_STRINGS)), experienceLevel)
        ev.setKeepExperience(this.level.gameRules.getBoolean(GameRule.KEEP_INVENTORY))
        ev.setKeepInventory(ev.getKeepExperience())
        server!!.getPluginManager().callEvent(ev)
        if (!ev.isCancelled()) {
            if (fishing != null) {
                stopFishing(false)
            }
            this.health = 0
            this.extinguish()
            this.scheduleUpdate()
            if (!ev.getKeepInventory() && this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
                for (item in ev.getDrops()) {
                    if (!item.hasEnchantment(Enchantment.ID_VANISHING_CURSE)) {
                        this.level.dropItem(this, item, null, true, 40)
                    }
                }
                if (this.inventory != null) {
                    this.inventory.clearAll()
                }
                if (this.offhandInventory != null) {
                    this.offhandInventory.clearAll()
                }
            }
            if (!ev.getKeepExperience() && this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
                if (isSurvival || isAdventure) {
                    var exp: Int = ev.getExperience() * 7
                    if (exp > 100) exp = 100
                    this.getLevel().dropExpOrb(this, exp)
                }
                this.setExperience(0, 0)
            }
            timeSinceRest = 0
            if (showMessages && !ev.getDeathMessage().toString().isEmpty()) {
                server.broadcast(ev.getDeathMessage(), Server.BROADCAST_CHANNEL_USERS)
            }
            val pk = RespawnPacket()
            val pos: Position? = spawn
            pk.x = pos.x
            pk.y = pos.y
            pk.z = pos.z
            pk.respawnState = RespawnPacket.STATE_SEARCHING_FOR_SPAWN
            this.dataPacket(pk)
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun isValidRespawnBlock(block: Block): Boolean {
        if (block.getId() === BlockID.RESPAWN_ANCHOR && block.getLevel().getDimension() === Level.DIMENSION_NETHER) {
            val anchor: BlockRespawnAnchor = block as BlockRespawnAnchor
            return anchor.getCharge() > 0
        }
        if (block.getId() === BlockID.BED_BLOCK && block.getLevel().getDimension() === Level.DIMENSION_OVERWORLD) {
            val bed: BlockBed = block as BlockBed
            return bed.isBedValid()
        }
        return false
    }

    protected fun respawn() {
        if (server!!.isHardcore()) {
            isBanned = true
            return
        }
        craftingType = CRAFTING_SMALL
        resetCraftingGridType()
        var spawnBlock: Vector3? = spawnBlock
        if (spawnBlock == null) {
            spawnBlock = spawnPosition
        }
        val playerRespawnEvent = PlayerRespawnEvent(this, spawn)
        var respawnBlock: Block
        var respawnBlockDim: Int = Level.DIMENSION_OVERWORLD
        if (spawnBlock != null) {
            val spawnBlockPos = Position(spawnBlock.x, spawnBlock.y, spawnBlock.z, playerRespawnEvent.getRespawnPosition().getLevel())
            respawnBlockDim = spawnBlockPos.level.getDimension()
            playerRespawnEvent.setRespawnBlockPosition(spawnBlockPos)
            respawnBlock = spawnBlockPos.getLevelBlock()
            if (isValidRespawnBlock(respawnBlock)) {
                playerRespawnEvent.setRespawnBlockAvailable(true)
                playerRespawnEvent.setConsumeCharge(respawnBlock.getId() === BlockID.RESPAWN_ANCHOR)
            } else {
                playerRespawnEvent.setRespawnBlockAvailable(false)
                playerRespawnEvent.setConsumeCharge(false)
                playerRespawnEvent.setOriginalRespawnPosition(playerRespawnEvent.getRespawnPosition())
                playerRespawnEvent.setRespawnPosition(server.getDefaultLevel().getSafeSpawn())
            }
        }
        server!!.getPluginManager().callEvent(playerRespawnEvent)
        if (!playerRespawnEvent.isRespawnBlockAvailable()) {
            if (!playerRespawnEvent.isKeepRespawnBlockPosition()) {
                spawnBlockPosition = null
            }
            if (!playerRespawnEvent.isKeepRespawnPosition()) {
                spawnPosition = null
                if (playerRespawnEvent.isSendInvalidRespawnBlockMessage()) {
                    sendMessage(TranslationContainer(TextFormat.GRAY.toString() +
                            "%tile." + (if (respawnBlockDim == Level.DIMENSION_OVERWORLD) "bed" else "respawn_anchor") + ".notValid"))
                }
            }
        }
        if (playerRespawnEvent.isConsumeCharge()) {
            val pos: Position = playerRespawnEvent.getRespawnBlockPosition()
            if (pos != null && pos.isValid()) {
                respawnBlock = pos.getLevelBlock()
                if (respawnBlock.getId() === BlockID.RESPAWN_ANCHOR) {
                    val respawnAnchor: BlockRespawnAnchor = respawnBlock as BlockRespawnAnchor
                    val charge: Int = respawnAnchor.getCharge()
                    if (charge > 0) {
                        respawnAnchor.setCharge(charge - 1)
                        respawnAnchor.getLevel().setBlock(respawnAnchor, respawnBlock)
                        respawnAnchor.getLevel().scheduleUpdate(respawnAnchor, 10)
                    }
                }
            }
        }
        val respawnPos: Position = playerRespawnEvent.getRespawnPosition()
        sendExperience()
        sendExperienceLevel()
        setSprinting(false)
        this.setSneaking(false)
        this.setDataProperty(ShortEntityData(DATA_AIR, 400), false)
        this.deadTicks = 0
        this.noDamageTicks = 60
        this.removeAllEffects()
        setHealth(this.getMaxHealth())
        getFoodData()!!.setLevel(20, 20)
        this.sendData(this)
        this.setMovementSpeed(DEFAULT_SPEED)
        getAdventureSettings()!!.update()
        this.inventory.sendContents(this)
        this.inventory.sendArmorContents(this)
        this.offhandInventory.sendContents(this)
        teleport(respawnPos, null)
        this.spawnToAll()
        this.scheduleUpdate()
        if (playerRespawnEvent.isConsumeCharge()) {
            getLevel().addSound(this, Sound.RESPAWN_ANCHOR_DEPLETE, 1, 1, this)
        }
    }

    @Override
    fun setHealth(health: Float) {
        var health = health
        if (health < 1) {
            health = 0f
        }
        super.setHealth(health)
        //TODO: Remove it in future! This a hack to solve the client-side absorption bug! WFT Mojang (Half a yellow heart cannot be shown, we can test it in local gaming)
        val attr: Attribute = Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(if (this.getAbsorption() % 2 !== 0) this.getMaxHealth() + 1 else this.getMaxHealth()).setValue(if (health > 0) if (health < getMaxHealth()) health else getMaxHealth() else 0)
        if (spawned) {
            val pk = UpdateAttributesPacket()
            pk.entries = arrayOf<Attribute>(attr)
            pk.entityId = this.id
            this.dataPacket(pk)
        }
    }

    @Override
    fun setMaxHealth(maxHealth: Int) {
        super.setMaxHealth(maxHealth)
        val attr: Attribute = Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(if (this.getAbsorption() % 2 !== 0) this.getMaxHealth() + 1 else this.getMaxHealth()).setValue(if (health > 0) if (health < getMaxHealth()) health else getMaxHealth() else 0)
        if (spawned) {
            val pk = UpdateAttributesPacket()
            pk.entries = arrayOf<Attribute>(attr)
            pk.entityId = this.id
            this.dataPacket(pk)
        }
    }

    var experience: Int
        get() = exp
        set(exp) {
            setExperience(exp, experienceLevel)
        }

    @JvmOverloads
    fun addExperience(add: Int, playLevelUpSound: Boolean = false) {
        if (add == 0) return
        val now = experience
        var added = now + add
        var level = experienceLevel
        var most = calculateRequireExperience(level)
        while (added >= most) {  //Level Up!
            added = added - most
            level++
            most = calculateRequireExperience(level)
        }
        this.setExperience(added, level, playLevelUpSound)
    }

    fun setExperience(exp: Int, level: Int) {
        setExperience(exp, level, false)
    }

    //todo something on performance, lots of exp orbs then lots of packets, could crash client
    fun setExperience(exp: Int, level: Int, playLevelUpSound: Boolean) {
        val levelBefore = experienceLevel
        this.exp = exp
        experienceLevel = level
        sendExperienceLevel(level)
        sendExperience(exp)
        if (playLevelUpSound && levelBefore < level && levelBefore / 5 != level / 5 && lastPlayerdLevelUpSoundTime < this.age - 100) {
            lastPlayerdLevelUpSoundTime = this.age
            level.addLevelSoundEvent(
                    this,
                    LevelSoundEventPacketV2.SOUND_LEVELUP,
                    Math.min(7, level / 5) shl 28,
                    "",
                    false, false
            )
        }
    }

    @JvmOverloads
    fun sendExperience(exp: Int = experience) {
        if (spawned) {
            var percent = exp.toFloat() / calculateRequireExperience(experienceLevel)
            percent = Math.max(0f, Math.min(1f, percent))
            setAttribute(Attribute.getAttribute(Attribute.EXPERIENCE).setValue(percent))
        }
    }

    @JvmOverloads
    fun sendExperienceLevel(level: Int = experienceLevel) {
        if (spawned) {
            setAttribute(Attribute.getAttribute(Attribute.EXPERIENCE_LEVEL).setValue(level))
        }
    }

    fun setAttribute(attribute: Attribute) {
        val pk = UpdateAttributesPacket()
        pk.entries = arrayOf<Attribute>(attribute)
        pk.entityId = this.id
        this.dataPacket(pk)
    }

    @Override
    fun setMovementSpeed(speed: Float) {
        setMovementSpeed(speed, true)
    }

    fun setMovementSpeed(speed: Float, send: Boolean) {
        super.setMovementSpeed(speed)
        if (spawned && send) {
            sendMovementSpeed(speed)
        }
    }

    @Since("1.4.0.0-PN")
    fun sendMovementSpeed(speed: Float) {
        val attribute: Attribute = Attribute.getAttribute(Attribute.MOVEMENT_SPEED).setValue(speed)
        setAttribute(attribute)
    }

    fun getKiller(): Entity? {
        return killer
    }

    @Override
    fun attack(source: EntityDamageEvent): Boolean {
        if (!this.isAlive()) {
            return false
        }
        if (isSpectator || isCreative && source.getCause() !== DamageCause.SUICIDE) {
            //source.setCancelled();
            return false
        } else if (getAdventureSettings()!!.get(Type.ALLOW_FLIGHT) && source.getCause() === DamageCause.FALL) {
            //source.setCancelled();
            return false
        } else if (source.getCause() === DamageCause.FALL) {
            if (this.getLevel().getBlock(this.getPosition().floor().add(0.5, -1, 0.5)).getId() === Block.SLIME_BLOCK) {
                if (!this.isSneaking()) {
                    //source.setCancelled();
                    resetFallDistance()
                    return false
                }
            }
        }
        return if (super.attack(source)) { //!source.isCancelled()
            if (this.getLastDamageCause() === source && spawned) {
                if (source is EntityDamageByEntityEvent) {
                    val damager: Entity = (source as EntityDamageByEntityEvent).getDamager()
                    if (damager is Player) {
                        (damager as Player).getFoodData()!!.updateFoodExpLevel(0.3)
                    }
                }
                val pk = EntityEventPacket()
                pk.eid = this.id
                pk.event = EntityEventPacket.HURT_ANIMATION
                this.dataPacket(pk)
            }
            true
        } else {
            false
        }
    }

    /**
     * Drops an item on the ground in front of the player. Returns if the item drop was successful.
     *
     * @param item to drop
     * @return bool if the item was dropped or if the item was null
     */
    fun dropItem(item: Item): Boolean {
        if (!spawned || !this.isAlive()) {
            return false
        }
        if (item.isNull()) {
            log.debug("{} attempted to drop a null item ({})", name, item)
            return true
        }
        val motion: Vector3 = this.getDirectionVector().multiply(0.4)
        this.level.dropItem(this.add(0, 1.3, 0), item, motion, 40)
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false)
        return true
    }

    /**
     * Drops an item on the ground in front of the player. Returns the dropped item.
     *
     * @param item to drop
     * @return EntityItem if the item was dropped or null if the item was null
     */
    @Since("1.4.0.0-PN")
    @Nullable
    fun dropAndGetItem(@Nonnull item: Item): EntityItem? {
        if (!spawned || !this.isAlive()) {
            return null
        }
        if (item.isNull()) {
            log.debug("{} attempted to drop a null item ({})", name, item)
            return null
        }
        val motion: Vector3 = this.getDirectionVector().multiply(0.4)
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false)
        return this.level.dropAndGetItem(this.add(0, 1.3, 0), item, motion, 40)
    }

    fun sendPosition(pos: Vector3?) {
        this.sendPosition(pos, this.yaw)
    }

    fun sendPosition(pos: Vector3?, yaw: Double) {
        this.sendPosition(pos, yaw, this.pitch)
    }

    fun sendPosition(pos: Vector3?, yaw: Double, pitch: Double) {
        this.sendPosition(pos, yaw, pitch, MovePlayerPacket.MODE_NORMAL)
    }

    fun sendPosition(pos: Vector3?, yaw: Double, pitch: Double, mode: Int) {
        this.sendPosition(pos, yaw, pitch, mode, null)
    }

    fun sendPosition(pos: Vector3?, yaw: Double, pitch: Double, mode: Int, targets: Array<Player>?) {
        val pk = MovePlayerPacket()
        pk.eid = this.getId()
        pk.x = pos.x
        pk.y = (pos.y + this.getEyeHeight())
        pk.z = pos.z
        pk.headYaw = yaw.toFloat()
        pk.pitch = pitch.toFloat()
        pk.yaw = yaw.toFloat()
        pk.mode = mode
        if (targets != null) {
            Server.broadcastPacket(targets, pk)
        } else {
            pk.eid = this.id
            this.dataPacket(pk)
        }
    }

    @Override
    protected fun checkChunks() {
        if (this.chunk == null || this.chunk.getX() !== this.x as Int shr 4 || this.chunk.getZ() !== this.z as Int shr 4) {
            if (this.chunk != null) {
                this.chunk.removeEntity(this)
            }
            this.chunk = this.level.getChunk(this.x as Int shr 4, this.z as Int shr 4, true)
            if (!this.justCreated) {
                val newChunk: Map<Integer, Player> = this.level.getChunkPlayers(this.x as Int shr 4, this.z as Int shr 4)
                newChunk.remove(loaderId)

                //List<Player> reload = new ArrayList<>();
                for (player in ArrayList(this.hasSpawned.values())) {
                    if (!newChunk.containsKey(player.loaderId)) {
                        this.despawnFrom(player)
                    } else {
                        newChunk.remove(player.loaderId)
                        //reload.add(player);
                    }
                }
                for (player in newChunk.values()) {
                    spawnTo(player)
                }
            }
            if (this.chunk == null) {
                return
            }
            this.chunk.addEntity(this)
        }
    }

    protected fun checkTeleportPosition(): Boolean {
        if (teleportPosition != null) {
            val chunkX = teleportPosition.x as Int shr 4
            val chunkZ = teleportPosition.z as Int shr 4
            for (X in -1..1) {
                for (Z in -1..1) {
                    val index: Long = Level.chunkHash(chunkX + X, chunkZ + Z)
                    if (!usedChunks.containsKey(index) || !usedChunks[index]!!) {
                        return false
                    }
                }
            }
            this.spawnToAll()
            forceMovement = teleportPosition
            teleportPosition = null
            return true
        }
        return false
    }

    protected fun sendPlayStatus(status: Int, immediate: Boolean = false) {
        val pk = PlayStatusPacket()
        pk.status = status
        this.dataPacket(pk)
    }

    @Override
    fun teleport(location: Location, cause: TeleportCause?): Boolean {
        if (!isOnline) {
            return false
        }
        val from: Location = this.getLocation()
        var to: Location = location
        if (cause != null) {
            val event = PlayerTeleportEvent(this, from, to, cause)
            server!!.getPluginManager().callEvent(event)
            if (event.isCancelled()) return false
            to = event.getTo()
        }

        //TODO Remove it! A hack to solve the client-side teleporting bug! (inside into the block)
        if (super.teleport(if (to.getY() === to.getFloorY()) to.add(0, 0.00001, 0) else to, null)) { // null to prevent fire of duplicate EntityTeleportEvent
            removeAllWindows()
            teleportPosition = Vector3(this.x, this.y, this.z)
            forceMovement = teleportPosition
            this.yaw = to.yaw
            this.pitch = to.pitch
            this.sendPosition(this, to.yaw, to.pitch, MovePlayerPacket.MODE_TELEPORT)
            checkTeleportPosition()
            resetFallDistance()
            nextChunkOrderRun = 0
            newPosition = null

            //DummyBossBar
            getDummyBossBars().values().forEach(DummyBossBar::reshow)
            //Weather
            this.getLevel().sendWeather(this)
            //Update time
            this.getLevel().sendTime(this)
            updateTrackingPositions(true)
            return true
        }
        return false
    }

    protected fun forceSendEmptyChunks() {
        val chunkPositionX: Int = this.getFloorX() shr 4
        val chunkPositionZ: Int = this.getFloorZ() shr 4
        for (x in -chunkRadius until chunkRadius) {
            for (z in -chunkRadius until chunkRadius) {
                val chunk = LevelChunkPacket()
                chunk.chunkX = chunkPositionX + x
                chunk.chunkZ = chunkPositionZ + z
                chunk.data = EmptyArrays.EMPTY_BYTES
                this.dataPacket(chunk)
            }
        }
    }

    fun teleportImmediate(location: Location) {
        this.teleportImmediate(location, TeleportCause.PLUGIN)
    }

    fun teleportImmediate(location: Location, cause: TeleportCause?) {
        val from: Location = this.getLocation()
        if (super.teleport(location, cause)) {
            for (window in ArrayList(windows.keySet())) {
                if (window === this.inventory) {
                    continue
                }
                this.removeWindow(window)
            }
            if (from.getLevel().getId() !== location.getLevel().getId()) { //Different level, update compass position
                val pk = SetSpawnPositionPacket()
                pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN
                val spawn: Position = location.getLevel().getSpawnLocation()
                pk.x = spawn.getFloorX()
                pk.y = spawn.getFloorY()
                pk.z = spawn.getFloorZ()
                pk.dimension = spawn.getLevel().getDimension()
                dataPacket(pk)
            }
            forceMovement = Vector3(this.x, this.y, this.z)
            this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET)
            resetFallDistance()
            orderChunks()
            nextChunkOrderRun = 0
            newPosition = null

            //Weather
            this.getLevel().sendWeather(this)
            //Update time
            this.getLevel().sendTime(this)
            updateTrackingPositions(true)
        }
    }

    /**
     * Shows a new FormWindow to the player
     * You can find out FormWindow result by listening to PlayerFormRespondedEvent
     *
     * @param window to show
     * @return form id to use in [PlayerFormRespondedEvent]
     */
    fun showFormWindow(window: FormWindow): Int {
        return showFormWindow(window, formWindowCount++)
    }

    /**
     * Shows a new FormWindow to the player
     * You can find out FormWindow result by listening to PlayerFormRespondedEvent
     *
     * @param window to show
     * @param id form id
     * @return form id to use in [PlayerFormRespondedEvent]
     */
    fun showFormWindow(window: FormWindow, id: Int): Int {
        val packet = ModalFormRequestPacket()
        packet.formId = id
        packet.data = window.getJSONData()
        formWindows.put(packet.formId, window)
        this.dataPacket(packet)
        return id
    }

    /**
     * Shows a new setting page in game settings
     * You can find out settings result by listening to PlayerFormRespondedEvent
     *
     * @param window to show on settings page
     * @return form id to use in [PlayerFormRespondedEvent]
     */
    fun addServerSettings(window: FormWindow?): Int {
        val id = formWindowCount++
        serverSettings.put(id, window)
        return id
    }

    /**
     * Creates and sends a BossBar to the player
     *
     * @param text   The BossBar message
     * @param length The BossBar percentage
     * @return bossBarId  The BossBar ID, you should store it if you want to remove or update the BossBar later
     */
    @Deprecated
    fun createBossBar(text: String?, length: Int): Long {
        val bossBar: DummyBossBar = Builder(this).text(text).length(length).build()
        return this.createBossBar(bossBar)
    }

    /**
     * Creates and sends a BossBar to the player
     *
     * @param dummyBossBar DummyBossBar Object (Instantiate it by the Class Builder)
     * @return bossBarId  The BossBar ID, you should store it if you want to remove or update the BossBar later
     * @see DummyBossBar.Builder
     */
    fun createBossBar(dummyBossBar: DummyBossBar): Long {
        dummyBossBars.put(dummyBossBar.getBossBarId(), dummyBossBar)
        dummyBossBar.create()
        return dummyBossBar.getBossBarId()
    }

    /**
     * Get a DummyBossBar object
     *
     * @param bossBarId The BossBar ID
     * @return DummyBossBar object
     * @see DummyBossBar.setText
     * @see DummyBossBar.setLength
     * @see DummyBossBar.setColor
     */
    fun getDummyBossBar(bossBarId: Long): DummyBossBar {
        return dummyBossBars.getOrDefault(bossBarId, null)
    }

    /**
     * Get all DummyBossBar objects
     *
     * @return DummyBossBars Map
     */
    fun getDummyBossBars(): Map<Long, DummyBossBar> {
        return dummyBossBars
    }

    /**
     * Updates a BossBar
     *
     * @param text      The new BossBar message
     * @param length    The new BossBar length
     * @param bossBarId The BossBar ID
     */
    @Deprecated
    fun updateBossBar(text: String?, length: Int, bossBarId: Long) {
        if (dummyBossBars.containsKey(bossBarId)) {
            val bossBar: DummyBossBar? = dummyBossBars[bossBarId]
            bossBar.setText(text)
            bossBar.setLength(length)
        }
    }

    /**
     * Removes a BossBar
     *
     * @param bossBarId The BossBar ID
     */
    fun removeBossBar(bossBarId: Long) {
        if (dummyBossBars.containsKey(bossBarId)) {
            dummyBossBars[bossBarId].destroy()
            dummyBossBars.remove(bossBarId)
        }
    }

    fun getWindowId(inventory: Inventory?): Int {
        return if (windows.containsKey(inventory)) {
            windows.get(inventory)
        } else -1
    }

    fun getWindowById(id: Int): Inventory {
        return windowIndex.get(id)
    }

    fun addWindow(inventory: Inventory?): Int {
        return this.addWindow(inventory, null)
    }

    fun addWindow(inventory: Inventory?, forceId: Integer?): Int {
        return addWindow(inventory, forceId, false)
    }

    fun addWindow(inventory: Inventory?, forceId: Integer?, isPermanent: Boolean): Int {
        return addWindow(inventory, forceId, isPermanent, false)
    }

    @Since("1.4.0.0-PN")
    fun addWindow(inventory: Inventory?, forceId: Integer?, isPermanent: Boolean, alwaysOpen: Boolean): Int {
        if (windows.containsKey(inventory)) {
            return windows.get(inventory)
        }
        val cnt: Int
        if (forceId == null) {
            cnt = Math.max(4, ++windowCnt % 99)
            windowCnt = cnt
        } else {
            cnt = forceId
        }
        windows.forcePut(inventory, cnt)
        if (isPermanent) {
            permanentWindows.add(cnt)
        }
        if (spawned && inventory.open(this)) {
            if (!isPermanent) {
                updateTrackingPositions(true)
            }
            return cnt
        } else if (!alwaysOpen) {
            this.removeWindow(inventory)
            return -1
        } else {
            inventory.getViewers().add(this)
        }
        if (!isPermanent) {
            updateTrackingPositions(true)
        }
        return cnt
    }

    val topWindow: Optional<Inventory>
        get() {
            for (entry in windows.entrySet()) {
                if (!permanentWindows.contains(entry.getValue())) {
                    return Optional.of(entry.getKey())
                }
            }
            return Optional.empty()
        }

    fun removeWindow(inventory: Inventory?) {
        this.removeWindow(inventory, false)
    }

    @Since("1.4.0.0-PN")
    protected fun removeWindow(inventory: Inventory?, isResponse: Boolean) {
        inventory.close(this)
        if (isResponse && !permanentWindows.contains(getWindowId(inventory))) {
            windows.remove(inventory)
            updateTrackingPositions(true)
        }
    }

    fun sendAllInventories() {
        cursorInventory.sendContents(this)
        for (inv in windows.keySet()) {
            inv.sendContents(this)
            if (inv is PlayerInventory) {
                (inv as PlayerInventory).sendArmorContents(this)
            }
        }
    }

    protected fun addDefaultWindows() {
        this.addWindow(this.getInventory(), ContainerIds.INVENTORY, true, true)
        playerUIInventory = PlayerUIInventory(this)
        this.addWindow(playerUIInventory, ContainerIds.UI, true)
        this.addWindow(this.offhandInventory, ContainerIds.OFFHAND, true, true)
        craftingGrid = playerUIInventory.getCraftingGrid()
        this.addWindow(craftingGrid, ContainerIds.NONE, true)

        //TODO: more windows
    }

    val uIInventory: PlayerUIInventory?
        get() = playerUIInventory
    val cursorInventory: PlayerCursorInventory
        get() = playerUIInventory.getCursorInventory()

    fun getCraftingGrid(): CraftingGrid? {
        return craftingGrid
    }

    fun setCraftingGrid(grid: CraftingGrid?) {
        craftingGrid = grid
        this.addWindow(grid, ContainerIds.NONE)
    }

    fun resetCraftingGridType() {
        if (craftingGrid != null) {
            var drops: Array<Item?> = this.inventory.addItem(craftingGrid.getContents().values().toArray(Item.EMPTY_ARRAY))
            if (drops.size > 0) {
                for (drop in drops) {
                    dropItem(drop)
                }
            }
            drops = this.inventory.addItem(cursorInventory.getItem(0))
            if (drops.size > 0) {
                for (drop in drops) {
                    dropItem(drop)
                }
            }
            playerUIInventory.clearAll()
            if (craftingGrid is BigCraftingGrid) {
                craftingGrid = playerUIInventory.getCraftingGrid()
                this.addWindow(craftingGrid, ContainerIds.NONE)
                //
//                ContainerClosePacket pk = new ContainerClosePacket(); //be sure, big crafting is really closed
//                pk.windowId = ContainerIds.NONE;
//                this.dataPacket(pk);
            }
            craftingType = CRAFTING_SMALL
        }
    }

    @JvmOverloads
    fun removeAllWindows(permanent: Boolean = false) {
        for (entry in ArrayList(windowIndex.entrySet())) {
            if (!permanent && permanentWindows.contains(entry.getKey())) {
                continue
            }
            this.removeWindow(entry.getValue())
        }
    }

    @Override
    fun setMetadata(metadataKey: String?, newMetadataValue: MetadataValue?) {
        server!!.getPlayerMetadata().setMetadata(this, metadataKey, newMetadataValue)
    }

    @Override
    fun getMetadata(metadataKey: String?): List<MetadataValue> {
        return server!!.getPlayerMetadata().getMetadata(this, metadataKey)
    }

    @Override
    fun hasMetadata(metadataKey: String?): Boolean {
        return server!!.getPlayerMetadata().hasMetadata(this, metadataKey)
    }

    @Override
    fun removeMetadata(metadataKey: String?, owningPlugin: Plugin?) {
        server!!.getPlayerMetadata().removeMetadata(this, metadataKey, owningPlugin)
    }

    @Override
    fun onChunkChanged(chunk: FullChunk) {
        usedChunks.remove(Level.chunkHash(chunk.getX(), chunk.getZ()))
    }

    @Override
    fun onChunkLoaded(chunk: FullChunk?) {
    }

    @Override
    fun onChunkPopulated(chunk: FullChunk?) {
    }

    @Override
    fun onChunkUnloaded(chunk: FullChunk?) {
    }

    @Override
    fun onBlockChanged(block: Vector3?) {
    }

    @get:Override
    val isLoaderActive: Boolean
        get() = isConnected
    var isFoodEnabled = true
        get() = !(isCreative || isSpectator) && field

    fun getFoodData(): PlayerFood? {
        return foodData
    }

    //todo a lot on dimension
    private fun setDimension(dimension: Int) {
        val pk = ChangeDimensionPacket()
        pk.dimension = dimension
        pk.x = this.x as Float
        pk.y = this.y as Float
        pk.z = this.z as Float
        this.dataPacket(pk)
    }

    @Override
    fun switchLevel(level: Level): Boolean {
        val oldLevel: Level = level
        if (super.switchLevel(level)) {
            val spawnPosition = SetSpawnPositionPacket()
            spawnPosition.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN
            val spawn: Position = level.getSpawnLocation()
            spawnPosition.x = spawn.getFloorX()
            spawnPosition.y = spawn.getFloorY()
            spawnPosition.z = spawn.getFloorZ()
            spawnPosition.dimension = spawn.getLevel().getDimension()
            this.dataPacket(spawnPosition)

            // Remove old chunks
            for (index in ArrayList(usedChunks.keySet())) {
                val chunkX: Int = Level.getHashX(index)
                val chunkZ: Int = Level.getHashZ(index)
                unloadChunk(chunkX, chunkZ, oldLevel)
            }
            usedChunks.clear()
            val setTime = SetTimePacket()
            setTime.time = level.getTime()
            this.dataPacket(setTime)
            val gameRulesChanged = GameRulesChangedPacket()
            gameRulesChanged.gameRules = level.getGameRules()
            this.dataPacket(gameRulesChanged)
            if (oldLevel.getDimension() !== level.getDimension()) {
                setDimension(level.getDimension())
            }
            updateTrackingPositions(true)
            return true
        }
        return false
    }

    fun setCheckMovement(checkMovement: Boolean) {
        isCheckingMovement = checkMovement
    }

    @Override
    fun setSprinting(value: Boolean) {
        if (isSprinting() !== value) {
            super.setSprinting(value)
            this.setMovementSpeed(if (value) getMovementSpeed() * 1.3f else getMovementSpeed() / 1.3f)
            if (this.hasEffect(Effect.SPEED)) {
                val movementSpeed: Float = this.getMovementSpeed()
                sendMovementSpeed(if (value) movementSpeed * 1.3f else movementSpeed)
            }
        }
    }

    fun transfer(address: InetSocketAddress) {
        val hostName: String = address.getAddress().getHostAddress()
        val port: Int = address.getPort()
        val pk = TransferPacket()
        pk.address = hostName
        pk.port = port
        this.dataPacket(pk)
    }

    fun getLoginChainData(): LoginChainData? {
        return loginChainData
    }

    fun pickupEntity(entity: Entity, near: Boolean): Boolean {
        if (!spawned || !this.isAlive() || !isOnline || isSpectator || entity.isClosed()) {
            return false
        }
        if (near) {
            var inventory: Inventory = this.inventory
            if (entity is EntityArrow && (entity as EntityArrow).hadCollision) {
                val item = ItemArrow()
                if (isSurvival) {
                    // Should only collect to the offhand slot if the item matches what is already there
                    if (this.offhandInventory.getItem(0).getId() === item.getId() && this.offhandInventory.canAddItem(item)) {
                        inventory = this.offhandInventory
                    } else if (!inventory.canAddItem(item)) {
                        return false
                    }
                }
                val ev = InventoryPickupArrowEvent(inventory, entity as EntityArrow)
                val pickupMode: Int = (entity as EntityArrow).getPickupMode()
                if (pickupMode == EntityArrow.PICKUP_NONE || pickupMode == EntityArrow.PICKUP_CREATIVE && !isCreative) {
                    ev.setCancelled()
                }
                server!!.getPluginManager().callEvent(ev)
                if (ev.isCancelled()) {
                    return false
                }
                val pk = TakeItemEntityPacket()
                pk.entityId = this.getId()
                pk.target = entity.getId()
                Server.broadcastPacket(entity.getViewers().values(), pk)
                this.dataPacket(pk)
                if (!isCreative) {
                    inventory.addItem(item.clone())
                }
                entity.close()
                return true
            } else if (entity is EntityThrownTrident) {
                // Check Trident is returning to shooter
                if (!(entity as EntityThrownTrident).hadCollision) {
                    if (entity.isNoClip()) {
                        if (!(entity as EntityProjectile).shootingEntity.equals(this)) {
                            return false
                        }
                    } else {
                        return false
                    }
                }
                if (!(entity as EntityThrownTrident).isPlayer()) {
                    return false
                }
                val item: Item = (entity as EntityThrownTrident).getItem()
                if (isSurvival && !inventory.canAddItem(item)) {
                    return false
                }
                val ev = InventoryPickupTridentEvent(inventory, entity as EntityThrownTrident)
                server!!.getPluginManager().callEvent(ev)
                if (ev.isCancelled()) {
                    return false
                }
                val pk = TakeItemEntityPacket()
                pk.entityId = this.getId()
                pk.target = entity.getId()
                Server.broadcastPacket(entity.getViewers().values(), pk)
                this.dataPacket(pk)
                if (!(entity as EntityThrownTrident).isCreative()) {
                    if (inventory.getItem((entity as EntityThrownTrident).getFavoredSlot()).getId() === Item.AIR) {
                        inventory.setItem((entity as EntityThrownTrident).getFavoredSlot(), item.clone())
                    } else {
                        inventory.addItem(item.clone())
                    }
                }
                entity.close()
                return true
            } else if (entity is EntityItem) {
                if ((entity as EntityItem).getPickupDelay() <= 0) {
                    val item: Item = (entity as EntityItem).getItem()
                    if (item != null) {
                        if (isSurvival && !inventory.canAddItem(item)) {
                            return false
                        }
                        var ev: InventoryPickupItemEvent
                        server!!.getPluginManager().callEvent(InventoryPickupItemEvent(inventory, entity as EntityItem).also { ev = it })
                        if (ev.isCancelled()) {
                            return false
                        }
                        when (item.getId()) {
                            Item.WOOD, Item.WOOD2 -> awardAchievement("mineWood")
                            Item.DIAMOND -> awardAchievement("diamond")
                        }
                        val pk = TakeItemEntityPacket()
                        pk.entityId = this.getId()
                        pk.target = entity.getId()
                        Server.broadcastPacket(entity.getViewers().values(), pk)
                        this.dataPacket(pk)
                        entity.close()
                        inventory.addItem(item.clone())
                        return true
                    }
                }
            }
        }
        val tick: Int = server.getTick()
        if (pickedXPOrb < tick && entity is EntityXPOrb && this.boundingBox.isVectorInside(entity)) {
            val xpOrb: EntityXPOrb = entity as EntityXPOrb
            if (xpOrb.getPickupDelay() <= 0) {
                val exp: Int = xpOrb.getExp()
                entity.kill()
                this.getLevel().addLevelEvent(LevelEventPacket.EVENT_SOUND_EXPERIENCE_ORB, 0, this)
                pickedXPOrb = tick

                //Mending
                val itemsWithMending: ArrayList<Integer> = ArrayList()
                for (i in 0..3) {
                    if (inventory.getArmorItem(i).getEnchantment(Enchantment.ID_MENDING as Short) != null) {
                        itemsWithMending.add(inventory.getSize() + i)
                    }
                }
                if (inventory.getItemInHand().getEnchantment(Enchantment.ID_MENDING as Short) != null) {
                    itemsWithMending.add(inventory.getHeldItemIndex())
                }
                if (itemsWithMending.size() > 0) {
                    val rand = Random()
                    val itemToRepair: Integer = itemsWithMending.get(rand.nextInt(itemsWithMending.size()))
                    val toRepair: Item = inventory.getItem(itemToRepair)
                    if (toRepair is ItemTool || toRepair is ItemArmor) {
                        if (toRepair.getDamage() > 0) {
                            var dmg: Int = toRepair.getDamage() - 2
                            if (dmg < 0) dmg = 0
                            toRepair.setDamage(dmg)
                            inventory.setItem(itemToRepair, toRepair)
                            return true
                        }
                    }
                }
                addExperience(exp, true)
                return true
            }
        }
        return false
    }

    @Override
    override fun hashCode(): Int {
        if (hash == 0 || hash == 485) {
            hash = 485 + if (getUniqueId() != null) getUniqueId().hashCode() else 0
        }
        return hash
    }

    @Override
    override fun equals(obj: Object): Boolean {
        if (obj !is Player) {
            return false
        }
        val other = obj as Player
        return Objects.equals(this.getUniqueId(), other.getUniqueId()) && this.getId() === other.getId()
    }

    fun isBreakingBlock(): Boolean {
        return breakingBlock != null
    }

    /**
     * Show a window of a XBOX account's profile
     * @param xuid XUID
     */
    fun showXboxProfile(xuid: String) {
        val pk = ShowProfilePacket()
        pk.xuid = xuid
        this.dataPacket(pk)
    }

    /**
     * Start fishing
     * @param fishingRod fishing rod item
     */
    fun startFishing(fishingRod: Item) {
        val nbt: CompoundTag = CompoundTag()
                .putList(ListTag<DoubleTag>("Pos")
                        .add(DoubleTag("", x))
                        .add(DoubleTag("", y + this.getEyeHeight()))
                        .add(DoubleTag("", z)))
                .putList(ListTag<DoubleTag>("Motion")
                        .add(DoubleTag("", -Math.sin(yaw / 180 + Math.PI) * Math.cos(pitch / 180 * Math.PI)))
                        .add(DoubleTag("", -Math.sin(pitch / 180 * Math.PI)))
                        .add(DoubleTag("", Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI))))
                .putList(ListTag<FloatTag>("Rotation")
                        .add(FloatTag("", yaw as Float))
                        .add(FloatTag("", pitch as Float)))
        val f = 1.1
        val fishingHook = EntityFishingHook(chunk, nbt, this)
        fishingHook.setMotion(Vector3(-Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f, -Math.sin(Math.toRadians(pitch)) * f * f,
                Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f))
        val ev = ProjectileLaunchEvent(fishingHook)
        server!!.getPluginManager().callEvent(ev)
        if (ev.isCancelled()) {
            fishingHook.close()
        } else {
            fishingHook.spawnToAll()
            fishing = fishingHook
            fishingHook.rod = fishingRod
        }
    }

    /**
     * Stop fishing
     * @param click clicked or forced
     */
    fun stopFishing(click: Boolean) {
        if (fishing != null && click) {
            fishing.reelLine()
        } else if (fishing != null) {
            fishing.close()
        }
        fishing = null
    }

    @Override
    fun doesTriggerPressurePlate(): Boolean {
        return gamemode != SPECTATOR
    }

    private fun updateBlockingFlag() {
        val shouldBlock = (noShieldTicks == 0 && (this.isSneaking() || getRiding() != null)
                && (this.getInventory().getItemInHand().getId() === ItemID.SHIELD || this.getOffhandInventory().getItem(0).getId() === ItemID.SHIELD))
        if (isBlocking() !== shouldBlock) {
            this.setBlocking(shouldBlock)
        }
    }

    @Override
    protected fun onBlock(entity: Entity?, animate: Boolean) {
        super.onBlock(entity, animate)
        if (animate) {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_BLOCKED_USING_DAMAGED_SHIELD, true)
            server!!.getScheduler().scheduleTask(null) {
                if (isOnline) {
                    this.setDataFlag(DATA_FLAGS, DATA_FLAG_BLOCKED_USING_DAMAGED_SHIELD, false)
                }
            }
        }
    }

    @Override
    override fun toString(): String {
        return "Player(name='" + name +
                "', location=" + super.toString() +
                ')'
    }

    /**
     * Adds the items to the main player inventory and drops on the floor any excess.
     * @param items The items to give to the player.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun giveItem(vararg items: Item?) {
        for (failed in getInventory().addItem(items)) {
            getLevel().dropItem(this, failed)
        }
    }

    // TODO: Support Translation Parameters
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun sendPopupJukebox(message: String) {
        val pk = TextPacket()
        pk.type = TextPacket.TYPE_JUKEBOX_POPUP
        pk.message = message
        this.dataPacket(pk)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun sendSystem(message: String) {
        val pk = TextPacket()
        pk.type = TextPacket.TYPE_SYSTEM
        pk.message = message
        this.dataPacket(pk)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun sendWhisper(message: String) {
        this.sendWhisper("", message)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun sendWhisper(source: String, message: String) {
        val pk = TextPacket()
        pk.type = TextPacket.TYPE_WHISPER
        pk.source = source
        pk.message = message
        this.dataPacket(pk)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun sendAnnouncement(message: String) {
        this.sendAnnouncement("", message)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun sendAnnouncement(source: String, message: String) {
        val pk = TextPacket()
        pk.type = TextPacket.TYPE_ANNOUNCEMENT
        pk.source = source
        pk.message = message
        this.dataPacket(pk)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun dataPacketImmediately(packet: DataPacket): Boolean {
        if (!isConnected) {
            return false
        }
        Timings.getSendDataPacketTiming(packet).use { ignored ->
            val ev = DataPacketSendEvent(this, packet)
            server!!.getPluginManager().callEvent(ev)
            if (ev.isCancelled()) {
                return false
            }
            if (log.isTraceEnabled() && !server!!.isIgnoredPacket(packet.getClass())) {
                log.trace("Immediate Outbound {}: {}", name, packet)
            }
            interfaz.putPacket(this, packet, false, true)
        }
        return true
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EMPTY_ARRAY = arrayOfNulls<Player>(0)
        private const val NO_SHIELD_DELAY = 10
        const val SURVIVAL = 0
        const val CREATIVE = 1
        const val ADVENTURE = 2
        const val SPECTATOR = 3
        const val VIEW = SPECTATOR
        const val SURVIVAL_SLOTS = 36
        const val CREATIVE_SLOTS = 112
        const val CRAFTING_SMALL = 0
        const val CRAFTING_BIG = 1
        const val CRAFTING_ANVIL = 2
        const val CRAFTING_ENCHANT = 3
        const val CRAFTING_BEACON = 4

        @PowerNukkitOnly
        val CRAFTING_GRINDSTONE = 1000

        @PowerNukkitOnly
        val CRAFTING_STONECUTTER = 1001

        @PowerNukkitOnly
        val CRAFTING_CARTOGRAPHY = 1002

        @PowerNukkitOnly
        val CRAFTING_SMITHING = 1003
        const val DEFAULT_SPEED = 0.1f
        const val MAXIMUM_SPEED = 0.5f
        const val PERMISSION_CUSTOM = 3
        const val PERMISSION_OPERATOR = 2
        const val PERMISSION_MEMBER = 1
        const val PERMISSION_VISITOR = 0
        const val ANVIL_WINDOW_ID = 2
        const val ENCHANT_WINDOW_ID = 3
        const val BEACON_WINDOW_ID = 4

        @PowerNukkitOnly
        val GRINDSTONE_WINDOW_ID: Int = dynamic(5)

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val SMITHING_WINDOW_ID: Int = dynamic(6)
        private fun uncheckedNewInetSocketAddress(ip: String, port: Int): InetSocketAddress {
            return try {
                InetSocketAddress(InetAddress.getByName(ip), port)
            } catch (exception: UnknownHostException) {
                throw IllegalArgumentException(exception)
            }
        }

        /**
         * Returns a client-friendly gamemode of the specified real gamemode
         * This function takes care of handling gamemodes known to MCPE (as of 1.1.0.3, that includes Survival, Creative and Adventure)
         *
         *
         * TODO: remove this when Spectator Mode gets added properly to MCPE
         */
        private fun getClientFriendlyGamemode(gamemode: Int): Int {
            var gamemode = gamemode
            gamemode = gamemode and 0x03
            return if (gamemode == SPECTATOR) {
                CREATIVE
            } else gamemode
        }

        fun calculateRequireExperience(level: Int): Int {
            return if (level >= 30) {
                112 + (level - 30) * 9
            } else if (level >= 15) {
                37 + (level - 15) * 5
            } else {
                7 + level * 2
            }
        }

        fun getChunkCacheFromData(chunkX: Int, chunkZ: Int, subChunkCount: Int, payload: ByteArray): BatchPacket {
            val pk = LevelChunkPacket()
            pk.chunkX = chunkX
            pk.chunkZ = chunkZ
            pk.subChunkCount = subChunkCount
            pk.data = payload
            pk.encode()
            val batch = BatchPacket()
            val batchPayload = arrayOfNulls<ByteArray>(2)
            val buf: ByteArray = pk.getBuffer()
            batchPayload[0] = Binary.writeUnsignedVarInt(buf.size)
            batchPayload[1] = buf
            val data: ByteArray = Binary.appendBytes(batchPayload)
            try {
                batch.payload = Network.deflateRaw(data, Server.getInstance().networkCompressionLevel)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
            return batch
        }
    }

    init {
        this.interfaz = interfaz
        perm = PermissibleBase(this)
        server = Server.getInstance()
        lastBreak = -1
        this.socketAddress = socketAddress
        this.clientID = clientID
        loaderId = Level.generateChunkLoaderId(this)
        chunksPerTick = server!!.getConfig("chunk-sending.per-tick", 4)!!
        spawnThreshold = server!!.getConfig("chunk-sending.spawn-threshold", 56)!!
        spawnPosition = null
        gamemode = server.getGamemode()
        this.setLevel(server.getDefaultLevel())
        viewDistance = server.getViewDistance()
        chunkRadius = viewDistance
        //this.newPosition = new Vector3(0, 0, 0);
        this.boundingBox = SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0)
        lastSkinChange = -1
        this.uuid = null
        this.rawUUID = null
        creationTime = System.currentTimeMillis()
    }
}