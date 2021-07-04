package cn.nukkit.event.player

import cn.nukkit.Player

/**
 * This event is called asynchronously
 *
 * @author CreeperFace
 */
class PlayerAsyncPreLoginEvent @Since("1.4.0.0-PN") constructor(val name: String?, uuid: UUID?, chainData: LoginChainData, skin: Skin?, address: String?, port: Int) : PlayerEvent() {
    private val uuid: UUID?
    private val chainData: LoginChainData
    private var skin: Skin?
    val address: String?
    val port: Int
    var loginResult = LoginResult.SUCCESS
    var kickMessage = "Plugin Reason"
    private val scheduledActions: List<Consumer<Server>> = ArrayList()

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "LoginChainData and Skin were added by refactoring this constructor", replaceWith = "PlayerAsyncPreLoginEvent(String name, UUID uuid, LoginChainData chainData, Skin skin, String address, int port)")
    @PowerNukkitOnly("The signature was changed in Cloudburst Nukkit and we re-added this constructor for backward-compatibility")
    constructor(name: String?, uuid: UUID?, address: String?, port: Int) : this(name, uuid, ClientChainData.of(EmptyArrays.EMPTY_BYTES), null, address, port) {
        // TODO PowerNukkit: I think this might cause an exception...
    }

    @Override
    override fun getPlayer(): Player {
        throw UnsupportedOperationException("Could not get player instance in an async event")
    }

    fun getUuid(): UUID? {
        return uuid
    }

    @Since("1.4.0.0-PN")
    fun getChainData(): LoginChainData {
        return chainData
    }

    val xuid: String
        @Since("1.4.0.0-PN") get() = chainData.getXUID()

    fun getSkin(): Skin? {
        return skin
    }

    fun setSkin(skin: Skin?) {
        this.skin = skin
    }

    fun scheduleSyncAction(action: Consumer<Server?>?) {
        scheduledActions.add(action)
    }

    fun getScheduledActions(): List<Consumer<Server>> {
        return ArrayList(scheduledActions)
    }

    fun allow() {
        loginResult = LoginResult.SUCCESS
    }

    fun disAllow(message: String) {
        loginResult = LoginResult.KICK
        kickMessage = message
    }

    enum class LoginResult {
        SUCCESS, KICK
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.uuid = uuid
        this.chainData = chainData
        this.skin = skin
        this.address = address
        this.port = port
    }
}