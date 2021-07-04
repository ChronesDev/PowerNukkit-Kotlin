package cn.nukkit.item

import cn.nukkit.nbt.tag.CompoundTag

/**
 * @author xtypr
 * @since 2015/12/27
 */
class ItemPotionSplash(meta: Integer?, count: Int) : ProjectileItem(SPLASH_POTION, meta, count, "Splash Potion") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun getProjectileEntityType(): String {
        return "ThrownPotion"
    }

    @Override
    override fun getThrowForce(): Float {
        return 0.5f
    }

    @Override
    protected override fun correctNBT(nbt: CompoundTag) {
        nbt.putInt("PotionId", this.meta)
    }
}