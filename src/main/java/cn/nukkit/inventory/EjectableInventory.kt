package cn.nukkit.inventory

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
abstract class EjectableInventory @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(holder: InventoryHolder?, type: InventoryType) : ContainerInventory(holder, type)