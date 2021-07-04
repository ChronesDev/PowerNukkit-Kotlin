package cn.nukkit.plugin.service

import cn.nukkit.plugin.Plugin

/**
 * @since 16-11-20
 */
interface ServiceManager {
    /**
     * Register an object as a service's provider.
     *
     * @param service  the service
     * @param provider the service provider
     * @param plugin   the plugin
     * @param priority the priority
     * @return `true`, or `false` only if `provider`
     * already registered
     */
    fun <T> register(service: Class<T>?, provider: T, plugin: Plugin?, priority: ServicePriority?): Boolean

    /**
     * Cancel service's provider(s) offered this plugin.
     *
     * @param plugin the plugin
     * @return a [com.google.common.collect.ImmutableList]
     * contains cancelled [RegisteredServiceProvider]
     */
    fun cancel(plugin: Plugin?): List<RegisteredServiceProvider<*>?>?

    /**
     * Cancel a service's provider.
     *
     * @param service  the service
     * @param provider the provider
     * @return the cancelled [RegisteredServiceProvider], or `null` if not
     * any provider cancelled
     */
    fun <T> cancel(service: Class<T>?, provider: T): RegisteredServiceProvider<T>?

    /**
     * Return the service's provider.
     *
     * @param service the target service
     * @return a [RegisteredServiceProvider] registered highest priority, or
     * `null` if not exists
     */
    fun <T> getProvider(service: Class<T>?): RegisteredServiceProvider<T>?

    /**
     * Return the known service(s).
     *
     * @return a [com.google.common.collect.ImmutableList] contains the
     * known service(s)
     */
    val knownService: List<Any?>?
    fun getRegistrations(plugin: Plugin?): List<RegisteredServiceProvider<*>?>?
    fun <T> getRegistrations(service: Class<T>?): List<RegisteredServiceProvider<T>?>?
    fun <T> isProvidedFor(service: Class<T>?): Boolean
}