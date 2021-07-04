package cn.nukkit

import cn.nukkit.lang.BaseLang

object ServerTest {
    fun setInstance(server: Server?) {
        assertDoesNotThrow {
            val instance: Field = Server::class.java.getDeclaredField("instance")
            instance.setAccessible(true)
            instance.set(null, server)
        }
    }

    fun setConfig(server: Server?, config: Config?) {
        assertDoesNotThrow {
            val instance: Field = Server::class.java.getDeclaredField("config")
            instance.setAccessible(true)
            instance.set(server, config)
        }
    }

    fun setLanguage(server: Server?, lang: BaseLang?) {
        assertDoesNotThrow {
            val instance: Field = Server::class.java.getDeclaredField("baseLang")
            instance.setAccessible(true)
            instance.set(server, lang)
        }
    }

    fun setPluginManager(server: Server?, pluginManager: PluginManager?) {
        assertDoesNotThrow {
            val instance: Field = Server::class.java.getDeclaredField("pluginManager")
            instance.setAccessible(true)
            instance.set(server, pluginManager)
        }
    }
}