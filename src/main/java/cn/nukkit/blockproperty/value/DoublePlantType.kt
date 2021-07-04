package cn.nukkit.blockproperty.value

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 * @since 2021-05-22
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@RequiredArgsConstructor
enum class DoublePlantType @SuppressWarnings("UnstableApiUsage") constructor() {
    SUNFLOWER, SYRINGA("Lilac", false), GRASS("Double Tallgrass", true), FERN("Large Fern", true), ROSE("Rose Bush", false), PAEONIA("Peony", false);

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val englishName: String

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val isReplaceable: Boolean

    init {
        englishName = capitalize(name())
        isReplaceable = false
    }
}