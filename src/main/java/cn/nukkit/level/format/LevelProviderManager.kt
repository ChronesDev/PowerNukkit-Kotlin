package cn.nukkit.level.format

import cn.nukkit.Server

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
object LevelProviderManager {
    internal val providers: Map<String, Class<out LevelProvider?>> = HashMap()
    fun addProvider(server: Server?, clazz: Class<out LevelProvider?>) {
        try {
            providers.put(clazz.getMethod("getProviderName").invoke(null) as String, clazz)
        } catch (e: Exception) {
            log.error("An error occurred while adding the level provider {}", clazz, e)
        }
    }

    fun getProvider(path: String?): Class<out LevelProvider?>? {
        for (provider in providers.values()) {
            try {
                if (provider.getMethod("isValid", String::class.java).invoke(null, path)) {
                    return provider
                }
            } catch (e: Exception) {
                log.error("An error occurred while getting the provider {}", path, e)
            }
        }
        return null
    }

    fun getProviderByName(name: String): Class<out LevelProvider?> {
        var name = name
        name = name.trim().toLowerCase()
        return providers.getOrDefault(name, null)
    }
}