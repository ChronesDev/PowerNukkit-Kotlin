package cn.nukkit.level.generator.populator.impl

import cn.nukkit.block.Block

class PopulatorLava : Populator() {
    private var level: ChunkManager? = null
    private var randomAmount = 0
    private var baseAmount = 0
    private var random: NukkitRandom? = null
    fun setRandomAmount(amount: Int) {
        randomAmount = amount
    }

    fun setBaseAmount(amount: Int) {
        baseAmount = amount
    }

    @Override
    fun populate(level: ChunkManager?, chunkX: Int, chunkZ: Int, random: NukkitRandom, chunk: FullChunk) {
        this.random = random
        if (random.nextRange(0, 100) < 5) {
            this.level = level
            val amount: Int = random.nextRange(0, randomAmount + 1) + baseAmount
            val bx = chunkX shl 4
            val bz = chunkZ shl 4
            val tx = bx + 15
            val tz = bz + 15
            for (i in 0 until amount) {
                val x: Int = random.nextRange(0, 15)
                val z: Int = random.nextRange(0, 15)
                val y = getHighestWorkableBlock(chunk, x, z)
                if (y != -1 && chunk.getBlockId(x, y, z) === Block.AIR) {
                    chunk.setBlock(x, y, z, Block.LAVA)
                    chunk.setBlockLight(x, y, z, Block.light.get(Block.LAVA))
                    lavaSpread(bx + x, y, bz + z)
                }
            }
        }
    }

    private fun getFlowDecay(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int): Int {
        return if (level.getBlockIdAt(x1, y1, z1) !== level.getBlockIdAt(x2, y2, z2)) {
            -1
        } else {
            level.getBlockDataAt(x2, y2, z2)
        }
    }

    private fun lavaSpread(x: Int, y: Int, z: Int) {
        if (level.getChunk(x shr 4, z shr 4) == null) {
            return
        }
        var decay = getFlowDecay(x, y, z, x, y, z)
        val multiplier = 2
        if (decay > 0) {
            var smallestFlowDecay = -100
            smallestFlowDecay = getSmallestFlowDecay(x, y, z, x, y, z - 1, smallestFlowDecay)
            smallestFlowDecay = getSmallestFlowDecay(x, y, z, x, y, z + 1, smallestFlowDecay)
            smallestFlowDecay = getSmallestFlowDecay(x, y, z, x - 1, y, z, smallestFlowDecay)
            smallestFlowDecay = getSmallestFlowDecay(x, y, z, x + 1, y, z, smallestFlowDecay)
            var k = smallestFlowDecay + multiplier
            if (k >= 8 || smallestFlowDecay < 0) {
                k = -1
            }
            val topFlowDecay = getFlowDecay(x, y, z, x, y + 1, z)
            if (topFlowDecay >= 0) {
                k = if (topFlowDecay >= 8) {
                    topFlowDecay
                } else {
                    topFlowDecay or 0x08
                }
            }
            if (decay < 8 && k < 8 && k > 1 && random.nextRange(0, 4) !== 0) {
                k = decay
            }
            if (k != decay) {
                decay = k
                if (decay < 0) {
                    level.setBlockAt(x, y, z, 0)
                } else {
                    level.setBlockAt(x, y, z, Block.LAVA, decay)
                    lavaSpread(x, y, z)
                    return
                }
            }
        }
        if (canFlowInto(x, y - 1, z)) {
            if (decay >= 8) {
                flowIntoBlock(x, y - 1, z, decay)
            } else {
                flowIntoBlock(x, y - 1, z, decay or 0x08)
            }
        } else if (decay >= 0 && (decay == 0 || !canFlowInto(x, y - 1, z))) {
            val flags = getOptimalFlowDirections(x, y, z)
            var l = decay + multiplier
            if (decay >= 8) {
                l = 1
            }
            if (l >= 8) {
                return
            }
            if (flags[0]) {
                flowIntoBlock(x - 1, y, z, l)
            }
            if (flags[1]) {
                flowIntoBlock(x + 1, y, z, l)
            }
            if (flags[2]) {
                flowIntoBlock(x, y, z - 1, l)
            }
            if (flags[3]) {
                flowIntoBlock(x, y, z + 1, l)
            }
        }
    }

    private fun flowIntoBlock(x: Int, y: Int, z: Int, newFlowDecay: Int) {
        if (level.getBlockIdAt(x, y, z) === Block.AIR) {
            level.setBlockAt(x, y, z, Block.LAVA, newFlowDecay)
            lavaSpread(x, y, z)
        }
    }

    private fun canFlowInto(x: Int, y: Int, z: Int): Boolean {
        val id: Int = level.getBlockIdAt(x, y, z)
        return id == Block.AIR || id == Block.LAVA || id == Block.STILL_LAVA
    }

    private fun calculateFlowCost(xx: Int, yy: Int, zz: Int, accumulatedCost: Int, previousDirection: Int): Int {
        var cost = 1000
        for (j in 0..3) {
            if (j == 0 && previousDirection == 1 ||
                    j == 1 && previousDirection == 0 ||
                    j == 2 && previousDirection == 3 ||
                    j == 3 && previousDirection == 2) {
                var x = xx
                var z = zz
                if (j == 0) {
                    --x
                } else if (j == 1) {
                    ++x
                } else if (j == 2) {
                    --z
                } else if (j == 3) {
                    ++z
                }
                if (!canFlowInto(x, yy, z)) {
                    continue
                } else if (canFlowInto(x, yy, z) && level.getBlockDataAt(x, yy, z) === 0) {
                    continue
                } else if (canFlowInto(x, yy - 1, z)) {
                    return accumulatedCost
                }
                if (accumulatedCost >= 4) {
                    continue
                }
                val realCost = calculateFlowCost(x, yy, z, accumulatedCost + 1, j)
                if (realCost < cost) {
                    cost = realCost
                }
            }
        }
        return cost
    }

    private fun getOptimalFlowDirections(xx: Int, yy: Int, zz: Int): BooleanArray {
        val flowCost = intArrayOf(0, 0, 0, 0)
        val isOptimalFlowDirection = booleanArrayOf(false, false, false, false)
        for (j in 0..3) {
            flowCost[j] = 1000
            var x = xx
            var z = zz
            if (j == 0) {
                --x
            } else if (j == 1) {
                ++x
            } else if (j == 2) {
                --z
            } else if (j == 3) {
                ++z
            }
            if (canFlowInto(x, yy - 1, z)) {
                flowCost[j] = 0
            } else {
                flowCost[j] = calculateFlowCost(x, yy, z, 1, j)
            }
        }
        var minCost = flowCost[0]
        for (i in 1..3) {
            if (flowCost[i] < minCost) {
                minCost = flowCost[i]
            }
        }
        for (i in 0..3) {
            isOptimalFlowDirection[i] = flowCost[i] == minCost
        }
        return isOptimalFlowDirection
    }

    private fun getSmallestFlowDecay(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int, decay: Int): Int {
        var blockDecay = getFlowDecay(x1, y1, z1, x2, y2, z2)
        if (blockDecay < 0) {
            return decay
        } else if (blockDecay >= 8) {
            blockDecay = 0
        }
        return if (decay >= 0 && blockDecay >= decay) decay else blockDecay
    }

    private fun getHighestWorkableBlock(chunk: FullChunk, x: Int, z: Int): Int {
        var y: Int
        y = 127
        while (y >= 0) {
            val b: Int = chunk.getBlockId(x, y, z)
            if (b == Block.AIR) {
                break
            }
            y--
        }
        return if (y == 0) -1 else y
    }
}