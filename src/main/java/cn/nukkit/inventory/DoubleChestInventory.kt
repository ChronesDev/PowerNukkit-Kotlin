package cn.nukkit.inventory

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class DoubleChestInventory(left: BlockEntityChest, right: BlockEntityChest) : ContainerInventory(null, InventoryType.DOUBLE_CHEST), InventoryHolder {
    private val left: ChestInventory
    private val right: ChestInventory

    @Override
    override fun getInventory(): Inventory {
        return this
    }

    @Override
    override fun getHolder(): BlockEntityChest {
        return left.getHolder()
    }

    @Override
    override fun getItem(index: Int): Item {
        return if (index < left.getSize()) left.getItem(index) else right.getItem(index - right.getSize())
    }

    @Override
    override fun setItem(index: Int, item: Item?, send: Boolean): Boolean {
        return if (index < left.getSize()) left.setItem(index, item, send) else right.setItem(index - right.getSize(), item, send)
    }

    @Override
    override fun clear(index: Int, send: Boolean): Boolean {
        return if (index < left.getSize()) left.clear(index, send) else right.clear(index - right.getSize(), send)
    }

    @Override
    fun getContents(): Map<Integer, Item> {
        val contents: Map<Integer, Item> = HashMap()
        for (i in 0 until this.getSize()) {
            contents.put(i, getItem(i))
        }
        return contents
    }

    @Override
    fun setContents(items: Map<Integer?, Item?>) {
        var items: Map<Integer?, Item?> = items
        if (items.size() > this.size) {
            val newItems: Map<Integer?, Item> = HashMap()
            for (i in 0 until this.size) {
                newItems.put(i, items[i])
            }
            items = newItems
        }
        for (i in 0 until this.size) {
            if (!items.containsKey(i)) {
                if (i < left.size) {
                    if (left.slots.containsKey(i)) {
                        clear(i)
                    }
                } else if (right.slots.containsKey(i - left.size)) {
                    clear(i)
                }
            } else if (!setItem(i, items[i])) {
                clear(i)
            }
        }
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    override fun onOpen(who: Player) {
        super.onOpen(who)
        left.viewers.add(who)
        right.viewers.add(who)
        if (this.getViewers().size() === 1) {
            val pk1 = BlockEventPacket()
            pk1.x = left.getHolder().getX()
            pk1.y = left.getHolder().getY()
            pk1.z = left.getHolder().getZ()
            pk1.case1 = 1
            pk1.case2 = 2
            var level: Level = left.getHolder().getLevel()
            if (level != null) {
                level.addSound(left.getHolder().add(0.5, 0.5, 0.5), Sound.RANDOM_CHESTOPEN)
                level.addChunkPacket(left.getHolder().getX() as Int shr 4, left.getHolder().getZ() as Int shr 4, pk1)
            }
            val pk2 = BlockEventPacket()
            pk2.x = right.getHolder().getX()
            pk2.y = right.getHolder().getY()
            pk2.z = right.getHolder().getZ()
            pk2.case1 = 1
            pk2.case2 = 2
            level = right.getHolder().getLevel()
            if (level != null) {
                level.addSound(right.getHolder().add(0.5, 0.5, 0.5), Sound.RANDOM_CHESTOPEN)
                level.addChunkPacket(right.getHolder().getX() as Int shr 4, right.getHolder().getZ() as Int shr 4, pk2)
            }
        }
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    override fun onClose(who: Player) {
        if (this.getViewers().size() === 1) {
            val pk1 = BlockEventPacket()
            pk1.x = right.getHolder().getX()
            pk1.y = right.getHolder().getY()
            pk1.z = right.getHolder().getZ()
            pk1.case1 = 1
            pk1.case2 = 0
            var level: Level = right.getHolder().getLevel()
            if (level != null) {
                level.addSound(right.getHolder().add(0.5, 0.5, 0.5), Sound.RANDOM_CHESTCLOSED)
                level.addChunkPacket(right.getHolder().getX() as Int shr 4, right.getHolder().getZ() as Int shr 4, pk1)
            }
            val pk2 = BlockEventPacket()
            pk2.x = left.getHolder().getX()
            pk2.y = left.getHolder().getY()
            pk2.z = left.getHolder().getZ()
            pk2.case1 = 1
            pk2.case2 = 0
            level = left.getHolder().getLevel()
            if (level != null) {
                level.addSound(left.getHolder().add(0.5, 0.5, 0.5), Sound.RANDOM_CHESTCLOSED)
                level.addChunkPacket(left.getHolder().getX() as Int shr 4, left.getHolder().getZ() as Int shr 4, pk2)
            }
        }
        left.viewers.remove(who)
        right.viewers.remove(who)
        super.onClose(who)
    }

    fun getLeftSide(): ChestInventory {
        return left
    }

    fun getRightSide(): ChestInventory {
        return right
    }

    fun sendSlot(inv: Inventory, index: Int, vararg players: Player) {
        val pk = InventorySlotPacket()
        pk.slot = if (inv === right) left.getSize() + index else index
        pk.item = inv.getItem(index).clone()
        for (player in players) {
            val id: Int = player.getWindowId(this)
            if (id == -1) {
                this.close(player)
                continue
            }
            pk.inventoryId = id
            player.dataPacket(pk)
        }
    }

    init {
        this.holder = this
        this.left = left.getRealInventory()
        this.left.setDoubleInventory(this)
        this.right = right.getRealInventory()
        this.right.setDoubleInventory(this)
        val items: Map<Integer?, Item> = HashMap()
        // First we add the items from the left chest
        for (idx in 0 until this.left.getSize()) {
            if (this.left.getContents().containsKey(idx)) { // Don't forget to skip empty slots!
                items.put(idx, this.left.getContents().get(idx))
            }
        }
        // And them the items from the right chest
        for (idx in 0 until this.right.getSize()) {
            if (this.right.getContents().containsKey(idx)) { // Don't forget to skip empty slots!
                items.put(idx + this.left.getSize(), this.right.getContents().get(idx)) // idx + this.left.getSize() so we don't overlap left chest items
            }
        }
        setContents(items)
    }
}