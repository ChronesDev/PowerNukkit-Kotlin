package cn.nukkit.command

import cn.nukkit.Server

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
class SimpleCommandMap(server: Server) : CommandMap {
    protected val knownCommands: Map<String?, Command> = HashMap()
    private val server: Server
    private fun setDefaultCommands() {
        this.register("nukkit", VersionCommand("version"))
        this.register("nukkit", PluginsCommand("plugins"))
        this.register("nukkit", SeedCommand("seed"))
        this.register("nukkit", HelpCommand("help"))
        this.register("nukkit", StopCommand("stop"))
        this.register("nukkit", TellCommand("tell"))
        this.register("nukkit", DefaultGamemodeCommand("defaultgamemode"))
        this.register("nukkit", BanCommand("ban"))
        this.register("nukkit", BanIpCommand("ban-ip"))
        this.register("nukkit", BanListCommand("banlist"))
        this.register("nukkit", PardonCommand("pardon"))
        this.register("nukkit", PardonIpCommand("pardon-ip"))
        this.register("nukkit", SayCommand("say"))
        this.register("nukkit", MeCommand("me"))
        this.register("nukkit", ListCommand("list"))
        this.register("nukkit", DifficultyCommand("difficulty"))
        this.register("nukkit", KickCommand("kick"))
        this.register("nukkit", OpCommand("op"))
        this.register("nukkit", DeopCommand("deop"))
        this.register("nukkit", WhitelistCommand("whitelist"))
        this.register("nukkit", SaveOnCommand("save-on"))
        this.register("nukkit", SaveOffCommand("save-off"))
        this.register("nukkit", SaveCommand("save-all"))
        this.register("nukkit", GiveCommand("give"))
        this.register("nukkit", EffectCommand("effect"))
        this.register("nukkit", EnchantCommand("enchant"))
        this.register("nukkit", ParticleCommand("particle"))
        this.register("nukkit", GamemodeCommand("gamemode"))
        this.register("nukkit", GameruleCommand("gamerule"))
        this.register("nukkit", KillCommand("kill"))
        this.register("nukkit", SpawnpointCommand("spawnpoint"))
        this.register("nukkit", SetWorldSpawnCommand("setworldspawn"))
        this.register("nukkit", TeleportCommand("tp"))
        this.register("nukkit", TimeCommand("time"))
        this.register("nukkit", TitleCommand("title"))
        this.register("nukkit", ReloadCommand("reload"))
        this.register("nukkit", WeatherCommand("weather"))
        this.register("nukkit", XpCommand("xp"))
        this.register("nukkit", SetBlockCommand("setblock"))

//        if ((boolean) this.server.getConfig("debug.commands", false)) {
        this.register("nukkit", StatusCommand("status"))
        this.register("nukkit", GarbageCollectorCommand("gc"))
        this.register("nukkit", TimingsCommand("timings"))
        this.register("nukkit", DebugPasteCommand("debugpaste"))
        //this.register("nukkit", new DumpMemoryCommand("dumpmemory"));
//        }
    }

    @Override
    fun registerAll(fallbackPrefix: String, commands: List<Command?>) {
        for (command in commands) {
            this.register(fallbackPrefix, command)
        }
    }

    @Override
    fun register(fallbackPrefix: String, command: Command?): Boolean {
        return this.register(fallbackPrefix, command, null)
    }

    @Override
    fun register(fallbackPrefix: String, command: Command?, label: String?): Boolean {
        var fallbackPrefix = fallbackPrefix
        var label = label
        if (label == null) {
            label = command.getName()
        }
        label = label.trim().toLowerCase()
        fallbackPrefix = fallbackPrefix.trim().toLowerCase()
        val registered = registerAlias(command, false, fallbackPrefix, label)
        val aliases: List<String> = ArrayList(Arrays.asList(command!!.getAliases()))
        val iterator = aliases.iterator()
        while (iterator.hasNext()) {
            val alias = iterator.next()
            if (!registerAlias(command, true, fallbackPrefix, alias)) {
                iterator.remove()
            }
        }
        command!!.setAliases(aliases.toArray(EmptyArrays.EMPTY_STRINGS))
        if (!registered) {
            command!!.setLabel("$fallbackPrefix:$label")
        }
        command!!.register(this)
        return registered
    }

    @Override
    override fun registerSimpleCommands(`object`: Object) {
        for (method in `object`.getClass().getDeclaredMethods()) {
            val def: cn.nukkit.command.simple.Command = method.getAnnotation(cn.nukkit.command.simple.Command::class.java)
            if (def != null) {
                val sc = SimpleCommand(`object`, method, def.name(), def.description(), def.usageMessage(), def.aliases())
                val args: Arguments = method.getAnnotation(Arguments::class.java)
                if (args != null) {
                    sc.setMaxArgs(args.max())
                    sc.setMinArgs(args.min())
                }
                val perm: CommandPermission = method.getAnnotation(CommandPermission::class.java)
                if (perm != null) {
                    sc.setPermission(perm.value())
                }
                if (method.isAnnotationPresent(ForbidConsole::class.java)) {
                    sc.setForbidConsole(true)
                }
                val commandParameters: CommandParameters = method.getAnnotation(CommandParameters::class.java)
                if (commandParameters != null) {
                    val map: Map<String, Array<CommandParameter>> = Arrays.stream(commandParameters.parameters())
                            .collect(Collectors.toMap(Parameters::name) { parameters ->
                                Arrays.stream(parameters.parameters())
                                        .map { parameter -> CommandParameter.newType(parameter.name(), parameter.optional(), parameter.type()) }
                                        .distinct()
                                        .toArray { _Dummy_.__Array__() }
                            })
                    sc.commandParameters.putAll(map)
                }
                this.register(def.name(), sc)
            }
        }
    }

    private fun registerAlias(command: Command?, isAlias: Boolean, fallbackPrefix: String, label: String?): Boolean {
        knownCommands.put("$fallbackPrefix:$label", command)

        //if you're registering a command alias that is already registered, then return false
        val alreadyRegistered = knownCommands.containsKey(label)
        val existingCommand: Command? = knownCommands[label]
        val existingCommandIsNotVanilla = alreadyRegistered && existingCommand !is VanillaCommand
        //basically, if we're an alias and it's already registered, or we're a vanilla command, then we can't override it
        if ((command is VanillaCommand || isAlias) && alreadyRegistered && existingCommandIsNotVanilla) {
            return false
        }

        //if you're registering a name (alias or label) which is identical to another command who's primary name is the same
        //so basically we can't override the main name of a command, but we can override aliases if we're not an alias

        //added the last statement which will allow us to override a VanillaCommand unconditionally
        if (alreadyRegistered && existingCommand.getLabel() != null && existingCommand.getLabel().equals(label) && existingCommandIsNotVanilla) {
            return false
        }

        //you can now assume that the command is either uniquely named, or overriding another command's alias (and is not itself, an alias)
        if (!isAlias) {
            command!!.setLabel(label)
        }

        // Then we need to check if there isn't any command conflicts with vanilla commands
        val toRemove: ArrayList<String> = ArrayList()
        for (entry in knownCommands.entrySet()) {
            val cmd: Command = entry.getValue()
            if (cmd.getLabel().equalsIgnoreCase(command.getLabel()) && !cmd.equals(command)) { // If the new command conflicts... (But if it isn't the same command)
                if (cmd is VanillaCommand) { // And if the old command is a vanilla command...
                    // Remove it!
                    toRemove.add(entry.getKey())
                }
            }
        }

        // Now we loop the toRemove list to remove the command conflicts from the knownCommands map
        for (cmd in toRemove) {
            knownCommands.remove(cmd)
        }
        knownCommands.put(label, command)
        return true
    }

    private fun parseArguments(cmdLine: String): ArrayList<String> {
        val sb = StringBuilder(cmdLine)
        val args: ArrayList<String> = ArrayList()
        var notQuoted = true
        var start = 0
        var i = 0
        while (i < sb.length()) {
            if (sb.charAt(i) === '\\') {
                sb.deleteCharAt(i)
                i++
                continue
            }
            if (sb.charAt(i) === ' ' && notQuoted) {
                val arg: String = sb.substring(start, i)
                if (!arg.isEmpty()) {
                    args.add(arg)
                }
                start = i + 1
            } else if (sb.charAt(i) === '"') {
                sb.deleteCharAt(i)
                --i
                notQuoted = !notQuoted
            }
            i++
        }
        val arg: String = sb.substring(start)
        if (!arg.isEmpty()) {
            args.add(arg)
        }
        return args
    }

    @Override
    fun dispatch(sender: CommandSender, cmdLine: String): Boolean {
        val parsed: ArrayList<String> = parseArguments(cmdLine)
        if (parsed.size() === 0) {
            return false
        }
        val sentCommandLabel: String = parsed.remove(0).toLowerCase()
        val args: Array<String?> = parsed.toArray(EmptyArrays.EMPTY_STRINGS)
        val target: Command = getCommand(sentCommandLabel) ?: return false
        target.timing.startTiming()
        try {
            target.execute(sender, sentCommandLabel, args)
        } catch (e: Exception) {
            sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.exception"))
            log.fatal(server.getLanguage().translateString("nukkit.command.exception", cmdLine, target.toString(), Utils.getExceptionMessage(e)), e)
        }
        target.timing.stopTiming()
        return true
    }

    @Override
    override fun clearCommands() {
        for (command in knownCommands.values()) {
            command.unregister(this)
        }
        knownCommands.clear()
        setDefaultCommands()
    }

    @Override
    override fun getCommand(name: String?): Command? {
        return if (knownCommands.containsKey(name)) {
            knownCommands[name]
        } else null
    }

    val commands: Map<String?, cn.nukkit.command.Command>
        get() = knownCommands

    fun registerServerAliases() {
        val values: Map<String, List<String>> = server.getCommandAliases()
        for (entry in values.entrySet()) {
            val alias: String = entry.getKey()
            val commandStrings: List<String> = entry.getValue()
            if (alias.contains(" ") || alias.contains(":")) {
                log.warn(server.getLanguage().translateString("nukkit.command.alias.illegal", alias))
                continue
            }
            val targets: List<String> = ArrayList()
            val bad = StringBuilder()
            for (commandString in commandStrings) {
                val args: Array<String> = commandString.split(" ")
                val command: Command? = getCommand(args[0])
                if (command == null) {
                    if (bad.length() > 0) {
                        bad.append(", ")
                    }
                    bad.append(commandString)
                } else {
                    targets.add(commandString)
                }
            }
            if (bad.length() > 0) {
                log.warn(server.getLanguage().translateString("nukkit.command.alias.notFound", arrayOf(alias, bad.toString())))
                continue
            }
            if (!targets.isEmpty()) {
                knownCommands.put(alias.toLowerCase(), FormattedCommandAlias(alias.toLowerCase(), targets))
            } else {
                knownCommands.remove(alias.toLowerCase())
            }
        }
    }

    init {
        this.server = server
        setDefaultCommands()
    }
}