package cn.nukkit.inventory

import cn.nukkit.blockentity.BlockEntityHopper

/**
 * @author CreeperFace
 * @since 8.5.2017
 */
class HopperInventory(hopper: BlockEntityHopper?) : ContainerInventory(hopper, InventoryType.HOPPER) {
    @Override
    override fun getHolder(): BlockEntityHopper {
        return super.getHolder() as BlockEntityHopper
    }
}