package cn.nukkit

import cn.nukkit.entity.Attribute

/**
 * @author funcraft
 * @since 2015/11/11
 */
class PlayerFood(player: Player, foodLevel: Int, foodSaturationLevel: Float) {
    private var foodLevel = 20
    val maxLevel: Int
    private var foodSaturationLevel = 20f
    private var foodTickTimer = 0
    private var foodExpLevel = 0.0
    private val player: Player
    fun getPlayer(): Player {
        return player
    }

    var level: Int
        get() = foodLevel
        set(foodLevel) {
            setLevel(foodLevel, -1f)
        }

    fun setLevel(foodLevel: Int, saturationLevel: Float) {
        var foodLevel = foodLevel
        if (foodLevel > 20) {
            foodLevel = 20
        }
        if (foodLevel < 0) {
            foodLevel = 0
        }
        if (foodLevel <= 6 && level > 6) {
            if (getPlayer().isSprinting()) {
                getPlayer().setSprinting(false)
            }
        }
        val ev = PlayerFoodLevelChangeEvent(getPlayer(), foodLevel, saturationLevel)
        getPlayer().getServer().getPluginManager().callEvent(ev)
        if (ev.isCancelled()) {
            sendFoodLevel(level)
            return
        }
        val foodLevel0: Int = ev.getFoodLevel()
        var fsl: Float = ev.getFoodSaturationLevel()
        this.foodLevel = foodLevel
        if (fsl != -1f) {
            if (fsl > foodLevel) fsl = foodLevel.toFloat()
            foodSaturationLevel = fsl
        }
        this.foodLevel = foodLevel0
        sendFoodLevel()
    }

    fun getFoodSaturationLevel(): Float {
        return foodSaturationLevel
    }

    fun setFoodSaturationLevel(fsl: Float) {
        var fsl = fsl
        if (fsl > level) fsl = level.toFloat()
        if (fsl < 0) fsl = 0f
        val ev = PlayerFoodLevelChangeEvent(getPlayer(), level, fsl)
        getPlayer().getServer().getPluginManager().callEvent(ev)
        if (ev.isCancelled()) {
            return
        }
        fsl = ev.getFoodSaturationLevel()
        foodSaturationLevel = fsl
    }

    @JvmOverloads
    fun useHunger(amount: Int = 1) {
        val sfl = getFoodSaturationLevel()
        val foodLevel = level
        if (sfl > 0) {
            var newSfl = sfl - amount
            if (newSfl < 0) newSfl = 0f
            setFoodSaturationLevel(newSfl)
        } else {
            level = foodLevel - amount
        }
    }

    fun addFoodLevel(food: Food) {
        this.addFoodLevel(food.getRestoreFood(), food.getRestoreSaturation())
    }

    fun addFoodLevel(foodLevel: Int, fsl: Float) {
        setLevel(level + foodLevel, getFoodSaturationLevel() + fsl)
    }

    fun reset() {
        foodLevel = 20
        foodSaturationLevel = 20f
        foodExpLevel = 0.0
        foodTickTimer = 0
        sendFoodLevel()
    }

    @JvmOverloads
    fun sendFoodLevel(foodLevel: Int = level) {
        if (getPlayer().spawned) {
            getPlayer().setAttribute(Attribute.getAttribute(Attribute.MAX_HUNGER).setValue(foodLevel))
        }
    }

    fun update(tickDiff: Int) {
        if (!getPlayer().isFoodEnabled()) return
        if (getPlayer().isAlive()) {
            val diff: Int = Server.getInstance().getDifficulty()
            if (level > 17) {
                foodTickTimer += tickDiff
                if (foodTickTimer >= 80) {
                    if (getPlayer().getHealth() < getPlayer().getMaxHealth()) {
                        val ev = EntityRegainHealthEvent(getPlayer(), 1, EntityRegainHealthEvent.CAUSE_EATING)
                        getPlayer().heal(ev)
                        updateFoodExpLevel(6.0)
                    }
                    foodTickTimer = 0
                }
            } else if (level == 0) {
                foodTickTimer += tickDiff
                if (foodTickTimer >= 80) {
                    val ev = EntityDamageEvent(getPlayer(), DamageCause.HUNGER, 1)
                    val now: Float = getPlayer().getHealth()
                    if (diff == 1) {
                        if (now > 10) getPlayer().attack(ev)
                    } else if (diff == 2) {
                        if (now > 1) getPlayer().attack(ev)
                    } else {
                        getPlayer().attack(ev)
                    }
                    foodTickTimer = 0
                }
            }
            if (getPlayer().hasEffect(Effect.HUNGER)) {
                updateFoodExpLevel(0.025)
            }
        }
    }

    fun updateFoodExpLevel(use: Double) {
        if (!getPlayer().isFoodEnabled()) return
        if (Server.getInstance().getDifficulty() === 0) return
        if (getPlayer().hasEffect(Effect.SATURATION)) return
        foodExpLevel += use
        if (foodExpLevel > 4) {
            useHunger(1)
            foodExpLevel = 0.0
        }
    }

    /**
     * @param foodLevel level
     */
    @Deprecated
    @Deprecated("""use {@link #setLevel(int)} instead
      """)
    fun setFoodLevel(foodLevel: Int) {
        level = foodLevel
    }

    /**
     * @param foodLevel level
     * @param saturationLevel saturation
     */
    @Deprecated
    @Deprecated("""use {@link #setLevel(int, float)} instead
      """)
    fun setFoodLevel(foodLevel: Int, saturationLevel: Float) {
        setLevel(foodLevel, saturationLevel)
    }

    init {
        this.player = player
        this.foodLevel = foodLevel
        maxLevel = 20
        this.foodSaturationLevel = foodSaturationLevel
    }
}