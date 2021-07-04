package cn.nukkit.blockproperty.value

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author LoboMetalurgico
 * @since 09/06/2021
 */
@PowerNukkitOnly
@Since("1.5.0.0-PN")
@AllArgsConstructor
enum class PrismarineBlockType {
    DEFAULT("Prismarine", BlockColor.CYAN_BLOCK_COLOR), DARK("Dark Prismarine", BlockColor.DIAMOND_BLOCK_COLOR), BRICKS("Prismarine Bricks", BlockColor.DIAMOND_BLOCK_COLOR);

    @get:Since("1.5.0.0-PN")
    @get:PowerNukkitOnly
    val englishName: String? = null

    @get:Since("1.5.0.0-PN")
    @get:PowerNukkitOnly
    val color: BlockColor? = null
}