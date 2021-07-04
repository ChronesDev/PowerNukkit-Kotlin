package cn.nukkit.blockentity

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockEntityEnchantTable(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt), BlockEntityNameable {
    @get:Override
    override val isBlockEntityValid: Boolean
        get() = getBlock().getId() === Block.ENCHANT_TABLE

    @get:Override
    @set:Override
    override var name: String
        get() = if (hasName()) this.namedTag.getString("CustomName") else "Enchanting Table"
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

    @get:Override
    override val spawnCompound: CompoundTag
        get() {
            val c: CompoundTag = CompoundTag()
                    .putString("id", BlockEntity.ENCHANT_TABLE)
                    .putInt("x", this.x as Int)
                    .putInt("y", this.y as Int)
                    .putInt("z", this.z as Int)
            if (hasName()) {
                c.put("CustomName", this.namedTag.get("CustomName"))
            }
            return c
        }
}