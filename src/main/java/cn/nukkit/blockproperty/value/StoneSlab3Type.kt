package cn.nukkit.blockproperty.value

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
enum class StoneSlab3Type(color: BlockColor) {
    END_STONE_BRICK(BlockColor.SAND_BLOCK_COLOR), SMOOTH_RED_SANDSTONE(BlockColor.ORANGE_BLOCK_COLOR), POLISHED_ANDESITE(BlockColor.STONE_BLOCK_COLOR), ANDESITE(BlockColor.STONE_BLOCK_COLOR), DIORITE(BlockColor.QUARTZ_BLOCK_COLOR), POLISHED_DIORITE(BlockColor.QUARTZ_BLOCK_COLOR), GRANITE(BlockColor.DIRT_BLOCK_COLOR), POLISHED_GRANITE(BlockColor.DIRT_BLOCK_COLOR);

    private val color: BlockColor

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val englishName: String
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getColor(): BlockColor {
        return color
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTY: ArrayBlockProperty<StoneSlab3Type> = ArrayBlockProperty("stone_slab_type_3", true, values())
    }

    init {
        this.color = color
        englishName = Arrays.stream(name().split("_")).map { name -> name.substring(0, 1) + name.substring(1).toLowerCase() }.collect(Collectors.joining(" "))
    }
}