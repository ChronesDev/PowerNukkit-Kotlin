package cn.nukkit.inventory

import cn.nukkit.block.BlockID

/**
 * @author MagicDroidX (Nukkit Project)
 */
object Fuel {
    val duration: Map<Integer, Short> = TreeMap()
    private fun addItem(itemID: Int, fuelDuration: Short) {
        duration.put(itemID, fuelDuration)
    }

    private fun addBlock(blockID: Int, fuelDuration: Short) {
        duration.put(if (blockID > 255) 255 - blockID else blockID, fuelDuration) // ItemBlock have a negative ID
    }

    init {
        addItem(ItemID.COAL, 1600.toShort())
        addBlock(BlockID.COAL_BLOCK, 16000.toShort())
        addBlock(BlockID.TRUNK, 300.toShort())
        addBlock(BlockID.WOODEN_PLANKS, 300.toShort())
        addBlock(BlockID.SAPLING, 100.toShort())
        addItem(ItemID.WOODEN_AXE, 200.toShort())
        addItem(ItemID.WOODEN_PICKAXE, 200.toShort())
        addItem(ItemID.WOODEN_SWORD, 200.toShort())
        addItem(ItemID.WOODEN_SHOVEL, 200.toShort())
        addItem(ItemID.WOODEN_HOE, 200.toShort())
        addItem(ItemID.STICK, 100.toShort())
        addBlock(BlockID.FENCE, 300.toShort())
        addBlock(BlockID.FENCE_GATE, 300.toShort())
        addBlock(BlockID.FENCE_GATE_SPRUCE, 300.toShort())
        addBlock(BlockID.FENCE_GATE_BIRCH, 300.toShort())
        addBlock(BlockID.FENCE_GATE_JUNGLE, 300.toShort())
        addBlock(BlockID.FENCE_GATE_ACACIA, 300.toShort())
        addBlock(BlockID.FENCE_GATE_DARK_OAK, 300.toShort())
        addBlock(BlockID.WOODEN_STAIRS, 300.toShort())
        addBlock(BlockID.SPRUCE_WOOD_STAIRS, 300.toShort())
        addBlock(BlockID.BIRCH_WOOD_STAIRS, 300.toShort())
        addBlock(BlockID.JUNGLE_WOOD_STAIRS, 300.toShort())
        addBlock(BlockID.TRAPDOOR, 300.toShort())
        addBlock(BlockID.WORKBENCH, 300.toShort())
        addBlock(BlockID.BOOKSHELF, 300.toShort())
        addBlock(BlockID.CHEST, 300.toShort())
        addItem(ItemID.BUCKET, 20000.toShort())
        addBlock(BlockID.LADDER, 300.toShort())
        addItem(ItemID.BOW, 200.toShort())
        addItem(ItemID.BOWL, 200.toShort())
        addBlock(BlockID.WOOD2, 300.toShort())
        addBlock(BlockID.WOODEN_PRESSURE_PLATE, 300.toShort())
        addBlock(BlockID.ACACIA_WOOD_STAIRS, 300.toShort())
        addBlock(BlockID.DARK_OAK_WOOD_STAIRS, 300.toShort())
        addBlock(BlockID.TRAPPED_CHEST, 300.toShort())
        addBlock(BlockID.DAYLIGHT_DETECTOR, 300.toShort())
        addBlock(BlockID.DAYLIGHT_DETECTOR_INVERTED, 300.toShort())
        addBlock(BlockID.JUKEBOX, 300.toShort())
        addBlock(BlockID.NOTEBLOCK, 300.toShort())
        addBlock(BlockID.WOOD_SLAB, 300.toShort())
        addBlock(BlockID.DOUBLE_WOOD_SLAB, 300.toShort())
        addItem(ItemID.BOAT, 1200.toShort())
        addItem(ItemID.BLAZE_ROD, 2400.toShort())
        addBlock(BlockID.BROWN_MUSHROOM_BLOCK, 300.toShort())
        addBlock(BlockID.RED_MUSHROOM_BLOCK, 300.toShort())
        addItem(ItemID.FISHING_ROD, 300.toShort())
        addBlock(BlockID.WOODEN_BUTTON, 100.toShort())
        addItem(ItemID.WOODEN_DOOR, 200.toShort())
        addItem(ItemID.SPRUCE_DOOR, 200.toShort())
        addItem(ItemID.BIRCH_DOOR, 200.toShort())
        addItem(ItemID.JUNGLE_DOOR, 200.toShort())
        addItem(ItemID.ACACIA_DOOR, 200.toShort())
        addItem(ItemID.DARK_OAK_DOOR, 200.toShort())
        addItem(ItemID.BANNER, 300.toShort())
        addBlock(BlockID.DEAD_BUSH, 100.toShort())
        addItem(ItemID.SIGN, 200.toShort())
        addItem(ItemID.ACACIA_SIGN, 200.toShort())
        addItem(ItemID.BIRCH_SIGN, 200.toShort())
        addItem(ItemID.SPRUCE_SIGN, 200.toShort())
        addItem(ItemID.DARK_OAK_SIGN, 200.toShort())
        addItem(ItemID.JUNGLE_SIGN, 200.toShort())
        addBlock(BlockID.DRIED_KELP_BLOCK, 4000.toShort())
    }
}