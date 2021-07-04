package cn.nukkit.metrics

import cn.nukkit.Player

@Since("1.4.0.0-PN")
@Log4j2
class NukkitMetrics private constructor(server: Server, start: Boolean) {
    private val server: Server
    private var enabled = false
    private var serverUUID: String? = null
    private var logFailedRequests = false
    private var metrics: Metrics? = null

    /**
     * Setup the nukkit metrics and starts it if it hadn't started yet.
     *
     * @param server The Nukkit server
     */
    @SuppressWarnings(["DeprecatedIsStillUsed", "java:S1133"])
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "1.4.0.0-PN", replaceWith = "NukkitMetrics.startNow(Server)", reason = "The original cloudburst nukkit constructor implementation behaves like a stateful static method " +
            "and don't comply with Java standards. Use the static method startNow(server) instead.")
    @Since("1.4.0.0-PN")
    @Deprecated("Replace with {@link #startNow(Server)}")
    constructor(server: Server) : this(server, true) {
    }

    private class JavaVersionRetriever : Callable<Map<String?, Map<String?, Integer?>?>?> {
        // The following code can be attributed to the PaperMC project
        // https://github.com/PaperMC/Paper/blob/master/Spigot-Server-Patches/0005-Paper-Metrics.patch#L614
        @Override
        fun call(): Map<String, Map<String, Integer>> {
            val map: Map<String, Map<String, Integer>> = HashMap()
            val javaVersion: String = System.getProperty("java.version")
            val entry: Map<String, Integer> = HashMap()
            entry.put(javaVersion, 1)

            // http://openjdk.java.net/jeps/223
            // Java decided to change their versioning scheme and in doing so modified the java.version system
            // property to return $major[.$minor][.$secuity][-ea], as opposed to 1.$major.0_$identifier
            // we can handle pre-9 by checking if the "major" is equal to "1", otherwise, 9+
            var majorVersion: String = javaVersion.split("\\.").get(0)
            val release: String
            val indexOf: Int = javaVersion.lastIndexOf('.')
            if (majorVersion.equals("1")) {
                release = "Java " + javaVersion.substring(0, indexOf)
            } else {
                // of course, it really wouldn't be all that simple if they didn't add a quirk, now would it
                // valid strings for the major may potentially include values such as -ea to deannotate a pre release
                val versionMatcher: Matcher = Pattern.compile("\\d+").matcher(majorVersion)
                if (versionMatcher.find()) {
                    majorVersion = versionMatcher.group(0)
                }
                release = "Java $majorVersion"
            }
            map.put(release, entry)
            return map
        }
    }

    /**
     * Loads the bStats configuration.
     */
    @Throws(IOException::class)
    private fun loadConfig() {
        val bStatsFolder = File(server.getPluginPath(), "bStats")
        if (!bStatsFolder.exists() && !bStatsFolder.mkdirs()) {
            log.warn("Failed to create bStats metrics directory")
            return
        }
        val configFile = File(bStatsFolder, "config.yml")
        if (!configFile.exists()) {
            writeFile(configFile,
                    "# bStats collects some data for plugin authors like how many servers are using their plugins.",
                    "# To honor their work, you should not disable it.",
                    "# This has nearly no effect on the server performance!",
                    "# Check out https://bStats.org/ to learn more :)",
                    "enabled: true", "serverUuid: \"" + UUID.randomUUID().toString().toString() + "\"",
                    "logFailedRequests: false")
        }
        val config = Config(configFile, Config.YAML)

        // Load configuration
        enabled = config.getBoolean("enabled", true)
        serverUUID = config.getString("serverUuid")
        logFailedRequests = config.getBoolean("logFailedRequests", false)
    }

    @Throws(IOException::class)
    private fun writeFile(file: File, vararg lines: String) {
        BufferedWriter(FileWriter(file)).use { writer ->
            for (line in lines) {
                writer.write(line)
                writer.newLine()
            }
        }
    }

    private fun mapDeviceOSToString(os: Int): String {
        return when (os) {
            1 -> "Android"
            2 -> "iOS"
            3 -> "macOS"
            4 -> "FireOS"
            5 -> "Gear VR"
            6 -> "Hololens"
            7 -> "Windows 10"
            8 -> "Windows"
            9 -> "Dedicated"
            10 -> "PS4"
            11, 12 -> "Switch"
            13 -> "Xbox One"
            14 -> "Windows Phone"
            else -> "Unknown"
        }
    }

    companion object {
        private val metricsStarted: AtomicReference<Map<Server, NukkitMetrics>> = AtomicReference(Collections.emptyMap())

        /**
         * Setup the nukkit metrics and starts it if it hadn't started yet.
         *
         * @param server The Nukkit server
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun startNow(server: Server): Boolean {
            val nukkitMetrics = getOrCreateMetrics(server)
            return nukkitMetrics!!.metrics != null
        }

        private fun getOrCreateMetrics(@Nonnull server: Server): NukkitMetrics? {
            var current: Map<Server?, NukkitMetrics?> = metricsStarted.get()
            var metrics = current[server]
            if (metrics != null) {
                return metrics
            }
            current = metricsStarted.updateAndGet { before ->
                var mutable: Map<Server?, NukkitMetrics?> = before
                if (before.isEmpty()) {
                    mutable = WeakHashMap(1)
                }
                mutable.computeIfAbsent(server) { server: Server -> createMetrics(server) }
                mutable
            }
            metrics = current[server]
            assert(metrics != null)
            return metrics
        }

        @Nonnull
        private fun createMetrics(@Nonnull server: Server): NukkitMetrics {
            val nukkitMetrics = NukkitMetrics(server, false)
            if (!nukkitMetrics.enabled) {
                return nukkitMetrics
            }
            val metrics = Metrics("Nukkit", nukkitMetrics.serverUUID, nukkitMetrics.logFailedRequests)
            nukkitMetrics.metrics = metrics
            metrics.addCustomChart(SingleLineChart("players") { server.getOnlinePlayers().size() })
            metrics.addCustomChart(SimplePie("codename", server::getCodename))
            metrics.addCustomChart(SimplePie("minecraft_version", server::getVersion))
            metrics.addCustomChart(SimplePie("nukkit_version", server::getNukkitVersion))
            metrics.addCustomChart(SimplePie("xbox_auth") { if (server.getPropertyBoolean("xbox-auth")) "Required" else "Not required" })
            metrics.addCustomChart(AdvancedPie("player_platform") {
                server.getOnlinePlayers().values().stream()
                        .map(Player::getLoginChainData)
                        .map(LoginChainData::getDeviceOS)
                        .collect(groupingBy({ os: Int -> nukkitMetrics.mapDeviceOSToString(os) }, countingInt()))
            })
            metrics.addCustomChart(AdvancedPie("player_game_version") {
                server.getOnlinePlayers().values().stream()
                        .map(Player::getLoginChainData)
                        .collect(groupingBy(LoginChainData::getGameVersion, countingInt()))
            })
            metrics.addCustomChart(DrilldownPie("java_version", JavaVersionRetriever()))
            return nukkitMetrics
        }
    }

    init {
        this.server = server
        try {
            loadConfig()
        } catch (e: Exception) {
            log.warn("Failed to load the bStats configuration file", e)
        }
        if (start && enabled) {
            startNow(server)
        }
    }
}