package cn.nukkit.network

import io.netty.buffer.ByteBuf

/**
 * @author MagicDroidX (Nukkit Project)
 */
interface AdvancedSourceInterface : SourceInterface {
    fun blockAddress(address: InetAddress?)
    fun blockAddress(address: InetAddress?, timeout: Int)
    fun unblockAddress(address: InetAddress?)
    fun setNetwork(network: Network?)
    fun sendRawPacket(socketAddress: InetSocketAddress?, payload: ByteBuf?)
}