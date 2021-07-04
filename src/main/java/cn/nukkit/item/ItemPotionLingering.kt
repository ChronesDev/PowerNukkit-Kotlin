package cn.nukkit.item

import cn.nukkit.nbt.tag.CompoundTag

class ItemPotionLingering @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ProjectileItem(LINGERING_POTION, meta, count, "Lingering Potion") {
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
        return "LingeringPotion"
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