package cn.nukkit.command.defaults

import cn.nukkit.command.CommandSender

/**
 * @author xtypr
 * @since 2015/11/12
 */
class VersionCommand(name: String?) : VanillaCommand(name,
        "%nukkit.command.version.description",
        "%nukkit.command.version.usage", arrayOf("ver", "about")) {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size == 0) {
            sender.sendMessage(TranslationContainer("nukkit.server.info.extended", sender.getServer().getName(),
                    sender.getServer().getNukkitVersion().toString() + " (" + sender.getServer().getGitCommit() + ")",
                    sender.getServer().getCodename(),
                    sender.getServer().getApiVersion(),
                    sender.getServer().getVersion(),
                    String.valueOf(ProtocolInfo.CURRENT_PROTOCOL)))
        } else {
            var pluginName = StringBuilder()
            for (arg in args) pluginName.append(arg).append(" ")
            pluginName = StringBuilder(pluginName.toString().trim())
            val found = booleanArrayOf(false)
            val exactPlugin: Array<Plugin?> = arrayOf<Plugin?>(sender.getServer().getPluginManager().getPlugin(pluginName.toString()))
            if (exactPlugin[0] == null) {
                pluginName = StringBuilder(pluginName.toString().toLowerCase())
                val finalPluginName: String = pluginName.toString()
                sender.getServer().getPluginManager().getPlugins().forEach { s, p ->
                    if (s.toLowerCase().contains(finalPluginName)) {
                        exactPlugin[0] = p
                        found[0] = true
                    }
                }
            } else {
                found[0] = true
            }
            if (found[0]) {
                val desc: PluginDescription = exactPlugin[0].getDescription()
                sender.sendMessage(TextFormat.DARK_GREEN + desc.getName() + TextFormat.WHITE.toString() + " version " + TextFormat.DARK_GREEN + desc.getVersion())
                if (desc.getDescription() != null) {
                    sender.sendMessage(desc.getDescription())
                }
                if (desc.getWebsite() != null) {
                    sender.sendMessage("Website: " + desc.getWebsite())
                }
                val authors: List<String> = desc.getAuthors()
                val authorsString = arrayOf("")
                authors.forEach { s -> authorsString[0] += s }
                if (authors.size() === 1) {
                    sender.sendMessage("Author: " + authorsString[0])
                } else if (authors.size() >= 2) {
                    sender.sendMessage("Authors: " + authorsString[0])
                }
            } else {
                sender.sendMessage(TranslationContainer("nukkit.command.version.noSuchPlugin"))
            }
        }
        return true
    }

    init {
        this.setPermission("nukkit.command.version")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("pluginName", true, CommandParamType.STRING)
        ))
    }
}