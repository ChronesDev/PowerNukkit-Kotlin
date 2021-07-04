package cn.nukkit.network.protocol.types

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
@ToString
@Log4j2
class NetworkInventoryAction {
    var sourceType = 0
    var windowId = 0
    var unknown: Long = 0
    var inventorySlot = 0
    var oldItem: Item? = null
    var newItem: Item? = null

    @Since("1.3.0.0-PN")
    var stackNetworkId = 0
    fun read(packet: InventoryTransactionPacket): NetworkInventoryAction {
        sourceType = packet.getUnsignedVarInt()
        when (sourceType) {
            SOURCE_CONTAINER -> windowId = packet.getVarInt()
            SOURCE_WORLD -> unknown = packet.getUnsignedVarInt()
            SOURCE_CREATIVE -> {
            }
            SOURCE_CRAFT_SLOT, SOURCE_TODO -> {
                windowId = packet.getVarInt()
                when (windowId) {
                    SOURCE_TYPE_CRAFTING_RESULT, SOURCE_TYPE_CRAFTING_USE_INGREDIENT -> packet.isCraftingPart = true
                    SOURCE_TYPE_ENCHANT_INPUT, SOURCE_TYPE_ENCHANT_OUTPUT, SOURCE_TYPE_ENCHANT_MATERIAL -> packet.isEnchantingPart = true
                    SOURCE_TYPE_ANVIL_INPUT, SOURCE_TYPE_ANVIL_MATERIAL, SOURCE_TYPE_ANVIL_RESULT -> packet.isRepairItemPart = true
                }
            }
        }
        inventorySlot = packet.getUnsignedVarInt()
        oldItem = packet.getSlot()
        newItem = packet.getSlot()
        return this
    }

    fun write(packet: InventoryTransactionPacket) {
        packet.putUnsignedVarInt(sourceType)
        when (sourceType) {
            SOURCE_CONTAINER, SOURCE_CRAFT_SLOT, SOURCE_TODO -> packet.putVarInt(windowId)
            SOURCE_WORLD -> packet.putUnsignedVarInt(unknown)
            SOURCE_CREATIVE -> {
            }
        }
        packet.putUnsignedVarInt(inventorySlot)
        packet.putSlot(oldItem)
        packet.putSlot(newItem)
    }

    fun createInventoryAction(player: Player): InventoryAction? {
        return when (sourceType) {
            SOURCE_CONTAINER -> {
                if (windowId == ContainerIds.ARMOR) {
                    //TODO: HACK!
                    inventorySlot += 36
                    windowId = ContainerIds.INVENTORY
                }
                // ID 124 with slot 14/15 is enchant inventory
                if (windowId == ContainerIds.UI) {
                    when (inventorySlot) {
                        PlayerUIComponent.CREATED_ITEM_OUTPUT_UI_SLOT -> if (player.getWindowById(Player.ANVIL_WINDOW_ID) != null) {
                            windowId = Player.ANVIL_WINDOW_ID
                            inventorySlot = 2
                        }
                        EnchantInventory.ENCHANT_INPUT_ITEM_UI_SLOT -> {
                            if (player.getWindowById(Player.ENCHANT_WINDOW_ID) == null) {
                                log.error("Player {} does not have enchant window open", player.getName())
                                return null
                            }
                            windowId = Player.ENCHANT_WINDOW_ID
                            inventorySlot = 0
                        }
                        EnchantInventory.ENCHANT_REAGENT_UI_SLOT -> {
                            if (player.getWindowById(Player.ENCHANT_WINDOW_ID) == null) {
                                log.error("Player {} does not have enchant window open", player.getName())
                                return null
                            }
                            windowId = Player.ENCHANT_WINDOW_ID
                            inventorySlot = 1
                        }
                        AnvilInventory.ANVIL_INPUT_UI_SLOT -> {
                            if (player.getWindowById(Player.ANVIL_WINDOW_ID) == null) {
                                log.error("Player {} does not have anvil window open", player.getName())
                                return null
                            }
                            windowId = Player.ANVIL_WINDOW_ID
                            inventorySlot = 0
                        }
                        AnvilInventory.ANVIL_MATERIAL_UI_SLOT -> {
                            if (player.getWindowById(Player.ANVIL_WINDOW_ID) == null) {
                                log.error("Player {} does not have anvil window open", player.getName())
                                return null
                            }
                            windowId = Player.ANVIL_WINDOW_ID
                            inventorySlot = 1
                        }
                        SmithingInventory.SMITHING_EQUIPMENT_UI_SLOT -> {
                            if (player.getWindowById(Player.SMITHING_WINDOW_ID) == null) {
                                log.error("Player {} does not have smithing table window open", player.getName())
                                return null
                            }
                            windowId = Player.SMITHING_WINDOW_ID
                            inventorySlot = 0
                        }
                        SmithingInventory.SMITHING_INGREDIENT_UI_SLOT -> {
                            if (player.getWindowById(Player.SMITHING_WINDOW_ID) == null) {
                                log.error("Player {} does not have smithing table window open", player.getName())
                                return null
                            }
                            windowId = Player.SMITHING_WINDOW_ID
                            inventorySlot = 1
                        }
                        GrindstoneInventory.GRINDSTONE_EQUIPMENT_UI_SLOT -> {
                            if (player.getWindowById(Player.GRINDSTONE_WINDOW_ID) == null) {
                                log.error("Player {} does not have grindstone window open", player.getName())
                                return null
                            }
                            windowId = Player.GRINDSTONE_WINDOW_ID
                            inventorySlot = 0
                        }
                        GrindstoneInventory.GRINDSTONE_INGREDIENT_UI_SLOT -> {
                            if (player.getWindowById(Player.GRINDSTONE_WINDOW_ID) == null) {
                                log.error("Player {} does not have grindstone window open", player.getName())
                                return null
                            }
                            windowId = Player.GRINDSTONE_WINDOW_ID
                            inventorySlot = 1
                        }
                    }
                }
                val window: Inventory = player.getWindowById(windowId)
                if (window != null) {
                    return SlotChangeAction(window, inventorySlot, oldItem, newItem)
                }
                log.debug("Player {} has no open container with window ID {}", player.getName(), windowId)
                null
            }
            SOURCE_WORLD -> {
                if (inventorySlot != InventoryTransactionPacket.ACTION_MAGIC_SLOT_DROP_ITEM) {
                    log.debug("Only expecting drop-item world actions from the client!")
                    return null
                }
                DropItemAction(oldItem, newItem)
            }
            SOURCE_CREATIVE -> {
                val type: Int
                type = when (inventorySlot) {
                    InventoryTransactionPacket.ACTION_MAGIC_SLOT_CREATIVE_DELETE_ITEM -> CreativeInventoryAction.TYPE_DELETE_ITEM
                    InventoryTransactionPacket.ACTION_MAGIC_SLOT_CREATIVE_CREATE_ITEM -> CreativeInventoryAction.TYPE_CREATE_ITEM
                    else -> {
                        log.debug("Unexpected creative action type {}", inventorySlot)
                        return null
                    }
                }
                CreativeInventoryAction(oldItem, newItem, type)
            }
            SOURCE_CRAFT_SLOT, SOURCE_TODO -> {
                when (windowId) {
                    SOURCE_TYPE_CRAFTING_ADD_INGREDIENT, SOURCE_TYPE_CRAFTING_REMOVE_INGREDIENT -> return SlotChangeAction(player.getCraftingGrid(), inventorySlot, oldItem, newItem)
                    SOURCE_TYPE_CONTAINER_DROP_CONTENTS -> {
                        val inventory: Optional<Inventory> = player.getTopWindow()
                        return if (!inventory.isPresent()) {
                            // No window open?
                            null
                        } else SlotChangeAction(inventory.get(), inventorySlot, oldItem, newItem)
                    }
                    SOURCE_TYPE_CRAFTING_RESULT -> return CraftingTakeResultAction(oldItem, newItem)
                    SOURCE_TYPE_CRAFTING_USE_INGREDIENT -> return CraftingTransferMaterialAction(oldItem, newItem, inventorySlot)
                }
                if (windowId >= SOURCE_TYPE_ANVIL_OUTPUT && windowId <= SOURCE_TYPE_ANVIL_INPUT) { //anvil actions
                    var inv: Inventory
                    if (player.getWindowById(Player.ANVIL_WINDOW_ID).also { inv = it } is AnvilInventory) {
                        val anvil: AnvilInventory = inv as AnvilInventory
                        return when (windowId) {
                            SOURCE_TYPE_ANVIL_INPUT, SOURCE_TYPE_ANVIL_MATERIAL, SOURCE_TYPE_ANVIL_RESULT -> RepairItemAction(oldItem, newItem, windowId)
                            else -> SlotChangeAction(anvil, inventorySlot, oldItem, newItem)
                        }
                    } else if (player.getWindowById(Player.GRINDSTONE_WINDOW_ID).also { inv = it } is GrindstoneInventory) {
                        return when (windowId) {
                            SOURCE_TYPE_ANVIL_INPUT, SOURCE_TYPE_ANVIL_MATERIAL, SOURCE_TYPE_ANVIL_RESULT -> GrindstoneItemAction(oldItem, newItem, windowId,
                                    if (windowId != SOURCE_TYPE_ANVIL_RESULT) 0 else (inv as GrindstoneInventory).getResultExperience()
                            )
                            else -> SlotChangeAction(inv, inventorySlot, oldItem, newItem)
                        }
                    } else if (player.getWindowById(Player.SMITHING_WINDOW_ID) is SmithingInventory) {
                        when (windowId) {
                            SOURCE_TYPE_ANVIL_INPUT, SOURCE_TYPE_ANVIL_MATERIAL, SOURCE_TYPE_ANVIL_OUTPUT, SOURCE_TYPE_ANVIL_RESULT -> return SmithingItemAction(oldItem, newItem, inventorySlot)
                        }
                    } else {
                        log.debug("Player {} has no open anvil or grindstone inventory", player.getName())
                        return null
                    }
                }
                if (windowId >= SOURCE_TYPE_ENCHANT_OUTPUT && windowId <= SOURCE_TYPE_ENCHANT_INPUT) {
                    val inv: Inventory = player.getWindowById(Player.ENCHANT_WINDOW_ID)
                    if (inv !is EnchantInventory) {
                        log.debug("Player {} has no open enchant inventory", player.getName())
                        return null
                    }
                    val enchant: EnchantInventory = inv as EnchantInventory
                    when (windowId) {
                        SOURCE_TYPE_ENCHANT_INPUT -> return EnchantingAction(oldItem, newItem, SOURCE_TYPE_ENCHANT_INPUT)
                        SOURCE_TYPE_ENCHANT_MATERIAL -> return EnchantingAction(newItem, oldItem, SOURCE_TYPE_ENCHANT_MATERIAL) // Mojang ish backwards?
                        SOURCE_TYPE_ENCHANT_OUTPUT -> return EnchantingAction(oldItem, newItem, SOURCE_TYPE_ENCHANT_OUTPUT)
                    }
                    return SlotChangeAction(enchant, inventorySlot, oldItem, newItem)
                }
                if (windowId == SOURCE_TYPE_BEACON) {
                    val inv: Inventory = player.getWindowById(Player.BEACON_WINDOW_ID)
                    if (inv !is BeaconInventory) {
                        log.debug("Player {} has no open beacon inventory", player.getName())
                        return null
                    }
                    val beacon: BeaconInventory = inv as BeaconInventory
                    inventorySlot = 0
                    return SlotChangeAction(beacon, inventorySlot, oldItem, newItem)
                }

                //TODO: more stuff
                log.debug("Player {} has no open container with window ID {}", player.getName(), windowId)
                null
            }
            else -> {
                log.debug("Unknown inventory source type {}", sourceType)
                null
            }
        }
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EMPTY_ARRAY = arrayOfNulls<NetworkInventoryAction>(0)
        const val SOURCE_CONTAINER = 0
        const val SOURCE_WORLD = 2 //drop/pickup item entity
        const val SOURCE_CREATIVE = 3
        const val SOURCE_TODO = 99999
        const val SOURCE_CRAFT_SLOT = 100

        /**
         * Fake window IDs for the SOURCE_TODO type (99999)
         *
         *
         * These identifiers are used for inventory source types which are not currently implemented server-side in MCPE.
         * As a general rule of thumb, anything that doesn't have a permanent inventory is client-side. These types are
         * to allow servers to track what is going on in client-side windows.
         *
         *
         * Expect these to change in the future.
         */
        const val SOURCE_TYPE_CRAFTING_ADD_INGREDIENT = -2
        const val SOURCE_TYPE_CRAFTING_REMOVE_INGREDIENT = -3
        const val SOURCE_TYPE_CRAFTING_RESULT = -4
        const val SOURCE_TYPE_CRAFTING_USE_INGREDIENT = -5
        const val SOURCE_TYPE_ANVIL_INPUT = -10
        const val SOURCE_TYPE_ANVIL_MATERIAL = -11
        const val SOURCE_TYPE_ANVIL_RESULT = -12
        const val SOURCE_TYPE_ANVIL_OUTPUT = -13
        const val SOURCE_TYPE_ENCHANT_INPUT = -15
        const val SOURCE_TYPE_ENCHANT_MATERIAL = -16
        const val SOURCE_TYPE_ENCHANT_OUTPUT = -17
        const val SOURCE_TYPE_TRADING_INPUT_1 = -20
        const val SOURCE_TYPE_TRADING_INPUT_2 = -21
        const val SOURCE_TYPE_TRADING_USE_INPUTS = -22
        const val SOURCE_TYPE_TRADING_OUTPUT = -23
        const val SOURCE_TYPE_BEACON = -24

        /**
         * Any client-side window dropping its contents when the player closes it
         */
        const val SOURCE_TYPE_CONTAINER_DROP_CONTENTS = -100
    }
}