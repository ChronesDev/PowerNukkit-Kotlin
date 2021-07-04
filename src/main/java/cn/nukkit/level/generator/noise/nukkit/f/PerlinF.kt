package cn.nukkit.level.generator.noise.nukkit.f

import cn.nukkit.math.NukkitRandom

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
class PerlinF(random: NukkitRandom, octaves: Float, persistence: Float, expansion: Float) : NoiseF() {
    constructor(random: NukkitRandom, octaves: Float, persistence: Float) : this(random, octaves, persistence, 1f) {}

    @Override
    override fun getNoise2D(x: Float, y: Float): Float {
        return getNoise3D(x, y, 0f)
    }

    @Override
    override fun getNoise3D(x: Float, y: Float, z: Float): Float {
        var x = x
        var y = y
        var z = z
        x += this.offsetX
        y += this.offsetY
        z += this.offsetZ
        val floorX = x.toInt()
        val floorY = y.toInt()
        val floorZ = z.toInt()
        val X = floorX and 0xFF
        val Y = floorY and 0xFF
        val Z = floorZ and 0xFF
        x -= floorX.toFloat()
        y -= floorY.toFloat()
        z -= floorZ.toFloat()

        //Fade curves
        //fX = fade(x);
        //fY = fade(y);
        //fZ = fade(z);
        val fX = x * x * x * (x * (x * 6 - 15) + 10)
        val fY = y * y * y * (y * (y * 6 - 15) + 10)
        val fZ = z * z * z * (z * (z * 6 - 15) + 10)

        //Cube corners
        val A: Int = this.perm.get(X) + Y
        val B: Int = this.perm.get(X + 1) + Y
        val AA: Int = this.perm.get(A) + Z
        val AB: Int = this.perm.get(A + 1) + Z
        val BA: Int = this.perm.get(B) + Z
        val BB: Int = this.perm.get(B + 1) + Z
        val AA1: Float = grad(this.perm.get(AA), x, y, z)
        val BA1: Float = grad(this.perm.get(BA), x - 1, y, z)
        val AB1: Float = grad(this.perm.get(AB), x, y - 1, z)
        val BB1: Float = grad(this.perm.get(BB), x - 1, y - 1, z)
        val AA2: Float = grad(this.perm.get(AA + 1), x, y, z - 1)
        val BA2: Float = grad(this.perm.get(BA + 1), x - 1, y, z - 1)
        val AB2: Float = grad(this.perm.get(AB + 1), x, y - 1, z - 1)
        val BB2: Float = grad(this.perm.get(BB + 1), x - 1, y - 1, z - 1)
        val xLerp11 = AA1 + fX * (BA1 - AA1)
        val zLerp1 = xLerp11 + fY * (AB1 + fX * (BB1 - AB1) - xLerp11)
        val xLerp21 = AA2 + fX * (BA2 - AA2)
        return zLerp1 + fZ * (xLerp21 + fY * (AB2 + fX * (BB2 - AB2) - xLerp21) - zLerp1)
    }

    init {
        octaves = octaves
        persistence = persistence
        expansion = expansion
        this.offsetX = random.nextFloat() * 256
        this.offsetY = random.nextFloat() * 256
        this.offsetZ = random.nextFloat() * 256
        this.perm = IntArray(512)
        for (i in 0..255) {
            this.perm.get(i) = random.nextBoundedInt(256)
        }
        for (i in 0..255) {
            val pos: Int = random.nextBoundedInt(256 - i) + i
            val old: Int = this.perm.get(i)
            this.perm.get(i) = this.perm.get(pos)
            this.perm.get(pos) = old
            this.perm.get(i + 256) = this.perm.get(i)
        }
    }
}