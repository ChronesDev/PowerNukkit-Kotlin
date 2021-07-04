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

import cn.nukkit.api.API

/**
 * @author joserobjr
 * @since 2020-12-21
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@API(definition = API.Definition.INTERNAL, usage = API.Usage.DEPRECATED)
@Deprecated
@DeprecationDetails(since = "1.4.0.0-PN", reason = "" +
        "This interface was created to map item ids which were used in v1.4.0.0-PN-ALPHA.1, v1.4.0.0-PN-ALPHA.2 and v1.4.0.0-PN-ALPHA.1 " +
        "and will no longer be used because Nukkit took an other way and we will follow it to keep plugin compatibility in future.")
@RequiredArgsConstructor
@Getter
enum class PNAlphaItemID {
    COD_BUCKET(802, MinecraftItemID.COD_BUCKET), GHAST_SPAWN_EGG(803, MinecraftItemID.GHAST_SPAWN_EGG), FLOWER_BANNER_PATTERN(804, MinecraftItemID.FLOWER_BANNER_PATTERN), ZOGLIN_SPAWN_EGG(805, MinecraftItemID.ZOGLIN_SPAWN_EGG), BLUE_DYE(806, MinecraftItemID.BLUE_DYE), SKULL_BANNER_PATTERN(807, MinecraftItemID.SKULL_BANNER_PATTERN), ENDERMITE_SPAWN_EGG(808, MinecraftItemID.ENDERMITE_SPAWN_EGG), POLAR_BEAR_SPAWN_EGG(809, MinecraftItemID.POLAR_BEAR_SPAWN_EGG), WHITE_DYE(810, MinecraftItemID.WHITE_DYE), TROPICAL_FISH_BUCKET(811, MinecraftItemID.TROPICAL_FISH_BUCKET), CYAN_DYE(812, MinecraftItemID.CYAN_DYE), LIGHT_BLUE_DYE(813, MinecraftItemID.LIGHT_BLUE_DYE), LIME_DYE(814, MinecraftItemID.LIME_DYE), ZOMBIE_VILLAGER_SPAWN_EGG(815, MinecraftItemID.ZOMBIE_VILLAGER_SPAWN_EGG), STRAY_SPAWN_EGG(816, MinecraftItemID.STRAY_SPAWN_EGG), GREEN_DYE(817, MinecraftItemID.GREEN_DYE), EVOKER_SPAWN_EGG(818, MinecraftItemID.EVOKER_SPAWN_EGG), WITHER_SKELETON_SPAWN_EGG(819, MinecraftItemID.WITHER_SKELETON_SPAWN_EGG), SALMON_BUCKET(820, MinecraftItemID.SALMON_BUCKET), JUNGLE_BOAT(821, MinecraftItemID.JUNGLE_BOAT), BLACK_DYE(822, MinecraftItemID.BLACK_DYE), MAGMA_CUBE_SPAWN_EGG(823, MinecraftItemID.MAGMA_CUBE_SPAWN_EGG), TROPICAL_FISH_SPAWN_EGG(824, MinecraftItemID.TROPICAL_FISH_SPAWN_EGG), VEX_SPAWN_EGG(825, MinecraftItemID.VEX_SPAWN_EGG), FIELD_MASONED_BANNER_PATTERN(826, MinecraftItemID.FIELD_MASONED_BANNER_PATTERN), WANDERING_TRADER_SPAWN_EGG(827, MinecraftItemID.WANDERING_TRADER_SPAWN_EGG), BROWN_DYE(828, MinecraftItemID.BROWN_DYE), PANDA_SPAWN_EGG(829, MinecraftItemID.PANDA_SPAWN_EGG), SILVERFISH_SPAWN_EGG(830, MinecraftItemID.SILVERFISH_SPAWN_EGG), OCELOT_SPAWN_EGG(831, MinecraftItemID.OCELOT_SPAWN_EGG), LAVA_BUCKET(832, MinecraftItemID.LAVA_BUCKET), SKELETON_SPAWN_EGG(833, MinecraftItemID.SKELETON_SPAWN_EGG), VILLAGER_SPAWN_EGG(834, MinecraftItemID.VILLAGER_SPAWN_EGG), ELDER_GUARDIAN_SPAWN_EGG(835, MinecraftItemID.ELDER_GUARDIAN_SPAWN_EGG), ACACIA_BOAT(836, MinecraftItemID.ACACIA_BOAT), OAK_BOAT(837, MinecraftItemID.OAK_BOAT), PHANTOM_SPAWN_EGG(838, MinecraftItemID.PHANTOM_SPAWN_EGG), HOGLIN_SPAWN_EGG(839, MinecraftItemID.HOGLIN_SPAWN_EGG), DARK_OAK_BOAT(840, MinecraftItemID.DARK_OAK_BOAT), HUSK_SPAWN_EGG(841, MinecraftItemID.HUSK_SPAWN_EGG), BLAZE_SPAWN_EGG(842, MinecraftItemID.BLAZE_SPAWN_EGG), BORDURE_INDENTED_BANNER_PATTERN(843, MinecraftItemID.BORDURE_INDENTED_BANNER_PATTERN), MULE_SPAWN_EGG(844, MinecraftItemID.MULE_SPAWN_EGG), CREEPER_BANNER_PATTERN(845, MinecraftItemID.CREEPER_BANNER_PATTERN), ZOMBIE_HORSE_SPAWN_EGG(846, MinecraftItemID.ZOMBIE_HORSE_SPAWN_EGG), BEE_SPAWN_EGG(847, MinecraftItemID.BEE_SPAWN_EGG), COD_SPAWN_EGG(848, MinecraftItemID.COD_SPAWN_EGG), LLAMA_SPAWN_EGG(849, MinecraftItemID.LLAMA_SPAWN_EGG), FOX_SPAWN_EGG(850, MinecraftItemID.FOX_SPAWN_EGG), PIGLIN_BRUTE_SPAWN_EGG(851, MinecraftItemID.PIGLIN_BRUTE_SPAWN_EGG), PIG_SPAWN_EGG(852, MinecraftItemID.PIG_SPAWN_EGG), COW_SPAWN_EGG(853, MinecraftItemID.COW_SPAWN_EGG), NPC_SPAWN_EGG(854, MinecraftItemID.NPC_SPAWN_EGG), SQUID_SPAWN_EGG(855, MinecraftItemID.SQUID_SPAWN_EGG), MAGENTA_DYE(856, MinecraftItemID.MAGENTA_DYE), RED_DYE(857, MinecraftItemID.RED_DYE), WITCH_SPAWN_EGG(858, MinecraftItemID.WITCH_SPAWN_EGG), INK_SAC(859, MinecraftItemID.INK_SAC), ORANGE_DYE(860, MinecraftItemID.ORANGE_DYE), PILLAGER_SPAWN_EGG(861, MinecraftItemID.PILLAGER_SPAWN_EGG), CAVE_SPIDER_SPAWN_EGG(862, MinecraftItemID.CAVE_SPIDER_SPAWN_EGG), BONE_MEAL(863, MinecraftItemID.BONE_MEAL), PUFFERFISH_BUCKET(864, MinecraftItemID.PUFFERFISH_BUCKET), BAT_SPAWN_EGG(865, MinecraftItemID.BAT_SPAWN_EGG), SPRUCE_BOAT(866, MinecraftItemID.SPRUCE_BOAT), SPIDER_SPAWN_EGG(867, MinecraftItemID.SPIDER_SPAWN_EGG), PIGLIN_BANNER_PATTERN(868, MinecraftItemID.PIGLIN_BANNER_PATTERN), RABBIT_SPAWN_EGG(869, MinecraftItemID.RABBIT_SPAWN_EGG), MOJANG_BANNER_PATTERN(870, MinecraftItemID.MOJANG_BANNER_PATTERN), PIGLIN_SPAWN_EGG(871, MinecraftItemID.PIGLIN_SPAWN_EGG), TURTLE_SPAWN_EGG(872, MinecraftItemID.TURTLE_SPAWN_EGG), MOOSHROOM_SPAWN_EGG(873, MinecraftItemID.MOOSHROOM_SPAWN_EGG), PUFFERFISH_SPAWN_EGG(874, MinecraftItemID.PUFFERFISH_SPAWN_EGG), PARROT_SPAWN_EGG(875, MinecraftItemID.PARROT_SPAWN_EGG), ZOMBIE_SPAWN_EGG(876, MinecraftItemID.ZOMBIE_SPAWN_EGG), WOLF_SPAWN_EGG(877, MinecraftItemID.WOLF_SPAWN_EGG), GRAY_DYE(878, MinecraftItemID.GRAY_DYE), COCOA_BEANS(879, MinecraftItemID.COCOA_BEANS), SKELETON_HORSE_SPAWN_EGG(880, MinecraftItemID.SKELETON_HORSE_SPAWN_EGG), SHEEP_SPAWN_EGG(881, MinecraftItemID.SHEEP_SPAWN_EGG), SLIME_SPAWN_EGG(882, MinecraftItemID.SLIME_SPAWN_EGG), VINDICATOR_SPAWN_EGG(883, MinecraftItemID.VINDICATOR_SPAWN_EGG), DROWNED_SPAWN_EGG(884, MinecraftItemID.DROWNED_SPAWN_EGG), MILK_BUCKET(885, MinecraftItemID.MILK_BUCKET), DOLPHIN_SPAWN_EGG(886, MinecraftItemID.DOLPHIN_SPAWN_EGG), DONKEY_SPAWN_EGG(887, MinecraftItemID.DONKEY_SPAWN_EGG), PURPLE_DYE(888, MinecraftItemID.PURPLE_DYE), BIRCH_BOAT(889, MinecraftItemID.BIRCH_BOAT), ENDERMAN_SPAWN_EGG(891, MinecraftItemID.ENDERMAN_SPAWN_EGG), CHICKEN_SPAWN_EGG(892, MinecraftItemID.CHICKEN_SPAWN_EGG), SHULKER_SPAWN_EGG(893, MinecraftItemID.SHULKER_SPAWN_EGG), STRIDER_SPAWN_EGG(894, MinecraftItemID.STRIDER_SPAWN_EGG), ZOMBIE_PIGMAN_SPAWN_EGG(895, MinecraftItemID.ZOMBIE_PIGMAN_SPAWN_EGG), YELLOW_DYE(896, MinecraftItemID.YELLOW_DYE), CAT_SPAWN_EGG(897, MinecraftItemID.CAT_SPAWN_EGG), GUARDIAN_SPAWN_EGG(898, MinecraftItemID.GUARDIAN_SPAWN_EGG), PINK_DYE(899, MinecraftItemID.PINK_DYE), SALMON_SPAWN_EGG(900, MinecraftItemID.SALMON_SPAWN_EGG), CREEPER_SPAWN_EGG(901, MinecraftItemID.CREEPER_SPAWN_EGG), HORSE_SPAWN_EGG(902, MinecraftItemID.HORSE_SPAWN_EGG), LAPIS_LAZULI(903, MinecraftItemID.LAPIS_LAZULI), RAVAGER_SPAWN_EGG(904, MinecraftItemID.RAVAGER_SPAWN_EGG), WATER_BUCKET(905, MinecraftItemID.WATER_BUCKET), LIGHT_GRAY_DYE(906, MinecraftItemID.LIGHT_GRAY_DYE), CHARCOAL(907, MinecraftItemID.CHARCOAL), AGENT_SPAWN_EGG(908, MinecraftItemID.AGENT_SPAWN_EGG);

    private val badItemId = 0
    private val minecraftItemId: MinecraftItemID? = null

    companion object {
        private val byId: Int2ObjectMap<PNAlphaItemID> = Int2ObjectOpenHashMap(values().size)
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun getBadAlphaId(id: Int): PNAlphaItemID {
            return byId.get(id)
        }

        init {
            for (value in values()) {
                byId.put(cn.nukkit.item.value.badItemId, cn.nukkit.item.value)
            }
        }
    }
}