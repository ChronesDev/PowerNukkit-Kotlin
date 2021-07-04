package cn.nukkit.command.simple

import java.lang.annotation.ElementType

/**
 * @author Tee7even
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
annotation class Arguments(val min: Int = 0, val max: Int = 0)