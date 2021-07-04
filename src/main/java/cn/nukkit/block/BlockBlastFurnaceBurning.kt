package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockBlastFurnaceBurning @PowerNukkitOnly constructor(meta: Int) : BlockFurnaceBurning(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = LIT_BLAST_FURNACE

    @get:Override
    override val name: String
        get() = "Burning Blast Furnace"

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.BLAST_FURNACE

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityBlastFurnace?>
        get() = BlockEntityBlastFurnace::class.java

    @Override
    override fun toItem(): Item {
        return ItemBlock(BlockBlastFurnace())
    }
}