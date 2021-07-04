package cn.nukkit.event.weather

import cn.nukkit.entity.weather.EntityLightningStrike

/**
 * @author funcraft (Nukkit Project)
 */
class LightningStrikeEvent(level: Level?, bolt: EntityLightningStrike) : WeatherEvent(level), Cancellable {
    private val bolt: EntityLightningStrike

    /**
     * Gets the bolt which is striking the earth.
     * @return lightning entity
     */
    val lightning: cn.nukkit.entity.weather.EntityLightningStrike
        get() = bolt

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.bolt = bolt
    }
}