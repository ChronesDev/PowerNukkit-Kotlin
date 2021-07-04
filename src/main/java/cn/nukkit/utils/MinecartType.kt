package cn.nukkit.utils

import cn.nukkit.api.API

/**
 * Helper class of Minecart variants
 *
 *
 * By Adam Matthew
 * Creation time: 2017/7/17 19:55.
 */
@API(usage = API.Usage.STABLE, definition = API.Definition.INTERNAL)
enum class MinecartType(
        /**
         * Get the variants of the current minecart
         *
         * @return Integer
         */
        val id: Int, private val hasBlockInside: Boolean,
        /**
         * Get the name of the minecart variants
         *
         * @return String
         */
        override val name: String) {
    /**
     * Represents an empty vehicle.
     */
    MINECART_EMPTY(0, false, "Minecart"),

    /**
     * Represents a chest holder.
     */
    MINECART_CHEST(1, true, "Minecart with Chest"),

    /**
     * Represents a furnace minecart.
     */
    MINECART_FURNACE(2, true, "Minecart with Furnace"),

    /**
     * Represents a TNT minecart.
     */
    MINECART_TNT(3, true, "Minecart with TNT"),

    /**
     * Represents a mob spawner minecart.
     */
    MINECART_MOB_SPAWNER(4, true, "Minecart with Mob Spawner"),

    /**
     * Represents a hopper minecart.
     */
    MINECART_HOPPER(5, true, "Minecart with Hopper"),

    /**
     * Represents a command block minecart.
     */
    MINECART_COMMAND_BLOCK(6, true, "Minecart with Command Block"),

    /**
     * Represents an unknown minecart.
     */
    MINECART_UNKNOWN(-1, false, "Unknown Minecart");

    companion object {
        private val TYPES: Map<Integer, MinecartType> = HashMap()

        /**
         * Returns of an instance of Minecart-variants
         *
         * @param types The number of minecart
         * @return Integer
         */
        fun valueOf(types: Int): MinecartType {
            val what = TYPES[types]
            return what ?: MINECART_UNKNOWN
        }

        init {
            for (var3 in values()) {
                TYPES.put(cn.nukkit.utils.var3.getId(), cn.nukkit.utils.var3)
            }
        }
    }

    /**
     * Gets if the minecart contains block
     *
     * @return Boolean
     */
    fun hasBlockInside(): Boolean {
        return hasBlockInside
    }
}