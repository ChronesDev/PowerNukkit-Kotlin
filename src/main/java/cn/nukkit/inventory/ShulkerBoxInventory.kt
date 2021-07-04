package cn.nukkit.inventory

import cn.nukkit.Player

/**
 * @author PetteriM1
 */
class ShulkerBoxInventory(box: BlockEntityShulkerBox?) : ContainerInventory(box, InventoryType.SHULKER_BOX) {
    @Override
    override fun getHolder(): BlockEntityShulkerBox {
        return this.holder as BlockEntityShulkerBox
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
                level.addSound(getHolder().add(0.5, 0.5, 0.5), Sound.RANDOM_SHULKERBOXOPEN)
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
                level.addSound(getHolder().add(0.5, 0.5, 0.5), Sound.RANDOM_SHULKERBOXCLOSED)
                level.addChunkPacket(getHolder().getX() as Int shr 4, getHolder().getZ() as Int shr 4, pk)
            }
        }
        super.onClose(who)
    }

    @Override
    override fun canAddItem(item: Item): Boolean {
        return if (item.getId() === BlockID.SHULKER_BOX || item.getId() === BlockID.UNDYED_SHULKER_BOX) {
            // Do not allow nested shulker boxes.
            false
        } else super.canAddItem(item)
    }

    @Override
    override fun sendSlot(index: Int, vararg players: Player?) {
        super.sendSlot(index, players)
    }
}