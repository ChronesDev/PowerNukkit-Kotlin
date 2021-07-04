package cn.nukkit.level.format.updater

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@FunctionalInterface
internal interface Updater {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun update(offsetX: Int, offsetY: Int, offsetZ: Int, x: Int, y: Int, z: Int, state: BlockState?): Boolean
}