package cn.nukkit.level.biome.impl.mushroom

import cn.nukkit.api.RemovedFromNewRakNet

class MushroomIslandBiome : GrassyBiome() {
    @get:Override
    val name: String
        get() = "Mushroom Island"

    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    fun getSurfaceBlock(y: Int): Int {
        return if (useNewRakNetSurface()) {
            getSurfaceId(0, y, 0) shr 4
        } else Block.MYCELIUM
    }

    init {
        val mushroomPopulator = MushroomPopulator()
        mushroomPopulator.setBaseAmount(1)
        addPopulator(mushroomPopulator)
        this.setBaseHeight(0.2f)
        this.setHeightVariation(0.3f)
    }
}