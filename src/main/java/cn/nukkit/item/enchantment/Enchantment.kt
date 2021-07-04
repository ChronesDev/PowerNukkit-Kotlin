package cn.nukkit.item.enchantment

import cn.nukkit.api.DeprecationDetails

/**
 * An enchantment that can be to applied to an item.
 *
 * @author MagicDroidX (Nukkit Project)
 */
abstract class Enchantment @Since("1.4.0.0-PN") protected constructor(
        /**
         * The internal ID which this enchantment got registered.
         */
        val id: Int, name: String, private val rarity: Rarity, type: EnchantmentType) : Cloneable {
    /**
     * The group of objects that this enchantment can be applied.
     */
    @Nonnull
    var type: EnchantmentType

    /**
     * The level of this enchantment. Starting from `1`.
     */
    protected var level = 1

    /**
     * The name visible by the player, this is used in conjunction with [.getName],
     * unless modified with an override, the getter will automatically add
     * "%enchantment." as prefix to grab the translation key
     */
    protected val name: String

    /**
     * Constructs this instance using the given data and with level 1.
     * @param id The enchantment ID
     * @param name The translation key without the "%enchantment." suffix
     * @param weight How rare this enchantment is, from `1` to `10` both inclusive where `1` is the rarest
     * @param type Where the enchantment can be applied
     */
    @PowerNukkitOnly("Was removed from Nukkit in 1.4.0.0-PN, keeping it in PowerNukkit for backward compatibility")
    @Deprecated
    @DeprecationDetails(by = "Cloudburst Nukkit", since = "1.4.0.0-PN", reason = "Changed the signature without backward compatibility", replaceWith = "Enchantment(int, String, Rarity, EnchantmentType)")
    protected constructor(id: Int, name: String, weight: Int, type: EnchantmentType) : this(id, name, Rarity.fromWeight(weight), type) {
    }

    /**
     * The current level of this enchantment. `0` means that the enchantment is not applied.
     * @return The level starting from `1`.
     */
    fun getLevel(): Int {
        return level
    }

    /**
     * Changes the level of this enchantment.
     * The level is clamped between the values returned in [.getMinLevel] and [.getMaxLevel].
     *
     * @param level The level starting from `1`.
     * @return This object so you can do chained calls
     */
    @Nonnull
    fun setLevel(level: Int): Enchantment {
        return this.setLevel(level, true)
    }

    /**
     * Changes the level of this enchantment.
     * When the `safe` param is `true`, the level is clamped between the values
     * returned in [.getMinLevel] and [.getMaxLevel].
     *
     * @param level The level starting from `1`.
     * @param safe If the level should clamped or applied directly
     * @return This object so you can do chained calls
     */
    @Nonnull
    fun setLevel(level: Int, safe: Boolean): Enchantment {
        if (!safe) {
            this.level = level
            return this
        }
        this.level = NukkitMath.clamp(level, getMinLevel(), getMaxLevel())
        return this
    }

    /**
     * The ID of this enchantment.
     */
    fun getId(): Int {
        return id
    }

    /**
     * How rare this enchantment is.
     */
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getRarity(): Rarity {
        return rarity
    }

    /**
     * How rare this enchantment is, from `1` to `10` where `1` is the rarest.
     */
    @DeprecationDetails(since = "1.4.0.0-PN", by = "Cloudburst Nukkit", reason = "Refactored enchantments and now uses a Rarity enum", replaceWith = "getRarity().getWeight()")
    @Deprecated
    @Deprecated("use {@link Rarity#getWeight()} instead")
    fun getWeight(): Int {
        return rarity.getWeight()
    }

    /**
     * The minimum safe level which is possible with this enchantment. It is usually `1`.
     */
    fun getMinLevel(): Int {
        return 1
    }

    /**
     * The maximum safe level which is possible with this enchantment.
     */
    fun getMaxLevel(): Int {
        return 1
    }

    /**
     * The maximum level that can be obtained using an enchanting table.
     */
    fun getMaxEnchantableLevel(): Int {
        return getMaxLevel()
    }

    /**
     * The minimum enchantability for the given level as described in https://minecraft.gamepedia.com/Enchanting/Levels
     * @param level The level being checked
     * @return The minimum value
     */
    fun getMinEnchantAbility(level: Int): Int {
        return 1 + level * 10
    }

    /**
     * The maximum enchantability for the given level as described in https://minecraft.gamepedia.com/Enchanting/Levels
     * @param level The level being checked
     * @return The maximum value
     */
    fun getMaxEnchantAbility(level: Int): Int {
        return getMinEnchantAbility(level) + 5
    }

    fun getProtectionFactor(event: EntityDamageEvent?): Float {
        return 0
    }

    fun getDamageBonus(entity: Entity?): Double {
        return 0
    }

    fun doPostAttack(attacker: Entity?, entity: Entity?) {}
    fun doPostHurt(attacker: Entity?, entity: Entity?) {}

    /**
     * Returns true if and only if this enchantment is compatible with the other and
     * the other is also compatible with this enchantment.
     * @param enchantment The enchantment which is being checked
     * @return If both enchantments are compatible
     * @implNote Cloudburst Nukkit added the final modifier, PowerNukkit removed it to maintain backward compatibility.
     * The right way to implement compatibility now is to override [.checkCompatibility]
     * and also make sure to keep it protected! Some overrides was incorrectly made public, let's avoid this mistake
     */
    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Cloudburst Nukkit added the final modifier, we removed it to maintain backward compatibility. " +
            "The right way to implement compatibility now is to override checkCompatibility(Enchantment enchantment) " +
            "and also make sure to keep it protected! Some overrides was incorrectly made public, let's avoid this mistake.")
    fun isCompatibleWith(@Nonnull enchantment: Enchantment): Boolean {
        return checkCompatibility(enchantment) && enchantment.checkCompatibility(this)
    }

    /**
     * Checks if this enchantment can be applied to an item that have the give enchantment without doing reverse check.
     * @param enchantment The enchantment to be checked
     * @return If this enchantment is compatible with the other enchantment.
     */
    @Since("1.4.0.0-PN")
    protected fun checkCompatibility(enchantment: Enchantment): Boolean {
        return this !== enchantment
    }

    fun getName(): String {
        return "%enchantment." + name
    }

    /**
     * Checks if the given item have a type which is compatible with this enchantment. This method does not check
     * if the item already have incompatible enchantments.
     * @param item The item to be checked
     * @return If the type of the item is valid for this enchantment
     */
    fun canEnchant(@Nonnull item: Item): Boolean {
        return type.canEnchantItem(item)
    }

    fun isMajor(): Boolean {
        return false
    }

    @Override
    protected fun clone(): Enchantment? {
        return try {
            super.clone() as Enchantment?
        } catch (e: CloneNotSupportedException) {
            null
        }
    }

    /**
     * Checks if an item can have this enchantment. It's not strict to the enchantment table.
     */
    @PowerNukkitOnly
    @Since("1.2.1.0-PN")
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "1.4.0.0-PN", reason = "Does the same as canEnchant(item)", replaceWith = "canEnchant(item)")
    fun isItemAcceptable(item: Item): Boolean {
        return canEnchant(item)
    }

    private class UnknownEnchantment(id: Int) : Enchantment(id, "unknown", Rarity.VERY_RARE, EnchantmentType.ALL)

    /**
     * How rare an enchantment is.
     */
    @Since("1.4.0.0-PN")
    enum class Rarity(private val weight: Int) {
        COMMON(10), UNCOMMON(5), RARE(2), VERY_RARE(1);

        @Since("1.4.0.0-PN")
        fun getWeight(): Int {
            return weight
        }

        companion object {
            /**
             * Converts the weight to the closest rarity using floor semantic.
             * @param weight The enchantment weight
             * @return The closest rarity
             */
            @Since("1.4.0.0-PN")
            fun fromWeight(weight: Int): Rarity {
                if (weight < 2) {
                    return VERY_RARE
                } else if (weight < 5) {
                    return RARE
                } else if (weight < 10) {
                    return UNCOMMON
                }
                return COMMON
            }
        }
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EMPTY_ARRAY = arrayOfNulls<Enchantment>(0)
        protected var enchantments: Array<Enchantment?>

        //http://minecraft.gamepedia.com/Enchanting#Aqua_Affinity
        const val ID_PROTECTION_ALL = 0
        const val ID_PROTECTION_FIRE = 1
        const val ID_PROTECTION_FALL = 2
        const val ID_PROTECTION_EXPLOSION = 3
        const val ID_PROTECTION_PROJECTILE = 4
        const val ID_THORNS = 5
        const val ID_WATER_BREATHING = 6
        const val ID_WATER_WALKER = 7
        const val ID_WATER_WORKER = 8
        const val ID_DAMAGE_ALL = 9
        const val ID_DAMAGE_SMITE = 10
        const val ID_DAMAGE_ARTHROPODS = 11
        const val ID_KNOCKBACK = 12
        const val ID_FIRE_ASPECT = 13
        const val ID_LOOTING = 14
        const val ID_EFFICIENCY = 15
        const val ID_SILK_TOUCH = 16
        const val ID_DURABILITY = 17
        const val ID_FORTUNE_DIGGING = 18
        const val ID_BOW_POWER = 19
        const val ID_BOW_KNOCKBACK = 20
        const val ID_BOW_FLAME = 21
        const val ID_BOW_INFINITY = 22
        const val ID_FORTUNE_FISHING = 23
        const val ID_LURE = 24
        const val ID_FROST_WALKER = 25
        const val ID_MENDING = 26
        const val ID_BINDING_CURSE = 27
        const val ID_VANISHING_CURSE = 28
        const val ID_TRIDENT_IMPALING = 29
        const val ID_TRIDENT_RIPTIDE = 30
        const val ID_TRIDENT_LOYALTY = 31
        const val ID_TRIDENT_CHANNELING = 32

        @Since("1.4.0.0-PN")
        val ID_CROSSBOW_MULTISHOT = 33

        @Since("1.4.0.0-PN")
        val ID_CROSSBOW_PIERCING = 34

        @Since("1.4.0.0-PN")
        val ID_CROSSBOW_QUICK_CHARGE = 35

        @Since("1.4.0.0-PN")
        val ID_SOUL_SPEED = 36
        fun init() {
            enchantments = arrayOfNulls(256)
            enchantments[ID_PROTECTION_ALL] = EnchantmentProtectionAll()
            enchantments[ID_PROTECTION_FIRE] = EnchantmentProtectionFire()
            enchantments[ID_PROTECTION_FALL] = EnchantmentProtectionFall()
            enchantments[ID_PROTECTION_EXPLOSION] = EnchantmentProtectionExplosion()
            enchantments[ID_PROTECTION_PROJECTILE] = EnchantmentProtectionProjectile()
            enchantments[ID_THORNS] = EnchantmentThorns()
            enchantments[ID_WATER_BREATHING] = EnchantmentWaterBreath()
            enchantments[ID_WATER_WORKER] = EnchantmentWaterWorker()
            enchantments[ID_WATER_WALKER] = EnchantmentWaterWalker()
            enchantments[ID_DAMAGE_ALL] = EnchantmentDamageAll()
            enchantments[ID_DAMAGE_SMITE] = EnchantmentDamageSmite()
            enchantments[ID_DAMAGE_ARTHROPODS] = EnchantmentDamageArthropods()
            enchantments[ID_KNOCKBACK] = EnchantmentKnockback()
            enchantments[ID_FIRE_ASPECT] = EnchantmentFireAspect()
            enchantments[ID_LOOTING] = EnchantmentLootWeapon()
            enchantments[ID_EFFICIENCY] = EnchantmentEfficiency()
            enchantments[ID_SILK_TOUCH] = EnchantmentSilkTouch()
            enchantments[ID_DURABILITY] = EnchantmentDurability()
            enchantments[ID_FORTUNE_DIGGING] = EnchantmentLootDigging()
            enchantments[ID_BOW_POWER] = EnchantmentBowPower()
            enchantments[ID_BOW_KNOCKBACK] = EnchantmentBowKnockback()
            enchantments[ID_BOW_FLAME] = EnchantmentBowFlame()
            enchantments[ID_BOW_INFINITY] = EnchantmentBowInfinity()
            enchantments[ID_FORTUNE_FISHING] = EnchantmentLootFishing()
            enchantments[ID_LURE] = EnchantmentLure()
            enchantments[ID_FROST_WALKER] = EnchantmentFrostWalker()
            enchantments[ID_MENDING] = EnchantmentMending()
            enchantments[ID_BINDING_CURSE] = EnchantmentBindingCurse()
            enchantments[ID_VANISHING_CURSE] = EnchantmentVanishingCurse()
            enchantments[ID_TRIDENT_IMPALING] = EnchantmentTridentImpaling()
            enchantments[ID_TRIDENT_RIPTIDE] = EnchantmentTridentRiptide()
            enchantments[ID_TRIDENT_LOYALTY] = EnchantmentTridentLoyalty()
            enchantments[ID_TRIDENT_CHANNELING] = EnchantmentTridentChanneling()
            enchantments[ID_CROSSBOW_MULTISHOT] = EnchantmentCrossbowMultishot()
            enchantments[ID_CROSSBOW_PIERCING] = EnchantmentCrossbowPiercing()
            enchantments[ID_CROSSBOW_QUICK_CHARGE] = EnchantmentCrossbowQuickCharge()
            enchantments[ID_SOUL_SPEED] = EnchantmentSoulSpeed()
        }

        /**
         * Returns the enchantment object registered with this ID, any change to the returned object affects
         * the creation of new enchantments as the returned object is not a copy.
         * @param id The enchantment id.
         * @return The enchantment, if no enchantment is found with that id, [UnknownEnchantment] is returned.
         * The UnknownEnchantment will be always a new instance and changes to it does not affects other calls.
         */
        @Deprecated
        @DeprecationDetails(by = "PowerNukkit", reason = "This is very insecure and can break the environment", since = "1.4.0.0-PN", replaceWith = "getEnchantment(int)")
        operator fun get(id: Int): Enchantment {
            var enchantment: Enchantment? = null
            if (id >= 0 && id < enchantments.size) {
                enchantment = enchantments[id]
            }
            return enchantment ?: UnknownEnchantment(id)
        }

        /**
         * The same as [.get] but returns a safe copy of the enchantment.
         * @param id The enchantment id
         * @return A new enchantment object.
         */
        fun getEnchantment(id: Int): Enchantment? {
            return Companion[id].clone()
        }

        /**
         * Gets an array of all registered enchantments, the objects in the array are linked to the registry,
         * it's not safe to change them. Changing them can cause the same issue as documented in [.get]
         * @return An array with the enchantment objects, the array may contain null objects but is very unlikely.
         */
        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "The objects returned by this method are not safe to use and the implementation may skip some enchantments", replaceWith = "getRegisteredEnchantments()")
        fun getEnchantments(): Array<Enchantment> {
            val list: ArrayList<Enchantment> = ArrayList()
            for (enchantment in enchantments) {
                if (enchantment == null) {
                    break
                }
                list.add(enchantment)
            }
            return list.toArray(EMPTY_ARRAY)
        }

        /**
         * Gets a collection with a safe copy of all enchantments that are currently registered.
         * @return The objects can be modified without affecting the registry and the collection will not have null values.
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun getRegisteredEnchantments(): Collection<Enchantment> {
            return Arrays.stream(enchantments)
                    .filter(Objects::nonNull)
                    .map { obj: Enchantment -> obj.clone() }
                    .collect(Collectors.toList())
        }

        val words = arrayOf("the", "elder", "scrolls", "klaatu", "berata", "niktu", "xyzzy", "bless", "curse", "light", "darkness", "fire", "air", "earth", "water", "hot", "dry", "cold", "wet", "ignite", "snuff", "embiggen", "twist", "shorten", "stretch", "fiddle", "destroy", "imbue", "galvanize", "enchant", "free", "limited", "range", "of", "towards", "inside", "sphere", "cube", "self", "other", "ball", "mental", "physical", "grow", "shrink", "demon", "elemental", "spirit", "animal", "creature", "beast", "humanoid", "undead", "fresh", "stale")
        fun getRandomName(): String {
            val count: Int = ThreadLocalRandom.current().nextInt(3, 6)
            val set: HashSet<String> = LinkedHashSet()
            while (set.size() < count) {
                set.add(words[ThreadLocalRandom.current().nextInt(0, words.size)])
            }
            val words: Array<String> = set.toArray(EmptyArrays.EMPTY_STRINGS)
            return String.join(" ", words)
        }
    }

    /**
     * Constructs this instance using the given data and with level 1.
     * @param id The enchantment ID
     * @param name The translation key without the "%enchantment." suffix
     * @param rarity How rare this enchantment is
     * @param type Where the enchantment can be applied
     */
    init {
        this.type = type
        this.name = name
    }
}