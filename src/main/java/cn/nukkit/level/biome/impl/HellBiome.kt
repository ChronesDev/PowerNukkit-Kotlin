package cn.nukkit.level.biome.impl

import cn.nukkit.api.PowerNukkitOnly

class HellBiome : Biome() {
    @get:Override
    val name: String
        get() = "Hell"

    @Override
    fun canRain(): Boolean {
        return false
    }

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    val isDry: Boolean
        get() = true
}