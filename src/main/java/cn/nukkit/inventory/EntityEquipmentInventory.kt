package cn.nukkit.inventory

import cn.nukkit.Player

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class EntityEquipmentInventory @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(entity: Entity) : BaseInventory(entity as InventoryHolder, InventoryType.ENTITY_EQUIPMENT) {
    private val entity: Entity
    @Override
    fun getName(): String {
        return "Entity Equipment"
    }

    @Override
    fun getSize(): Int {
        return 2
    }

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
    override fun sendSlot(index: Int, vararg players: Player) {
        for (player in players) {
            this.sendSlot(index, player)
        }
    }

    @Override
    override fun sendSlot(index: Int, player: Player) {
        val mobEquipmentPacket = MobEquipmentPacket()
        mobEquipmentPacket.eid = entity.getId()
        mobEquipmentPacket.hotbarSlot = index
        mobEquipmentPacket.inventorySlot = mobEquipmentPacket.hotbarSlot
        mobEquipmentPacket.item = this.getItem(index)
        player.dataPacket(mobEquipmentPacket)
    }

    @Override
    override fun getViewers(): Set<Player> {
        val viewers: Set<Player> = HashSet(this.viewers)
        viewers.addAll(entity.getViewers().values())
        return viewers
    }

    @Override
    override fun open(who: Player?): Boolean {
        return this.viewers.add(who)
    }

    @Override
    override fun onClose(who: Player?) {
        this.viewers.remove(who)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getItemInHand(): Item {
        return this.getItem(MAIN_HAND)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getItemInOffhand(): Item {
        return this.getItem(OFFHAND)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setItemInHand(item: Item?, send: Boolean): Boolean {
        return this.setItem(MAIN_HAND, item, send)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setItemInOffhand(item: Item?, send: Boolean): Boolean {
        return this.setItem(OFFHAND, item, send)
    }

    @Override
    override fun sendContents(target: Player) {
        this.sendSlot(MAIN_HAND, target)
        this.sendSlot(OFFHAND, target)
    }

    @Override
    override fun sendContents(vararg target: Player) {
        for (player in target) {
            this.sendContents(player)
        }
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val MAIN_HAND = 0

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val OFFHAND = 1
    }

    /**
     * @param entity an Entity which implements [InventoryHolder].
     * @throws ClassCastException if the entity does not implements [InventoryHolder]
     */
    init {
        this.entity = entity
    }
}