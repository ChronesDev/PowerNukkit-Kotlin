package cn.nukkit.event.player

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class PlayerCreationEvent(interfaz: SourceInterface, baseClass: Class<out Player?>, playerClass: Class<out Player?>, clientId: Long, socketAddress: InetSocketAddress) : Event() {
    private val interfaz: SourceInterface
    val clientId: Long
    private val socketAddress: InetSocketAddress
    private var baseClass: Class<out Player?>
    private var playerClass: Class<out Player?>
    val `interface`: SourceInterface
        get() = interfaz
    val address: String
        get() = socketAddress.getAddress().toString()
    val port: Int
        get() = socketAddress.getPort()

    fun getSocketAddress(): InetSocketAddress {
        return socketAddress
    }

    fun getBaseClass(): Class<out Player?> {
        return baseClass
    }

    fun setBaseClass(baseClass: Class<out Player?>) {
        this.baseClass = baseClass
    }

    fun getPlayerClass(): Class<out Player?> {
        return playerClass
    }

    fun setPlayerClass(playerClass: Class<out Player?>) {
        this.playerClass = playerClass
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.interfaz = interfaz
        this.clientId = clientId
        this.socketAddress = socketAddress
        this.baseClass = baseClass
        this.playerClass = playerClass
    }
}