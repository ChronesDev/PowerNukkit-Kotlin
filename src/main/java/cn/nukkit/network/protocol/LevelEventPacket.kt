package cn.nukkit.network.protocol

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class LevelEventPacket : DataPacket() {
    var evid = 0
    var x = 0f
    var y = 0f
    var z = 0f
    var data = 0

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        evid = this.getVarInt()
        val v: Vector3f = this.getVector3f()
        x = v.x
        y = v.y
        z = v.z
        data = this.getVarInt()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putVarInt(evid)
        this.putVector3f(x, y, z)
        this.putVarInt(data)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.LEVEL_EVENT_PACKET
        const val EVENT_SOUND_CLICK = 1000
        const val EVENT_SOUND_CLICK_FAIL = 1001
        const val EVENT_SOUND_SHOOT = 1002
        const val EVENT_SOUND_DOOR = 1003
        const val EVENT_SOUND_FIZZ = 1004
        const val EVENT_SOUND_TNT = 1005
        const val EVENT_SOUND_GHAST = 1007
        const val EVENT_SOUND_BLAZE_SHOOT = 1008
        const val EVENT_SOUND_GHAST_SHOOT = 1009
        const val EVENT_SOUND_DOOR_BUMP = 1010
        const val EVENT_SOUND_DOOR_CRASH = 1012
        const val EVENT_SOUND_ENDERMAN_TELEPORT = 1018
        const val EVENT_SOUND_ANVIL_BREAK = 1020
        const val EVENT_SOUND_ANVIL_USE = 1021
        const val EVENT_SOUND_ANVIL_FALL = 1022
        const val EVENT_SOUND_ITEM_DROP = 1030
        const val EVENT_SOUND_ITEM_THROWN = 1031
        const val EVENT_SOUND_PORTAL = 1032
        const val EVENT_SOUND_ITEM_FRAME_ITEM_ADDED = 1040
        const val EVENT_SOUND_ITEM_FRAME_PLACED = 1041
        const val EVENT_SOUND_ITEM_FRAME_REMOVED = 1042
        const val EVENT_SOUND_ITEM_FRAME_ITEM_REMOVED = 1043
        const val EVENT_SOUND_ITEM_FRAME_ITEM_ROTATED = 1044
        const val EVENT_SOUND_CAMERA_TAKE_PICTURE = 1050
        const val EVENT_SOUND_EXPERIENCE_ORB = 1051
        const val EVENT_SOUND_TOTEM = 1052
        const val EVENT_SOUND_ARMOR_STAND_BREAK = 1060
        const val EVENT_SOUND_ARMOR_STAND_HIT = 1061
        const val EVENT_SOUND_ARMOR_STAND_FALL = 1062
        const val EVENT_SOUND_ARMOR_STAND_PLACE = 1063

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val EVENT_SOUND_POINTED_DRIPSTONE_LAND = 1064

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val EVENT_SOUND_DYE_USED = 1065

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val EVENT_SOUND_INK_SACE_USED = 1066
        const val EVENT_GUARDIAN_CURSE = 2006
        const val EVENT_PARTICLE_BLOCK_FORCE_FIELD = 2008
        const val EVENT_PARTICLE_PROJECTILE_HIT = 2009
        const val EVENT_PARTICLE_DRAGON_EGG_TELEPORT = 2010
        const val EVENT_PARTICLE_ENDERMAN_TELEPORT = 2013
        const val EVENT_PARTICLE_PUNCH_BLOCK = 2014

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val EVENT_PARTICLE_VIBRATION_SIGNAL = 2027

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val EVENT_PARTICLE_DRIPSTONE_DRIP = 2028

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val EVENT_PARTICLE_FIZZ_EFFECT = 2029

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val EVENT_PARTICLE_WAX_ON = 2030

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val EVENT_PARTICLE_WAX_OFF = 2031

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val EVENT_PARTICLE_SCRAPE = 2032

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val EVENT_PARTICLE_ELECTRIC_SPARK = 2033
        const val EVENT_SOUND_BUTTON_CLICK = 3500
        const val EVENT_SOUND_EXPLODE = 3501
        const val EVENT_CAULDRON_DYE_ARMOR = 3502
        const val EVENT_CAULDRON_CLEAN_ARMOR = 3503
        const val EVENT_CAULDRON_FILL_POTION = 3504
        const val EVENT_CAULDRON_TAKE_POTION = 3505
        const val EVENT_SOUND_SPLASH = 3506
        const val EVENT_CAULDRON_TAKE_WATER = 3507
        const val EVENT_CAULDRON_ADD_DYE = 3508
        const val EVENT_CAULDRON_CLEAN_BANNER = 3509
        const val EVENT_PARTICLE_SHOOT = 2000
        const val EVENT_PARTICLE_DESTROY = 2001
        const val EVENT_PARTICLE_SPLASH = 2002
        const val EVENT_PARTICLE_EYE_DESPAWN = 2003
        const val EVENT_PARTICLE_SPAWN = 2004
        const val EVENT_PARTICLE_BONEMEAL = 2005
        const val EVENT_START_RAIN = 3001
        const val EVENT_START_THUNDER = 3002
        const val EVENT_STOP_RAIN = 3003
        const val EVENT_STOP_THUNDER = 3004
        const val EVENT_SOUND_CAULDRON = 3501
        const val EVENT_SOUND_CAULDRON_DYE_ARMOR = 3502
        const val EVENT_SOUND_CAULDRON_FILL_POTION = 3504
        const val EVENT_SOUND_CAULDRON_FILL_WATER = 3506

        @Since("1.5.0.0-PN")
        @PowerNukkitOnly
        val EVENT_SOUND_CAULDRON_FILL_POWDER_SNOW = 3514

        @Since("1.5.0.0-PN")
        @PowerNukkitOnly
        val EVENT_SOUND_CAULDRON_TAKE_POWDER_SNOW = 3515
        const val EVENT_BLOCK_START_BREAK = 3600
        const val EVENT_BLOCK_STOP_BREAK = 3601
        const val EVENT_SET_DATA = 4000
        const val EVENT_PLAYERS_SLEEPING = 9800
        const val EVENT_ADD_PARTICLE_MASK = 0x4000
    }
}