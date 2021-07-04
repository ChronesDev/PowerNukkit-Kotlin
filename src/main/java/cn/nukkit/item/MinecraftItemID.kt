/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cn.nukkit.item

import cn.nukkit.api.PowerNukkitOnly

/**
 * An enum containing all valid vanilla Minecraft items.
 *
 * @author joserobjr
 * @since 2020-12-20
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
enum class MinecraftItemID {
    UNKNOWN(false, true), QUARTZ_BRICKS, CRACKED_NETHER_BRICKS, CHISELED_NETHER_BRICKS, STRIPPED_WARPED_HYPHAE, STRIPPED_CRIMSON_HYPHAE, CRIMSON_HYPHAE, WARPED_HYPHAE, POLISHED_BLACKSTONE_WALL, POLISHED_BLACKSTONE_BUTTON, POLISHED_BLACKSTONE_PRESSURE_PLATE, POLISHED_BLACKSTONE_DOUBLE_SLAB(true, true), POLISHED_BLACKSTONE_SLAB, POLISHED_BLACKSTONE_STAIRS, POLISHED_BLACKSTONE, SOUL_CAMPFIRE_BLOCK_FORM(true, true), CRYING_OBSIDIAN, NETHER_GOLD_ORE, TWISTING_VINES, CHAIN_BLOCK_FORM(true, true), POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB(true, true), POLISHED_BLACKSTONE_BRICK_SLAB, BLACKSTONE_DOUBLE_SLAB(true, true), BLACKSTONE_SLAB, GILDED_BLACKSTONE, CRACKED_POLISHED_BLACKSTONE_BRICKS, CHISELED_POLISHED_BLACKSTONE, POLISHED_BLACKSTONE_BRICK_WALL, BLACKSTONE_WALL, BLACKSTONE_STAIRS, POLISHED_BLACKSTONE_BRICK_STAIRS, POLISHED_BLACKSTONE_BRICKS, BLACKSTONE, RESPAWN_ANCHOR, ANCIENT_DEBRIS, NETHERITE_BLOCK, SOUL_LANTERN, SOUL_TORCH, WARPED_DOUBLE_SLAB(true, true), CRIMSON_DOUBLE_SLAB(true, true), WARPED_SLAB, CRIMSON_SLAB, WARPED_PRESSURE_PLATE, CRIMSON_PRESSURE_PLATE, WARPED_BUTTON, CRIMSON_BUTTON, WARPED_FENCE_GATE, CRIMSON_FENCE_GATE, WARPED_FENCE, CRIMSON_FENCE, WARPED_STAIRS, CRIMSON_STAIRS, WARPED_WALL_SIGN(false, true), CRIMSON_WALL_SIGN(false, true), WARPED_STANDING_SIGN(false, true), CRIMSON_STANDING_SIGN(false, true), WARPED_TRAPDOOR, CRIMSON_TRAPDOOR, WARPED_DOOR_BLOCK_FORM(true, true), CRIMSON_DOOR_BLOCK_FORM(true, true), WARPED_PLANKS, CRIMSON_PLANKS, STRIPPED_WARPED_STEM, STRIPPED_CRIMSON_STEM, TARGET, NETHER_SPROUTS_BLOCK_FORM(true, true), SOUL_FIRE(false, true), SOUL_SOIL, POLISHED_BASALT, BASALT, WARPED_NYLIUM, CRIMSON_NYLIUM, WEEPING_VINES, SHROOMLIGHT, WARPED_FUNGUS, CRIMSON_FUNGUS, WARPED_WART_BLOCK, WARPED_STEM, CRIMSON_STEM, WARPED_ROOTS, CRIMSON_ROOTS, LODESTONE, HONEYCOMB_BLOCK, HONEY_BLOCK, BEEHIVE, BEE_NEST,  //@PowerNukkitOnly @Since("1.4.0.0-PN") STICKYPISTONARMCOLLISION("minecraft:stickyPistonArmCollision", "minecraft:sticky_piston"),
    WITHER_ROSE, LIGHT_BLOCK, LIT_BLAST_FURNACE(false, true), COMPOSTER, WOOD, JIGSAW, LAVA_CAULDRON(false, true), CAMPFIRE_BLOCK_FORM(true, true), LANTERN, SWEET_BERRY_BUSH, BELL, LOOM, BARREL, SMITHING_TABLE, FLETCHING_TABLE, CARTOGRAPHY_TABLE, LIT_SMOKER(false, true), SMOKER, STONECUTTER_BLOCK, BLAST_FURNACE, GRINDSTONE, LECTERN, DARKOAK_WALL_SIGN(false, true), DARKOAK_STANDING_SIGN(false, true), ACACIA_WALL_SIGN(false, true), ACACIA_STANDING_SIGN(false, true), JUNGLE_WALL_SIGN(false, true), JUNGLE_STANDING_SIGN(false, true), BIRCH_WALL_SIGN(false, true), BIRCH_STANDING_SIGN(false, true), SMOOTH_QUARTZ_STAIRS, RED_NETHER_BRICK_STAIRS, SMOOTH_STONE, SPRUCE_WALL_SIGN(false, true), SPRUCE_STANDING_SIGN(false, true), NORMAL_STONE_STAIRS, MOSSY_COBBLESTONE_STAIRS, END_BRICK_STAIRS, SMOOTH_SANDSTONE_STAIRS, SMOOTH_RED_SANDSTONE_STAIRS, MOSSY_STONE_BRICK_STAIRS, POLISHED_ANDESITE_STAIRS, POLISHED_DIORITE_STAIRS, POLISHED_GRANITE_STAIRS, ANDESITE_STAIRS, DIORITE_STAIRS, GRANITE_STAIRS, REAL_DOUBLE_STONE_SLAB4(true, true), REAL_DOUBLE_STONE_SLAB3(true, true), DOUBLE_STONE_SLAB4, SCAFFOLDING, BAMBOO_SAPLING(false, true), BAMBOO, DOUBLE_STONE_SLAB3, BARRIER, BUBBLE_COLUMN, TURTLE_EGG, AIR(false, true), CONDUIT, SEA_PICKLE, CARVED_PUMPKIN, SPRUCE_PRESSURE_PLATE, JUNGLE_PRESSURE_PLATE, DARK_OAK_PRESSURE_PLATE, BIRCH_PRESSURE_PLATE, ACACIA_PRESSURE_PLATE, SPRUCE_TRAPDOOR, JUNGLE_TRAPDOOR, DARK_OAK_TRAPDOOR, BIRCH_TRAPDOOR, ACACIA_TRAPDOOR, SPRUCE_BUTTON, JUNGLE_BUTTON, DARK_OAK_BUTTON, BIRCH_BUTTON, ACACIA_BUTTON, DRIED_KELP_BLOCK, KELP_BLOCK_FORM(true, true), CORAL_FAN_HANG3, CORAL_FAN_HANG2, CORAL_FAN_HANG, CORAL_FAN_DEAD, CORAL_FAN, CORAL_BLOCK, CORAL, SEAGRASS, ELEMENT_118(false, false, true), ELEMENT_117(false, false, true), ELEMENT_116(false, false, true), ELEMENT_115(false, false, true), ELEMENT_114(false, false, true), ELEMENT_113(false, false, true), ELEMENT_112(false, false, true), ELEMENT_111(false, false, true), ELEMENT_110(false, false, true), ELEMENT_109(false, false, true), ELEMENT_108(false, false, true), ELEMENT_107(false, false, true), ELEMENT_106(false, false, true), ELEMENT_105(false, false, true), ELEMENT_104(false, false, true), ELEMENT_103(false, false, true), ELEMENT_102(false, false, true), ELEMENT_101(false, false, true), ELEMENT_100(false, false, true), ELEMENT_99(false, false, true), ELEMENT_98(false, false, true), ELEMENT_97(false, false, true), ELEMENT_96(false, false, true), ELEMENT_95(false, false, true), ELEMENT_94(false, false, true), ELEMENT_93(false, false, true), ELEMENT_92(false, false, true), ELEMENT_91(false, false, true), ELEMENT_90(false, false, true), ELEMENT_89(false, false, true), ELEMENT_88(false, false, true), ELEMENT_87(false, false, true), ELEMENT_86(false, false, true), ELEMENT_85(false, false, true), ELEMENT_84(false, false, true), ELEMENT_83(false, false, true), ELEMENT_82(false, false, true), ELEMENT_81(false, false, true), ELEMENT_80(false, false, true), ELEMENT_79(false, false, true), ELEMENT_78(false, false, true), ELEMENT_77(false, false, true), ELEMENT_76(false, false, true), ELEMENT_75(false, false, true), ELEMENT_74(false, false, true), ELEMENT_73(false, false, true), ELEMENT_72(false, false, true), ELEMENT_71(false, false, true), ELEMENT_70(false, false, true), ELEMENT_69(false, false, true), ELEMENT_68(false, false, true), ELEMENT_67(false, false, true), ELEMENT_66(false, false, true), ELEMENT_65(false, false, true), ELEMENT_64(false, false, true), ELEMENT_63(false, false, true), ELEMENT_62(false, false, true), ELEMENT_61(false, false, true), ELEMENT_60(false, false, true), ELEMENT_59(false, false, true), ELEMENT_58(false, false, true), ELEMENT_57(false, false, true), ELEMENT_56(false, false, true), ELEMENT_55(false, false, true), ELEMENT_54(false, false, true), ELEMENT_53(false, false, true), ELEMENT_52(false, false, true), ELEMENT_51(false, false, true), ELEMENT_50(false, false, true), ELEMENT_49(false, false, true), ELEMENT_48(false, false, true), ELEMENT_47(false, false, true), ELEMENT_46(false, false, true), ELEMENT_45(false, false, true), ELEMENT_44(false, false, true), ELEMENT_43(false, false, true), ELEMENT_42(false, false, true), ELEMENT_41(false, false, true), ELEMENT_40(false, false, true), ELEMENT_39(false, false, true), ELEMENT_38(false, false, true), ELEMENT_37(false, false, true), ELEMENT_36(false, false, true), ELEMENT_35(false, false, true), ELEMENT_34(false, false, true), ELEMENT_33(false, false, true), ELEMENT_32(false, false, true), ELEMENT_31(false, false, true), ELEMENT_30(false, false, true), ELEMENT_29(false, false, true), ELEMENT_28(false, false, true), ELEMENT_27(false, false, true), ELEMENT_26(false, false, true), ELEMENT_25(false, false, true), ELEMENT_24(false, false, true), ELEMENT_23(false, false, true), ELEMENT_22(false, false, true), ELEMENT_21(false, false, true), ELEMENT_20(false, false, true), ELEMENT_19(false, false, true), ELEMENT_18(false, false, true), ELEMENT_17(false, false, true), ELEMENT_16(false, false, true), ELEMENT_15(false, false, true), ELEMENT_14(false, false, true), ELEMENT_13(false, false, true), ELEMENT_12(false, false, true), ELEMENT_11(false, false, true), ELEMENT_10(false, false, true), ELEMENT_9(false, false, true), ELEMENT_8(false, false, true), ELEMENT_7(false, false, true), ELEMENT_6(false, false, true), ELEMENT_5(false, false, true), ELEMENT_4(false, false, true), ELEMENT_3(false, false, true), ELEMENT_2(false, false, true), ELEMENT_1(false, false, true), BLUE_ICE, STRIPPED_OAK_LOG, STRIPPED_DARK_OAK_LOG, STRIPPED_ACACIA_LOG, STRIPPED_JUNGLE_LOG, STRIPPED_BIRCH_LOG, STRIPPED_SPRUCE_LOG, PRISMARINE_BRICKS_STAIRS, DARK_PRISMARINE_STAIRS, PRISMARINE_STAIRS, STONE, GRASS, DIRT, COBBLESTONE, PLANKS, SAPLING, BEDROCK, FLOWING_WATER(false, true), WATER(false, true), FLOWING_LAVA(false, true), LAVA(false, true), SAND, GRAVEL, GOLD_ORE, IRON_ORE, COAL_ORE, LOG, LEAVES, SPONGE, GLASS, LAPIS_ORE, LAPIS_BLOCK, DISPENSER, SANDSTONE, NOTEBLOCK, BED_BLOCK_FORM(true, true), GOLDEN_RAIL, DETECTOR_RAIL, STICKY_PISTON, WEB, TALLGRASS, DEADBUSH, PISTON,  //@PowerNukkitOnly @Since("1.4.0.0-PN") PISTONARMCOLLISION("minecraft:pistonArmCollision", "minecraft:piston"),
    WOOL, ELEMENT_0(false, false, true), YELLOW_FLOWER, RED_FLOWER, BROWN_MUSHROOM, RED_MUSHROOM, GOLD_BLOCK, IRON_BLOCK, REAL_DOUBLE_STONE_SLAB(true, true), DOUBLE_STONE_SLAB, BRICK_BLOCK, TNT, BOOKSHELF, MOSSY_COBBLESTONE, OBSIDIAN, TORCH, FIRE(false, true), MOB_SPAWNER, OAK_STAIRS, CHEST, REDSTONE_WIRE, DIAMOND_ORE, DIAMOND_BLOCK, CRAFTING_TABLE, WHEAT_BLOCK("minecraft:item.wheat", "minecraft:wheat"), FARMLAND, FURNACE, LIT_FURNACE(false, true), STANDING_SIGN(false, true), WOODEN_DOOR_BLOCK_FORM(true, true), LADDER, RAIL, STONE_STAIRS, WALL_SIGN(false, true), LEVER, STONE_PRESSURE_PLATE, IRON_DOOR_BLOCK_FORM(true, true), WOODEN_PRESSURE_PLATE, REDSTONE_ORE, LIT_REDSTONE_ORE(false, true), UNLIT_REDSTONE_TORCH(false, true), REDSTONE_TORCH, STONE_BUTTON, SNOW_LAYER, ICE, SNOW, CACTUS, CLAY, REEDS, JUKEBOX, FENCE, PUMPKIN, NETHERRACK, SOUL_SAND, GLOWSTONE, PORTAL(false, true), LIT_PUMPKIN, CAKE_BLOCK_FORM(true, true), UNPOWERED_REPEATER(false, true), POWERED_REPEATER, INVISIBLEBEDROCK(false, true), TRAPDOOR, MONSTER_EGG, STONEBRICK, BROWN_MUSHROOM_BLOCK, RED_MUSHROOM_BLOCK, IRON_BARS, GLASS_PANE, MELON_BLOCK, PUMPKIN_STEM(false, true), MELON_STEM(false, true), VINE, FENCE_GATE, BRICK_STAIRS, STONE_BRICK_STAIRS, MYCELIUM, WATERLILY, NETHER_BRICK, NETHER_BRICK_FENCE, NETHER_BRICK_STAIRS, NETHER_WART_BLOCK_FORM(true, true), ENCHANTING_TABLE, BREWINGSTANDBLOCK(false, true), CAULDRON_BLOCK_FORM(true, true), END_PORTAL(false, true), END_PORTAL_FRAME, END_STONE, DRAGON_EGG, REDSTONE_LAMP, LIT_REDSTONE_LAMP, DROPPER, ACTIVATOR_RAIL, COCOA(false, true), SANDSTONE_STAIRS, EMERALD_ORE, ENDER_CHEST, TRIPWIRE_HOOK, TRIPWIRE(false, true), EMERALD_BLOCK, SPRUCE_STAIRS, BIRCH_STAIRS, JUNGLE_STAIRS, COMMAND_BLOCK, BEACON, COBBLESTONE_WALL, FLOWER_POT_BLOCK_FORM(true, true), CARROTS(false, true), POTATOES(false, true), WOODEN_BUTTON, SKULL_BLOCK_FORM(true, true), ANVIL, TRAPPED_CHEST, LIGHT_WEIGHTED_PRESSURE_PLATE, HEAVY_WEIGHTED_PRESSURE_PLATE, UNPOWERED_COMPARATOR(false, true), POWERED_COMPARATOR, DAYLIGHT_DETECTOR, REDSTONE_BLOCK, QUARTZ_ORE, HOPPER_BLOCK_FORM(true, true), QUARTZ_BLOCK, QUARTZ_STAIRS, DOUBLE_WOODEN_SLAB(true, true), WOODEN_SLAB, STAINED_HARDENED_CLAY, STAINED_GLASS_PANE, LEAVES2, LOG2, ACACIA_STAIRS, DARK_OAK_STAIRS, SLIME, GLOW_STICK(false, false, true), IRON_TRAPDOOR, PRISMARINE, SEALANTERN, HAY_BLOCK, CARPET, HARDENED_CLAY, COAL_BLOCK, PACKED_ICE, DOUBLE_PLANT, STANDING_BANNER(false, true), WALL_BANNER(false, true), DAYLIGHT_DETECTOR_INVERTED(false, true), RED_SANDSTONE, RED_SANDSTONE_STAIRS, REAL_DOUBLE_STONE_SLAB2(true, true), DOUBLE_STONE_SLAB2, SPRUCE_FENCE_GATE, BIRCH_FENCE_GATE, JUNGLE_FENCE_GATE, DARK_OAK_FENCE_GATE, ACACIA_FENCE_GATE, REPEATING_COMMAND_BLOCK, CHAIN_COMMAND_BLOCK, HARD_GLASS_PANE(false, false, true), HARD_STAINED_GLASS_PANE(false, false, true), CHEMICAL_HEAT(false, false, true), SPRUCE_DOOR_BLOCK_FORM(true, true), BIRCH_DOOR_BLOCK_FORM(true, true), JUNGLE_DOOR_BLOCK_FORM(true, true), ACACIA_DOOR_BLOCK_FORM(true, true), DARK_OAK_DOOR_BLOCK_FORM(true, true), GRASS_PATH, FRAME_BLOCK_FORM(true, true), CHORUS_FLOWER, PURPUR_BLOCK, COLORED_TORCH_RG(false, false, true), PURPUR_STAIRS, COLORED_TORCH_BP(false, false, true), UNDYED_SHULKER_BOX, END_BRICKS, FROSTED_ICE, END_ROD, END_GATEWAY(false, true), ALLOW, DENY, BORDER_BLOCK, MAGMA, NETHER_WART_BLOCK, RED_NETHER_BRICK, BONE_BLOCK, STRUCTURE_VOID, SHULKER_BOX, PURPLE_GLAZED_TERRACOTTA, WHITE_GLAZED_TERRACOTTA, ORANGE_GLAZED_TERRACOTTA, MAGENTA_GLAZED_TERRACOTTA, LIGHT_BLUE_GLAZED_TERRACOTTA, YELLOW_GLAZED_TERRACOTTA, LIME_GLAZED_TERRACOTTA, PINK_GLAZED_TERRACOTTA, GRAY_GLAZED_TERRACOTTA, SILVER_GLAZED_TERRACOTTA, CYAN_GLAZED_TERRACOTTA, BLUE_GLAZED_TERRACOTTA, BROWN_GLAZED_TERRACOTTA, GREEN_GLAZED_TERRACOTTA, RED_GLAZED_TERRACOTTA, BLACK_GLAZED_TERRACOTTA, CONCRETE, CONCRETE_POWDER("minecraft:concretepowder", "minecraft:concrete_powder", arrayOf("minecraft:concrete_powder")), CHEMISTRY_TABLE(false, false, true), UNDERWATER_TORCH(false, false, true), CHORUS_PLANT, STAINED_GLASS, CAMERA_BLOCK_FORM(true, true, true), PODZOL, BEETROOT_BLOCK_FORM(true, true), STONECUTTER(false, true), GLOWINGOBSIDIAN(false, true), NETHERREACTOR, INFO_UPDATE(false, true), INFO_UPDATE2(false, true),  //@PowerNukkitOnly @Since("1.4.0.0-PN") MOVINGBLOCK("minecraft:movingBlock", "minecraft:air"),
    OBSERVER, STRUCTURE_BLOCK, HARD_GLASS(false, false, true), HARD_STAINED_GLASS(false, false, true), RESERVED6(false, true), IRON_SHOVEL, IRON_PICKAXE, IRON_AXE, FLINT_AND_STEEL, APPLE, BOW, ARROW, COAL, DIAMOND, IRON_INGOT, GOLD_INGOT, IRON_SWORD, WOODEN_SWORD, WOODEN_SHOVEL, WOODEN_PICKAXE, WOODEN_AXE, STONE_SWORD, STONE_SHOVEL, STONE_PICKAXE, STONE_AXE, DIAMOND_SWORD, DIAMOND_SHOVEL, DIAMOND_PICKAXE, DIAMOND_AXE, STICK, BOWL, MUSHROOM_STEW, GOLDEN_SWORD, GOLDEN_SHOVEL, GOLDEN_PICKAXE, GOLDEN_AXE, STRING, FEATHER, GUNPOWDER, WOODEN_HOE, STONE_HOE, IRON_HOE, DIAMOND_HOE, GOLDEN_HOE, WHEAT_SEEDS, WHEAT(false), BREAD, LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS, CHAINMAIL_HELMET, CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS, IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS, DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS, GOLDEN_HELMET, GOLDEN_CHESTPLATE, GOLDEN_LEGGINGS, GOLDEN_BOOTS, FLINT, PORKCHOP, COOKED_PORKCHOP, PAINTING, GOLDEN_APPLE, OAK_SIGN, WOODEN_DOOR(false), BUCKET, MINECART, SADDLE, IRON_DOOR(false), REDSTONE, SNOWBALL, BOAT, LEATHER, KELP(false), BRICK, CLAY_BALL, SUGAR_CANE, PAPER, BOOK, SLIME_BALL, CHEST_MINECART, EGG, COMPASS, FISHING_ROD, CLOCK, GLOWSTONE_DUST, COD, COOKED_COD, DYE, BONE, SUGAR, CAKE(false), BED(false), REPEATER, COOKIE, FILLED_MAP, SHEARS, MELON_SLICE, PUMPKIN_SEEDS, MELON_SEEDS, BEEF, COOKED_BEEF, CHICKEN, COOKED_CHICKEN, ROTTEN_FLESH, ENDER_PEARL, BLAZE_ROD, GHAST_TEAR, GOLD_NUGGET, NETHER_WART(false), POTION, GLASS_BOTTLE, SPIDER_EYE, FERMENTED_SPIDER_EYE, BLAZE_POWDER, MAGMA_CREAM, BREWING_STAND, CAULDRON(false), ENDER_EYE, GLISTERING_MELON_SLICE, SPAWN_EGG, EXPERIENCE_BOTTLE, FIRE_CHARGE, WRITABLE_BOOK, WRITTEN_BOOK, EMERALD, FRAME(false), FLOWER_POT(false), CARROT, POTATO, BAKED_POTATO, POISONOUS_POTATO, EMPTY_MAP, GOLDEN_CARROT, SKULL(false), CARROT_ON_A_STICK, NETHER_STAR, PUMPKIN_PIE, FIREWORK_ROCKET, FIREWORK_STAR, ENCHANTED_BOOK, COMPARATOR, NETHERBRICK, QUARTZ, TNT_MINECART, HOPPER_MINECART, PRISMARINE_SHARD, HOPPER(false), RABBIT, COOKED_RABBIT, RABBIT_STEW, RABBIT_FOOT, RABBIT_HIDE, LEATHER_HORSE_ARMOR, IRON_HORSE_ARMOR, GOLDEN_HORSE_ARMOR, DIAMOND_HORSE_ARMOR, LEAD, NAME_TAG("minecraft:name_tag", "minecraft:name_tag", arrayOf("minecraft:nametag")), PRISMARINE_CRYSTALS, MUTTON, COOKED_MUTTON, ARMOR_STAND, END_CRYSTAL, SPRUCE_DOOR(false), BIRCH_DOOR(false), JUNGLE_DOOR(false), ACACIA_DOOR(false), DARK_OAK_DOOR(false), CHORUS_FRUIT, POPPED_CHORUS_FRUIT, BANNER_PATTERN, DRAGON_BREATH, SPLASH_POTION, LINGERING_POTION, SPARKLER(false, false, true), COMMAND_BLOCK_MINECART, ELYTRA, SHULKER_SHELL, BANNER, MEDICINE(false, false, true), BALLOON(false, false, true), RAPID_FERTILIZER(false, false, true), TOTEM_OF_UNDYING, BLEACH(false, false, true), IRON_NUGGET, ICE_BOMB(false, false, true), TRIDENT, BEETROOT(false), BEETROOT_SEEDS, BEETROOT_SOUP, SALMON, TROPICAL_FISH, PUFFERFISH, COOKED_SALMON, DRIED_KELP, NAUTILUS_SHELL, ENCHANTED_GOLDEN_APPLE, HEART_OF_THE_SEA, SCUTE, TURTLE_HELMET, PHANTOM_MEMBRANE, CROSSBOW, SPRUCE_SIGN, BIRCH_SIGN, JUNGLE_SIGN, ACACIA_SIGN, DARK_OAK_SIGN, SWEET_BERRIES, CAMERA(false, false, true), COMPOUND(false, false, true), MUSIC_DISC_13, MUSIC_DISC_CAT, MUSIC_DISC_BLOCKS, MUSIC_DISC_CHIRP, MUSIC_DISC_FAR, MUSIC_DISC_MALL, MUSIC_DISC_MELLOHI, MUSIC_DISC_STAL, MUSIC_DISC_STRAD, MUSIC_DISC_WARD, MUSIC_DISC_11, MUSIC_DISC_WAIT, SHIELD, CAMPFIRE(false), SUSPICIOUS_STEW, HONEYCOMB, HONEY_BOTTLE, LODESTONE_COMPASS, NETHERITE_INGOT, NETHERITE_SWORD, NETHERITE_SHOVEL, NETHERITE_PICKAXE, NETHERITE_AXE, NETHERITE_HOE, NETHERITE_HELMET, NETHERITE_CHESTPLATE, NETHERITE_LEGGINGS, NETHERITE_BOOTS, NETHERITE_SCRAP, CRIMSON_SIGN, WARPED_SIGN, CRIMSON_DOOR(false), WARPED_DOOR(false), WARPED_FUNGUS_ON_A_STICK, CHAIN(false), MUSIC_DISC_PIGSTEP, NETHER_SPROUTS(false), SOUL_CAMPFIRE(false), COD_BUCKET, GHAST_SPAWN_EGG, FLOWER_BANNER_PATTERN, ZOGLIN_SPAWN_EGG, BLUE_DYE, SKULL_BANNER_PATTERN, ENDERMITE_SPAWN_EGG, POLAR_BEAR_SPAWN_EGG, WHITE_DYE, TROPICAL_FISH_BUCKET, CYAN_DYE, LIGHT_BLUE_DYE, LIME_DYE, ZOMBIE_VILLAGER_SPAWN_EGG, STRAY_SPAWN_EGG, GREEN_DYE, EVOKER_SPAWN_EGG, WITHER_SKELETON_SPAWN_EGG, SALMON_BUCKET, JUNGLE_BOAT, BLACK_DYE, MAGMA_CUBE_SPAWN_EGG, TROPICAL_FISH_SPAWN_EGG, VEX_SPAWN_EGG, FIELD_MASONED_BANNER_PATTERN, WANDERING_TRADER_SPAWN_EGG, BROWN_DYE, PANDA_SPAWN_EGG, SILVERFISH_SPAWN_EGG, OCELOT_SPAWN_EGG, LAVA_BUCKET, SKELETON_SPAWN_EGG, VILLAGER_SPAWN_EGG, ELDER_GUARDIAN_SPAWN_EGG, ACACIA_BOAT, OAK_BOAT, PHANTOM_SPAWN_EGG, HOGLIN_SPAWN_EGG, DARK_OAK_BOAT, HUSK_SPAWN_EGG, BLAZE_SPAWN_EGG, BORDURE_INDENTED_BANNER_PATTERN, MULE_SPAWN_EGG, CREEPER_BANNER_PATTERN, ZOMBIE_HORSE_SPAWN_EGG, BEE_SPAWN_EGG, COD_SPAWN_EGG, LLAMA_SPAWN_EGG, FOX_SPAWN_EGG, PIGLIN_BRUTE_SPAWN_EGG, PIG_SPAWN_EGG, COW_SPAWN_EGG, NPC_SPAWN_EGG, SQUID_SPAWN_EGG, MAGENTA_DYE, RED_DYE, WITCH_SPAWN_EGG, INK_SAC, ORANGE_DYE, PILLAGER_SPAWN_EGG, CAVE_SPIDER_SPAWN_EGG, BONE_MEAL, PUFFERFISH_BUCKET, BAT_SPAWN_EGG, SPRUCE_BOAT, SPIDER_SPAWN_EGG, PIGLIN_BANNER_PATTERN, RABBIT_SPAWN_EGG, MOJANG_BANNER_PATTERN, PIGLIN_SPAWN_EGG, TURTLE_SPAWN_EGG, MOOSHROOM_SPAWN_EGG, PUFFERFISH_SPAWN_EGG, PARROT_SPAWN_EGG, ZOMBIE_SPAWN_EGG, WOLF_SPAWN_EGG, GRAY_DYE, COCOA_BEANS, SKELETON_HORSE_SPAWN_EGG, SHEEP_SPAWN_EGG, SLIME_SPAWN_EGG, VINDICATOR_SPAWN_EGG, DROWNED_SPAWN_EGG, MILK_BUCKET, DOLPHIN_SPAWN_EGG, DONKEY_SPAWN_EGG, PURPLE_DYE, BIRCH_BOAT,  //@PowerNukkitOnly @Since("1.4.0.0-PN") DEBUG_STICK(false, true),
    ENDERMAN_SPAWN_EGG, CHICKEN_SPAWN_EGG, SHULKER_SPAWN_EGG, STRIDER_SPAWN_EGG, ZOMBIE_PIGMAN_SPAWN_EGG, YELLOW_DYE, CAT_SPAWN_EGG, GUARDIAN_SPAWN_EGG, PINK_DYE, SALMON_SPAWN_EGG, CREEPER_SPAWN_EGG, HORSE_SPAWN_EGG, LAPIS_LAZULI, RAVAGER_SPAWN_EGG, WATER_BUCKET, LIGHT_GRAY_DYE, CHARCOAL, AGENT_SPAWN_EGG(false, false, true);

    private val namespacedId: String
    private val itemFormNamespaceId: String
    private val technical: Boolean
    private val edu: Boolean
    private val aliases: Array<String>

    constructor(namespacedId: String, itemFormNamespaceId: String, aliases: Array<String>) {
        this.namespacedId = namespacedId
        this.itemFormNamespaceId = itemFormNamespaceId
        technical = false
        edu = false
        this.aliases = aliases
    }

    constructor() {
        namespacedId = "minecraft:" + name().toLowerCase()
        itemFormNamespaceId = namespacedId
        technical = false
        edu = false
        aliases = EmptyArrays.EMPTY_STRINGS
    }

    @JvmOverloads
    constructor(blockForm: Boolean, technical: Boolean = false, edu: Boolean = false) {
        this.technical = technical
        this.edu = edu
        val namespacedId: String = name().toLowerCase()
        aliases = EmptyArrays.EMPTY_STRINGS
        itemFormNamespaceId = "minecraft:$namespacedId"
        if (blockForm) {
            this.namespacedId = "minecraft:item.$namespacedId"
        } else {
            this.namespacedId = itemFormNamespaceId
        }
    }

    constructor(namespacedId: String, itemFormNamespaceId: String) : this(namespacedId, itemFormNamespaceId, false) {}
    constructor(namespacedId: String, itemFormNamespaceId: String, technical: Boolean) : this(namespacedId, itemFormNamespaceId, technical, false) {}
    constructor(namespacedId: String, itemFormNamespaceId: String, technical: Boolean, edu: Boolean) {
        this.edu = edu
        this.technical = technical
        this.namespacedId = namespacedId
        this.itemFormNamespaceId = itemFormNamespaceId
        aliases = EmptyArrays.EMPTY_STRINGS
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    operator fun get(amount: Int): Item? {
        return RuntimeItems.getRuntimeMapping()!!.getItemByNamespaceId(getItemFormNamespaceId(), amount)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    operator fun get(amount: Int, compoundTag: ByteArray?): Item? {
        val item: Item? = get(amount)
        item.setCompoundTag(compoundTag?.clone())
        return item
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getItemFormNamespaceId(): String {
        return itemFormNamespaceId
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getNamespacedId(): String {
        return namespacedId
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun isTechnical(): Boolean {
        return technical
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun isEducationEdition(): Boolean {
        return edu
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getAliases(): Array<String> {
        return if (aliases.size == 0) aliases else aliases.clone()
    }

    companion object {
        private val namespacedIdMap: Map<String, MinecraftItemID> = Arrays.stream(values())
                .flatMap { id ->
                    Stream.of(Arrays.stream(id.aliases), Stream.of(id.getNamespacedId()))
                            .flatMap(Function.identity())
                            .map { ns -> SimpleEntry(ns, id) }
                }
                .collect(Collectors.toMap({ entry -> entry.getKey().toLowerCase() }, AbstractMap.SimpleEntry::getValue))

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        @Nullable
        fun getByNamespaceId(namespacedId: String): MinecraftItemID? {
            return namespacedIdMap[namespacedId]
        }
    }
}