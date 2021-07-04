package cn.nukkit.utils

import com.google.common.collect.Maps

/**
 * All supported formatting values for chat and console.
 */
enum class TextFormat @JvmOverloads constructor(
        /**
         * Gets the char value associated with this color
         *
         * @return A char value of this color code
         */
        val char: Char, private val intCode: Int,
        /**
         * Checks if this code is a format code as opposed to a color code.
         */
        val isFormat: Boolean = false) {
    /**
     * Represents black.
     */
    BLACK('0', 0x00),

    /**
     * Represents dark blue.
     */
    DARK_BLUE('1', 0x1),

    /**
     * Represents dark green.
     */
    DARK_GREEN('2', 0x2),

    /**
     * Represents dark blue (aqua).
     */
    DARK_AQUA('3', 0x3),

    /**
     * Represents dark red.
     */
    DARK_RED('4', 0x4),

    /**
     * Represents dark purple.
     */
    DARK_PURPLE('5', 0x5),

    /**
     * Represents gold.
     */
    GOLD('6', 0x6),

    /**
     * Represents gray.
     */
    GRAY('7', 0x7),

    /**
     * Represents dark gray.
     */
    DARK_GRAY('8', 0x8),

    /**
     * Represents blue.
     */
    BLUE('9', 0x9),

    /**
     * Represents green.
     */
    GREEN('a', 0xA),

    /**
     * Represents aqua.
     */
    AQUA('b', 0xB),

    /**
     * Represents red.
     */
    RED('c', 0xC),

    /**
     * Represents light purple.
     */
    LIGHT_PURPLE('d', 0xD),

    /**
     * Represents yellow.
     */
    YELLOW('e', 0xE),

    /**
     * Represents white.
     */
    WHITE('f', 0xF),

    /**
     * Represents minecoins gold.
     */
    MINECOIN_GOLD('g', 0x16),

    /**
     * Makes the text obfuscated.
     */
    OBFUSCATED('k', 0x10, true),

    /**
     * Makes the text bold.
     */
    BOLD('l', 0x11, true),

    /**
     * Makes a line appear through the text.
     */
    STRIKETHROUGH('m', 0x12, true),

    /**
     * Makes the text appear underlined.
     */
    UNDERLINE('n', 0x13, true),

    /**
     * Makes the text italic.
     */
    ITALIC('o', 0x14, true),

    /**
     * Resets all previous chat colors or formats.
     */
    RESET('r', 0x15);

    companion object {
        /**
         * The special character which prefixes all format codes. Use this if
         * you need to dynamically convert format codes from your custom format.
         */
        const val ESCAPE = '\u00A7'
        private val CLEAN_PATTERN: Pattern = Pattern.compile("(?i)" + ESCAPE + "[0-9A-GK-OR]")
        private val BY_ID: Map<Integer, TextFormat> = Maps.newTreeMap()
        private val BY_CHAR: Map<Character, TextFormat> = HashMap()

        /**
         * Gets the TextFormat represented by the specified format code.
         *
         * @param code Code to check
         * @return Associative [TextFormat] with the given code,
         * or null if it doesn't exist
         */
        fun getByChar(code: Char): TextFormat? {
            return BY_CHAR[code]
        }

        /**
         * Gets the TextFormat represented by the specified format code.
         *
         * @param code Code to check
         * @return Associative [TextFormat] with the given code,
         * or null if it doesn't exist
         */
        fun getByChar(code: String?): TextFormat? {
            return if (code == null || code.length() <= 1) {
                null
            } else BY_CHAR[code.charAt(0)]
        }

        /**
         * Cleans the given message of all format codes.
         *
         * @param input String to clean.
         * @return A copy of the input string, without any formatting.
         */
        fun clean(input: String?): String? {
            return clean(input, false)
        }

        fun clean(input: String?, recursive: Boolean): String? {
            if (input == null) {
                return null
            }
            val result: String = CLEAN_PATTERN.matcher(input).replaceAll("")
            return if (recursive && CLEAN_PATTERN.matcher(result).find()) {
                clean(result, true)
            } else result
        }

        /**
         * Translates a string using an alternate format code character into a
         * string that uses the internal TextFormat.ESCAPE format code
         * character. The alternate format code character will only be replaced if
         * it is immediately followed by 0-9, A-G, a-g, K-O, k-o, R or r.
         *
         * @param altFormatChar   The alternate format code character to replace. Ex: &amp;
         * @param textToTranslate Text containing the alternate format code character.
         * @return Text containing the TextFormat.ESCAPE format code character.
         */
        fun colorize(altFormatChar: Char, textToTranslate: String): String {
            val b: CharArray = textToTranslate.toCharArray()
            for (i in 0 until b.size - 1) {
                if (b[i] == altFormatChar && "0123456789AaBbCcDdEeFfGgKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                    b[i] = ESCAPE
                    b[i + 1] = Character.toLowerCase(b[i + 1])
                }
            }
            return String(b)
        }

        /**
         * Translates a string, using an ampersand (&amp;) as an alternate format code
         * character, into a string that uses the internal TextFormat.ESCAPE format
         * code character. The alternate format code character will only be replaced if
         * it is immediately followed by 0-9, A-G, a-g, K-O, k-o, R or r.
         *
         * @param textToTranslate Text containing the alternate format code character.
         * @return Text containing the TextFormat.ESCAPE format code character.
         */
        fun colorize(textToTranslate: String): String {
            return colorize('&', textToTranslate)
        }

        /**
         * Gets the chat color used at the end of the given input string.
         *
         * @param input Input string to retrieve the colors from.
         * @return Any remaining chat color to pass onto the next line.
         */
        fun getLastColors(input: String): String {
            val result = StringBuilder()
            val length: Int = input.length()

            // Search backwards from the end as it is faster
            for (index in length - 1 downTo -1 + 1) {
                val section: Char = input.charAt(index)
                if (section == ESCAPE && index < length - 1) {
                    val c: Char = input.charAt(index + 1)
                    val color = getByChar(c)
                    if (color != null) {
                        result.insert(0, color.toString())

                        // Once we find a color or reset we can stop searching
                        if (color.isColor || color.equals(RESET)) {
                            break
                        }
                    }
                }
            }
            return result.toString()
        }

        init {
            for (color in values()) {
                BY_ID.put(cn.nukkit.utils.color.intCode, cn.nukkit.utils.color)
                BY_CHAR.put(cn.nukkit.utils.color.code, cn.nukkit.utils.color)
            }
        }
    }

    private val toString: String

    @Override
    override fun toString(): String {
        return toString
    }

    /**
     * Checks if this code is a color code as opposed to a format code.
     */
    val isColor: Boolean
        get() = !isFormat && this != RESET

    init {
        toString = String(charArrayOf(ESCAPE, char))
    }
}