package cn.nukkit.test.matcher

import org.mockito.ArgumentMatcher

/**
 * @author joserobjr
 */
object ExtraArgumentMatchers {
    fun inRange(fromInclusive: Byte, toInclusive: Byte): Byte {
        reportMatcher(InRange(fromInclusive, toInclusive))
        return 0
    }

    fun inRange(fromInclusive: Short, toInclusive: Short): Short {
        reportMatcher(InRange(fromInclusive, toInclusive))
        return 0
    }

    fun inRange(fromInclusive: Char, toInclusive: Char): Char {
        reportMatcher(InRange(fromInclusive, toInclusive))
        return 0
    }

    fun inRange(fromInclusive: Int, toInclusive: Int): Int {
        reportMatcher(InRange(fromInclusive, toInclusive))
        return 0
    }

    fun inRange(fromInclusive: Long, toInclusive: Long): Long {
        reportMatcher(InRange(fromInclusive, toInclusive))
        return 0
    }

    fun inRange(fromInclusive: Float, toInclusive: Float): Float {
        reportMatcher(InRange(fromInclusive, toInclusive))
        return 0
    }

    fun inRange(fromInclusive: Double, toInclusive: Double): Double {
        reportMatcher(InRange(fromInclusive, toInclusive))
        return 0
    }

    fun <C : Comparable<C>?> inRange(fromInclusive: C, toInclusive: C): C {
        reportMatcher(InRange(fromInclusive, toInclusive))
        return fromInclusive
    }

    private fun reportMatcher(matcher: ArgumentMatcher<*>) {
        ThreadSafeMockingProgress.mockingProgress().getArgumentMatcherStorage().reportMatcher(matcher)
    }
}