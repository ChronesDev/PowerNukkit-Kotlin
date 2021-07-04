package cn.nukkit.command.simple

import java.lang.annotation.ElementType

/**
 * @author nilsbrychzy
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
annotation class CommandParameters(val parameters: Array<Parameters> = [])