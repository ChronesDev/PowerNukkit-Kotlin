package cn.nukkit.plugin

import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.network.protocol.types.CommandOriginData.Origin
import CommandOriginData.Origin
import cn.nukkit.network.protocol.ItemStackRequestPacket.Request
import kotlin.jvm.Synchronized
import kotlin.jvm.JvmOverloads

/**
 * 描述一个Nukkit插件加载顺序的类。<br></br>
 * Describes a Nukkit plugin load order.
 *
 *
 *
 * Nukkit插件的加载顺序有两个:[cn.nukkit.plugin.PluginLoadOrder.STARTUP]
 * 和 [cn.nukkit.plugin.PluginLoadOrder.POSTWORLD]。<br></br>
 * The load order of a Nukkit plugin can be [cn.nukkit.plugin.PluginLoadOrder.STARTUP]
 * or [cn.nukkit.plugin.PluginLoadOrder.POSTWORLD].
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author iNevet(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
enum class PluginLoadOrder {
    /**
     * 表示这个插件在服务器启动时就开始加载。<br></br>
     * Indicates that the plugin will be loaded at startup.
     *
     * @see cn.nukkit.plugin.PluginLoadOrder
     *
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    STARTUP,

    /**
     * 表示这个插件在第一个世界加载完成后开始加载。<br></br>
     * Indicates that the plugin will be loaded after the first/default world was created.
     *
     * @see cn.nukkit.plugin.PluginLoadOrder
     *
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    POSTWORLD
}