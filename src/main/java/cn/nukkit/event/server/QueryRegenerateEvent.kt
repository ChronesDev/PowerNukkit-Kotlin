package cn.nukkit.event.server

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class QueryRegenerateEvent(server: Server, var timeout: Int) : ServerEvent() {
    var serverName: String
    private var listPlugins: Boolean
    private var plugins: Array<Plugin>
    private var players: Array<Player>
    private val gameType: String
    private val version: String
    private val server_engine: String
    var world: String
    var playerCount: Int
    var maxPlayerCount: Int
    private val whitelist: String
    private val port: Int
    private val ip: String
    var extraData: Map<String, String> = HashMap()

    constructor(server: Server) : this(server, 5) {}

    fun canListPlugins(): Boolean {
        return listPlugins
    }

    fun setListPlugins(listPlugins: Boolean) {
        this.listPlugins = listPlugins
    }

    fun getPlugins(): Array<Plugin> {
        return plugins
    }

    fun setPlugins(plugins: Array<Plugin>) {
        this.plugins = plugins
    }

    var playerList: Array<Any>
        get() = players
        set(players) {
            this.players = players
        }

    fun getLongQuery(buffer: ByteArray?): ByteArray {
        var buffer = buffer
        if (buffer == null) buffer = ByteArray(Character.MAX_VALUE)
        val query = FastByteArrayOutputStream(buffer)
        try {
            var plist = StringBuilder(server_engine)
            if (plugins.size > 0 && listPlugins) {
                plist.append(":")
                for (p in plugins) {
                    val d: PluginDescription = p.getDescription()
                    plist.append(" ").append(d.getName().replace(";", "").replace(":", "").replace(" ", "_")).append(" ").append(d.getVersion().replace(";", "").replace(":", "").replace(" ", "_")).append(";")
                }
                plist = StringBuilder(plist.substring(0, plist.length() - 2))
            }
            query.write("splitnum".getBytes())
            query.write(0x00.toByte())
            query.write(128.toByte())
            query.write(0x00.toByte())
            val KVdata: LinkedHashMap<String, String> = LinkedHashMap()
            KVdata.put("hostname", serverName)
            KVdata.put("gametype", gameType)
            KVdata.put("game_id", GAME_ID)
            KVdata.put("version", version)
            KVdata.put("server_engine", server_engine)
            KVdata.put("plugins", plist.toString())
            KVdata.put("map", world)
            KVdata.put("numplayers", String.valueOf(playerCount))
            KVdata.put("maxplayers", String.valueOf(maxPlayerCount))
            KVdata.put("whitelist", whitelist)
            KVdata.put("hostip", ip)
            KVdata.put("hostport", String.valueOf(port))
            for (entry in KVdata.entrySet()) {
                query.write(entry.getKey().getBytes(StandardCharsets.UTF_8))
                query.write(0x00.toByte())
                query.write(entry.getValue().getBytes(StandardCharsets.UTF_8))
                query.write(0x00.toByte())
            }
            query.write(byteArrayOf(0x00, 0x01))
            query.write("player_".getBytes())
            query.write(byteArrayOf(0x00, 0x00))
            for (player in players) {
                query.write(player.getName().getBytes(StandardCharsets.UTF_8))
                query.write(0x00.toByte())
            }
            query.write(0x00.toByte())
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return query.toByteArray()
    }

    fun getShortQuery(buffer: ByteArray?): ByteArray {
        var buffer = buffer
        if (buffer == null) buffer = ByteArray(Character.MAX_VALUE)
        val query = FastByteArrayOutputStream(buffer)
        try {
            query.write(serverName.getBytes(StandardCharsets.UTF_8))
            query.write(0x00.toByte())
            query.write(gameType.getBytes(StandardCharsets.UTF_8))
            query.write(0x00.toByte())
            query.write(world.getBytes(StandardCharsets.UTF_8))
            query.write(0x00.toByte())
            query.write(String.valueOf(playerCount).getBytes(StandardCharsets.UTF_8))
            query.write(0x00.toByte())
            query.write(String.valueOf(maxPlayerCount).getBytes(StandardCharsets.UTF_8))
            query.write(0x00.toByte())
            query.write(Binary.writeLShort(port))
            query.write(ip.getBytes(StandardCharsets.UTF_8))
            query.write(0x00.toByte())
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return query.toByteArray()
    }

    companion object {
        //alot todo
        val handlers: HandlerList = HandlerList()
        private const val GAME_ID = "MINECRAFTPE"
    }

    init {
        serverName = server.getMotd()
        listPlugins = server.getConfig("settings.query-plugins", true)
        plugins = server.getPluginManager().getPlugins().values().toArray(Plugin.EMPTY_ARRAY)
        players = server.getOnlinePlayers().values().toArray(Player.EMPTY_ARRAY)
        gameType = if (server.getGamemode() and 0x01 === 0) "SMP" else "CMP"
        version = server.getVersion()
        server_engine = server.getName().toString() + " " + server.getNukkitVersion() + " (" + server.getGitCommit() + ")"
        world = if (server.getDefaultLevel() == null) "unknown" else server.getDefaultLevel().getName()
        playerCount = players.size
        maxPlayerCount = server.getMaxPlayers()
        whitelist = if (server.hasWhitelist()) "on" else "off"
        port = server.getPort()
        ip = server.getIp()
    }
}