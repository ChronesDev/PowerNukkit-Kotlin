package cn.nukkit.utils

import cn.nukkit.api.PowerNukkitOnly

class BannerPattern(val type: Type?, color: DyeColor?) {
    private val color: DyeColor?
    fun getColor(): DyeColor? {
        return color
    }

    enum class Type(override val name: String) {
        PATTERN_BOTTOM_STRIPE("bs"), PATTERN_TOP_STRIPE("ts"), PATTERN_LEFT_STRIPE("ls"), PATTERN_RIGHT_STRIPE("rs"), PATTERN_CENTER_STRIPE("cs"), PATTERN_MIDDLE_STRIPE("ms"), PATTERN_DOWN_RIGHT_STRIPE("drs"), PATTERN_DOWN_LEFT_STRIPE("dls"), PATTERN_SMALL_STRIPES("ss"), PATTERN_DIAGONAL_CROSS("cr"), PATTERN_SQUARE_CROSS("sc"), PATTERN_LEFT_OF_DIAGONAL("ld"), PATTERN_RIGHT_OF_UPSIDE_DOWN_DIAGONAL("rud"), PATTERN_LEFT_OF_UPSIDE_DOWN_DIAGONAL("lud"), PATTERN_RIGHT_OF_DIAGONAL("rd"), PATTERN_VERTICAL_HALF_LEFT("vh"), PATTERN_VERTICAL_HALF_RIGHT("vhr"), PATTERN_HORIZONTAL_HALF_TOP("hh"), PATTERN_HORIZONTAL_HALF_BOTTOM("hhb"), PATTERN_BOTTOM_LEFT_CORNER("bl"), PATTERN_BOTTOM_RIGHT_CORNER("br"), PATTERN_TOP_LEFT_CORNER("tl"), PATTERN_TOP_RIGHT_CORNER("tr"), PATTERN_BOTTOM_TRIANGLE("bt"), PATTERN_TOP_TRIANGLE("tt"), PATTERN_BOTTOM_TRIANGLE_SAWTOOTH("bts"), PATTERN_TOP_TRIANGLE_SAWTOOTH("tts"), PATTERN_MIDDLE_CIRCLE("mc"), PATTERN_MIDDLE_RHOMBUS("mr"), PATTERN_BORDER("bo"), PATTERN_CURLY_BORDER("cbo"), PATTERN_BRICK("bri"), PATTERN_GRADIENT("gra"), PATTERN_GRADIENT_UPSIDE_DOWN("gru"), PATTERN_CREEPER("cre"), PATTERN_SKULL("sku"), PATTERN_FLOWER("flo"), PATTERN_MOJANG("moj"), PATTERN_SNOUT("pig");

        companion object {
            private val BY_NAME: Map<String, Type> = HashMap()
            fun getByName(name: String): Type? {
                return BY_NAME[name]
            }

            init {
                for (type in values()) {
                    BY_NAME.put(cn.nukkit.utils.type.getName(), cn.nukkit.utils.type)
                }
            }
        }
    }

    companion object {
        fun fromCompoundTag(compoundTag: CompoundTag): BannerPattern {
            return BannerPattern(Type.getByName(if (compoundTag.contains("Pattern")) compoundTag.getString("Pattern") else ""), if (compoundTag.contains("Color")) DyeColor.getByDyeData(compoundTag.getInt("Color")) else DyeColor.BLACK)
        }
    }

    init {
        this.color = color
    }
}