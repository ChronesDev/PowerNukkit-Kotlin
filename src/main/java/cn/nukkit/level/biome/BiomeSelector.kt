package cn.nukkit.level.biome

import cn.nukkit.level.generator.noise.nukkit.f.SimplexF

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
//WIP
//do not touch lol
class BiomeSelector(random: NukkitRandom?) {
    private val temperature: SimplexF
    private val rainfall: SimplexF
    private val river: SimplexF
    private val ocean: SimplexF
    private val hills: SimplexF
    fun pickBiome(x: Int, z: Int): Biome {
        /*double noiseOcean = ocean.noise2D(x, z, true);
        double noiseTemp = temperature.noise2D(x, z, true);
        double noiseRain = rainfall.noise2D(x, z, true);
        if (noiseOcean < -0.15) {
            if (noiseOcean < -0.9) {
                return EnumBiome.MUSHROOM_ISLAND.biome;
            } else {
                return EnumBiome.OCEAN.biome;
            }
        }
        double noiseRiver = Math.abs(river.noise2D(x, z, true));
        if (noiseRiver < 0.04) {
            return EnumBiome.RIVER.biome;
        }
        return EnumBiome.OCEAN.biome;*/

        // > using actual biome selectors in 2018
        //x >>= 6;
        //z >>= 6;

        //here's a test for just every biome, for making sure there's no crashes:
        //return Biome.unorderedBiomes.get(Math.abs(((int) x >> 5) ^ 6457109 * ((int) z >> 5) ^ 9800471) % Biome.unorderedBiomes.size());

        //a couple random high primes: 6457109 9800471 7003231

        //here's a test for mesas
        /*boolean doPlateau = ocean.noise2D(x, z, true) < 0f;
        boolean doF = rainfall.noise2D(x, z, true) < -0.5f;
        if (doPlateau)  {
            boolean doM = temperature.noise2D(x, z, true) < 0f;
            if (doM && doF)    {
                return EnumBiome.MESA_PLATEAU_F_M.biome;
            } else if (doM) {
                return EnumBiome.MESA_PLATEAU_M.biome;
            } else if (doF) {
                return EnumBiome.MESA_PLATEAU_F.biome;
            } else {
                return EnumBiome.MESA_PLATEAU.biome;
            }
        } else {
            return doF ? EnumBiome.MESA_BRYCE.biome : EnumBiome.MESA.biome;
        }*/

        //here's a test for extreme hills + oceans
        /*double noiseOcean = ocean.noise2D(x, z, true);
        if (noiseOcean < -0.15f) {
            return EnumBiome.OCEAN.biome;
        } else if (noiseOcean < -0.19f) {
            return EnumBiome.STONE_BEACH.biome;
        } else {
            boolean plus = temperature.noise2D(x, z, true) < 0f;
            boolean m = rainfall.noise2D(x, z, true) < 0f;
            if (plus && m) {
                return EnumBiome.EXTREME_HILLS_PLUS_M.biome;
            } else if (m) {
                return EnumBiome.EXTREME_HILLS_M.biome;
            } else if (plus) {
                return EnumBiome.EXTREME_HILLS_PLUS.biome;
            } else {
                return EnumBiome.EXTREME_HILLS.biome;
            }
        }*/
        val noiseOcean: Float = ocean.noise2D(x.toFloat(), z.toFloat(), true)
        val noiseRiver: Float = river.noise2D(x.toFloat(), z.toFloat(), true)
        val temperature: Float = temperature.noise2D(x.toFloat(), z.toFloat(), true)
        val rainfall: Float = rainfall.noise2D(x.toFloat(), z.toFloat(), true)
        val biome: EnumBiome
        biome = if (noiseOcean < -0.15f) {
            if (noiseOcean < -0.91f) {
                if (noiseOcean < -0.92f) {
                    EnumBiome.MUSHROOM_ISLAND
                } else {
                    EnumBiome.MUSHROOM_ISLAND_SHORE
                }
            } else {
                if (rainfall < 0f) {
                    EnumBiome.OCEAN
                } else {
                    EnumBiome.DEEP_OCEAN
                }
            }
        } else if (Math.abs(noiseRiver) < 0.04f) {
            if (temperature < -0.3f) {
                EnumBiome.FROZEN_RIVER
            } else {
                EnumBiome.RIVER
            }
        } else {
            val hills: Float = hills.noise2D(x.toFloat(), z.toFloat(), true)
            if (temperature < -0.379f) {
                //freezing
                if (noiseOcean < -0.12f) {
                    EnumBiome.COLD_BEACH
                } else if (rainfall < 0f) {
                    if (hills < -0.1f) {
                        EnumBiome.COLD_TAIGA
                    } else if (hills < 0.5f) {
                        EnumBiome.COLD_TAIGA_HILLS
                    } else {
                        EnumBiome.COLD_TAIGA_M
                    }
                } else {
                    if (hills < 0.7f) {
                        EnumBiome.ICE_PLAINS
                    } else {
                        EnumBiome.ICE_PLAINS_SPIKES
                    }
                }
            } else if (noiseOcean < -0.12f) {
                EnumBiome.BEACH
            } else if (temperature < 0f) {
                //cold
                if (hills < 0.2f) {
                    if (rainfall < -0.5f) {
                        EnumBiome.EXTREME_HILLS_M
                    } else if (rainfall > 0.5f) {
                        EnumBiome.EXTREME_HILLS_PLUS_M
                    } else if (rainfall < 0f) {
                        EnumBiome.EXTREME_HILLS
                    } else {
                        EnumBiome.EXTREME_HILLS_PLUS
                    }
                } else {
                    if (rainfall < -0.6) {
                        EnumBiome.MEGA_TAIGA
                    } else if (rainfall > 0.6) {
                        EnumBiome.MEGA_SPRUCE_TAIGA
                    } else if (rainfall < 0.2f) {
                        EnumBiome.TAIGA
                    } else {
                        EnumBiome.TAIGA_M
                    }
                }
            } else if (temperature < 0.5f) {
                //normal
                if (temperature < 0.25f) {
                    if (rainfall < 0f) {
                        if (noiseOcean < 0f) {
                            EnumBiome.SUNFLOWER_PLAINS
                        } else {
                            EnumBiome.PLAINS
                        }
                    } else if (rainfall < 0.25f) {
                        if (noiseOcean < 0f) {
                            EnumBiome.FLOWER_FOREST
                        } else {
                            EnumBiome.FOREST
                        }
                    } else {
                        if (noiseOcean < 0f) {
                            EnumBiome.BIRCH_FOREST_M
                        } else {
                            EnumBiome.BIRCH_FOREST
                        }
                    }
                } else {
                    if (rainfall < -0.2f) {
                        if (noiseOcean < 0f) {
                            EnumBiome.SWAMPLAND_M
                        } else {
                            EnumBiome.SWAMP
                        }
                    } else if (rainfall > 0.1f) {
                        if (noiseOcean < 0.155f) {
                            EnumBiome.JUNGLE_M
                        } else {
                            EnumBiome.JUNGLE
                        }
                    } else {
                        if (noiseOcean < 0f) {
                            EnumBiome.ROOFED_FOREST_M
                        } else {
                            EnumBiome.ROOFED_FOREST
                        }
                    }
                }
            } else {
                //hot
                if (rainfall < 0f) {
                    if (noiseOcean < 0f) {
                        EnumBiome.DESERT_M
                    } else if (hills < 0f) {
                        EnumBiome.DESERT_HILLS
                    } else {
                        EnumBiome.DESERT
                    }
                } else if (rainfall > 0.4f) {
                    if (noiseOcean < 0.155f) {
                        if (hills < 0f) {
                            EnumBiome.SAVANNA_PLATEAU_M
                        } else {
                            EnumBiome.SAVANNA_M
                        }
                    } else {
                        if (hills < 0f) {
                            EnumBiome.SAVANNA_PLATEAU
                        } else {
                            EnumBiome.SAVANNA
                        }
                    }
                } else {
                    if (noiseOcean < 0f) {
                        if (hills < 0f) {
                            EnumBiome.MESA_PLATEAU_F
                        } else {
                            EnumBiome.MESA_PLATEAU_F_M
                        }
                    } else if (hills < 0f) {
                        if (noiseOcean < 0.2f) {
                            EnumBiome.MESA_PLATEAU_M
                        } else {
                            EnumBiome.MESA_PLATEAU
                        }
                    } else {
                        if (noiseOcean < 0.1f) {
                            EnumBiome.MESA_BRYCE
                        } else {
                            EnumBiome.MESA
                        }
                    }
                }
            }
        }
        return biome.biome
    }

    init {
        temperature = SimplexF(random, 2f, 1f / 8f, 1f / 2048f)
        rainfall = SimplexF(random, 2f, 1f / 8f, 1f / 2048f)
        river = SimplexF(random, 6f, 2 / 4f, 1 / 1024f)
        ocean = SimplexF(random, 6f, 2 / 4f, 1 / 2048f)
        hills = SimplexF(random, 2f, 2 / 4f, 1 / 2048f)
    }
}