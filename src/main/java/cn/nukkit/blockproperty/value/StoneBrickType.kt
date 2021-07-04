package cn.nukkit.blockproperty.value

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author LoboMetalurgico
 * @since 09/06/2021
 */
@PowerNukkitOnly
@Since("1.5.0.0-PN")
@AllArgsConstructor
enum class StoneBrickType {
    DEFAULT("Stone Bricks"), MOSSY("Mossy Stone Bricks"), CRACKED("Cracked Stone Bricks"), CHISELED("Chiseled Stone Bricks"), SMOOTH("Smooth Stone Bricks");

    @get:Since("1.5.0.0-PN")
    @get:PowerNukkitOnly
    val englishName: String? = null
}