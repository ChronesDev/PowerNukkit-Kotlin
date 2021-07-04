package cn.nukkit.command

import cn.nukkit.lang.TranslationContainer

/**
 * @author MagicDroidX (Nukkit Project)
 */
class PluginCommand<T : Plugin?>(name: String, @get:Override override val plugin: T) : Command(name), PluginIdentifiableCommand {
    private var executor: CommandExecutor
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>?): Boolean {
        if (!plugin.isEnabled()) {
            return false
        }
        if (!this.testPermission(sender)) {
            return false
        }
        val success: Boolean = executor.onCommand(sender, this, commandLabel, args)
        if (!success && !this.usageMessage.equals("")) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
        }
        return success
    }

    fun getExecutor(): CommandExecutor {
        return executor
    }

    fun setExecutor(executor: CommandExecutor?) {
        this.executor = if (executor != null) executor else plugin
    }

    init {
        executor = plugin
        this.usageMessage = ""
    }
}