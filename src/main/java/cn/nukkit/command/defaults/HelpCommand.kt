package cn.nukkit.command.defaults

import cn.nukkit.command.Command

/**
 * @author MagicDroidX (Nukkit Project)
 */
class HelpCommand(name: String?) : VanillaCommand(name, "%nukkit.command.help.description", "%commands.help.usage", arrayOf("?")) {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>): Boolean {
        var args = args
        if (!this.testPermission(sender)) {
            return true
        }
        val command = StringBuilder()
        var pageNumber = 1
        var pageHeight = 5
        if (args.size != 0) {
            try {
                pageNumber = Integer.parseInt(args[args.size - 1])
                if (pageNumber <= 0) {
                    pageNumber = 1
                }
                val newargs = arrayOfNulls<String>(args.size - 1)
                System.arraycopy(args, 0, newargs, 0, newargs.size)
                args = newargs
                /*if (args.length > 1) {
                    args = Arrays.copyOfRange(args, 0, args.length - 2);
                } else {
                    args = new String[0];
                }*/for (arg in args) {
                    if (!command.toString().equals("")) {
                        command.append(" ")
                    }
                    command.append(arg)
                }
            } catch (e: NumberFormatException) {
                pageNumber = 1
                for (arg in args) {
                    if (!command.toString().equals("")) {
                        command.append(" ")
                    }
                    command.append(arg)
                }
            }
        }
        if (sender is ConsoleCommandSender) {
            pageHeight = Integer.MAX_VALUE
        }
        return if (command.toString().equals("")) {
            val commands: Map<String, Command> = TreeMap()
            for (cmd in sender.getServer().getCommandMap().getCommands().values()) {
                if (cmd.testPermissionSilent(sender)) {
                    commands.put(cmd.getName(), cmd)
                }
            }
            val totalPage: Int = if (commands.size() % pageHeight === 0) commands.size() / pageHeight else commands.size() / pageHeight + 1
            pageNumber = Math.min(pageNumber, totalPage)
            if (pageNumber < 1) {
                pageNumber = 1
            }
            sender.sendMessage(TranslationContainer("commands.help.header", String.valueOf(pageNumber), String.valueOf(totalPage)))
            var i = 1
            for (command1 in commands.values()) {
                if (i >= (pageNumber - 1) * pageHeight + 1 && i <= Math.min(commands.size(), pageNumber * pageHeight)) {
                    sender.sendMessage(TextFormat.DARK_GREEN.toString() + "/" + command1.getName() + ": " + TextFormat.WHITE + command1.getDescription())
                }
                i++
            }
            true
        } else {
            val cmd: Command = sender.getServer().getCommandMap().getCommand(command.toString().toLowerCase())
            if (cmd != null) {
                if (cmd.testPermissionSilent(sender)) {
                    var message: String = TextFormat.YELLOW.toString() + "--------- " + TextFormat.WHITE + " Help: /" + cmd.getName() + TextFormat.YELLOW + " ---------\n"
                    message += TextFormat.GOLD.toString() + "Description: " + TextFormat.WHITE + cmd.getDescription() + "\n"
                    val usage = StringBuilder()
                    val usages: Array<String> = cmd.getUsage().split("\n")
                    for (u in usages) {
                        if (!usage.toString().equals("")) {
                            usage.append("""
    
    ${TextFormat.WHITE}
    """.trimIndent())
                        }
                        usage.append(u)
                    }
                    message += TextFormat.GOLD.toString() + "Usage: " + TextFormat.WHITE + usage + "\n"
                    sender.sendMessage(message)
                    return true
                }
            }
            sender.sendMessage(TextFormat.RED.toString() + "No help for " + command.toString().toLowerCase())
            true
        }
    }

    init {
        this.setPermission("nukkit.command.help")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("page", true, CommandParamType.INT)
        ))
    }
}