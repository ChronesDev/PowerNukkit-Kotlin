package cn.nukkit.item

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class ItemArmor : Item, ItemDurable {
    constructor(id: Int) : super(id) {}
    constructor(id: Int, meta: Integer?) : super(id, meta) {}
    constructor(id: Int, meta: Integer?, count: Int) : super(id, meta, count) {}
    constructor(id: Int, meta: Integer?, count: Int, name: String?) : super(id, meta, count, name) {}

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }

    @Override
    override fun isArmor(): Boolean {
        return true
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    override fun onClickAir(player: Player, directionVector: Vector3?): Boolean {
        var equip = false
        var oldSlotItem: Item = Item.get(AIR)
        if (this.isHelmet()) {
            oldSlotItem = player.getInventory().getHelmet()
            if (player.getInventory().setHelmet(this)) {
                equip = true
            }
        } else if (this.isChestplate()) {
            oldSlotItem = player.getInventory().getChestplate()
            if (player.getInventory().setChestplate(this)) {
                equip = true
            }
        } else if (this.isLeggings()) {
            oldSlotItem = player.getInventory().getLeggings()
            if (player.getInventory().setLeggings(this)) {
                equip = true
            }
        } else if (this.isBoots()) {
            oldSlotItem = player.getInventory().getBoots()
            if (player.getInventory().setBoots(this)) {
                equip = true
            }
        }
        if (equip) {
            player.getInventory().setItem(player.getInventory().getHeldItemIndex(), oldSlotItem)
            when (this.getTier()) {
                TIER_CHAIN -> player.getLevel().addSound(player, Sound.ARMOR_EQUIP_CHAIN)
                TIER_DIAMOND -> player.getLevel().addSound(player, Sound.ARMOR_EQUIP_DIAMOND)
                TIER_GOLD -> player.getLevel().addSound(player, Sound.ARMOR_EQUIP_GOLD)
                TIER_IRON -> player.getLevel().addSound(player, Sound.ARMOR_EQUIP_IRON)
                TIER_LEATHER -> player.getLevel().addSound(player, Sound.ARMOR_EQUIP_LEATHER)
                TIER_NETHERITE -> player.getLevel().addSound(player, Sound.ARMOR_EQUIP_NETHERITE)
                TIER_OTHER -> player.getLevel().addSound(player, Sound.ARMOR_EQUIP_GENERIC)
                else -> player.getLevel().addSound(player, Sound.ARMOR_EQUIP_GENERIC)
            }
        }
        return this.getCount() === 0
    }

    @Override
    override fun getEnchantAbility(): Int {
        when (this.getTier()) {
            TIER_CHAIN -> return 12
            TIER_LEATHER, TIER_NETHERITE -> return 15
            TIER_DIAMOND -> return 10
            TIER_GOLD -> return 25
            TIER_IRON -> return 9
        }
        return 0
    }

    @Override
    override fun isUnbreakable(): Boolean {
        val tag: Tag = this.getNamedTagEntry("Unbreakable")
        return tag is ByteTag && (tag as ByteTag).data > 0
    }

    companion object {
        const val TIER_LEATHER = 1
        const val TIER_IRON = 2
        const val TIER_CHAIN = 3
        const val TIER_GOLD = 4
        const val TIER_DIAMOND = 5

        @Since("1.4.0.0-PN")
        val TIER_NETHERITE = 6

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "The value of this 'constant' is unstable, it may change if new tiers gets added. Refrain from using it. " +
                "Changes in this value will not be considered as an API breaking change and will not affect code that " +
                "is already compiled.")
        val TIER_OTHER = 1000
    }
}