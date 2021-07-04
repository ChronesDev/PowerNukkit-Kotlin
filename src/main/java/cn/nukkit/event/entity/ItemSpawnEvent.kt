package cn.nukkit.event.entity

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Is cancellable only in PowerNukkit")
class ItemSpawnEvent(item: EntityItem?) : EntityEvent(), Cancellable {
    @Override
    override fun getEntity(): EntityItem {
        return this.entity as EntityItem
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.entity = item
    }
}