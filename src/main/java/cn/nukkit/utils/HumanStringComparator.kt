package cn.nukkit.utils

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class HumanStringComparator : Comparator<String?> {
    @Override
    fun compare(o1: String, o2: String): Int {
        val o1StringPart: String = o1.replaceAll("\\d", "")
        val o2StringPart: String = o2.replaceAll("\\d", "")
        return if (o1StringPart.equalsIgnoreCase(o2StringPart)) {
            extractInt(o1) - extractInt(o2)
        } else o1.compareTo(o2)
    }

    fun extractInt(s: String): Int {
        val num: String = s.replaceAll("\\D", "")
        // return 0 if no digits found
        return try {
            if (num.isEmpty()) 0 else Integer.parseInt(num)
        } catch (e: NumberFormatException) {
            0
        }
    }

    companion object {
        @get:Since("1.4.0.0-PN")
        @get:PowerNukkitOnly
        val instance = HumanStringComparator()
    }
}