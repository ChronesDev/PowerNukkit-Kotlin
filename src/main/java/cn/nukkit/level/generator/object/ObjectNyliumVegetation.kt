package cn.nukkit.level.generator.`object`

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
object ObjectNyliumVegetation {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun growVegetation(level: ChunkManager, pos: Vector3, random: NukkitRandom) {
        for (i in 0..127) {
            var num = 0
            var x: Int = pos.getFloorX()
            var y: Int = pos.getFloorY() + 1
            var z: Int = pos.getFloorZ()
            var crimson = level.getBlockIdAt(x, y - 1, z) === BlockID.CRIMSON_NYLIUM
            while (true) {
                if (num >= i / 16) {
                    if (level.getBlockIdAt(x, y, z) === BlockID.AIR) {
                        if (crimson) {
                            if (random.nextBoundedInt(8) === 0) {
                                if (random.nextBoundedInt(8) === 0) {
                                    level.setBlockAt(x, y, z, BlockID.WARPED_FUNGUS)
                                } else {
                                    level.setBlockAt(x, y, z, BlockID.CRIMSON_FUNGUS)
                                }
                            } else {
                                level.setBlockAt(x, y, z, BlockID.CRIMSON_ROOTS)
                            }
                        } else {
                            if (random.nextBoundedInt(8) === 0) {
                                if (random.nextBoundedInt(8) === 0) {
                                    level.setBlockAt(x, y, z, BlockID.CRIMSON_FUNGUS)
                                } else {
                                    level.setBlockAt(x, y, z, BlockID.WARPED_FUNGUS)
                                }
                            } else {
                                if (random.nextBoolean()) {
                                    level.setBlockAt(x, y, z, BlockID.WARPED_ROOTS)
                                } else {
                                    level.setBlockIdAt(x, y, z, BlockID.NETHER_SPROUTS_BLOCK)
                                }
                            }
                        }
                    }
                    break
                }
                x += random.nextRange(-1, 1)
                y += random.nextRange(-1, 1) * random.nextBoundedInt(3) / 2
                z += random.nextRange(-1, 1)
                val id: Int = level.getBlockIdAt(x, y - 1, z)
                crimson = id == BlockID.CRIMSON_NYLIUM
                if (!crimson && id != BlockID.WARPED_NYLIUM || y > 255 || y < 0) {
                    break
                }
                ++num
            }
        }
    }
}