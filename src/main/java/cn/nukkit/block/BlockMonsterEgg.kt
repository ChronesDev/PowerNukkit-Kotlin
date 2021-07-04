package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

class BlockMonsterEgg @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta) {
    @get:Override
    override val id: Int
        get() = MONSTER_EGG

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Nonnull
    @get:Since("1.5.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.5.0.0-PN")
    @set:PowerNukkitOnly
    var monsterEggStoneType: MonsterEggStoneType
        get() = getPropertyValue(MONSTER_EGG_STONE_TYPE)
        set(value) {
            setPropertyValue(MONSTER_EGG_STONE_TYPE, value)
        }

    @get:Override
    override val hardness: Double
        get() = 0.75

    @get:Override
    override val resistance: Double
        get() = 3.75

    @get:Override
    override val name: String
        get() = NAMES.get(if (this.getDamage() > 5) 0 else this.getDamage()) + " Monster Egg"

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return Item.EMPTY_ARRAY
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val MONSTER_EGG_STONE_TYPE: ArrayBlockProperty<MonsterEggStoneType> = ArrayBlockProperty(
                "monster_egg_stone_type", true, MonsterEggStoneType::class.java)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(MONSTER_EGG_STONE_TYPE)
        const val STONE = 0
        const val COBBLESTONE = 1
        const val STONE_BRICK = 2
        const val MOSSY_BRICK = 3
        const val CRACKED_BRICK = 4
        const val CHISELED_BRICK = 5
        private val NAMES = arrayOf(
                "Stone",
                "Cobblestone",
                "Stone Brick",
                "Mossy Stone Brick",
                "Cracked Stone Brick",
                "Chiseled Stone Brick"
        )
    }
}