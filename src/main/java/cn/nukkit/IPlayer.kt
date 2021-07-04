package cn.nukkit

import cn.nukkit.metadata.Metadatable

/**
 * 用来描述一个玩家和获得这个玩家相应信息的接口。<br></br>
 * An interface to describe a player and get its information.
 *
 *
 * 这个玩家可以在线，也可以是不在线。<br></br>
 * This player can be online or offline.
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see cn.nukkit.Player
 *
 * @see cn.nukkit.OfflinePlayer
 *
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
interface IPlayer : ServerOperator, Metadatable {
    /**
     * 返回这个玩家是否在线。<br></br>
     * Returns if this player is online.
     *
     * @return 这个玩家是否在线。<br></br>If this player is online.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    val isOnline: Boolean

    /**
     * 返回这个玩家的名称。<br></br>
     * Returns the name of this player.
     *
     *
     * 如果是在线的玩家，这个函数只会返回登录名字。如果要返回显示的名字，参见[cn.nukkit.Player.getDisplayName]<br></br>
     * Notice that this will only return its login name. If you need its display name, turn to
     * [cn.nukkit.Player.getDisplayName]
     *
     * @return 这个玩家的名称。<br></br>The name of this player.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    val name: String?
    val uniqueId: UUID?
    /**
     * 返回这个玩家是否被封禁(ban)。<br></br>
     * Returns if this player is banned.
     *
     * @return 这个玩家的名称。<br></br>The name of this player.
     * @see .setBanned
     *
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    /**
     * 设置这个玩家是否被封禁(ban)。<br></br>
     * Sets this player to be banned or to be pardoned.
     *
     * @param value 如果为`true`，封禁这个玩家。如果为`false`，解封这个玩家。<br></br>
     * `true` for ban and `false` for pardon.
     * @see .isBanned
     *
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    var isBanned: Boolean
    /**
     * 返回这个玩家是否已加入白名单。<br></br>
     * Returns if this player is pardoned by whitelist.
     *
     * @return 这个玩家是否已加入白名单。<br></br>If this player is pardoned by whitelist.
     * @see cn.nukkit.Server.isWhitelisted
     *
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    /**
     * 把这个玩家加入白名单，或者取消这个玩家的白名单。<br></br>
     * Adds this player to the white list, or removes it from the whitelist.
     *
     * @param value 如果为`true`，把玩家加入白名单。如果为`false`，取消这个玩家的白名单。<br></br>
     * `true` for add and `false` for remove.
     * @see .isWhitelisted
     *
     * @see cn.nukkit.Server.addWhitelist
     *
     * @see cn.nukkit.Server.removeWhitelist
     *
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    var isWhitelisted: Boolean

    /**
     * 得到这个接口的`Player`对象。<br></br>
     * Returns a `Player` object for this interface.
     *
     * @return 这个接口的 `Player`对象。<br></br>a `Player` object for this interface.
     * @see cn.nukkit.Server.getPlayerExact
     *
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    val player: cn.nukkit.Player?

    /**
     * 返回玩家所在的服务器。<br></br>
     * Returns the server carrying this player.
     *
     * @return 玩家所在的服务器。<br></br>the server carrying this player.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    val server: cn.nukkit.Server?

    /**
     * 得到这个玩家第一次游戏的时间。<br></br>
     * Returns the time this player first played in this server.
     *
     * @return Unix时间（以秒为单位。<br></br>Unix time in seconds.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    val firstPlayed: Long?

    /**
     * 得到这个玩家上次加入游戏的时间。<br></br>
     * Returns the time this player last joined in this server.
     *
     * @return Unix时间（以秒为单位。<br></br>Unix time in seconds.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    val lastPlayed: Long?

    /**
     * 返回这个玩家以前是否来过服务器。<br></br>
     * Returns if this player has played in this server before.
     *
     *
     * 如果想得到这个玩家是不是第一次玩，可以使用：<br></br>
     * If you want to know if this player is the first time playing in this server, you can use:<br></br>
     *
     * <pre>if(!player.hasPlayerBefore()) {...}</pre>
     *
     * @return 这个玩家以前是不是玩过游戏。<br></br>If this player has played in this server before.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    fun hasPlayedBefore(): Boolean
}