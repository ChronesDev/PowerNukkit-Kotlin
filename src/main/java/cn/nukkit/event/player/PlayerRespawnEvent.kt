package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerRespawnEvent(player: Player?, position: Position?, firstSpawn: Boolean) : PlayerEvent() {
    private var position: Position?
    private var spawnBlock: Position? = null
    private var originalSpawnPosition: Position? = null

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isRespawnBlockAvailable = false
    val isFirstSpawn: Boolean

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isKeepRespawnBlockPosition = false

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isKeepRespawnPosition = false

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isSendInvalidRespawnBlockMessage = true

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isConsumeCharge = true

    constructor(player: Player?, position: Position?) : this(player, position, false) {}

    var respawnPosition: Position?
        get() = position
        set(position) {
            this.position = position
        }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var respawnBlockPosition: Position?
        get() = spawnBlock
        set(spawnBlock) {
            this.spawnBlock = spawnBlock
        }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var originalRespawnPosition: Position?
        get() = originalSpawnPosition
        set(originalSpawnPosition) {
            this.originalSpawnPosition = originalSpawnPosition
        }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.position = position
        isFirstSpawn = firstSpawn
    }
}