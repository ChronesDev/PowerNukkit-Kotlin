package cn.nukkit.utils

import cn.nukkit.api.PowerNukkitDifference

enum class DyeColor(
        /**
         * The `minecraft:dye` meta from `0-15` that represents the source of a dye. Includes
         * ink_sac, bone_meal, cocoa_beans, and lapis_lazuli.
         */
        val dyeData: Int, val woolData: Int,
        /**
         * The `minecraft:dye` meta that actually represents the item dye for that color.
         * Uses black_dye instead of ink_sac, white_dye instead of bone_meal, and so on.
         */
        @get:PowerNukkitOnly
        @get:Since("1.4.0.0-PN") val itemDyeMeta: Int, override val name: String, dyeName: String, blockColor: BlockColor, leatherColor: BlockColor) {
    BLACK(0, 15, 16, "Black", "Ink Sack", BlockColor.BLACK_BLOCK_COLOR, BlockColor(0x1D1D21)), RED(1, 14, 1, "Red", "Rose Red", BlockColor.RED_BLOCK_COLOR, BlockColor(0xB02E26)), GREEN(2, 13, 2, "Green", "Cactus Green", BlockColor.GREEN_BLOCK_COLOR, BlockColor(0x5E7C16)), BROWN(3, 12, 17, "Brown", "Cocoa Beans", BlockColor.BROWN_BLOCK_COLOR, BlockColor(0x835432)), BLUE(4, 11, 18, "Blue", "Lapis Lazuli", BlockColor.BLUE_BLOCK_COLOR, BlockColor(0x3C44AA)), PURPLE(5, 10, 5, "Purple", BlockColor.PURPLE_BLOCK_COLOR, BlockColor(0x8932B8)), CYAN(6, 9, 6, "Cyan", BlockColor.CYAN_BLOCK_COLOR, BlockColor(0x169C9C)), LIGHT_GRAY(7, 8, 7, "Light Gray", BlockColor.LIGHT_GRAY_BLOCK_COLOR, BlockColor(0x9D9D97)), GRAY(8, 7, 8, "Gray", BlockColor.GRAY_BLOCK_COLOR, BlockColor(0x474F52)), PINK(9, 6, 9, "Pink", BlockColor.PINK_BLOCK_COLOR, BlockColor(0xF38BAA)), LIME(10, 5, 10, "Lime", BlockColor.LIME_BLOCK_COLOR, BlockColor(0x80C71F)), YELLOW(11, 4, 11, "Yellow", "Dandelion Yellow", BlockColor.YELLOW_BLOCK_COLOR, BlockColor(0xFED83D)), LIGHT_BLUE(12, 3, 12, "Light Blue", BlockColor.LIGHT_BLUE_BLOCK_COLOR, BlockColor(0x3AB3DA)), MAGENTA(13, 2, 13, "Magenta", BlockColor.MAGENTA_BLOCK_COLOR, BlockColor(0xC74EBD)), ORANGE(14, 1, 14, "Orange", BlockColor.ORANGE_BLOCK_COLOR, BlockColor(0xFF9801)), WHITE(15, 0, 19, "White", "Bone Meal", BlockColor.WHITE_BLOCK_COLOR, BlockColor(0xF0F0F0));

    val dyeName: String
    private val blockColor: BlockColor
    private val leatherColor: BlockColor

    constructor(dyeColorMeta: Int, woolColorMeta: Int, itemDyeMeta: Int, colorName: String, blockColor: BlockColor) : this(dyeColorMeta, woolColorMeta, itemDyeMeta, colorName, blockColor, blockColor) {}
    constructor(dyeColorMeta: Int, woolColorMeta: Int, itemDyeMeta: Int, colorName: String, blockColor: BlockColor, leatherColor: BlockColor) : this(dyeColorMeta, woolColorMeta, itemDyeMeta, colorName, "$colorName Dye", blockColor, leatherColor) {}
    constructor(dyeColorMeta: Int, woolColorMeta: Int, itemDyeMeta: Int, colorName: String, dyeName: String, blockColor: BlockColor) : this(dyeColorMeta, woolColorMeta, itemDyeMeta, colorName, blockColor, blockColor) {}

    val color: cn.nukkit.utils.BlockColor
        get() = blockColor

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getLeatherColor(): BlockColor {
        return leatherColor
    }

    companion object {
        private val BY_WOOL_DATA: Array<DyeColor>
        private val BY_DYE_DATA: Array<DyeColor?>
        @PowerNukkitDifference(since = "1.4.0.0-PN", info = "When overflowed, instead of wrapping, the meta will be clamped, accepts the new dye metas")
        fun getByDyeData(dyeColorMeta: Int): DyeColor? {
            return BY_DYE_DATA[MathHelper.clamp(dyeColorMeta, 0, BY_DYE_DATA.size - 1)]
        }

        fun getByWoolData(woolColorMeta: Int): DyeColor {
            return BY_WOOL_DATA[woolColorMeta and 0x0f]
        }

        init {
            BY_WOOL_DATA = values()
            BY_DYE_DATA = arrayOfNulls(Arrays.stream(BY_WOOL_DATA).mapToInt { obj: DyeColor? -> cn.nukkit.utils.obj.getItemDyeMeta() }.max().orElse(0) + 1)
            for (dyeColor in BY_WOOL_DATA) {
                BY_DYE_DATA[cn.nukkit.utils.dyeColor.dyeColorMeta] = cn.nukkit.utils.dyeColor
                BY_DYE_DATA[cn.nukkit.utils.dyeColor.itemDyeMeta] = cn.nukkit.utils.dyeColor
            }
            for (color in values()) {
                BY_WOOL_DATA[cn.nukkit.utils.color.woolColorMeta and 0x0f] = cn.nukkit.utils.color
            }
        }
    }

    init {
        this.blockColor = blockColor
        this.dyeName = dyeName
        this.leatherColor = leatherColor
    }
}