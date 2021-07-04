package cn.nukkit.blockentity

import cn.nukkit.api.PowerNukkitDifference

@PowerNukkitDifference(info = "Extends BlockEntityEjectable instead of " +
        "BlockEntitySpawnable, BlockEntityContainer, BlockEntityNameable, and InventoryHolder " +
        "only in PowerNukkit", since = "1.4.0.0-PN")
class BlockEntityDispenser(chunk: FullChunk?, nbt: CompoundTag) : BlockEntityEjectable(chunk, nbt) {
    protected override var inventory: DispenserInventory? = null
    @Override
    protected override fun createInventory(): DispenserInventory {
        return DispenserInventory(this).also { inventory = it }
    }

    @get:Override
    protected override val blockEntityName: String?
        protected get() = BlockEntity.DISPENSER

    @Override
    override fun getInventory(): DispenserInventory? {
        return inventory
    }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() = this.getLevelBlock().getId() === BlockID.DISPENSER
}