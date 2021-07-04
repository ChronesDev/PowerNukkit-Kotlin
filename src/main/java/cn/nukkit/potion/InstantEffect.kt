package cn.nukkit.potion

import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.network.protocol.types.CommandOriginData.Origin
import CommandOriginData.Origin
import cn.nukkit.network.protocol.ItemStackRequestPacket.Request
import kotlin.jvm.Synchronized
import kotlin.jvm.JvmOverloads

/**
 * @author MagicDroidX (Nukkit Project)
 */
class InstantEffect : Effect {
    constructor(id: Int, name: String?, r: Int, g: Int, b: Int) : super(id, name, r, g, b) {}
    constructor(id: Int, name: String?, r: Int, g: Int, b: Int, isBad: Boolean) : super(id, name, r, g, b, isBad) {}
}