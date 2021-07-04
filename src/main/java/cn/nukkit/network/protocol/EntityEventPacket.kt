package cn.nukkit.network.protocol

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class EntityEventPacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return NETWORK_ID.toByte()
    }

    var eid: Long = 0
    var event = 0
    var data = 0

    @Override
    override fun decode() {
        eid = this.getEntityRuntimeId()
        event = this.getByte()
        data = this.getVarInt()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putEntityRuntimeId(eid)
        this.putByte(event.toByte())
        this.putVarInt(data)
    }

    companion object {
        val NETWORK_ID: Int = ProtocolInfo.ENTITY_EVENT_PACKET.toInt()

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val NONE = 0

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val JUMP = 1
        const val HURT_ANIMATION = 2
        const val DEATH_ANIMATION = 3
        const val ARM_SWING = 4

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val ATTACK_STOP = 5
        const val TAME_FAIL = 6
        const val TAME_SUCCESS = 7
        const val SHAKE_WET = 8
        const val USE_ITEM = 9
        const val EAT_GRASS_ANIMATION = 10
        const val FISH_HOOK_BUBBLE = 11
        const val FISH_HOOK_POSITION = 12
        const val FISH_HOOK_HOOK = 13
        const val FISH_HOOK_TEASE = 14
        const val SQUID_INK_CLOUD = 15
        const val ZOMBIE_VILLAGER_CURE = 16
        const val AMBIENT_SOUND = 17
        const val RESPAWN = 18
        const val IRON_GOLEM_OFFER_FLOWER = 19
        const val IRON_GOLEM_WITHDRAW_FLOWER = 20
        const val LOVE_PARTICLES = 21

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val VILLAGER_ANGRY = 22

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val VILLAGER_HAPPY = 23
        const val WITCH_SPELL_PARTICLES = 24
        const val FIREWORK_EXPLOSION = 25

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val IN_LOVE_HEARTS = 26
        const val SILVERFISH_SPAWN_ANIMATION = 27

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val GUARDIAN_ATTACK_ANIMATION = 28
        const val WITCH_DRINK_POTION = 29
        const val WITCH_THROW_POTION = 30
        const val MINECART_TNT_PRIME_FUSE = 31

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PRIME_CREEPER = 32

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val AIR_SUPPLY = 33
        const val ENCHANT = 34
        const val ELDER_GUARDIAN_CURSE = 35
        const val AGENT_ARM_SWING = 36
        const val ENDER_DRAGON_DEATH = 37
        const val DUST_PARTICLES = 38
        const val ARROW_SHAKE = 39
        const val EATING_ITEM = 57
        const val BABY_ANIMAL_FEED = 60
        const val DEATH_SMOKE_CLOUD = 61
        const val COMPLETE_TRADE = 62
        const val REMOVE_LEASH = 63

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val CARAVAN = 64
        const val CONSUME_TOTEM = 65
        const val PLAYER_CHECK_TREASURE_HUNTER_ACHIEVEMENT = 66
        const val ENTITY_SPAWN = 67
        const val DRAGON_PUKE = 68
        const val MERGE_ITEMS = 69

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val START_SWIMMING = 70

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val BALLOON_POP = 71

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val TREASURE_HUNT = 72

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val SUMMON_AGENT = 73

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val FINISHED_CHARGING_CROSSBOW = 74

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val LANDED_ON_GROUND = 75
    }
}