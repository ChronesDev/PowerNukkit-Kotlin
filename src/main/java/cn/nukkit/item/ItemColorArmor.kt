package cn.nukkit.item

import cn.nukkit.nbt.tag.CompoundTag

/**
 * @author fromgate
 * @since 27.03.2016
 */
abstract class ItemColorArmor : ItemArmor {
    constructor(id: Int) : super(id) {}
    constructor(id: Int, meta: Integer?) : super(id, meta) {}
    constructor(id: Int, meta: Integer?, count: Int) : super(id, meta, count) {}
    constructor(id: Int, meta: Integer?, count: Int, name: String?) : super(id, meta, count, name) {}

    /**
     * Set leather armor color
     *
     * @param dyeColor - Dye color data value
     * @return - Return colored item
     */
    @Deprecated
    fun setColor(dyeColor: Int): ItemColorArmor {
        val blockColor: BlockColor = DyeColor.getByDyeData(dyeColor).getColor()
        return setColor(blockColor.getRed(), blockColor.getGreen(), blockColor.getBlue())
    }

    /**
     * Set leather armor color
     *
     * @param dyeColor - DyeColor object
     * @return - Return colored item
     */
    fun setColor(dyeColor: DyeColor): ItemColorArmor {
        val blockColor: BlockColor = dyeColor.getColor()
        return setColor(blockColor.getRed(), blockColor.getGreen(), blockColor.getBlue())
    }

    /**
     * Set leather armor color
     *
     * @param color - BlockColor object
     * @return - Return colored item
     */
    fun setColor(color: BlockColor): ItemColorArmor {
        return setColor(color.getRed(), color.getGreen(), color.getBlue())
    }

    /**
     * Set leather armor color
     *
     * @param r - red
     * @param g - green
     * @param b - blue
     * @return - Return colored item
     */
    fun setColor(r: Int, g: Int, b: Int): ItemColorArmor {
        val rgb = r shl 16 or (g shl 8) or b
        val tag: CompoundTag = if (this.hasCompoundTag()) this.getNamedTag() else CompoundTag()
        tag.putInt("customColor", rgb)
        this.setNamedTag(tag)
        return this
    }

    /**
     * Get color of Leather Item
     *
     * @return - BlockColor, or null if item has no color
     */
    fun getColor(): BlockColor? {
        if (!this.hasCompoundTag()) return null
        val tag: CompoundTag = this.getNamedTag()
        if (!tag.exist("customColor")) return null
        val rgb: Int = tag.getInt("customColor")
        return BlockColor(rgb)
    }
}