package cn.nukkit.utils

import kotlin.jvm.Synchronized
import kotlin.Throws
import kotlin.jvm.Volatile
import kotlin.jvm.JvmOverloads

enum class TerracottaColor(val dyeData: Int, val terracottaData: Int, override val name: String, dyeName: String, blockColor: BlockColor) {
    BLACK(0, 15, "Black", "Ink Sack", BlockColor.BLACK_TERRACOTA_BLOCK_COLOR), RED(1, 14, "Red", "Rose Red", BlockColor.RED_TERRACOTA_BLOCK_COLOR), GREEN(2, 13, "Green", "Cactus Green", BlockColor.GREEN_TERRACOTA_BLOCK_COLOR), BROWN(3, 12, "Brown", "Cocoa Beans", BlockColor.BROWN_TERRACOTA_BLOCK_COLOR), BLUE(4, 11, "Blue", "Lapis Lazuli", BlockColor.BLUE_TERRACOTA_BLOCK_COLOR), PURPLE(5, 10, "Purple", BlockColor.PURPLE_TERRACOTA_BLOCK_COLOR), CYAN(6, 9, "Cyan", BlockColor.CYAN_TERRACOTA_BLOCK_COLOR), LIGHT_GRAY(7, 8, "Light Gray", BlockColor.LIGHT_GRAY_TERRACOTA_BLOCK_COLOR), GRAY(8, 7, "Gray", BlockColor.GRAY_TERRACOTA_BLOCK_COLOR), PINK(9, 6, "Pink", BlockColor.PINK_TERRACOTA_BLOCK_COLOR), LIME(10, 5, "Lime", BlockColor.LIME_TERRACOTA_BLOCK_COLOR), YELLOW(11, 4, "Yellow", "Dandelion Yellow", BlockColor.YELLOW_TERRACOTA_BLOCK_COLOR), LIGHT_BLUE(12, 3, "Light Blue", BlockColor.LIGHT_BLUE_TERRACOTA_BLOCK_COLOR), MAGENTA(13, 2, "Magenta", BlockColor.MAGENTA_TERRACOTA_BLOCK_COLOR), ORANGE(14, 1, "Orange", BlockColor.ORANGE_TERRACOTA_BLOCK_COLOR), WHITE(15, 0, "White", "Bone Meal", BlockColor.WHITE_TERRACOTA_BLOCK_COLOR);

    val dyeName: String
    private val blockColor: BlockColor

    constructor(dyeColorMeta: Int, terracottaColorMeta: Int, colorName: String, blockColor: BlockColor) : this(dyeColorMeta, terracottaColorMeta, colorName, "$colorName Dye", blockColor) {}

    val color: cn.nukkit.utils.BlockColor
        get() = blockColor

    companion object {
        private val BY_TERRACOTA_DATA: Array<TerracottaColor>
        private val BY_DYE_DATA: Array<TerracottaColor>
        fun getByDyeData(dyeColorMeta: Int): TerracottaColor {
            return BY_DYE_DATA[dyeColorMeta and 0x0f]
        }

        fun getByTerracottaData(terracottaColorMeta: Int): TerracottaColor {
            return BY_TERRACOTA_DATA[terracottaColorMeta and 0x0f]
        }

        init {
            BY_DYE_DATA = values()
            BY_TERRACOTA_DATA = values()
            for (color in values()) {
                BY_TERRACOTA_DATA[cn.nukkit.utils.color.terracottaColorMeta and 0x0f] = cn.nukkit.utils.color
                BY_DYE_DATA[cn.nukkit.utils.color.dyeColorMeta and 0x0f] = cn.nukkit.utils.color
            }
        }
    }

    init {
        this.blockColor = blockColor
        this.dyeName = dyeName
    }
}