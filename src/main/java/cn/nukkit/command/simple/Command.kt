package cn.nukkit.command.simple

import java.lang.annotation.ElementType

/**
 * @author Tee7even
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
annotation class Command(val name: String, val description: String = "", val usageMessage: String = "", val aliases: Array<String> = [])