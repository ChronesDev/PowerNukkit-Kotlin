package cn.nukkit.blockproperty.value

import cn.nukkit.api.PowerNukkitOnly

@Since("1.4.0.0-PN")
@PowerNukkitOnly
@RequiredArgsConstructor
enum class StructureBlockType {
    INVALID("Structure Block"), DATA("Data Structure Block"), SAVE("Save Structure Block"), LOAD("Load Structure Block"), CORNER("Corner Structure Block"), EXPORT("Export Structure Block");

    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    val englishName: String? = null
}