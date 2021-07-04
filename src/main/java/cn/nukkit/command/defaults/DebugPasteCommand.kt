package cn.nukkit.command.defaults

import cn.nukkit.Nukkit

@Log4j2
class DebugPasteCommand(name: String?) : VanillaCommand(name, "%nukkit.command.debug.description", "%nukkit.command.debug.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        sender.sendMessage("The /debugpaste is executing, please wait...")
        val server: Server = Server.getInstance()
        if ((args.size != 1 || !"clear".equalsIgnoreCase(args[0])) &&
                TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - server.getLaunchTime()) < 15) {
            sender.sendMessage("Tip: This command works better if you use it right after you experience an issue")
        }
        server.getScheduler().scheduleAsyncTask(object : AsyncTask() {
            @Override
            fun onRun() {
                if (args.size == 1 && "clear".equalsIgnoreCase(args[0])) {
                    clear(sender)
                    return
                }
                if (args.size == 2 && "upload".equalsIgnoreCase(args[0]) && "last".equalsIgnoreCase(args[1])) {
                    uploadLast(sender)
                    return
                }
                val now: String = SimpleDateFormat("yyyy-MM-dd'T'HH.mm.ss.SSSZ").format(Date())
                val zipPath: Path
                try {
                    val dataPath: Path = Paths.get(server.getDataPath()).toAbsolutePath()
                    val dir: Path = Files.createDirectories(dataPath.resolve("debugpastes/debugpaste-$now")).toAbsolutePath()
                    Utils.writeFile(dir.resolve("thread-dump.txt").toString(), Utils.getAllThreadDumps())
                    val capturing = CapturingCommandSender()
                    capturing.setOp(true)
                    StatusCommand("status").execute(capturing, "status", arrayOf<String?>())
                    Utils.writeFile(dir.resolve("status.txt").toString(), capturing.getCleanCapture())
                    val secondMostLatest: Optional<Path> = StreamSupport.stream(Files.newDirectoryStream(dataPath.resolve("logs")).spliterator(), false)
                            .filter { path -> path.toString().toLowerCase().endsWith(".log.gz") }
                            .max label@{ a, b ->
                                var aTime: FileTime? = null
                                var bTime: FileTime? = null
                                try {
                                    aTime = Files.readAttributes(a, BasicFileAttributes::class.java).creationTime()
                                } catch (ignored: IOException) {
                                }
                                try {
                                    bTime = Files.readAttributes(b, BasicFileAttributes::class.java).creationTime()
                                } catch (ignored: IOException) {
                                }
                                if (aTime == null && bTime != null) {
                                    return@label 1
                                }
                                if (aTime != null && bTime == null) {
                                    return@label -1
                                }
                                if (aTime == null) {
                                    return@label 0
                                }
                                val comp: Int = aTime.compareTo(bTime)
                                if (comp != 0) {
                                    return@label comp
                                }
                                HumanStringComparator.getInstance().compare(
                                        a.getFileName().toString().toLowerCase(),
                                        b.getFileName().toString().toLowerCase()
                                )
                            }
                    Utils.copyFile(dataPath.resolve("logs/server.log").toFile(), dir.resolve("server-latest.log").toFile())
                    if (secondMostLatest.isPresent()) {
                        Utils.copyFile(secondMostLatest.get().toFile(), dir.resolve("server-second-most-latest.log.gz").toFile())
                    }
                    Utils.copyFile(dataPath.resolve("nukkit.yml").toFile(), dir.resolve("nukkit.yml").toFile())
                    Utils.copyFile(dataPath.resolve("server.properties").toFile(), dir.resolve("server.properties").toFile())
                    val b = StringBuilder()
                    b.append("\n# Server Information\n")
                    b.append("server.name: ").append(server.getName()).append('\n')
                    b.append("version.api: ").append(server.getApiVersion()).append('\n')
                    b.append("version.nukkit: ").append(server.getNukkitVersion()).append('\n')
                    b.append("version.git: ").append(server.getGitCommit()).append('\n')
                    b.append("version.codename: ").append(server.getCodename()).append('\n')
                    b.append("version.minecraft: ").append(server.getVersion()).append('\n')
                    b.append("version.protocol: ").append(ProtocolInfo.CURRENT_PROTOCOL).append('\n')
                    b.append("plugins:")
                    for (plugin in server.getPluginManager().getPlugins().values()) {
                        val enabled: Boolean = plugin.isEnabled()
                        val name: String = plugin.getName()
                        val desc: PluginDescription = plugin.getDescription()
                        val version: String = desc.getVersion()
                        b.append("\n  ")
                                .append(name)
                                .append(":\n    ")
                                .append("version: '")
                                .append(version)
                                .append('\'')
                                .append("\n    enabled: ")
                                .append(enabled)
                    }
                    b.append("\n\n# Java Details\n")
                    val runtime: Runtime = Runtime.getRuntime()
                    b.append("memory.free: ").append(runtime.freeMemory()).append('\n')
                    b.append("memory.max: ").append(runtime.maxMemory()).append('\n')
                    b.append("cpu.runtime: ").append(ManagementFactory.getRuntimeMXBean().getUptime()).append('\n')
                    b.append("cpu.processors: ").append(runtime.availableProcessors()).append('\n')
                    b.append("java.specification.version: '").append(System.getProperty("java.specification.version")).append("'\n")
                    b.append("java.vendor: '").append(System.getProperty("java.vendor")).append("'\n")
                    b.append("java.version: '").append(System.getProperty("java.version")).append("'\n")
                    b.append("os.arch: '").append(System.getProperty("os.arch")).append("'\n")
                    b.append("os.name: '").append(System.getProperty("os.name")).append("'\n")
                    b.append("os.version: '").append(System.getProperty("os.version")).append("'\n")
                    b.append("ulimit:\n").append(eval("sh", "-c", "ulimit -a")).append("\n\n")
                    b.append("\n# Create a ticket: https://github.com/PowerNukkit/PowerNukkit/issues/new")
                    Utils.writeFile(dir.resolve("server-info.txt").toString(), b.toString())
                    zipPath = dir.resolveSibling(dir.getFileName().toString() + ".zip")
                    Utils.zipFolder(dir, zipPath)
                    val relative: Path = dataPath.relativize(zipPath)
                    log.info("A debug paste was created at {}", relative)
                    if (sender.isPlayer()) {
                        sender.sendMessage("A debug paste has been saved in $relative")
                    }
                    FileUtils.deleteRecursively(dir.toFile())
                } catch (e: IOException) {
                    log.error("Failed to create a debugpaste in debugpastes/debugpaste-{}", now, e)
                    if (sender.isPlayer()) {
                        sender.sendMessage("An error has occurred: $e")
                        sender.sendMessage("A partial paste might be available in debugpastes/debugpaste-$now")
                    }
                    return
                }
                if (args.size == 1 && "upload".equalsIgnoreCase(args[0])) {
                    upload(sender, zipPath)
                } else {
                    sender.sendMessage("Review the file and scrub the files as you wish, and run \"/debugpaste upload last\" to make it available online")
                }
            }
        })
        return true
    }

    companion object {
        private const val ENDPOINT = "https://debugpaste.powernukkit.org/paste.php"
        private val USER_AGENT = "PowerNukkit/" + Nukkit.VERSION
        private fun filterValidPastes(file: Path): Boolean {
            val name: String = file.getFileName().toString()
            return name.startsWith("debugpaste-") && name.endsWith(".zip")
        }

        private fun clear(sender: CommandSender) {
            val dataPath: Path = Paths.get(sender.getServer().getDataPath()).toAbsolutePath()
            val pastesFolder: Path = dataPath.resolve("debugpastes")
            try {
                val count = AtomicInteger()
                if (Files.isDirectory(pastesFolder)) {
                    Files.list(pastesFolder).use { listing ->
                        listing.filter { file: Path -> filterValidPastes(file) }
                                .forEach { file ->
                                    try {
                                        Files.delete(file)
                                        count.incrementAndGet()
                                    } catch (e: IOException) {
                                        log.error("Could not delete {}", file, e)
                                    }
                                }
                    }
                }
                if (count.get() === 0) {
                    sender.sendMessage("The debug pastes folder is already clean")
                    return
                }
                log.info("{} debug pastes were deleted by {}", count.get(), sender.getName())
                sender.sendMessage("All " + count.get().toString() + " debug pastes were deleted")
            } catch (e: Exception) {
                sender.sendMessage("Oh no! An error has occurred! $e")
                log.error("Failed to delete {}", dataPath, e)
            }
        }

        private fun uploadLast(sender: CommandSender) {
            val dataPath: Path = Paths.get(sender.getServer().getDataPath()).toAbsolutePath()
            val pastesFolder: Path = dataPath.resolve("debugpastes")
            var last: Optional<Path?> = Optional.empty()
            try {
                try {
                    Files.list(pastesFolder).use { listing ->
                        last = listing.filter(Files::isRegularFile)
                                .filter { file: Path -> filterValidPastes(file) }
                                .max(Comparator.comparing { file ->
                                    try {
                                        val attributes: BasicFileAttributes = Files.readAttributes(file, BasicFileAttributes::class.java)
                                        return@comparing attributes.creationTime().toMillis()
                                    } catch (e: IOException) {
                                        throw UncheckedIOException(e)
                                    }
                                })
                    }
                } catch (e: UncheckedIOException) {
                    throw e.getCause()
                }
                if (!last.isPresent()) {
                    sender.sendMessage("No debug pastes was found. Try to run \"/debugpaste upload\" to create a new one and send right away.")
                    return
                }
                val lastPath: Path = last.get()
                val urlPath: Path = lastPath.resolveSibling(lastPath.getFileName().toString() + ".url")
                if (Files.isRegularFile(urlPath)) {
                    val url: Optional<String> = Files.lines(urlPath).filter { line -> line.startsWith("URL=http") && line.length() > 8 }.findFirst()
                    if (url.isPresent()) {
                        sender.sendMessage("The last debug paste " + lastPath.getFileName().toString() + " was already uploaded to:")
                        val directUrl: String = url.get().substring(4)
                        sender.sendMessage(directUrl)
                        if (sender !is ConsoleCommandSender) {
                            sender.sendMessage("The url is also being logged in the console for convenience, so you can do CTRL+C, CTRL+V if you have console access.")
                            log.info("The last debug paste {} was already uploaded to: {}", lastPath.getFileName(), directUrl)
                        }
                        return
                    }
                    val fileName: String = urlPath.getFileName().toString()
                    Files.move(urlPath, urlPath.resolveSibling(fileName.substring(0, fileName.length() - 4) + System.currentTimeMillis().toString() + ".url"))
                }
                upload(sender, lastPath)
            } catch (e: IOException) {
                log.error("Failed to find the last debug paste", e)
                sender.sendMessage("Sorry, an error has occurred. Check the logs for details. $e")
            }
        }

        private fun upload(sender: CommandSender, zipPath: Path) {
            sender.sendMessage("Uploading...")
            try {
                val url = URL(ENDPOINT)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.setRequestMethod("PUT")
                connection.setRequestProperty("User-Agent", USER_AGENT)
                connection.setRequestProperty("Content-Type", "application/zip")
                connection.setDoOutput(true)
                BufferedOutputStream(connection.getOutputStream()).use { bos -> Files.copy(zipPath, bos) }
                val code: Int = connection.getResponseCode()
                if (code != 201) {
                    throw IOException("The server responded with code $code, expected 201")
                }
                BufferedInputStream(connection.getInputStream()).use { `in` ->
                    if (connection.getContentEncoding() != null) InputStreamReader(`in`, connection.getContentEncoding()) else InputStreamReader(`in`).use { rd ->
                        BufferedReader(rd).use { reader ->
                            val response: String = reader.readLine()
                            if (log.isDebugEnabled()) {
                                val sb: StringBuilder = StringBuilder().append(response)
                                var line: String?
                                while (reader.readLine().also { line = it } != null) {
                                    sb.append(line).append("\n")
                                }
                                val fullReturn: String = sb.toString()
                                if (!fullReturn.equals(response)) {
                                    log.debug(fullReturn)
                                }
                            }
                            val publicUrl = URL(response)
                            log.info("The debug paste {} was uploaded to {}", zipPath.getFileName(), publicUrl)
                            if (sender.isPlayer()) {
                                sender.sendMessage("Your paste was uploaded to: $publicUrl")
                            }
                            Utils.writeFile(zipPath.resolveSibling(zipPath.getFileName().toString() + ".url").toString(),
                                    "[InternetShortcut]" + System.lineSeparator().toString() +
                                            "URL=" + publicUrl + System.lineSeparator())
                        }
                    }
                }
            } catch (e: Exception) {
                log.error("Failed to upload the debugpaste {}", zipPath, e)
                sender.sendMessage("Failed to upload the debugpaste, the file is still available in your server directory.")
            }
        }

        @Nonnull
        private fun eval(vararg command: String): String {
            try {
                Runtime.getRuntime().exec(command).getInputStream().use { `in` -> return IOUtils.readInputStreamToString(`in`, Charset.defaultCharset()).trim() }
            } catch (e: Exception) {
                return e.toString().trim()
            }
        }
    }

    init {
        this.setPermission("nukkit.command.debug.perform")
        this.commandParameters.clear()
        this.commandParameters.put("clear", arrayOf<CommandParameter>(
                CommandParameter.newEnum("clear", arrayOf("clear"))
        ))
        this.commandParameters.put("upload", arrayOf<CommandParameter>(
                CommandParameter.newEnum("upload", arrayOf("upload")),
                CommandParameter.newEnum("last", true, arrayOf("last"))
        ))
        this.commandParameters.put("default", CommandParameter.EMPTY_ARRAY)
    }
}