package cn.nukkit.utils.functional

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@FunctionalInterface
interface BlockPositionDataConsumer<D> {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun accept(x: Int, y: Int, z: Int, data: D)
}