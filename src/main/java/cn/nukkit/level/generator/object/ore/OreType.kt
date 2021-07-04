package cn.nukkit.level.generator.`object`.ore

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
//porktodo: rewrite this, the whole class is terrible and generated ores look stupid
class OreType(material: Block, clusterCount: Int, clusterSize: Int, minHeight: Int, maxHeight: Int, replaceBlockId: Int) {
    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    val fullId: Int

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    val blockId: Int

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    val blockData: Int
    val clusterCount: Int
    val clusterSize: Int
    val maxHeight: Int
    val minHeight: Int
    val replaceBlockId: Int

    constructor(material: Block, clusterCount: Int, clusterSize: Int, minHeight: Int, maxHeight: Int) : this(material, clusterCount, clusterSize, minHeight, maxHeight, STONE) {}

    fun spawn(level: ChunkManager, rand: NukkitRandom, replaceId: Int, x: Int, y: Int, z: Int): Boolean {
        val piScaled: Float = rand.nextFloat() * Math.PI as Float
        val scaleMaxX: Double = (x + 8).toFloat() + MathHelper.sin(piScaled) * clusterSize.toFloat() / 8.0f
        val scaleMinX: Double = (x + 8).toFloat() - MathHelper.sin(piScaled) * clusterSize.toFloat() / 8.0f
        val scaleMaxZ: Double = (z + 8).toFloat() + MathHelper.cos(piScaled) * clusterSize.toFloat() / 8.0f
        val scaleMinZ: Double = (z + 8).toFloat() - MathHelper.cos(piScaled) * clusterSize.toFloat() / 8.0f
        val scaleMaxY: Double = y + rand.nextBoundedInt(3) - 2
        val scaleMinY: Double = y + rand.nextBoundedInt(3) - 2
        for (i in 0 until clusterSize) {
            val sizeIncr = i as Float / clusterSize.toFloat()
            val scaleX = scaleMaxX + (scaleMinX - scaleMaxX) * sizeIncr.toDouble()
            val scaleY = scaleMaxY + (scaleMinY - scaleMaxY) * sizeIncr.toDouble()
            val scaleZ = scaleMaxZ + (scaleMinZ - scaleMaxZ) * sizeIncr.toDouble()
            val randSizeOffset: Double = rand.nextDouble() * clusterSize.toDouble() / 16.0
            val randVec1 = (MathHelper.sin(Math.PI as Float * sizeIncr) + 1.0f) as Double * randSizeOffset + 1.0
            val randVec2 = (MathHelper.sin(Math.PI as Float * sizeIncr) + 1.0f) as Double * randSizeOffset + 1.0
            val minX: Int = MathHelper.floor(scaleX - randVec1 / 2.0)
            val minY: Int = MathHelper.floor(scaleY - randVec2 / 2.0)
            val minZ: Int = MathHelper.floor(scaleZ - randVec1 / 2.0)
            val maxX: Int = MathHelper.floor(scaleX + randVec1 / 2.0)
            val maxY: Int = MathHelper.floor(scaleY + randVec2 / 2.0)
            val maxZ: Int = MathHelper.floor(scaleZ + randVec1 / 2.0)
            for (xSeg in minX..maxX) {
                val xVal = (xSeg.toDouble() + 0.5 - scaleX) / (randVec1 / 2.0)
                if (xVal * xVal < 1.0) {
                    for (ySeg in minY..maxY) {
                        val yVal = (ySeg.toDouble() + 0.5 - scaleY) / (randVec2 / 2.0)
                        if (xVal * xVal + yVal * yVal < 1.0) {
                            for (zSeg in minZ..maxZ) {
                                val zVal = (zSeg.toDouble() + 0.5 - scaleZ) / (randVec1 / 2.0)
                                if (xVal * xVal + yVal * yVal + zVal * zVal < 1.0) {
                                    if (level.getBlockIdAt(xSeg, ySeg, zSeg) === replaceBlockId) {
                                        level.setBlockAt(xSeg, ySeg, zSeg, blockId, blockData)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EMPTY_ARRAY = arrayOfNulls<OreType>(0)
    }

    init {
        fullId = material.getFullId()
        blockId = material.getId()
        blockData = material.getDamage()
        this.clusterCount = clusterCount
        this.clusterSize = clusterSize
        this.maxHeight = maxHeight
        this.minHeight = minHeight
        this.replaceBlockId = replaceBlockId
    }
}