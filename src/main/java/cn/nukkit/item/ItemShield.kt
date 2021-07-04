package cn.nukkit.item

import cn.nukkit.api.PowerNukkitDifference

@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Extends ItemTool instead of Item only in PowerNukkit")
class ItemShield @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemTool(SHIELD, meta, count, "Shield") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }

    @Override
    override fun getMaxDurability(): Int {
        return 336
    }
}