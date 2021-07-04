package cn.nukkit.inventory

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ChestInventory(chest: BlockEntityChest?) : ContainerInventory(chest, InventoryType.CHEST) {
    protected var doubleInventory: DoubleChestInventory? = null

    @Override
    override fun getHolder(): BlockEntityChest {
        return this.holder as BlockEntityChest
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    override fun onOpen(who: Player) {
        super.onOpen(who)
        if (this.getViewers().size() === 1) {
            val pk = BlockEventPacket()
            pk.x = getHolder().getX()
            pk.y = getHolder().getY()
            pk.z = getHolder().getZ()
            pk.case1 = 1
            pk.case2 = 2
            val level: Level = getHolder().getLevel()
            if (level != null) {
                level.addSound(getHolder().add(0.5, 0.5, 0.5), Sound.RANDOM_CHESTOPEN)
                level.addChunkPacket(getHolder().getX() as Int shr 4, getHolder().getZ() as Int shr 4, pk)
            }
        }
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    override fun onClose(who: Player) {
        if (this.getViewers().size() === 1) {
            val pk = BlockEventPacket()
            pk.x = getHolder().getX()
            pk.y = getHolder().getY()
            pk.z = getHolder().getZ()
            pk.case1 = 1
            pk.case2 = 0
            val level: Level = getHolder().getLevel()
            if (level != null) {
                level.addSound(getHolder().add(0.5, 0.5, 0.5), Sound.RANDOM_CHESTCLOSED)
                level.addChunkPacket(getHolder().getX() as Int shr 4, getHolder().getZ() as Int shr 4, pk)
            }
        }
        super.onClose(who)
    }

    fun setDoubleInventory(doubleInventory: DoubleChestInventory?) {
        this.doubleInventory = doubleInventory
    }

    fun getDoubleInventory(): DoubleChestInventory? {
        return doubleInventory
    }

    @Override
    override fun sendSlot(index: Int, vararg players: Player?) {
        if (doubleInventory != null) {
            doubleInventory.sendSlot(this, index, players)
        } else {
            super.sendSlot(index, players)
        }
    }
}