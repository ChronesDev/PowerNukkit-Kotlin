package cn.nukkit.entity

import cn.nukkit.Player

abstract class EntityHumanType(chunk: FullChunk?, nbt: CompoundTag?) : EntityCreature(chunk, nbt), InventoryHolder {
    protected var inventory: PlayerInventory? = null
    protected var enderChestInventory: PlayerEnderChestInventory? = null
    protected var offhandInventory: PlayerOffhandInventory? = null
    @Override
    fun getInventory(): PlayerInventory? {
        return inventory
    }

    fun getEnderChestInventory(): PlayerEnderChestInventory? {
        return enderChestInventory
    }

    fun getOffhandInventory(): PlayerOffhandInventory? {
        return offhandInventory
    }

    @Override
    protected override fun initEntity() {
        inventory = PlayerInventory(this)
        if (namedTag.containsNumber("SelectedInventorySlot")) {
            inventory.setHeldItemSlot(NukkitMath.clamp(this.namedTag.getInt("SelectedInventorySlot"), 0, 8))
        }
        offhandInventory = PlayerOffhandInventory(this)
        if (this.namedTag.contains("Inventory") && this.namedTag.get("Inventory") is ListTag) {
            val inventoryList: ListTag<CompoundTag> = this.namedTag.getList("Inventory", CompoundTag::class.java)
            for (item in inventoryList.getAll()) {
                val slot: Int = item.getByte("Slot")
                if (slot >= 0 && slot < 9) { //hotbar
                    //Old hotbar saving stuff, remove it (useless now)
                    inventoryList.remove(item)
                } else if (slot >= 100 && slot < 104) {
                    inventory.setItem(inventory.getSize() + slot - 100, NBTIO.getItemHelper(item))
                } else if (slot == -106) {
                    offhandInventory.setItem(0, NBTIO.getItemHelper(item))
                } else {
                    inventory.setItem(slot - 9, NBTIO.getItemHelper(item))
                }
            }
        }
        enderChestInventory = PlayerEnderChestInventory(this)
        if (this.namedTag.contains("EnderItems") && this.namedTag.get("EnderItems") is ListTag) {
            val inventoryList: ListTag<CompoundTag> = this.namedTag.getList("EnderItems", CompoundTag::class.java)
            for (item in inventoryList.getAll()) {
                enderChestInventory.setItem(item.getByte("Slot"), NBTIO.getItemHelper(item))
            }
        }
        super.initEntity()
    }

    @Override
    override fun saveNBT() {
        super.saveNBT()
        var inventoryTag: ListTag<CompoundTag?>? = null
        if (inventory != null) {
            inventoryTag = ListTag("Inventory")
            this.namedTag.putList(inventoryTag)
            for (slot in 0..8) {
                inventoryTag.add(CompoundTag()
                        .putByte("Count", 0)
                        .putShort("Damage", 0)
                        .putByte("Slot", slot)
                        .putByte("TrueSlot", -1)
                        .putShort("id", 0)
                )
            }
            val slotCount: Int = Player.SURVIVAL_SLOTS + 9
            for (slot in 9 until slotCount) {
                val item: Item = inventory.getItem(slot - 9)
                inventoryTag.add(NBTIO.putItemHelper(item, slot))
            }
            for (slot in 100..103) {
                val item: Item = inventory.getItem(inventory.getSize() + slot - 100)
                if (item != null && item.getId() !== Item.AIR) {
                    inventoryTag.add(NBTIO.putItemHelper(item, slot))
                }
            }
            this.namedTag.putInt("SelectedInventorySlot", inventory.getHeldItemIndex())
        }
        if (offhandInventory != null) {
            val item: Item = offhandInventory.getItem(0)
            if (item.getId() !== Item.AIR) {
                if (inventoryTag == null) {
                    inventoryTag = ListTag("Inventory")
                    this.namedTag.putList(inventoryTag)
                }
                inventoryTag.add(NBTIO.putItemHelper(item, -106))
            }
        }
        this.namedTag.putList(ListTag<CompoundTag>("EnderItems"))
        if (enderChestInventory != null) {
            for (slot in 0..26) {
                val item: Item = enderChestInventory.getItem(slot)
                if (item != null && item.getId() !== Item.AIR) {
                    this.namedTag.getList("EnderItems", CompoundTag::class.java).add(NBTIO.putItemHelper(item, slot))
                }
            }
        }
    }

    @get:Override
    override val drops: Array<Any>
        get() {
            if (inventory != null) {
                val drops: List<Item> = ArrayList(inventory.getContents().values())
                drops.addAll(offhandInventory.getContents().values())
                return drops.toArray(Item.EMPTY_ARRAY)
            }
            return Item.EMPTY_ARRAY
        }

    @Override
    override fun attack(source: EntityDamageEvent): Boolean {
        if (this.isClosed() || !this.isAlive()) {
            return false
        }
        if (source.getCause() !== DamageCause.VOID && source.getCause() !== DamageCause.CUSTOM && source.getCause() !== DamageCause.MAGIC) {
            var armorPoints = 0
            var epf = 0
            val toughness = 0
            for (armor in inventory.getArmorContents()) {
                armorPoints += armor.getArmorPoints()
                epf += calculateEnchantmentProtectionFactor(armor, source).toInt()
                //toughness += armor.getToughness();
            }
            if (source.canBeReducedByArmor()) {
                source.setDamage(-source.getFinalDamage() * armorPoints * 0.04f, DamageModifier.ARMOR)
            }
            source.setDamage(-source.getFinalDamage() * Math.min(NukkitMath.ceilFloat(Math.min(epf, 25) * (ThreadLocalRandom.current().nextInt(50, 100) as Float / 100)), 20) * 0.04f,
                    DamageModifier.ARMOR_ENCHANTMENTS)
            source.setDamage(-Math.min(this.getAbsorption(), source.getFinalDamage()), DamageModifier.ABSORPTION)
        }
        return if (super.attack(source)) {
            var damager: Entity? = null
            if (source is EntityDamageByEntityEvent) {
                damager = (source as EntityDamageByEntityEvent).getDamager()
            }
            for (slot in 0..3) {
                val armor: Item = damageArmor(inventory.getArmorItem(slot), damager)
                inventory.setArmorItem(slot, armor, armor.getId() !== BlockID.AIR)
            }
            true
        } else {
            false
        }
    }

    protected fun calculateEnchantmentProtectionFactor(item: Item, source: EntityDamageEvent?): Double {
        if (!item.hasEnchantments()) {
            return 0
        }
        var epf = 0.0
        for (ench in item.getEnchantments()) {
            epf += ench.getProtectionFactor(source)
        }
        return epf
    }

    @Override
    override fun setOnFire(seconds: Int) {
        var seconds = seconds
        var level = 0
        for (armor in inventory.getArmorContents()) {
            val fireProtection: Enchantment = armor.getEnchantment(Enchantment.ID_PROTECTION_FIRE)
            if (fireProtection != null && fireProtection.getLevel() > 0) {
                level = Math.max(level, fireProtection.getLevel())
            }
        }
        seconds = (seconds * (1 - level * 0.15)).toInt()
        super.setOnFire(seconds)
    }

    @Override
    protected override fun applyNameTag(@Nonnull player: Player?, @Nonnull item: Item?): Boolean {
        return false
    }

    @PowerNukkitOnly
    @Deprecated
    @Override
    override fun applyNameTag(item: Item?): Boolean {
        return false
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun damageArmor(armor: Item, damager: Entity?): Item {
        if (armor.hasEnchantments()) {
            if (damager != null) {
                for (enchantment in armor.getEnchantments()) {
                    enchantment.doPostAttack(damager, this)
                }
            }
            val durability: Enchantment = armor.getEnchantment(Enchantment.ID_DURABILITY)
            if (durability != null && durability.getLevel() > 0 && 100 / (durability.getLevel() + 1) <= Utils.random.nextInt(100)) {
                return armor
            }
        }
        if (armor.isUnbreakable() || armor.getMaxDurability() < 0) {
            return armor
        }
        armor.setDamage(armor.getDamage() + 1)
        if (armor.getDamage() >= armor.getMaxDurability()) {
            getLevel().addSound(this, Sound.RANDOM_BREAK)
            return Item.get(BlockID.AIR, 0, 0)
        }
        return armor
    }
}