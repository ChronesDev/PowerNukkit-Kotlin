package cn.nukkit

import cn.nukkit.math.NukkitMath

/*
 * `_   _       _    _    _ _
 * | \ | |     | |  | |  (_) |
 * |  \| |_   _| | _| | ___| |_
 * | . ` | | | | |/ / |/ / | __|
 * | |\  | |_| |   <|   <| | |_
 * |_| \_|\__,_|_|\_\_|\_\_|\__|
 */
/**
 * Nukkit启动类，包含`main`函数。<br></br>
 * The launcher class of Nukkit, including the `main` function.
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
@Log4j2
object Nukkit {
    val GIT_INFO: Properties? = gitInfo
    val VERSION = version
    val GIT_COMMIT = gitCommit
    val API_VERSION: String = dynamic("1.0.12")
    val CODENAME: String = dynamic("PowerNukkit")

    @Deprecated
    val MINECRAFT_VERSION: String = ProtocolInfo.MINECRAFT_VERSION

    @Deprecated
    val MINECRAFT_VERSION_NETWORK: String = ProtocolInfo.MINECRAFT_VERSION_NETWORK
    val PATH: String = System.getProperty("user.dir").toString() + "/"
    val DATA_PATH: String = System.getProperty("user.dir").toString() + "/"
    val PLUGIN_PATH = DATA_PATH + "plugins"
    val START_TIME: Long = System.currentTimeMillis()
    var ANSI = true
    var TITLE = false
    var shortTitle = requiresShortTitle()
    var DEBUG = 1
    fun main(args: Array<String?>?) {
        val disableSentry = AtomicBoolean(false)
        Sentry.init { options ->
            options.setDsn("https://a99f9e0c50424fff9f96feb2fd94c22f:6891b003c5874fa4bf407fe45035e3f1@o505263.ingest.sentry.io/5593371")
            options.setRelease(version + "-" + gitCommit)
            options.setBeforeSend { event, hint ->
                if (disableSentry.get()) {
                    return@setBeforeSend null
                }
                try {
                    val sv: Server = Server.getInstance()
                    event.setExtra("players", sv.getOnlinePlayers().size())
                    val levels: Map<Integer, cn.nukkit.level.Level> = sv!!.getLevels()
                    event.setExtra("levels", levels.size())
                    event.setExtra("chunks", levels.values().stream().mapToInt { l -> l.getChunks().size() }.sum())
                    event.setExtra("tiles", levels.values().stream().mapToInt { l -> l.getBlockEntities().size() }.sum())
                    event.setExtra("entities", levels.values().stream().mapToInt { l -> l.getEntities().length }.sum())
                } catch (e: Exception) {
                    log.debug("Failed to add player/level/chunk/tiles/entities information", e)
                }
                try {
                    val runtime: Runtime = Runtime.getRuntime()
                    val totalMB: Double = NukkitMath.round(runtime.totalMemory() as Double / 1024 / 1024, 2)
                    val usedMB: Double = NukkitMath.round((runtime.totalMemory() - runtime.freeMemory()) as Double / 1024 / 1024, 2)
                    val maxMB: Double = NukkitMath.round(runtime.maxMemory() as Double / 1024 / 1024, 2)
                    val usage = usedMB / maxMB * 100
                    event.setExtra("memTotal", totalMB)
                    event.setExtra("memUsed", usedMB)
                    event.setExtra("memMax", maxMB)
                    event.setExtra("memUsage", usage)
                } catch (e: Exception) {
                    log.debug("Failed to add memory information", e)
                }
                try {
                    event.setModules(
                            Server.getInstance().getPluginManager().getPlugins().entrySet().stream()
                                    .map { entry ->
                                        SimpleEntry(
                                                entry.getKey(),
                                                entry.getValue().getDescription().getVersion()
                                        )
                                    }.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                    )
                } catch (e: Exception) {
                    log.debug("Failed to grab the list of enabled plugins", e)
                }
                event
            }
        }
        disableSentry.set(Boolean.parseBoolean(System.getProperty("disableSentry", "false")))
        val propertiesPath: Path = Paths.get(DATA_PATH, "server.properties")
        if (!disableSentry.get() && Files.isRegularFile(propertiesPath)) {
            val properties = Properties()
            try {
                FileReader(propertiesPath.toFile()).use { reader ->
                    properties.load(reader)
                    var value: String = properties.getProperty("disable-auto-bug-report", "false")
                    if (value.equalsIgnoreCase("on") || value.equals("1")) {
                        value = "true"
                    }
                    disableSentry.set(Boolean.parseBoolean(value.toLowerCase(Locale.ENGLISH)))
                }
            } catch (e: IOException) {
                log.error("Failed to load server.properties to check disable-auto-bug-report.", e)
            }
        }

        // Force IPv4 since Nukkit is not compatible with IPv6
        System.setProperty("java.net.preferIPv4Stack", "true")
        System.setProperty("log4j.skipJansi", "false")
        System.getProperties().putIfAbsent("io.netty.allocator.type", "unpooled") // Disable memory pooling unless specified

        // Force Mapped ByteBuffers for LevelDB till fixed.
        System.setProperty("leveldb.mmap", "true")

        // Netty logger for debug info
        InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE)
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID)

        // Define args
        val parser = OptionParser()
        parser.allowsUnrecognizedOptions()
        val helpSpec: OptionSpec<Void> = parser.accepts("help", "Shows this page").forHelp()
        val ansiSpec: OptionSpec<Void> = parser.accepts("disable-ansi", "Disables console coloring")
        val titleSpec: OptionSpec<Void> = parser.accepts("enable-title", "Enables title at the top of the window")
        val vSpec: OptionSpec<String> = parser.accepts("v", "Set verbosity of logging").withRequiredArg().ofType(String::class.java)
        val verbositySpec: OptionSpec<String> = parser.accepts("verbosity", "Set verbosity of logging").withRequiredArg().ofType(String::class.java)
        val languageSpec: OptionSpec<String> = parser.accepts("language", "Set a predefined language").withOptionalArg().ofType(String::class.java)

        // Parse arguments
        val options: OptionSet = parser.parse(args)
        if (options.has(helpSpec)) {
            try {
                // Display help page
                parser.printHelpOn(System.out)
            } catch (e: IOException) {
                // ignore
            }
            return
        }
        ANSI = !options.has(ansiSpec)
        TITLE = options.has(titleSpec)
        var verbosity: String = options.valueOf(vSpec)
        if (verbosity == null) {
            verbosity = options.valueOf(verbositySpec)
        }
        if (verbosity != null) {
            try {
                val level: Level = Level.valueOf(verbosity)
                logLevel = level
            } catch (e: Exception) {
                // ignore
            }
        }
        val language: String = options.valueOf(languageSpec)
        try {
            if (TITLE) {
                System.out.print(0x1b as Char.toString() + "]0;Nukkit is starting up..." + 0x07.toChar())
            }
            Server(PATH, DATA_PATH, PLUGIN_PATH, language)
        } catch (t: Throwable) {
            log.catching(t)
        }
        if (TITLE) {
            System.out.print(0x1b as Char.toString() + "]0;Stopping Server..." + 0x07.toChar())
        }
        log.info("Stopping other threads")
        for (thread in java.lang.Thread.getAllStackTraces().keySet()) {
            if (thread !is InterruptibleThread) {
                continue
            }
            log.debug("Stopping {} thread", thread.getClass().getSimpleName())
            if (thread.isAlive()) {
                thread.interrupt()
            }
        }
        val killer = ServerKiller(8)
        killer.start()
        if (TITLE) {
            System.out.print(0x1b as Char.toString() + "]0;Server Stopped" + 0x07.toChar())
        }
        System.exit(0)
    }

    private fun requiresShortTitle(): Boolean {
        //Shorter title for windows 8/2012
        val osName: String = System.getProperty("os.name").toLowerCase()
        return osName.contains("windows") && (osName.contains("windows 8") || osName.contains("2012"))
    }

    private val gitInfo: Properties?
        private get() {
            val gitFileStream: InputStream = Nukkit::class.java.getClassLoader().getResourceAsStream("git.properties")
                    ?: return null
            val properties = Properties()
            try {
                properties.load(gitFileStream)
            } catch (e: IOException) {
                return null
            }
            return properties
        }
    private val version: String
        private get() {
            val resourceAsStream: InputStream = Nukkit::class.java.getClassLoader().getResourceAsStream("VERSION.txt")
                    ?: return "Unknown-PN-SNAPSHOT"
            try {
                resourceAsStream.use { `is` ->
                    InputStreamReader(`is`).use { reader ->
                        BufferedReader(reader).use { buffered ->
                            val line: String = buffered.readLine().trim()
                            return if ("\${project.version}".equalsIgnoreCase(line)) {
                                "Unknown-PN-SNAPSHOT"
                            } else {
                                line
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                return "Unknown-PN-SNAPSHOT"
            }
        }
    private val gitCommit: String
        private get() {
            val version = StringBuilder()
            version.append("git-")
            var commitId: String?
            return if (GIT_INFO == null || GIT_INFO.getProperty("git.commit.id.abbrev").also { commitId = it } == null) {
                version.append("null").toString()
            } else version.append(commitId).toString()
        }
    var logLevel: Level
        get() {
            val ctx: LoggerContext = LogManager.getContext(false) as LoggerContext
            val log4jConfig: Configuration = ctx.getConfiguration()
            val loggerConfig: LoggerConfig = log4jConfig.getLoggerConfig(org.apache.logging.log4j.LogManager.ROOT_LOGGER_NAME)
            return loggerConfig.getLevel()
        }
        set(level) {
            Preconditions.checkNotNull(level, "level")
            val ctx: LoggerContext = LogManager.getContext(false) as LoggerContext
            val log4jConfig: Configuration = ctx.getConfiguration()
            val loggerConfig: LoggerConfig = log4jConfig.getLoggerConfig(org.apache.logging.log4j.LogManager.ROOT_LOGGER_NAME)
            loggerConfig.setLevel(level)
            ctx.updateLoggers()
        }
}