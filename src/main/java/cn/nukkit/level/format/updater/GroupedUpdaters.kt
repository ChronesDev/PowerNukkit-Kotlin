package cn.nukkit.level.format.updater

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
internal class GroupedUpdaters @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(vararg updaters: Updater) : Updater {
    private val updaters: Array<Updater>

    @Override
    override fun update(offsetX: Int, offsetY: Int, offsetZ: Int, x: Int, y: Int, z: Int, state: BlockState?): Boolean {
        for (updater in updaters) {
            if (updater != null && updater.update(offsetX, offsetY, offsetZ, x, y, z, state)) {
                return true
            }
        }
        return false
    }

    init {
        this.updaters = updaters
    }
}