package cn.nukkit.network.protocol

import cn.nukkit.api.DeprecationDetails

@ToString
class LevelSoundEventPacket : DataPacket() {
    var sound = 0
    var x = 0f
    var y = 0f
    var z = 0f
    var extraData = -1
    var entityIdentifier: String? = null
    var isBabyMob = false
    var isGlobal = false

    @Override
    override fun decode() {
        sound = this.getUnsignedVarInt() as Int
        val v: Vector3f = this.getVector3f()
        x = v.x
        y = v.y
        z = v.z
        extraData = this.getVarInt()
        entityIdentifier = this.getString()
        isBabyMob = this.getBoolean()
        isGlobal = this.getBoolean()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putUnsignedVarInt(sound)
        this.putVector3f(x, y, z)
        this.putVarInt(extraData)
        this.putString(entityIdentifier)
        this.putBoolean(isBabyMob)
        this.putBoolean(isGlobal)
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.LEVEL_SOUND_EVENT_PACKET
        const val SOUND_ITEM_USE_ON = 0
        const val SOUND_HIT = 1
        const val SOUND_STEP = 2
        const val SOUND_FLY = 3
        const val SOUND_JUMP = 4
        const val SOUND_BREAK = 5
        const val SOUND_PLACE = 6
        const val SOUND_HEAVY_STEP = 7
        const val SOUND_GALLOP = 8
        const val SOUND_FALL = 9
        const val SOUND_AMBIENT = 10
        const val SOUND_AMBIENT_BABY = 11
        const val SOUND_AMBIENT_IN_WATER = 12
        const val SOUND_BREATHE = 13
        const val SOUND_DEATH = 14
        const val SOUND_DEATH_IN_WATER = 15
        const val SOUND_DEATH_TO_ZOMBIE = 16
        const val SOUND_HURT = 17
        const val SOUND_HURT_IN_WATER = 18
        const val SOUND_MAD = 19
        const val SOUND_BOOST = 20
        const val SOUND_BOW = 21
        const val SOUND_SQUISH_BIG = 22
        const val SOUND_SQUISH_SMALL = 23
        const val SOUND_FALL_BIG = 24
        const val SOUND_FALL_SMALL = 25
        const val SOUND_SPLASH = 26
        const val SOUND_FIZZ = 27
        const val SOUND_FLAP = 28
        const val SOUND_SWIM = 29
        const val SOUND_DRINK = 30
        const val SOUND_EAT = 31
        const val SOUND_TAKEOFF = 32
        const val SOUND_SHAKE = 33
        const val SOUND_PLOP = 34
        const val SOUND_LAND = 35
        const val SOUND_SADDLE = 36
        const val SOUND_ARMOR = 37
        const val SOUND_MOB_ARMOR_STAND_PLACE = 38
        const val SOUND_ADD_CHEST = 39
        const val SOUND_THROW = 40
        const val SOUND_ATTACK = 41
        const val SOUND_ATTACK_NODAMAGE = 42
        const val SOUND_ATTACK_STRONG = 43
        const val SOUND_WARN = 44
        const val SOUND_SHEAR = 45
        const val SOUND_MILK = 46
        const val SOUND_THUNDER = 47
        const val SOUND_EXPLODE = 48
        const val SOUND_FIRE = 49
        const val SOUND_IGNITE = 50
        const val SOUND_FUSE = 51
        const val SOUND_STARE = 52
        const val SOUND_SPAWN = 53
        const val SOUND_SHOOT = 54
        const val SOUND_BREAK_BLOCK = 55
        const val SOUND_LAUNCH = 56
        const val SOUND_BLAST = 57
        const val SOUND_LARGE_BLAST = 58
        const val SOUND_TWINKLE = 59
        const val SOUND_REMEDY = 60
        const val SOUND_UNFECT = 61
        const val SOUND_LEVELUP = 62
        const val SOUND_BOW_HIT = 63
        const val SOUND_BULLET_HIT = 64
        const val SOUND_EXTINGUISH_FIRE = 65
        const val SOUND_ITEM_FIZZ = 66
        const val SOUND_CHEST_OPEN = 67
        const val SOUND_CHEST_CLOSED = 68
        const val SOUND_SHULKERBOX_OPEN = 69
        const val SOUND_SHULKERBOX_CLOSED = 70
        const val SOUND_ENDERCHEST_OPEN = 71
        const val SOUND_ENDERCHEST_CLOSED = 72
        const val SOUND_POWER_ON = 73
        const val SOUND_POWER_OFF = 74
        const val SOUND_ATTACH = 75
        const val SOUND_DETACH = 76
        const val SOUND_DENY = 77
        const val SOUND_TRIPOD = 78
        const val SOUND_POP = 79
        const val SOUND_DROP_SLOT = 80
        const val SOUND_NOTE = 81
        const val SOUND_THORNS = 82
        const val SOUND_PISTON_IN = 83
        const val SOUND_PISTON_OUT = 84
        const val SOUND_PORTAL = 85
        const val SOUND_WATER = 86
        const val SOUND_LAVA_POP = 87
        const val SOUND_LAVA = 88
        const val SOUND_BURP = 89
        const val SOUND_BUCKET_FILL_WATER = 90
        const val SOUND_BUCKET_FILL_LAVA = 91
        const val SOUND_BUCKET_EMPTY_WATER = 92
        const val SOUND_BUCKET_EMPTY_LAVA = 93
        const val SOUND_ARMOR_EQUIP_CHAIN = 94
        const val SOUND_ARMOR_EQUIP_DIAMOND = 95
        const val SOUND_ARMOR_EQUIP_GENERIC = 96
        const val SOUND_ARMOR_EQUIP_GOLD = 97
        const val SOUND_ARMOR_EQUIP_IRON = 98
        const val SOUND_ARMOR_EQUIP_LEATHER = 99
        const val SOUND_ARMOR_EQUIP_ELYTRA = 100
        const val SOUND_RECORD_13 = 101
        const val SOUND_RECORD_CAT = 102
        const val SOUND_RECORD_BLOCKS = 103
        const val SOUND_RECORD_CHIRP = 104
        const val SOUND_RECORD_FAR = 105
        const val SOUND_RECORD_MALL = 106
        const val SOUND_RECORD_MELLOHI = 107
        const val SOUND_RECORD_STAL = 108
        const val SOUND_RECORD_STRAD = 109
        const val SOUND_RECORD_WARD = 110
        const val SOUND_RECORD_11 = 111
        const val SOUND_RECORD_WAIT = 112
        const val SOUND_STOP_RECORD = 113 //Not really a sound
        const val SOUND_GUARDIAN_FLOP = 114
        const val SOUND_ELDERGUARDIAN_CURSE = 115
        const val SOUND_MOB_WARNING = 116
        const val SOUND_MOB_WARNING_BABY = 117
        const val SOUND_TELEPORT = 118
        const val SOUND_SHULKER_OPEN = 119
        const val SOUND_SHULKER_CLOSE = 120
        const val SOUND_HAGGLE = 121
        const val SOUND_HAGGLE_YES = 122
        const val SOUND_HAGGLE_NO = 123
        const val SOUND_HAGGLE_IDLE = 124
        const val SOUND_CHORUSGROW = 125
        const val SOUND_CHORUSDEATH = 126
        const val SOUND_GLASS = 127
        const val SOUND_POTION_BREWED = 128
        const val SOUND_CAST_SPELL = 129
        const val SOUND_PREPARE_ATTACK = 130
        const val SOUND_PREPARE_SUMMON = 131
        const val SOUND_PREPARE_WOLOLO = 132
        const val SOUND_FANG = 133
        const val SOUND_CHARGE = 134
        const val SOUND_CAMERA_TAKE_PICTURE = 135
        const val SOUND_LEASHKNOT_PLACE = 136
        const val SOUND_LEASHKNOT_BREAK = 137
        const val SOUND_GROWL = 138
        const val SOUND_WHINE = 139
        const val SOUND_PANT = 140
        const val SOUND_PURR = 141
        const val SOUND_PURREOW = 142
        const val SOUND_DEATH_MIN_VOLUME = 143
        const val SOUND_DEATH_MID_VOLUME = 144
        const val SOUND_IMITATE_BLAZE = 145
        const val SOUND_IMITATE_CAVE_SPIDER = 146
        const val SOUND_IMITATE_CREEPER = 147
        const val SOUND_IMITATE_ELDER_GUARDIAN = 148
        const val SOUND_IMITATE_ENDER_DRAGON = 149
        const val SOUND_IMITATE_ENDERMAN = 150
        const val SOUND_IMITATE_EVOCATION_ILLAGER = 152
        const val SOUND_IMITATE_GHAST = 153
        const val SOUND_IMITATE_HUSK = 154
        const val SOUND_IMITATE_ILLUSION_ILLAGER = 155
        const val SOUND_IMITATE_MAGMA_CUBE = 156
        const val SOUND_IMITATE_POLAR_BEAR = 157
        const val SOUND_IMITATE_SHULKER = 158
        const val SOUND_IMITATE_SILVERFISH = 159
        const val SOUND_IMITATE_SKELETON = 160
        const val SOUND_IMITATE_SLIME = 161
        const val SOUND_IMITATE_SPIDER = 162
        const val SOUND_IMITATE_STRAY = 163
        const val SOUND_IMITATE_VEX = 164
        const val SOUND_IMITATE_VINDICATION_ILLAGER = 165
        const val SOUND_IMITATE_WITCH = 166
        const val SOUND_IMITATE_WITHER = 167
        const val SOUND_IMITATE_WITHER_SKELETON = 168
        const val SOUND_IMITATE_WOLF = 169
        const val SOUND_IMITATE_ZOMBIE = 170
        const val SOUND_IMITATE_ZOMBIE_PIGMAN = 171
        const val SOUND_IMITATE_ZOMBIE_VILLAGER = 172
        const val SOUND_BLOCK_END_PORTAL_FRAME_FILL = 173
        const val SOUND_BLOCK_END_PORTAL_SPAWN = 174
        const val SOUND_RANDOM_ANVIL_USE = 175
        const val SOUND_BOTTLE_DRAGONBREATH = 176
        const val SOUND_PORTAL_TRAVEL = 177
        const val SOUND_ITEM_TRIDENT_HIT = 178
        const val SOUND_ITEM_TRIDENT_RETURN = 179
        const val SOUND_ITEM_TRIDENT_RIPTIDE_1 = 180
        const val SOUND_ITEM_TRIDENT_RIPTIDE_2 = 181
        const val SOUND_ITEM_TRIDENT_RIPTIDE_3 = 182
        const val SOUND_ITEM_TRIDENT_THROW = 183
        const val SOUND_ITEM_TRIDENT_THUNDER = 184
        const val SOUND_ITEM_TRIDENT_HIT_GROUND = 185
        const val SOUND_DEFAULT = 186
        const val SOUND_ELEMCONSTRUCT_OPEN = 188
        const val SOUND_ICEBOMB_HIT = 189
        const val SOUND_BALLOONPOP = 190
        const val SOUND_LT_REACTION_ICEBOMB = 191
        const val SOUND_LT_REACTION_BLEACH = 192
        const val SOUND_LT_REACTION_EPASTE = 193
        const val SOUND_LT_REACTION_EPASTE2 = 194
        const val SOUND_LT_REACTION_FERTILIZER = 199
        const val SOUND_LT_REACTION_FIREBALL = 200
        const val SOUND_LT_REACTION_MGSALT = 201
        const val SOUND_LT_REACTION_MISCFIRE = 202
        const val SOUND_LT_REACTION_FIRE = 203
        const val SOUND_LT_REACTION_MISCEXPLOSION = 204
        const val SOUND_LT_REACTION_MISCMYSTICAL = 205
        const val SOUND_LT_REACTION_MISCMYSTICAL2 = 206
        const val SOUND_LT_REACTION_PRODUCT = 207
        const val SOUND_SPARKLER_USE = 208
        const val SOUND_GLOWSTICK_USE = 209
        const val SOUND_SPARKLER_ACTIVE = 210
        const val SOUND_CONVERT_TO_DROWNED = 211
        const val SOUND_BUCKET_FILL_FISH = 212
        const val SOUND_BUCKET_EMPTY_FISH = 213
        const val SOUND_BUBBLE_UP = 214
        const val SOUND_BUBBLE_DOWN = 215
        const val SOUND_BUBBLE_POP = 216
        const val SOUND_BUBBLE_UPINSIDE = 217
        const val SOUND_BUBBLE_DOWNINSIDE = 218
        const val SOUND_HURT_BABY = 219
        const val SOUND_DEATH_BABY = 220
        const val SOUND_STEP_BABY = 221
        const val SOUND_BORN = 223
        const val SOUND_BLOCK_TURTLE_EGG_BREAK = 224
        const val SOUND_BLOCK_TURTLE_EGG_CRACK = 225
        const val SOUND_BLOCK_TURTLE_EGG_HATCH = 226
        const val SOUND_BLOCK_TURTLE_EGG_ATTACK = 228
        const val SOUND_BEACON_ACTIVATE = 229
        const val SOUND_BEACON_AMBIENT = 230
        const val SOUND_BEACON_DEACTIVATE = 231
        const val SOUND_BEACON_POWER = 232
        const val SOUND_CONDUIT_ACTIVATE = 233
        const val SOUND_CONDUIT_AMBIENT = 234
        const val SOUND_CONDUIT_ATTACK = 235
        const val SOUND_CONDUIT_DEACTIVATE = 236
        const val SOUND_CONDUIT_SHORT = 237
        const val SOUND_SWOOP = 238
        const val SOUND_BLOCK_BAMBOO_SAPLING_PLACE = 239
        const val SOUND_PRESNEEZE = 240
        const val SOUND_SNEEZE = 241
        const val SOUND_AMBIENT_TAME = 242
        const val SOUND_SCARED = 243
        const val SOUND_BLOCK_SCAFFOLDING_CLIMB = 244
        const val SOUND_CROSSBOW_LOADING_START = 245
        const val SOUND_CROSSBOW_LOADING_MIDDLE = 246
        const val SOUND_CROSSBOW_LOADING_END = 247
        const val SOUND_CROSSBOW_SHOOT = 248
        const val SOUND_CROSSBOW_QUICK_CHARGE_START = 249
        const val SOUND_CROSSBOW_QUICK_CHARGE_MIDDLE = 250
        const val SOUND_CROSSBOW_QUICK_CHARGE_END = 251
        const val SOUND_AMBIENT_AGGRESSIVE = 252
        const val SOUND_AMBIENT_WORRIED = 253
        const val SOUND_CANT_BREED = 254
        const val SOUND_UNDEFINED = 255

        @Since("1.4.0.0-PN")
        val SOUND_RECORD_PIGSTEP = 314
    }
}