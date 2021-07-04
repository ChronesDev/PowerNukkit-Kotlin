package cn.nukkit.item

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemDye : Item {
    constructor() : this(0, 1) {}
    constructor(meta: Integer?) : this(meta, 1) {}
    constructor(dyeColor: DyeColor) : this(dyeColor.getDyeData(), 1) {}
    constructor(dyeColor: DyeColor, amount: Int) : this(dyeColor.getDyeData(), amount) {}
    constructor(meta: Integer, amount: Int) : super(DYE, meta, amount, if (meta <= 15) DyeColor.getByDyeData(meta).getDyeName() else DyeColor.getByDyeData(meta).getName().toString() + " Dye") {
        if (meta === DyeColor.BROWN.getDyeData()) {
            this.block = Block.get(BlockID.COCOA_BLOCK)
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected constructor(id: Int, meta: Integer?, count: Int, name: String?) : super(id, meta, count, name) {
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isFertilizer(): Boolean {
        return getId() === DYE && getDyeColor().equals(DyeColor.WHITE)
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    fun isLapisLazuli(): Boolean {
        return getId() === DYE && getDyeColor().equals(DyeColor.BLUE)
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    fun isCocoaBeans(): Boolean {
        return getId() === DYE && getDyeColor().equals(DyeColor.BROWN)
    }

    fun getDyeColor(): DyeColor {
        return DyeColor.getByDyeData(meta)
    }

    companion object {
        @Deprecated
        val WHITE: Int = DyeColor.WHITE.getDyeData()

        @Deprecated
        val ORANGE: Int = DyeColor.ORANGE.getDyeData()

        @Deprecated
        val MAGENTA: Int = DyeColor.MAGENTA.getDyeData()

        @Deprecated
        val LIGHT_BLUE: Int = DyeColor.LIGHT_BLUE.getDyeData()

        @Deprecated
        val YELLOW: Int = DyeColor.YELLOW.getDyeData()

        @Deprecated
        val LIME: Int = DyeColor.LIME.getDyeData()

        @Deprecated
        val PINK: Int = DyeColor.PINK.getDyeData()

        @Deprecated
        val GRAY: Int = DyeColor.GRAY.getDyeData()

        @Deprecated
        val LIGHT_GRAY: Int = DyeColor.LIGHT_GRAY.getDyeData()

        @Deprecated
        val CYAN: Int = DyeColor.CYAN.getDyeData()

        @Deprecated
        val PURPLE: Int = DyeColor.PURPLE.getDyeData()

        @Deprecated
        val BLUE: Int = DyeColor.BLUE.getDyeData()

        @Deprecated
        val BROWN: Int = DyeColor.BROWN.getDyeData()

        @Deprecated
        val GREEN: Int = DyeColor.GREEN.getDyeData()

        @Deprecated
        val RED: Int = DyeColor.RED.getDyeData()

        @Deprecated
        val BLACK: Int = DyeColor.BLACK.getDyeData()
        @Deprecated
        fun getColor(meta: Int): BlockColor {
            return DyeColor.getByDyeData(meta).getColor()
        }

        @Deprecated
        fun getColorName(meta: Int): String {
            return DyeColor.getByDyeData(meta).getName()
        }
    }
}