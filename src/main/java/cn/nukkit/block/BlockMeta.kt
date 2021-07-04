package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

abstract class BlockMeta : Block {
    /**
     * Creates the block in the default state.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected constructor() {
        // Does nothing
    }

    /**
     * Create the block from a specific state.
     *
     * If the meta is not acceptable by [.getProperties], it will be modified to an accepted value.
     *
     * @param meta The block state meta
     */
    protected constructor(meta: Int) {
        if (meta != 0) {
            getMutableState().setDataStorageFromInt(meta, true)
        }
    }

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    abstract override val properties: BlockProperties?
}