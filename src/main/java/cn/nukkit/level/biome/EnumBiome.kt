package cn.nukkit.level.biome

import cn.nukkit.level.biome.impl.HellBiome

/**
 * @author DaPorkchop_
 *
 *
 * A more effective way of accessing specific biomes (to prevent Biome.getBiome(Biome.OCEAN) and such)
 * Also just looks cleaner than listing everything as static final in [Biome]
 *
 */
enum class EnumBiome(id: Int, biome: Biome) {
    OCEAN(0, OceanBiome()),  //
    PLAINS(1, PlainsBiome()), DESERT(2, DesertBiome()), EXTREME_HILLS(3, ExtremeHillsBiome()), FOREST(4, ForestBiome()), TAIGA(5, TaigaBiome()), SWAMP(6, SwampBiome()), RIVER(7, RiverBiome()),  //
    HELL(8, HellBiome()), FROZEN_OCEAN(10, FrozenOceanBiome()),  //DOES NOT GENERATE NATUALLY
    FROZEN_RIVER(11, FrozenRiverBiome()), ICE_PLAINS(12, IcePlainsBiome()), MUSHROOM_ISLAND(14, MushroomIslandBiome()),  //
    MUSHROOM_ISLAND_SHORE(15, MushroomIslandShoreBiome()), BEACH(16, BeachBiome()), DESERT_HILLS(17, DesertHillsBiome()), FOREST_HILLS(18, ForestHillsBiome()), TAIGA_HILLS(19, TaigaHillsBiome()), EXTREME_HILLS_EDGE(20, ExtremeHillsEdgeBiome()),  //DOES NOT GENERATE NATUALLY
    JUNGLE(21, JungleBiome()), JUNGLE_HILLS(22, JungleHillsBiome()), JUNGLE_EDGE(23, JungleEdgeBiome()), DEEP_OCEAN(24, DeepOceanBiome()), STONE_BEACH(25, StoneBeachBiome()), COLD_BEACH(26, ColdBeachBiome()), BIRCH_FOREST(27, ForestBiome(ForestBiome.TYPE_BIRCH)), BIRCH_FOREST_HILLS(28, ForestHillsBiome(ForestHillsBiome.TYPE_BIRCH)), ROOFED_FOREST(29, RoofedForestBiome()), COLD_TAIGA(30, ColdTaigaBiome()), COLD_TAIGA_HILLS(31, ColdTaigaHillsBiome()), MEGA_TAIGA(32, MegaTaigaBiome()), MEGA_TAIGA_HILLS(33, MegaTaigaHillsBiome()), EXTREME_HILLS_PLUS(34, ExtremeHillsPlusBiome()), SAVANNA(35, SavannaBiome()), SAVANNA_PLATEAU(36, SavannaPlateauBiome()), MESA(37, MesaBiome()), MESA_PLATEAU_F(38, MesaPlateauFBiome()), MESA_PLATEAU(39, MesaPlateauBiome()),  //    All biomes below this comment are mutated variants of existing biomes
    SUNFLOWER_PLAINS(129, SunflowerPlainsBiome()), DESERT_M(130, DesertMBiome()), EXTREME_HILLS_M(131, ExtremeHillsMBiome()), FLOWER_FOREST(132, FlowerForestBiome()), TAIGA_M(133, TaigaMBiome()), SWAMPLAND_M(134, SwamplandMBiome()),  //no, the following jumps in IDs are NOT mistakes
    ICE_PLAINS_SPIKES(140, IcePlainsSpikesBiome()), JUNGLE_M(149, JungleMBiome()), JUNGLE_EDGE_M(151, JungleEdgeMBiome()), BIRCH_FOREST_M(155, ForestBiome(ForestBiome.TYPE_BIRCH_TALL)), BIRCH_FOREST_HILLS_M(156, ForestHillsBiome(ForestBiome.TYPE_BIRCH_TALL)), ROOFED_FOREST_M(157, RoofedForestMBiome()), COLD_TAIGA_M(158, ColdTaigaMBiome()), MEGA_SPRUCE_TAIGA(160, MegaSpruceTaigaBiome()), EXTREME_HILLS_PLUS_M(162, ExtremeHillsPlusMBiome()), SAVANNA_M(163, SavannaMBiome()), SAVANNA_PLATEAU_M(164, SavannaPlateauMBiome()), MESA_BRYCE(165, MesaBryceBiome()), MESA_PLATEAU_F_M(166, MesaPlateauFMBiome()), MESA_PLATEAU_M(167, MesaPlateauMBiome());

    val id: Int
    val biome: Biome

    companion object {
        /**
         * You really shouldn't use this method if you can help it, reference the biomes directly!
         *
         * @param id biome id
         * @return biome
         */
        @Deprecated
        fun getBiome(id: Int): Biome {
            return Biome.getBiome(id)
        }

        /**
         * You really shouldn't use this method if you can help it, reference the biomes directly!
         *
         * @param name biome name
         * @return biome
         */
        @Deprecated
        fun getBiome(name: String): Biome? {
            return Biome.getBiome(name)
        }
    }

    init {
        Biome.register(id, biome)
        this.id = id
        this.biome = biome
    }
}