package cn.nukkit.item.food

import cn.nukkit.Player

/**
 * @author Snake1999
 * @since 2016/1/13
 */
@SuppressWarnings("StaticInitializerReferencesSubClass")
abstract class Food {
    protected var restoreFood = 0
    protected var restoreSaturation = 0f
    protected val relativeIDs: List<NodeIDMeta> = ArrayList()
    fun eatenBy(player: Player): Boolean {
        val event = PlayerEatFoodEvent(player, this)
        player.getServer().getPluginManager().callEvent(event)
        return if (event.isCancelled()) false else event.getFood().onEatenBy(player)
    }

    protected fun onEatenBy(player: Player): Boolean {
        player.getFoodData().addFoodLevel(this)
        return true
    }

    @JvmOverloads
    fun addRelative(relativeID: Int, meta: Int = 0): Food {
        val node = NodeIDMeta(relativeID, meta)
        return addRelative(node)
    }

    private fun addRelative(node: NodeIDMeta): Food {
        if (!relativeIDs.contains(node)) relativeIDs.add(node)
        return this
    }

    fun getRestoreFood(): Int {
        return restoreFood
    }

    fun setRestoreFood(restoreFood: Int): Food {
        this.restoreFood = restoreFood
        return this
    }

    fun getRestoreSaturation(): Float {
        return restoreSaturation
    }

    fun setRestoreSaturation(restoreSaturation: Float): Food {
        this.restoreSaturation = restoreSaturation
        return this
    }

    class NodeIDMeta(val id: Int, val meta: Int)
    internal class NodeIDMetaPlugin(id: Int, meta: Int, plugin: Plugin) : NodeIDMeta(id, meta) {
        val plugin: Plugin

        init {
            this.plugin = plugin
        }
    }

    companion object {
        private val registryCustom: Map<NodeIDMetaPlugin, Food> = LinkedHashMap()
        private val registryDefault: Map<NodeIDMeta, Food> = LinkedHashMap()
        val apple = registerDefaultFood(FoodNormal(4, 2.4f).addRelative(Item.APPLE))
        val apple_golden = registerDefaultFood(FoodEffective(4, 9.6f)
                .addEffect(Effect.getEffect(Effect.REGENERATION).setAmplifier(1).setDuration(5 * 20))
                .addEffect(Effect.getEffect(Effect.ABSORPTION).setDuration(2 * 60 * 20))
                .addRelative(Item.GOLDEN_APPLE))
        val apple_golden_enchanted = registerDefaultFood(FoodEffective(4, 9.6f)
                .addEffect(Effect.getEffect(Effect.REGENERATION).setAmplifier(4).setDuration(30 * 20))
                .addEffect(Effect.getEffect(Effect.ABSORPTION).setDuration(2 * 60 * 20).setAmplifier(3))
                .addEffect(Effect.getEffect(Effect.DAMAGE_RESISTANCE).setDuration(5 * 60 * 20))
                .addEffect(Effect.getEffect(Effect.FIRE_RESISTANCE).setDuration(5 * 60 * 20))
                .addRelative(Item.GOLDEN_APPLE_ENCHANTED))
        val beef_raw = registerDefaultFood(FoodNormal(3, 1.8f).addRelative(Item.RAW_BEEF))
        val beetroot = registerDefaultFood(FoodNormal(1, 1.2f).addRelative(Item.BEETROOT))
        val beetroot_soup = registerDefaultFood(FoodInBowl(6, 7.2f).addRelative(Item.BEETROOT_SOUP))
        val bread = registerDefaultFood(FoodNormal(5, 6f).addRelative(Item.BREAD))
        val cake_slice = registerDefaultFood(FoodNormal(2, 0.4f)
                .addRelative(Block.CAKE_BLOCK, 0).addRelative(Block.CAKE_BLOCK, 1).addRelative(Block.CAKE_BLOCK, 2)
                .addRelative(Block.CAKE_BLOCK, 3).addRelative(Block.CAKE_BLOCK, 4).addRelative(Block.CAKE_BLOCK, 5)
                .addRelative(Block.CAKE_BLOCK, 6))
        val carrot = registerDefaultFood(FoodNormal(3, 4.8f).addRelative(Item.CARROT))
        val carrot_golden = registerDefaultFood(FoodNormal(6, 14.4f).addRelative(Item.GOLDEN_CARROT))
        val chicken_raw = registerDefaultFood(FoodEffective(2, 1.2f)
                .addChanceEffect(0.3f, Effect.getEffect(Effect.HUNGER).setDuration(30 * 20))
                .addRelative(Item.RAW_CHICKEN))
        val chicken_cooked = registerDefaultFood(FoodNormal(6, 7.2f).addRelative(Item.COOKED_CHICKEN))
        val chorus_fruit = registerDefaultFood(FoodChorusFruit())
        val cookie = registerDefaultFood(FoodNormal(2, 0.4f).addRelative(Item.COOKIE))
        val melon_slice = registerDefaultFood(FoodNormal(2, 1.2f).addRelative(Item.MELON_SLICE))
        val milk = registerDefaultFood(FoodMilk().addRelative(Item.BUCKET, 1))
        val mushroom_stew = registerDefaultFood(FoodInBowl(6, 7.2f).addRelative(Item.MUSHROOM_STEW))
        val mutton_cooked = registerDefaultFood(FoodNormal(6, 9.6f).addRelative(Item.COOKED_MUTTON))
        val mutton_raw = registerDefaultFood(FoodNormal(2, 1.2f).addRelative(Item.RAW_MUTTON))
        val porkchop_cooked = registerDefaultFood(FoodNormal(8, 12.8f).addRelative(Item.COOKED_PORKCHOP))
        val porkchop_raw = registerDefaultFood(FoodNormal(3, 1.8f).addRelative(Item.RAW_PORKCHOP))
        val potato_raw = registerDefaultFood(FoodNormal(1, 0.6f).addRelative(Item.POTATO))
        val potato_baked = registerDefaultFood(FoodNormal(5, 7.2f).addRelative(Item.BAKED_POTATO))
        val potato_poisonous = registerDefaultFood(FoodEffective(2, 1.2f)
                .addChanceEffect(0.6f, Effect.getEffect(Effect.POISON).setDuration(4 * 20))
                .addRelative(Item.POISONOUS_POTATO))
        val pumpkin_pie = registerDefaultFood(FoodNormal(8, 4.8f).addRelative(Item.PUMPKIN_PIE))
        val rabbit_cooked = registerDefaultFood(FoodNormal(5, 6f).addRelative(Item.COOKED_RABBIT))
        val rabbit_raw = registerDefaultFood(FoodNormal(3, 1.8f).addRelative(Item.RAW_RABBIT))
        val rabbit_stew = registerDefaultFood(FoodInBowl(10, 12f).addRelative(Item.RABBIT_STEW))
        val rotten_flesh = registerDefaultFood(FoodEffective(4, 0.8f)
                .addChanceEffect(0.8f, Effect.getEffect(Effect.HUNGER).setDuration(30 * 20))
                .addRelative(Item.ROTTEN_FLESH))
        val spider_eye = registerDefaultFood(FoodEffective(2, 3.2f)
                .addEffect(Effect.getEffect(Effect.POISON).setDuration(4 * 20))
                .addRelative(Item.SPIDER_EYE))
        val steak = registerDefaultFood(FoodNormal(8, 12.8f).addRelative(Item.COOKED_BEEF))

        //different kinds of fishes
        val clownfish = registerDefaultFood(FoodNormal(1, 0.2f).addRelative(Item.CLOWNFISH))
        val fish_cooked = registerDefaultFood(FoodNormal(5, 6f).addRelative(Item.COOKED_FISH))
        val fish_raw = registerDefaultFood(FoodNormal(2, 0.4f).addRelative(Item.RAW_FISH))
        val salmon_cooked = registerDefaultFood(FoodNormal(6, 9.6f).addRelative(Item.COOKED_SALMON))
        val salmon_raw = registerDefaultFood(FoodNormal(2, 0.4f).addRelative(Item.RAW_SALMON))
        val pufferfish = registerDefaultFood(FoodEffective(1, 0.2f)
                .addEffect(Effect.getEffect(Effect.HUNGER).setAmplifier(2).setDuration(15 * 20))
                .addEffect(Effect.getEffect(Effect.NAUSEA).setAmplifier(1).setDuration(15 * 20))
                .addEffect(Effect.getEffect(Effect.POISON).setAmplifier(4).setDuration(60 * 20))
                .addRelative(Item.PUFFERFISH))
        val dried_kelp = registerDefaultFood(FoodNormal(1, 0.6f).addRelative(Item.DRIED_KELP))
        val sweet_berries = registerDefaultFood(FoodNormal(2, 0.4f).addRelative(Item.SWEET_BERRIES))

        @PowerNukkitOnly
        val suspicious_stew_night_vision = registerDefaultFood(FoodEffectiveInBow(6, 7.2f)
                .addEffect(Effect.getEffect(Effect.NIGHT_VISION).setAmplifier(1).setDuration(4 * 20)).addRelative(Item.SUSPICIOUS_STEW, 0))

        @PowerNukkitOnly
        val suspicious_stew_jump = registerDefaultFood(FoodEffectiveInBow(6, 7.2f)
                .addEffect(Effect.getEffect(Effect.JUMP).setAmplifier(1).setDuration(4 * 20)).addRelative(Item.SUSPICIOUS_STEW, 1))

        @PowerNukkitOnly
        val suspicious_stew_weakness = registerDefaultFood(FoodEffectiveInBow(6, 7.2f)
                .addEffect(Effect.getEffect(Effect.WEAKNESS).setAmplifier(1).setDuration(7 * 20)).addRelative(Item.SUSPICIOUS_STEW, 2))

        @PowerNukkitOnly
        val suspicious_stew_blindness = registerDefaultFood(FoodEffectiveInBow(6, 7.2f)
                .addEffect(Effect.getEffect(Effect.BLINDNESS).setAmplifier(1).setDuration(6 * 20)).addRelative(Item.SUSPICIOUS_STEW, 3))

        @PowerNukkitOnly
        val suspicious_stew_poison = registerDefaultFood(FoodEffectiveInBow(6, 7.2f)
                .addEffect(Effect.getEffect(Effect.POISON).setAmplifier(1).setDuration(11 * 20)).addRelative(Item.SUSPICIOUS_STEW, 4))

        @PowerNukkitOnly
        val suspicious_stew_saturation = registerDefaultFood(FoodEffectiveInBow(6, 7.2f)
                .addEffect(Effect.getEffect(Effect.SATURATION).setAmplifier(1).setDuration(7)).addRelative(Item.SUSPICIOUS_STEW, 6))

        @PowerNukkitOnly
        val suspicious_stew_fire_resistance = registerDefaultFood(FoodEffectiveInBow(6, 7.2f)
                .addEffect(Effect.getEffect(Effect.FIRE_RESISTANCE).setAmplifier(1).setDuration(2 * 20)).addRelative(Item.SUSPICIOUS_STEW, 7))

        @PowerNukkitOnly
        val suspicious_stew_regeneration = registerDefaultFood(FoodEffectiveInBow(6, 7.2f)
                .addEffect(Effect.getEffect(Effect.REGENERATION).setAmplifier(1).setDuration(6 * 20)).addRelative(Item.SUSPICIOUS_STEW, 8))

        @PowerNukkitOnly
        val suspicious_stew_wither = registerDefaultFood(FoodEffectiveInBow(6, 7.2f)
                .addEffect(Effect.getEffect(Effect.WITHER).setAmplifier(1).setDuration(6 * 20)).addRelative(Item.SUSPICIOUS_STEW, 9))

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "Was added in Cloudburst Nukkit with another name", replaceWith = "honey_bottle")
        @PowerNukkitOnly
        val honey = registerDefaultFood(FoodHoney(6, 1.2f).addRelative(Item.HONEY_BOTTLE))

        @PowerNukkitDifference(since = "1.4.0.0-PN", info = "PowerNukkit uses FoodHoney instead of FoodNormal")
        @Since("1.4.0.0-PN")
        val honey_bottle = honey

        //Opened API for plugins
        fun registerFood(food: Food, plugin: Plugin): Food {
            Objects.requireNonNull(food)
            Objects.requireNonNull(plugin)
            food.relativeIDs.forEach { n -> registryCustom.put(NodeIDMetaPlugin(n.id, n.meta, plugin), food) }
            return food
        }

        private fun registerDefaultFood(food: Food): Food {
            food.relativeIDs.forEach { n -> registryDefault.put(n, food) }
            return food
        }

        fun getByRelative(item: Item): Food? {
            Objects.requireNonNull(item)
            return getByRelative(item.getId(), item.getDamage())
        }

        fun getByRelative(block: Block): Food? {
            Objects.requireNonNull(block)
            return getByRelative(block.getId(), block.getDamage())
        }

        fun getByRelative(relativeID: Int, meta: Int): Food? {
            val result = arrayOf<Food?>(null)
            registryCustom.forEach { n, f -> if (n.id === relativeID && n.meta === meta && n.plugin.isEnabled()) result[0] = f }
            if (result[0] == null) {
                registryDefault.forEach { n, f -> if (n.id === relativeID && n.meta === meta) result[0] = f }
            }
            return result[0]
        }
    }
}