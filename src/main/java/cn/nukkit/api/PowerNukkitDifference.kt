package cn.nukkit.api

import java.lang.annotation.*

/**
 * Indicates that the annotated element works differently in PowerNukkit environment
 * and may cause issues or unexpected behaviour when used in a normal NukkitX server
 * without PowerNukkit's patches and features.
 */
@Retention(RetentionPolicy.CLASS)
@Target([ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD, ElementType.PACKAGE])
@PowerNukkitOnly
@Since("1.3.0.0-PN")
@Inherited
@Documented
@Repeatable(PowerNukkitDifference.DifferenceList::class)
annotation class PowerNukkitDifference(val info: String, val since: String = "") {
    @Retention(RetentionPolicy.CLASS)
    @Target([ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD, ElementType.PACKAGE])
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Inherited
    @Documented
    annotation class DifferenceList(vararg val value: PowerNukkitDifference)
}