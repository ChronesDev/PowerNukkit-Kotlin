package cn.nukkit.command.simple

import cn.nukkit.command.data.CommandParamType

/**
 * @author nilsbrychzy
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
annotation class Parameter(val name: String, val type: CommandParamType = CommandParamType.RAWTEXT, val optional: Boolean = false)