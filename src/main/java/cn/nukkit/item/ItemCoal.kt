package cn.nukkit.item

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemCoal : Item {
    constructor() : this(0, 1) {}

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Charcoal now have it's own id", replaceWith = "ItemCoal() or ItemCharcoal()")
    constructor(meta: Integer?) : this(meta, 1) {
    }

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Charcoal now have it's own id", replaceWith = "ItemCoal() or ItemCharcoal()")
    constructor(meta: Integer?, count: Int) : super(COAL, meta, count, "Coal") {
        if (meta === 1) {
            this.name = "Charcoal"
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected constructor(id: Int, meta: Integer?, count: Int, name: String?) : super(id, meta, count, name) {
    }

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Charcoal  now it's own ids, and its implementation extends ItemCoal, " +
            "so you may get 0 as meta result even though you have a charcoal.", replaceWith = "isCharcoal()")
    @Override
    override fun getDamage(): Int {
        return super.getDamage()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun isCharcoal(): Boolean {
        return getId() === COAL && super.getDamage() === 1
    }
}