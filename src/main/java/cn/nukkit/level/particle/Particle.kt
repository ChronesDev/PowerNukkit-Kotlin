package cn.nukkit.level.particle

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class Particle : Vector3 {
    constructor() : super(0, 0, 0) {}
    constructor(x: Double) : super(x, 0, 0) {}
    constructor(x: Double, y: Double) : super(x, y, 0) {}
    constructor(x: Double, y: Double, z: Double) : super(x, y, z) {}

    abstract fun encode(): Array<DataPacket?>?

    companion object {
        val TYPE_BUBBLE: Int = dynamic(1)

        @Since("1.4.0.0-PN")
        val TYPE_BUBBLE_MANUAL: Int = dynamic(2)
        val TYPE_CRITICAL: Int = dynamic(3)
        val TYPE_BLOCK_FORCE_FIELD: Int = dynamic(4)
        val TYPE_SMOKE: Int = dynamic(5)
        val TYPE_EXPLODE: Int = dynamic(6)
        val TYPE_EVAPORATION: Int = dynamic(7)
        val TYPE_FLAME: Int = dynamic(8)
        val TYPE_LAVA: Int = dynamic(9)
        val TYPE_LARGE_SMOKE: Int = dynamic(10)
        val TYPE_REDSTONE: Int = dynamic(11)
        val TYPE_RISING_RED_DUST: Int = dynamic(12)
        val TYPE_ITEM_BREAK: Int = dynamic(13)
        val TYPE_SNOWBALL_POOF: Int = dynamic(14)
        val TYPE_HUGE_EXPLODE: Int = dynamic(15)
        val TYPE_HUGE_EXPLODE_SEED: Int = dynamic(16)
        val TYPE_MOB_FLAME: Int = dynamic(17)
        val TYPE_HEART: Int = dynamic(18)
        val TYPE_TERRAIN: Int = dynamic(19)
        val TYPE_SUSPENDED_TOWN: Int = dynamic(20)
        val TYPE_TOWN_AURA = TYPE_SUSPENDED_TOWN
        val TYPE_PORTAL: Int = dynamic(21)

        // 22 same as 21
        val TYPE_SPLASH: Int = dynamic(23)
        val TYPE_WATER_SPLASH = TYPE_SPLASH

        @Since("1.4.0.0-PN")
        val TYPE_WATER_SPLASH_MANUAL: Int = dynamic(24)
        val TYPE_WATER_WAKE: Int = dynamic(25)
        val TYPE_DRIP_WATER: Int = dynamic(26)
        val TYPE_DRIP_LAVA: Int = dynamic(27)
        val TYPE_DRIP_HONEY: Int = dynamic(28)

        @Since("1.4.0.0-PN")
        val TYPE_STALACTITE_DRIP_WATER: Int = dynamic(29)

        @Since("1.4.0.0-PN")
        val TYPE_STALACTITE_DRIP_LAVA: Int = dynamic(30)
        val TYPE_FALLING_DUST: Int = dynamic(31)
        val TYPE_DUST = TYPE_FALLING_DUST
        val TYPE_MOB_SPELL: Int = dynamic(32)
        val TYPE_MOB_SPELL_AMBIENT: Int = dynamic(33)
        val TYPE_MOB_SPELL_INSTANTANEOUS: Int = dynamic(34)
        val TYPE_INK: Int = dynamic(35)
        val TYPE_SLIME: Int = dynamic(36)
        val TYPE_RAIN_SPLASH: Int = dynamic(37)
        val TYPE_VILLAGER_ANGRY: Int = dynamic(38)
        val TYPE_VILLAGER_HAPPY: Int = dynamic(39)
        val TYPE_ENCHANTMENT_TABLE: Int = dynamic(40)
        val TYPE_TRACKING_EMITTER: Int = dynamic(41)
        val TYPE_NOTE: Int = dynamic(42)

        @PowerNukkitOnly("Backward compatibility")
        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "NukkitX", reason = "Removed from Nukkit")
        val TYPE_NOTE_AND_DUST = TYPE_NOTE
        val TYPE_WITCH_SPELL: Int = dynamic(43)
        val TYPE_CARROT: Int = dynamic(44)

        @Since("1.4.0.0-PN")
        val TYPE_MOB_APPEARANCE: Int = dynamic(45)
        val TYPE_END_ROD: Int = dynamic(46)
        val TYPE_RISING_DRAGONS_BREATH: Int = dynamic(47)
        val TYPE_SPIT: Int = dynamic(48)
        val TYPE_TOTEM: Int = dynamic(49)
        val TYPE_FOOD: Int = dynamic(50)
        val TYPE_FIREWORKS_STARTER: Int = dynamic(51)
        val TYPE_FIREWORKS_SPARK: Int = dynamic(52)
        val TYPE_FIREWORKS_OVERLAY: Int = dynamic(53)
        val TYPE_BALLOON_GAS: Int = dynamic(54)
        val TYPE_COLORED_FLAME: Int = dynamic(55)
        val TYPE_SPARKLER: Int = dynamic(56)
        val TYPE_CONDUIT: Int = dynamic(57)
        val TYPE_BUBBLE_COLUMN_UP: Int = dynamic(58)
        val TYPE_BUBBLE_COLUMN_DOWN: Int = dynamic(59)
        val TYPE_SNEEZE: Int = dynamic(60)

        @Since("1.4.0.0-PN")
        val TYPE_SHULKER_BULLET: Int = dynamic(61)

        @Since("1.4.0.0-PN")
        val TYPE_BLEACH: Int = dynamic(62)
        val TYPE_LARGE_EXPLOSION: Int = dynamic(63)

        @Since("1.4.0.0-PN")
        val TYPE_MYCELIUM_DUST: Int = dynamic(64)
        val TYPE_FALLING_RED_DUST: Int = dynamic(65)
        val TYPE_CAMPFIRE_SMOKE: Int = dynamic(66)

        @Since("1.4.0.0-PN")
        val TYPE_TALL_CAMPFIRE_SMOKE: Int = dynamic(67)
        val TYPE_FALLING_DRAGONS_BREATH: Int = dynamic(68)
        val TYPE_DRAGONS_BREATH: Int = dynamic(69)

        @Since("1.4.0.0-PN")
        val TYPE_BLUE_FLAME: Int = dynamic(70)

        @Since("1.4.0.0-PN")
        val TYPE_SOUL: Int = dynamic(71)

        @Since("1.4.0.0-PN")
        val TYPE_OBSIDIAN_TEAR: Int = dynamic(72)

        @Since("1.4.0.0-PN")
        val TYPE_PORTAL_REVERSE: Int = dynamic(73)

        @Since("1.4.0.0-PN")
        val TYPE_SNOWFLAKE: Int = dynamic(74)

        @Since("1.4.0.0-PN")
        val TYPE_VIBRATION_SIGNAL: Int = dynamic(75)

        @Since("1.4.0.0-PN")
        val TYPE_SCULK_SENSOR_REDSTONE: Int = dynamic(76)

        @Since("1.4.0.0-PN")
        val TYPE_SPORE_BLOSSOM_SHOWER: Int = dynamic(77)

        @Since("1.4.0.0-PN")
        val TYPE_SPORE_BLOSSOM_AMBIENT: Int = dynamic(78)

        @Since("1.4.0.0-PN")
        val TYPE_WAX: Int = dynamic(79)

        @Since("1.4.0.0-PN")
        val TYPE_ELECTRIC_SPARK: Int = dynamic(80)
        @Since("1.4.0.0-PN")
        fun getParticleIdByName(name: String): Integer? {
            var name = name
            name = name.toUpperCase()
            try {
                val field: Field = Particle::class.java.getDeclaredField(if (name.startsWith("TYPE_")) name else "TYPE_$name")
                val type: Class<*> = field.getType()
                if (type === Int::class.javaPrimitiveType) {
                    return field.getInt(null)
                }
            } catch (e: NoSuchFieldException) {
                // ignore
            } catch (e: IllegalAccessException) {
            }
            return null
        }

        @Since("1.4.0.0-PN")
        fun particleExists(name: String): Boolean {
            return getParticleIdByName(name) != null
        }
    }
}