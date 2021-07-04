package cn.nukkit

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author MagicDroidX (Nukkit Project)
 */
class AdventureSettings(player: Player) : Cloneable {
    private val values: Map<Type, Boolean> = EnumMap(Type::class.java)
    private var player: Player
    fun clone(newPlayer: Player): AdventureSettings? {
        return try {
            val settings = super.clone() as AdventureSettings
            settings.player = newPlayer
            settings
        } catch (e: CloneNotSupportedException) {
            null
        }
    }

    operator fun set(type: Type?, value: Boolean): AdventureSettings {
        values.put(type, value)
        return this
    }

    operator fun get(type: Type): Boolean {
        val value = values[type]
        return value ?: type.defaultValue
    }

    @PowerNukkitDifference(info = "Players in spectator mode will be flagged as member even if they are OP due to a client-side limitation", since = "1.3.1.2-PN")
    fun update() {
        val pk = AdventureSettingsPacket()
        for (t in Type.values()) {
            pk.setFlag(t.id, get(t))
        }
        pk.commandPermission = if (player.isOp()) AdventureSettingsPacket.PERMISSION_OPERATOR else AdventureSettingsPacket.PERMISSION_NORMAL
        pk.playerPermission = if (player.isOp() && !player.isSpectator()) Player.PERMISSION_OPERATOR else Player.PERMISSION_MEMBER
        pk.entityUniqueId = player.getId()
        Server.broadcastPacket(player.getViewers().values(), pk)
        player.dataPacket(pk)
        player.resetInAirTicks()
    }

    enum class Type(val id: Int, val defaultValue: Boolean) {
        WORLD_IMMUTABLE(AdventureSettingsPacket.WORLD_IMMUTABLE, false), AUTO_JUMP(AdventureSettingsPacket.AUTO_JUMP, true), ALLOW_FLIGHT(AdventureSettingsPacket.ALLOW_FLIGHT, false), NO_CLIP(AdventureSettingsPacket.NO_CLIP, false), WORLD_BUILDER(AdventureSettingsPacket.WORLD_BUILDER, true), FLYING(AdventureSettingsPacket.FLYING, false), MUTED(AdventureSettingsPacket.MUTED, false), BUILD_AND_MINE(AdventureSettingsPacket.BUILD_AND_MINE, true), DOORS_AND_SWITCHED(AdventureSettingsPacket.DOORS_AND_SWITCHES, true), OPEN_CONTAINERS(AdventureSettingsPacket.OPEN_CONTAINERS, true), ATTACK_PLAYERS(AdventureSettingsPacket.ATTACK_PLAYERS, true), ATTACK_MOBS(AdventureSettingsPacket.ATTACK_MOBS, true), OPERATOR(AdventureSettingsPacket.OPERATOR, false), TELEPORT(AdventureSettingsPacket.TELEPORT, false);

    }

    companion object {
        const val PERMISSION_NORMAL = 0
        const val PERMISSION_OPERATOR = 1
        const val PERMISSION_HOST = 2
        const val PERMISSION_AUTOMATION = 3
        const val PERMISSION_ADMIN = 4
    }

    init {
        this.player = player
    }
}