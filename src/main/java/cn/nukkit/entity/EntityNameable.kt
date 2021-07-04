package cn.nukkit.entity

import cn.nukkit.Player

/**
 * An entity which can be named by name tags.
 */
@PowerNukkitOnly
interface EntityNameable {
    @get:PowerNukkitOnly("The Entity implementations are not PowerNukkit only")
    @set:PowerNukkitOnly("The Entity implementations are not PowerNukkit only")
    var nameTag: String?

    @get:PowerNukkitOnly("The Entity implementations are not PowerNukkit only")
    @set:PowerNukkitOnly("The Entity implementations are not PowerNukkit only")
    var isNameTagVisible: Boolean

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isPersistent: Boolean

    @PowerNukkitOnly("The Entity implementations are not PowerNukkit only")
    fun onInteract(player: Player, item: Item, clickedPos: Vector3?): Boolean {
        if (item.getId() === Item.NAME_TAG) {
            if (!player.isSpectator()) {
                return playerApplyNameTag(player, item)
            }
        }
        return false
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun playerApplyNameTag(@Nonnull player: Player, @Nonnull item: Item): Boolean {
        return playerApplyNameTag(player, item, true)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun playerApplyNameTag(@Nonnull player: Player, @Nonnull item: Item, consume: Boolean): Boolean {
        if (item.hasCustomName()) {
            nameTag = item.getCustomName()
            isNameTagVisible = true
            if (consume && !player.isCreative()) {
                player.getInventory().removeItem(item)
            }
            // Set entity as persistent.
            return true
        }
        return false
    }

    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "New implementation needs a player instance, using this method may allow players to name unexpected entities", by = "PowerNukkit", replaceWith = "playerApplyNameTag(Player, Item)")
    fun applyNameTag(item: Item): Boolean {
        return if (item.hasCustomName()) {
            nameTag = item.getCustomName()
            isNameTagVisible = true
            true
        } else {
            false
        }
    }
}