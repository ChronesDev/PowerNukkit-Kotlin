package cn.nukkit.inventory

import cn.nukkit.Player

class BarrelInventory(barrel: BlockEntityBarrel?) : ContainerInventory(barrel, InventoryType.BARREL) {
    @Override
    override fun getHolder(): BlockEntityBarrel {
        return this.holder as BlockEntityBarrel
    }

    @Override
    override fun onOpen(who: Player) {
        super.onOpen(who)
        if (this.getViewers().size() === 1) {
            val barrel: BlockEntityBarrel = getHolder()
            val level: Level = barrel.getLevel()
            if (level != null) {
                val block: Block = barrel.getBlock()
                if (block is BlockBarrel) {
                    val blockBarrel: BlockBarrel = block as BlockBarrel
                    if (!blockBarrel.isOpen()) {
                        blockBarrel.setOpen(true)
                        level.setBlock(blockBarrel, blockBarrel, true, true)
                        level.addSound(blockBarrel, Sound.BLOCK_BARREL_OPEN)
                    }
                }
            }
        }
    }

    @Override
    override fun onClose(who: Player) {
        super.onClose(who)
        if (this.getViewers().isEmpty()) {
            val barrel: BlockEntityBarrel = getHolder()
            val level: Level = barrel.getLevel()
            if (level != null) {
                val block: Block = barrel.getBlock()
                if (block is BlockBarrel) {
                    val blockBarrel: BlockBarrel = block as BlockBarrel
                    if (blockBarrel.isOpen()) {
                        blockBarrel.setOpen(false)
                        level.setBlock(blockBarrel, blockBarrel, true, true)
                        level.addSound(blockBarrel, Sound.BLOCK_BARREL_CLOSE)
                    }
                }
            }
        }
    }
}