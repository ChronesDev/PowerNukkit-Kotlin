package cn.nukkit.block

import cn.nukkit.api.DeprecationDetails

interface BlockID {
    companion object {
        const val AIR = 0
        const val STONE = 1
        const val GRASS = 2
        const val DIRT = 3
        const val COBBLESTONE = 4
        const val COBBLE = 4
        const val PLANK = 5
        const val PLANKS = 5
        const val WOODEN_PLANK = 5
        const val WOODEN_PLANKS = 5
        const val SAPLING = 6
        const val SAPLINGS = 6
        const val BEDROCK = 7
        const val WATER = 8
        const val STILL_WATER = 9
        const val LAVA = 10
        const val STILL_LAVA = 11
        const val SAND = 12
        const val GRAVEL = 13
        const val GOLD_ORE = 14
        const val IRON_ORE = 15
        const val COAL_ORE = 16
        const val LOG = 17
        const val WOOD = 17
        const val TRUNK = 17
        const val LEAVES = 18
        const val LEAVE = 18
        const val SPONGE = 19
        const val GLASS = 20
        const val LAPIS_ORE = 21
        const val LAPIS_BLOCK = 22
        const val DISPENSER = 23
        const val SANDSTONE = 24
        const val NOTEBLOCK = 25
        const val BED_BLOCK = 26
        const val POWERED_RAIL = 27
        const val DETECTOR_RAIL = 28
        const val STICKY_PISTON = 29
        const val COBWEB = 30
        const val TALL_GRASS = 31
        const val BUSH = 32
        const val DEAD_BUSH = 32
        const val PISTON = 33
        const val PISTON_HEAD = 34
        const val WOOL = 35
        const val DANDELION = 37
        const val POPPY = 38
        const val ROSE = 38
        const val FLOWER = 38
        const val RED_FLOWER = 38
        const val BROWN_MUSHROOM = 39
        const val RED_MUSHROOM = 40
        const val GOLD_BLOCK = 41
        const val IRON_BLOCK = 42
        const val DOUBLE_SLAB = 43
        const val DOUBLE_STONE_SLAB = 43
        const val DOUBLE_SLABS = 43
        const val SLAB = 44
        const val STONE_SLAB = 44
        const val SLABS = 44
        const val BRICKS = 45
        const val BRICKS_BLOCK = 45
        const val TNT = 46
        const val BOOKSHELF = 47
        const val MOSS_STONE = 48
        const val MOSSY_STONE = 48
        const val OBSIDIAN = 49
        const val TORCH = 50
        const val FIRE = 51
        const val MONSTER_SPAWNER = 52

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val MOB_SPAWNER = MONSTER_SPAWNER
        const val WOOD_STAIRS = 53
        const val WOODEN_STAIRS = 53
        const val OAK_WOOD_STAIRS = 53
        const val OAK_WOODEN_STAIRS = 53
        const val CHEST = 54
        const val REDSTONE_WIRE = 55
        const val DIAMOND_ORE = 56
        const val DIAMOND_BLOCK = 57
        const val CRAFTING_TABLE = 58
        const val WORKBENCH = 58
        const val WHEAT_BLOCK = 59
        const val FARMLAND = 60
        const val FURNACE = 61
        const val BURNING_FURNACE = 62
        const val LIT_FURNACE = 62
        const val SIGN_POST = 63
        const val DOOR_BLOCK = 64
        const val WOODEN_DOOR_BLOCK = 64
        const val WOOD_DOOR_BLOCK = 64
        const val LADDER = 65
        const val RAIL = 66
        const val COBBLE_STAIRS = 67
        const val COBBLESTONE_STAIRS = 67
        const val WALL_SIGN = 68
        const val LEVER = 69
        const val STONE_PRESSURE_PLATE = 70
        const val IRON_DOOR_BLOCK = 71
        const val WOODEN_PRESSURE_PLATE = 72
        const val REDSTONE_ORE = 73
        const val GLOWING_REDSTONE_ORE = 74
        const val LIT_REDSTONE_ORE = 74
        const val UNLIT_REDSTONE_TORCH = 75
        const val REDSTONE_TORCH = 76
        const val STONE_BUTTON = 77
        const val SNOW = 78
        const val SNOW_LAYER = 78
        const val ICE = 79
        const val SNOW_BLOCK = 80
        const val CACTUS = 81
        const val CLAY_BLOCK = 82
        const val REEDS = 83
        const val SUGARCANE_BLOCK = 83
        const val JUKEBOX = 84
        const val FENCE = 85
        const val PUMPKIN = 86
        const val NETHERRACK = 87
        const val SOUL_SAND = 88
        const val GLOWSTONE = 89
        const val GLOWSTONE_BLOCK = 89
        const val NETHER_PORTAL = 90
        const val LIT_PUMPKIN = 91
        const val JACK_O_LANTERN = 91
        const val CAKE_BLOCK = 92
        const val UNPOWERED_REPEATER = 93
        const val POWERED_REPEATER = 94
        const val INVISIBLE_BEDROCK = 95
        const val TRAPDOOR = 96
        const val MONSTER_EGG = 97
        const val STONE_BRICKS = 98
        const val STONE_BRICK = 98
        const val BROWN_MUSHROOM_BLOCK = 99
        const val RED_MUSHROOM_BLOCK = 100
        const val IRON_BAR = 101
        const val IRON_BARS = 101
        const val GLASS_PANE = 102
        const val GLASS_PANEL = 102
        const val MELON_BLOCK = 103
        const val PUMPKIN_STEM = 104
        const val MELON_STEM = 105
        const val VINE = 106
        const val VINES = 106
        const val FENCE_GATE = 107
        const val FENCE_GATE_OAK = 107
        const val BRICK_STAIRS = 108
        const val STONE_BRICK_STAIRS = 109
        const val MYCELIUM = 110
        const val WATER_LILY = 111
        const val LILY_PAD = 111
        const val NETHER_BRICKS = 112
        const val NETHER_BRICK_BLOCK = 112
        const val NETHER_BRICK_FENCE = 113
        const val NETHER_BRICKS_STAIRS = 114
        const val NETHER_WART_BLOCK = 115
        const val ENCHANTING_TABLE = 116
        const val ENCHANT_TABLE = 116
        const val ENCHANTMENT_TABLE = 116
        const val BREWING_STAND_BLOCK = 117
        const val BREWING_BLOCK = 117
        const val CAULDRON_BLOCK = 118
        const val END_PORTAL = 119
        const val END_PORTAL_FRAME = 120
        const val END_STONE = 121
        const val DRAGON_EGG = 122
        const val REDSTONE_LAMP = 123
        const val LIT_REDSTONE_LAMP = 124

        //Note: dropper CAN NOT BE HARVESTED WITH HAND -- canHarvestWithHand method should be overridden FALSE.
        const val DROPPER = 125
        const val ACTIVATOR_RAIL = 126
        const val COCOA = 127
        const val COCOA_BLOCK = 127
        const val SANDSTONE_STAIRS = 128
        const val EMERALD_ORE = 129
        const val ENDER_CHEST = 130
        const val TRIPWIRE_HOOK = 131
        const val TRIPWIRE = 132
        const val EMERALD_BLOCK = 133
        const val SPRUCE_WOOD_STAIRS = 134
        const val SPRUCE_WOODEN_STAIRS = 134
        const val BIRCH_WOOD_STAIRS = 135
        const val BIRCH_WOODEN_STAIRS = 135
        const val JUNGLE_WOOD_STAIRS = 136
        const val JUNGLE_WOODEN_STAIRS = 136
        const val BEACON = 138
        const val COBBLE_WALL = 139
        const val STONE_WALL = 139
        const val COBBLESTONE_WALL = 139
        const val FLOWER_POT_BLOCK = 140
        const val CARROT_BLOCK = 141
        const val POTATO_BLOCK = 142
        const val WOODEN_BUTTON = 143
        const val SKULL_BLOCK = 144
        const val ANVIL = 145
        const val TRAPPED_CHEST = 146
        const val LIGHT_WEIGHTED_PRESSURE_PLATE = 147
        const val HEAVY_WEIGHTED_PRESSURE_PLATE = 148
        const val UNPOWERED_COMPARATOR = 149
        const val POWERED_COMPARATOR = 150
        const val DAYLIGHT_DETECTOR = 151
        const val REDSTONE_BLOCK = 152
        const val QUARTZ_ORE = 153
        const val HOPPER_BLOCK = 154
        const val QUARTZ_BLOCK = 155
        const val QUARTZ_STAIRS = 156
        const val DOUBLE_WOOD_SLAB = 157
        const val DOUBLE_WOODEN_SLAB = 157
        const val DOUBLE_WOOD_SLABS = 157
        const val DOUBLE_WOODEN_SLABS = 157
        const val WOOD_SLAB = 158
        const val WOODEN_SLAB = 158
        const val WOOD_SLABS = 158
        const val WOODEN_SLABS = 158
        const val STAINED_TERRACOTTA = 159
        const val STAINED_HARDENED_CLAY = STAINED_TERRACOTTA
        const val STAINED_GLASS_PANE = 160
        const val LEAVES2 = 161
        const val LEAVE2 = 161
        const val WOOD2 = 162
        const val TRUNK2 = 162
        const val LOG2 = 162
        const val ACACIA_WOOD_STAIRS = 163
        const val ACACIA_WOODEN_STAIRS = 163
        const val DARK_OAK_WOOD_STAIRS = 164
        const val DARK_OAK_WOODEN_STAIRS = 164
        const val SLIME_BLOCK = 165
        const val IRON_TRAPDOOR = 167
        const val PRISMARINE = 168
        const val SEA_LANTERN = 169
        const val HAY_BALE = 170
        const val CARPET = 171
        const val TERRACOTTA = 172

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val HARDENED_CLAY = TERRACOTTA
        const val COAL_BLOCK = 173
        const val PACKED_ICE = 174
        const val DOUBLE_PLANT = 175
        const val STANDING_BANNER = 176
        const val WALL_BANNER = 177
        const val DAYLIGHT_DETECTOR_INVERTED = 178
        const val RED_SANDSTONE = 179
        const val RED_SANDSTONE_STAIRS = 180
        const val DOUBLE_RED_SANDSTONE_SLAB = 181
        const val RED_SANDSTONE_SLAB = 182
        const val FENCE_GATE_SPRUCE = 183
        const val FENCE_GATE_BIRCH = 184
        const val FENCE_GATE_JUNGLE = 185
        const val FENCE_GATE_DARK_OAK = 186
        const val FENCE_GATE_ACACIA = 187
        const val SPRUCE_DOOR_BLOCK = 193
        const val BIRCH_DOOR_BLOCK = 194
        const val JUNGLE_DOOR_BLOCK = 195
        const val ACACIA_DOOR_BLOCK = 196
        const val DARK_OAK_DOOR_BLOCK = 197
        const val GRASS_PATH = 198
        const val ITEM_FRAME_BLOCK = 199
        const val CHORUS_FLOWER = 200
        const val PURPUR_BLOCK = 201

        //int COLORED_TORCH_RG = 202;
        const val PURPUR_STAIRS = 203

        //int COLORED_TORCH_BP = 204;
        const val UNDYED_SHULKER_BOX = 205
        const val END_BRICKS = 206

        //Note: frosted ice CAN NOT BE HARVESTED WITH HAND -- canHarvestWithHand method should be overridden FALSE.
        const val ICE_FROSTED = 207
        const val END_ROD = 208
        const val END_GATEWAY = 209

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val ALLOW = 210

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val DENY = 211

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val BORDER_BLOCK = 212
        const val MAGMA = 213
        const val BLOCK_NETHER_WART_BLOCK = 214
        const val RED_NETHER_BRICK = 215
        const val BONE_BLOCK = 216

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val STRUCTURE_VOID = 217
        const val SHULKER_BOX = 218
        const val PURPLE_GLAZED_TERRACOTTA = 219
        const val WHITE_GLAZED_TERRACOTTA = 220
        const val ORANGE_GLAZED_TERRACOTTA = 221
        const val MAGENTA_GLAZED_TERRACOTTA = 222
        const val LIGHT_BLUE_GLAZED_TERRACOTTA = 223
        const val YELLOW_GLAZED_TERRACOTTA = 224
        const val LIME_GLAZED_TERRACOTTA = 225
        const val PINK_GLAZED_TERRACOTTA = 226
        const val GRAY_GLAZED_TERRACOTTA = 227
        const val SILVER_GLAZED_TERRACOTTA = 228
        const val CYAN_GLAZED_TERRACOTTA = 229
        const val BLUE_GLAZED_TERRACOTTA = 231
        const val BROWN_GLAZED_TERRACOTTA = 232
        const val GREEN_GLAZED_TERRACOTTA = 233
        const val RED_GLAZED_TERRACOTTA = 234
        const val BLACK_GLAZED_TERRACOTTA = 235
        const val CONCRETE = 236

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CONCRETEPOWDER = 237
        val CONCRETE_POWDER = CONCRETEPOWDER
        const val CHORUS_PLANT = 240
        const val STAINED_GLASS = 241
        const val PODZOL = 243
        const val BEETROOT_BLOCK = 244
        const val STONECUTTER = 245
        const val GLOWING_OBSIDIAN = 246

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val NETHERREACTOR = 247
        val NETHER_REACTOR = NETHERREACTOR

        @Since("1.5.0.0-PN")
        @Deprecated
        @DeprecationDetails(by = "PowerNukkit", reason = "This was added by Cloudburst Nukkit, but it is a tecnical block, avoid usinig it.", since = "1.5.0.0-PN")
        val INFO_UPDATE = 248

        @Since("1.5.0.0-PN")
        @Deprecated
        @DeprecationDetails(by = "PowerNukkit", reason = "This was added by Cloudburst Nukkit, but it is a tecnical block, avoid usinig it.", since = "1.5.0.0-PN")
        val INFO_UPDATE2 = 249
        const val PISTON_EXTENSION = 250

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val MOVING_BLOCK = PISTON_EXTENSION
        const val OBSERVER = 251

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val STRUCTURE_BLOCK = 252

        @PowerNukkitOnly
        val PRISMARINE_STAIRS = 257

        @PowerNukkitOnly
        val DARK_PRISMARINE_STAIRS = 258

        @PowerNukkitOnly
        val PRISMARINE_BRICKS_STAIRS = 259

        @PowerNukkitOnly
        val STRIPPED_SPRUCE_LOG = 260

        @PowerNukkitOnly
        val STRIPPED_BIRCH_LOG = 261

        @PowerNukkitOnly
        val STRIPPED_JUNGLE_LOG = 262

        @PowerNukkitOnly
        val STRIPPED_ACACIA_LOG = 263

        @PowerNukkitOnly
        val STRIPPED_DARK_OAK_LOG = 264

        @PowerNukkitOnly
        val STRIPPED_OAK_LOG = 265

        @PowerNukkitOnly
        val BLUE_ICE = 266

        @PowerNukkitOnly
        val SEAGRASS = 385

        @PowerNukkitOnly
        val CORAL = 386

        @PowerNukkitOnly
        val CORAL_BLOCK = 387

        @PowerNukkitOnly
        val CORAL_FAN = 388

        @PowerNukkitOnly
        val CORAL_FAN_DEAD = 389

        @PowerNukkitOnly
        val CORAL_FAN_HANG = 390

        @PowerNukkitOnly
        val CORAL_FAN_HANG2 = 391

        @PowerNukkitOnly
        val CORAL_FAN_HANG3 = 392

        @PowerNukkitOnly
        val BLOCK_KELP = 393

        @PowerNukkitOnly
        val DRIED_KELP_BLOCK = 394

        @PowerNukkitOnly
        val ACACIA_BUTTON = 395

        @PowerNukkitOnly
        val BIRCH_BUTTON = 396

        @PowerNukkitOnly
        val DARK_OAK_BUTTON = 397

        @PowerNukkitOnly
        val JUNGLE_BUTTON = 398

        @PowerNukkitOnly
        val SPRUCE_BUTTON = 399

        @PowerNukkitOnly
        val ACACIA_TRAPDOOR = 400

        @PowerNukkitOnly
        val BIRCH_TRAPDOOR = 401

        @PowerNukkitOnly
        val DARK_OAK_TRAPDOOR = 402

        @PowerNukkitOnly
        val JUNGLE_TRAPDOOR = 403

        @PowerNukkitOnly
        val SPRUCE_TRAPDOOR = 404

        @PowerNukkitOnly
        val ACACIA_PRESSURE_PLATE = 405

        @PowerNukkitOnly
        val BIRCH_PRESSURE_PLATE = 406

        @PowerNukkitOnly
        val DARK_OAK_PRESSURE_PLATE = 407

        @PowerNukkitOnly
        val JUNGLE_PRESSURE_PLATE = 408

        @PowerNukkitOnly
        val SPRUCE_PRESSURE_PLATE = 409

        @PowerNukkitOnly
        val CARVED_PUMPKIN = 410

        @PowerNukkitOnly
        val SEA_PICKLE = 411

        @PowerNukkitOnly
        val CONDUIT = 412

        @PowerNukkitOnly
        val TURTLE_EGG = 414

        @PowerNukkitOnly
        val BUBBLE_COLUMN = 415

        @PowerNukkitOnly
        val BARRIER = 416

        @PowerNukkitOnly
        val STONE_SLAB3 = 417

        @PowerNukkitOnly
        val BAMBOO = 418

        @PowerNukkitOnly
        val BAMBOO_SAPLING = 419

        @PowerNukkitOnly
        val SCAFFOLDING = 420

        @PowerNukkitOnly
        val STONE_SLAB4 = 421

        @PowerNukkitOnly
        val DOUBLE_STONE_SLAB3 = 422

        @PowerNukkitOnly
        val DOUBLE_STONE_SLAB4 = 423

        @PowerNukkitOnly
        val GRANITE_STAIRS = 424

        @PowerNukkitOnly
        val DIORITE_STAIRS = 425

        @PowerNukkitOnly
        val ANDESITE_STAIRS = 426

        @PowerNukkitOnly
        val POLISHED_GRANITE_STAIRS = 427

        @PowerNukkitOnly
        val POLISHED_DIORITE_STAIRS = 428

        @PowerNukkitOnly
        val POLISHED_ANDESITE_STAIRS = 429

        @PowerNukkitOnly
        val MOSSY_STONE_BRICK_STAIRS = 430

        @PowerNukkitOnly
        val SMOOTH_RED_SANDSTONE_STAIRS = 431

        @PowerNukkitOnly
        val SMOOTH_SANDSTONE_STAIRS = 432

        @PowerNukkitOnly
        val END_BRICK_STAIRS = 433

        @PowerNukkitOnly
        val MOSSY_COBBLESTONE_STAIRS = 434

        @PowerNukkitOnly
        val NORMAL_STONE_STAIRS = 435

        @PowerNukkitOnly
        val SPRUCE_STANDING_SIGN = 436

        @PowerNukkitOnly
        val SPRUCE_WALL_SIGN = 437

        @PowerNukkitOnly
        val SMOOTH_STONE = 438

        @PowerNukkitOnly
        val RED_NETHER_BRICK_STAIRS = 439

        @PowerNukkitOnly
        val SMOOTH_QUARTZ_STAIRS = 440

        @PowerNukkitOnly
        val BIRCH_STANDING_SIGN = 441

        @PowerNukkitOnly
        val BIRCH_WALL_SIGN = 442

        @PowerNukkitOnly
        val JUNGLE_STANDING_SIGN = 443

        @PowerNukkitOnly
        val JUNGLE_WALL_SIGN = 444

        @PowerNukkitOnly
        val ACACIA_STANDING_SIGN = 445

        @PowerNukkitOnly
        val ACACIA_WALL_SIGN = 446

        @PowerNukkitOnly
        val DARKOAK_STANDING_SIGN = 447

        @PowerNukkitOnly
        val DARK_OAK_STANDING_SIGN = 447

        @PowerNukkitOnly
        val DARKOAK_WALL_SIGN = 448

        @PowerNukkitOnly
        val DARK_OAK_WALL_SIGN = 448

        @PowerNukkitOnly
        val LECTERN = 449

        @PowerNukkitOnly
        val GRINDSTONE = 450

        @PowerNukkitOnly
        val BLAST_FURNACE = 451

        @PowerNukkitOnly
        val STONECUTTER_BLOCK = 452

        @PowerNukkitOnly
        val SMOKER = 453

        @PowerNukkitOnly
        val LIT_SMOKER = 454

        @PowerNukkitOnly
        val CARTOGRAPHY_TABLE = 455

        @PowerNukkitOnly
        val FLETCHING_TABLE = 456

        @PowerNukkitOnly
        val SMITHING_TABLE = 457

        @PowerNukkitOnly
        val BARREL = 458

        @PowerNukkitOnly
        val LOOM = 459

        @PowerNukkitOnly
        val BELL = 461

        @PowerNukkitOnly
        val SWEET_BERRY_BUSH = 462

        @PowerNukkitOnly
        val LANTERN = 463

        @PowerNukkitOnly
        val CAMPFIRE_BLOCK = 464

        @PowerNukkitOnly
        val LAVA_CAULDRON = 465

        @PowerNukkitOnly
        val JIGSAW = 466

        @PowerNukkitOnly
        val WOOD_BARK = 467

        @PowerNukkitOnly
        val COMPOSTER = 468

        @PowerNukkitOnly
        val LIT_BLAST_FURNACE = 469

        @PowerNukkitOnly
        val LIGHT_BLOCK = 470

        @PowerNukkitOnly
        val WITHER_ROSE = 471

        @PowerNukkitOnly
        val STICKYPISTONARMCOLLISION = 472

        @PowerNukkitOnly
        val PISTON_HEAD_STICKY = 472

        @PowerNukkitOnly
        val BEE_NEST = 473

        @PowerNukkitOnly
        val BEEHIVE = 474

        @PowerNukkitOnly
        val HONEY_BLOCK = 475

        @PowerNukkitOnly
        val HONEYCOMB_BLOCK = 476

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val LODESTONE = 477

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRIMSON_ROOTS = 478

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WARPED_ROOTS = 479

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRIMSON_STEM = 480

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WARPED_STEM = 481

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WARPED_WART_BLOCK = 482

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRIMSON_FUNGUS = 483

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WARPED_FUNGUS = 484

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val SHROOMLIGHT = 485

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WEEPING_VINES = 486

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRIMSON_NYLIUM = 487

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WARPED_NYLIUM = 488

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val BASALT = 489

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val POLISHED_BASALT = 490

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val SOUL_SOIL = 491

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val SOUL_FIRE = 492

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val NETHER_SPROUTS_BLOCK = 493

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val TARGET = 494

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val STRIPPED_CRIMSON_STEM = 495

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val STRIPPED_WARPED_STEM = 496

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRIMSON_PLANKS = 497

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WARPED_PLANKS = 498

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRIMSON_DOOR_BLOCK = 499

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WARPED_DOOR_BLOCK = 500

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRIMSON_TRAPDOOR = 501

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WARPED_TRAPDOOR = 502

        // 503
        // 504
        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRIMSON_STANDING_SIGN = 505

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WARPED_STANDING_SIGN = 506

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRIMSON_WALL_SIGN = 507

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WARPED_WALL_SIGN = 508

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRIMSON_STAIRS = 509

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WARPED_STAIRS = 510

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRIMSON_FENCE = 511

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WARPED_FENCE = 512

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRIMSON_FENCE_GATE = 513

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WARPED_FENCE_GATE = 514

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRIMSON_BUTTON = 515

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WARPED_BUTTON = 516

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRIMSON_PRESSURE_PLATE = 517

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WARPED_PRESSURE_PLATE = 518

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRIMSON_SLAB = 519

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WARPED_SLAB = 520

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRIMSON_DOUBLE_SLAB = 521

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WARPED_DOUBLE_SLAB = 522

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val SOUL_TORCH = 523

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val SOUL_LANTERN = 524

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val NETHERITE_BLOCK = 525

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val ANCIENT_DERBRIS = 526

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val RESPAWN_ANCHOR = 527

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val BLACKSTONE = 528

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val POLISHED_BLACKSTONE_BRICKS = 529

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val POLISHED_BLACKSTONE_BRICK_STAIRS = 530

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val BLACKSTONE_STAIRS = 531

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val BLACKSTONE_WALL = 532

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val POLISHED_BLACKSTONE_BRICK_WALL = 533

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CHISELED_POLISHED_BLACKSTONE = 534

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRACKED_POLISHED_BLACKSTONE_BRICKS = 535

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val GILDED_BLACKSTONE = 536

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val BLACKSTONE_SLAB = 537

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val BLACKSTONE_DOUBLE_SLAB = 538

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val POLISHED_BLACKSTONE_BRICK_SLAB = 539

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB = 540

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CHAIN_BLOCK = 541

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val TWISTING_VINES = 542

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val NETHER_GOLD_ORE = 543

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRYING_OBSIDIAN = 544

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val SOUL_CAMPFIRE_BLOCK = 545

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val POLISHED_BLACKSTONE = 546

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val POLISHED_BLACKSTONE_STAIRS = 547

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val POLISHED_BLACKSTONE_SLAB = 548

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val POLISHED_BLACKSTONE_DOUBLE_SLAB = 549

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val POLISHED_BLACKSTONE_PRESSURE_PLATE = 550

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val POLISHED_BLACKSTONE_BUTTON = 551

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val POLISHED_BLACKSTONE_WALL = 552

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WARPED_HYPHAE = 553

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRIMSON_HYPHAE = 554

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val STRIPPED_CRIMSON_HYPHAE = 555

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val STRIPPED_WARPED_HYPHAE = 556

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CHISELED_NETHER_BRICKS = 557

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRACKED_NETHER_BRICKS = 558

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val QUARTZ_BRICKS = 559 //int UNKNOWN = 600;
    }
}