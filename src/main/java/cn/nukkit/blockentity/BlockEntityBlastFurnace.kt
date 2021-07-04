package cn.nukkit.blockentity

import cn.nukkit.block.Block

class BlockEntityBlastFurnace(chunk: FullChunk?, nbt: CompoundTag) : BlockEntityFurnace(chunk, nbt) {
    @get:Override
    protected override val furnaceName: String
        protected get() = "Blast Furnace"

    @get:Override
    protected override val clientName: String
        protected get() = BLAST_FURNACE

    @get:Override
    protected override val idleBlockId: Int
        protected get() = Block.BLAST_FURNACE

    @get:Override
    protected override val burningBlockId: Int
        protected get() = Block.LIT_BLAST_FURNACE

    @get:Override
    protected override val inventoryType: InventoryType
        protected get() = InventoryType.BLAST_FURNACE

    @Override
    protected override fun matchRecipe(raw: Item?): SmeltingRecipe {
        return this.server.getCraftingManager().matchBlastFurnaceRecipe(raw)
    }

    @get:Override
    protected override val speedMultiplier: Int
        protected get() = 2
}