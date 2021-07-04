package cn.nukkit.blockentity

import cn.nukkit.block.Block

class BlockEntitySmoker(chunk: FullChunk?, nbt: CompoundTag) : BlockEntityFurnace(chunk, nbt) {
    @get:Override
    protected override val furnaceName: String
        protected get() = "Smoker"

    @get:Override
    protected override val clientName: String
        protected get() = SMOKER

    @get:Override
    protected override val idleBlockId: Int
        protected get() = Block.SMOKER

    @get:Override
    protected override val burningBlockId: Int
        protected get() = Block.LIT_SMOKER

    @get:Override
    protected override val inventoryType: InventoryType
        protected get() = InventoryType.SMOKER

    @Override
    protected override fun matchRecipe(raw: Item?): SmeltingRecipe {
        return this.server.getCraftingManager().matchSmokerRecipe(raw)
    }

    @get:Override
    protected override val speedMultiplier: Int
        protected get() = 2
}