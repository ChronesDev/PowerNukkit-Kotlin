package cn.nukkit.blockproperty.value

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
enum class WoodType {
    OAK(BlockColor.WOOD_BLOCK_COLOR), SPRUCE(BlockColor.SPRUCE_BLOCK_COLOR), BIRCH(BlockColor.SAND_BLOCK_COLOR), JUNGLE(BlockColor.DIRT_BLOCK_COLOR), ACACIA(BlockColor.ORANGE_BLOCK_COLOR), DARK_OAK(BlockColor.BROWN_BLOCK_COLOR, "Dark Oak");

    private val color: BlockColor

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val englishName: String

    constructor(color: BlockColor) {
        this.color = color
        englishName = name().substring(0, 1) + name().substring(1).toLowerCase()
    }

    constructor(color: BlockColor, name: String) {
        this.color = color
        englishName = name
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getColor(): BlockColor {
        return color
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTY: ArrayBlockProperty<WoodType> = ArrayBlockProperty("wood_type", true, values())
    }
}