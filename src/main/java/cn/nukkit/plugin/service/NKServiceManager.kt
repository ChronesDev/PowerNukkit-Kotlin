package cn.nukkit.plugin.service

import cn.nukkit.Server

/**
 * @since 16-11-20
 */
class NKServiceManager : ServiceManager {
    private val handle: Map<Class<*>, List<RegisteredServiceProvider<*>>> = HashMap()
    @Override
    fun <T> register(service: Class<T>, provider: T, plugin: Plugin?, priority: ServicePriority): Boolean {
        Preconditions.checkNotNull(provider)
        Preconditions.checkNotNull(priority)
        Preconditions.checkNotNull(service)

        // build-in service provider needn't plugin param
        if (plugin == null && provider.getClass().getClassLoader() !== Server::class.java.getClassLoader()) {
            throw NullPointerException("plugin")
        }
        return provide(service, provider, plugin, priority)
    }

    protected fun <T> provide(service: Class<T>, instance: T, plugin: Plugin?, priority: ServicePriority): Boolean {
        synchronized(handle) {
            val list: List<RegisteredServiceProvider<*>> = handle.computeIfAbsent(service) { k -> ArrayList() }
            val registered: RegisteredServiceProvider<T> = RegisteredServiceProvider(service, instance, priority, plugin)
            val position: Int = Collections.binarySearch(list, registered)
            if (position > -1) return false
            list.add(-(position + 1), registered)
        }
        return true
    }

    @Override
    override fun cancel(plugin: Plugin): List<RegisteredServiceProvider<*>> {
        val builder: ImmutableList.Builder<RegisteredServiceProvider<*>> = ImmutableList.builder()
        var it: Iterator<RegisteredServiceProvider<*>>
        var registered: RegisteredServiceProvider<*>
        synchronized(handle) {
            for (list in handle.values()) {
                it = list.iterator()
                while (it.hasNext()) {
                    registered = it.next()
                    if (registered.getPlugin() === plugin) {
                        it.remove()
                        builder.add(registered)
                    }
                }
            }
        }
        return builder.build()
    }

    @Override
    override fun <T> cancel(service: Class<T>, provider: T): RegisteredServiceProvider<T>? {
        var result: RegisteredServiceProvider<T>? = null
        synchronized(handle) {
            val it: Iterator<RegisteredServiceProvider<*>> = handle[service]!!.iterator()
            var next: RegisteredServiceProvider
            while (it.hasNext() && result == null) {
                next = it.next()
                if (next.getProvider() === provider) {
                    it.remove()
                    result = next
                }
            }
        }
        return result
    }

    @Override
    override fun <T> getProvider(service: Class<T>): RegisteredServiceProvider<T>? {
        synchronized(handle) {
            val list: List<RegisteredServiceProvider<*>>? = handle[service]
            return if (list == null || list.isEmpty()) null else list[0] as RegisteredServiceProvider<T>
        }
    }

    @get:Override
    override val knownService: List<Any?>?
        get() = ImmutableList.copyOf(handle.keySet())

    @Override
    override fun getRegistrations(plugin: Plugin?): List<RegisteredServiceProvider<*>> {
        val builder: ImmutableList.Builder<RegisteredServiceProvider<*>> = ImmutableList.builder()
        synchronized(handle) {
            for (registered in handle.values()) {
                for (provider in registered) {
                    if (provider.getPlugin()!!.equals(plugin)) {
                        builder.add(provider)
                    }
                }
            }
        }
        return builder.build()
    }

    @Override
    override fun <T> getRegistrations(service: Class<T>): List<RegisteredServiceProvider<T>> {
        val builder: ImmutableList.Builder<RegisteredServiceProvider<T>> = ImmutableList.builder()
        synchronized(handle) {
            val registered: List<RegisteredServiceProvider<*>>? = handle[service]
            if (registered == null) {
                return ImmutableList.of()
            }
            for (provider in registered!!) {
                builder.add(provider as RegisteredServiceProvider<T>)
            }
        }
        return builder.build()
    }

    @Override
    override fun <T> isProvidedFor(service: Class<T>): Boolean {
        synchronized(handle) { return handle.containsKey(service) }
    }
}