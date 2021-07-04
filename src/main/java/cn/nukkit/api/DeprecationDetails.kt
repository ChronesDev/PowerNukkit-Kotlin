package cn.nukkit.api

import java.lang.annotation.*

/**
 * Describe the deprecation with more details. This is persisted to the class file, so it can be read without javadocs.
 */
@Retention(RetentionPolicy.CLASS)
@Target([ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD, ElementType.PACKAGE])
@Documented
annotation class DeprecationDetails(
        /**
         * The version which marked this element as deprecated.
         */
        val since: String,
        /**
         * Why it is deprecated.
         */
        val reason: String,
        /**
         * What should be used or do instead.
         */
        val replaceWith: String = "",
        /**
         * When the annotated element will be removed or have it's signature changed.
         */
        val toBeRemovedAt: String = "",
        /**
         * The maintainer party that has added this depreciation. For example: PowerNukkit, Cloudburst Nukkit, and Nukkit
         */
        val by: String = "")