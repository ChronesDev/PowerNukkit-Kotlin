package cn.nukkit.command

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class Command(name: String, description: String?, usageMessage: String?, aliases: Array<String>) {
    protected var commandData: CommandData
    val name: String
    private var nextLabel: String?
    var label: String?
        private set
    private var aliases: Array<String>
    private var activeAliases: Array<String>
    private var commandMap: CommandMap? = null
    var description: String?
    var usage: String
    var permission: String? = null
    var permissionMessage: String? = null
    protected var commandParameters: Map<String, Array<CommandParameter>> = HashMap()
    var timing: Timing

    constructor(name: String) : this(name, "", null, EmptyArrays.EMPTY_STRINGS) {}
    constructor(name: String, description: String?) : this(name, description, null, EmptyArrays.EMPTY_STRINGS) {}
    constructor(name: String, description: String?, usageMessage: String?) : this(name, description, usageMessage, EmptyArrays.EMPTY_STRINGS) {}

    /**
     * Returns an CommandData containing command data
     *
     * @return CommandData
     */
    val defaultCommandData: CommandData
        get() = commandData

    fun getCommandParameters(key: String): Array<CommandParameter> {
        return commandParameters[key]!!
    }

    fun getCommandParameters(): Map<String, Array<CommandParameter>> {
        return commandParameters
    }

    fun setCommandParameters(commandParameters: Map<String, Array<CommandParameter>>) {
        this.commandParameters = commandParameters
    }

    fun addCommandParameters(key: String?, parameters: Array<CommandParameter?>?) {
        commandParameters.put(key, parameters)
    }

    /**
     * Generates modified command data for the specified player
     * for AvailableCommandsPacket.
     *
     * @param player player
     * @return CommandData|null
     */
    fun generateCustomCommandData(player: Player): CommandDataVersions? {
        if (!testPermission(player)) {
            return null
        }
        val customData: CommandData = commandData.clone()
        if (getAliases().size > 0) {
            val aliases: List<String> = ArrayList(Arrays.asList(getAliases()))
            if (!aliases.contains(name)) {
                aliases.add(name)
            }
            customData.aliases = CommandEnum(name + "Aliases", aliases)
        }
        customData.description = player.getServer().getLanguage().translateString(description)
        commandParameters.forEach { key, par ->
            val overload = CommandOverload()
            overload.input.parameters = par
            customData.overloads.put(key, overload)
        }
        if (customData.overloads.size() === 0) customData.overloads.put("default", CommandOverload())
        val versions = CommandDataVersions()
        versions.versions.add(customData)
        return versions
    }

    val overloads: Map<String, Any>
        get() = commandData.overloads

    protected fun parseTilde(arg: String, pos: Double): Double {
        return if (arg.equals("~")) {
            pos
        } else if (!arg.startsWith("~")) {
            Double.parseDouble(arg)
        } else {
            pos + Double.parseDouble(arg.substring(1))
        }
    }

    abstract fun execute(sender: CommandSender?, commandLabel: String?, args: Array<String?>?): Boolean
    fun testPermission(target: CommandSender): Boolean {
        if (testPermissionSilent(target)) {
            return true
        }
        if (permissionMessage == null) {
            target.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.unknown", name))
        } else if (!permissionMessage!!.equals("")) {
            target.sendMessage(permissionMessage.replace("<permission>", permission))
        }
        return false
    }

    fun testPermissionSilent(target: CommandSender): Boolean {
        if (permission == null || permission!!.equals("")) {
            return true
        }
        val permissions: Array<String> = permission.split(";")
        for (permission in permissions) {
            if (target.hasPermission(permission)) {
                return true
            }
        }
        return false
    }

    fun setLabel(name: String?): Boolean {
        nextLabel = name
        if (!isRegistered) {
            label = name
            timing = Timings.getCommandTiming(this)
            return true
        }
        return false
    }

    fun register(commandMap: CommandMap?): Boolean {
        if (allowChangesFrom(commandMap)) {
            this.commandMap = commandMap
            return true
        }
        return false
    }

    fun unregister(commandMap: CommandMap?): Boolean {
        if (allowChangesFrom(commandMap)) {
            this.commandMap = null
            activeAliases = aliases
            label = nextLabel
            return true
        }
        return false
    }

    fun allowChangesFrom(commandMap: CommandMap?): Boolean {
        return commandMap != null && !commandMap.equals(this.commandMap)
    }

    val isRegistered: Boolean
        get() = commandMap != null

    fun getAliases(): Array<String> {
        return activeAliases
    }

    fun setAliases(aliases: Array<String>) {
        this.aliases = aliases
        if (!isRegistered) {
            activeAliases = aliases
        }
    }

    @Override
    override fun toString(): String {
        return name
    }

    companion object {
        private val defaultDataTemplate: CommandData? = null
        fun generateDefaultData(): CommandData {
            if (defaultDataTemplate == null) {
                //defaultDataTemplate = new Gson().fromJson(new InputStreamReader(Server.class.getClassLoader().getResourceAsStream("command_default.json")));
            }
            return defaultDataTemplate.clone()
        }

        fun broadcastCommandMessage(source: CommandSender?, message: String?) {
            broadcastCommandMessage(source, message, true)
        }

        fun broadcastCommandMessage(source: CommandSender, message: String?, sendToSource: Boolean) {
            val users: Set<Permissible> = source.getServer().getPluginManager().getPermissionSubscriptions(Server.BROADCAST_CHANNEL_ADMINISTRATIVE)
            val result = TranslationContainer("chat.type.admin", source.getName(), message)
            val colored = TranslationContainer(TextFormat.GRAY.toString() + "" + TextFormat.ITALIC + "%chat.type.admin", source.getName(), message)
            if (sendToSource && source !is ConsoleCommandSender) {
                source.sendMessage(message)
            }
            for (user in users) {
                if (user is CommandSender) {
                    if (user is ConsoleCommandSender) {
                        (user as ConsoleCommandSender).sendMessage(result)
                    } else if (!user.equals(source)) {
                        (user as CommandSender).sendMessage(colored)
                    }
                }
            }
        }

        fun broadcastCommandMessage(source: CommandSender?, message: TextContainer?) {
            broadcastCommandMessage(source, message, true)
        }

        fun broadcastCommandMessage(source: CommandSender, message: TextContainer, sendToSource: Boolean) {
            val m: TextContainer = message.clone()
            val resultStr = "[" + source.getName().toString() + ": " + (if (!m.getText().equals(source.getServer().getLanguage().get(m.getText()))) "%" else "") + m.getText().toString() + "]"
            val users: Set<Permissible> = source.getServer().getPluginManager().getPermissionSubscriptions(Server.BROADCAST_CHANNEL_ADMINISTRATIVE)
            val coloredStr: String = TextFormat.GRAY.toString() + "" + TextFormat.ITALIC + resultStr
            m.setText(resultStr)
            val result: TextContainer = m.clone()
            m.setText(coloredStr)
            val colored: TextContainer = m.clone()
            if (sendToSource && source !is ConsoleCommandSender) {
                source.sendMessage(message)
            }
            for (user in users) {
                if (user is CommandSender) {
                    if (user is ConsoleCommandSender) {
                        (user as ConsoleCommandSender).sendMessage(result)
                    } else if (!user.equals(source)) {
                        (user as CommandSender).sendMessage(colored)
                    }
                }
            }
        }
    }

    init {
        commandData = CommandData()
        this.name = name.toLowerCase() // Uppercase letters crash the client?!?
        nextLabel = name
        label = name
        this.description = description
        usage = usageMessage ?: "/$name"
        this.aliases = aliases
        activeAliases = aliases
        timing = Timings.getCommandTiming(this)
        commandParameters.put("default", arrayOf<CommandParameter>(CommandParameter.newType("args", true, CommandParamType.RAWTEXT)))
    }
}