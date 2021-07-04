package cn.nukkit.command.simple

import java.lang.annotation.ElementType

/**
 * @author nilsbrychzy
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
annotation class Parameters(val name: String, val parameters: Array<Parameter>)