package cn.nukkit.inventory

import cn.nukkit.Player

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class EntityArmorInventory @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(entity: Entity) : BaseInventory(entity as InventoryHolder, InventoryType.ENTITY_ARMOR) {
    private val entity: Entity
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getEntity(): Entity {
        return entity
    }

    @Override
    override fun getHolder(): InventoryHolder {
        return this.holder!!
    }

    @Override
    fun getName(): String {
        return "Entity Armor"
    }

    @Override
    fun getSize(): Int {
        return 4
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getHelmet(): Item {
        return this.getItem(SLOT_HEAD)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getChestplate(): Item {
        return this.getItem(SLOT_CHEST)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getLeggings(): Item {
        return this.getItem(SLOT_LEGS)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getBoots(): Item {
        return this.getItem(SLOT_FEET)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setHelmet(item: Item?) {
        this.setItem(SLOT_CHEST, item)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setChestplate(item: Item?) {
        this.setItem(SLOT_CHEST, item)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setLeggings(item: Item?) {
        this.setItem(SLOT_LEGS, item)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setBoots(item: Item?) {
        this.setItem(SLOT_FEET, item)
    }

    @Override
    override fun sendSlot(index: Int, vararg players: Player) {
        for (player in players) {
            this.sendSlot(index, player)
        }
    }

    @Override
    override fun sendSlot(index: Int, player: Player) {
        val mobArmorEquipmentPacket = MobArmorEquipmentPacket()
        mobArmorEquipmentPacket.eid = entity.getId()
        mobArmorEquipmentPacket.slots = arrayOf<Item>(getHelmet(), getChestplate(), getLeggings(), getBoots())
        if (player === this.holder) {
            val inventorySlotPacket = InventorySlotPacket()
            inventorySlotPacket.inventoryId = player.getWindowId(this)
            inventorySlotPacket.slot = index
            inventorySlotPacket.item = this.getItem(index)
            player.dataPacket(inventorySlotPacket)
        } else {
            player.dataPacket(mobArmorEquipmentPacket)
        }
    }

    @Override
    override fun sendContents(vararg players: Player) {
        for (player in players) {
            this.sendContents(player)
        }
    }

    @Override
    override fun sendContents(player: Player) {
        val mobArmorEquipmentPacket = MobArmorEquipmentPacket()
        mobArmorEquipmentPacket.eid = entity.getId()
        mobArmorEquipmentPacket.slots = arrayOf<Item>(getHelmet(), getChestplate(), getLeggings(), getBoots())
        if (player === this.holder) {
            val inventoryContentPacket = InventoryContentPacket()
            inventoryContentPacket.inventoryId = player.getWindowId(this)
            inventoryContentPacket.slots = arrayOf<Item>(getHelmet(), getChestplate(), getLeggings(), getBoots())
            player.dataPacket(inventoryContentPacket)
        } else {
            player.dataPacket(mobArmorEquipmentPacket)
        }
    }

    @Override
    override fun onOpen(who: Player?) {
        this.viewers.add(who)
    }

    @Override
    override fun onClose(who: Player?) {
        this.viewers.remove(who)
    }

    @Override
    override fun getViewers(): Set<Player> {
        val viewers: Set<Player> = HashSet(this.viewers)
        viewers.addAll(entity.getViewers().values())
        return viewers
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val SLOT_HEAD = 0

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val SLOT_CHEST = 1

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val SLOT_LEGS = 2

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val SLOT_FEET = 3
    }

    /**
     * @param entity an Entity which implements [InventoryHolder].
     * @throws ClassCastException if the entity does not implements [InventoryHolder]
     */
    init {
        this.entity = entity
    }
}