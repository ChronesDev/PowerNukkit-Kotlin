package cn.nukkit.level.biome.impl.roofedforest

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

class RoofedForestMBiome : RoofedForestBiome() {
    @get:Override
    override val name: String
        get() = "Roofed Forest M"

    init {
        this.setBaseHeight(0.2f)
        this.setHeightVariation(0.4f)
    }
}