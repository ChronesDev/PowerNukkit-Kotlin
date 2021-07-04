package cn.nukkit.utils

import cn.nukkit.api.DeprecationDetails

@PowerNukkitOnly
@Since("1.3.0.0-PN")
@EqualsAndHashCode(callSuper = true)
@Deprecated
@DeprecationDetails(since = "1.4.0.0-PN", reason = "Moved to a class with more details and unlimited data bits", replaceWith = "InvalidBlockPropertyMetaException")
class InvalidBlockDamageException @PowerNukkitOnly @Since("1.3.0.0-PN") constructor(@get:PowerNukkitOnly
                                                                                    @get:Since("1.3.0.0-PN") val blockId: Int, @get:PowerNukkitOnly
                                                                                    @get:Since("1.3.0.0-PN") val damage: Int, @get:PowerNukkitOnly
                                                                                    @get:Since("1.3.0.0-PN") val before: Int) : InvalidBlockPropertyMetaException(BlockUnknown.UNKNOWN,
        before, damage,
        "Invalid block-meta combination. New: $blockId:$damage, Before: $blockId:$before")