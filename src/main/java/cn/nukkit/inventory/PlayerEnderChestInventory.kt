package cn.nukkit.inventory

import cn.nukkit.Player

class PlayerEnderChestInventory(player: EntityHumanType?) : BaseInventory(player, InventoryType.ENDER_CHEST) {
    @Override
    override fun getHolder(): EntityHuman {
        return this.holder as EntityHuman
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    override fun onOpen(who: Player) {
        if (who !== getHolder()) {
            return
        }
        super.onOpen(who)
        val containerOpenPacket = ContainerOpenPacket()
        containerOpenPacket.windowId = who.getWindowId(this)
        containerOpenPacket.type = this.getType().getNetworkType()
        val chest: BlockEnderChest = who.getViewingEnderChest()
        if (chest != null) {
            containerOpenPacket.x = chest.getX()
            containerOpenPacket.y = chest.getY()
            containerOpenPacket.z = chest.getZ()
        } else {
            containerOpenPacket.z = 0
            containerOpenPacket.y = containerOpenPacket.z
            containerOpenPacket.x = containerOpenPacket.y
        }
        who.dataPacket(containerOpenPacket)
        this.sendContents(who)
        if (chest != null && chest.getViewers().size() === 1) {
            val blockEventPacket = BlockEventPacket()
            blockEventPacket.x = chest.getX()
            blockEventPacket.y = chest.getY()
            blockEventPacket.z = chest.getZ()
            blockEventPacket.case1 = 1
            blockEventPacket.case2 = 2
            val level: Level = getHolder().getLevel()
            if (level != null) {
                level.addSound(getHolder().add(0.5, 0.5, 0.5), Sound.RANDOM_ENDERCHESTOPEN)
                level.addChunkPacket(getHolder().getX() as Int shr 4, getHolder().getZ() as Int shr 4, blockEventPacket)
            }
        }
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    override fun onClose(who: Player) {
        val containerClosePacket = ContainerClosePacket()
        containerClosePacket.windowId = who.getWindowId(this)
        containerClosePacket.wasServerInitiated = who.getClosingWindowId() !== containerClosePacket.windowId
        who.dataPacket(containerClosePacket)
        super.onClose(who)
        val chest: BlockEnderChest = who.getViewingEnderChest()
        if (chest != null && chest.getViewers().size() === 1) {
            val blockEventPacket = BlockEventPacket()
            blockEventPacket.x = chest.getX()
            blockEventPacket.y = chest.getY()
            blockEventPacket.z = chest.getZ()
            blockEventPacket.case1 = 1
            blockEventPacket.case2 = 0
            val level: Level = getHolder().getLevel()
            if (level != null) {
                level.addSound(getHolder().add(0.5, 0.5, 0.5), Sound.RANDOM_ENDERCHESTCLOSED)
                level.addChunkPacket(getHolder().getX() as Int shr 4, getHolder().getZ() as Int shr 4, blockEventPacket)
            }
            who.setViewingEnderChest(null)
        }
        super.onClose(who)
    }
}