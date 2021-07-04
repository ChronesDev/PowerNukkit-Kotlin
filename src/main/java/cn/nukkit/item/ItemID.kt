package cn.nukkit.item

import cn.nukkit.api.PowerNukkitOnly

interface ItemID {
    companion object {
        const val IRON_SHOVEL = 256
        const val IRON_PICKAXE = 257
        const val IRON_AXE = 258
        const val FLINT_STEEL = 259
        const val FLINT_AND_STEEL = 259
        const val APPLE = 260
        const val BOW = 261
        const val ARROW = 262
        const val COAL = 263
        const val DIAMOND = 264
        const val IRON_INGOT = 265
        const val GOLD_INGOT = 266
        const val IRON_SWORD = 267
        const val WOODEN_SWORD = 268
        const val WOODEN_SHOVEL = 269
        const val WOODEN_PICKAXE = 270
        const val WOODEN_AXE = 271
        const val STONE_SWORD = 272
        const val STONE_SHOVEL = 273
        const val STONE_PICKAXE = 274
        const val STONE_AXE = 275
        const val DIAMOND_SWORD = 276
        const val DIAMOND_SHOVEL = 277
        const val DIAMOND_PICKAXE = 278
        const val DIAMOND_AXE = 279
        const val STICK = 280
        const val STICKS = 280
        const val BOWL = 281
        const val MUSHROOM_STEW = 282
        const val GOLD_SWORD = 283
        const val GOLDEN_SWORD = 283
        const val GOLD_SHOVEL = 284
        const val GOLDEN_SHOVEL = 284
        const val GOLD_PICKAXE = 285
        const val GOLDEN_PICKAXE = 285
        const val GOLD_AXE = 286
        const val GOLDEN_AXE = 286
        const val STRING = 287
        const val FEATHER = 288
        const val GUNPOWDER = 289
        const val WOODEN_HOE = 290
        const val STONE_HOE = 291
        const val IRON_HOE = 292
        const val DIAMOND_HOE = 293
        const val GOLD_HOE = 294
        const val GOLDEN_HOE = 294
        const val SEEDS = 295
        const val WHEAT_SEEDS = 295
        const val WHEAT = 296
        const val BREAD = 297
        const val LEATHER_CAP = 298
        const val LEATHER_TUNIC = 299
        const val LEATHER_PANTS = 300
        const val LEATHER_BOOTS = 301
        const val CHAIN_HELMET = 302
        const val CHAIN_CHESTPLATE = 303
        const val CHAIN_LEGGINGS = 304
        const val CHAIN_BOOTS = 305
        const val IRON_HELMET = 306
        const val IRON_CHESTPLATE = 307
        const val IRON_LEGGINGS = 308
        const val IRON_BOOTS = 309
        const val DIAMOND_HELMET = 310
        const val DIAMOND_CHESTPLATE = 311
        const val DIAMOND_LEGGINGS = 312
        const val DIAMOND_BOOTS = 313
        const val GOLD_HELMET = 314
        const val GOLD_CHESTPLATE = 315
        const val GOLD_LEGGINGS = 316
        const val GOLD_BOOTS = 317
        const val FLINT = 318
        const val RAW_PORKCHOP = 319
        const val COOKED_PORKCHOP = 320
        const val PAINTING = 321
        const val GOLDEN_APPLE = 322
        const val SIGN = 323
        const val WOODEN_DOOR = 324
        const val BUCKET = 325
        const val MINECART = 328
        const val SADDLE = 329
        const val IRON_DOOR = 330
        const val REDSTONE = 331
        const val REDSTONE_DUST = 331
        const val SNOWBALL = 332
        const val BOAT = 333
        const val LEATHER = 334
        const val KELP = 335
        const val BRICK = 336
        const val CLAY = 337
        const val SUGARCANE = 338
        const val SUGAR_CANE = 338
        const val SUGAR_CANES = 338
        const val PAPER = 339
        const val BOOK = 340
        const val SLIMEBALL = 341
        const val MINECART_WITH_CHEST = 342
        const val EGG = 344
        const val COMPASS = 345
        const val FISHING_ROD = 346
        const val CLOCK = 347
        const val GLOWSTONE_DUST = 348
        const val RAW_FISH = 349
        const val COOKED_FISH = 350
        const val DYE = 351
        const val BONE = 352
        const val SUGAR = 353
        const val CAKE = 354
        const val BED = 355
        const val REPEATER = 356
        const val COOKIE = 357
        const val MAP = 358
        const val SHEARS = 359
        const val MELON = 360
        const val MELON_SLICE = 360
        const val PUMPKIN_SEEDS = 361
        const val MELON_SEEDS = 362
        const val RAW_BEEF = 363
        const val STEAK = 364
        const val COOKED_BEEF = 364
        const val RAW_CHICKEN = 365
        const val COOKED_CHICKEN = 366
        const val ROTTEN_FLESH = 367
        const val ENDER_PEARL = 368
        const val BLAZE_ROD = 369
        const val GHAST_TEAR = 370
        const val GOLD_NUGGET = 371
        const val GOLDEN_NUGGET = 371
        const val NETHER_WART = 372
        const val POTION = 373
        const val GLASS_BOTTLE = 374
        const val BOTTLE = 374
        const val SPIDER_EYE = 375
        const val FERMENTED_SPIDER_EYE = 376
        const val BLAZE_POWDER = 377
        const val MAGMA_CREAM = 378
        const val BREWING_STAND = 379
        const val BREWING = 379
        const val CAULDRON = 380
        const val ENDER_EYE = 381
        const val GLISTERING_MELON = 382
        const val SPAWN_EGG = 383
        const val EXPERIENCE_BOTTLE = 384
        const val FIRE_CHARGE = 385
        const val BOOK_AND_QUILL = 386
        const val WRITTEN_BOOK = 387
        const val EMERALD = 388
        const val ITEM_FRAME = 389
        const val FLOWER_POT = 390
        const val CARROT = 391
        const val CARROTS = 391
        const val POTATO = 392
        const val POTATOES = 392
        const val BAKED_POTATO = 393
        const val BAKED_POTATOES = 393
        const val POISONOUS_POTATO = 394
        const val EMPTY_MAP = 395
        const val GOLDEN_CARROT = 396
        const val SKULL = 397
        const val CARROT_ON_A_STICK = 398
        const val NETHER_STAR = 399
        const val PUMPKIN_PIE = 400
        const val FIREWORKS = 401
        const val FIREWORKSCHARGE = 402
        const val ENCHANTED_BOOK = 403
        const val ENCHANT_BOOK = 403
        const val COMPARATOR = 404
        const val NETHER_BRICK = 405
        const val QUARTZ = 406
        const val NETHER_QUARTZ = 406
        const val MINECART_WITH_TNT = 407
        const val MINECART_WITH_HOPPER = 408
        const val PRISMARINE_SHARD = 409
        const val HOPPER = 410
        const val RAW_RABBIT = 411
        const val COOKED_RABBIT = 412
        const val RABBIT_STEW = 413
        const val RABBIT_FOOT = 414
        const val RABBIT_HIDE = 415
        const val LEATHER_HORSE_ARMOR = 416
        const val IRON_HORSE_ARMOR = 417
        const val GOLD_HORSE_ARMOR = 418
        const val DIAMOND_HORSE_ARMOR = 419
        const val LEAD = 420
        const val NAME_TAG = 421
        const val PRISMARINE_CRYSTALS = 422
        const val RAW_MUTTON = 423
        const val COOKED_MUTTON = 424
        const val ARMOR_STAND = 425
        const val END_CRYSTAL = 426
        const val SPRUCE_DOOR = 427
        const val BIRCH_DOOR = 428
        const val JUNGLE_DOOR = 429
        const val ACACIA_DOOR = 430
        const val DARK_OAK_DOOR = 431
        const val CHORUS_FRUIT = 432
        const val POPPED_CHORUS_FRUIT = 433

        @Since("1.2.1.0-PN")
        @PowerNukkitOnly
        val BANNER_PATTERN = 434
        const val DRAGON_BREATH = 437
        const val SPLASH_POTION = 438
        const val LINGERING_POTION = 441
        const val COMMAND_BLOCK_MINECART = 443
        const val ELYTRA = 444
        const val SHULKER_SHELL = 445
        const val BANNER = 446
        const val TOTEM = 450
        const val IRON_NUGGET = 452
        const val TRIDENT = 455
        const val BEETROOT = 457
        const val BEETROOT_SEEDS = 458
        const val BEETROOT_SEED = 458
        const val BEETROOT_SOUP = 459
        const val RAW_SALMON = 460
        const val CLOWNFISH = 461
        const val PUFFERFISH = 462
        const val COOKED_SALMON = 463
        const val DRIED_KELP = 464
        const val NAUTILUS_SHELL = 465
        const val GOLDEN_APPLE_ENCHANTED = 466
        const val HEART_OF_THE_SEA = 467
        const val SCUTE = 468
        const val TURTLE_SHELL = 469
        const val PHANTOM_MEMBRANE = 470
        const val CROSSBOW = 471

        @PowerNukkitOnly
        val SPRUCE_SIGN = 472

        @PowerNukkitOnly
        val BIRCH_SIGN = 473

        @PowerNukkitOnly
        val JUNGLE_SIGN = 474

        @PowerNukkitOnly
        val ACACIA_SIGN = 475

        @PowerNukkitOnly
        val DARKOAK_SIGN = 476

        @PowerNukkitOnly
        val DARK_OAK_SIGN = 476
        const val SWEET_BERRIES = 477
        const val RECORD_13 = 500
        const val RECORD_CAT = 501
        const val RECORD_BLOCKS = 502
        const val RECORD_CHIRP = 503
        const val RECORD_FAR = 504
        const val RECORD_MALL = 505
        const val RECORD_MELLOHI = 506
        const val RECORD_STAL = 507
        const val RECORD_STRAD = 508
        const val RECORD_WARD = 509
        const val RECORD_11 = 510
        const val RECORD_WAIT = 511
        const val SHIELD = 513

        @PowerNukkitOnly
        val CAMPFIRE = 720

        @PowerNukkitOnly
        val SUSPICIOUS_STEW = 734
        const val HONEYCOMB = 736
        const val HONEY_BOTTLE = 737

        @Since("1.4.0.0-PN")
        val LODESTONECOMPASS = 741

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val LODESTONE_COMPASS = LODESTONECOMPASS

        @Since("1.4.0.0-PN")
        val NETHERITE_INGOT = 742

        @Since("1.4.0.0-PN")
        val NETHERITE_SWORD = 743

        @Since("1.4.0.0-PN")
        val NETHERITE_SHOVEL = 744

        @Since("1.4.0.0-PN")
        val NETHERITE_PICKAXE = 745

        @Since("1.4.0.0-PN")
        val NETHERITE_AXE = 746

        @Since("1.4.0.0-PN")
        val NETHERITE_HOE = 747

        @Since("1.4.0.0-PN")
        val NETHERITE_HELMET = 748

        @Since("1.4.0.0-PN")
        val NETHERITE_CHESTPLATE = 749

        @Since("1.4.0.0-PN")
        val NETHERITE_LEGGINGS = 750

        @Since("1.4.0.0-PN")
        val NETHERITE_BOOTS = 751

        @Since("1.4.0.0-PN")
        val NETHERITE_SCRAP = 752

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRIMSON_SIGN = 753

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WARPED_SIGN = 754

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CRIMSON_DOOR = 755

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val WARPED_DOOR = 756

        @Since("1.4.0.0-PN")
        val WARPED_FUNGUS_ON_A_STICK = 757

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val CHAIN = 758

        @Since("1.4.0.0-PN")
        val RECORD_PIGSTEP = 759

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val NETHER_SPROUTS = 760

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val SOUL_CAMPFIRE = 801 //@PowerNukkitOnly int DEBUG_STICK = <Possible:> 802+;
    }
}