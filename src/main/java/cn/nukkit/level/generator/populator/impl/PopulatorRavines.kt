package cn.nukkit.level.generator.populator.impl

import cn.nukkit.block.Block

class PopulatorRavines : Populator() {
    protected var checkAreaSize = 8
    private var random: Random? = null
    private var worldLong1: Long = 0
    private var worldLong2: Long = 0
    private val ravineRarity = 1 //2
    private val ravineMinAltitude = 20
    private val ravineMaxAltitude = 67
    private val ravineMinLength = 84
    private val ravineMaxLength = 111
    private val ravineDepth = 3.0
    private val worldHeightCap = 1 shl 8
    private val a = FloatArray(1024)
    @Override
    fun populate(level: ChunkManager, chunkX: Int, chunkZ: Int, random: NukkitRandom?, chunk: FullChunk?) {
        this.random = Random()
        this.random.setSeed(level.getSeed())
        worldLong1 = this.random.nextLong()
        worldLong2 = this.random.nextLong()
        val i = checkAreaSize
        for (x in chunkX - i..chunkX + i) for (z in chunkZ - i..chunkZ + i) {
            val l3 = x * worldLong1
            val l4 = z * worldLong2
            this.random.setSeed(l3 xor l4 xor level.getSeed())
            generateChunk(chunkX, chunkZ, level.getChunk(chunkX, chunkZ))
        }
    }

    protected fun generateChunk(chunkX: Int, chunkZ: Int, generatingChunkBuffer: FullChunk) {
        if (random.nextInt(300) >= ravineRarity) return
        val d1: Double = chunkX * 16 + random.nextInt(16)
        val d2 = numberInRange(random, ravineMinAltitude, ravineMaxAltitude).toDouble()
        val d3: Double = chunkZ * 16 + random.nextInt(16)
        val i = 1
        for (j in 0 until i) {
            val f1: Float = random.nextFloat() * 3.141593f * 2.0f
            val f2: Float = (random.nextFloat() - 0.5f) * 2.0f / 8.0f
            val f3: Float = (random.nextFloat() * 2.0f + random.nextFloat()) * 2.0f
            val size = numberInRange(random, ravineMinLength, ravineMaxLength)
            createRavine(random.nextLong(), generatingChunkBuffer, d1, d2, d3, f3, f1, f2, size, ravineDepth)
        }
    }

    protected fun createRavine(paramLong: Long, generatingChunkBuffer: FullChunk, paramDouble1: Double, paramDouble2: Double, paramDouble3: Double,
                               paramFloat1: Float, paramFloat2: Float, paramFloat3: Float, size: Int, paramDouble4: Double) {
        var paramDouble1 = paramDouble1
        var paramDouble2 = paramDouble2
        var paramDouble3 = paramDouble3
        var paramFloat2 = paramFloat2
        var paramFloat3 = paramFloat3
        val localRandom = Random(paramLong)
        val chunkX: Int = generatingChunkBuffer.getX()
        val chunkZ: Int = generatingChunkBuffer.getZ()
        val d1 = (chunkX * 16 + 8).toDouble()
        val d2 = (chunkZ * 16 + 8).toDouble()
        var f1 = 0.0f
        var f2 = 0.0f
        val i = 0
        var f3 = 1.0f
        var j = 0
        while (true) {
            if (j >= worldHeightCap) break
            if (j == 0 || localRandom.nextInt(3) === 0) {
                f3 = 1.0f + localRandom.nextFloat() * localRandom.nextFloat() * 1.0f
            }
            a[j] = f3 * f3
            j++
        }
        for (stepCount in 0 until size) {
            var d3: Double = 1.5 + MathHelper.sin(stepCount * 3.141593f / size) * paramFloat1 * 1.0f
            var d4 = d3 * paramDouble4
            d3 *= localRandom.nextFloat() * 0.25 + 0.75
            d4 *= localRandom.nextFloat() * 0.25 + 0.75
            val f4: Float = MathHelper.cos(paramFloat3)
            val f5: Float = MathHelper.sin(paramFloat3)
            paramDouble1 += MathHelper.cos(paramFloat2) * f4
            paramDouble2 += f5.toDouble()
            paramDouble3 += MathHelper.sin(paramFloat2) * f4
            paramFloat3 *= 0.7f
            paramFloat3 += f2 * 0.05f
            paramFloat2 += f1 * 0.05f
            f2 *= 0.8f
            f1 *= 0.5f
            f2 += (localRandom.nextFloat() - localRandom.nextFloat()) * localRandom.nextFloat() * 2.0f
            f1 += (localRandom.nextFloat() - localRandom.nextFloat()) * localRandom.nextFloat() * 4.0f
            if (i == 0 && localRandom.nextInt(4) === 0) {
                continue
            }
            val d5 = paramDouble1 - d1
            val d6 = paramDouble3 - d2
            val d7: Double = (size - stepCount).toDouble()
            val d8 = (paramFloat1 + 2.0f + 16.0f).toDouble()
            if (d5 * d5 + d6 * d6 - d7 * d7 > d8 * d8) {
                return
            }
            if (paramDouble1 < d1 - 16.0 - d3 * 2.0 || paramDouble3 < d2 - 16.0 - d3 * 2.0 || paramDouble1 > d1 + 16.0 + d3 * 2.0 || paramDouble3 > d2 + 16.0 + d3 * 2.0) continue
            var k: Int = MathHelper.floor(paramDouble1 - d3) - chunkX * 16 - 1
            var m: Int = MathHelper.floor(paramDouble1 + d3) - chunkZ * 16 + 1
            var maxY: Int = MathHelper.floor(paramDouble2 - d4) - 1
            var minY: Int = MathHelper.floor(paramDouble2 + d4) + 1
            var i2: Int = MathHelper.floor(paramDouble3 - d3) - chunkX * 16 - 1
            var i3: Int = MathHelper.floor(paramDouble3 + d3) - chunkZ * 16 + 1
            if (k < 0) k = 0
            if (m > 16) m = 16
            if (maxY < 1) maxY = 1
            if (minY > worldHeightCap - 8) minY = worldHeightCap - 8
            if (i2 < 0) i2 = 0
            if (i3 > 16) i3 = 16
            var i4 = 0
            run {
                var localX = k
                while (i4 == 0 && localX < m) {
                    var localZ = i2
                    while (i4 == 0 && localZ < i3) {
                        var localY = minY + 1
                        while (i4 == 0 && localY >= maxY - 1) {
                            if (localY < 0) {
                                localY--
                                continue
                            }
                            if (localY < worldHeightCap) {
                                val materialAtPosition: Int = generatingChunkBuffer.getBlockId(localX, localY, localZ)
                                if (materialAtPosition == Block.WATER
                                        || materialAtPosition == Block.STILL_WATER) {
                                    i4 = 1
                                }
                                if (localY != maxY - 1 && localX != k && localX != m - 1 && localZ != i2 && localZ != i3 - 1) localY = maxY
                            }
                            localY--
                        }
                        localZ++
                    }
                    localX++
                }
            }
            if (i4 != 0) {
                continue
            }
            for (localX in k until m) {
                val d9: Double = (localX + chunkX * 16 + 0.5 - paramDouble1) / d3
                for (localZ in i2 until i3) {
                    val d10: Double = (localZ + chunkZ * 16 + 0.5 - paramDouble3) / d3
                    if (d9 * d9 + d10 * d10 < 1.0) {
                        for (localY in minY downTo maxY) {
                            val d11: Double = (localY - 1 + 0.5 - paramDouble2) / d4
                            if ((d9 * d9 + d10 * d10) * a[localY - 1] + d11 * d11 / 6.0 < 1.0) {
                                val material: Int = generatingChunkBuffer.getBlockId(localX, localY, localZ)
                                if (material == Block.GRASS) {
                                    if (localY - 1 < 10) {
                                        generatingChunkBuffer.setBlock(localX, localY, localZ, Block.LAVA)
                                    } else {
                                        generatingChunkBuffer.setBlock(localX, localY, localZ, Block.AIR)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (i != 0) break
        }
    }

    companion object {
        fun numberInRange(random: Random?, min: Int, max: Int): Int {
            return min + random.nextInt(max - min + 1)
        }
    }
}