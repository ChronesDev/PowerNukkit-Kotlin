package cn.nukkit.level.biome.impl.extremehills

import cn.nukkit.api.NewRakNetOnly

/**
 * @author DaPorkchop_ (Nukkit Project)
 *
 *
 * make sure this is touching another extreme hills type or it'll look dumb
 *
 * very smooth hills with flat areas between
 */
class ExtremeHillsMBiome @JvmOverloads constructor(tree: Boolean = true) : ExtremeHillsPlusBiome(tree) {
    @get:Override
    override val name: String
        get() = "Extreme Hills M"

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    fun getSurfaceState(x: Int, y: Int, z: Int): BlockState {
        return if (gravelNoise.noise2D(x, z, true) < -0.75f) STATE_GRAVEL else STATE_GRASS
    }

    @NewRakNetOnly
    @Override
    fun getSurfaceDepth(x: Int, y: Int, z: Int): Int {
        return if (gravelNoise.noise2D(x, z, true) < -0.75f) 4 else 1
    }

    @NewRakNetOnly
    @Override
    fun getGroundDepth(x: Int, y: Int, z: Int): Int {
        return if (gravelNoise.noise2D(x, z, true) < -0.75f) 0 else 4
    }

    @Override
    override fun doesOverhang(): Boolean {
        return false
    }

    companion object {
        private val STATE_GRAVEL: BlockState = BlockState.of(GRAVEL)
        private val STATE_GRASS: BlockState = BlockState.of(GRASS)
        private val gravelNoise: SimplexF = SimplexF(NukkitRandom(0), 1f, 1 / 4f, 1 / 64f)
    }

    init {
        this.setBaseHeight(1f)
        this.setHeightVariation(0.5f)
    }
}