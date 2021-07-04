package cn.nukkit.api

import java.lang.annotation.*

/**
 * Indicates which version added the annotated element.
 */
@Retention(RetentionPolicy.CLASS)
@Target([ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD, ElementType.PACKAGE])
@Documented
@Inherited
annotation class Since(
        /**
         * The version which added the element.
         */
        val value: String)