package cn.nukkit.plugin

import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.network.protocol.types.CommandOriginData.Origin
import CommandOriginData.Origin
import cn.nukkit.network.protocol.ItemStackRequestPacket.Request
import kotlin.jvm.Synchronized
import kotlin.jvm.JvmOverloads

/**
 * @since 15-12-13
 */
class LibraryLoadException(library: Library) : RuntimeException("Load library " + library.getArtifactId().toString() + " failed!")