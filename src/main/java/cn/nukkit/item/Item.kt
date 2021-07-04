package cn.nukkit.item

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
class Item @JvmOverloads constructor(protected val id: Int, meta: Integer? = 0, count: Int = 1, name: String? = UNKNOWN_STR) : Cloneable, BlockID, ItemID {
    protected var block: Block? = null
    protected var meta = 0
    protected var hasMeta = true
    private var tags: ByteArray? = EmptyArrays.EMPTY_BYTES

    @Transient
    private var cachedNBT: CompoundTag? = null
    var count: Int

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Unused", replaceWith = "meta or getDamage()")
    protected var durability = 0
    protected var name: String?

    constructor(id: Int, meta: Integer?) : this(id, meta, 1, UNKNOWN_STR) {}
    constructor(id: Int, meta: Integer?, count: Int) : this(id, meta, count, UNKNOWN_STR) {}

    fun hasMeta(): Boolean {
        return hasMeta
    }

    fun canBeActivated(): Boolean {
        return false
    }

    fun setCompoundTag(tag: CompoundTag?): Item {
        setNamedTag(tag)
        return this
    }

    fun setCompoundTag(tags: ByteArray?): Item {
        this.tags = tags
        cachedNBT = null
        return this
    }

    fun getCompoundTag(): ByteArray? {
        return tags
    }

    fun hasCompoundTag(): Boolean {
        return tags != null && tags!!.size > 0
    }

    fun hasCustomBlockData(): Boolean {
        if (!hasCompoundTag()) {
            return false
        }
        val tag: CompoundTag? = getNamedTag()
        return tag.contains("BlockEntityTag") && tag.get("BlockEntityTag") is CompoundTag
    }

    fun clearCustomBlockData(): Item {
        if (!hasCompoundTag()) {
            return this
        }
        val tag: CompoundTag? = getNamedTag()
        if (tag.contains("BlockEntityTag") && tag.get("BlockEntityTag") is CompoundTag) {
            tag.remove("BlockEntityTag")
            setNamedTag(tag)
        }
        return this
    }

    fun setCustomBlockData(compoundTag: CompoundTag): Item {
        val tags: CompoundTag = compoundTag.copy()
        tags.setName("BlockEntityTag")
        val tag: CompoundTag?
        if (!hasCompoundTag()) {
            tag = CompoundTag()
        } else {
            tag = getNamedTag()
        }
        tag.putCompound("BlockEntityTag", tags)
        setNamedTag(tag)
        return this
    }

    fun getCustomBlockData(): CompoundTag? {
        if (!hasCompoundTag()) {
            return null
        }
        val tag: CompoundTag? = getNamedTag()
        if (tag.contains("BlockEntityTag")) {
            val bet: Tag = tag.get("BlockEntityTag")
            if (bet is CompoundTag) {
                return bet as CompoundTag
            }
        }
        return null
    }

    fun hasEnchantments(): Boolean {
        if (!hasCompoundTag()) {
            return false
        }
        val tag: CompoundTag? = getNamedTag()
        if (tag.contains("ench")) {
            val enchTag: Tag = tag.get("ench")
            return enchTag is ListTag
        }
        return false
    }

    /**
     * Convenience method to check if the item stack has positive level on a specific enchantment by it's id.
     * @param id The enchantment ID from [Enchantment] constants.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun hasEnchantment(id: Int): Boolean {
        return getEnchantmentLevel(id) > 0
    }

    /**
     * Find the enchantment level by the enchantment id.
     * @param id The enchantment ID from [Enchantment] constants.
     * @return `0` if the item don't have that enchantment or the current level of the given enchantment.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getEnchantmentLevel(id: Int): Int {
        if (!hasEnchantments()) {
            return 0
        }
        for (entry in getNamedTag().getList("ench", CompoundTag::class.java).getAll()) {
            if (entry.getShort("id") === id) {
                return entry.getShort("lvl")
            }
        }
        return 0
    }

    fun getEnchantment(id: Int): Enchantment? {
        return getEnchantment((id and 0xffff).toShort())
    }

    fun getEnchantment(id: Short): Enchantment? {
        if (!hasEnchantments()) {
            return null
        }
        for (entry in getNamedTag().getList("ench", CompoundTag::class.java).getAll()) {
            if (entry.getShort("id") === id) {
                val e: Enchantment = Enchantment.getEnchantment(entry.getShort("id"))
                if (e != null) {
                    e.setLevel(entry.getShort("lvl"), false)
                    return e
                }
            }
        }
        return null
    }

    fun addEnchantment(vararg enchantments: Enchantment) {
        val tag: CompoundTag?
        if (!hasCompoundTag()) {
            tag = CompoundTag()
        } else {
            tag = getNamedTag()
        }
        val ench: ListTag<CompoundTag>
        if (!tag.contains("ench")) {
            ench = ListTag("ench")
            tag.putList(ench)
        } else {
            ench = tag.getList("ench", CompoundTag::class.java)
        }
        for (enchantment in enchantments) {
            var found = false
            for (k in 0 until ench.size()) {
                val entry: CompoundTag = ench.get(k)
                if (entry.getShort("id") === enchantment.getId()) {
                    ench.add(k, CompoundTag()
                            .putShort("id", enchantment.getId())
                            .putShort("lvl", enchantment.getLevel())
                    )
                    found = true
                    break
                }
            }
            if (!found) {
                ench.add(CompoundTag()
                        .putShort("id", enchantment.getId())
                        .putShort("lvl", enchantment.getLevel())
                )
            }
        }
        setNamedTag(tag)
    }

    fun getEnchantments(): Array<Enchantment> {
        if (!hasEnchantments()) {
            return Enchantment.EMPTY_ARRAY
        }
        val enchantments: List<Enchantment> = ArrayList()
        val ench: ListTag<CompoundTag> = getNamedTag().getList("ench", CompoundTag::class.java)
        for (entry in ench.getAll()) {
            val e: Enchantment = Enchantment.getEnchantment(entry.getShort("id"))
            if (e != null) {
                e.setLevel(entry.getShort("lvl"), false)
                enchantments.add(e)
            }
        }
        return enchantments.toArray(Enchantment.EMPTY_ARRAY)
    }

    @Since("1.4.0.0-PN")
    fun getRepairCost(): Int {
        if (hasCompoundTag()) {
            val tag: CompoundTag? = getNamedTag()
            if (tag.contains("RepairCost")) {
                val repairCost: Tag = tag.get("RepairCost")
                if (repairCost is IntTag) {
                    return (repairCost as IntTag).data
                }
            }
        }
        return 0
    }

    @Since("1.4.0.0-PN")
    fun setRepairCost(cost: Int): Item {
        if (cost <= 0 && hasCompoundTag()) {
            return setNamedTag(getNamedTag().remove("RepairCost"))
        }
        val tag: CompoundTag?
        if (!hasCompoundTag()) {
            tag = CompoundTag()
        } else {
            tag = getNamedTag()
        }
        return setNamedTag(tag.putInt("RepairCost", cost))
    }

    fun hasCustomName(): Boolean {
        if (!hasCompoundTag()) {
            return false
        }
        val tag: CompoundTag? = getNamedTag()
        if (tag.contains("display")) {
            val tag1: Tag = tag.get("display")
            return tag1 is CompoundTag && (tag1 as CompoundTag).contains("Name") && (tag1 as CompoundTag).get("Name") is StringTag
        }
        return false
    }

    fun getCustomName(): String {
        if (!hasCompoundTag()) {
            return ""
        }
        val tag: CompoundTag? = getNamedTag()
        if (tag.contains("display")) {
            val tag1: Tag = tag.get("display")
            if (tag1 is CompoundTag && (tag1 as CompoundTag).contains("Name") && (tag1 as CompoundTag).get("Name") is StringTag) {
                return (tag1 as CompoundTag).getString("Name")
            }
        }
        return ""
    }

    fun setCustomName(name: String?): Item {
        if (name == null || name.equals("")) {
            clearCustomName()
        }
        val tag: CompoundTag?
        if (!hasCompoundTag()) {
            tag = CompoundTag()
        } else {
            tag = getNamedTag()
        }
        if (tag.contains("display") && tag.get("display") is CompoundTag) {
            tag.getCompound("display").putString("Name", name)
        } else {
            tag.putCompound("display", CompoundTag("display")
                    .putString("Name", name)
            )
        }
        setNamedTag(tag)
        return this
    }

    fun clearCustomName(): Item {
        if (!hasCompoundTag()) {
            return this
        }
        val tag: CompoundTag? = getNamedTag()
        if (tag.contains("display") && tag.get("display") is CompoundTag) {
            tag.getCompound("display").remove("Name")
            if (tag.getCompound("display").isEmpty()) {
                tag.remove("display")
            }
            setNamedTag(tag)
        }
        return this
    }

    fun getLore(): Array<String> {
        val tag: Tag? = getNamedTagEntry("display")
        val lines: ArrayList<String> = ArrayList()
        if (tag is CompoundTag) {
            val nbt: CompoundTag? = tag as CompoundTag?
            val lore: ListTag<StringTag> = nbt.getList("Lore", StringTag::class.java)
            if (lore.size() > 0) {
                for (stringTag in lore.getAll()) {
                    lines.add(stringTag.data)
                }
            }
        }
        return lines.toArray(EmptyArrays.EMPTY_STRINGS)
    }

    fun setLore(vararg lines: String?): Item {
        val tag: CompoundTag?
        if (!hasCompoundTag()) {
            tag = CompoundTag()
        } else {
            tag = getNamedTag()
        }
        val lore: ListTag<StringTag> = ListTag("Lore")
        for (line in lines) {
            lore.add(StringTag("", line))
        }
        if (!tag.contains("display")) {
            tag.putCompound("display", CompoundTag("display").putList(lore))
        } else {
            tag.getCompound("display").putList(lore)
        }
        setNamedTag(tag)
        return this
    }

    fun getNamedTagEntry(name: String?): Tag? {
        val tag: CompoundTag? = getNamedTag()
        return if (tag != null) {
            if (tag.contains(name)) tag.get(name) else null
        } else null
    }

    fun getNamedTag(): CompoundTag? {
        if (!hasCompoundTag()) {
            return null
        }
        if (cachedNBT == null) {
            cachedNBT = parseCompoundTag(tags)
        }
        if (cachedNBT != null) {
            cachedNBT.setName("")
        }
        return cachedNBT
    }

    fun setNamedTag(tag: CompoundTag?): Item {
        if (tag.isEmpty()) {
            return clearNamedTag()
        }
        tag.setName(null)
        cachedNBT = tag
        tags = writeCompoundTag(tag)
        return this
    }

    fun clearNamedTag(): Item {
        return this.setCompoundTag(EmptyArrays.EMPTY_BYTES)
    }

    fun writeCompoundTag(tag: CompoundTag?): ByteArray {
        return try {
            tag.setName("")
            NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN)
        } catch (e: IOException) {
            throw UncheckedIOException(e)
        }
    }

    fun getCount(): Int {
        return count
    }

    fun setCount(count: Int) {
        this.count = count
    }

    fun isNull(): Boolean {
        return count <= 0 || id == AIR
    }

    fun getName(): String {
        return if (hasCustomName()) getCustomName() else name!!
    }

    fun canBePlaced(): Boolean {
        return block != null && block.canBePlaced()
    }

    fun getBlock(): Block {
        return if (block != null) {
            block.clone()
        } else {
            Block.get(BlockID.AIR)
        }
    }

    @Since("1.4.0.0-PN")
    @API(definition = API.Definition.INTERNAL, usage = API.Usage.INCUBATING)
    fun getBlockUnsafe(): Block? {
        return block
    }

    fun getId(): Int {
        return id
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Throws(UnknownNetworkIdException::class)
    fun getNetworkFullId(): Int {
        return try {
            RuntimeItems.getRuntimeMapping()!!.getNetworkFullId(this)
        } catch (e: IllegalArgumentException) {
            throw UnknownNetworkIdException(this, e)
        }
    }

    @Since("1.4.0.0-PN")
    @Throws(UnknownNetworkIdException::class)
    fun getNetworkId(): Int {
        return RuntimeItems.getNetworkId(getNetworkFullId())
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getNamespaceId(): String {
        val runtimeMapping: RuntimeItemMapping = RuntimeItems.getRuntimeMapping()
        return runtimeMapping!!.getNamespacedIdByNetworkId(
                RuntimeItems.getNetworkId(runtimeMapping!!.getNetworkFullId(this))
        )
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getBlockId(): Int {
        return if (block != null) {
            block.getId()
        } else {
            -1
        }
    }

    fun getDamage(): Int {
        return meta
    }

    fun setDamage(meta: Integer?) {
        if (meta != null) {
            this.meta = meta and 0xffff
        } else {
            hasMeta = false
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun createFuzzyCraftingRecipe(): Item? {
        val item = clone()
        item!!.hasMeta = false
        return item
    }

    fun getMaxStackSize(): Int {
        return if (block == null) 64 else block.getItemMaxStackSize()
    }

    fun getFuelTime(): Short? {
        if (!Fuel.duration.containsKey(id)) {
            return null
        }
        return if (id != BUCKET || meta == 10) {
            Fuel.duration.get(id)
        } else null
    }

    fun useOn(entity: Entity?): Boolean {
        return false
    }

    fun useOn(block: Block?): Boolean {
        return false
    }

    fun isTool(): Boolean {
        return false
    }

    fun getMaxDurability(): Int {
        return -1
    }

    fun getTier(): Int {
        return 0
    }

    fun isPickaxe(): Boolean {
        return false
    }

    fun isAxe(): Boolean {
        return false
    }

    fun isSword(): Boolean {
        return false
    }

    fun isShovel(): Boolean {
        return false
    }

    fun isHoe(): Boolean {
        return false
    }

    fun isShears(): Boolean {
        return false
    }

    fun isArmor(): Boolean {
        return false
    }

    fun isHelmet(): Boolean {
        return false
    }

    fun isChestplate(): Boolean {
        return false
    }

    fun isLeggings(): Boolean {
        return false
    }

    fun isBoots(): Boolean {
        return false
    }

    fun getEnchantAbility(): Int {
        return 0
    }

    fun getAttackDamage(): Int {
        return 1
    }

    fun getArmorPoints(): Int {
        return 0
    }

    fun getToughness(): Int {
        return 0
    }

    fun isUnbreakable(): Boolean {
        return false
    }

    /**
     * If the item is resistant to lava and fire and can float on lava like if it was on water.
     * @since 1.4.0.0-PN
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun isLavaResistant(): Boolean {
        return false
    }

    fun onUse(player: Player?, ticksUsed: Int): Boolean {
        return false
    }

    fun onRelease(player: Player?, ticksUsed: Int): Boolean {
        return false
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun damageWhenBreaking(): Boolean {
        return true
    }

    @Override
    override fun toString(): String {
        return "Item " + name + " (" + id + ":" + (if (!hasMeta) "?" else meta) + ")x" + count + if (hasCompoundTag()) " tags:0x" + Binary.bytesToHexString(getCompoundTag()) else ""
    }

    fun getDestroySpeed(block: Block?, player: Player?): Int {
        return 1
    }

    fun onActivate(level: Level?, player: Player?, block: Block?, target: Block?, face: BlockFace?, fx: Double, fy: Double, fz: Double): Boolean {
        return false
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun decrement(amount: Int): Item? {
        return increment(-amount)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun increment(amount: Int): Item? {
        if (count + amount <= 0) {
            return getBlock(BlockID.AIR)
        }
        val cloned = clone()
        cloned!!.count += amount
        return cloned
    }

    /**
     * When true, this item can be used to reduce growing times like a bone meal.
     * @return `true` if it can act like a bone meal
     */
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    fun isFertilizer(): Boolean {
        return false
    }

    /**
     * Called when a player uses the item on air, for example throwing a projectile.
     * Returns whether the item was changed, for example count decrease or durability change.
     *
     * @param player player
     * @param directionVector direction
     * @return item changed
     */
    fun onClickAir(player: Player?, directionVector: Vector3?): Boolean {
        return false
    }

    @Override
    override fun equals(item: Object): Boolean {
        return item is Item && this.equals(item as Item, true)
    }

    @JvmOverloads
    fun equals(item: Item, checkDamage: Boolean, checkCompound: Boolean = true): Boolean {
        if (getId() == item.getId() && (!checkDamage || getDamage() == item.getDamage())) {
            if (checkCompound) {
                if (Arrays.equals(getCompoundTag(), item.getCompoundTag())) {
                    return true
                } else if (hasCompoundTag() && item.hasCompoundTag()) {
                    return getNamedTag().equals(item.getNamedTag())
                }
            } else {
                return true
            }
        }
        return false
    }

    /**
     * Returns whether the specified item stack has the same ID, damage, NBT and count as this item stack.
     *
     * @param other item
     * @return equal
     */
    fun equalsExact(other: Item): Boolean {
        return this.equals(other, true, true) && count == other.count
    }

    /**
     * Same as [.equals] but the enchantment order of the items does not affect the result.
     * @since 1.2.1.0-PN
     */
    fun equalsIgnoringEnchantmentOrder(item: Item, checkDamage: Boolean): Boolean {
        if (!this.equals(item, checkDamage, false)) {
            return false
        }
        if (Arrays.equals(getCompoundTag(), item.getCompoundTag())) {
            return true
        }
        if (!hasCompoundTag() || !item.hasCompoundTag()) {
            return false
        }
        val thisTags: CompoundTag? = getNamedTag()
        val otherTags: CompoundTag? = item.getNamedTag()
        if (thisTags.equals(otherTags)) {
            return true
        }
        if (!thisTags.contains("ench") || !otherTags.contains("ench")
                || thisTags.get("ench") !is ListTag
                || otherTags.get("ench") !is ListTag
                || thisTags.getList("ench").size() !== otherTags.getList("ench").size()) {
            return false
        }
        val thisEnchantmentTags: ListTag<CompoundTag> = thisTags.getList("ench", CompoundTag::class.java)
        val otherEnchantmentTags: ListTag<CompoundTag> = otherTags.getList("ench", CompoundTag::class.java)
        val size: Int = thisEnchantmentTags.size()
        val enchantments: Int2IntMap = Int2IntArrayMap(size)
        enchantments.defaultReturnValue(Integer.MIN_VALUE)
        for (i in 0 until size) {
            val tag: CompoundTag = thisEnchantmentTags.get(i)
            enchantments.put(tag.getShort("id"), tag.getShort("lvl"))
        }
        for (i in 0 until size) {
            val tag: CompoundTag = otherEnchantmentTags.get(i)
            if (enchantments.get(tag.getShort("id")) !== tag.getShort("lvl")) {
                return false
            }
        }
        return true
    }

    @Deprecated
    fun deepEquals(item: Item): Boolean {
        return equals(item, true)
    }

    @Deprecated
    fun deepEquals(item: Item, checkDamage: Boolean): Boolean {
        return equals(item, checkDamage, true)
    }

    @Deprecated
    fun deepEquals(item: Item, checkDamage: Boolean, checkCompound: Boolean): Boolean {
        return equals(item, checkDamage, checkCompound)
    }

    @Override
    fun clone(): Item? {
        return try {
            val item = super.clone() as Item
            item.tags = tags.clone()
            item.cachedNBT = null
            item
        } catch (e: CloneNotSupportedException) {
            null
        }
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EMPTY_ARRAY = arrayOfNulls<Item>(0)

        /**
         * Groups:
         *
         *  1. namespace (optional)
         *  1. item name (choice)
         *  1. damage (optional, for item name)
         *  1. numeric id (choice)
         *  1. damage (optional, for numeric id)
         *
         */
        private val ITEM_STRING_PATTERN: Pattern = Pattern.compile( //       1:namespace    2:name           3:damage   4:num-id    5:damage
                "^(?:(?:([a-z_]\\w*):)?([a-z._]\\w*)(?::(-?\\d+))?|(-?\\d+)(?::(-?\\d+))?)$")
        protected var UNKNOWN_STR = "Unknown"
        var list: Array<Class?>? = null
        private val itemIds: Map<String, Integer> = Arrays.stream(ItemID::class.java.getDeclaredFields())
                .filter { field -> field.getModifiers() === Modifier.PUBLIC or Modifier.STATIC or Modifier.FINAL }
                .filter { field -> field.getType().equals(Int::class.javaPrimitiveType) }
                .collect(Collectors.toMap(
                        { field -> field.getName().toLowerCase() },
                        { field ->
                            try {
                                return@toMap field.getInt(null)
                            } catch (e: IllegalAccessException) {
                                throw InternalError(e)
                            }
                        },
                        { e1, e2 -> e1 }) { LinkedHashMap() })
        private val blockIds: Map<String, Integer> = Arrays.stream(BlockID::class.java.getDeclaredFields())
                .filter { field -> field.getModifiers() === Modifier.PUBLIC or Modifier.STATIC or Modifier.FINAL }
                .filter { field -> field.getType().equals(Int::class.javaPrimitiveType) }
                .collect(Collectors.toMap(
                        { field -> field.getName().toLowerCase() },
                        { field ->
                            try {
                                val blockId: Int = field.getInt(null)
                                if (blockId > 255) {
                                    return@toMap 255 - blockId
                                }
                                return@toMap blockId
                            } catch (e: IllegalAccessException) {
                                throw InternalError(e)
                            }
                        },
                        { e1, e2 -> e1 }) { LinkedHashMap() })

        fun init() {
            if (list == null) {
                list = arrayOfNulls<Class>(65535)
                list!![IRON_SHOVEL] = ItemShovelIron::class.java //256
                list!![IRON_PICKAXE] = ItemPickaxeIron::class.java //257
                list!![IRON_AXE] = ItemAxeIron::class.java //258
                list!![FLINT_AND_STEEL] = ItemFlintSteel::class.java //259
                list!![APPLE] = ItemApple::class.java //260
                list!![BOW] = ItemBow::class.java //261
                list!![ARROW] = ItemArrow::class.java //262
                list!![COAL] = ItemCoal::class.java //263
                list!![DIAMOND] = ItemDiamond::class.java //264
                list!![IRON_INGOT] = ItemIngotIron::class.java //265
                list!![GOLD_INGOT] = ItemIngotGold::class.java //266
                list!![IRON_SWORD] = ItemSwordIron::class.java //267
                list!![WOODEN_SWORD] = ItemSwordWood::class.java //268
                list!![WOODEN_SHOVEL] = ItemShovelWood::class.java //269
                list!![WOODEN_PICKAXE] = ItemPickaxeWood::class.java //270
                list!![WOODEN_AXE] = ItemAxeWood::class.java //271
                list!![STONE_SWORD] = ItemSwordStone::class.java //272
                list!![STONE_SHOVEL] = ItemShovelStone::class.java //273
                list!![STONE_PICKAXE] = ItemPickaxeStone::class.java //274
                list!![STONE_AXE] = ItemAxeStone::class.java //275
                list!![DIAMOND_SWORD] = ItemSwordDiamond::class.java //276
                list!![DIAMOND_SHOVEL] = ItemShovelDiamond::class.java //277
                list!![DIAMOND_PICKAXE] = ItemPickaxeDiamond::class.java //278
                list!![DIAMOND_AXE] = ItemAxeDiamond::class.java //279
                list!![STICK] = ItemStick::class.java //280
                list!![BOWL] = ItemBowl::class.java //281
                list!![MUSHROOM_STEW] = ItemMushroomStew::class.java //282
                list!![GOLD_SWORD] = ItemSwordGold::class.java //283
                list!![GOLD_SHOVEL] = ItemShovelGold::class.java //284
                list!![GOLD_PICKAXE] = ItemPickaxeGold::class.java //285
                list!![GOLD_AXE] = ItemAxeGold::class.java //286
                list!![STRING] = ItemString::class.java //287
                list!![FEATHER] = ItemFeather::class.java //288
                list!![GUNPOWDER] = ItemGunpowder::class.java //289
                list!![WOODEN_HOE] = ItemHoeWood::class.java //290
                list!![STONE_HOE] = ItemHoeStone::class.java //291
                list!![IRON_HOE] = ItemHoeIron::class.java //292
                list!![DIAMOND_HOE] = ItemHoeDiamond::class.java //293
                list!![GOLD_HOE] = ItemHoeGold::class.java //294
                list!![WHEAT_SEEDS] = ItemSeedsWheat::class.java //295
                list!![WHEAT] = ItemWheat::class.java //296
                list!![BREAD] = ItemBread::class.java //297
                list!![LEATHER_CAP] = ItemHelmetLeather::class.java //298
                list!![LEATHER_TUNIC] = ItemChestplateLeather::class.java //299
                list!![LEATHER_PANTS] = ItemLeggingsLeather::class.java //300
                list!![LEATHER_BOOTS] = ItemBootsLeather::class.java //301
                list!![CHAIN_HELMET] = ItemHelmetChain::class.java //302
                list!![CHAIN_CHESTPLATE] = ItemChestplateChain::class.java //303
                list!![CHAIN_LEGGINGS] = ItemLeggingsChain::class.java //304
                list!![CHAIN_BOOTS] = ItemBootsChain::class.java //305
                list!![IRON_HELMET] = ItemHelmetIron::class.java //306
                list!![IRON_CHESTPLATE] = ItemChestplateIron::class.java //307
                list!![IRON_LEGGINGS] = ItemLeggingsIron::class.java //308
                list!![IRON_BOOTS] = ItemBootsIron::class.java //309
                list!![DIAMOND_HELMET] = ItemHelmetDiamond::class.java //310
                list!![DIAMOND_CHESTPLATE] = ItemChestplateDiamond::class.java //311
                list!![DIAMOND_LEGGINGS] = ItemLeggingsDiamond::class.java //312
                list!![DIAMOND_BOOTS] = ItemBootsDiamond::class.java //313
                list!![GOLD_HELMET] = ItemHelmetGold::class.java //314
                list!![GOLD_CHESTPLATE] = ItemChestplateGold::class.java //315
                list!![GOLD_LEGGINGS] = ItemLeggingsGold::class.java //316
                list!![GOLD_BOOTS] = ItemBootsGold::class.java //317
                list!![FLINT] = ItemFlint::class.java //318
                list!![RAW_PORKCHOP] = ItemPorkchopRaw::class.java //319
                list!![COOKED_PORKCHOP] = ItemPorkchopCooked::class.java //320
                list!![PAINTING] = ItemPainting::class.java //321
                list!![GOLDEN_APPLE] = ItemAppleGold::class.java //322
                list!![SIGN] = ItemSign::class.java //323
                list!![WOODEN_DOOR] = ItemDoorWood::class.java //324
                list!![BUCKET] = ItemBucket::class.java //325
                list!![MINECART] = ItemMinecart::class.java //328
                list!![SADDLE] = ItemSaddle::class.java //329
                list!![IRON_DOOR] = ItemDoorIron::class.java //330
                list!![REDSTONE] = ItemRedstone::class.java //331
                list!![SNOWBALL] = ItemSnowball::class.java //332
                list!![BOAT] = ItemBoat::class.java //333
                list!![LEATHER] = ItemLeather::class.java //334
                list!![KELP] = ItemKelp::class.java //335
                list!![BRICK] = ItemBrick::class.java //336
                list!![CLAY] = ItemClay::class.java //337
                list!![SUGARCANE] = ItemSugarcane::class.java //338
                list!![PAPER] = ItemPaper::class.java //339
                list!![BOOK] = ItemBook::class.java //340
                list!![SLIMEBALL] = ItemSlimeball::class.java //341
                list!![MINECART_WITH_CHEST] = ItemMinecartChest::class.java //342
                list!![EGG] = ItemEgg::class.java //344
                list!![COMPASS] = ItemCompass::class.java //345
                list!![FISHING_ROD] = ItemFishingRod::class.java //346
                list!![CLOCK] = ItemClock::class.java //347
                list!![GLOWSTONE_DUST] = ItemGlowstoneDust::class.java //348
                list!![RAW_FISH] = ItemFish::class.java //349
                list!![COOKED_FISH] = ItemFishCooked::class.java //350
                list!![DYE] = ItemDye::class.java //351
                list!![BONE] = ItemBone::class.java //352
                list!![SUGAR] = ItemSugar::class.java //353
                list!![CAKE] = ItemCake::class.java //354
                list!![BED] = ItemBed::class.java //355
                list!![REPEATER] = ItemRedstoneRepeater::class.java //356
                list!![COOKIE] = ItemCookie::class.java //357
                list!![MAP] = ItemMap::class.java //358
                list!![SHEARS] = ItemShears::class.java //359
                list!![MELON] = ItemMelon::class.java //360
                list!![PUMPKIN_SEEDS] = ItemSeedsPumpkin::class.java //361
                list!![MELON_SEEDS] = ItemSeedsMelon::class.java //362
                list!![RAW_BEEF] = ItemBeefRaw::class.java //363
                list!![STEAK] = ItemSteak::class.java //364
                list!![RAW_CHICKEN] = ItemChickenRaw::class.java //365
                list!![COOKED_CHICKEN] = ItemChickenCooked::class.java //366
                list!![ROTTEN_FLESH] = ItemRottenFlesh::class.java //367
                list!![ENDER_PEARL] = ItemEnderPearl::class.java //368
                list!![BLAZE_ROD] = ItemBlazeRod::class.java //369
                list!![GHAST_TEAR] = ItemGhastTear::class.java //370
                list!![GOLD_NUGGET] = ItemNuggetGold::class.java //371
                list!![NETHER_WART] = ItemNetherWart::class.java //372
                list!![POTION] = ItemPotion::class.java //373
                list!![GLASS_BOTTLE] = ItemGlassBottle::class.java //374
                list!![SPIDER_EYE] = ItemSpiderEye::class.java //375
                list!![FERMENTED_SPIDER_EYE] = ItemSpiderEyeFermented::class.java //376
                list!![BLAZE_POWDER] = ItemBlazePowder::class.java //377
                list!![MAGMA_CREAM] = ItemMagmaCream::class.java //378
                list!![BREWING_STAND] = ItemBrewingStand::class.java //379
                list!![CAULDRON] = ItemCauldron::class.java //380
                list!![ENDER_EYE] = ItemEnderEye::class.java //381
                list!![GLISTERING_MELON] = ItemMelonGlistering::class.java //382
                list!![SPAWN_EGG] = ItemSpawnEgg::class.java //383
                list!![EXPERIENCE_BOTTLE] = ItemExpBottle::class.java //384
                list!![FIRE_CHARGE] = ItemFireCharge::class.java //385
                list!![BOOK_AND_QUILL] = ItemBookAndQuill::class.java //386
                list!![WRITTEN_BOOK] = ItemBookWritten::class.java //387
                list!![EMERALD] = ItemEmerald::class.java //388
                list!![ITEM_FRAME] = ItemItemFrame::class.java //389
                list!![FLOWER_POT] = ItemFlowerPot::class.java //390
                list!![CARROT] = ItemCarrot::class.java //391
                list!![POTATO] = ItemPotato::class.java //392
                list!![BAKED_POTATO] = ItemPotatoBaked::class.java //393
                list!![POISONOUS_POTATO] = ItemPotatoPoisonous::class.java //394
                list!![EMPTY_MAP] = ItemEmptyMap::class.java //395
                list!![GOLDEN_CARROT] = ItemCarrotGolden::class.java //396
                list!![SKULL] = ItemSkull::class.java //397
                list!![CARROT_ON_A_STICK] = ItemCarrotOnAStick::class.java //398
                list!![NETHER_STAR] = ItemNetherStar::class.java //399
                list!![PUMPKIN_PIE] = ItemPumpkinPie::class.java //400
                list!![FIREWORKS] = ItemFirework::class.java //401
                list!![ENCHANTED_BOOK] = ItemBookEnchanted::class.java //403
                list!![COMPARATOR] = ItemRedstoneComparator::class.java //404
                list!![NETHER_BRICK] = ItemNetherBrick::class.java //405
                list!![QUARTZ] = ItemQuartz::class.java //406
                list!![MINECART_WITH_TNT] = ItemMinecartTNT::class.java //407
                list!![MINECART_WITH_HOPPER] = ItemMinecartHopper::class.java //408
                list!![PRISMARINE_SHARD] = ItemPrismarineShard::class.java //409
                list!![HOPPER] = ItemHopper::class.java
                list!![RAW_RABBIT] = ItemRabbitRaw::class.java //411
                list!![COOKED_RABBIT] = ItemRabbitCooked::class.java //412
                list!![RABBIT_STEW] = ItemRabbitStew::class.java //413
                list!![RABBIT_FOOT] = ItemRabbitFoot::class.java //414
                list!![RABBIT_HIDE] = ItemRabbitHide::class.java //415
                list!![LEATHER_HORSE_ARMOR] = ItemHorseArmorLeather::class.java //416
                list!![IRON_HORSE_ARMOR] = ItemHorseArmorIron::class.java //417
                list!![GOLD_HORSE_ARMOR] = ItemHorseArmorGold::class.java //418
                list!![DIAMOND_HORSE_ARMOR] = ItemHorseArmorDiamond::class.java //419
                list!![LEAD] = ItemLead::class.java //420
                list!![NAME_TAG] = ItemNameTag::class.java //421
                list!![PRISMARINE_CRYSTALS] = ItemPrismarineCrystals::class.java //422
                list!![RAW_MUTTON] = ItemMuttonRaw::class.java //423
                list!![COOKED_MUTTON] = ItemMuttonCooked::class.java //424
                list!![ARMOR_STAND] = ItemArmorStand::class.java //425
                list!![END_CRYSTAL] = ItemEndCrystal::class.java //426
                list!![SPRUCE_DOOR] = ItemDoorSpruce::class.java //427
                list!![BIRCH_DOOR] = ItemDoorBirch::class.java //428
                list!![JUNGLE_DOOR] = ItemDoorJungle::class.java //429
                list!![ACACIA_DOOR] = ItemDoorAcacia::class.java //430
                list!![DARK_OAK_DOOR] = ItemDoorDarkOak::class.java //431
                list!![CHORUS_FRUIT] = ItemChorusFruit::class.java //432
                list!![POPPED_CHORUS_FRUIT] = ItemChorusFruitPopped::class.java //433
                list!![BANNER_PATTERN] = ItemBannerPattern::class.java //434
                list!![DRAGON_BREATH] = ItemDragonBreath::class.java //437
                list!![SPLASH_POTION] = ItemPotionSplash::class.java //438
                list!![LINGERING_POTION] = ItemPotionLingering::class.java //441
                list!![ELYTRA] = ItemElytra::class.java //444
                list!![SHULKER_SHELL] = ItemShulkerShell::class.java //445
                list!![BANNER] = ItemBanner::class.java //446
                list!![TOTEM] = ItemTotem::class.java //450
                list!![IRON_NUGGET] = ItemNuggetIron::class.java //452
                list!![TRIDENT] = ItemTrident::class.java //455
                list!![BEETROOT] = ItemBeetroot::class.java //457
                list!![BEETROOT_SEEDS] = ItemSeedsBeetroot::class.java //458
                list!![BEETROOT_SOUP] = ItemBeetrootSoup::class.java //459
                list!![RAW_SALMON] = ItemSalmon::class.java //460
                list!![CLOWNFISH] = ItemClownfish::class.java //461
                list!![PUFFERFISH] = ItemPufferfish::class.java //462
                list!![COOKED_SALMON] = ItemSalmonCooked::class.java //463
                list!![DRIED_KELP] = ItemDriedKelp::class.java //464
                list!![GOLDEN_APPLE_ENCHANTED] = ItemAppleGoldEnchanted::class.java //466
                list!![TURTLE_SHELL] = ItemTurtleShell::class.java //469
                list!![CROSSBOW] = ItemCrossbow::class.java //471
                list!![SPRUCE_SIGN] = ItemSpruceSign::class.java //472
                list!![BIRCH_SIGN] = ItemBirchSign::class.java //473
                list!![JUNGLE_SIGN] = ItemJungleSign::class.java //474
                list!![ACACIA_SIGN] = ItemAcaciaSign::class.java //475
                list!![DARKOAK_SIGN] = ItemDarkOakSign::class.java //476
                list!![SWEET_BERRIES] = ItemSweetBerries::class.java //477
                list!![RECORD_13] = ItemRecord13::class.java //500
                list!![RECORD_CAT] = ItemRecordCat::class.java //501
                list!![RECORD_BLOCKS] = ItemRecordBlocks::class.java //502
                list!![RECORD_CHIRP] = ItemRecordChirp::class.java //503
                list!![RECORD_FAR] = ItemRecordFar::class.java //504
                list!![RECORD_MALL] = ItemRecordMall::class.java //505
                list!![RECORD_MELLOHI] = ItemRecordMellohi::class.java //506
                list!![RECORD_STAL] = ItemRecordStal::class.java //507
                list!![RECORD_STRAD] = ItemRecordStrad::class.java //508
                list!![RECORD_WARD] = ItemRecordWard::class.java //509
                list!![RECORD_11] = ItemRecord11::class.java //510
                list!![RECORD_WAIT] = ItemRecordWait::class.java //511
                list!![SHIELD] = ItemShield::class.java //513
                list!![CAMPFIRE] = ItemCampfire::class.java //720
                list!![SUSPICIOUS_STEW] = ItemSuspiciousStew::class.java //734
                list!![HONEYCOMB] = ItemHoneycomb::class.java //736
                list!![HONEY_BOTTLE] = ItemHoneyBottle::class.java //737
                list!![LODESTONECOMPASS] = ItemCompassLodestone::class.java //741;
                list!![NETHERITE_INGOT] = ItemIngotNetherite::class.java //742
                list!![NETHERITE_SWORD] = ItemSwordNetherite::class.java //743
                list!![NETHERITE_SHOVEL] = ItemShovelNetherite::class.java //744
                list!![NETHERITE_PICKAXE] = ItemPickaxeNetherite::class.java //745
                list!![NETHERITE_AXE] = ItemAxeNetherite::class.java //746
                list!![NETHERITE_HOE] = ItemHoeNetherite::class.java //747
                list!![NETHERITE_HELMET] = ItemHelmetNetherite::class.java //748
                list!![NETHERITE_CHESTPLATE] = ItemChestplateNetherite::class.java //749
                list!![NETHERITE_LEGGINGS] = ItemLeggingsNetherite::class.java //750
                list!![NETHERITE_BOOTS] = ItemBootsNetherite::class.java //751
                list!![NETHERITE_SCRAP] = ItemScrapNetherite::class.java //752
                list!![CRIMSON_SIGN] = ItemCrimsonSign::class.java //753
                list!![WARPED_SIGN] = ItemWarpedSign::class.java //754
                list!![CRIMSON_DOOR] = ItemDoorCrimson::class.java //755
                list!![WARPED_DOOR] = ItemDoorWarped::class.java //756
                list!![WARPED_FUNGUS_ON_A_STICK] = ItemWarpedFungusOnAStick::class.java //757
                list!![CHAIN] = ItemChain::class.java //758
                list!![RECORD_PIGSTEP] = ItemRecordPigstep::class.java //759
                list!![NETHER_SPROUTS] = ItemNetherSprouts::class.java //760
                list!![SOUL_CAMPFIRE] = ItemCampfireSoul::class.java //801
                for (i in 0..255) {
                    if (Block.list.get(i) != null) {
                        list!![i] = Block.list.get(i)
                    }
                }
            }
            initCreativeItems()
        }

        private var itemList: List<String>? = null
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun rebuildItemList(): List<String> {
            return Collections.unmodifiableList(Stream.of(
                    BlockStateRegistry.getPersistenceNames().stream()
                            .map { name -> name.substring(name.indexOf(':') + 1) },
                    itemIds.keySet().stream()
            ).flatMap(Function.identity()).distinct().collect(Collectors.toList())).also { itemList = it }
        }

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun getItemList(): List<String> {
            return itemList ?: return rebuildItemList()
        }

        private val creative: ArrayList<Item> = ArrayList()
        @SneakyThrows(IOException::class)
        @SuppressWarnings("unchecked")
        private fun initCreativeItems() {
            clearCreativeItems()
            val config = Config(Config.JSON)
            Server::class.java.getClassLoader().getResourceAsStream("creativeitems.json").use { resourceAsStream -> config.load(resourceAsStream) }
            val list: List<Map> = config.getMapList("items")
            for (map in list) {
                try {
                    val item = loadCreativeItemEntry(map)
                    if (item != null) {
                        addCreativeItem(item)
                    }
                } catch (e: Exception) {
                    log.error("Error while registering a creative item", e)
                }
            }
        }

        private fun loadCreativeItemEntry(data: Map<String, Object>): Item? {
            val nbt = data["nbt_b64"] as String?
            val nbtBytes: ByteArray = if (nbt != null) Base64.getDecoder().decode(nbt) else EmptyArrays.EMPTY_BYTES
            val id: String = data["id"].toString()
            var item: Item? = null
            if (data.containsKey("damage")) {
                val meta: Int = Utils.toInt(data["damage"])
                item = fromString("$id:$meta")
            } else if (data.containsKey("blockRuntimeId")) {
                val blockId: Integer = BlockStateRegistry.getBlockId(id)
                if (blockId == null || blockId > BlockID.QUARTZ_BRICKS) { //TODO Remove this after the support is added
                    return null
                }
                var blockRuntimeId = -1
                try {
                    blockRuntimeId = (data["blockRuntimeId"] as Number?).intValue()
                    val blockState: BlockState = BlockStateRegistry.getBlockStateByRuntimeId(blockRuntimeId)
                    if (blockState != null) {
                        item = blockState.asItemBlock()
                    } else {
                        log.warn("Block state not found for the creative item {} with runtimeId {}", id, blockRuntimeId)
                    }
                } catch (e: Throwable) {
                    log.error("Error loading the creative item {} with runtimeId {}", id, blockRuntimeId, e)
                    return null
                }
            }
            if (item == null) {
                item = fromString(id)
            }
            item.setCompoundTag(nbtBytes)
            return item
        }

        fun clearCreativeItems() {
            creative.clear()
        }

        fun getCreativeItems(): ArrayList<Item> {
            return ArrayList(creative)
        }

        fun addCreativeItem(item: Item) {
            creative.add(item.clone())
        }

        fun removeCreativeItem(item: Item) {
            val index = getCreativeItemIndex(item)
            if (index != -1) {
                creative.remove(index)
            }
        }

        fun isCreativeItem(item: Item): Boolean {
            for (aCreative in creative) {
                if (item.equals(aCreative, !item.isTool())) {
                    return true
                }
            }
            return false
        }

        fun getCreativeItem(index: Int): Item? {
            return if (index >= 0 && index < creative.size()) creative.get(index) else null
        }

        fun getCreativeItemIndex(item: Item): Int {
            for (i in 0 until creative.size()) {
                if (item.equals(creative.get(i), !item.isTool())) {
                    return i
                }
            }
            return -1
        }

        fun getBlock(id: Int): Item? {
            return getBlock(id, 0)
        }

        fun getBlock(id: Int, meta: Integer): Item? {
            return getBlock(id, meta, 1)
        }

        fun getBlock(id: Int, meta: Integer, count: Int): Item? {
            return getBlock(id, meta, count, EmptyArrays.EMPTY_BYTES)
        }

        fun getBlock(id: Int, meta: Integer, count: Int, tags: ByteArray): Item? {
            var id = id
            if (id > 255) {
                id = 255 - id
            }
            return Companion[id, meta, count, tags]
        }

        @JvmOverloads
        operator fun get(id: Int, meta: Integer = 0): Item? {
            return Companion[id, meta, 1]
        }

        operator fun get(id: Int, meta: Integer, count: Int): Item? {
            return Companion[id, meta, count, EmptyArrays.EMPTY_BYTES]
        }

        @PowerNukkitDifference(info = "Prevents players from getting invalid items by limiting the return to the maximum damage defined in Block.getMaxItemDamage()", since = "1.4.0.0-PN")
        operator fun get(id: Int, meta: Integer, count: Int, tags: ByteArray): Item? {
            return try {
                var c: Class? = null
                c = if (id < 0) {
                    val blockId = 255 - id
                    Block.list.get(blockId)
                } else {
                    list!![id]
                }
                var item: Item?
                if (id < 256) {
                    val blockId = if (id < 0) 255 - id else id
                    if (meta == 0) {
                        item = ItemBlock(Block.get(blockId), 0, count)
                    } else if (meta == -1) {
                        // Special case for item instances used in fuzzy recipes
                        item = ItemBlock(Block.get(blockId), -1)
                    } else {
                        val state: BlockState = BlockState.of(blockId, meta)
                        try {
                            state.validate()
                            item = state.asItemBlock(count)
                        } catch (e: InvalidBlockPropertyMetaException) {
                            log.warn("Attempted to get an ItemBlock with invalid block state in memory: {}, trying to repair the block state...", state)
                            log.catching(org.apache.logging.log4j.Level.DEBUG, e)
                            val repaired: Block = state.getBlockRepairing(null, 0, 0, 0)
                            item = repaired.asItemBlock(count)
                            log.error("Attempted to get an illegal item block {}:{} ({}), the meta was changed to {}",
                                    id, meta, blockId, item.getDamage(), e)
                        } catch (e: InvalidBlockStateException) {
                            log.warn("Attempted to get an ItemBlock with invalid block state in memory: {}, trying to repair the block state...", state)
                            log.catching(org.apache.logging.log4j.Level.DEBUG, e)
                            val repaired: Block = state.getBlockRepairing(null, 0, 0, 0)
                            item = repaired.asItemBlock(count)
                            log.error("Attempted to get an illegal item block {}:{} ({}), the meta was changed to {}",
                                    id, meta, blockId, item.getDamage(), e)
                        } catch (e: UnknownRuntimeIdException) {
                            log.warn("Attempted to get an illegal item block {}:{} ({}), the runtime id was unknown and the meta was changed to 0",
                                    id, meta, blockId, e)
                            item = BlockState.of(id).asItemBlock(count)
                        }
                    }
                } else if (c == null) {
                    item = Item(id, meta, count)
                } else {
                    item = if (meta == -1) {
                        (c.getConstructor(Integer::class.java, Int::class.javaPrimitiveType).newInstance(0, count) as Item).createFuzzyCraftingRecipe()
                    } else {
                        c.getConstructor(Integer::class.java, Int::class.javaPrimitiveType).newInstance(meta, count)
                    }
                }
                if (tags.size != 0) {
                    item.setCompoundTag(tags)
                }
                item
            } catch (e: Exception) {
                log.error("Error getting the item {}:{}{}! Returning an unsafe item stack!",
                        id, meta, if (id < 0) " (" + (255 - id) + ")" else "", e)
                Item(id, meta, count).setCompoundTag(tags)
            }
        }

        @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Improve namespaced name handling and allows to get custom blocks by name")
        fun fromString(str: String): Item? {
            val normalized: String = str.trim().replace(' ', '_').toLowerCase()
            val matcher: Matcher = ITEM_STRING_PATTERN.matcher(normalized)
            if (!matcher.matches()) {
                return get(AIR)
            }
            val name: String = matcher.group(2)
            var meta: OptionalInt = OptionalInt.empty()
            val metaGroup: String
            metaGroup = if (name != null) {
                matcher.group(3)
            } else {
                matcher.group(5)
            }
            if (metaGroup != null) {
                meta = OptionalInt.of(Short.parseShort(metaGroup))
            }
            val numericIdGroup: String = matcher.group(4)
            if (name != null) {
                val namespaceGroup: String = matcher.group(1)
                val namespacedId: String
                namespacedId = if (namespaceGroup != null) {
                    "$namespaceGroup:$name"
                } else {
                    "minecraft:$name"
                }
                val minecraftItemId: MinecraftItemID = MinecraftItemID.getByNamespaceId(namespacedId)
                if (minecraftItemId != null) {
                    var item: Item? = minecraftItemId.get(1)
                    if (meta.isPresent()) {
                        val damage: Int = meta.getAsInt()
                        if (damage < 0) {
                            item = item!!.createFuzzyCraftingRecipe()
                        } else {
                            item!!.setDamage(damage)
                        }
                    }
                    return item
                } else if (namespaceGroup != null && !namespaceGroup.equals("minecraft:")) {
                    return get(AIR)
                }
            } else if (numericIdGroup != null) {
                val id: Int = Integer.parseInt(numericIdGroup)
                return Companion[id, meta.orElse(0)]
            }
            if (name == null) {
                return get(AIR)
            }
            var id = 0
            try {
                id = ItemID::class.java.getField(name.toUpperCase()).getInt(null)
            } catch (ignore1: Exception) {
                try {
                    id = BlockID::class.java.getField(name.toUpperCase()).getInt(null)
                    if (id > 255) {
                        id = 255 - id
                    }
                } catch (ignore2: Exception) {
                }
            }
            return Companion[id, meta.orElse(0)]
        }

        fun fromJson(data: Map<String, Object>): Item? {
            return fromJson(data, false)
        }

        private fun fromJson(data: Map<String, Object>, ignoreNegativeItemId: Boolean): Item? {
            var nbt = data["nbt_b64"] as String?
            val nbtBytes: ByteArray
            if (nbt != null) {
                nbtBytes = Base64.getDecoder().decode(nbt)
            } else { // Support old format for backwards compat
                nbt = data.getOrDefault("nbt_hex", null) as String?
                nbtBytes = if (nbt == null) {
                    EmptyArrays.EMPTY_BYTES
                } else {
                    Utils.parseHexBinary(nbt)
                }
            }
            val id: Int = Utils.toInt(data["id"])
            return if (ignoreNegativeItemId && id < 0) null else Companion[id, Utils.toInt(data.getOrDefault("damage", 0)), Utils.toInt(data.getOrDefault("count", 1)), nbtBytes]
        }

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun fromJsonNetworkId(data: Map<String?, Object?>): Item? {
            var nbt = data["nbt_b64"] as String?
            val nbtBytes: ByteArray
            if (nbt != null) {
                nbtBytes = Base64.getDecoder().decode(nbt)
            } else { // Support old format for backwards compat
                nbt = data.getOrDefault("nbt_hex", null) as String?
                nbtBytes = if (nbt == null) {
                    EmptyArrays.EMPTY_BYTES
                } else {
                    Utils.parseHexBinary(nbt)
                }
            }
            val networkId: Int = Utils.toInt(data["id"])
            val mapping: RuntimeItemMapping = RuntimeItems.getRuntimeMapping()
            val legacyFullId: Int = mapping!!.getLegacyFullId(networkId)
            val id: Int = RuntimeItems.getId(legacyFullId)
            var meta: OptionalInt = if (RuntimeItems.hasData(legacyFullId)) OptionalInt.of(RuntimeItems.getData(legacyFullId)) else OptionalInt.empty()
            if (data.containsKey("damage")) {
                val jsonMeta: Int = Utils.toInt(data["damage"])
                if (jsonMeta != Short.MAX_VALUE) {
                    if (meta.isPresent() && jsonMeta != meta.getAsInt()) {
                        throw IllegalArgumentException(
                                "Conflicting damage value for " + mapping!!.getNamespacedIdByNetworkId(networkId).toString() + ". " +
                                        "From json: " + jsonMeta.toString() + ", from mapping: " + meta.getAsInt()
                        )
                    }
                    meta = OptionalInt.of(jsonMeta)
                } else if (!meta.isPresent()) {
                    meta = OptionalInt.of(-1)
                }
            }
            return Companion[id, meta.orElse(0), Utils.toInt(data.getOrDefault("count", 1)), nbtBytes]
        }

        fun fromStringMultiple(str: String): Array<Item?> {
            val b: Array<String> = str.split(",")
            val items = arrayOfNulls<Item>(b.size - 1)
            for (i in b.indices) {
                items[i] = fromString(b[i])
            }
            return items
        }

        fun parseCompoundTag(tag: ByteArray?): CompoundTag {
            return try {
                NBTIO.read(tag, ByteOrder.LITTLE_ENDIAN)
            } catch (e: IOException) {
                throw UncheckedIOException(e)
            }
        }
    }

    init {
        //this.id = id & 0xffff;
        if (meta != null && meta >= 0) {
            this.meta = meta and 0xffff
        } else {
            hasMeta = false
        }
        this.count = count
        this.name = name?.intern()
        /*f (this.block != null && this.id <= 0xff && Block.list[id] != null) { //probably useless
            this.block = Block.get(this.id, this.meta);
            this.name = this.block.getName();
        }*/
    }
}