package cn.nukkit.inventory

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class BaseInventory(holder: InventoryHolder?, type: InventoryType, items: Map<Integer?, Item?>, overrideSize: Integer?, overrideTitle: String?) : Inventory {
    override val type: InventoryType

    @get:Override
    @set:Override
    override var maxStackSize: Int = Inventory.MAX_STACK

    @get:Override
    override var size = 0

    @get:Override
    override val name: String

    @get:Override
    override var title: String? = null
    val slots: Map<Integer, Item> = HashMap()
    override val viewers: Set<Player> = HashSet()
    override var holder: InventoryHolder?
    private var listeners: List<InventoryListener>? = null

    constructor(holder: InventoryHolder?, type: InventoryType) : this(holder, type, HashMap()) {}
    constructor(holder: InventoryHolder?, type: InventoryType, items: Map<Integer?, Item?>) : this(holder, type, items, null) {}
    constructor(holder: InventoryHolder?, type: InventoryType, items: Map<Integer?, Item?>, overrideSize: Integer?) : this(holder, type, items, overrideSize, null) {}

    @Override
    override fun getItem(index: Int): Item {
        return if (slots.containsKey(index)) slots[index].clone() else ItemBlock(Block.get(BlockID.AIR), null, 0)
    }

    @get:Override
    @set:Override
    override var contents: Map<Any?, Any?>?
        get() = HashMap(slots)
        set(items) {
            var items: Map<Integer?, Item?> = items!!
            if (items.size() > size) {
                var newItems: TreeMap<Integer?, Item?> = TreeMap()
                for (entry in items.entrySet()) {
                    newItems.put(entry.getKey(), entry.getValue())
                }
                items = newItems
                newItems = TreeMap()
                var i = 0
                for (entry in items.entrySet()) {
                    newItems.put(entry.getKey(), entry.getValue())
                    i++
                    if (i >= size) {
                        break
                    }
                }
                items = newItems
            }
            for (i in 0 until size) {
                if (!items.containsKey(i)) {
                    if (slots.containsKey(i)) {
                        clear(i)
                    }
                } else {
                    if (!setItem(i, items[i])) {
                        clear(i)
                    }
                }
            }
        }

    @Override
    override fun setItem(index: Int, item: Item, send: Boolean): Boolean {
        var item: Item = item
        item = item.clone()
        if (index < 0 || index >= size) {
            return false
        } else if (item.getId() === 0 || item.getCount() <= 0) {
            return clear(index, send)
        }
        val holder: InventoryHolder? = getHolder()
        if (holder is Entity) {
            val ev = EntityInventoryChangeEvent(holder as Entity?, getItem(index), item, index)
            Server.getInstance().getPluginManager().callEvent(ev)
            if (ev.isCancelled()) {
                this.sendSlot(index, getViewers())
                return false
            }
            item = ev.getNewItem()
        }
        if (holder is BlockEntity) {
            (holder as BlockEntity?).setDirty()
        }
        val old: Item = getItem(index)
        slots.put(index, item.clone())
        onSlotChange(index, old, send)
        return true
    }

    @Override
    override operator fun contains(item: Item): Boolean {
        var count: Int = Math.max(1, item.getCount())
        val checkDamage = item.hasMeta() && item.getDamage() >= 0
        val checkTag = item.getCompoundTag() != null
        for (i in contents!!.values()) {
            if (item.equals(i, checkDamage, checkTag)) {
                count -= i.getCount()
                if (count <= 0) {
                    return true
                }
            }
        }
        return false
    }

    @Override
    override fun all(item: Item): Map<Integer, Item> {
        val slots: Map<Integer, Item> = HashMap()
        val checkDamage = item.hasMeta() && item.getDamage() >= 0
        val checkTag = item.getCompoundTag() != null
        for (entry in contents.entrySet()) {
            if (item.equals(entry.getValue(), checkDamage, checkTag)) {
                slots.put(entry.getKey(), entry.getValue())
            }
        }
        return slots
    }

    @Override
    override fun remove(item: Item) {
        val checkDamage: Boolean = item.hasMeta()
        val checkTag = item.getCompoundTag() != null
        for (entry in contents.entrySet()) {
            if (item.equals(entry.getValue(), checkDamage, checkTag)) {
                clear(entry.getKey())
            }
        }
    }

    @Override
    override fun first(item: Item, exact: Boolean): Int {
        val count: Int = Math.max(1, item.getCount())
        val checkDamage: Boolean = item.hasMeta()
        val checkTag = item.getCompoundTag() != null
        for (entry in contents.entrySet()) {
            if (item.equals(entry.getValue(), checkDamage, checkTag) && (entry.getValue().getCount() === count || !exact && entry.getValue().getCount() > count)) {
                return entry.getKey()
            }
        }
        return -1
    }

    @Override
    override fun firstEmpty(item: Item?): Int {
        for (i in 0 until size) {
            if (getItem(i).getId() === Item.AIR) {
                return i
            }
        }
        return -1
    }

    @Override
    override fun decreaseCount(slot: Int) {
        val item: Item = getItem(slot)
        if (item.getCount() > 0) {
            item.count--
            setItem(slot, item)
        }
    }

    @Override
    override fun canAddItem(item: Item): Boolean {
        var item: Item = item
        item = item.clone()
        val checkDamage: Boolean = item.hasMeta()
        val checkTag = item.getCompoundTag() != null
        for (i in 0 until size) {
            val slot: Item = getItem(i)
            if (item.equals(slot, checkDamage, checkTag)) {
                var diff: Int
                if (Math.min(slot.getMaxStackSize(), maxStackSize) - slot.getCount().also { diff = it } > 0) {
                    item.setCount(item.getCount() - diff)
                }
            } else if (slot.getId() === Item.AIR) {
                item.setCount(item.getCount() - Math.min(slot.getMaxStackSize(), maxStackSize))
            }
            if (item.getCount() <= 0) {
                return true
            }
        }
        return false
    }

    @Override
    override fun addItem(vararg slots: Item): Array<Item> {
        val itemSlots: List<Item> = ArrayList()
        for (slot in slots) {
            if (slot.getId() !== 0 && slot.getCount() > 0) {
                itemSlots.add(slot.clone())
            }
        }
        val emptySlots: List<Integer> = ArrayList()
        for (i in 0 until size) {
            val item: Item = getItem(i)
            if (item.getId() === Item.AIR || item.getCount() <= 0) {
                emptySlots.add(i)
            }
            for (slot in ArrayList(itemSlots)) {
                if (slot.equals(item)) {
                    val maxStackSize: Int = Math.min(maxStackSize, item.getMaxStackSize())
                    if (item.getCount() < maxStackSize) {
                        var amount: Int = Math.min(maxStackSize - item.getCount(), slot.getCount())
                        amount = Math.min(amount, this.maxStackSize)
                        if (amount > 0) {
                            slot.setCount(slot.getCount() - amount)
                            item.setCount(item.getCount() + amount)
                            setItem(i, item)
                            if (slot.getCount() <= 0) {
                                itemSlots.remove(slot)
                            }
                        }
                    }
                }
            }
            if (itemSlots.isEmpty()) {
                break
            }
        }
        if (!itemSlots.isEmpty() && !emptySlots.isEmpty()) {
            for (slotIndex in emptySlots) {
                if (!itemSlots.isEmpty()) {
                    val slot: Item = itemSlots[0]
                    val maxStackSize: Int = Math.min(slot.getMaxStackSize(), maxStackSize)
                    var amount: Int = Math.min(maxStackSize, slot.getCount())
                    amount = Math.min(amount, this.maxStackSize)
                    slot.setCount(slot.getCount() - amount)
                    val item: Item = slot.clone()
                    item.setCount(amount)
                    setItem(slotIndex, item)
                    if (slot.getCount() <= 0) {
                        itemSlots.remove(slot)
                    }
                }
            }
        }
        return itemSlots.toArray(Item.EMPTY_ARRAY)
    }

    @Override
    override fun removeItem(vararg slots: Item): Array<Item> {
        val itemSlots: List<Item> = ArrayList()
        for (slot in slots) {
            if (slot.getId() !== 0 && slot.getCount() > 0) {
                itemSlots.add(slot.clone())
            }
        }
        for (i in 0 until size) {
            val item: Item = getItem(i)
            if (item.getId() === Item.AIR || item.getCount() <= 0) {
                continue
            }
            for (slot in ArrayList(itemSlots)) {
                if (slot.equals(item, item.hasMeta(), item.getCompoundTag() != null)) {
                    val amount: Int = Math.min(item.getCount(), slot.getCount())
                    slot.setCount(slot.getCount() - amount)
                    item.setCount(item.getCount() - amount)
                    setItem(i, item)
                    if (slot.getCount() <= 0) {
                        itemSlots.remove(slot)
                    }
                }
            }
            if (itemSlots.size() === 0) {
                break
            }
        }
        return itemSlots.toArray(Item.EMPTY_ARRAY)
    }

    @Override
    override fun clear(index: Int, send: Boolean): Boolean {
        if (slots.containsKey(index)) {
            var item: Item = ItemBlock(Block.get(BlockID.AIR), null, 0)
            val old: Item? = slots[index]
            val holder: InventoryHolder? = getHolder()
            if (holder is Entity) {
                val ev = EntityInventoryChangeEvent(holder as Entity?, old, item, index)
                Server.getInstance().getPluginManager().callEvent(ev)
                if (ev.isCancelled()) {
                    this.sendSlot(index, getViewers())
                    return false
                }
                item = ev.getNewItem()
            }
            if (item.getId() !== Item.AIR) {
                slots.put(index, item.clone())
            } else {
                slots.remove(index)
            }
            onSlotChange(index, old, send)
        }
        return true
    }

    @Override
    override fun clearAll() {
        for (index in contents.keySet()) {
            clear(index)
        }
    }

    @Override
    fun getViewers(): Set<Player> {
        return viewers
    }

    @Override
    fun getHolder(): InventoryHolder? {
        return holder
    }

    @Override
    override fun open(who: Player): Boolean {
        //if (this.viewers.contains(who)) return false;
        val ev = InventoryOpenEvent(this, who)
        who.getServer().getPluginManager().callEvent(ev)
        if (ev.isCancelled()) {
            return false
        }
        onOpen(who)
        return true
    }

    @Override
    override fun close(who: Player?) {
        onClose(who)
    }

    @Override
    override fun onOpen(who: Player?) {
        viewers.add(who)
    }

    @Override
    override fun onClose(who: Player?) {
        viewers.remove(who)
    }

    @Override
    override fun onSlotChange(index: Int, before: Item?, send: Boolean) {
        if (send) {
            this.sendSlot(index, getViewers())
        }
        if (holder is BlockEntity) {
            (holder as BlockEntity?).setDirty()
        }
        if (before.getId() === ItemID.LODESTONE_COMPASS || getItem(index).getId() === ItemID.LODESTONE_COMPASS) {
            if (holder is Player) {
                (holder as Player?).updateTrackingPositions(true)
            }
            getViewers().forEach { p -> p.updateTrackingPositions(true) }
        }
        if (listeners != null) {
            for (listener in listeners!!) {
                listener.onInventoryChanged(this, before, index)
            }
        }
    }

    @Override
    override fun sendContents(player: Player) {
        this.sendContents(*arrayOf<Player>(player))
    }

    @Override
    override fun sendContents(vararg players: Player) {
        val pk = InventoryContentPacket()
        pk.slots = arrayOfNulls<Item>(size)
        for (i in 0 until size) {
            pk.slots.get(i) = getItem(i)
        }
        for (player in players) {
            val id: Int = player.getWindowId(this)
            if (id == -1 || !player.spawned) {
                close(player)
                continue
            }
            pk.inventoryId = id
            player.dataPacket(pk)
        }
    }

    @get:Override
    override val isFull: Boolean
        get() {
            if (slots.size() < size) {
                return false
            }
            for (item in slots.values()) {
                if (item == null || item.getId() === 0 || item.getCount() < item.getMaxStackSize() || item.getCount() < maxStackSize) {
                    return false
                }
            }
            return true
        }

    @get:Override
    override val isEmpty: Boolean
        get() {
            if (maxStackSize <= 0) {
                return false
            }
            for (item in slots.values()) {
                if (item != null && item.getId() !== 0 && item.getCount() > 0) {
                    return false
                }
            }
            return true
        }

    fun getFreeSpace(item: Item): Int {
        val maxStackSize: Int = Math.min(item.getMaxStackSize(), maxStackSize)
        var space: Int = (size - slots.size()) * maxStackSize
        for (slot in contents!!.values()) {
            if (slot == null || slot.getId() === 0) {
                space += maxStackSize
                continue
            }
            if (slot.equals(item, true, true)) {
                space += maxStackSize - slot.getCount()
            }
        }
        return space
    }

    @Override
    override fun sendContents(players: Collection<Player?>) {
        this.sendContents(players.toArray(Player.EMPTY_ARRAY))
    }

    @Override
    override fun sendSlot(index: Int, player: Player) {
        this.sendSlot(index, *arrayOf<Player>(player))
    }

    @Override
    override fun sendSlot(index: Int, vararg players: Player) {
        val pk = InventorySlotPacket()
        pk.slot = index
        pk.item = getItem(index).clone()
        for (player in players) {
            val id: Int = player.getWindowId(this)
            if (id == -1) {
                close(player)
                continue
            }
            pk.inventoryId = id
            player.dataPacket(pk)
        }
    }

    @Override
    override fun sendSlot(index: Int, players: Collection<Player?>) {
        this.sendSlot(index, players.toArray(Player.EMPTY_ARRAY))
    }

    @Override
    override fun addListener(listener: InventoryListener?) {
        if (listeners == null) {
            listeners = ArrayList()
        }
        listeners.add(listener)
    }

    @Override
    override fun removeListener(listener: InventoryListener?) {
        if (listeners != null) {
            listeners.remove(listener)
        }
    }

    @Override
    fun getType(): InventoryType {
        return type
    }

    init {
        this.holder = holder
        this.type = type
        if (overrideSize != null) {
            size = overrideSize
        } else {
            size = this.type.getDefaultSize()
        }
        if (overrideTitle != null) {
            title = overrideTitle
        } else {
            title = this.type.getDefaultTitle()
        }
        name = this.type.getDefaultTitle()
        if (this !is DoubleChestInventory) {
            contents = items
        }
    }
}