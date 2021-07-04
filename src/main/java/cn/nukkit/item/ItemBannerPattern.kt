package cn.nukkit.item

import cn.nukkit.api.PowerNukkitOnly

@Since("1.2.1.0-PN")
@PowerNukkitOnly
class ItemBannerPattern : Item {
    @PowerNukkitOnly
    constructor() : this(0, 1) {
    }

    @PowerNukkitOnly
    constructor(meta: Integer?) : this(meta, 1) {
    }

    @PowerNukkitOnly
    constructor(meta: Integer?, count: Int) : super(BANNER_PATTERN, meta, count, "Bone") {
        updateName()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected constructor(id: Int, meta: Integer?, count: Int, name: String?) : super(id, meta, count, name) {
    }

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }

    @Override
    override fun setDamage(meta: Integer?) {
        super.setDamage(meta)
        updateName()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getPatternType(): BannerPattern.Type {
        return if (getId() !== BANNER_PATTERN) {
            BannerPattern.Type.PATTERN_CREEPER
        } else when (getDamage()) {
            PATTERN_CREEPER_CHARGE -> BannerPattern.Type.PATTERN_CREEPER
            PATTERN_SKULL_CHARGE -> BannerPattern.Type.PATTERN_SKULL
            PATTERN_FLOWER_CHARGE -> BannerPattern.Type.PATTERN_FLOWER
            PATTERN_THING -> BannerPattern.Type.PATTERN_MOJANG
            PATTERN_FIELD_MASONED -> BannerPattern.Type.PATTERN_BRICK
            PATTERN_BORDURE_INDENTED -> BannerPattern.Type.PATTERN_CURLY_BORDER
            else -> BannerPattern.Type.PATTERN_CREEPER
        }
    }

    protected fun updateName() {
        if (getId() !== BANNER_PATTERN) {
            return
        }
        when (super.meta % 6) {
            PATTERN_CREEPER_CHARGE -> {
                name = "Creeper Charge Banner Pattern"
                return
            }
            PATTERN_SKULL_CHARGE -> {
                name = "Skull Charge Banner Pattern"
                return
            }
            PATTERN_FLOWER_CHARGE -> {
                name = "Flower Charge Banner Pattern"
                return
            }
            PATTERN_THING -> {
                name = "Thing Banner Pattern"
                return
            }
            PATTERN_FIELD_MASONED -> {
                name = "Field Banner Pattern"
                return
            }
            PATTERN_BORDURE_INDENTED -> {
                name = "Bordure Idented Banner Pattern"
                return
            }
            else -> name = "Banner Pattern"
        }
    }

    companion object {
        @PowerNukkitOnly
        val PATTERN_CREEPER_CHARGE = 0

        @PowerNukkitOnly
        val PATTERN_SKULL_CHARGE = 1

        @PowerNukkitOnly
        val PATTERN_FLOWER_CHARGE = 2

        @PowerNukkitOnly
        val PATTERN_THING = 3

        @PowerNukkitOnly
        val PATTERN_FIELD_MASONED = 4

        @PowerNukkitOnly
        val PATTERN_BORDURE_INDENTED = 5
    }
}