package cn.nukkit.inventory

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class PlayerInventory(player: EntityHumanType?) : BaseInventory(player, InventoryType.PLAYER) {
    protected var itemInHandIndex = 0
    private val hotbar: IntArray
    @Override
    fun getSize(): Int {
        return super.getSize() - 4
    }

    @Override
    fun setSize(size: Int) {
        super.setSize(size + 4)
        this.sendContents(this.getViewers())
    }

    /**
     * Called when a client equips a hotbar inventorySlot. This method should not be used by plugins.
     * This method will call PlayerItemHeldEvent.
     *
     * @param slot hotbar slot Number of the hotbar slot to equip.
     * @return boolean if the equipment change was successful, false if not.
     */
    fun equipItem(slot: Int): Boolean {
        if (!isHotbarSlot(slot)) {
            this.sendContents(getHolder() as Player?)
            return false
        }
        if (getHolder() is Player) {
            val player: Player? = getHolder() as Player?
            val ev = PlayerItemHeldEvent(player, this.getItem(slot), slot)
            getHolder().getLevel().getServer().getPluginManager().callEvent(ev)
            if (ev.isCancelled()) {
                this.sendContents(this.getViewers())
                return false
            }
            if (player.fishing != null) {
                if (!this.getItem(slot).equals(player.fishing.rod)) {
                    player.stopFishing(false)
                }
            }
        }
        this.setHeldItemIndex(slot, false)
        return true
    }

    private fun isHotbarSlot(slot: Int): Boolean {
        return slot >= 0 && slot <= getHotbarSize()
    }

    @Deprecated
    fun getHotbarSlotIndex(index: Int): Int {
        return index
    }

    @Deprecated
    fun setHotbarSlotIndex(index: Int, slot: Int) {
    }

    fun getHeldItemIndex(): Int {
        return itemInHandIndex
    }

    fun setHeldItemIndex(index: Int) {
        setHeldItemIndex(index, true)
    }

    fun setHeldItemIndex(index: Int, send: Boolean) {
        if (index >= 0 && index < getHotbarSize()) {
            itemInHandIndex = index
            if (getHolder() is Player && send) {
                this.sendHeldItem(getHolder() as Player?)
            }
            this.sendHeldItem(getHolder().getViewers().values())
        }
    }

    fun getItemInHand(): Item {
        val item: Item = this.getItem(getHeldItemIndex())
        return if (item != null) {
            item
        } else {
            ItemBlock(Block.get(BlockID.AIR), 0, 0)
        }
    }

    fun setItemInHand(item: Item?): Boolean {
        return this.setItem(getHeldItemIndex(), item)
    }

    @Deprecated
    fun getHeldItemSlot(): Int {
        return itemInHandIndex
    }

    fun setHeldItemSlot(slot: Int) {
        if (!isHotbarSlot(slot)) {
            return
        }
        itemInHandIndex = slot
        if (getHolder() is Player) {
            this.sendHeldItem(getHolder() as Player?)
        }
        this.sendHeldItem(this.getViewers())
    }

    fun sendHeldItem(vararg players: Player) {
        val item: Item = getItemInHand()
        val pk = MobEquipmentPacket()
        pk.item = item
        pk.hotbarSlot = getHeldItemIndex()
        pk.inventorySlot = pk.hotbarSlot
        for (player in players) {
            pk.eid = getHolder().getId()
            if (player.equals(getHolder())) {
                pk.eid = player.getId()
                this.sendSlot(getHeldItemIndex(), player)
            }
            player.dataPacket(pk)
        }
    }

    fun sendHeldItem(players: Collection<Player?>?) {
        this.sendHeldItem(players.toArray(Player.EMPTY_ARRAY))
    }

    @Override
    override fun onSlotChange(index: Int, before: Item?, send: Boolean) {
        val holder: EntityHuman? = getHolder()
        if (holder is Player && !(holder as Player?).spawned) {
            return
        }
        if (index >= getSize()) {
            this.sendArmorSlot(index, this.getViewers())
            this.sendArmorSlot(index, getHolder().getViewers().values())
        } else {
            super.onSlotChange(index, before, send)
        }
    }

    fun getHotbarSize(): Int {
        return 9
    }

    fun getArmorItem(index: Int): Item {
        return this.getItem(getSize() + index)
    }

    fun setArmorItem(index: Int, item: Item?): Boolean {
        return this.setArmorItem(index, item, false)
    }

    fun setArmorItem(index: Int, item: Item?, ignoreArmorEvents: Boolean): Boolean {
        return this.setItem(getSize() + index, item, ignoreArmorEvents)
    }

    fun getHelmet(): Item {
        return this.getItem(getSize())
    }

    fun getChestplate(): Item {
        return this.getItem(getSize() + 1)
    }

    fun getLeggings(): Item {
        return this.getItem(getSize() + 2)
    }

    fun getBoots(): Item {
        return this.getItem(getSize() + 3)
    }

    fun setHelmet(helmet: Item?): Boolean {
        return this.setItem(getSize(), helmet)
    }

    fun setChestplate(chestplate: Item?): Boolean {
        return this.setItem(getSize() + 1, chestplate)
    }

    fun setLeggings(leggings: Item?): Boolean {
        return this.setItem(getSize() + 2, leggings)
    }

    fun setBoots(boots: Item?): Boolean {
        return this.setItem(getSize() + 3, boots)
    }

    @Override
    override fun setItem(index: Int, item: Item?): Boolean {
        return setItem(index, item, true, false)
    }

    private fun setItem(index: Int, item: Item?, send: Boolean, ignoreArmorEvents: Boolean): Boolean {
        var item: Item? = item
        if (index < 0 || index >= this.size) {
            return false
        } else if (item.getId() === 0 || item.getCount() <= 0) {
            return clear(index)
        }

        //Armor change
        item = if (!ignoreArmorEvents && index >= getSize()) {
            val ev = EntityArmorChangeEvent(getHolder(), this.getItem(index), item, index)
            Server.getInstance().getPluginManager().callEvent(ev)
            if (ev.isCancelled() && getHolder() != null) {
                this.sendArmorSlot(index, this.getViewers())
                return false
            }
            ev.getNewItem()
        } else {
            val ev = EntityInventoryChangeEvent(getHolder(), this.getItem(index), item, index)
            Server.getInstance().getPluginManager().callEvent(ev)
            if (ev.isCancelled()) {
                this.sendSlot(index, this.getViewers())
                return false
            }
            ev.getNewItem()
        }
        val old: Item = this.getItem(index)
        this.slots.put(index, item.clone())
        onSlotChange(index, old, send)
        return true
    }

    @Override
    override fun clear(index: Int, send: Boolean): Boolean {
        if (this.slots.containsKey(index)) {
            var item: Item = ItemBlock(Block.get(BlockID.AIR), null, 0)
            val old: Item = this.slots.get(index)
            item = if (index >= getSize() && index < this.size) {
                val ev = EntityArmorChangeEvent(getHolder(), old, item, index)
                Server.getInstance().getPluginManager().callEvent(ev)
                if (ev.isCancelled()) {
                    if (index >= this.size) {
                        this.sendArmorSlot(index, this.getViewers())
                    } else {
                        this.sendSlot(index, this.getViewers())
                    }
                    return false
                }
                ev.getNewItem()
            } else {
                val ev = EntityInventoryChangeEvent(getHolder(), old, item, index)
                Server.getInstance().getPluginManager().callEvent(ev)
                if (ev.isCancelled()) {
                    if (index >= this.size) {
                        this.sendArmorSlot(index, this.getViewers())
                    } else {
                        this.sendSlot(index, this.getViewers())
                    }
                    return false
                }
                ev.getNewItem()
            }
            if (item.getId() !== Item.AIR) {
                this.slots.put(index, item.clone())
            } else {
                this.slots.remove(index)
            }
            onSlotChange(index, old, send)
        }
        return true
    }

    fun getArmorContents(): Array<Item?> {
        val armor: Array<Item?> = arrayOfNulls<Item>(4)
        for (i in 0..3) {
            armor[i] = this.getItem(getSize() + i)
        }
        return armor
    }

    @Override
    override fun clearAll() {
        val limit = getSize() + 4
        for (index in 0 until limit) {
            clear(index)
        }
        getHolder().getOffhandInventory().clearAll()
    }

    fun sendArmorContents(player: Player) {
        this.sendArmorContents(arrayOf<Player>(player))
    }

    fun sendArmorContents(players: Array<Player>) {
        val armor: Array<Item?> = getArmorContents()
        val pk = MobArmorEquipmentPacket()
        pk.eid = getHolder().getId()
        pk.slots = armor
        pk.tryEncode()
        for (player in players) {
            if (player.equals(getHolder())) {
                val pk2 = InventoryContentPacket()
                pk2.inventoryId = InventoryContentPacket.SPECIAL_ARMOR
                pk2.slots = armor
                player.dataPacket(pk2)
            } else {
                player.dataPacket(pk)
            }
        }
    }

    fun setArmorContents(items: Array<Item?>) {
        var items: Array<Item?> = items
        if (items.size < 4) {
            val newItems: Array<Item?> = arrayOfNulls<Item>(4)
            System.arraycopy(items, 0, newItems, 0, items.size)
            items = newItems
        }
        for (i in 0..3) {
            if (items[i] == null) {
                items[i] = ItemBlock(Block.get(BlockID.AIR), null, 0)
            }
            if (items[i].getId() === Item.AIR) {
                clear(getSize() + i)
            } else {
                this.setItem(getSize() + i, items[i])
            }
        }
    }

    fun sendArmorContents(players: Collection<Player?>) {
        this.sendArmorContents(players.toArray(Player.EMPTY_ARRAY))
    }

    fun sendArmorSlot(index: Int, player: Player) {
        this.sendArmorSlot(index, arrayOf<Player>(player))
    }

    fun sendArmorSlot(index: Int, players: Array<Player>) {
        val armor: Array<Item?> = getArmorContents()
        val pk = MobArmorEquipmentPacket()
        pk.eid = getHolder().getId()
        pk.slots = armor
        pk.tryEncode()
        for (player in players) {
            if (player.equals(getHolder())) {
                val pk2 = InventorySlotPacket()
                pk2.inventoryId = InventoryContentPacket.SPECIAL_ARMOR
                pk2.slot = index - getSize()
                pk2.item = this.getItem(index)
                player.dataPacket(pk2)
            } else {
                player.dataPacket(pk)
            }
        }
    }

    fun sendArmorSlot(index: Int, players: Collection<Player?>) {
        this.sendArmorSlot(index, players.toArray(Player.EMPTY_ARRAY))
    }

    @Override
    override fun sendContents(player: Player?) {
        this.sendContents(arrayOf<Player?>(player))
    }

    @Override
    override fun sendContents(players: Collection<Player?>) {
        this.sendContents(players.toArray(Player.EMPTY_ARRAY))
    }

    @Override
    override fun sendContents(players: Array<Player>) {
        val pk = InventoryContentPacket()
        pk.slots = arrayOfNulls<Item>(getSize())
        for (i in 0 until getSize()) {
            pk.slots.get(i) = this.getItem(i)
        }

        /*//Because PE is stupid and shows 9 less slots than you send it, give it 9 dummy slots so it shows all the REAL slots.
        for(int i = this.getSize(); i < this.getSize() + this.getHotbarSize(); ++i){
            pk.slots[i] = new ItemBlock(Block.get(BlockID.AIR));
        }
            pk.slots[i] = new ItemBlock(Block.get(BlockID.AIR));
        }*/for (player in players) {
            val id: Int = player.getWindowId(this)
            if (id == -1 || !player.spawned) {
                if (getHolder() !== player) this.close(player)
                continue
            }
            pk.inventoryId = id
            player.dataPacket(pk.clone())
        }
    }

    @Override
    override fun sendSlot(index: Int, player: Player) {
        this.sendSlot(index, *arrayOf<Player>(player))
    }

    @Override
    override fun sendSlot(index: Int, players: Collection<Player?>) {
        this.sendSlot(index, players.toArray(Player.EMPTY_ARRAY))
    }

    @Override
    override fun sendSlot(index: Int, vararg players: Player) {
        val pk = InventorySlotPacket()
        pk.slot = index
        pk.item = this.getItem(index).clone()
        for (player in players) {
            if (player.equals(getHolder())) {
                pk.inventoryId = ContainerIds.INVENTORY
                player.dataPacket(pk)
            } else {
                val id: Int = player.getWindowId(this)
                if (id == -1) {
                    this.close(player)
                    continue
                }
                pk.inventoryId = id
                player.dataPacket(pk.clone())
            }
        }
    }

    fun sendCreativeContents() {
        if (getHolder() !is Player) {
            return
        }
        val p: Player? = getHolder() as Player?
        val pk = CreativeContentPacket()
        if (!p.isSpectator()) { //fill it for all gamemodes except spectator
            pk.entries = Item.getCreativeItems().toArray(Item.EMPTY_ARRAY)
        }
        p.dataPacket(pk)
    }

    @Override
    override fun getHolder(): EntityHuman? {
        return super.getHolder() as EntityHuman?
    }

    @Override
    override fun onOpen(who: Player) {
        super.onOpen(who)
        if (who.spawned) {
            val pk = ContainerOpenPacket()
            pk.windowId = who.getWindowId(this)
            pk.type = this.getType().getNetworkType()
            pk.x = who.getFloorX()
            pk.y = who.getFloorY()
            pk.z = who.getFloorZ()
            pk.entityId = who.getId()
            who.dataPacket(pk)
        }
    }

    @Override
    override fun onClose(who: Player) {
        val pk = ContainerClosePacket()
        pk.windowId = who.getWindowId(this)
        pk.wasServerInitiated = who.getClosingWindowId() !== pk.windowId
        who.dataPacket(pk)
        // player can never stop viewing their own inventory
        if (who !== holder) {
            super.onClose(who)
        }
    }

    init {
        hotbar = IntArray(getHotbarSize())
        for (i in hotbar.indices) {
            hotbar[i] = i
        }
    }
}