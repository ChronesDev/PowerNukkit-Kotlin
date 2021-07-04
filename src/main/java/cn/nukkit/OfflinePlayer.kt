package cn.nukkit

import cn.nukkit.metadata.MetadataValue

/**
 * 描述一个不在线的玩家的类。<br></br>
 * Describes an offline player.
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see cn.nukkit.Player
 *
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
class OfflinePlayer(server: Server, uuid: UUID?, name: String?) : IPlayer {
    private override val server: Server
    private val namedTag: CompoundTag?

    /**
     * 初始化这个`OfflinePlayer`对象。<br></br>
     * Initializes the object `OfflinePlayer`.
     *
     * @param server 这个玩家所在服务器的`Server`对象。<br></br>
     * The server this player is in, as a `Server` object.
     * @param uuid   这个玩家的UUID。<br></br>
     * UUID of this player.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    constructor(server: Server, uuid: UUID?) : this(server, uuid, null) {}
    constructor(server: Server, name: String?) : this(server, null, name) {}

    @get:Override
    override val isOnline: Boolean
        get() = player != null

    @get:Override
    override val name: String?
        get() = if (namedTag != null && namedTag.contains("NameTag")) {
            namedTag.getString("NameTag")
        } else null

    @get:Override
    override val uniqueId: UUID?
        get() {
            if (namedTag != null) {
                val least: Long = namedTag.getLong("UUIDLeast")
                val most: Long = namedTag.getLong("UUIDMost")
                if (least != 0L && most != 0L) {
                    return UUID(most, least)
                }
            }
            return null
        }

    fun getServer(): Server {
        return server
    }

    @get:Override
    @set:Override
    var isOp: Boolean
        get() = server.isOp(name.toLowerCase())
        set(value) {
            if (value == isOp) {
                return
            }
            if (value) {
                server.addOp(name.toLowerCase())
            } else {
                server.removeOp(name.toLowerCase())
            }
        }

    @get:Override
    @set:Override
    override var isBanned: Boolean
        get() = server.getNameBans().isBanned(name)
        set(value) {
            if (value) {
                server.getNameBans().addBan(name, null, null, null)
            } else {
                server.getNameBans().remove(name)
            }
        }

    @get:Override
    @set:Override
    override var isWhitelisted: Boolean
        get() = server.isWhitelisted(name.toLowerCase())
        set(value) {
            if (value) {
                server.addWhitelist(name.toLowerCase())
            } else {
                server.removeWhitelist(name.toLowerCase())
            }
        }

    @get:Override
    override val player: cn.nukkit.Player?
        get() = server.getPlayerExact(name)

    @get:Override
    override val firstPlayed: Long?
        get() = if (namedTag != null) namedTag.getLong("firstPlayed") else null

    @get:Override
    override val lastPlayed: Long?
        get() = if (namedTag != null) namedTag.getLong("lastPlayed") else null

    @Override
    override fun hasPlayedBefore(): Boolean {
        return namedTag != null
    }

    fun setMetadata(metadataKey: String?, newMetadataValue: MetadataValue?) {
        server.getPlayerMetadata().setMetadata(this, metadataKey, newMetadataValue)
    }

    fun getMetadata(metadataKey: String?): List<MetadataValue> {
        return server.getPlayerMetadata().getMetadata(this, metadataKey)
    }

    fun hasMetadata(metadataKey: String?): Boolean {
        return server.getPlayerMetadata().hasMetadata(this, metadataKey)
    }

    fun removeMetadata(metadataKey: String?, owningPlugin: Plugin?) {
        server.getPlayerMetadata().removeMetadata(this, metadataKey, owningPlugin)
    }

    init {
        this.server = server
        var nbt: CompoundTag?
        nbt = if (uuid != null) {
            this.server.getOfflinePlayerData(uuid, false)
        } else if (name != null) {
            this.server.getOfflinePlayerData(name, false)
        } else {
            throw IllegalArgumentException("Name and UUID cannot both be null")
        }
        if (nbt == null) {
            nbt = CompoundTag()
        }
        namedTag = nbt
        if (uuid != null) {
            namedTag.putLong("UUIDMost", uuid.getMostSignificantBits())
            namedTag.putLong("UUIDLeast", uuid.getLeastSignificantBits())
        } else {
            namedTag.putString("NameTag", name)
        }
    }
}