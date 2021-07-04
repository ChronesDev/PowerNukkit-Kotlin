package cn.nukkit.level.biome.impl.mesa

import cn.nukkit.api.RemovedFromNewRakNet

/**
 * @author DaPorkchop_
 */
class MesaPlateauFBiome : MesaPlateauBiome() {
    @get:RemovedFromNewRakNet
    @get:Override
    @get:Since("1.4.0.0-PN")
    val coverBlock: Int
        get() = if (useNewRakNetCover()) {
            getCoverId(0, 0) shr 4
        } else GRASS

    @get:Override
    override val name: String
        get() = "Mesa Plateau F"

    init {
        val tree = PopulatorTree(BlockSapling.OAK)
        tree.setBaseAmount(2)
        tree.setRandomAmount(1)
        this.addPopulator(tree)
    }
}