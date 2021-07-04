package cn.nukkit.blockentity

import cn.nukkit.block.BlockID

class BlockEntityBarrel(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnableContainer(chunk, nbt), BlockEntityNameable {
    @Override
    protected override fun initBlockEntity() {
        inventory = BarrelInventory(this)
        super.initBlockEntity()
    }

    @get:Override
    override val size: Int
        get() = 27

    @get:Override
    override val spawnCompound: CompoundTag
        get() = CompoundTag()
                .putString("id", BlockEntity.BARREL)
                .putInt("x", this.x as Int)
                .putInt("y", this.y as Int)
                .putInt("z", this.z as Int)
                .putBoolean("isMovable", this.isMovable())
                .putBoolean("Findable", false)

    @get:Override
    override val isBlockEntityValid: Boolean
        get() = getBlock().getId() === BlockID.BARREL

    @get:Override
    override var inventory: BarrelInventory
        get() = field as BarrelInventory
        set(inventory) {
            super.inventory = inventory
        }

    @get:Override
    @set:Override
    override var name: String
        get() = if (hasName()) this.namedTag.getString("CustomName") else "Barrel"
        set(name) {
            if (name == null || name.equals("")) {
                this.namedTag.remove("CustomName")
                return
            }
            this.namedTag.putString("CustomName", name)
        }

    @Override
    override fun hasName(): Boolean {
        return this.namedTag.contains("CustomName")
    }
}