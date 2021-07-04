package cn.nukkit.level.biome.impl.savanna

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_
 */
class SavannaMBiome : SavannaBiome() {
    @get:Override
    override val name: String
        get() = "Savanna M"

    //@Override
    //public int getSurfaceBlock(int y) {
    //    return coarseDirt ? COARSE_DIRT : DIRT;
    //}
    //@Override
    //public void preCover(int x, int z) {
    //    coarseDirt = coarseDirtNoise.noise2D(x, z, true) < 0;
    //}
    @Override
    fun doesOverhang(): Boolean {
        return true
    }

    //TODO: coarse dirt?
    //private static final Simplex coarseDirtNoise = new Simplex(new NukkitRandom(0), 1d, 1 / 4f, 1 / 16f);
    //boolean coarseDirt = false;
    init {

        //this is set to be above the build limit so that it'll actually hit it sometimes
        this.setBaseHeight(0.3625f)
        this.setHeightVariation(1.225f)
    }
}