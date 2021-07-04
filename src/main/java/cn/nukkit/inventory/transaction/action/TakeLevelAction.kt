package cn.nukkit.inventory.transaction.action

import cn.nukkit.Player

@ToString(callSuper = true)
class TakeLevelAction(val levels: Int) : InventoryAction(Item.get(0), Item.get(0)) {
    @Override
    override fun isValid(source: Player): Boolean {
        return source.isCreative() || source.getExperienceLevel() >= levels
    }

    @Override
    override fun execute(source: Player): Boolean {
        if (source.isCreative()) {
            return true
        }
        val playerLevels: Int = source.getExperienceLevel()
        if (playerLevels < levels) {
            return false
        }
        source.setExperience(source.getExperience(), playerLevels - levels, false)
        return true
    }

    @Override
    override fun onExecuteSuccess(source: Player?) {
    }

    @Override
    override fun onExecuteFail(source: Player?) {
    }
}