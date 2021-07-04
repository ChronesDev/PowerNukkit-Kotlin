package cn.nukkit.blockproperty.value

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author LoboMetalurgico
 * @since 09/06/2021
 */
@PowerNukkitOnly
@Since("1.5.0.0-PN")
@AllArgsConstructor
enum class SandStoneType {
    DEFAULT("Sandstone"), HEIROGLYPHS("Chiseled Sandstone"), CUT("Cut Sandstone"), SMOOTH("Smooth Sandstone");

    @get:Since("1.5.0.0-PN")
    @get:PowerNukkitOnly
    val englishName: String? = null
}