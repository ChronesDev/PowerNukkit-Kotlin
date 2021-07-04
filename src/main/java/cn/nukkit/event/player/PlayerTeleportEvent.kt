package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerTeleportEvent private constructor(player: Player) : PlayerEvent(), Cancellable {
    var cause: TeleportCause? = null
        private set
    private var from: Location? = null
    private var to: Location? = null

    constructor(player: Player, from: Location?, to: Location?, cause: TeleportCause?) : this(player) {
        this.from = from
        this.to = to
        this.cause = cause
    }

    constructor(player: Player, from: Vector3, to: Vector3, cause: TeleportCause?) : this(player) {
        this.from = vectorToLocation(player.getLevel(), from)
        this.from = vectorToLocation(player.getLevel(), to)
        this.cause = cause
    }

    fun getFrom(): Location? {
        return from
    }

    fun getTo(): Location? {
        return to
    }

    private fun vectorToLocation(baseLevel: Level, vector: Vector3): Location {
        if (vector is Location) return vector as Location
        return if (vector is Position) (vector as Position).getLocation() else Location(vector.getX(), vector.getY(), vector.getZ(), 0, 0, baseLevel)
    }

    enum class TeleportCause {
        COMMAND,  // For Nukkit tp command only
        PLUGIN,  // Every plugin
        NETHER_PORTAL,  // Teleport using Nether portal
        ENDER_PEARL,  // Teleport by ender pearl
        CHORUS_FRUIT,  // Teleport by chorus fruit
        UNKNOWN // Unknown cause
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
    }
}