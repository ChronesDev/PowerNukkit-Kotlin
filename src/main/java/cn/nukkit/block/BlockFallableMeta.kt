package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
abstract class BlockFallableMeta : BlockFallable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() {
        // Does nothing
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(meta: Int) {
        if (meta != 0) {
            getMutableState().setDataStorageFromInt(meta, true)
        }
    }

    @get:Override
    @get:PowerNukkitOnly
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    abstract override val properties: BlockProperties?
}