package cn.nukkit

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX
 * @author Box
 */
@Log4j2
class Server {
    private var banByName: BanList
    private var banByIP: BanList
    private var operators: Config
    private var whitelist: Config
    private val isRunning: AtomicBoolean = AtomicBoolean(true)
    private var hasStopped = false
    private var pluginManager: PluginManager? = null
    private val profilingTickrate = 20
    private var scheduler: ServerScheduler? = null
    var tick = 0
        private set
    var nextTick: Long = 0
        private set
    private val tickAverage = floatArrayOf(20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f)
    private val useAverage = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var maxTick = 20f
    private var maxUse = 0f
    private var sendUsageTicker = 0
    private val dispatchSignals = false
    private val console: NukkitConsole
    private val consoleThread: ConsoleThread
    private var commandMap: SimpleCommandMap
    private var craftingManager: CraftingManager? = null
    private var resourcePackManager: ResourcePackManager? = null
    private var consoleSender: ConsoleCommandSender? = null
    var maxPlayers = 0
    private var autoSave = true
    var isRedstoneEnabled = true
    private var rcon: RCON? = null
    private var entityMetadata: EntityMetadataStore? = null
    private var playerMetadata: PlayerMetadataStore? = null
    private var levelMetadata: LevelMetadataStore? = null
    private var network: Network? = null
    private var networkCompressionAsync = true
    var networkCompressionLevel = 7
    private var networkZlibProvider = 0
    private var autoTickRate = true
    private var autoTickRateLimit = 20
    private var alwaysTickPlayers = false
    private var baseTickRate = 1
    var getAllowFlight: Boolean? = null
    private var difficulty: Int = Integer.MAX_VALUE
    var defaultGamemode: Int = Integer.MAX_VALUE
        get() {
            if (field == Integer.MAX_VALUE) {
                field = gamemode
            }
            return field
        }
        private set
    private var autoSaveTicker = 0
    private var autoSaveTicks = 6000
    private var baseLang: BaseLang
    var isLanguageForced = false
        private set
    private var serverID: UUID? = null
    val filePath: String
    val dataPath: String
    val pluginPath: String
    private val uniquePlayers: Set<UUID> = HashSet()
    private var queryHandler: QueryHandler? = null
    private var queryRegenerateEvent: QueryRegenerateEvent? = null
    private var properties: Config
    private var config: Config
    private val players: Map<InetSocketAddress, Player> = HashMap()
    private val playerList: Map<UUID, Player> = HashMap()
    private var positionTrackingService: PositionTrackingService? = null
    private val levels: Map<Integer, Level> = object : HashMap<Integer?, Level?>() {
        fun put(key: Integer?, value: Level?): Level {
            val result: Level = super.put(key, value)
            levelArray = this.values().toArray(Level.EMPTY_ARRAY)
            return result
        }

        fun remove(key: Object?, value: Object?): Boolean {
            val result: Boolean = super.remove(key, value)
            levelArray = this.values().toArray(Level.EMPTY_ARRAY)
            return result
        }

        fun remove(key: Object?): Level {
            val result: Level = super.remove(key)
            levelArray = this.values().toArray(Level.EMPTY_ARRAY)
            return result
        }
    }
    private var levelArray: Array<Level>
    val serviceManager: ServiceManager = NKServiceManager()
    var defaultLevel: Level? = null
        set(defaultLevel) {
            if (defaultLevel == null || isLevelLoaded(defaultLevel.getFolderName()) && defaultLevel !== this.defaultLevel) {
                field = defaultLevel
            }
        }
    var isNetherAllowed = false
        private set
    private val currentThread: Thread

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val launchTime: Long
    private var watchdog: Watchdog? = null
    private var nameLookup: DB? = null
    private var playerDataSerializer: PlayerDataSerializer? = null
    private val ignoredPackets: Set<String> = HashSet()

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    var isSafeSpawn = false
        private set

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    var isForceSkinTrusted = false
        private set

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    var isCheckMovement = true
        private set

    /**
     * Minimal initializer for testing
     */
    @SuppressWarnings("UnstableApiUsage")
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    internal constructor(tempDir: File) {
        if (tempDir.isFile() && !tempDir.delete()) {
            throw IOException("Failed to delete $tempDir")
        }
        instance = this
        config = Config()
        levelArray = Level.EMPTY_ARRAY
        launchTime = System.currentTimeMillis()
        val batchPacket = BatchPacket()
        batchPacket.payload = EmptyArrays.EMPTY_BYTES
        CraftingManager.packet = batchPacket
        currentThread = Thread.currentThread()
        val abs: File = tempDir.getAbsoluteFile()
        filePath = abs.getPath()
        dataPath = filePath
        val dir = File(tempDir, "plugins")
        pluginPath = dir.getPath()
        Files.createParentDirs(dir)
        Files.createParentDirs(File(tempDir, "worlds"))
        Files.createParentDirs(File(tempDir, "players"))
        baseLang = BaseLang(BaseLang.FALLBACK_LANGUAGE)
        console = NukkitConsole(this)
        consoleThread = ConsoleThread()
        properties = Config()
        banByName = BanList(dataPath + "banned-players.json")
        banByIP = BanList(dataPath + "banned-ips.json")
        operators = Config()
        whitelist = Config()
        commandMap = SimpleCommandMap(this)
        maxPlayers = 10
        registerEntities()
        registerBlockEntities()
    }

    internal constructor(filePath: String, dataPath: String, pluginPath: String?, predefinedLanguage: String?) {
        var predefinedLanguage = predefinedLanguage
        Preconditions.checkState(instance == null, "Already initialized!")
        launchTime = System.currentTimeMillis()
        currentThread = Thread.currentThread() // Saves the current thread instance as a reference, used in Server#isPrimaryThread()
        instance = this
        this.filePath = filePath
        if (!File(dataPath + "worlds/").exists()) {
            File(dataPath + "worlds/").mkdirs()
        }
        if (!File(dataPath + "players/").exists()) {
            File(dataPath + "players/").mkdirs()
        }
        if (!File(pluginPath).exists()) {
            File(pluginPath).mkdirs()
        }
        this.dataPath = File(dataPath).getAbsolutePath().toString() + "/"
        this.pluginPath = File(pluginPath).getAbsolutePath().toString() + "/"
        console = NukkitConsole(this)
        consoleThread = ConsoleThread()
        consoleThread.start()
        playerDataSerializer = DefaultPlayerDataSerializer(this)

        //todo: VersionString 现在不必要
        if (!File(this.dataPath + "nukkit.yml").exists()) {
            log.info(TextFormat.GREEN.toString() + "Welcome! Please choose a language first!")
            val languagesCommaList: String
            languagesCommaList = try {
                val languageList: InputStream = this.getClass().getClassLoader().getResourceAsStream("lang/language.list")
                        ?: throw IllegalStateException("lang/language.list is missing. If you are running a development version, make sure you have run 'git submodule update --init'.")
                val lines: Array<String> = Utils.readFile(languageList).split("\n")
                for (line in lines) {
                    log.info(line)
                }
                Stream.of(lines)
                        .filter { line -> !line.isEmpty() }
                        .map { line -> line.substring(0, 3) }
                        .collect(Collectors.joining(", "))
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
            val fallback: String = BaseLang.FALLBACK_LANGUAGE
            var language: String? = null
            while (language == null) {
                var lang: String
                lang = if (predefinedLanguage != null) {
                    log.info("Trying to load language from predefined language: {}", predefinedLanguage)
                    predefinedLanguage
                } else {
                    console.readLine()
                }
                val conf: InputStream = this.getClass().getClassLoader().getResourceAsStream("lang/$lang/lang.ini")
                if (conf != null) {
                    language = lang
                } else if (predefinedLanguage != null) {
                    log.warn("No language found for predefined language: {}, please choose a valid language", predefinedLanguage)
                    predefinedLanguage = null
                }
            }
            val nukkitYmlLang = Properties()
            var nukkitYmlLangIS: InputStream = this.getClass().getClassLoader().getResourceAsStream("lang/$language/nukkit.yml.properties")
            if (nukkitYmlLangIS == null) {
                nukkitYmlLangIS = this.getClass().getClassLoader().getResourceAsStream("lang/$fallback/nukkit.yml.properties")
            }
            if (nukkitYmlLangIS == null) {
                try {
                    Utils.writeFile(this.dataPath + "nukkit.yml", Server::class.java.getResourceAsStream("/default-nukkit.yml"))
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            } else {
                try {
                    nukkitYmlLang.load(InputStreamReader(nukkitYmlLangIS, StandardCharsets.UTF_8))
                } catch (e: IOException) {
                    throw RuntimeException(e)
                } finally {
                    try {
                        nukkitYmlLangIS.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                val result = StringBuilder()
                if (nukkitYmlLang.containsKey("nukkit.yml.header") && !nukkitYmlLang.getProperty("nukkit.yml.header").trim().isEmpty()) {
                    for (header in nukkitYmlLang.getProperty("nukkit.yml.header").trim().split("\n")) {
                        result.append("# ").append(header).append(System.lineSeparator())
                    }
                    result.append(System.lineSeparator())
                }
                val keyBuilder = StringBuilder()
                try {
                    BufferedReader(InputStreamReader(Server::class.java.getResourceAsStream("/default-nukkit.yml"), StandardCharsets.UTF_8)).use { `in` ->
                        var line: String?
                        val path: LinkedList<Array<String>> = LinkedList()
                        val pattern: Pattern = Pattern.compile("^( *)([a-z-]+):")
                        var lastIdent = 0
                        var last: Array<String>? = null
                        while (`in`.readLine().also { line = it } != null) {
                            val matcher: Matcher = pattern.matcher(line)
                            if (!matcher.find()) {
                                result.append(line).append(System.lineSeparator())
                                continue
                            }
                            val current: String = matcher.group(2)
                            val ident: String = matcher.group(1)
                            val newIdent: Int = ident.length()
                            if (newIdent < lastIdent) {
                                var reduced = lastIdent
                                var parent: Array<String>
                                while (path.pollLast().also { parent = it } != null) {
                                    reduced -= parent[1].length()
                                    if (reduced <= newIdent) {
                                        break
                                    }
                                }
                                lastIdent = reduced
                            } else if (newIdent > lastIdent) {
                                path.add(last)
                                lastIdent = newIdent
                            }
                            last = arrayOf(current, ident)
                            keyBuilder.setLength(0)
                            keyBuilder.append("nukkit.yml")
                            for (part in path) {
                                keyBuilder.append('.').append(part[0])
                            }
                            keyBuilder.append('.').append(current)
                            val key: String = keyBuilder.toString()
                            if (!nukkitYmlLang.containsKey(key) || nukkitYmlLang.getProperty(key).trim().isEmpty()) {
                                result.append(line).append(System.lineSeparator())
                                continue
                            }
                            val comments: Array<String> = nukkitYmlLang.getProperty(key).trim().split("\n")
                            if (key.equals("nukkit.yml.aliases") || key.equals("nukkit.yml.worlds")) {
                                result.append(line).append(System.lineSeparator())
                                for (comment in comments) {
                                    result.append(ident).append(" # ").append(comment).append(System.lineSeparator())
                                }
                            } else if (key.equals("nukkit.yml.settings.language")) {
                                for (comment in comments) {
                                    comment = comment.replace("%s", languagesCommaList)
                                    result.append(ident).append("# ").append(comment).append(System.lineSeparator())
                                }
                                result.append(ident).append("language: ").append(language).append(System.lineSeparator())
                            } else {
                                for (comment in comments) {
                                    result.append(ident).append("# ").append(comment).append(System.lineSeparator())
                                }
                                result.append(line).append(System.lineSeparator())
                            }
                        }
                        Utils.writeFile(this.dataPath + "nukkit.yml", result.toString())
                    }
                } catch (e: IOException) {
                    throw AssertionError("Failed to create nukkit.yml", e)
                }
            }
        }
        console.setExecutingCommands(true)
        log.info("Loading {} ...", TextFormat.GREEN.toString() + "nukkit.yml" + TextFormat.WHITE)
        config = Config(this.dataPath + "nukkit.yml", Config.YAML)
        levelArray = Level.EMPTY_ARRAY
        Nukkit.DEBUG = NukkitMath.clamp(this.getConfig("debug.level", 1), 1, 3)
        val logLevel: Int = (Nukkit.DEBUG + 3) * 100
        val currentLevel: org.apache.logging.log4j.Level = Nukkit.getLogLevel()
        for (level in org.apache.logging.log4j.Level.values()) {
            if (level.intLevel() === logLevel && level.intLevel() > currentLevel.intLevel()) {
                Nukkit.setLogLevel(level)
                break
            }
        }
        ignoredPackets.addAll(getConfig().getStringList("debug.ignored-packets"))
        ignoredPackets.add("BatchPacket")
        log.info("Loading {} ...", TextFormat.GREEN.toString() + "server.properties" + TextFormat.WHITE)
        properties = Config(this.dataPath + "server.properties", Config.PROPERTIES, object : ConfigSection() {
            init {
                put("motd", "PowerNukkit Server")
                put("sub-motd", "https://powernukkit.org")
                put("server-port", 19132)
                put("server-ip", "0.0.0.0")
                put("view-distance", 10)
                put("white-list", false)
                put("achievements", true)
                put("announce-player-achievements", true)
                put("spawn-protection", 16)
                put("max-players", 20)
                put("allow-flight", false)
                put("spawn-animals", true)
                put("spawn-mobs", true)
                put("gamemode", 0)
                put("force-gamemode", false)
                put("hardcore", false)
                put("pvp", true)
                put("difficulty", 1)
                put("generator-settings", "")
                put("level-name", "world")
                put("level-seed", "")
                put("level-type", "DEFAULT")
                put("allow-nether", true)
                put("enable-query", true)
                put("enable-rcon", false)
                put("rcon.password", Base64.getEncoder().encodeToString(UUID.randomUUID().toString().replace("-", "").getBytes()).substring(3, 13))
                put("auto-save", true)
                put("force-resources", false)
                put("xbox-auth", true)
                put("disable-auto-bug-report", false)
            }
        })

        // Allow Nether? (determines if we create a nether world if one doesn't exist on startup)
        isNetherAllowed = properties.getBoolean("allow-nether", true)
        isLanguageForced = this.getConfig("settings.force-language", false)!!
        baseLang = BaseLang(this.getConfig<T>("settings.language", BaseLang.FALLBACK_LANGUAGE))
        log.info(language.translateString("language.selected", arrayOf<String>(language.getName(), language.getLang())))
        log.info(language.translateString("nukkit.server.start", TextFormat.AQUA + version + TextFormat.RESET))
        var poolSize: Object = this.getConfig<Any>("settings.async-workers", "auto" as Object)
        if (poolSize !is Integer) {
            poolSize = try {
                Integer.valueOf(poolSize as String)
            } catch (e: Exception) {
                Math.max(Runtime.getRuntime().availableProcessors() + 1, 4)
            }
        }
        ServerScheduler.WORKERS = poolSize
        networkZlibProvider = this.getConfig("network.zlib-provider", 2)!!
        Zlib.setProvider(networkZlibProvider)
        networkCompressionLevel = this.getConfig("network.compression-level", 7)!!
        networkCompressionAsync = this.getConfig("network.async-compression", true)!!
        autoTickRate = this.getConfig("level-settings.auto-tick-rate", true)!!
        autoTickRateLimit = this.getConfig("level-settings.auto-tick-rate-limit", 20)!!
        alwaysTickPlayers = this.getConfig("level-settings.always-tick-players", false)!!
        baseTickRate = this.getConfig("level-settings.base-tick-rate", 1)!!
        isRedstoneEnabled = this.getConfig("level-settings.tick-redstone", true)!!
        isSafeSpawn = this.getConfig().getBoolean("settings.safe-spawn", true)
        isForceSkinTrusted = this.getConfig().getBoolean("player.force-skin-trusted", false)
        isCheckMovement = this.getConfig().getBoolean("player.check-movement", true)
        scheduler = ServerScheduler()
        if (this.getPropertyBoolean("enable-rcon", false)) {
            try {
                rcon = RCON(this, this.getPropertyString("rcon.password", ""), if (!ip.equals("")) ip else "0.0.0.0", this.getPropertyInt("rcon.port", port))
            } catch (e: IllegalArgumentException) {
                log.error(language.translateString(e.getMessage(), e.getCause().getMessage()))
            }
        }
        entityMetadata = EntityMetadataStore()
        playerMetadata = PlayerMetadataStore()
        levelMetadata = LevelMetadataStore()
        operators = Config(this.dataPath + "ops.txt", Config.ENUM)
        whitelist = Config(this.dataPath + "white-list.txt", Config.ENUM)
        banByName = BanList(this.dataPath + "banned-players.json")
        banByName.load()
        banByIP = BanList(this.dataPath + "banned-ips.json")
        banByIP.load()
        maxPlayers = this.getPropertyInt("max-players", 20)
        setAutoSave(this.getPropertyBoolean("auto-save", true))
        if (this.getPropertyBoolean("hardcore", false) && getDifficulty() < 3) {
            setPropertyInt("difficulty", 3)
        }
        val bugReport: Boolean
        if (this.getConfig().exists("settings.bug-report")) {
            bugReport = this.getConfig().getBoolean("settings.bug-report")
            getProperties().remove("bug-report")
        } else {
            bugReport = this.getPropertyBoolean("bug-report", true) //backwards compat
        }
        if (bugReport) {
            ExceptionHandler.registerExceptionHandler()
        }
        log.info(language.translateString("nukkit.server.networkStart", arrayOf(if (ip.equals("")) "*" else ip, String.valueOf(port))))
        serverID = UUID.randomUUID()
        network = Network(this)
        network.setName(motd)
        network.setSubName(subMotd)
        log.info(language.translateString("nukkit.server.info", name, TextFormat.YELLOW + nukkitVersion + " (" + gitCommit + ")" + TextFormat.WHITE, TextFormat.AQUA + codename + TextFormat.WHITE, apiVersion))
        log.info(language.translateString("nukkit.server.license", name))
        consoleSender = ConsoleCommandSender()
        commandMap = SimpleCommandMap(this)

        // Initialize metrics
        NukkitMetrics.startNow(this)
        registerEntities()
        registerBlockEntities()
        Block.init()
        Enchantment.init()
        Item.init()
        EnumBiome.values() //load class, this also registers biomes
        Effect.init()
        Potion.init()
        Attribute.init()
        DispenseBehaviorRegister.init()
        GlobalBlockPalette.getOrCreateRuntimeId(0, 0) //Force it to load

        // Convert legacy data before plugins get the chance to mess with it.
        nameLookup = try {
            Iq80DBFactory.factory.open(File(dataPath, "players"), Options()
                    .createIfMissing(true)
                    .compressionType(CompressionType.ZLIB_RAW))
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        convertLegacyPlayerData()
        craftingManager = CraftingManager()
        resourcePackManager = ResourcePackManager(File(Nukkit.DATA_PATH, "resource_packs"))
        pluginManager = PluginManager(this, commandMap)
        pluginManager.subscribeToPermission(BROADCAST_CHANNEL_ADMINISTRATIVE, consoleSender)
        pluginManager.registerInterface(JavaPluginLoader::class.java)
        queryRegenerateEvent = QueryRegenerateEvent(this, 5)
        network.registerInterface(RakNetInterface(this))
        try {
            log.debug("Loading position tracking service")
            positionTrackingService = PositionTrackingService(File(Nukkit.DATA_PATH, "services/position_tracking_db"))
            //getScheduler().scheduleRepeatingTask(null, positionTrackingService::forceRecheckAllPlayers, 20 * 5);
        } catch (e: IOException) {
            log.fatal("Failed to start the Position Tracking DB service!", e)
        }
        pluginManager.loadPowerNukkitPlugins()
        pluginManager.loadPlugins(this.pluginPath)
        enablePlugins(PluginLoadOrder.STARTUP)
        LevelProviderManager.addProvider(this, Anvil::class.java)
        Generator.addGenerator(Flat::class.java, "flat", Generator.TYPE_FLAT)
        Generator.addGenerator(Normal::class.java, "normal", Generator.TYPE_INFINITE)
        Generator.addGenerator(Normal::class.java, "default", Generator.TYPE_INFINITE)
        Generator.addGenerator(Nether::class.java, "nether", Generator.TYPE_NETHER)
        //todo: add old generator and hell generator
        for (name in this.getConfig<Any>("worlds", HashMap<String, Object>()).keySet()) {
            if (!loadLevel(name)) {
                var seed: Long
                try {
                    seed = (this.getConfig<T>("worlds.$name.seed", ThreadLocalRandom.current().nextLong()) as Number).longValue()
                } catch (e: Exception) {
                    try {
                        seed = this.getConfig<Any>("worlds.$name.seed").toString().hashCode().toLong()
                    } catch (e2: Exception) {
                        seed = System.currentTimeMillis()
                        e2.addSuppressed(e)
                        log.warn("Failed to load the world seed for \"{}\". Generating a random seed", name, e2)
                    }
                }
                val options: Map<String?, Object> = HashMap()
                val opts: Array<String> = this.getConfig<T>("worlds.$name.generator", Generator.getGenerator("default").getSimpleName()).split(":")
                val generator: Class<out Generator?> = Generator.getGenerator(opts[0])
                if (opts.size > 1) {
                    var preset = StringBuilder()
                    for (i in 1 until opts.size) {
                        preset.append(opts[i]).append(":")
                    }
                    preset = StringBuilder(preset.substring(0, preset.length() - 1))
                    options.put("preset", preset.toString())
                }
                this.generateLevel(name, seed, generator, options)
            }
        }
        if (defaultLevel == null) {
            var defaultName = this.getPropertyString("level-name", "world")
            if (defaultName == null || defaultName.trim().isEmpty()) {
                log.warn("level-name cannot be null, using default")
                defaultName = "world"
                setPropertyString("level-name", defaultName)
            }
            if (!loadLevel(defaultName)) {
                var seed: Long
                val seedString: String = String.valueOf(this.getProperty("level-seed", System.currentTimeMillis()))
                try {
                    seed = Long.parseLong(seedString)
                } catch (e: NumberFormatException) {
                    seed = seedString.hashCode().toLong()
                }
                this.generateLevel(defaultName, if (seed == 0L) System.currentTimeMillis() else seed)
            }
            defaultLevel = getLevelByName(defaultName)
        }
        properties.save(true)
        if (defaultLevel == null) {
            log.fatal(language.translateString("nukkit.level.defaultError"))
            forceShutdown()
            return
        }
        EnumLevel.initLevels()
        if (this.getConfig("ticks-per.autosave", 6000)!! > 0) {
            autoSaveTicks = this.getConfig("ticks-per.autosave", 6000)!!
        }
        enablePlugins(PluginLoadOrder.POSTWORLD)
        if ( /*Nukkit.DEBUG < 2 && */!Boolean.parseBoolean(System.getProperty("disableWatchdog", "false"))) {
            watchdog = Watchdog(this, 60000)
            watchdog.start()
        }
        System.runFinalization()
        start()
    }

    fun broadcastMessage(message: String?): Int {
        return this.broadcast(message, BROADCAST_CHANNEL_USERS)
    }

    fun broadcastMessage(message: TextContainer?): Int {
        return this.broadcast(message, BROADCAST_CHANNEL_USERS)
    }

    fun broadcastMessage(message: String?, recipients: Array<CommandSender>): Int {
        for (recipient in recipients) {
            recipient.sendMessage(message)
        }
        return recipients.size
    }

    fun broadcastMessage(message: String?, recipients: Collection<CommandSender?>): Int {
        for (recipient in recipients) {
            recipient.sendMessage(message)
        }
        return recipients.size()
    }

    fun broadcastMessage(message: TextContainer?, recipients: Collection<CommandSender?>): Int {
        for (recipient in recipients) {
            recipient.sendMessage(message)
        }
        return recipients.size()
    }

    fun broadcast(message: String?, permissions: String): Int {
        val recipients: Set<CommandSender> = HashSet()
        for (permission in permissions.split(";")) {
            for (permissible in pluginManager.getPermissionSubscriptions(permission)) {
                if (permissible is CommandSender && permissible.hasPermission(permission)) {
                    recipients.add(permissible as CommandSender)
                }
            }
        }
        for (recipient in recipients) {
            recipient.sendMessage(message)
        }
        return recipients.size()
    }

    fun broadcast(message: TextContainer?, permissions: String): Int {
        val recipients: Set<CommandSender> = HashSet()
        for (permission in permissions.split(";")) {
            for (permissible in pluginManager.getPermissionSubscriptions(permission)) {
                if (permissible is CommandSender && permissible.hasPermission(permission)) {
                    recipients.add(permissible as CommandSender)
                }
            }
        }
        for (recipient in recipients) {
            recipient.sendMessage(message)
        }
        return recipients.size()
    }

    @DeprecationDetails(since = "1.4.0.0-PN", by = "Cloudburst Nukkit", reason = "Packet management was refactored, batching is done automatically near the RakNet layer")
    @Deprecated
    fun batchPackets(players: Array<Player>?, packets: Array<DataPacket?>?) {
        this.batchPackets(players, packets, false)
    }

    @DeprecationDetails(since = "1.4.0.0-PN", by = "Cloudburst Nukkit", reason = "Packet management was refactored, batching is done automatically near the RakNet layer")
    @Deprecated
    fun batchPackets(players: Array<Player>?, packets: Array<DataPacket?>?, forceSync: Boolean) {
        if (players == null || packets == null || players.size == 0 || packets.size == 0) {
            return
        }
        val ev = BatchPacketsEvent(players, packets, forceSync)
        getPluginManager().callEvent(ev)
        if (ev.isCancelled()) {
            return
        }
        Timings.playerNetworkSendTimer.startTiming()
        val payload = arrayOfNulls<ByteArray>(packets.size * 2)
        for (i in packets.indices) {
            val p: DataPacket? = packets[i]
            val idx: Int = i * 2
            p.tryEncode()
            val buf: ByteArray = p.getBuffer()
            payload[idx] = Binary.writeUnsignedVarInt(buf.size)
            payload[idx + 1] = buf
            packets[i] = null
        }
        val targets: List<InetSocketAddress> = ArrayList()
        for (p in players) {
            if (p.isConnected()) {
                targets.add(p.getSocketAddress())
            }
        }
        if (!forceSync && networkCompressionAsync) {
            getScheduler().scheduleAsyncTask(CompressBatchedTask(payload, targets, networkCompressionLevel))
        } else {
            try {
                val data: ByteArray = Binary.appendBytes(payload)
                broadcastPacketsCallback(Network.deflateRaw(data, networkCompressionLevel), targets)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
        Timings.playerNetworkSendTimer.stopTiming()
    }

    fun broadcastPacketsCallback(data: ByteArray, targets: List<InetSocketAddress>) {
        val pk = BatchPacket()
        pk.payload = data
        for (i in targets) {
            if (players.containsKey(i)) {
                players[i]!!.dataPacket(pk)
            }
        }
    }

    fun enablePlugins(type: PluginLoadOrder) {
        for (plugin in ArrayList(pluginManager.getPlugins().values())) {
            if (!plugin.isEnabled() && type === plugin.getDescription().getOrder()) {
                enablePlugin(plugin)
            }
        }
        if (type === PluginLoadOrder.POSTWORLD) {
            commandMap.registerServerAliases()
            DefaultPermissions.registerCorePermissions()
        }
    }

    fun enablePlugin(plugin: Plugin?) {
        pluginManager.enablePlugin(plugin)
    }

    fun disablePlugins() {
        pluginManager.disablePlugins()
    }

    @Throws(ServerException::class)
    fun dispatchCommand(sender: CommandSender?, commandLine: String): Boolean {
        // First we need to check if this command is on the main thread or not, if not, warn the user
        if (!isPrimaryThread) {
            log.warn("Command Dispatched Async: {}\nPlease notify author of plugin causing this execution to fix this bug!", commandLine,
                    ConcurrentModificationException("Command Dispatched Async: $commandLine"))
            scheduler.scheduleTask(null) { dispatchCommand(sender, commandLine) }
            return true
        }
        if (sender == null) {
            throw ServerException("CommandSender is not valid")
        }
        if (commandMap.dispatch(sender, commandLine)) {
            return true
        }
        sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.unknown", commandLine))
        return false
    }

    //todo: use ticker to check console
    fun getConsoleSender(): ConsoleCommandSender? {
        return consoleSender
    }

    fun reload() {
        log.info("Reloading...")
        log.info("Saving levels...")
        for (level in levelArray) {
            level.save()
        }
        pluginManager.disablePlugins()
        pluginManager.clearPlugins()
        commandMap.clearCommands()
        log.info("Reloading properties...")
        properties.reload()
        maxPlayers = this.getPropertyInt("max-players", 20)
        if (this.getPropertyBoolean("hardcore", false) && getDifficulty() < 3) {
            setPropertyInt("difficulty", 3.also { difficulty = it })
        }
        banByIP.load()
        banByName.load()
        reloadWhitelist()
        operators.reload()
        for (entry in iPBans.getEntires().values()) {
            try {
                getNetwork().blockAddress(InetAddress.getByName(entry.getName()), -1)
            } catch (e: UnknownHostException) {
                // ignore
            }
        }
        pluginManager.registerInterface(JavaPluginLoader::class.java)
        pluginManager.loadPlugins(pluginPath)
        enablePlugins(PluginLoadOrder.STARTUP)
        enablePlugins(PluginLoadOrder.POSTWORLD)
        Timings.reset()
    }

    fun shutdown() {
        isRunning.compareAndSet(true, false)
    }

    fun forceShutdown() {
        if (hasStopped) {
            return
        }
        try {
            isRunning.compareAndSet(true, false)
            hasStopped = true
            val serverStopEvent = ServerStopEvent()
            getPluginManager().callEvent(serverStopEvent)
            if (rcon != null) {
                rcon.close()
            }
            for (player in ArrayList(players.values())) {
                player.close(player.getLeaveMessage(), this.getConfig("settings.shutdown-message", "Server closed"))
            }
            log.debug("Disabling all plugins")
            pluginManager.disablePlugins()
            log.debug("Removing event handlers")
            HandlerList.unregisterAll()
            log.debug("Stopping all tasks")
            scheduler.cancelAllTasks()
            scheduler.mainThreadHeartbeat(Integer.MAX_VALUE)
            log.debug("Unloading all levels")
            for (level in levelArray) {
                this.unloadLevel(level, true)
            }
            if (positionTrackingService != null) {
                log.debug("Closing position tracking service")
                positionTrackingService.close()
            }
            log.debug("Closing console")
            consoleThread.interrupt()
            log.debug("Stopping network interfaces")
            for (interfaz in network.getInterfaces()) {
                interfaz.shutdown()
                network.unregisterInterface(interfaz)
            }
            if (nameLookup != null) {
                nameLookup.close()
            }
            log.debug("Disabling timings")
            Timings.stopServer()
            //todo other things
        } catch (e: Exception) {
            log.fatal("Exception happened while shutting down, exiting the process", e)
            System.exit(1)
        }
    }

    fun start() {
        if (this.getPropertyBoolean("enable-query", true)) {
            queryHandler = QueryHandler()
        }
        for (entry in iPBans.getEntires().values()) {
            try {
                network.blockAddress(InetAddress.getByName(entry.getName()), -1)
            } catch (e: UnknownHostException) {
                // ignore
            }
        }

        //todo send usage setting
        tick = 0
        log.info(language.translateString("nukkit.server.defaultGameMode", getGamemodeString(gamemode)))
        log.info(language.translateString("nukkit.server.startFinished", String.valueOf((System.currentTimeMillis() - Nukkit.START_TIME) as Double / 1000)))
        tickProcessor()
        forceShutdown()
    }

    fun handlePacket(address: InetSocketAddress, payload: ByteBuf) {
        try {
            if (!payload.isReadable(3)) {
                return
            }
            val prefix = ByteArray(2)
            payload.readBytes(prefix)
            if (!Arrays.equals(prefix, byteArrayOf(0xfe.toByte(), 0xfd.toByte()))) {
                return
            }
            if (queryHandler != null) {
                queryHandler.handle(address, payload)
            }
        } catch (e: Exception) {
            log.error("Error whilst handling packet", e)
            network.blockAddress(address.getAddress(), -1)
        }
    }

    private var lastLevelGC = 0
    fun tickProcessor() {
        getScheduler().scheduleDelayedTask(object : Task() {
            @Override
            fun onRun(currentTick: Int) {
                System.runFinalization()
                System.gc()
            }
        }, 60)
        nextTick = System.currentTimeMillis()
        try {
            while (isRunning.get()) {
                try {
                    tick()
                    val next = nextTick
                    val current: Long = System.currentTimeMillis()
                    if (next - 0.1 > current) {
                        var allocated = next - current - 1
                        run {
                            // Instead of wasting time, do something potentially useful
                            var offset = 0
                            for (i in levelArray.indices) {
                                offset = (i + lastLevelGC) % levelArray.size
                                val level: Level = levelArray[offset]
                                level.doGarbageCollection(allocated - 1)
                                allocated = next - System.currentTimeMillis()
                                if (allocated <= 0) {
                                    break
                                }
                            }
                            lastLevelGC = offset + 1
                        }
                        if (allocated > 0) {
                            Thread.sleep(allocated, 900000)
                        }
                    }
                } catch (e: RuntimeException) {
                    log.error("A RuntimeException happened while ticking the server", e)
                }
            }
        } catch (e: Throwable) {
            log.fatal("Exception happened while ticking server\n{}", Utils.getAllThreadDumps(), e)
        }
    }

    fun onPlayerCompleteLoginSequence(player: Player) {
        sendFullPlayerListData(player)
    }

    fun onPlayerLogin(player: Player) {
        if (sendUsageTicker > 0) {
            uniquePlayers.add(player.getUniqueId())
        }
    }

    fun addPlayer(socketAddress: InetSocketAddress?, player: Player?) {
        players.put(socketAddress, player)
    }

    fun addOnlinePlayer(player: Player) {
        playerList.put(player.getUniqueId(), player)
        this.updatePlayerListData(player.getUniqueId(), player.getId(), player.getDisplayName(), player.getSkin(), player.getLoginChainData().getXUID())
    }

    fun removeOnlinePlayer(player: Player) {
        if (playerList.containsKey(player.getUniqueId())) {
            playerList.remove(player.getUniqueId())
            val pk = PlayerListPacket()
            pk.type = PlayerListPacket.TYPE_REMOVE
            pk.entries = arrayOf<PlayerListPacket.Entry>(Entry(player.getUniqueId()))
            broadcastPacket(playerList.values(), pk)
        }
    }

    fun updatePlayerListData(uuid: UUID?, entityId: Long, name: String?, skin: Skin?) {
        this.updatePlayerListData(uuid, entityId, name, skin, "", playerList.values())
    }

    fun updatePlayerListData(uuid: UUID?, entityId: Long, name: String?, skin: Skin?, xboxUserId: String?) {
        this.updatePlayerListData(uuid, entityId, name, skin, xboxUserId, playerList.values())
    }

    fun updatePlayerListData(uuid: UUID?, entityId: Long, name: String?, skin: Skin?, players: Array<Player>) {
        this.updatePlayerListData(uuid, entityId, name, skin, "", players)
    }

    fun updatePlayerListData(uuid: UUID?, entityId: Long, name: String?, skin: Skin?, xboxUserId: String?, players: Array<Player>) {
        val pk = PlayerListPacket()
        pk.type = PlayerListPacket.TYPE_ADD
        pk.entries = arrayOf<PlayerListPacket.Entry>(Entry(uuid, entityId, name, skin, xboxUserId))
        broadcastPacket(players, pk)
    }

    fun updatePlayerListData(uuid: UUID?, entityId: Long, name: String?, skin: Skin?, xboxUserId: String?, players: Collection<Player?>) {
        this.updatePlayerListData(uuid, entityId, name, skin, xboxUserId, players.toArray(Player.EMPTY_ARRAY))
    }

    fun removePlayerListData(uuid: UUID?) {
        this.removePlayerListData(uuid, playerList.values())
    }

    fun removePlayerListData(uuid: UUID?, players: Array<Player>) {
        val pk = PlayerListPacket()
        pk.type = PlayerListPacket.TYPE_REMOVE
        pk.entries = arrayOf<PlayerListPacket.Entry>(Entry(uuid))
        broadcastPacket(players, pk)
    }

    @Since("1.4.0.0-PN")
    fun removePlayerListData(uuid: UUID?, player: Player) {
        val pk = PlayerListPacket()
        pk.type = PlayerListPacket.TYPE_REMOVE
        pk.entries = arrayOf<PlayerListPacket.Entry>(Entry(uuid))
        player.dataPacket(pk)
    }

    fun removePlayerListData(uuid: UUID?, players: Collection<Player?>) {
        this.removePlayerListData(uuid, players.toArray(Player.EMPTY_ARRAY))
    }

    fun sendFullPlayerListData(player: Player) {
        val pk = PlayerListPacket()
        pk.type = PlayerListPacket.TYPE_ADD
        pk.entries = playerList.values().stream()
                .map { p ->
                    Entry(
                            p.getUniqueId(),
                            p.getId(),
                            p.getDisplayName(),
                            p.getSkin(),
                            p.getLoginChainData().getXUID())
                }
                .toArray { _Dummy_.__Array__() }
        player.dataPacket(pk)
    }

    fun sendRecipeList(player: Player) {
        player.dataPacket(CraftingManager.getCraftingPacket())
    }

    private fun checkTickUpdates(currentTick: Int, tickTime: Long) {
        for (p in ArrayList(players.values())) {
            /*if (!p.loggedIn && (tickTime - p.creationTime) >= 10000 && p.kick(PlayerKickEvent.Reason.LOGIN_TIMEOUT, "Login timeout")) {
                continue;
            }

            client freezes when applying resource packs
            todo: fix*/
            if (alwaysTickPlayers) {
                p.onUpdate(currentTick)
            }
        }

        //Do level ticks
        for (level in levelArray) {
            if (level.getTickRate() > baseTickRate && --level.tickRateCounter > 0) {
                continue
            }
            try {
                val levelTime: Long = System.currentTimeMillis()
                level.doTick(currentTick)
                val tickMs = (System.currentTimeMillis() - levelTime) as Int
                level.tickRateTime = tickMs
                if (autoTickRate) {
                    if (tickMs < 50 && level.getTickRate() > baseTickRate) {
                        var r: Int
                        level.setTickRate(level.getTickRate() - 1.also { r = it })
                        if (r > baseTickRate) {
                            level.tickRateCounter = level.getTickRate()
                        }
                        log.debug("Raising level \"{}\" tick rate to {} ticks", level.getName(), level.getTickRate())
                    } else if (tickMs >= 50) {
                        if (level.getTickRate() === baseTickRate) {
                            level.setTickRate(Math.max(baseTickRate + 1, Math.min(autoTickRateLimit, tickMs / 50)))
                            log.debug("Level \"{}\" took {}ms, setting tick rate to {} ticks", level.getName(), NukkitMath.round(tickMs, 2), level.getTickRate())
                        } else if (tickMs / level.getTickRate() >= 50 && level.getTickRate() < autoTickRateLimit) {
                            level.setTickRate(level.getTickRate() + 1)
                            log.debug("Level \"{}\" took {}ms, setting tick rate to {} ticks", level.getName(), NukkitMath.round(tickMs, 2), level.getTickRate())
                        }
                        level.tickRateCounter = level.getTickRate()
                    }
                }
            } catch (e: Exception) {
                log.error(language.translateString("nukkit.level.tickError",
                        level.getFolderName(), Utils.getExceptionMessage(e)), e)
            }
        }
    }

    fun doAutoSave() {
        if (getAutoSave()) {
            Timings.levelSaveTimer.startTiming()
            for (player in ArrayList(players.values())) {
                if (player.isOnline()) {
                    player.save(true)
                } else if (!player.isConnected()) {
                    removePlayer(player)
                }
            }
            for (level in levelArray) {
                level.save()
            }
            Timings.levelSaveTimer.stopTiming()
        }
    }

    private fun tick(): Boolean {
        val tickTime: Long = System.currentTimeMillis()

        // TODO
        val time = tickTime - nextTick
        if (time < -25) {
            try {
                Thread.sleep(Math.max(5, -time - 25))
            } catch (e: InterruptedException) {
                log.debug("The thread {} got interrupted", Thread.currentThread().getName(), e)
            }
        }
        val tickTimeNano: Long = System.nanoTime()
        if (tickTime - nextTick < -25) {
            return false
        }
        Timings.fullServerTickTimer.startTiming()
        ++tick
        Timings.connectionTimer.startTiming()
        network.processInterfaces()
        if (rcon != null) {
            rcon.check()
        }
        Timings.connectionTimer.stopTiming()
        Timings.schedulerTimer.startTiming()
        scheduler.mainThreadHeartbeat(tick)
        Timings.schedulerTimer.stopTiming()
        checkTickUpdates(tick, tickTime)
        for (player in ArrayList(players.values())) {
            player.checkNetwork()
        }
        if (tick and 15 == 0) {
            titleTick()
            network.resetStatistics()
            maxTick = 20f
            maxUse = 0f
            if (tick and 511 == 0) {
                try {
                    getPluginManager().callEvent(QueryRegenerateEvent(this, 5).also { queryRegenerateEvent = it })
                    if (queryHandler != null) {
                        queryHandler.regenerateInfo()
                    }
                } catch (e: Exception) {
                    log.error(e)
                }
            }
            getNetwork().updateName()
        }
        if (autoSave && ++autoSaveTicker >= autoSaveTicks) {
            autoSaveTicker = 0
            doAutoSave()
        }
        if (sendUsageTicker > 0 && --sendUsageTicker == 0) {
            sendUsageTicker = 6000
            //todo sendUsage
        }
        if (tick % 100 == 0) {
            for (level in levelArray) {
                level.doChunkGarbageCollection()
            }
        }
        Timings.fullServerTickTimer.stopTiming()
        //long now = System.currentTimeMillis();
        val nowNano: Long = System.nanoTime()
        //float tick = Math.min(20, 1000 / Math.max(1, now - tickTime));
        //float use = Math.min(1, (now - tickTime) / 50);
        val tick = Math.min(20, 1000000000 / Math.max(1000000, nowNano.toDouble() - tickTimeNano)) as Float
        val use = Math.min(1, (nowNano - tickTimeNano).toDouble() / 50000000) as Float
        if (maxTick > tick) {
            maxTick = tick
        }
        if (maxUse < use) {
            maxUse = use
        }
        System.arraycopy(tickAverage, 1, tickAverage, 0, tickAverage.size - 1)
        tickAverage[tickAverage.size - 1] = tick
        System.arraycopy(useAverage, 1, useAverage, 0, useAverage.size - 1)
        useAverage[useAverage.size - 1] = use
        if (nextTick - tickTime < -1000) {
            nextTick = tickTime
        } else {
            nextTick += 50
        }
        return true
    }

    // TODO: Fix title tick
    fun titleTick() {
        if (!Nukkit.ANSI || !Nukkit.TITLE) {
            return
        }
        val runtime: Runtime = Runtime.getRuntime()
        val used: Double = NukkitMath.round((runtime.totalMemory() - runtime.freeMemory()) as Double / 1024 / 1024, 2)
        val max: Double = NukkitMath.round(runtime.maxMemory() as Double / 1024 / 1024, 2)
        val usage: String = Math.round(used / max * 100).toString() + "%"
        var title: String = (0x1b as Char.toString() + "]0;" + name + " "
        +nukkitVersion
        +" | " + gitCommit
        +" | Online " + players.size() + "/" + maxPlayers
        +" | Memory " + usage)
        if (!Nukkit.shortTitle) {
            title += " | U " + NukkitMath.round(network.getUpload() / 1024 * 1000, 2)
                    .toString() + " D " + NukkitMath.round(network.getDownload() / 1024 * 1000, 2).toString() + " kB/s"
        }
        title += (" | TPS " + ticksPerSecond
                + " | Load " + tickUsage + "%" + 0x07.toChar())
        System.out.print(title)
    }

    val queryInformation: QueryRegenerateEvent?
        get() = queryRegenerateEvent
    val name: String
        get() = "Nukkit"

    fun isRunning(): Boolean {
        return isRunning.get()
    }

    val nukkitVersion: String
        get() = Nukkit.VERSION
    val gitCommit: String
        get() = Nukkit.GIT_COMMIT
    val codename: String
        get() = Nukkit.CODENAME
    val version: String
        get() = ProtocolInfo.MINECRAFT_VERSION
    val apiVersion: String
        get() = Nukkit.API_VERSION
    val port: Int
        get() = this.getPropertyInt("server-port", 19132)
    val viewDistance: Int
        get() = this.getPropertyInt("view-distance", 10)
    val ip: String
        get() = this.getPropertyString("server-ip", "0.0.0.0")
    val serverUniqueId: UUID?
        get() = serverID

    fun getAutoSave(): Boolean {
        return autoSave
    }

    fun setAutoSave(autoSave: Boolean) {
        this.autoSave = autoSave
        for (level in levelArray) {
            level.setAutoSave(this.autoSave)
        }
    }

    val levelType: String
        get() = this.getPropertyString("level-type", "DEFAULT")
    val generateStructures: Boolean
        get() = this.getPropertyBoolean("generate-structures", true)
    val gamemode: Int
        get() = try {
            this.getPropertyInt("gamemode", 0) and 3
        } catch (exception: NumberFormatException) {
            getGamemodeFromString(this.getPropertyString("gamemode")) and 3
        }
    val forceGamemode: Boolean
        get() = this.getPropertyBoolean("force-gamemode", false)

    fun getDifficulty(): Int {
        if (difficulty == Integer.MAX_VALUE) {
            difficulty = getDifficultyFromString(this.getPropertyString("difficulty", "1"))
        }
        return difficulty
    }

    fun hasWhitelist(): Boolean {
        return this.getPropertyBoolean("white-list", false)
    }

    val spawnRadius: Int
        get() = this.getPropertyInt("spawn-protection", 16)
    val allowFlight: Boolean
        get() {
            if (getAllowFlight == null) {
                getAllowFlight = this.getPropertyBoolean("allow-flight", false)
            }
            return getAllowFlight!!
        }
    val isHardcore: Boolean
        get() = this.getPropertyBoolean("hardcore", false)
    val motd: String
        get() = this.getPropertyString("motd", "PowerNukkit Server")

    // The client doesn't allow empty sub-motd in 1.16.210
    val subMotd: String
        get() {
            var subMotd = this.getPropertyString("sub-motd", "https://powernukkit.org")
            if (subMotd.isEmpty()) {
                subMotd = "https://powernukkit.org" // The client doesn't allow empty sub-motd in 1.16.210
            }
            return subMotd
        }
    val forceResources: Boolean
        get() = this.getPropertyBoolean("force-resources", false)

    @get:DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Use your own logger, sharing loggers makes bug analyses harder.", replaceWith = "@Log4j2 annotation in the class and use the `log` static field that is generated by lombok, " +
            "also make sure to log the exception as the last argument, don't concatenate it or use it as parameter replacement. " +
            "Just put it as last argument and SLF4J will understand that the log message was caused by that exception/throwable.")
    @get:Deprecated
    val logger: MainLogger
        get() = MainLogger.getLogger()

    fun getEntityMetadata(): EntityMetadataStore? {
        return entityMetadata
    }

    fun getPlayerMetadata(): PlayerMetadataStore? {
        return playerMetadata
    }

    fun getLevelMetadata(): LevelMetadataStore? {
        return levelMetadata
    }

    fun getPluginManager(): PluginManager? {
        return pluginManager
    }

    fun getCraftingManager(): CraftingManager? {
        return craftingManager
    }

    fun getResourcePackManager(): ResourcePackManager? {
        return resourcePackManager
    }

    fun getScheduler(): ServerScheduler? {
        return scheduler
    }

    val ticksPerSecond: Float
        get() = Math.round(maxTick * 100) as Float / 100
    val ticksPerSecondAverage: Float
        get() {
            var sum = 0f
            val count = tickAverage.size
            for (aTickAverage in tickAverage) {
                sum += aTickAverage
            }
            return NukkitMath.round(sum / count, 2)
        }
    val tickUsage: Float
        get() = NukkitMath.round(maxUse * 100, 2)
    val tickUsageAverage: Float
        get() {
            var sum = 0f
            val count = useAverage.size
            for (aUseAverage in useAverage) {
                sum += aUseAverage
            }
            return Math.round(sum / count * 100) as Float / 100
        }

    fun getCommandMap(): SimpleCommandMap {
        return commandMap
    }

    val onlinePlayers: Map<Any, cn.nukkit.Player>
        get() = ImmutableMap.copyOf(playerList)

    fun addRecipe(recipe: Recipe?) {
        craftingManager.registerRecipe(recipe)
    }

    fun getPlayer(uuid: UUID): Optional<Player> {
        Preconditions.checkNotNull(uuid, "uuid")
        return Optional.ofNullable(playerList[uuid])
    }

    fun lookupName(name: String): Optional<UUID> {
        val nameBytes: ByteArray = name.toLowerCase().getBytes(StandardCharsets.UTF_8)
        val uuidBytes: ByteArray = nameLookup.get(nameBytes) ?: return Optional.empty()
        if (uuidBytes.size != 16) {
            log.warn("Invalid uuid in name lookup database detected! Removing")
            nameLookup.delete(nameBytes)
            return Optional.empty()
        }
        val buffer: ByteBuffer = ByteBuffer.wrap(uuidBytes)
        return Optional.of(UUID(buffer.getLong(), buffer.getLong()))
    }

    fun updateName(uuid: UUID, name: String) {
        val nameBytes: ByteArray = name.toLowerCase().getBytes(StandardCharsets.UTF_8)
        val buffer: ByteBuffer = ByteBuffer.allocate(16)
        buffer.putLong(uuid.getMostSignificantBits())
        buffer.putLong(uuid.getLeastSignificantBits())
        nameLookup.put(nameBytes, buffer.array())
    }

    @Deprecated
    fun getOfflinePlayer(name: String): IPlayer {
        val result: IPlayer? = getPlayerExact(name.toLowerCase())
        return if (result != null) {
            result
        } else lookupName(name).map { uuid -> OfflinePlayer(this, uuid) }
                .orElse(OfflinePlayer(this, name))
    }

    fun getOfflinePlayer(uuid: UUID?): IPlayer {
        Preconditions.checkNotNull(uuid, "uuid")
        val onlinePlayer: Optional<Player> = getPlayer(uuid)
        return if (onlinePlayer.isPresent()) {
            onlinePlayer.get()
        } else OfflinePlayer(this, uuid)
    }

    fun getOfflinePlayerData(uuid: UUID?): CompoundTag {
        return getOfflinePlayerData(uuid, false)
    }

    fun getOfflinePlayerData(uuid: UUID, create: Boolean): CompoundTag? {
        return getOfflinePlayerDataInternal(uuid.toString(), true, create)
    }

    @Deprecated
    fun getOfflinePlayerData(name: String?): CompoundTag {
        return getOfflinePlayerData(name, false)
    }

    @Deprecated
    fun getOfflinePlayerData(name: String, create: Boolean): CompoundTag? {
        val uuid: Optional<UUID> = lookupName(name)
        return getOfflinePlayerDataInternal(uuid.map(UUID::toString).orElse(name), true, create)
    }

    private fun getOfflinePlayerDataInternal(name: String, runEvent: Boolean, create: Boolean): CompoundTag? {
        Preconditions.checkNotNull(name, "name")
        val event = PlayerDataSerializeEvent(name, playerDataSerializer)
        if (runEvent) {
            pluginManager.callEvent(event)
        }
        var dataStream: Optional<InputStream?> = Optional.empty()
        try {
            dataStream = event.getSerializer().read(name, event.getUuid().orElse(null))
            if (dataStream.isPresent()) {
                return NBTIO.readCompressed(dataStream.get())
            }
        } catch (e: IOException) {
            log.warn(language.translateString("nukkit.data.playerCorrupted", name), e)
        } finally {
            if (dataStream.isPresent()) {
                try {
                    dataStream.get().close()
                } catch (e: IOException) {
                    log.catching(e)
                }
            }
        }
        var nbt: CompoundTag? = null
        if (create) {
            if (shouldSavePlayerData()) {
                log.info(language.translateString("nukkit.data.playerNotFound", name))
            }
            val spawn: Position = defaultLevel.getSafeSpawn()
            nbt = CompoundTag()
                    .putLong("firstPlayed", System.currentTimeMillis() / 1000)
                    .putLong("lastPlayed", System.currentTimeMillis() / 1000)
                    .putList(ListTag<DoubleTag>("Pos")
                            .add(DoubleTag("0", spawn.x))
                            .add(DoubleTag("1", spawn.y))
                            .add(DoubleTag("2", spawn.z)))
                    .putString("Level", defaultLevel.getName())
                    .putList(ListTag("Inventory"))
                    .putCompound("Achievements", CompoundTag())
                    .putInt("playerGameType", gamemode)
                    .putList(ListTag<DoubleTag>("Motion")
                            .add(DoubleTag("0", 0))
                            .add(DoubleTag("1", 0))
                            .add(DoubleTag("2", 0)))
                    .putList(ListTag<FloatTag>("Rotation")
                            .add(FloatTag("0", 0))
                            .add(FloatTag("1", 0)))
                    .putFloat("FallDistance", 0)
                    .putShort("Fire", 0)
                    .putShort("Air", 300)
                    .putBoolean("OnGround", true)
                    .putBoolean("Invulnerable", false)
            this.saveOfflinePlayerData(name, nbt, true, runEvent)
        }
        return nbt
    }

    fun saveOfflinePlayerData(uuid: UUID?, tag: CompoundTag?) {
        this.saveOfflinePlayerData(uuid, tag, false)
    }

    fun saveOfflinePlayerData(name: String?, tag: CompoundTag?) {
        this.saveOfflinePlayerData(name, tag, false)
    }

    fun saveOfflinePlayerData(uuid: UUID, tag: CompoundTag?, async: Boolean) {
        this.saveOfflinePlayerData(uuid.toString(), tag, async)
    }

    fun saveOfflinePlayerData(name: String, tag: CompoundTag?, async: Boolean) {
        val uuid: Optional<UUID> = lookupName(name)
        saveOfflinePlayerData(uuid.map(UUID::toString).orElse(name), tag, async, true)
    }

    private fun saveOfflinePlayerData(name: String, tag: CompoundTag?, async: Boolean, runEvent: Boolean) {
        val nameLower: String = name.toLowerCase()
        if (shouldSavePlayerData()) {
            val event = PlayerDataSerializeEvent(nameLower, playerDataSerializer)
            if (runEvent) {
                pluginManager.callEvent(event)
            }
            getScheduler().scheduleTask(object : Task() {
                var hasRun = false
                @Override
                fun onRun(currentTick: Int) {
                    onCancel()
                }

                //doing it like this ensures that the playerdata will be saved in a server shutdown
                @Override
                fun onCancel() {
                    if (!hasRun) {
                        hasRun = true
                        saveOfflinePlayerDataInternal(event.getSerializer(), tag, nameLower, event.getUuid().orElse(null))
                    }
                }
            }, async)
        }
    }

    private fun saveOfflinePlayerDataInternal(serializer: PlayerDataSerializer, tag: CompoundTag?, name: String, uuid: UUID) {
        try {
            serializer.write(name, uuid).use { dataStream -> NBTIO.writeGZIPCompressed(tag, dataStream, ByteOrder.BIG_ENDIAN) }
        } catch (e: Exception) {
            log.error(language.translateString("nukkit.data.saveError", name, e))
        }
    }

    private fun convertLegacyPlayerData() {
        val dataDirectory = File(dataPath, "players/")
        val uuidPattern: Pattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}.dat$")
        val files: Array<File> = dataDirectory.listFiles { file ->
            val name: String = file.getName()
            !uuidPattern.matcher(name).matches() && name.endsWith(".dat")
        } ?: return
        for (legacyData in files) {
            var name: String = legacyData.getName()
            // Remove file extension
            name = name.substring(0, name.length() - 4)
            log.debug("Attempting legacy player data conversion for {}", name)
            val tag: CompoundTag? = getOfflinePlayerDataInternal(name, false, false)
            if (tag == null || !tag.contains("UUIDLeast") || !tag.contains("UUIDMost")) {
                // No UUID so we cannot convert. Wait until player logs in.
                continue
            }
            val uuid = UUID(tag.getLong("UUIDMost"), tag.getLong("UUIDLeast"))
            if (!tag.contains("NameTag")) {
                tag.putString("NameTag", name)
            }
            if (File(dataPath + "players/" + uuid.toString() + ".dat").exists()) {
                // We don't want to overwrite existing data.
                continue
            }
            saveOfflinePlayerData(uuid.toString(), tag, false, false)

            // Add name to lookup table
            updateName(uuid, name)

            // Delete legacy data
            if (!legacyData.delete()) {
                log.warn("Unable to delete legacy data for {}", name)
            }
        }
    }

    fun getPlayer(name: String): Player? {
        var name = name
        var found: Player? = null
        name = name.toLowerCase()
        var delta: Int = Integer.MAX_VALUE
        for (player in onlinePlayers.values()) {
            if (player.getName().toLowerCase().startsWith(name)) {
                val curDelta: Int = player.getName().length() - name.length()
                if (curDelta < delta) {
                    found = player
                    delta = curDelta
                }
                if (curDelta == 0) {
                    break
                }
            }
        }
        return found
    }

    fun getPlayerExact(name: String?): Player? {
        var name = name
        name = name.toLowerCase()
        for (player in onlinePlayers.values()) {
            if (player.getName().toLowerCase().equals(name)) {
                return player
            }
        }
        return null
    }

    fun matchPlayer(partialName: String): Array<Player> {
        var partialName = partialName
        partialName = partialName.toLowerCase()
        val matchedPlayer: List<Player> = ArrayList()
        for (player in onlinePlayers.values()) {
            if (player.getName().toLowerCase().equals(partialName)) {
                return arrayOf<Player>(player)
            } else if (player.getName().toLowerCase().contains(partialName)) {
                matchedPlayer.add(player)
            }
        }
        return matchedPlayer.toArray(Player.EMPTY_ARRAY)
    }

    fun removePlayer(player: Player) {
        val toRemove: Player = players.remove(player.getSocketAddress())
        if (toRemove != null) {
            return
        }
        for (socketAddress in ArrayList(players.keySet())) {
            val p: Player? = players[socketAddress]
            if (player === p) {
                players.remove(socketAddress)
                break
            }
        }
    }

    fun getLevels(): Map<Integer, Level> {
        return levels
    }

    fun isLevelLoaded(name: String?): Boolean {
        return getLevelByName(name) != null
    }

    fun getLevel(levelId: Int): Level? {
        return if (levels.containsKey(levelId)) {
            levels[levelId]
        } else null
    }

    fun getLevelByName(name: String?): Level? {
        for (level in levelArray) {
            if (level.getFolderName().equalsIgnoreCase(name)) {
                return level
            }
        }
        return null
    }

    fun unloadLevel(level: Level): Boolean {
        return this.unloadLevel(level, false)
    }

    fun unloadLevel(level: Level, forceUnload: Boolean): Boolean {
        if (level === defaultLevel && !forceUnload) {
            throw IllegalStateException("The default level cannot be unloaded while running, please switch levels.")
        }
        return level.unload(forceUnload)
    }

    fun loadLevel(name: String): Boolean {
        if (Objects.equals(name.trim(), "")) {
            throw LevelException("Invalid empty level name")
        }
        if (isLevelLoaded(name)) {
            return true
        } else if (!isLevelGenerated(name)) {
            log.warn(language.translateString("nukkit.level.notFound", name))
            return false
        }
        val path: String
        path = if (name.contains("/") || name.contains("\\")) {
            name
        } else {
            dataPath + "worlds/" + name + "/"
        }
        val provider: Class<out LevelProvider?> = LevelProviderManager.getProvider(path)
        if (provider == null) {
            log.error(language.translateString("nukkit.level.loadError", arrayOf(name, "Unknown provider")))
            return false
        }
        val level: Level
        try {
            level = Level(this, name, path, provider)
        } catch (e: Exception) {
            log.error(language.translateString("nukkit.level.loadError", name, e.getMessage()), e)
            return false
        }
        levels.put(level.getId(), level)
        level.initLevel()
        getPluginManager().callEvent(LevelLoadEvent(level))
        level.setTickRate(baseTickRate)
        return true
    }

    fun generateLevel(name: String): Boolean {
        return this.generateLevel(name, Random().nextLong())
    }

    fun generateLevel(name: String, seed: Long): Boolean {
        return this.generateLevel(name, seed, null)
    }

    fun generateLevel(name: String, seed: Long, generator: Class<out Generator?>?): Boolean {
        return this.generateLevel(name, seed, generator, HashMap())
    }

    fun generateLevel(name: String, seed: Long, generator: Class<out Generator?>?, options: Map<String?, Object?>): Boolean {
        return generateLevel(name, seed, generator, options, null)
    }

    fun generateLevel(name: String, seed: Long, generator: Class<out Generator?>?, options: Map<String?, Object?>, provider: Class<out LevelProvider?>?): Boolean {
        var generator: Class<out Generator?>? = generator
        var provider: Class<out LevelProvider?>? = provider
        if (Objects.equals(name.trim(), "") || isLevelGenerated(name)) {
            return false
        }
        if (!options.containsKey("preset")) {
            options.put("preset", this.getPropertyString("generator-settings", ""))
        }
        if (generator == null) {
            generator = Generator.getGenerator(levelType)
        }
        if (provider == null) {
            provider = LevelProviderManager.getProviderByName(this.getConfig().get("level-settings.default-format", "anvil"))
        }
        val path: String
        path = if (name.contains("/") || name.contains("\\")) {
            name
        } else {
            dataPath + "worlds/" + name + "/"
        }
        val level: Level
        try {
            provider.getMethod("generate", String::class.java, String::class.java, Long::class.javaPrimitiveType, Class::class.java, Map::class.java).invoke(null, path, name, seed, generator, options)
            level = Level(this, name, path, provider)
            levels.put(level.getId(), level)
            level.initLevel()
            level.setTickRate(baseTickRate)
        } catch (e: Exception) {
            log.error(language.translateString("nukkit.level.generationError", arrayOf(name, Utils.getExceptionMessage(e))), e)
            return false
        }
        getPluginManager().callEvent(LevelInitEvent(level))
        getPluginManager().callEvent(LevelLoadEvent(level))

        /*this.getLogger().notice(this.getLanguage().translateString("nukkit.level.backgroundGeneration", name));

        int centerX = (int) level.getSpawnLocation().getX() >> 4;
        int centerZ = (int) level.getSpawnLocation().getZ() >> 4;

        TreeMap<String, Integer> order = new TreeMap<>();

        for (int X = -3; X <= 3; ++X) {
            for (int Z = -3; Z <= 3; ++Z) {
                int distance = X * X + Z * Z;
                int chunkX = X + centerX;
                int chunkZ = Z + centerZ;
                order.put(Level.chunkHash(chunkX, chunkZ), distance);
            }
        }

        List<Map.Entry<String, Integer>> sortList = new ArrayList<>(order.entrySet());

        Collections.sort(sortList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        for (String index : order.keySet()) {
            Chunk.Entry entry = Level.getChunkXZ(index);
            level.populateChunk(entry.chunkX, entry.chunkZ, true);
        }*/return true
    }

    fun isLevelGenerated(name: String): Boolean {
        if (Objects.equals(name.trim(), "")) {
            return false
        }
        val path = dataPath + "worlds/" + name + "/"
        return if (getLevelByName(name) == null) {
            LevelProviderManager.getProvider(path) != null
        } else true
    }

    val language: BaseLang
        get() = baseLang

    fun getNetwork(): Network? {
        return network
    }

    //Revising later...
    fun getConfig(): Config {
        return config
    }

    fun <T> getConfig(variable: String?): T {
        return this.getConfig(variable, null)
    }

    @SuppressWarnings("unchecked")
    fun <T> getConfig(variable: String?, defaultValue: T?): T? {
        val value: Object = config.get(variable)
        return if (value == null) defaultValue else value
    }

    fun getProperties(): Config {
        return properties
    }

    fun getProperty(variable: String?): Object {
        return this.getProperty(variable, null)
    }

    fun getProperty(variable: String?, defaultValue: Object?): Object {
        return if (properties.exists(variable)) properties.get(variable) else defaultValue
    }

    fun setPropertyString(variable: String?, value: String?) {
        properties.set(variable, value)
        properties.save()
    }

    fun getPropertyString(variable: String?): String {
        return this.getPropertyString(variable, null)
    }

    fun getPropertyString(variable: String?, defaultValue: String?): String {
        return if (properties.exists(variable)) properties.get(variable).toString() else defaultValue!!
    }

    fun getPropertyInt(variable: String?): Int {
        return this.getPropertyInt(variable, null)
    }

    fun getPropertyInt(variable: String?, defaultValue: Integer?): Int {
        return if (properties.exists(variable)) if (!properties.get(variable).equals("")) Integer.parseInt(String.valueOf(properties.get(variable))) else defaultValue else defaultValue
    }

    fun setPropertyInt(variable: String?, value: Int) {
        properties.set(variable, value)
        properties.save()
    }

    fun getPropertyBoolean(variable: String?): Boolean {
        return this.getPropertyBoolean(variable, null)
    }

    fun getPropertyBoolean(variable: String?, defaultValue: Object?): Boolean {
        val value: Object = if (properties.exists(variable)) properties.get(variable) else defaultValue
        if (value is Boolean) {
            return value
        }
        when (String.valueOf(value)) {
            "on", "true", "1", "yes" -> return true
        }
        return false
    }

    fun setPropertyBoolean(variable: String?, value: Boolean) {
        properties.set(variable, if (value) "1" else "0")
        properties.save()
    }

    fun getPluginCommand(name: String?): PluginIdentifiableCommand? {
        val command: Command = commandMap.getCommand(name)
        return if (command is PluginIdentifiableCommand) {
            command as PluginIdentifiableCommand
        } else {
            null
        }
    }

    val nameBans: BanList
        get() = banByName
    val iPBans: BanList
        get() = banByIP

    fun addOp(name: String) {
        operators.set(name.toLowerCase(), true)
        val player: Player? = getPlayerExact(name)
        if (player != null) {
            player.recalculatePermissions()
        }
        operators.save(true)
    }

    fun removeOp(name: String) {
        operators.remove(name.toLowerCase())
        val player: Player? = getPlayerExact(name)
        if (player != null) {
            player.recalculatePermissions()
        }
        operators.save()
    }

    fun addWhitelist(name: String) {
        whitelist.set(name.toLowerCase(), true)
        whitelist.save(true)
    }

    fun removeWhitelist(name: String) {
        whitelist.remove(name.toLowerCase())
        whitelist.save(true)
    }

    fun isWhitelisted(name: String?): Boolean {
        return !hasWhitelist() || operators.exists(name, true) || whitelist.exists(name, true)
    }

    fun isOp(name: String?): Boolean {
        return operators.exists(name, true)
    }

    fun getWhitelist(): Config {
        return whitelist
    }

    val ops: Config
        get() = operators

    fun reloadWhitelist() {
        whitelist.reload()
    }

    val commandAliases: Map<String, List<String>>
        get() {
            val section: Object = this.getConfig<Any>("aliases")
            val result: Map<String, List<String>> = LinkedHashMap()
            if (section is Map) {
                for (entry in (section as Map).entrySet() as Set<Map.Entry>) {
                    val commands: List<String> = ArrayList()
                    val key = entry.getKey() as String
                    val value: Object = entry.getValue()
                    if (value is List) {
                        commands.addAll(value as List<String?>)
                    } else {
                        commands.add(value as String)
                    }
                    result.put(key, commands)
                }
            }
            return result
        }

    fun shouldSavePlayerData(): Boolean {
        return this.getConfig("player.save-player-data", true)!!
    }

    val playerSkinChangeCooldown: Int
        get() = this.getConfig("player.skin-change-cooldown", 30)!!

    /**
     * Checks the current thread against the expected primary thread for the
     * server.
     *
     *
     * **Note:** this method should not be used to indicate the current
     * synchronized state of the runtime. A current thread matching the main
     * thread indicates that it is synchronized, but a mismatch does not
     * preclude the same assumption.
     *
     * @return true if the current thread matches the expected primary thread,
     * false otherwise
     */
    val isPrimaryThread: Boolean
        get() = Thread.currentThread() === currentThread

    fun getPrimaryThread(): Thread {
        return currentThread
    }

    private fun registerEntities() {
        Entity.registerEntity("Lightning", EntityLightning::class.java)
        Entity.registerEntity("Arrow", EntityArrow::class.java)
        Entity.registerEntity("EnderPearl", EntityEnderPearl::class.java)
        Entity.registerEntity("FallingSand", EntityFallingBlock::class.java)
        Entity.registerEntity("Firework", EntityFirework::class.java)
        Entity.registerEntity("Item", EntityItem::class.java)
        Entity.registerEntity("Painting", EntityPainting::class.java)
        Entity.registerEntity("PrimedTnt", EntityPrimedTNT::class.java)
        Entity.registerEntity("Snowball", EntitySnowball::class.java)
        //Monsters
        Entity.registerEntity("Blaze", EntityBlaze::class.java)
        Entity.registerEntity("CaveSpider", EntityCaveSpider::class.java)
        Entity.registerEntity("Creeper", EntityCreeper::class.java)
        Entity.registerEntity("Drowned", EntityDrowned::class.java)
        Entity.registerEntity("ElderGuardian", EntityElderGuardian::class.java)
        Entity.registerEntity("EnderDragon", EntityEnderDragon::class.java)
        Entity.registerEntity("Enderman", EntityEnderman::class.java)
        Entity.registerEntity("Endermite", EntityEndermite::class.java)
        Entity.registerEntity("Evoker", EntityEvoker::class.java)
        Entity.registerEntity("Ghast", EntityGhast::class.java)
        Entity.registerEntity("Guardian", EntityGuardian::class.java)
        Entity.registerEntity("Hoglin", EntityHoglin::class.java)
        Entity.registerEntity("Husk", EntityHusk::class.java)
        Entity.registerEntity("MagmaCube", EntityMagmaCube::class.java)
        Entity.registerEntity("Phantom", EntityPhantom::class.java)
        Entity.registerEntity("Piglin", EntityPiglin::class.java)
        Entity.registerEntity("PiglinBrute", EntityPiglinBrute::class.java)
        Entity.registerEntity("Pillager", EntityPillager::class.java)
        Entity.registerEntity("Ravager", EntityRavager::class.java)
        Entity.registerEntity("Shulker", EntityShulker::class.java)
        Entity.registerEntity("Silverfish", EntitySilverfish::class.java)
        Entity.registerEntity("Skeleton", EntitySkeleton::class.java)
        Entity.registerEntity("Slime", EntitySlime::class.java)
        Entity.registerEntity("IronGolem", EntityIronGolem::class.java)
        Entity.registerEntity("SnowGolem", EntitySnowGolem::class.java)
        Entity.registerEntity("Spider", EntitySpider::class.java)
        Entity.registerEntity("Stray", EntityStray::class.java)
        Entity.registerEntity("Vex", EntityVex::class.java)
        Entity.registerEntity("Vindicator", EntityVindicator::class.java)
        Entity.registerEntity("Witch", EntityWitch::class.java)
        Entity.registerEntity("Wither", EntityWither::class.java)
        Entity.registerEntity("WitherSkeleton", EntityWitherSkeleton::class.java)
        Entity.registerEntity("Zombie", EntityZombie::class.java)
        Entity.registerEntity("Zoglin", EntityZoglin::class.java)
        Entity.registerEntity("ZombiePigman", EntityZombiePigman::class.java)
        Entity.registerEntity("ZombieVillager", EntityZombieVillager::class.java)
        Entity.registerEntity("ZombieVillagerV1", EntityZombieVillagerV1::class.java)
        //Passive
        Entity.registerEntity("Bat", EntityBat::class.java)
        Entity.registerEntity("Bee", EntityBee::class.java)
        Entity.registerEntity("Cat", EntityCat::class.java)
        Entity.registerEntity("Chicken", EntityChicken::class.java)
        Entity.registerEntity("Cod", EntityCod::class.java)
        Entity.registerEntity("Cow", EntityCow::class.java)
        Entity.registerEntity("Dolphin", EntityDolphin::class.java)
        Entity.registerEntity("Donkey", EntityDonkey::class.java)
        Entity.registerEntity("Fox", EntityFox::class.java)
        Entity.registerEntity("Horse", EntityHorse::class.java)
        Entity.registerEntity("Llama", EntityLlama::class.java)
        Entity.registerEntity("Mooshroom", EntityMooshroom::class.java)
        Entity.registerEntity("Mule", EntityMule::class.java)
        Entity.registerEntity("Ocelot", EntityOcelot::class.java)
        Entity.registerEntity("Panda", EntityPanda::class.java)
        Entity.registerEntity("Parrot", EntityParrot::class.java)
        Entity.registerEntity("Pig", EntityPig::class.java)
        Entity.registerEntity("PolarBear", EntityPolarBear::class.java)
        Entity.registerEntity("Pufferfish", EntityPufferfish::class.java)
        Entity.registerEntity("Rabbit", EntityRabbit::class.java)
        Entity.registerEntity("Salmon", EntitySalmon::class.java)
        Entity.registerEntity("Sheep", EntitySheep::class.java)
        Entity.registerEntity("SkeletonHorse", EntitySkeletonHorse::class.java)
        Entity.registerEntity("Squid", EntitySquid::class.java)
        Entity.registerEntity("Strider", EntityStrider::class.java)
        Entity.registerEntity("TropicalFish", EntityTropicalFish::class.java)
        Entity.registerEntity("Turtle", EntityTurtle::class.java)
        Entity.registerEntity("Villager", EntityVillager::class.java)
        Entity.registerEntity("VillagerV1", EntityVillagerV1::class.java)
        Entity.registerEntity("WanderingTrader", EntityWanderingTrader::class.java)
        Entity.registerEntity("Wolf", EntityWolf::class.java)
        Entity.registerEntity("ZombieHorse", EntityZombieHorse::class.java)
        Entity.registerEntity("NPC", EntityNPCEntity::class.java)
        //Projectile
        Entity.registerEntity("AreaEffectCloud", EntityAreaEffectCloud::class.java)
        Entity.registerEntity("Egg", EntityEgg::class.java)
        Entity.registerEntity("LingeringPotion", EntityPotionLingering::class.java)
        Entity.registerEntity("ThrownExpBottle", EntityExpBottle::class.java)
        Entity.registerEntity("ThrownPotion", EntityPotion::class.java)
        Entity.registerEntity("ThrownTrident", EntityThrownTrident::class.java)
        Entity.registerEntity("XpOrb", EntityXPOrb::class.java)
        Entity.registerEntity("ArmorStand", EntityArmorStand::class.java)
        Entity.registerEntity("Human", EntityHuman::class.java, true)
        //Vehicle
        Entity.registerEntity("Boat", EntityBoat::class.java)
        Entity.registerEntity("MinecartChest", EntityMinecartChest::class.java)
        Entity.registerEntity("MinecartHopper", EntityMinecartHopper::class.java)
        Entity.registerEntity("MinecartRideable", EntityMinecartEmpty::class.java)
        Entity.registerEntity("MinecartTnt", EntityMinecartTNT::class.java)
        Entity.registerEntity("EndCrystal", EntityEndCrystal::class.java)
        Entity.registerEntity("FishingHook", EntityFishingHook::class.java)
    }

    private fun registerBlockEntities() {
        BlockEntity.registerBlockEntity(BlockEntity.FURNACE, BlockEntityFurnace::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.CHEST, BlockEntityChest::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.SIGN, BlockEntitySign::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.ENCHANT_TABLE, BlockEntityEnchantTable::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.SKULL, BlockEntitySkull::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.FLOWER_POT, BlockEntityFlowerPot::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.BREWING_STAND, BlockEntityBrewingStand::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.ITEM_FRAME, BlockEntityItemFrame::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.CAULDRON, BlockEntityCauldron::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.ENDER_CHEST, BlockEntityEnderChest::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.BEACON, BlockEntityBeacon::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.PISTON_ARM, BlockEntityPistonArm::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.COMPARATOR, BlockEntityComparator::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.HOPPER, BlockEntityHopper::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.BED, BlockEntityBed::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.JUKEBOX, BlockEntityJukebox::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.SHULKER_BOX, BlockEntityShulkerBox::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.BANNER, BlockEntityBanner::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.MUSIC, BlockEntityMusic::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.LECTERN, BlockEntityLectern::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.BLAST_FURNACE, BlockEntityBlastFurnace::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.SMOKER, BlockEntitySmoker::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.BEEHIVE, BlockEntityBeehive::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.CONDUIT, BlockEntityConduit::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.BARREL, BlockEntityBarrel::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.CAMPFIRE, BlockEntityCampfire::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.BELL, BlockEntityBell::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.DAYLIGHT_DETECTOR, BlockEntityDaylightDetector::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.DISPENSER, BlockEntityDispenser::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.DROPPER, BlockEntityDropper::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.MOVING_BLOCK, BlockEntityMovingBlock::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.NETHER_REACTOR, BlockEntityNetherReactor::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.LODESTONE, BlockEntityLodestone::class.java)
        BlockEntity.registerBlockEntity(BlockEntity.TARGET, BlockEntityTarget::class.java)
    }

    fun getPlayerDataSerializer(): PlayerDataSerializer? {
        return playerDataSerializer
    }

    fun setPlayerDataSerializer(playerDataSerializer: PlayerDataSerializer?) {
        this.playerDataSerializer = Preconditions.checkNotNull(playerDataSerializer, "playerDataSerializer")
    }

    @Since("1.3.0.0-PN")
    fun isIgnoredPacket(clazz: Class<out DataPacket?>): Boolean {
        return ignoredPackets.contains(clazz.getSimpleName())
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getPositionTrackingService(): PositionTrackingService? {
        return positionTrackingService
    }

    private inner class ConsoleThread : Thread(), InterruptibleThread {
        @Override
        fun run() {
            console.start()
        }
    }

    companion object {
        const val BROADCAST_CHANNEL_ADMINISTRATIVE = "nukkit.broadcast.admin"
        const val BROADCAST_CHANNEL_USERS = "nukkit.broadcast.user"
        var instance: Server? = null
            private set

        fun broadcastPacket(players: Collection<Player>, packet: DataPacket) {
            packet.tryEncode()
            for (player in players) {
                player.dataPacket(packet)
            }
        }

        fun broadcastPacket(players: Array<Player>, packet: DataPacket) {
            packet.tryEncode()
            for (player in players) {
                player.dataPacket(packet)
            }
        }

        fun getGamemodeString(mode: Int): String {
            return getGamemodeString(mode, false)
        }

        fun getGamemodeString(mode: Int, direct: Boolean): String {
            when (mode) {
                Player.SURVIVAL -> return if (direct) "Survival" else "%gameMode.survival"
                Player.CREATIVE -> return if (direct) "Creative" else "%gameMode.creative"
                Player.ADVENTURE -> return if (direct) "Adventure" else "%gameMode.adventure"
                Player.SPECTATOR -> return if (direct) "Spectator" else "%gameMode.spectator"
            }
            return "UNKNOWN"
        }

        fun getGamemodeFromString(str: String): Int {
            when (str.trim().toLowerCase()) {
                "0", "survival", "s" -> return Player.SURVIVAL
                "1", "creative", "c" -> return Player.CREATIVE
                "2", "adventure", "a" -> return Player.ADVENTURE
                "3", "spectator", "spc", "view", "v" -> return Player.SPECTATOR
            }
            return -1
        }

        fun getDifficultyFromString(str: String): Int {
            when (str.trim().toLowerCase()) {
                "0", "peaceful", "p" -> return 0
                "1", "easy", "e" -> return 1
                "2", "normal", "n" -> return 2
                "3", "hard", "h" -> return 3
            }
            return -1
        }
    }
}