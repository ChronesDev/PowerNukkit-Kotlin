package cn.nukkit.plugin.service

import cn.nukkit.plugin.Plugin

/**
 * @since 16-11-20
 */
class RegisteredServiceProvider<T> internal constructor(service: Class<T>, provider: T, priority: ServicePriority, plugin: Plugin?) : Comparable<RegisteredServiceProvider<T>?> {
    private val plugin: Plugin?
    private val priority: ServicePriority
    private val service: Class<T>

    /**
     * Return the service provider.
     *
     * @return the service provider
     */
    val provider: T

    /**
     * Return the provided service.
     *
     * @return the provided service
     */
    fun getService(): Class<T> {
        return service
    }

    /**
     * Return the plugin provide this service.
     *
     * @return the plugin provide this service, or `null`
     * only if this service provided by server
     */
    fun getPlugin(): Plugin? {
        return plugin
    }

    fun getPriority(): ServicePriority {
        return priority
    }

    @Override
    override fun equals(o: Object?): Boolean {
        if (this === o) return true
        if (o == null || getClass() !== o.getClass()) return false
        val that = o as RegisteredServiceProvider<*>
        return provider === that.provider || provider!!.equals(that.provider)
    }

    @Override
    override fun hashCode(): Int {
        return provider!!.hashCode()
    }

    operator fun compareTo(other: RegisteredServiceProvider<T>): Int {
        return other.priority.ordinal() - priority.ordinal()
    }

    init {
        this.plugin = plugin
        this.provider = provider
        this.service = service
        this.priority = priority
    }
}