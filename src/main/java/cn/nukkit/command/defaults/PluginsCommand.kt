package cn.nukkit.command.defaults

import cn.nukkit.command.CommandSender

/**
 * @author xtypr
 * @since 2015/11/12
 */
class PluginsCommand(name: String?) : VanillaCommand(name,
        "%nukkit.command.plugins.description",
        "%nukkit.command.plugins.usage", arrayOf("pl")) {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>?): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        sendPluginList(sender)
        return true
    }

    private fun sendPluginList(sender: CommandSender) {
        val list = StringBuilder()
        val plugins: Map<String, Plugin> = sender.getServer().getPluginManager().getPlugins()
        for (plugin in plugins.values()) {
            if (list.length() > 0) {
                list.append(TextFormat.WHITE.toString() + ", ")
            }
            list.append(if (plugin.isEnabled()) TextFormat.GREEN else TextFormat.RED)
            list.append(plugin.getDescription().getFullName())
        }
        sender.sendMessage(TranslationContainer("nukkit.command.plugins.success", String.valueOf(plugins.size()), list.toString()))
    }

    init {
        this.setPermission("nukkit.command.plugins")
        this.commandParameters.clear()
    }
}